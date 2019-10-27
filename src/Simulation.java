import javafx.animation.AnimationTimer;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.transform.Scale;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.Collections;

public class Simulation extends AnimationTimer {

    private Stage stage;
    private Pane myWindow;
    private Pane trails;
    private TabPane guiTabs;
    private CrosshairSelector selector;
    private CrosshairFocusIndicator focusIndicator;
    private Pane visibleObjects;
    private ZoomPane simulationView;
    private BuildState editorView;
    private SimulationUI simulationUI;
    private EditorUI editorUI;
    private Line vectorLine;
    private InteractableBody focusBody = null;
    private InteractableBody selectedBody = null;
    private long previousSecond = 0;
    private int second = 0;
    private int frames = 0;
    public long frame = 0;
    private double simulationDifferenceX, simulationDifferenceY;
    private double editorDifferenceX, editorDifferenceY;
    private double playbackSpeed = 1;
    public final double GRAVITATIONAL_CONSTANT = 2;
    private boolean playing;
    public final boolean BACKGROUND_BLACK = false;
    private ArrayList<InteractableBody> interactableObjects;
    private ArrayList<StartConditions> startConditions;


    public Parent setUpWindow(Stage s) {
        stage = s;
        myWindow = new Pane();
        interactableObjects = new ArrayList<>();
        startConditions = new ArrayList<>();
        simulationUI = new SimulationUI(this);
        editorUI = new EditorUI(this);
        myWindow.getChildren().addAll(
                simulationView(),
                editorView(),
                guiTabs());
        createLocationPicker();
        myWindow.setOnScroll(e -> {
            if (guiTabs.getSelectionModel().getSelectedItem().getContent().equals(simulationUI)) {
                if (e.getDeltaY() > 0) simulationView.zoomIn(e.getX(), e.getY());
                else simulationView.zoomOut(e.getX(), e.getY());
                selector.updateSelectorSize();
                focusIndicator.updateSelectorSize();
            }
            if (guiTabs.getSelectionModel().getSelectedItem().getContent().equals(editorUI)) {
                if (e.getDeltaY() > 0) editorView.zoomIn(e.getX(), e.getY());
                else editorView.zoomOut(e.getX(), e.getY());
            }
        });
        myWindow.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ESCAPE || e.getCode() == KeyCode.ENTER) {
                myWindow.requestFocus();
            }
            if ((e.getCode() == KeyCode.SPACE || e.getCode() == KeyCode.K) && myWindow.isFocused()) {
                simulationUI.playPauseButton.arm();
            }
        });
        myWindow.setOnKeyReleased(e -> {
            if ((e.getCode() == KeyCode.SPACE || e.getCode() == KeyCode.K) && myWindow.isFocused()) {
                simulationUI.playPauseButton.fire();
                simulationUI.playPauseButton.disarm();
            }
        });
        if (BACKGROUND_BLACK)
            myWindow.setBackground(new Background(new BackgroundFill(Color.BLACK, CornerRadii.EMPTY, Insets.EMPTY)));
        return myWindow;
    }

    private ZoomPane simulationView() {
        simulationView = new ZoomPane();
        selector = new CrosshairSelector(this);
        focusIndicator = new CrosshairFocusIndicator(this);
        simulationView.getChildren().addAll(
                trails(),
                visibleObjects(),
                selector,
                focusIndicator);
        return simulationView;
    }

    /**
     * EXPERIMENTAL FEATURE
     */

    private BuildState editorView() {
        editorView = new BuildState(this);
        Body centerObject = new Body(0, 100, 0, 0, null, null, editorView, 1000);
        Body orbitingObject = new Body(300, 100, 0, 0, null, null, editorView, 10);
        RingRegion ring = new RingRegion(orbitingObject, null, editorView, RingRegion.OrbitDirection.CLOCKWISE);
        ring.setRings(5);
        ring.setObjectsPerRing(10);
        ring.setInnerRadius(10);
        ring.setOuterRadius(15);
        ring.setAngleRandomizationRange(Math.PI);
        ring.setRadialRandomizationRange(2);
        ring.generate();

        RectangularRegion rect = new RectangularRegion(125, 200, null, editorView);
        rect.setColumns(5);
        rect.setRows(10);
        rect.setHeight(30);
        rect.setWidth(30);
        rect.generate();

        BodyGroup subGroup = new BodyGroup(110, 500, null, editorView);
        Body obj1 = new Body(100, 500, 0, 0, null, null, editorView, 10);
        Body obj2 = new Body(120, 500, 0, 0, null, null, editorView, 10);
        subGroup.addAll(obj1, obj2);
        RingRegion subRing = new RingRegion(subGroup, null, editorView, RingRegion.OrbitDirection.COUNTERCLOCKWISE);
        subRing.setRings(10);
        subRing.setObjectsPerRing(20);
        subRing.setInnerRadius(60);
        subRing.setOuterRadius(80);
        subRing.setAngleRandomizationRange(Math.PI);
        subRing.setRadialRandomizationRange(30);
        subRing.generate();
        BodyGroup testGroup = new BodyGroup(125, 100, null, editorView);
        testGroup.addAll(centerObject, orbitingObject, ring, rect, subGroup, subRing);
        editorView.addUnit(testGroup);
//        RingRegion ring = new RingRegion(centerObject, null, editorView, RingRegion.OrbitDirection.CLOCKWISE);
//        ring.setAngleRandomizationRange(Math.PI);
//        ring.generate();
//        editorView.addUnit(centerObject);
//        editorView.addUnit(ring);
        editorView.setVisible(false);
        return editorView;
    }

    private Pane visibleObjects() {
        visibleObjects = new Pane();
        createDemo();
        return visibleObjects;
    }

    private Pane trails() {
        trails = new Pane();
        return trails;
    }

    private TabPane guiTabs() {
        guiTabs = new TabPane();
        Tab simulationTab = new Tab("Simulate");
        simulationTab.setContent(simulationUI);
        Tab editorTab = new Tab("Build");
        editorTab.setContent(editorUI);
        guiTabs.getTabs().addAll(
                simulationTab
//                ,editorTab
        );
        simulationTab.setOnSelectionChanged(e -> {
            simulationView.setVisible(true);
            editorView.setVisible(false);
        });
        editorTab.setOnSelectionChanged(e -> {
            simulationUI.backButton.fire();
            simulationView.setVisible(false);
            editorView.setVisible(true);
        });
        guiTabs.setTabMinWidth(75);
        guiTabs.setPrefHeight(175);
        guiTabs.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        guiTabs.setBackground(new Background(new BackgroundFill(Color.gray(0.9, 0.7), CornerRadii.EMPTY, Insets.EMPTY)));
        guiTabs.minWidthProperty().bind(stage.widthProperty());
        return guiTabs;
    }

    private void createLocationPicker() {
        vectorLine = new Line();
        vectorLine.setVisible(false);
        simulationView.getChildren().add(vectorLine);
        myWindow.setOnMousePressed(e -> {
            myWindow.requestFocus();
            if (e.getButton() == MouseButton.PRIMARY && !e.isShiftDown() && !e.isControlDown()) {
                vectorLine.setVisible(true);
                vectorLine.setStartX((e.getX() - simulationView.getTranslateX()) / simulationView.getZoomScale().getX());
                vectorLine.setStartY((e.getY() - simulationView.getTranslateY()) / simulationView.getZoomScale().getY());
                vectorLine.setEndX((e.getX() - simulationView.getTranslateX()) / simulationView.getZoomScale().getX());
                vectorLine.setEndY((e.getY() - simulationView.getTranslateY()) / simulationView.getZoomScale().getY());
            } else if (e.getButton() == MouseButton.PRIMARY && e.isControlDown() && !(e.getPickResult().getIntersectedNode() instanceof InteractableBody)) {
                setSelectedBody(null);
                selector.setVisible(false);
            } else if (e.getButton() == MouseButton.SECONDARY) {
                simulationDifferenceX = e.getX() - simulationView.getTranslateX();
                simulationDifferenceY = e.getY() - simulationView.getTranslateY();
                editorDifferenceX = e.getX() - editorView.getTranslateX();
                editorDifferenceY = e.getY() - editorView.getTranslateY();
            }
        });
        myWindow.setOnMouseDragged(e -> {
            if (e.getButton() == MouseButton.PRIMARY && !e.isShiftDown() && !e.isControlDown() && guiTabs.getSelectionModel().getSelectedItem().getContent().equals(simulationUI)) {
                vectorLine.setEndX((e.getX() - simulationView.getTranslateX()) / simulationView.getZoomScale().getX());
                vectorLine.setEndY((e.getY() - simulationView.getTranslateY()) / simulationView.getZoomScale().getY());
            } else if (e.getButton() == MouseButton.SECONDARY) {
                if (guiTabs.getSelectionModel().getSelectedItem().getContent().equals(simulationUI)) {
                    simulationView.setTranslateX(e.getX() - simulationDifferenceX);
                    simulationView.setTranslateY(e.getY() - simulationDifferenceY);
                } else {
                    editorView.setTranslateX(e.getX() - editorDifferenceX);
                    editorView.setTranslateY(e.getY() - editorDifferenceY);
                }
            }
        });
        myWindow.setOnMouseReleased(e -> {
            vectorLine.setVisible(false);
            if (e.getButton() == MouseButton.PRIMARY && !e.isShiftDown() && !e.isControlDown()) {
                InteractableBody b = new InteractableBody((hasFocusBody() ? focusBody.getSimulationX() : 0) + vectorLine.getStartX() - (hasFocusBody() ? focusBody.getCenterX() : 0), (hasFocusBody() ? focusBody.getSimulationY() : 0) + vectorLine.getStartY() - (hasFocusBody() ? focusBody.getCenterY() : 0), (vectorLine.getEndX() - vectorLine.getStartX()) / 50 + (hasFocusBody() ? focusBody.getVX() : 0), (vectorLine.getEndY() - vectorLine.getStartY()) / 50 + (hasFocusBody() ? focusBody.getVY() : 0), Double.parseDouble(simulationUI.massField.getText()),
                        this);
                addObject(b);
            }
        });
    }

    /**
     * DEMOS
     */
    private void createDemo() {
        /**
         * EXPERIMENTATION SECTION
         */
        int sideLength = 35;
        double centerX = 0;
        double centerY = 300;
        InteractableBody center = passCreatedObject(0, 0, 0, 0, 6000, InteractableBody.defaultRadius(1000) / 10, false, Color.RED);
        for (int i = 0; i < sideLength; i++) {
            for (int j = 0; j < sideLength; j++) {
                double radius = 0.2 * i + Math.random() * 0.5;
                double angle = Math.random() * 2 * Math.PI;
                double tx = Math.cos(angle) * radius + centerX;
                double ty = Math.sin(angle) * radius + centerY;
                double speed = Math.sqrt(GRAVITATIONAL_CONSTANT * i * j * 0.5 / radius / radius);
                double vx = speed * (ty - centerY) / radius + 3;
                double vy = -speed * (tx - centerX) / radius;
                createObject(tx, ty, vx, vy, 0.1);
            }
        }

//        NBDWriter writer = new NBDWriter();
//        writer.write(interactableObjects);
    }

    public InteractableBody createObject(StartConditions c) {
        return createObject(c.getX(), c.getY(), c.getVX(), c.getVY(), c.getMass(), c.getInteractableRadius(), c.isStationary(), c.getColor());
    }

    public InteractableBody createObject(double x, double y, double vx, double vy, double mass) {
        InteractableBody b = passCreatedObject(x, y, vx, vy, mass, InteractableBody.defaultRadius(mass), false, Color.BLACK);
        return b;
    }

    private InteractableBody passCreatedObject(double x, double y, double vx, double vy, double mass, double interactableRadius, boolean isStationary, Color color) {
        InteractableBody b = createObject(x, y, vx, vy, mass, interactableRadius, isStationary, color);
        addStartInfo(b);
        return b;
    }

    public InteractableBody createObjectIgnoreRate(double x, double y, double vx, double vy, double mass, double interactableRadius, boolean isStationary, Color color) {
        return createObject(x, y, vx / playbackSpeed, vy / playbackSpeed, mass, interactableRadius, isStationary, color);
    }

    public InteractableBody createObject(double x, double y, double vx, double vy, double mass, double interactableRadius, boolean isStationary, Color color) {
        InteractableBody b;
        if (isStationary) {
            b = new StationaryInteractableBody(mass, x, y, this);
            b.setFill(color);
        } else {
            b = new InteractableBody(x, y, vx * playbackSpeed, vy * playbackSpeed, mass, this);
            b.setFill(color);
        }
        b.setInteractableRadius(interactableRadius);
        addObject(b);
        return b;
    }

    public void collide(ArrayList<InteractableBody> objects) {
        interactableObjects.removeAll(objects);
        visibleObjects.getChildren().removeAll(objects);
        boolean hasSelectedBody = false;
        boolean hasFocusBody = false;
        double totalMass = 0;
        InteractableBody largestObject = objects.get(0);
//        for (InteractableBody b : objects) {
//            totalMass += b.getMass();
//            if (b.getMass() > largestObject.getMass()) largestObject = b;
//        }
        double averageVX = 0;
        double averageVY = 0;
        double averagePX = 0;
        double averagePY = 0;
        for (InteractableBody b : objects) {
            totalMass += b.getMass();
            if (b.getMass() > largestObject.getMass()) largestObject = b;
            bounds.remove(b.getLeftBound());
            bounds.remove(b.getRightBound());

            averageVX += b.getVX() * b.getMass();
            averageVY += b.getVY() * b.getMass();
            averagePX += b.getSimulationX() * b.getMass();
            averagePY += b.getSimulationY() * b.getMass();
            if (b.equals(selectedBody)) hasSelectedBody = true;
            if (b.equals(focusBody)) hasFocusBody = true;
        }
        averageVX /= totalMass;
        averageVY /= totalMass;
        averagePX /= totalMass;
        averagePY /= totalMass;
        InteractableBody b = createObjectIgnoreRate(
                averagePX,
                averagePY,
                averageVX,
                averageVY,
                totalMass,
                InteractableBody.defaultRadius(totalMass),
                largestObject instanceof StationaryInteractableBody,
                (Color) largestObject.getFill());
        if (hasSelectedBody) setSelectedBody(b);
        if (hasFocusBody) setFocusBody(b);
    }

    private ArrayList<CollisionGroup> collisionGroups = new ArrayList<>();
    private ArrayList<MutableDouble> bounds = new ArrayList<>();

    public void handle(long now) {
        frame++;
        double totalMass = 0;
        if (isCollisionEnabled()) {
            /**
             * Sweep and Prune
             */
            Collections.sort(bounds);
            ArrayList<InteractableBody> activeObjects = new ArrayList<>();
            for (MutableDouble bound : bounds) {
                if (bound instanceof LeftBound) {
                    for (InteractableBody b : activeObjects)
                        b.getPotentialIntersections().add(((LeftBound) bound).getOwner());
                    ((LeftBound) bound).getOwner().getPotentialIntersections().addAll(activeObjects);
                    activeObjects.add(((LeftBound) bound).getOwner());
                } else {
                    activeObjects.remove(((RightBound) bound).getOwner());
                    CollisionGroup collisionGroup = ((RightBound) bound).getOwner().detectCollisions();
                    if (collisionGroup != null) collisionGroups.add(collisionGroup);
                }
            }

            for (CollisionGroup group : collisionGroups) {
                collide(group.getCollisionObjects());
            }
            activeObjects.clear();
            /**
             * Brute force
             */
//            for (InteractableBody b : interactableObjects) {
//                if (!b.isMarkedForCollision()) {
//                    CollisionGroup collisionGroup = b.detectCollisions();
//                    if (collisionGroup != null) {
//                        collisionGroups.add(collisionGroup);
//                    }
//                }
//            }
//            for (CollisionGroup group : collisionGroups) {
//                collide(group.getCollisionObjects());
//            }
            collisionGroups.clear();
        }
        interactableObjects.parallelStream()
                .forEach(b -> {
                    if (!b.isFirstUpdate()) {
                        b.setPreviousX(b.getCenterX());
                        b.setPreviousY(b.getCenterY());
                    }
                    b.updateLocation(0.5);
                });
        interactableObjects.parallelStream()
                .forEach(b -> b.updateVelocity());
        interactableObjects.parallelStream()
                .forEach(b -> b.updateLocation(0.5));
        for (InteractableBody b : interactableObjects) {
            b.updateVisualLocation();
            totalMass += b.getMass();
        }
//        collisionObjects.clear();
        selector.updateSelectorSize();
        focusIndicator.updateSelectorSize();
        if (now - previousSecond >= 1000000000) {
            second++;
            previousSecond = now;
            simulationUI.fpsCounter.setVisible(true);
            simulationUI.fpsCounter.setText("fps: " + frames);
            frames = 0;
        }
        simulationUI.objectCounter.setText("Objects: " + interactableObjects.size());
        frames++;

        /**
         * EXPERIMENTATION
         */
    }

    private void addObject(InteractableBody b) {
        interactableObjects.add(b);
        visibleObjects.getChildren().add(b);
        bounds.add(b.getLeftBound());
        bounds.add(b.getRightBound());
    }

    private void addObjects(InteractableBody... objects) {
        for (InteractableBody b : objects) {
            addObject(b);
        }
    }

    public void clear() {
        interactableObjects.clear();
        visibleObjects.getChildren().clear();
        trails.getChildren().clear();
        setSelectedBody(null);
        setFocusBody(null);
        bounds.clear();
    }

    public void addStartInfo(InteractableBody b) {
        startConditions.add(new StartConditions(b));
    }

    public void addStartInfo(InteractableBody... objects) {
        for (InteractableBody b : objects) {
            addStartInfo(b);
        }
    }

    public void setPlaybackSpeed(double speed) {
        stop();
        for (InteractableBody b : interactableObjects) {
            b.scaleVelocity(speed / playbackSpeed);
        }
        playbackSpeed = speed;
        if (playing) start();
    }

    public double getPlaybackSpeed() {
        return playbackSpeed;
    }

    public ArrayList<InteractableBody> getInteractableObjects() {
        return interactableObjects;
    }

    public ArrayList<MutableDouble> getBounds() {
        return bounds;
    }

    public ArrayList<StartConditions> getStartConditions() {
        return startConditions;
    }

    public Pane getVisibleObjects() {
        return visibleObjects;
    }

    public Pane getTrails() {
        return trails;
    }

    public InteractableBody getFocusBody() {
        return focusBody;
    }

    public void setFocusBody(InteractableBody focusBody) {
        trails.getChildren().clear();
        if (this.focusBody != null && this.focusBody.equals(focusBody)) return;
        this.focusBody = focusBody;
        if (focusBody == null) {
            focusIndicator.setVisible(false);
            for (InteractableBody b : interactableObjects) {
                if (!b.equals(focusBody)) {
                    b.setSimulationX(b.getCenterX());
                    b.setSimulationY(b.getCenterY());
                }
            }
        } else {
            focusIndicator.setVisible(true);
        }
        focusIndicator.updateSelectorSize();
    }

    public boolean hasFocusBody() {
        return focusBody != null;
    }

    public InteractableBody getSelectedBody() {
        return selectedBody;
    }

    public void setSelectedBody(InteractableBody b) {
        selectedBody = b;
        if (b == null) {
            selector.setVisible(false);
        } else {
            selector.setVisible(true);
        }
        selector.updateSelectorSize();
    }

    public void setPlaying(boolean playing) {
        this.playing = playing;
    }

    public boolean hasSelectedBody() {
        return selectedBody != null;
    }

    public Scale getSimulationScale() {
        return simulationView.getZoomScale();
    }

    public String getClippingType() {
        return simulationUI.clippingTypeSelector.getText();
    }

    public boolean isCollisionEnabled() {
        return simulationUI.collisionsEnabled.isSelected();
    }

    public boolean trailsEnabled() {
        return simulationUI.trailsEnabled.isSelected();
    }

    public boolean isPlaying() {
        return playing;
    }

    public Pane getWindow() {
        return myWindow;
    }

    public Stage getStage() {
        return stage;
    }

    public CrosshairFocusIndicator getFocusIndicator() {
        return focusIndicator;
    }

    public CrosshairSelector getSelector() {
        return selector;
    }

    private long closestMultiple(double a, int factor) {
        if (factor == 0) return Math.round(a);
        return a % factor > (double) factor / 2 ? (long) a / factor * factor + factor : (long) a / factor * factor;
    }
}
