package io.github.ericmedvet.mrsim2d.buildable;

import io.github.ericmedvet.jnb.core.NamedBuilder;
import io.github.ericmedvet.mrsim2d.core.EmbodiedAgent;
import io.github.ericmedvet.mrsim2d.core.agents.independentvoxel.AbstractIndependentVoxel;
import io.github.ericmedvet.mrsim2d.core.agents.independentvoxel.NumIndependentVoxel;
import io.github.ericmedvet.mrsim2d.core.engine.Engine;
import io.github.ericmedvet.mrsim2d.core.functions.SimpleIntegerSupplier;
import io.github.ericmedvet.mrsim2d.core.functions.TimedRealFunction;
import io.github.ericmedvet.mrsim2d.core.tasks.locomotion.PrebuiltIndependentLocomotion;
import io.github.ericmedvet.mrsim2d.viewer.Drawers;
import io.github.ericmedvet.mrsim2d.viewer.RealtimeViewer;

import java.util.List;
import java.util.ServiceLoader;
import java.util.function.Supplier;

public class Tester {
    public static void main(String[] args) {
        NamedBuilder<Object> builder = PreparedNamedBuilder.get();
        PrebuiltIndependentLocomotion pil = (PrebuiltIndependentLocomotion) builder.build("sim.task.prebuiltIndependentLocomotion(duration = 60; " +
                "terrain = sim.terrain.stairs(); shape = sim.agent.vsr.shape.worm(w = 3; h = 2); linkType = NONE)");
        Supplier<AbstractIndependentVoxel> agent = () -> new NumIndependentVoxel(List.of(), NumIndependentVoxel.AreaActuation.OVERALL, false, 0,
                TimedRealFunction.from((a, b) -> new double[]{0}, 0, 1));
        pil.run(agent, ServiceLoader.load(Engine.class).findFirst().orElseThrow(), new RealtimeViewer(Drawers.basic().profiled()));
    }
}
