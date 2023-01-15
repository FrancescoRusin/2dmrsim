package io.github.ericmedvet.mrsim2d.core.functions;

import java.util.*;

public class MixedRF extends AbstractParamRF {
    AbstractParamRF function1;
    AbstractParamRF function2;

    public MixedRF(AbstractParamRF function1, AbstractParamRF function2) {
        if (function1.nOfInputs() != function2.nOfInputs() || function1.nOfOutputs() != function2.nOfOutputs()) {
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
        return this.function1.nOfOutputs();
    }

    @Override
    public double[] getParams() {
        double[] f1Params = function1.getParams();
        double[] f2Params = function2.getParams();
        double[] totalParams = new double[f1Params.length + f2Params.length];
        System.arraycopy(f1Params, 0, totalParams, 0, f1Params.length);
        if (totalParams.length - f1Params.length >= 0)
            System.arraycopy(f2Params, f1Params.length - f1Params.length, totalParams, f1Params.length, totalParams.length - f1Params.length);
        return totalParams;
    }

    @Override
    public void setParams(double[] params) {
        double[] f1Params = new double[function1.getParams().length];
        double[] f2Params = new double[function2.getParams().length];
        for (int i = 0; i < f1Params.length; ++i) {
            f1Params[i] = params[i];
        }
        for (int j = f1Params.length; j < params.length; ++j) {
            f2Params[j - f1Params.length] = params[j];
        }
        function1.setParams(f1Params);
        function2.setParams(f2Params);
    }
}
