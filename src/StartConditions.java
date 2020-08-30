import javafx.scene.paint.Color;

public class StartConditions {

    private double x;
    private double y;
    private double vx;
    private double vy;
    private double mass;
    private double interactableRadius;
    private boolean isStationary;
    private boolean isGasParticle;
    private InteractableBody.CollisionMode collisionMode;
    private Color color;

    public StartConditions(InteractableBody b) {
        this.x = b.getSimulationX();
        this.y = b.getSimulationY();
        this.vx = b.getVX();
        this.vy = b.getVY();
        this.mass = b.getMass();
        this.interactableRadius = b.getInteractableRadius();
        this.isStationary = b instanceof StationaryInteractableBody;
        this.isGasParticle = b instanceof GasParticle;
        this.color = (Color)b.getFill();
        this.collisionMode = b.getCollisionMode();
    }

    public StartConditions(double x, double y, double vx, double vy, double mass, boolean isStationary, boolean isGasParticle, Color color) {
        this.x = x;
        this.y = y;
        this.vx = vx;
        this.vy = vy;
        this.mass = mass;
        this.isStationary = isStationary;
        this.isGasParticle = isGasParticle;
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
        return isStationary;
    }

    public boolean isGasParticle() {
        return isGasParticle;
    }

    public InteractableBody.CollisionMode getCollisionMode() {
        return collisionMode;
    }
}
