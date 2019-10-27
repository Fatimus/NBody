import java.util.ArrayList;

public abstract class ScatterRegion extends InteractableUnit {

    protected ArrayList<Body> objects;
    protected double massPerObject;
    protected double massRandomizationRange;
    private boolean locked;

    public ScatterRegion(double x, double y, BodyGroup parentGroup, BuildState state) {
        super(x, y, parentGroup, state);
        objects = new ArrayList<>();

    }

    public abstract void generate();

    public void lock() {
        locked = true;
    }

    public void free() {
        locked = false;
    }

    public ArrayList<Body> getObjects() {
        return objects;
    }

    public void setMassPerObject(double massPerObject) {
        this.massPerObject = massPerObject;
    }

    public void setMassRandomizationRange(double massRandomizationRange) {
        this.massRandomizationRange = massRandomizationRange;
    }

    public double getMassPerObject() {
        return massPerObject;
    }

    public double getMassRandomizationRange() {
        return massRandomizationRange;
    }

    public double getMass() {
        double totalMass = 0;
        for(Body b : objects) {
            totalMass += b.getMass();
        }
        return totalMass;
    }
}
