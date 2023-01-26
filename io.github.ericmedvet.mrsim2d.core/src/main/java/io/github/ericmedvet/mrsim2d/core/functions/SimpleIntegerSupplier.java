package io.github.ericmedvet.mrsim2d.core.functions;

public class SimpleIntegerSupplier implements ParamFunction<Double, Integer> {
    private final double[] results;
    private final int minimum;
    private final int maximum;
    private int counter;

    public SimpleIntegerSupplier(double[] results, int minimum, int maximum) {
        this.results = new double[results.length];
        System.arraycopy(results, 0, this.results, 0, results.length);
        this.counter = 0;
        this.minimum = minimum;
        this.maximum = maximum;
    }

    public SimpleIntegerSupplier(int size, int minimum, int maximum) {
        this.results = new double[size];
        this.minimum = minimum;
        this.maximum = maximum;
    }

    @Override
    public Integer apply(Double input) {
        int result = (int) Math.floor((this.results[counter++] + 1d) / 2d * (maximum - minimum)) + minimum;
        return Math.max(minimum, Math.min(result, maximum));
    }

    @Override
    public double[] getParams() {
        return results;
    }

    @Override
    public void setParams(double[] params) {
        System.arraycopy(params, 0, this.results, 0, this.results.length);
    }
}
