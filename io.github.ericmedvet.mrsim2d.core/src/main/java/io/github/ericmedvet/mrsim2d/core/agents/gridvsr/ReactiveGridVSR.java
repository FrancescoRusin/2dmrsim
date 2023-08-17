package io.github.ericmedvet.mrsim2d.core.agents.gridvsr;

import io.github.ericmedvet.jsdynsym.core.numerical.NumericalDynamicalSystem;
import io.github.ericmedvet.jsdynsym.grid.Grid;
import io.github.ericmedvet.mrsim2d.core.Sensor;
import io.github.ericmedvet.mrsim2d.core.bodies.Body;
import io.github.ericmedvet.mrsim2d.core.bodies.Voxel;

import java.util.List;
import java.util.Map;

public class ReactiveGridVSR extends NumGridVSR {

  private final Grid<ReactiveVoxel> reactiveVoxelGrid;

  public ReactiveGridVSR(Grid<ReactiveVoxel> reactiveVoxelGrid) {
    this(reactiveVoxelGrid, VOXEL_SIDE_LENGTH, VOXEL_MASS);
  }

  public ReactiveGridVSR(
      Grid<ReactiveVoxel> reactiveVoxelGrid,
      double voxelSideLength,
      double voxelMass
  ) {
    super(
        new GridBody(reactiveVoxelGrid.map(re -> new GridBody.SensorizedElement(re.element(), re.sensors()))),
        voxelSideLength,
        voxelMass
    );
    this.reactiveVoxelGrid = reactiveVoxelGrid;
  }

  public record ReactiveVoxel(
      GridBody.Element element,
      List<Sensor<? super Body>> sensors,
      NumericalDynamicalSystem<?> numericalDynamicalSystem
  ) {
    public ReactiveVoxel {
      if (numericalDynamicalSystem.nOfInputs() != sensors.size()) {
        throw new IllegalArgumentException("Wrong number of inputs: %d found, %d expected by the controller".formatted(
            sensors.size(),
            numericalDynamicalSystem.nOfInputs()
        ));
      }
      if (numericalDynamicalSystem.nOfOutputs() != 4) {
        throw new IllegalArgumentException("Wrong number of outputs: %d produced by the controller, 4 expected".formatted(
            numericalDynamicalSystem.nOfInputs()
        ));
      }
    }
  }

  public record ReactiveVoxelAction(
      Map<Voxel.Side, Double> sideActions
  ) {}

  @Override
  protected Grid<double[]> computeActuationValues(double t, Grid<double[]> inputsGrid) {
    return inputsGrid.map((k, inputs) -> inputs == null ? new double[4] : reactiveVoxelGrid.get(k)
        .numericalDynamicalSystem()
        .step(t, inputs));
  }
}