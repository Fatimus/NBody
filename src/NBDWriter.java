import javafx.scene.paint.Color;

import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

public class NBDWriter {

    //    private String path = "D:\\Files\\N-Body Files\\";
    private String path;
    private String name;
    FileWriter writer;

    public NBDWriter(String path, String name) {
        this.path = path;
        this.name = name;
        try {
            writer = new FileWriter(path + name);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void write(ArrayList<InteractableBody> list) {
        int count = 0;
        try {
            for (InteractableBody b : list) {
                writer.write(getDataFrom(b));
                writer.flush();
                count++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
//        System.out.println("Operation Completed with " + count + " objects written to disk");
    }

    public void update(String fileName) {
        File nbd = new File("D:\\Files\\N-Body Files\\" + fileName + ".nbd");
        try {
            Scanner scan = new Scanner(nbd);
            int count = 0;
            try {
                FileWriter nbdWriter = new FileWriter(path + fileName + ".nbd");
                while (scan.hasNextLine()) {
                    nbdWriter.write(scan.nextDouble() + " ");
                    nbdWriter.write(scan.nextDouble() + " ");
                    nbdWriter.write(scan.nextDouble() + " ");
                    nbdWriter.write(scan.nextDouble() + " ");
                    double mass = scan.nextDouble();
                    nbdWriter.write(mass + " ");
                    nbdWriter.write(InteractableBody.defaultRadius(mass) + " ");
                    nbdWriter.write(scan.nextBoolean() + " ");
                    nbdWriter.write(scan.next() + System.getProperty("line.separator"));
                    scan.nextLine();
                    nbdWriter.flush();
                    count++;

                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println("Operation Completed with " + count + " objects written to disk");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private static String dataFrom(double x, double y, double vx, double vy, double mass, boolean isStationary, Color color) {
        return
                x + " " +
                        y + " " +
                        vx + " " +
                        vy + " " +
                        mass + " " +
                        InteractableBody.defaultRadius(mass) + " " +
                        isStationary + " " +
                        color.toString() +
                        System.getProperty("line.separator");
    }

    private static String getDataFrom(InteractableBody b) {
        return
                b.getSimulationX() + " " +
                        b.getSimulationY() + " " +
                        b.getVX() + " " + b.getVY() + " " +
                        b.getMass() + " " +
                        b.getInteractableRadius() + " " +
                        (b instanceof StationaryInteractableBody) + " " +
                        ((Color) (b.getFill())).toString() +
                        System.getProperty("line.separator");
    }

}
