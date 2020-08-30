public class StationaryInteractableBody extends InteractableBody {

    public StationaryInteractableBody(double x, double y, double mass, CollisionMode collisionMode, Simulation simulation) {
        super(x, y, 0, 0, mass, collisionMode, simulation);
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
