/*
 * Copyright 2022 eric
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

package it.units.erallab.mrsim2d.core.agents.independentvoxel;

import it.units.erallab.mrsim2d.core.Action;
import it.units.erallab.mrsim2d.core.ActionOutcome;
import it.units.erallab.mrsim2d.core.Sensor;
import it.units.erallab.mrsim2d.core.actions.ActuateVoxel;
import it.units.erallab.mrsim2d.core.actions.AttractAndLinkClosestAnchorable;
import it.units.erallab.mrsim2d.core.actions.DetachAnchors;
import it.units.erallab.mrsim2d.core.actions.Sense;
import it.units.erallab.mrsim2d.core.bodies.Anchor;
import it.units.erallab.mrsim2d.core.bodies.Voxel;
import it.units.erallab.mrsim2d.core.functions.TimedRealFunction;
import it.units.erallab.mrsim2d.core.util.Parametrized;
import it.units.malelab.jnb.core.BuilderMethod;
import it.units.malelab.jnb.core.Param;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;

/**
 * @author "Eric Medvet" on 2022/07/13 for 2dmrsim
 */
public class NumIndependentVoxel extends AbstractIndependentVoxel implements Parametrized {

  private final static double ATTACH_ACTION_THRESHOLD = 0.1d;
  private final static int N_OF_OUTPUTS = 8;

  private final List<Sensor<? super Voxel>> sensors;
  private final double[] inputs;
  private final TimedRealFunction timedRealFunction;

  public NumIndependentVoxel(
      Voxel.Material material,
      double voxelSideLength,
      double voxelMass,
      List<Sensor<? super Voxel>> sensors,
      TimedRealFunction timedRealFunction
  ) {
    super(material, voxelSideLength, voxelMass);
    this.sensors = sensors;
    inputs = new double[sensors.size()];
    this.timedRealFunction = timedRealFunction;
  }

  @BuilderMethod
  public NumIndependentVoxel(
      @Param("sensors") List<Sensor<? super Voxel>> sensors,
      @Param("function") BiFunction<Integer, Integer, ? extends TimedRealFunction> timedRealFunctionBuilder
  ) {
    this(sensors, timedRealFunctionBuilder.apply(nOfInputs(sensors), nOfOutputs()));
  }

  public NumIndependentVoxel(List<Sensor<? super Voxel>> sensors, TimedRealFunction timedRealFunction) {
    this(new Voxel.Material(), VOXEL_SIDE_LENGTH, VOXEL_MASS, sensors, timedRealFunction);
  }

  public static int nOfInputs(List<Sensor<? super Voxel>> sensors) {
    return sensors.size();
  }

  public static int nOfOutputs() {
    return N_OF_OUTPUTS;
  }

  @SuppressWarnings("unchecked")
  @Override
  public List<? extends Action<?>> act(double t, List<ActionOutcome<?, ?>> previousActionOutcomes) {
    //read inputs from last request
    double[] readInputs = previousActionOutcomes.stream()
        .filter(ao -> ao.action() instanceof Sense)
        .mapToDouble(ao -> {
          ActionOutcome<Sense<? super Voxel>, Double> so = (ActionOutcome<Sense<? super Voxel>, Double>) ao;
          return so.action().range().normalize(so.outcome().orElse(0d));
        })
        .toArray();
    System.arraycopy(readInputs, 0, inputs, 0, readInputs.length);
    //compute actuation
    double[] outputs = timedRealFunction.apply(t, inputs);
    //generate next sense actions
    List<Action<?>> actions = new ArrayList<>(sensors.stream().map(f -> f.apply(voxel)).toList());
    //generate actuation actions
    actions.add(new ActuateVoxel(voxel, outputs[0], outputs[1], outputs[2], outputs[3]));
    for (int i = 0; i < Voxel.Side.values().length; i++) {
      Voxel.Side side = Voxel.Side.values()[i];
      double m = outputs[i + 4];
      if (m > ATTACH_ACTION_THRESHOLD) {
        actions.add(new AttractAndLinkClosestAnchorable(voxel.anchorsOn(side), 1, Anchor.Link.Type.SOFT));
      } else if (m < -ATTACH_ACTION_THRESHOLD) {
        actions.add(new DetachAnchors(voxel.anchorsOn(side)));
      }
    }
    return actions;
  }

  @Override
  public double[] getParams() {
    if (timedRealFunction instanceof Parametrized parametrized) {
      return parametrized.getParams();
    }
    return new double[0];
  }

  @Override
  public void setParams(double[] params) {
    if (timedRealFunction instanceof Parametrized parametrized) {
      parametrized.setParams(params);
    } else if (params.length > 0) {
      throw new IllegalArgumentException("Cannot set params because the function %s has no params".formatted(
          timedRealFunction));
    }
  }

  public List<Sensor<? super Voxel>> getSensors() {
    return sensors;
  }
}
