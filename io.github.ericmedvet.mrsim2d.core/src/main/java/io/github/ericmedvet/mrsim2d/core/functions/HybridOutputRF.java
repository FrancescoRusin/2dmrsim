package io.github.ericmedvet.mrsim2d.core.functions;

import java.util.Arrays;

public class HybridOutputRF extends AbstractParamRF {
    AbstractParamRF function1;
    AbstractParamRF function2;

    public HybridOutputRF(AbstractParamRF function1, AbstractParamRF function2) {
        if (function1.nOfInputs() != function2.nOfInputs()) {
            throw new IllegalArgumentException("Bad construction of MixedRF");
        }
        this.function1 = function1;
        this.function2 = function2;
    }

    @Override
    public double[] apply(double[] input) {
        double[] result1 = function1.apply(input);
        double[] result2 = function2.apply(input);
        double[] totalResult = new double[this.nOfOutputs()];
        System.arraycopy(result1, 0, totalResult, 0, result1.length);
        System.arraycopy(result2, 0, totalResult, result1.length, result2.length);
        return totalResult;
    }

    @Override
    public int nOfInputs() {
        return this.function1.nOfInputs();
    }

    @Override
    public int nOfOutputs() {
        return this.function1.nOfOutputs() + this.function2.nOfOutputs();
    }

    @Override
    public double[] getParams() {
        double[] f1Params = function1.getParams();
        double[] f2Params = function2.getParams();
        double[] totalParams = new double[f1Params.length + f2Params.length];
        System.arraycopy(f1Params, 0, totalParams, 0, f1Params.length);
        System.arraycopy(f2Params, 0, totalParams, f1Params.length, totalParams.length - f1Params.length);
        return totalParams;
    }

    @Override
    public void setParams(double[] params) {
        double[] f1Params = new double[function1.getParams().length];
        double[] f2Params = new double[function2.getParams().length];
        System.arraycopy(params, 0, f1Params, 0, f1Params.length);
        System.arraycopy(params, f1Params.length, f2Params, 0, f2Params.length);
        function1.setParams(f1Params);
        function2.setParams(f2Params);
    }
}
