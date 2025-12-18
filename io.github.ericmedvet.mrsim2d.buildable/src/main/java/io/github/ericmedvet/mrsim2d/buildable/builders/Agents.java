/*-
 * ========================LICENSE_START=================================
 * mrsim2d-buildable
 * %%
 * Copyright (C) 2020 - 2025 Eric Medvet
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * =========================LICENSE_END==================================
 */

package io.github.ericmedvet.mrsim2d.buildable.builders;

import io.github.ericmedvet.jnb.core.Discoverable;
import io.github.ericmedvet.jnb.core.Param;
import io.github.ericmedvet.jnb.datastructure.Grid;
import io.github.ericmedvet.jsdynsym.core.numerical.MultivariateRealFunction;
import io.github.ericmedvet.jsdynsym.core.numerical.NumericalDynamicalSystem;
import io.github.ericmedvet.mrsim2d.core.EmbodiedAgent;
import io.github.ericmedvet.mrsim2d.core.Sensor;
import io.github.ericmedvet.mrsim2d.core.agents.gridvsr.CentralizedNumGridVSR;
import io.github.ericmedvet.mrsim2d.core.agents.gridvsr.DistributedNumGridVSR;
import io.github.ericmedvet.mrsim2d.core.agents.gridvsr.GridBody;
import io.github.ericmedvet.mrsim2d.core.agents.gridvsr.ReactiveGridVSR;
import io.github.ericmedvet.mrsim2d.core.agents.gridvsr.ReactiveGridVSR.ReactiveVoxel;
import io.github.ericmedvet.mrsim2d.core.agents.independentvoxel.NumIndependentVoxel;
import io.github.ericmedvet.mrsim2d.core.agents.legged.AbstractLeggedHybridModularRobot;
import io.github.ericmedvet.mrsim2d.core.agents.legged.AbstractLeggedHybridRobot;
import io.github.ericmedvet.mrsim2d.core.agents.legged.NumLeggedHybridModularRobot;
import io.github.ericmedvet.mrsim2d.core.agents.legged.NumLeggedHybridRobot;
import io.github.ericmedvet.mrsim2d.core.bodies.Voxel;
import java.util.List;
import java.util.function.Function;

@Discoverable(prefixTemplate = "sim|s.agent|a")
public class Agents {

  private Agents() {
  }

  @SuppressWarnings("unused")
  public static CentralizedNumGridVSR centralizedNumGridVSR(
      @Param("body") GridBody body,
      @Param("nds") Function<NumericalDynamicalSystem<?>, NumericalDynamicalSystem<?>> nds
  ) {
    return new CentralizedNumGridVSR(
        body,
        MultivariateRealFunction.from(
            CentralizedNumGridVSR.nOfInputs(body),
            CentralizedNumGridVSR.nOfOutputs(body)
        )
    );
  }

  @SuppressWarnings("unused")
  public static DistributedNumGridVSR distributedNumGridVSR(
      @Param("body") GridBody body,
      @Param("nds") Function<NumericalDynamicalSystem<?>, NumericalDynamicalSystem<?>> nds,
      @Param(value = "nOfSignals", dI = 1) int nOfSignals,
      @Param("directional") boolean directional
  ) {
    return new DistributedNumGridVSR(
        body,
        Grid.create(
            body.grid().w(),
            body.grid().h(),
            k -> body.grid()
                .get(k)
                .element()
                .type()
                .equals(GridBody.VoxelType.NONE) ? null : MultivariateRealFunction.from(
                    DistributedNumGridVSR.nOfInputs(body, k, nOfSignals, directional),
                    DistributedNumGridVSR.nOfOutputs(body, k, nOfSignals, directional)
                )
        ),
        nOfSignals,
        directional
    );
  }

  @SuppressWarnings("unused")
  public static EmbodiedAgent dummyBox(
      @Param(value = "w", dI = 2) int nOfBaseVoxels,
      @Param(value = "h", dI = 2) int nOfHeightVoxels
  ) {
    GridBody body = new GridBody(
        Grid.create(
            nOfBaseVoxels,
            nOfHeightVoxels,
            (x, y) -> GridBody.VoxelType.RIGID
        ),
        g -> g.map(b -> List.of())
    );
    return new CentralizedNumGridVSR(
        body,
        MultivariateRealFunction.from(
            in -> new double[CentralizedNumGridVSR.nOfOutputs(body)],
            CentralizedNumGridVSR.nOfInputs(body),
            CentralizedNumGridVSR.nOfOutputs(body)
        )
    );
  }

  @SuppressWarnings("unused")
  public static NumIndependentVoxel numIndependentVoxel(
      @Param("sensors") List<Sensor<? super Voxel>> sensors,
      @Param(value = "areaActuation", dS = "sides") NumIndependentVoxel.AreaActuation areaActuation,
      @Param(value = "attachActuation", dB = true) boolean attachActuation,
      @Param(value = "nOfNFCChannels", dI = 1) int nOfNFCChannels,
      @Param("nds") Function<NumericalDynamicalSystem<?>, NumericalDynamicalSystem<?>> nds
  ) {
    return new NumIndependentVoxel(
        sensors,
        areaActuation,
        attachActuation,
        nOfNFCChannels,
        nds.apply(
            MultivariateRealFunction.from(
                NumIndependentVoxel.nOfInputs(sensors, nOfNFCChannels),
                NumIndependentVoxel.nOfOutputs(areaActuation, attachActuation, nOfNFCChannels)
            )
        )
    );
  }

  @SuppressWarnings("unused")
  public static NumLeggedHybridModularRobot numLeggedHybridModularRobot(
      @Param("modules") List<AbstractLeggedHybridModularRobot.Module> modules,
      @Param("nds") Function<NumericalDynamicalSystem<?>, NumericalDynamicalSystem<?>> nds
  ) {
    return new NumLeggedHybridModularRobot(
        modules,
        nds.apply(
            MultivariateRealFunction.from(
                NumLeggedHybridModularRobot.nOfInputs(modules),
                NumLeggedHybridModularRobot.nOfOutputs(modules)
            )
        )
    );
  }

  @SuppressWarnings("unused")
  public static NumLeggedHybridRobot numLeggedHybridRobot(
      @Param("legs") List<AbstractLeggedHybridRobot.Leg> legs,
      @Param(value = "trunkLength", dD = 4 * LeggedMisc.TRUNK_LENGTH) double trunkLength,
      @Param(value = "trunkWidth", dD = LeggedMisc.TRUNK_WIDTH) double trunkWidth,
      @Param(value = "trunkMass", dD = 4 * LeggedMisc.TRUNK_MASS) double trunkMass,
      @Param(value = "headMass", dD = LeggedMisc.TRUNK_WIDTH * LeggedMisc.TRUNK_WIDTH * LeggedMisc.RIGID_DENSITY) double headMass,
      @Param("headSensors") List<Sensor<?>> headSensors,
      @Param("nds") Function<NumericalDynamicalSystem<?>, NumericalDynamicalSystem<?>> nds
  ) {
    return new NumLeggedHybridRobot(
        legs,
        trunkLength,
        trunkWidth,
        trunkMass,
        headMass,
        headSensors,
        nds.apply(
            MultivariateRealFunction.from(
                NumLeggedHybridRobot.nOfInputs(legs, headSensors),
                NumLeggedHybridRobot.nOfOutputs(legs)
            )
        )
    );
  }

  @SuppressWarnings("unused")
  public static ReactiveGridVSR reactiveGridVSR(@Param("body") Grid<ReactiveVoxel> body) {
    return new ReactiveGridVSR(body);
  }
}