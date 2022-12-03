/*
 * Copyright 2022 Eric Medvet <eric.medvet@gmail.com> (as eric)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package it.units.erallab.mrsim2d.core.functions;

import java.util.Arrays;
import java.util.random.RandomGenerator;

/**
 * @author "Eric Medvet" on 2022/07/17 for 2dmrsim
 */
public class NoisedTRF extends CompositeTRF {
  private final double inputSigma;
  private final double ouputSigma;
  private final RandomGenerator randomGenerator;

  public NoisedTRF(TimedRealFunction innerF, double inputSigma, double ouputSigma, RandomGenerator randomGenerator) {
    super(innerF);
    this.inputSigma = inputSigma;
    this.ouputSigma = ouputSigma;
    this.randomGenerator = randomGenerator;
  }

  @Override
  public double[] apply(double t, double[] input) {
    double[] noisedInput = input;
    if (inputSigma > 0) {
      noisedInput = Arrays.stream(input).map(v -> randomGenerator.nextGaussian(0, inputSigma)).toArray();
    }
    double[] noisedOutput = innerF.apply(t, noisedInput);
    if (inputSigma > 0) {
      noisedOutput = Arrays.stream(noisedOutput).map(v -> randomGenerator.nextGaussian(0, ouputSigma)).toArray();
    }
    return noisedOutput;
  }

  @Override
  public int nOfInputs() {
    return innerF.nOfInputs();
  }

  @Override
  public int nOfOutputs() {
    return innerF.nOfOutputs();
  }

  @Override
  public String toString() {
    return "NoisedTRF{" +
        "inputSigma=" + inputSigma +
        ", ouputSigma=" + ouputSigma +
        '}';
  }
}