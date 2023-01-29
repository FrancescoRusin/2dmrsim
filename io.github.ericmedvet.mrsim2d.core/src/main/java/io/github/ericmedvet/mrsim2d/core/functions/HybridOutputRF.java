package io.github.ericmedvet.mrsim2d.core.functions;

import java.util.ArrayList;
import java.util.List;

public class HybridOutputRF extends AbstractParamRF {
    AbstractParamRF function1;
    AbstractParamRF function2;
    List<Integer> distribution;

    public HybridOutputRF(AbstractParamRF function1, AbstractParamRF function2, List<Integer> distribution) {
        if (function1.nOfInputs() != function2.nOfInputs() || function1.nOfOutputs() + function2.nOfOutputs() != distribution.size()) {
            throw new IllegalArgumentException("Bad construction of MixedRF");
        }
        for (Integer i : distribution) {
            if (i != 1 && i != 2) {
                throw new IllegalArgumentException("Bad construction of MixedRF");
            }
        }
        if (distribution.stream().filter(i -> i == 1).count() != function1.nOfOutputs() ||
                distribution.stream().filter(i -> i == 2).count() != function2.nOfOutputs()) {
            throw new IllegalArgumentException("Bad construction of MixedRF");
        }
        this.function1 = function1;
        this.function2 = function2;
        this.distribution = distribution;
    }

    public HybridOutputRF(AbstractParamRF function1, AbstractParamRF function2) {
        List<Integer> distribution = new ArrayList<>(function1.nOfOutputs() + function2.nOfOutputs());
        for(int i = 0; i < function1.nOfOutputs(); ++i) {
            distribution.add(1);
        }
        for(int i = 0; i < function2.nOfOutputs(); ++i) {
            distribution.add(2);
        }
        this.function1 = function1;
        this.function2 = function2;
        this.distribution = distribution;
    }

    @Override
    public double[] apply(double[] input) {
        double[] result1 = function1.apply(input);
        double[] result2 = function2.apply(input);
        double[] totalResult = new double[this.nOfOutputs()];
        int c1 = 0;
        int c2 = 0;
        for(int i = 0; i < totalResult.length; ++i) {
            if(distribution.get(i) == 1) {
                totalResult[i] = result1[c1++];
            } else {
                totalResult[i] = result2[c2++];
            }
        }
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
