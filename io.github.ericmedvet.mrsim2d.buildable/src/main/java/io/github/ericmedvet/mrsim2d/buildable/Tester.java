package io.github.ericmedvet.mrsim2d.buildable;

import io.github.ericmedvet.jnb.core.NamedBuilder;
import io.github.ericmedvet.mrsim2d.core.functions.SimpleIntegerSupplier;

public class Tester {
    public static void main(String[] args) {
        NamedBuilder<Object> builder = PreparedNamedBuilder.get();
        SimpleIntegerSupplier supplier = (SimpleIntegerSupplier) builder.build("sim.function.simpleIntegerSupplier(nOfResults = 10)");
        double[] array1 = new double[10];
        for(int i = 0; i < 10; ++i) {
            array1[i] = i / 5d - 0.95;
        }
        supplier.setParams(array1);
        for(int i = 0; i < 10; ++i) {
            System.out.println(supplier.apply((double) i));
        }
    }
}
