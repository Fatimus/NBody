public class StationaryInteractableBody extends InteractableBody {

    public StationaryInteractableBody(double mass, double x, double y, Simulation s) {
        super(x, y, 0, 0, mass, s);
    }

    @Override
    public void updateVelocity() {
        age++;
    }

    @Override
    public void updateLocation(double factor) {}

    @Override
    public void updateVisualLocation() {
        if (simulation.hasFocusBody()) {
            setCenterX(simulationX - (simulation.getFocusBody().getSimulationX() - simulation.getFocusBody().getCenterX()));
            setCenterY(simulationY - (simulation.getFocusBody().getSimulationY() - simulation.getFocusBody().getCenterY()));
        }
    }
}
