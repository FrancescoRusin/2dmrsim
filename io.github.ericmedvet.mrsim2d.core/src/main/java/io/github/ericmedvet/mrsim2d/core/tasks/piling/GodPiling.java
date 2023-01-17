package io.github.ericmedvet.mrsim2d.core.tasks.piling;

import io.github.ericmedvet.mrsim2d.core.EmbodiedAgent;
import io.github.ericmedvet.mrsim2d.core.Snapshot;
import io.github.ericmedvet.mrsim2d.core.actions.AddAgent;
import io.github.ericmedvet.mrsim2d.core.actions.CreateUnmovableBody;
import io.github.ericmedvet.mrsim2d.core.actions.TranslateAgent;
import io.github.ericmedvet.mrsim2d.core.bodies.Body;
import io.github.ericmedvet.mrsim2d.core.engine.Engine;
import io.github.ericmedvet.mrsim2d.core.functions.ParamFunction;
import io.github.ericmedvet.mrsim2d.core.functions.TimedRealFunction;
import io.github.ericmedvet.mrsim2d.core.geometry.BoundingBox;
import io.github.ericmedvet.mrsim2d.core.geometry.Point;
import io.github.ericmedvet.mrsim2d.core.geometry.Terrain;
import io.github.ericmedvet.mrsim2d.core.tasks.Observation;
import io.github.ericmedvet.mrsim2d.core.tasks.Outcome;
import io.github.ericmedvet.mrsim2d.core.tasks.Task;
import io.github.ericmedvet.mrsim2d.core.util.DoubleRange;
import io.github.ericmedvet.mrsim2d.core.util.Pair;
import io.github.ericmedvet.mrsim2d.core.util.PolyUtils;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class GodPiling implements Task<Pair<Supplier<EmbodiedAgent>, Supplier<ParamFunction<Double, Integer>>>, Outcome> {
    private final static double FIRST_X_GAP = 10;
    private final static double SPAWN_Y_GAP = 0.1;
    private final static double SPAWN_TICKS = 1;
    private final double duration;
    private final int nOfAgents;
    private final double xGapRatio;
    private final Terrain terrain;
    private final double firstXGap;
    private final double spawnYGap;
    private final double spawnTicks;

    public GodPiling(
            double duration,
            int nOfAgents,
            double xGapRatio,
            Terrain terrain,
            double firstXGap,
            double spawnYGap,
            double spawnTicks
    ) {
        this.duration = duration;
        this.nOfAgents = nOfAgents;
        this.xGapRatio = xGapRatio;
        this.terrain = terrain;
        this.firstXGap = firstXGap;
        this.spawnYGap = spawnYGap;
        this.spawnTicks = spawnTicks;
    }

    public GodPiling(double duration, int nOfAgents, double xGapRatio, Terrain terrain) {
        this(duration, nOfAgents, xGapRatio, terrain, FIRST_X_GAP, SPAWN_Y_GAP, SPAWN_TICKS);
    }

    private void placeAgent(Engine engine, EmbodiedAgent agent, int position, List<EmbodiedAgent> agents) {
        BoundingBox agentBB = agent.boundingBox();
        double x = firstXGap + agentBB.width() * (0.5 + xGapRatio) * position;
        DoubleRange xRange = agentBB.xRange()
                .delta(x);
        double maxClippingY = agents.stream().map(EmbodiedAgent::boundingBox).filter(b -> b.min().x() <= xRange.max() && b.max().x() >= xRange.min())
                .mapToDouble(b -> b.max().y()).max().orElse(terrain.maxHeightAt(xRange));
        double y = spawnYGap + maxClippingY;
        engine.perform(new TranslateAgent(agent, new Point(
                xRange.min() + xRange.extent() / 2d - agentBB.min().x(),
                y - agentBB.min().y()
        )));
    }

    @Override
    public Outcome run(
            Pair<Supplier<EmbodiedAgent>, Supplier<ParamFunction<Double, Integer>>> supplier,
            Engine engine,
            Consumer<Snapshot> snapshotConsumer
    ) {
        //build world
        engine.perform(new CreateUnmovableBody(terrain.poly()));
        //initialize agent list
        List<EmbodiedAgent> agents = new ArrayList<>(nOfAgents);
        //create spawn function
        ParamFunction<Double, Integer> spawnPositionFunction = supplier.second().get();
        //run for defined time
        Map<Double, Observation> observations = new HashMap<>();
        EmbodiedAgent agent;
        Snapshot snapshot;
        int nextTick = 0;
        while (engine.t() < duration) {
            while (agents.size() < nOfAgents && engine.t() >= nextTick * spawnTicks) {
                ++nextTick;
                agent = supplier.first().get();
                engine.perform(new AddAgent(agent));
                placeAgent(engine, agent, spawnPositionFunction.apply(engine.t()), agents);
                agents.add(agent);
            }
            //tick
            snapshot = engine.tick();
            snapshotConsumer.accept(snapshot);
            observations.put(
                    engine.t(),
                    new Observation(agents.stream()
                            .map(a -> new Observation.Agent(
                                    a.bodyParts().stream().map(Body::poly).toList(),
                                    PolyUtils.maxYAtX(terrain.poly(), a.boundingBox().center().x())
                            ))
                            .toList()
                    )
            );
        }
        return new Outcome(new TreeMap<>(observations));
    }
}