import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class SimulationUI extends Group {

    private Simulation simulation;

    public Text playingStatus;
    public SettableNumberField massField;
    public SettableNumberField playbackSpeedField;
    public SettableNumberField trailLengthField;
    public CheckBox trailsEnabled;
    public CheckBox collisionsEnabled;
    public CheckBox splittingEnabled;
    public Button clippingTypeSelector;
    public Button focusBodySetter;
    public Button dragSetter;
    public Button stepFrameButton;
    public Button playPauseButton;
    public Button backButton;
    public ImageView playIcon;
    public ImageView pauseIcon;
    public ImageView backIcon;
    public Text fpsCounter;
    public Text objectCounter;
    public Button fileOpenerButton;
    public Button bakeButton;

//    private double

    public SimulationUI(Simulation simulation) {
        this.simulation = simulation;
        getChildren().addAll(
                playbackSpeedPrompt(), playbackSpeedField(),
                backToStartButton(), playPause(), playingStatus(),
                massPrompt(), massField(),
                clear(),
                clearTrails(), enableTrails(),
                trailLengthPrompt(), trailLengthField(),
                enableCollisions(),
                enableSplitting(),
                clippingTypePrompt(), collisionlessClippingType(),
                focusBodySetter(),
                dragSetter(),
                stepFrame(),
                fileOpener(),
                setBakeButton(),
                fpsCounter(),
                objectCounter());
    }

    private Text playbackSpeedPrompt() {
        Text prompt = new Text("Simulation Speed:");
        if (simulation.BACKGROUND_BLACK) prompt.setFill(Color.gray(0.7));
        prompt.setFont(Font.font(15));
        prompt.setTranslateX(20);
        prompt.setTranslateY(103.5);
        return prompt;
    }

    private SettableNumberField playbackSpeedField() {
        playbackSpeedField = new SettableNumberField(1);
        playbackSpeedField.setPrefWidth(110);
        playbackSpeedField.setTranslateX(145);
        playbackSpeedField.setTranslateY(86);
        playbackSpeedField.setProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                simulation.setPlaybackSpeed(playbackSpeedField.getValue());
            }
        });
        return playbackSpeedField;
    }

    private Button backToStartButton() {
        backButton = new Button();
        setBackIcon();
        backButton.setGraphic(backIcon);
        backButton.setTranslateX(260);
        backButton.setTranslateY(80);
        backButton.setOnAction(e -> {
            simulation.clear();
            for (StartConditions c : simulation.getStartConditions()) {
                simulation.createObject(c);
            }
            objectCounter.setText("Objects: " + simulation.getStartConditions().size());
            if (simulation.isPlaying()) playPauseButton.fire();
            simulation.setFrame(0);
        });
        return backButton;
    }

    private Button playPause() {
        playPauseButton = new Button();
        setPlayIcon();
        setPauseIcon();
        simulation.setPlaying(false);
        playPauseButton.setGraphic(playIcon);
        playPauseButton.setTranslateX(305);
        playPauseButton.setTranslateY(80);
        playPauseButton.setOnAction(e -> {
            if (!simulation.isPlaying()) {
                simulation.start();
                playPauseButton.setGraphic(pauseIcon);
                playingStatus.setText("Playing");
                playingStatus.setFill(Color.GREEN);
                bakeButton.setDisable(true);
                simulation.setPlaying(true);
            } else {
                simulation.stop();
                playPauseButton.setGraphic(playIcon);
                playingStatus.setText("Paused");
                playingStatus.setFill(Color.RED);
                bakeButton.setDisable(false);
                simulation.setPlaying(false);
            }
        });
        return playPauseButton;
    }

    private Text playingStatus() {
        playingStatus = new Text("Paused");
        playingStatus.setFill(Color.RED);
        playingStatus.setFont(Font.font(20));
        playingStatus.setTranslateX(355);
        playingStatus.setTranslateY(105);
        return playingStatus;
    }

    private Text massPrompt() {
        Text massPrompt = new Text("Mass:");
        if (simulation.BACKGROUND_BLACK) massPrompt.setFill(Color.gray(0.7));
        massPrompt.setTranslateX(20);
        massPrompt.setTranslateY(37);
        massPrompt.setFont(Font.font(15));
        return massPrompt;
    }

    private SettableNumberField massField() {
        massField = new SettableNumberField(10);
        massField.setTranslateX(65);
        massField.setTranslateY(20);
        massField.setPrefWidth(75);
        return massField;
    }

    private Button clear() {
        Button clear = new Button("Clear");
        clear.setTranslateX(150);
        clear.setTranslateY(20);
        clear.setPrefWidth(75);
        clear.setOnAction(e -> {
            simulation.clear();
        });
        return clear;
    }

    private Button clearTrails() {
        Button clearTrails = new Button("Clear Trails");
        clearTrails.setPrefWidth(100);
        clearTrails.setTranslateX(235);
        clearTrails.setTranslateY(20);
        clearTrails.setOnAction(e -> simulation.getTrails().getChildren().clear());
        return clearTrails;
    }

    private CheckBox enableTrails() {
        trailsEnabled = new CheckBox("Enable Trails");
        if (simulation.BACKGROUND_BLACK) trailsEnabled.setTextFill(Color.gray(0.7));
        trailsEnabled.setTranslateX(345);
        trailsEnabled.setTranslateY(23);
        return trailsEnabled;
    }

    private Text trailLengthPrompt() {
        Text trailLengthPrompt = new Text("Trail Length:");
        if (simulation.BACKGROUND_BLACK) trailLengthPrompt.setFill(Color.gray(0.7));
        trailLengthPrompt.setTranslateX(267);
        trailLengthPrompt.setTranslateY(67);
        trailLengthPrompt.setFont(Font.font(13));
        return trailLengthPrompt;
    }

    private SettableNumberField trailLengthField() {
        trailLengthField = new SettableNumberField(Simulation.DEFAULT_TRAIL_LENGTH);
        trailLengthField.setPrefWidth(100);
        trailLengthField.setTranslateX(345);
        trailLengthField.setTranslateY(49);
        trailLengthField.setProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                simulation.setTrailLength((int)trailLengthField.getValue());
            }
        });
        return trailLengthField;
    }

    private CheckBox enableCollisions() {
        collisionsEnabled = new CheckBox("Enable Collisions");
        if (simulation.BACKGROUND_BLACK) collisionsEnabled.setTextFill(Color.gray(0.7));
        collisionsEnabled.setSelected(true);
        collisionsEnabled.setTranslateX(460);
        collisionsEnabled.setTranslateY(23);
//        collisionsEnabled.selectedProperty().addListener(new ChangeListener<Boolean>() {
//            @Override
//            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
//                splittingEnabled.setDisable(!newValue);
//            }
//        });
        return collisionsEnabled;
    }

    private Text clippingTypePrompt() {
        Text clippingTypePrompt = new Text("Clipping Mode:");
        if (simulation.BACKGROUND_BLACK) clippingTypePrompt.setFill(Color.gray(0.7));
        clippingTypePrompt.setTranslateX(460);
        clippingTypePrompt.setTranslateY(63);
        return clippingTypePrompt;
    }

    private Button focusBodySetter() {
        focusBodySetter = new Button("Track Object");
        focusBodySetter.setPrefWidth(100);
        focusBodySetter.setTranslateX(460);
        focusBodySetter.setTranslateY(90);
        focusBodySetter.setOnAction(e -> simulation.setFocusBody(simulation.getSelectedBody()));
        return focusBodySetter;
    }

    private Button dragSetter() {
        dragSetter = new Button("Drag Object");
        dragSetter.setPrefWidth(100);
        dragSetter.setTranslateX(570);
        dragSetter.setTranslateY(90);
        dragSetter.setOnAction(e -> simulation.setDraggingBody(simulation.getSelectedBody()));
        return dragSetter;
    }

    private Button stepFrame() {
        stepFrameButton = new Button("Step Frame");
        stepFrameButton.setTranslateX(680);
        stepFrameButton.setTranslateY(90);
        stepFrameButton.setPrefWidth(100);
        stepFrameButton.setOnAction(e -> simulation.handle(0));
        return stepFrameButton;
    }

    private Button collisionlessClippingType() {
        clippingTypeSelector = new Button("Soft");
        clippingTypeSelector.disableProperty().bindBidirectional(collisionsEnabled.selectedProperty());
        clippingTypeSelector.setDisable(true);
        clippingTypeSelector.setOnAction(e -> {
            if (clippingTypeSelector.getText().equals("Soft")) {
                clippingTypeSelector.setText("Full");
            } else clippingTypeSelector.setText("Soft");
        });
        clippingTypeSelector.setTranslateX(565);
        clippingTypeSelector.setTranslateY(46);
        clippingTypeSelector.setPrefWidth(50);
        return clippingTypeSelector;
    }

    private CheckBox enableSplitting() {
        splittingEnabled = new CheckBox("Split on Collision");
        splittingEnabled.disableProperty().bind(collisionsEnabled.selectedProperty().not());
        splittingEnabled.setTranslateX(620);
        splittingEnabled.setTranslateY(23);
        return splittingEnabled;
    }

    private Button fileOpener() {
        fileOpenerButton = new Button("Open File");
        fileOpenerButton.setTranslateX(800);
        fileOpenerButton.setTranslateY(20);
        fileOpenerButton.setPrefWidth(100);
        fileOpenerButton.setOnAction(e -> {
            FileChooser chooser = new FileChooser();
            chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("N-Body Files", "*.nbd", "*.nbb"));
            chooser.setTitle("Open Simulation");
            File simulationFile = chooser.showOpenDialog(simulation.getStage());
            if (simulationFile != null) {
                Scanner scan;
                try {
                    scan = new Scanner(simulationFile);
                    simulation.clear();
                    simulation.getStartConditions().clear();

                    if (simulationFile.getName().substring(simulationFile.getName().length() - 4).equals(".nbd"))
                        while (scan.hasNextLine()) {
                            simulation.addStartInfo(
                                    simulation.createObject(
                                            scan.nextDouble(),
                                            scan.nextDouble(),
                                            scan.nextDouble(),
                                            scan.nextDouble(),
                                            scan.nextDouble(),
                                            scan.nextDouble(),
                                            InteractableBody.CollisionMode.FULL,
                                            false,
                                            scan.nextBoolean(),
                                            Color.valueOf(scan.next())
                                    )
                            );
                            scan.nextLine();
                        }
                    else if (simulationFile.getName().substring(simulationFile.getName().length() - 4).equals(".nbb"))
                        while (scan.hasNextLine()) {

                        }
                    if (simulation.isPlaying()) playPauseButton.fire();
                } catch (FileNotFoundException exception) {
                    exception.printStackTrace();
                }
            }
        });
        return fileOpenerButton;
    }

    public Button setBakeButton() {
        bakeButton = new Button("Bake");
        bakeButton.setPrefWidth(100);
        bakeButton.setTranslateX(800);
        bakeButton.setTranslateY(50);
        bakeButton.setOnMouseClicked(e -> {
            Stage bakeStage = new Stage();
            bakeStage.setTitle("Bake");
            bakeStage.setWidth(400);
            bakeStage.setHeight(200);
            bakeStage.setResizable(false);
            bakeStage.setScene(new Scene(new BakeUI(simulation, bakeStage)));
            bakeStage.show();
        });
        return bakeButton;
    }

    private void setBackIcon() {
        Image backImage = new Image(getClass().getClassLoader().getResourceAsStream("icons/back to start.png"));
        backIcon = new ImageView(backImage);
        backIcon.setFitWidth(25);
        backIcon.setFitHeight(30);
        backIcon.setMouseTransparent(true);
    }

    private void setPlayIcon() {
        Image playImage = new Image(getClass().getClassLoader().getResourceAsStream("icons/play.png"));
        playIcon = new ImageView(playImage);
        playIcon.setFitWidth(25);
        playIcon.setFitHeight(30);
        playIcon.setMouseTransparent(true);
    }

    private void setPauseIcon() {
        Image pauseImage = new Image(getClass().getClassLoader().getResourceAsStream("icons/pause.png"));
        pauseIcon = new ImageView(pauseImage);
        pauseIcon.setFitHeight(30);
        pauseIcon.setFitWidth(25);
        pauseIcon.setMouseTransparent(true);
    }

    private Text fpsCounter() {
        fpsCounter = new Text("fps: N/A");
        fpsCounter.setFont(Font.font(12));
        fpsCounter.setFill(Color.RED);
        fpsCounter.setTranslateX(2);
        fpsCounter.setTranslateY(10);
        return fpsCounter;
    }

    private Text objectCounter() {
        objectCounter = new Text("Objects: " + simulation.getInteractableObjects().size());
        objectCounter.setFont(Font.font(12));
        objectCounter.setTranslateX(20);
        objectCounter.setTranslateY(130);
        return objectCounter;
    }
}
