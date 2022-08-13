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

package it.units.erallab.mrsim.builders;

import it.units.erallab.mrsim.core.actions.Sense;
import it.units.erallab.mrsim.core.bodies.Voxel;
import it.units.erallab.mrsim.util.Grid;
import it.units.erallab.mrsim.util.builder.Param;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * @author "Eric Medvet" on 2022/08/11 for 2dmrsim
 */
public class VSRSensorizingFunctionBuilder {

  public static Function<Grid<Boolean>, Grid<List<Function<Voxel, Sense<? super Voxel>>>>> empty() {
    return shape -> Grid.create(shape, b -> List.of());
  }

  public static Function<Grid<Boolean>, Grid<List<Function<Voxel, Sense<? super Voxel>>>>> uniform(
      @Param(value = "sensors") List<Function<Voxel, Sense<? super Voxel>>> sensors
  ) {
    return shape -> Grid.create(
        shape,
        b -> sensors
    );
  }

  public static Function<Grid<Boolean>, Grid<List<Function<Voxel, Sense<? super Voxel>>>>> directional(
      @Param(value = "nSsensors") List<Function<Voxel, Sense<? super Voxel>>> nSensors,
      @Param(value = "eSensors") List<Function<Voxel, Sense<? super Voxel>>> eSensors,
      @Param(value = "sSensors") List<Function<Voxel, Sense<? super Voxel>>> sSensors,
      @Param(value = "wSensors") List<Function<Voxel, Sense<? super Voxel>>> wSensors
  ) {
    return shape -> Grid.create(shape.w(), shape.h(), (Integer x, Integer y) -> {
      if (!shape.get(x, y)) {
        return null;
      }
      int maxX = shape.entries()
          .stream()
          .filter(e -> e.key().y() == y && e.value())
          .mapToInt(e -> e.key().x())
          .max()
          .orElse(0);
      int minX = shape.entries()
          .stream()
          .filter(e -> e.key().y() == y && e.value())
          .mapToInt(e -> e.key().x())
          .min()
          .orElse(0);
      List<Function<Voxel, Sense<? super Voxel>>> localSensors = new ArrayList<>();
      if (x == maxX) {
        localSensors.addAll(eSensors);
      }
      if (x == minX) {
        localSensors.addAll(wSensors);
      }
      if (y == 0) {
        localSensors.addAll(sSensors);
      }
      if (y == shape.h() - 1) {
        localSensors.addAll(nSensors);
      }
      return localSensors;
    });
  }

}