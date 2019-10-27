import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

public class EditorUI extends Group {

    private Simulation simulation;

    private Text newUnitPrompt;
    private final int ANCHOR_X = 50;
    private final int ANCHOR_Y = 120;

    public EditorUI(Simulation simulation) {
        this.simulation = simulation;
        setTranslateX(ANCHOR_X);
        setTranslateY(ANCHOR_Y);
        getChildren().addAll(
                newUnitPrompt()
//                newBodyButton()
        );
    }

    private Text newUnitPrompt() {
        newUnitPrompt = new Text("Add");
        newUnitPrompt.setFont(Font.font(14));
        return newUnitPrompt;
    }

    private Button newBodyButton(){
        Button newBodyButton = new Button("Body");
        newBodyButton.setTranslateX(30);
        newBodyButton.setTranslateY(-100);
        newBodyButton.setPrefWidth(100);
        return newBodyButton;
    }
}
