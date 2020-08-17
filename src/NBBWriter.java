import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class NBBWriter {

    private String path;
    private String name;
    private int lengthFrames;
    private FileWriter writer;

    public NBBWriter(String path, String name, int length) {
        this.path = path;
        this.name = name;
        this.lengthFrames = length;
        try {
            writer = new FileWriter(path + name, true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void bakeFrame(int frame, ArrayList<InteractableBody> interactableObjects) {
        write(frame + " " + interactableObjects.size() + System.getProperty("line.separator"));
        for (InteractableBody b : interactableObjects) {
            write(getVisualDataFrom(b));
        }
    }

    private void write(String s) {
        try {
            writer.write(s);
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String getVisualDataFrom(InteractableBody b) {
        return b.getSimulationX() + " " + b.getSimulationY() + " " + b.getRadius() + System.getProperty("line.separator");
    }

    public int getLengthFrames() {
        return lengthFrames;
    }
}
