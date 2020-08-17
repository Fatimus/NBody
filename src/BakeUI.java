import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

public class BakeUI extends Pane {

    private Simulation simulation;
    private Stage bakeStage;

    private Text fileNamePrompt;
    private TextField fileNameField;
    private Text fileExtensionText;
    private Text outputPathPrompt;
    private TextField outputPathField;
    private Button pathBrowserButton;
    private Text framePrompt;
    private TextField frameField;
    private Text illegalFrameCountWarning;
    private Button startBakeButton;
    private Button cancelButton;

    public BakeUI(Simulation s, Stage ownerWindow) {
        this.simulation = s;
        this.bakeStage = ownerWindow;
        getChildren().addAll(
                fileNamePrompt(), fileNameField(), fileExtensionText(),
                outputPathPrompt(), outputPathField(), pathBrowserButton(),
                framePrompt(), frameField(),
                illegalFrameCountWarning(),
                cancelButton(), startBakeButton()
        );
    }

    private Text fileNamePrompt() {
        fileNamePrompt = new Text("File name:");
        fileNamePrompt.setFont(Font.font(13));
        fileNamePrompt.setTranslateX(15);
        fileNamePrompt.setTranslateY(25);
        return fileNamePrompt;
    }

    private TextField fileNameField() {
        fileNameField = new TextField();
        fileNameField.setPrefWidth(215);
        fileNameField.setTranslateX(125);
        fileNameField.setTranslateY(9);
        fileNameField.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if(newValue.length() != 0 && outputPathField.getText().length() != 0 && frameField.getText().length() != 0) startBakeButton.setDisable(false);
                else startBakeButton.setDisable(true);
            }
        });
        return fileNameField;
    }

    private Text fileExtensionText() {
        fileExtensionText = new Text(".nbb");
        fileExtensionText.setFont(Font.font(13));
        fileExtensionText.setTranslateX(345);
        fileExtensionText.setTranslateY(25);
        return fileExtensionText;
    }

    private Text outputPathPrompt() {
        outputPathPrompt = new Text("Output directory:");
        outputPathPrompt.setFont(Font.font(13));
        outputPathPrompt.setTranslateX(15);
        outputPathPrompt.setTranslateY(55);
        return outputPathPrompt;
    }

    private TextField outputPathField() {
        outputPathField = new TextField();
        outputPathField.setPrefWidth(170);
        outputPathField.setTranslateX(125);
        outputPathField.setTranslateY(39);
        outputPathField.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if(newValue.length() != 0 && fileNameField.getText().length() != 0 && frameField.getText().length() != 0) startBakeButton.setDisable(false);
                else startBakeButton.setDisable(true);
            }
        });
        return outputPathField;
    }

    private Button pathBrowserButton() {
        pathBrowserButton = new Button("Browse...");
        pathBrowserButton.setPrefWidth(75);
        pathBrowserButton.setTranslateX(300);
        pathBrowserButton.setTranslateY(39);
        pathBrowserButton.setOnMouseClicked(e -> {
            DirectoryChooser savePathChooser = new DirectoryChooser();
            savePathChooser.setTitle("Save as...");
            outputPathField.setText(savePathChooser.showDialog(bakeStage).getPath() + "\\");
        });
        return pathBrowserButton;
    }

    private Text framePrompt() {
        framePrompt = new Text("Frames to bake:");
        framePrompt.setFont(Font.font(13));
        framePrompt.setTranslateX(15);
        framePrompt.setTranslateY(85);
        return framePrompt;
    }

    private TextField frameField() {
        // matches regex ^[1-9]\d*$
        frameField = new TextField();
        frameField.setPrefWidth(250);
        frameField.setTranslateX(125);
        frameField.setTranslateY(69);
        frameField.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if(newValue.length() != 0 && fileNameField.getText().length() != 0 && outputPathField.getText().length() != 0) startBakeButton.setDisable(false);
                else startBakeButton.setDisable(true);
            }
        });
        return frameField;
    }

    private Text illegalFrameCountWarning() {
        illegalFrameCountWarning = new Text("Frame count must be a positive integer.");
        illegalFrameCountWarning.setTranslateX(125);
        illegalFrameCountWarning.setTranslateY(105);
        illegalFrameCountWarning.setFill(Color.RED);
        illegalFrameCountWarning.setVisible(false);
        return illegalFrameCountWarning;
    }

    private Button cancelButton() {
        cancelButton = new Button("Cancel");
        cancelButton.setPrefWidth(120);
        cancelButton.setTranslateX(50);
        cancelButton.setTranslateY(120);
        cancelButton.setOnMouseClicked(e -> bakeStage.close());
        return cancelButton;
    }

    private Button startBakeButton() {
        startBakeButton = new Button("Bake");
        startBakeButton.setPrefWidth(120);
        startBakeButton.setTranslateX(230);
        startBakeButton.setTranslateY(120);
        startBakeButton.setDisable(true);
        startBakeButton.setOnMouseClicked(e -> {
            if(frameField.getText().matches("^[1-9]\\d*$")) {
                simulation.startBake(new NBBWriter(outputPathField.getText(), fileNameField.getText() + ".nbb", Integer.parseInt(frameField.getText())));
                bakeStage.close();
            } else {
                illegalFrameCountWarning.setVisible(true);
            }
        });
        return startBakeButton;
    }
}
