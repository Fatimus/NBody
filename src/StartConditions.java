import javafx.scene.paint.Color;

public class StartConditions {

    private double x;
    private double y;
    private double vx;
    private double vy;
    private double mass;
    private double interactableRadius;
    private boolean stationary;
    private Color color;

    public StartConditions(InteractableBody b) {
        this.x = b.getSimulationX();
        this.y = b.getSimulationY();
        this.vx = b.getVX();
        this.vy = b.getVY();
        this.mass = b.getMass();
        this.interactableRadius = b.getInteractableRadius();
        this.stationary = b instanceof StationaryInteractableBody;
        this.color = (Color)b.getFill();
    }

    public StartConditions(double x, double y, double vx, double vy, double mass, boolean stationary, Color color) {
        this.x = x;
        this.y = y;
        this.vx = vx;
        this.vy = vy;
        this.mass = mass;
        this.stationary = stationary;
        this.color = color;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getVX() {
        return vx;
    }

    public double getVY() {
        return vy;
    }

    public double getMass() {
        return mass;
    }

    public double getInteractableRadius() {
        return interactableRadius;
    }

    public Color getColor() {
        return color;
    }

    public boolean isStationary() {
        return stationary;
    }
}
