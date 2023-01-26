package io.github.ericmedvet.mrsim2d.core.functions;

public class Constant extends AbstractParamRF {
    protected final int nOfInputs;
    protected final double[] outputs;

    public Constant(int nOfInputs, double[] outputs) {
        this.nOfInputs = nOfInputs;
        this.outputs = new double[outputs.length];
        System.arraycopy(outputs, 0, this.outputs, 0, outputs.length);
    }

    @Override
    public double[] apply(double[] input) {
        return outputs;
    }

    @Override
    public double[] getParams() {
        return new double[0];
    }

    @Override
    public void setParams(double[] params) {}

    @Override
    public int nOfInputs() {
        return nOfInputs;
    }

    @Override
    public int nOfOutputs() {
        return outputs.length;
    }
}
