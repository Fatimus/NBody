import javafx.scene.shape.Line;

public class TrailLink extends Line {

    private int age;
    private Simulation simulation;

    public TrailLink(double startX, double startY, double endX, double endY, Simulation s) {
        super(startX, startY, endX, endY);
        simulation = s;
    }

    public boolean removeIfAgeExceeded() {
        if(age >= simulation.getTrailLength()) {
            simulation.getTrails().getChildren().remove(this);
            return true;
        }
        return false;
    }

    public void age() {
        age++;
    }

    public int getAge() {
        return age;
    }
}
