import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    public void start(Stage s) {
        Stage stage = s;
        stage.setMaximized(true);
        stage.setScene(new Scene(new Simulation().setUpWindow(stage)));
        stage.show();
    }

    private long closestMultiple(double a, int factor) {
        if(factor == 0) return Math.round(a);
        return a % factor > (double)factor / 2 ? (long)a / factor * factor + factor : (long)a / factor * factor;
    }
}
