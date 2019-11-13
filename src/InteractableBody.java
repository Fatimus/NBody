import javafx.scene.input.MouseButton;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

public class InteractableBody extends Circle {

    private static int numberOfObjects = 0;
    private double vx, vy;
    private LeftBound leftBound;
    private RightBound rightBound;
    private double previousX, previousY;
    protected double simulationX, simulationY;
    private double mass;
    private double interactableRadius;
    private boolean isFirstUpdate;
    private boolean markedForCollision = false;
    private HashSet<InteractableBody> potentialIntersections = new HashSet<>();
    protected Simulation simulation;

    public InteractableBody(double x, double y, double vx, double vy, double mass, Simulation simulation) {
        super(
                x - (simulation.hasFocusBody() ? simulation.getFocusBody().getSimulationX() - simulation.getFocusBody().getCenterX() : 0),
                y - (simulation.hasFocusBody() ? simulation.getFocusBody().getSimulationY() - simulation.getFocusBody().getCenterY() : 0),
                defaultRadius(mass));
        interactableRadius = mass < 0 ? 0 : getRadius();
        isFirstUpdate = true;
        simulationX = x;
        simulationY = y;
        leftBound = new LeftBound(simulationX - interactableRadius, this);
        rightBound = new RightBound(simulationX + interactableRadius, this);
        this.mass = mass;
        this.vx = vx;
        this.vy = vy;
        this.simulation = simulation;
        numberOfObjects++;

        /**
         * NEW SECTION
         */
        setFill(mass < 0 ? Color.BLUE : Color.BLACK);
        setOnMouseClicked(e -> {
            if (e.isShiftDown()) remove();
            else if (e.getButton() == MouseButton.PRIMARY && e.isControlDown()) {
                simulation.setSelectedBody(this);
                simulation.getSelector().updateSelectorSize();
                simulation.getSelector().setVisible(true);
            }
        });
    }

    public void updateVelocity() {
        double accelX = 0;
        double accelY = 0;
        for (InteractableBody b : simulation.getInteractableObjects()) {
            if (!b.equals(this) && (mass == 0 ? b.getMass() != 0 : true)) {
                double distance = Math.sqrt((simulationX - b.getSimulationX()) * (simulationX - b.getSimulationX()) + (simulationY - b.getSimulationY()) * (simulationY - b.getSimulationY()));
                double inverseDistance = 1 / distance;
                if (distance >= interactableRadius + b.interactableRadius || simulation.getClippingType().equals("Full")) {
                    double force = calculateForce(b.mass, inverseDistance);
                    accelX += (simulationX - b.simulationX) * inverseDistance * force;
                    accelY += (simulationY - b.simulationY) * inverseDistance * force;
                }
            }
        }
        vx -= accelX;
        vy -= accelY;
    }

    public CollisionGroup detectCollisions() {
        if(markedForCollision) return null;
        List<InteractableBody> collisionObjects = Collections.synchronizedList(new ArrayList<>());
//        System.out.println(this + ": " + potentialIntersections);
        potentialIntersections.parallelStream()
//        simulation.getInteractableObjects().parallelStream()
                .forEach(b -> {
                    if (
                            !b.equals(this) &&
                            !(mass == 0 && b.getMass() == 0) &&
                            !b.markedForCollision &&
                            Math.sqrt(
                                    (simulationX - b.getSimulationX()) * (simulationX - b.getSimulationX()) +
                                            (simulationY - b.getSimulationY()) * (simulationY - b.getSimulationY())) < interactableRadius + b.interactableRadius &&
                            !(b instanceof StationaryInteractableBody)) {
                        collisionObjects.add(b);
                        b.markedForCollision = true;
                        markedForCollision = true;
                    }
                });
        potentialIntersections.clear();
        return collisionObjects.isEmpty() ? null : new CollisionGroup(this, collisionObjects);
    }

    private double calculateForce(double mass, double inverseDistance) {
        return simulation.GRAVITATIONAL_CONSTANT * simulation.getPlaybackSpeed() * simulation.getPlaybackSpeed() * mass * inverseDistance * inverseDistance;
    }

    public void updateLocation(double factor) {
        simulationX += vx * factor;
        simulationY += vy * factor;
        leftBound.setValue(simulationX - interactableRadius);
        rightBound.setValue(simulationX + interactableRadius);
    }

    public void updateVisualLocation() {
        if (simulation.hasFocusBody()) {
            setCenterX(simulationX - (simulation.getFocusBody().getSimulationX() - simulation.getFocusBody().getCenterX()));
            setCenterY(simulationY - (simulation.getFocusBody().getSimulationY() - simulation.getFocusBody().getCenterY()));
        } else {
            setCenterX(simulationX);
            setCenterY(simulationY);
        }
        if (!isFirstUpdate && simulation.trailsEnabled()) {
            Line trailLink = new Line(previousX, previousY, getCenterX(), getCenterY());
            trailLink.setStroke(Color.BLUE);
            simulation.getTrails().getChildren().add(trailLink);
        } else isFirstUpdate = false;
    }

    public void scaleVelocity(double scaleFactor) {
        vx *= scaleFactor;
        vy *= scaleFactor;
    }

    public void remove() {
        simulation.getInteractableObjects().remove(this);
        simulation.getVisibleObjects().getChildren().remove(this);
        simulation.getBounds().remove(leftBound);
        simulation.getBounds().remove(rightBound);
        if (this.equals(simulation.getFocusBody())) simulation.setFocusBody(null);
        if (this.equals(simulation.getSelectedBody())) simulation.setSelectedBody(null);
    }

    public double getMass() {
        return mass;
    }

    public void setSimulationX(double simulationX) {
        this.simulationX = simulationX;
    }

    public void setSimulationY(double simulationY) {
        this.simulationY = simulationY;
    }

    public double getSimulationX() {
        return simulationX;
    }

    public double getSimulationY() {
        return simulationY;
    }

    public LeftBound getLeftBound() {
        return leftBound;
    }

    public RightBound getRightBound() {
        return rightBound;
    }

    public double getVX() {
        return vx;
    }

    public double getVY() {
        return vy;
    }

    public void setPreviousX(double previousX) {
        this.previousX = previousX;
    }

    public void setPreviousY(double previousY) {
        this.previousY = previousY;
    }

    public double getPreviousX() {
        return previousX;
    }

    public double getPreviousY() {
        return previousY;
    }

    public double getInteractableRadius() {
        return interactableRadius;
    }

    public boolean isFirstUpdate() {
        return isFirstUpdate;
    }

    public boolean isMarkedForCollision() {
        return markedForCollision;
    }

    public void setFirstUpdateStatus(boolean value) {
        isFirstUpdate = value;
    }

    public void setInteractableRadius(double interactableRadius) {
        this.interactableRadius = interactableRadius;
        setRadius(interactableRadius);
        leftBound.setValue(simulationX - interactableRadius);
        rightBound.setValue(simulationX - interactableRadius);
    }

    public HashSet<InteractableBody> getPotentialIntersections() {
        return potentialIntersections;
    }

    public static int numberOfObjects() {
        return numberOfObjects;
    }

    public static double defaultRadius(double mass) {
        return mass < 0 ? 3 : Math.pow(3.0 / 4 * mass, 1.0 / 3) + 1.5;
    }
}
