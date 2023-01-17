package io.github.ericmedvet.mrsim2d.buildable.builders;

import io.github.ericmedvet.jnb.core.Param;
import io.github.ericmedvet.mrsim2d.core.functions.SimpleDoubleSupplier;

public class ParamFunctions {

    private ParamFunctions() {
    }

    @SuppressWarnings("unused")
    public static SimpleDoubleSupplier simpleDoubleSupplier(
            @Param("nOfResults") int nOfResults
    ) {
        return new SimpleDoubleSupplier(nOfResults);
    }
}
