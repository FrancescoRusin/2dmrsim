package io.github.ericmedvet.mrsim2d.core.functions;

import io.github.ericmedvet.mrsim2d.core.util.Parametrized;

import java.util.*;

public class MultiControl implements TimedRealFunction, Parametrized {
    protected final TreeMap<Double, AbstractParamRF> functionMap;
    protected final List<Double> timeMap;

    public MultiControl(TreeMap<Double, AbstractParamRF> functionMap) {
        if(functionMap.values().stream().map(f -> List.of(f.nOfInputs(), f.nOfOutputs())).distinct().count() > 1){
            throw new IllegalArgumentException(String.format(
                    "The functions should all have the same input and output length; found %s as inputs and %s as outputs",
                    functionMap.values().stream().map(TimedRealFunction::nOfInputs).toList(),
                    functionMap.values().stream().map(TimedRealFunction::nOfOutputs).toList()
            ));
        }
        this.functionMap = new TreeMap<>(functionMap);
        this.timeMap = this.functionMap.keySet().stream().sorted().toList();
    }
    @Override
    public double[] apply(double t, double[] input) {
        for(double k : timeMap){
            if(k >= t){
                return functionMap.get(k).apply(input);
            }
        }
        return null;
    }

    @Override
    public double[] getParams() {
        return functionMap.values().stream().map(f -> Arrays.stream(f.getParams()).boxed().toList())
                .flatMap(List::stream).mapToDouble(d -> d).toArray();
    }

    @Override
    public void setParams(double[] params) {
        List<Double> paramList = Arrays.stream(params).boxed().toList();
        int counter = 0;
        int paramNumber;
        for(Double k : functionMap.keySet()){
            paramNumber = functionMap.get(k).getParams().length;
            functionMap.get(k).setParams(paramList.subList(counter, counter + paramNumber).stream().mapToDouble(d -> d).toArray());
            counter += paramNumber;
        }
    }

    @Override
    public int nOfInputs() {
        return functionMap.get(functionMap.firstKey()).nOfInputs();
    }

    @Override
    public int nOfOutputs() {
        return functionMap.get(functionMap.firstKey()).nOfOutputs();
    }
}
