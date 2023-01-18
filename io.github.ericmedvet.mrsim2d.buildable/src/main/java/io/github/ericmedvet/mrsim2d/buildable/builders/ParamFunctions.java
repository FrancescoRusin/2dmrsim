package io.github.ericmedvet.mrsim2d.buildable.builders;

import io.github.ericmedvet.jnb.core.Param;
import io.github.ericmedvet.mrsim2d.core.functions.SimpleIntegerSupplier;

public class ParamFunctions {

    private ParamFunctions() {
    }

    @SuppressWarnings("unused")
    public static SimpleIntegerSupplier simpleIntegerSupplier(
            @Param("nOfResults") int nOfResults,
            @Param(value = "min", dI = 0) int minimum,
            @Param(value = "max", dI = 10) int maximum
    ) {
        return new SimpleIntegerSupplier(nOfResults, minimum, maximum);
    }
}
