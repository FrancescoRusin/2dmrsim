package io.github.ericmedvet.mrsim2d.buildable.builders;

import io.github.ericmedvet.mrsim2d.core.functions.ParamFunction;

import java.util.function.BiFunction;

public class ParamFunctions {
    private ParamFunctions() {
    }

    public interface Builder<F extends ParamFunction<?, ?>> {
        public F build();
    }

    public Builder<ParamFunction<Double, Integer>>
}
