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

package it.units.erallab.mrsim;

import it.units.erallab.mrsim.core.Snapshot;
import it.units.erallab.mrsim.core.actions.CreateRigidBodyAt;
import it.units.erallab.mrsim.core.actions.CreateUnmovableBodyAt;
import it.units.erallab.mrsim.core.geometry.Point;
import it.units.erallab.mrsim.core.geometry.Poly;
import it.units.erallab.mrsim.engine.Engine;
import it.units.erallab.mrsim.engine.dyn4j.Dyn4JEngine;
import it.units.erallab.mrsim.engine.simple.SimpleEngine;
import it.units.erallab.mrsim.viewer.Drawers;
import it.units.erallab.mrsim.viewer.FramesImageBuilder;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * @author "Eric Medvet" on 2022/07/06 for 2dmrsim
 */
public class Main {
  public static void main(String[] args) throws IOException {
    Poly triangle = Poly.regular(1, 3);
    Poly rectangle = Poly.rectangle(2, 1);
    Poly ground = Poly.rectangle(10, 2);
    Engine engine = new Dyn4JEngine();
    engine.perform(new CreateRigidBodyAt(triangle, 1, new Point(1, 1d)));
    engine.perform(new CreateUnmovableBodyAt(ground, new Point(0, -2d)));
    FramesImageBuilder builder = new FramesImageBuilder(
        300,
        200,
        10,
        0.25,
        FramesImageBuilder.Direction.HORIZONTAL,
        Drawers.basic()
    );
    while (engine.t() < 10) {
      Snapshot snapshot = engine.tick();
      if (engine.t() > 2 && snapshot.bodies().size() < 3) {
        engine.perform(new CreateRigidBodyAt(rectangle, 2, new Point(5, 1d)));
      }
      System.out.printf(
          "t=%4.1f nBodies=%1d nAgents=%1d%n",
          snapshot.t(),
          snapshot.bodies().size(),
          snapshot.agentPairs().size()
      );
      builder.accept(snapshot);
    }
    BufferedImage image = builder.get();
    ImageIO.write(image, "png", new File("/home/eric/experiments/simple.png"));
  }
}
