package io.github.ericmedvet.mrsim2d.buildable;

import io.github.ericmedvet.mrsim2d.buildable.builders.Terrains;
import io.github.ericmedvet.mrsim2d.core.EmbodiedAgent;
import io.github.ericmedvet.mrsim2d.core.agents.independentvoxel.NumIndependentVoxel;
import io.github.ericmedvet.mrsim2d.core.engine.Engine;
import io.github.ericmedvet.mrsim2d.core.functions.ParamFunction;
import io.github.ericmedvet.mrsim2d.core.functions.TimedRealFunction;
import io.github.ericmedvet.mrsim2d.core.tasks.piling.GodPiling;
import io.github.ericmedvet.mrsim2d.core.util.Pair;
import io.github.ericmedvet.mrsim2d.viewer.Drawers;
import io.github.ericmedvet.mrsim2d.viewer.RealtimeViewer;

import java.util.List;
import java.util.Random;
import java.util.ServiceLoader;
import java.util.function.Supplier;

public class Tester {
    public static void main(String[] args) {
        GodPiling piling = new GodPiling(20, 10, 0.6, Terrains.flat(500d, 25d, 100d, 10d));
        RealtimeViewer viewer = new RealtimeViewer(Drawers.basic().profiled());
        Random random = new Random(System.currentTimeMillis());
        Pair<Supplier<EmbodiedAgent>, Supplier<ParamFunction<Double, Integer>>> supplier = new Pair<>(() ->
                new NumIndependentVoxel(List.of(), TimedRealFunction.from((a, b) -> new double[]{0, 0, 0, 0, 0, 0, 0, 0}, 0, 8)),
                () -> new ParamFunction<>() {
                    @Override
                    public Integer apply(Double input) {
                        return random.nextInt(0, 4);
                    }

                    @Override
                    public ParamFunction<Double, Integer> copy() {
                        return this;
                    }

                    @Override
                    public double[] getParams() {
                        return new double[0];
                    }

                    @Override
                    public void setParams(double[] params) {}
                });
        piling.run(supplier, ServiceLoader.load(Engine.class).findFirst().orElseThrow(), viewer);
    }
}
