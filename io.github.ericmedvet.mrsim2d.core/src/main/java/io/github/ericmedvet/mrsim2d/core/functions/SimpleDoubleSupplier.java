package io.github.ericmedvet.mrsim2d.core.functions;

public class SimpleDoubleSupplier implements ParamFunction<Double, Integer> {
    final double[] results;
    int counter;

    public SimpleDoubleSupplier(double[] results) {
        this.results = new double[results.length];
        System.arraycopy(results, 0, this.results, 0, results.length);
        this.counter = 0;
    }

    public SimpleDoubleSupplier(int size) {
        this.results = new double[size];
    }

    @Override
    public Integer apply(Double input) {
        return (int) Math.floor(this.results[counter++]);
    }

    @Override
    public ParamFunction<Double, Integer> copy() {
        return new SimpleDoubleSupplier(this.results);
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
