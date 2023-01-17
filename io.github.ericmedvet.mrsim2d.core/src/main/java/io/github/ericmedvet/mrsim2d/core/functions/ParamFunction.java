package io.github.ericmedvet.mrsim2d.core.functions;

import io.github.ericmedvet.mrsim2d.core.util.Parametrized;

public interface ParamFunction<I, O> extends Parametrized {
    public O apply(I input);
    public ParamFunction<I, O> copy();
}
