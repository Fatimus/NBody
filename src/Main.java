import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    public void start(Stage s) {
        Stage stage = s;
        stage.setWidth(1280);
        stage.setHeight(720);
        stage.setMaximized(true);
        stage.setScene(new Scene(new Simulation().setUpWindow(stage)));
        stage.show();
    }
}
