package io.github.ericmedvet.mrsim2d.core.functions;

import io.github.ericmedvet.mrsim2d.core.util.Parametrized;

public interface ParamFunction<I, O> extends Parametrized {
    O apply(I input);
    ParamFunction<I, O> copy();
}
