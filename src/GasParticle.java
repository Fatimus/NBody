import javafx.scene.paint.Color;

public class GasParticle extends InteractableBody {

    private double rMaxForce;
    private final double R_MAX_UPPER_BOUND = 34;
    private final double R_MAX_LOWER_BOUND = 3.74999;

    public GasParticle(double x, double y, double vx, double vy, double mass, Simulation simulation) {
        super(x, y, vx, vy, mass, CollisionMode.DISABLED, simulation);
        if (mass < 0 || mass > 100)
            throw new IllegalArgumentException("criteria failed: 0 < m < 100, mass must be between 0 and 100");
        setFill(Color.RED);
        rMaxForce = rMaxBinarySearch(R_MAX_LOWER_BOUND, R_MAX_UPPER_BOUND);
    }

    private double rMaxBinarySearch(double lowerBound, double upperBound) {
        double midpoint = (lowerBound + upperBound) / 2;
        if(upperBound - lowerBound < 0.0001) return midpoint;
        double repulsiveForce = baseRepulsiveFunction(midpoint);
        double attractiveForce = baseAttractiveFunction(midpoint);
        if (repulsiveForce > attractiveForce) return rMaxBinarySearch(lowerBound, midpoint);
        else if (repulsiveForce < attractiveForce) return rMaxBinarySearch(midpoint, upperBound);
        else return midpoint;
    }

    public double baseRepulsiveFunction(double r) {
        double inverseVisibleRadius = 1 / getRadius();
        return 10 * (16.0 * 0.020408 * inverseVisibleRadius * inverseVisibleRadius - 1 / Math.pow(r - 3 * getRadius() * 0.25, 2));
    }

    private double baseAttractiveFunction(double r) {
        return getMass() / r / r;
    }

    public double getRMaxForce() {
        return rMaxForce;
    }
}
