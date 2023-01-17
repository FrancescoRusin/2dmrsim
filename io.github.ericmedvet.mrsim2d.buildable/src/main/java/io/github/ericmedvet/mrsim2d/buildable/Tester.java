package io.github.ericmedvet.mrsim2d.buildable;

import io.github.ericmedvet.jnb.core.NamedBuilder;

public class Tester {
    public static void main(String[] args) {
        NamedBuilder<Object> builder = PreparedNamedBuilder.get();
        builder.build("sim.function.simpleDoubleSupplier(nOfResults = 10)");
    }
}
