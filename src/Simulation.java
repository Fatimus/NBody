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
import javafx.scene.shape.Circle;
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
    private Line dragIndicatorLine;
    private InteractableBody focusBody = null;
    private InteractableBody selectedBody = null;
    private InteractableBody draggingBody = null;
    private long previousSecond = 0;
    private int second = 0;
    private int frames = 0;
    public int frame = 0;
    private double simulationDifferenceX, simulationDifferenceY;
    private double editorDifferenceX, editorDifferenceY;
    private double playbackSpeed = 1;
    public final double GRAVITATIONAL_CONSTANT = 2;
    public final double MATERIAL_BINDING_CONSTANT = 1;
    private boolean playing;
    private boolean dragging = false;
    private boolean baking = false;
    public final boolean BACKGROUND_BLACK = false;
    private NBBWriter nbbWriter = null;
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
        dragIndicatorLine = new Line();
        dragIndicatorLine.setVisible(false);
        simulationView.getChildren().addAll(vectorLine, dragIndicatorLine);
        myWindow.setOnMousePressed(e -> {
            myWindow.requestFocus();
            if (e.getButton() == MouseButton.PRIMARY && !e.isShiftDown() && !e.isControlDown()) {
                if (hasDraggingBody()) {
                    dragging = true;
                    dragIndicatorLine.setVisible(true);
                    dragIndicatorLine.setStartX((hasFocusBody() ? focusBody.getSimulationX() : 0) + draggingBody.getSimulationX() - (hasFocusBody() ? focusBody.getCenterX() : 0));
                    dragIndicatorLine.setStartY((hasFocusBody() ? focusBody.getSimulationY() : 0) + draggingBody.getSimulationY() - (hasFocusBody() ? focusBody.getCenterY() : 0));
                    dragIndicatorLine.setEndX((e.getX() - simulationView.getTranslateX()) / simulationView.getZoomScale().getX());
                    dragIndicatorLine.setEndY((e.getY() - simulationView.getTranslateY()) / simulationView.getZoomScale().getY());
                } else {
                    vectorLine.setVisible(true);
                    vectorLine.setStartX((e.getX() - simulationView.getTranslateX()) / simulationView.getZoomScale().getX());
                    vectorLine.setStartY((e.getY() - simulationView.getTranslateY()) / simulationView.getZoomScale().getY());
                    vectorLine.setEndX((e.getX() - simulationView.getTranslateX()) / simulationView.getZoomScale().getX());
                    vectorLine.setEndY((e.getY() - simulationView.getTranslateY()) / simulationView.getZoomScale().getY());
                }
            } else if (e.getButton() == MouseButton.PRIMARY && e.isControlDown() &&
                    !(e.getPickResult().getIntersectedNode() instanceof InteractableBody)) {
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
            if (e.getButton() == MouseButton.PRIMARY &&
                    !e.isShiftDown() &&
                    !e.isControlDown() &&
                    guiTabs.getSelectionModel().getSelectedItem().getContent().equals(simulationUI)) {
                if (hasDraggingBody()) {
                    dragIndicatorLine.setEndX((e.getX() - simulationView.getTranslateX()) / simulationView.getZoomScale().getX());
                    dragIndicatorLine.setEndY((e.getY() - simulationView.getTranslateY()) / simulationView.getZoomScale().getY());
                } else {
                    vectorLine.setEndX((e.getX() - simulationView.getTranslateX()) / simulationView.getZoomScale().getX());
                    vectorLine.setEndY((e.getY() - simulationView.getTranslateY()) / simulationView.getZoomScale().getY());
                }
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
            dragging = false;
            vectorLine.setVisible(false);
            dragIndicatorLine.setVisible(false);
            if (e.getButton() == MouseButton.PRIMARY && !e.isShiftDown() && !e.isControlDown() && !hasDraggingBody()) {
                InteractableBody b = new InteractableBody(
                        (hasFocusBody() ? focusBody.getSimulationX() : 0) + vectorLine.getStartX() - (hasFocusBody() ? focusBody.getCenterX() : 0),
                        (hasFocusBody() ? focusBody.getSimulationY() : 0) + vectorLine.getStartY() - (hasFocusBody() ? focusBody.getCenterY() : 0),
                        (vectorLine.getEndX() - vectorLine.getStartX()) / 50 + (hasFocusBody() ? focusBody.getVX() : 0),
                        (vectorLine.getEndY() - vectorLine.getStartY()) / 50 + (hasFocusBody() ? focusBody.getVY() : 0),
                        Double.parseDouble(simulationUI.massField.getText()),
                        this);
                addObject(b);
            }
        });
    }

    /**
     * DEMOS (FOR TESTING ONLY)
     */
    private void createDemo() {
        double centerX = 900;
        double centerY = 500;

        /**
         * EXPERIMENTATION SECTION
         */

//        double r = 9300;
//        double mP = 1000000000000D;
//        createObject(900, 500 + r, 0, 0, mP);
//        createObject(900, 500, Math.sqrt(GRAVITATIONAL_CONSTANT * mP / r), 0, 10);


//        for (int i = 0; i < 10; i++) {
//            for (int j = 0; j < 70; j++) {
//                double angle = Math.random() * 2 * Math.PI;
//                double radius = 9200 + 5 * i + Math.random() * 5;
//                double tx = centerX + radius * Math.cos(angle);
//                double ty = 500 + r + radius * Math.sin(angle);
//                addStartInfo(createObject(tx, ty, 0, 0, 0, InteractableBody.defaultRadius(0), true, Color.DARKBLUE));
//            }
//        }

        /**
         * RANDOMIZED RECTANGLE
         */
//        createObject(0, 582.5, 700, 0, 100);

//        double scale = 25;
//        for(int i = 0; i < 10; i++) {
//            for(int j = 0; j < 100; j++) {
//                InteractableBody boi = createObject(1000 + scale * j + Math.random() * scale, 450 + scale * i + Math.random() * scale, 0, 0, 10);
//            }
//        }

        /**
         * DEFAULT OBJECT WITH RING
         */
        InteractableBody center = createObject(centerX, centerY, 0, 0, 1000);
        for (int i = 0; i < 40; i++) {
            for (int j = 0; j < 40; j++) {
                double angle = Math.random() * 2 * Math.PI;
                double radius = 50 + 2 * i + Math.random() * 3;
                double tx = centerX + radius * Math.cos(angle);
                double ty = centerY + radius * Math.sin(angle);
                double speed = Math.sqrt(GRAVITATIONAL_CONSTANT * center.getMass() / radius);
                double vx = center.getVX() - speed * Math.sin(angle);
                double vy = center.getVY() + speed * Math.cos(angle);
                createObject(tx, ty, vx, vy, 0);
            }
        }
//        InteractableBody boi = createObject(centerX + 91,
//                centerY,
//                0,
//                Math.sqrt(GRAVITATIONAL_CONSTANT * center.getMass() / 91),
//                1,
//                InteractableBody.defaultRadius(1),
//                false,
//                Color.DARKGREEN);
//        addStartInfo(boi);
        /**
         * SATISFYING ZOOMS
         */
//        createObject(500, 500, 0, 0, 0);
//        double x = 500 + 1.3 * InteractableBody.defaultRadius(1000);
//        for(int i = 1; i < 15; i++) {
//            double mass = Math.pow(1000, i);
//            createObject(x, 500, 0, 0, mass, InteractableBody.defaultRadius(mass), true, Color.BLACK);
//            x += 1.2 * InteractableBody.defaultRadius(mass) + InteractableBody.defaultRadius(mass * 1000);
//        }

        /**
         * GALAXY BOI
         */
//        for (int i = 0; i < 37; i++) {
//            for (int j = 0; j < 37; j++) {
//                double angle = Math.random() * 2 * Math.PI;
//                double radius = i + Math.random();
//                double tx = centerX + radius * Math.cos(angle);
//                double ty = centerY + radius * Math.sin(angle);
//                createObject(tx, ty, 12, 0, 0.1);
//            }
//        }

        /**
         * SCATTERING0
         */
//        for(int i = 0; i < 1000; i++) {
//            createObject(500, 450 + 0.4 * i, 20, 0, 0);
//        }
//
//        passCreatedObject(1400, 500, 0, 0, -1000, InteractableBody.defaultRadius(-1000), true, Color.BLUE);
//        passCreatedObject(1400, 900, 0, 0, -1000, InteractableBody.defaultRadius(-1000), true, Color.BLUE);
//        passCreatedObject(1500, 700, 0, 0, -1000, InteractableBody.defaultRadius(-1000), true, Color.BLUE);
//        passCreatedObject(1200, 750, 0, 0, -1000, InteractableBody.defaultRadius(-1000), true, Color.BLUE);

        /**
         * N-GON
         */

//        double centerX = 900;
//        double centerY = 500;
//        InteractableBody center = createObject(centerX, centerY, 0, 0, 1000);
//        double n = 6;
//        double rFactor = 50 * Math.sqrt(3);
//        for (int i = 0; i < 2 * n; i += 2) {
//            int objectsPerHalfSide = 100;
//            for (int j = 0; j < objectsPerHalfSide; j++) {
//                double baseAngle = i * Math.PI / n;
//                double additionalAngle = j * Math.PI / n / objectsPerHalfSide;
//                double radius = rFactor / Math.cos(Math.PI / n - additionalAngle);
//                double tx = centerX + radius * Math.cos(baseAngle + additionalAngle);
//                double ty = centerY + radius * Math.sin(baseAngle + additionalAngle);
//                double speed = Math.sqrt(GRAVITATIONAL_CONSTANT * center.getMass() / radius) * 0.7;
//                double vx = center.getVX() - speed * Math.sin(baseAngle + additionalAngle);
//                double vy = center.getVY() + speed * Math.cos(baseAngle + additionalAngle);
//                createObject(tx, ty, vx, vy, 0);
//            }
//            for (int j = 0; j < objectsPerHalfSide; j++) {
//                double baseAngle = (i + 1) * Math.PI / n;
//                double additionalAngle = j * Math.PI / n / objectsPerHalfSide;
//                double radius = rFactor / Math.cos(additionalAngle);
//                double tx = centerX + radius * Math.cos(baseAngle + additionalAngle);
//                double ty = centerY + radius * Math.sin(baseAngle + additionalAngle);
//                double speed = Math.sqrt(GRAVITATIONAL_CONSTANT * center.getMass() / radius) * 0.7;
//                double vx = center.getVX() - speed * Math.sin(baseAngle + additionalAngle);
//                double vy = center.getVY() + speed * Math.cos(baseAngle + additionalAngle);
//                createObject(tx, ty, vx, vy, 0);
//            }
//        }

        /**
         * RAYS
         */
//        InteractableBody center = createObject(centerX, centerY, 0, 0, 1000);
//        int rays = 7;
//        for (int i = 0; i < 75; i++) {
//            for (int j = 0; j < rays; j++) {
//                double angle = j * 2 * Math.PI / rays;
//                double radius = 100 + 2 * i;
//                double tx = centerX + radius * Math.cos(angle);
//                double ty = centerY + radius * Math.sin(angle);
//                double speed = Math.sqrt(GRAVITATIONAL_CONSTANT * center.getMass() / radius);
//                double vx = center.getVX() - speed * Math.sin(angle);
//                double vy = center.getVY() + speed * Math.cos(angle);
//                createObject(tx, ty, vx, vy, 0);
//            }
//        }

//        double centerX = 400;
//        double centerY = 900;
//        InteractableBody center = createObject(centerX, centerY, 0, 0, 1000);
//        center.setFill(Color.DARKRED);
//        for (int i = 0; i < 60; i++) {
//            double angle = i * 2 * Math.PI / 60;
//            double radius = 600;
//            double tx = centerX + radius * Math.cos(angle);
//            double ty = centerY + radius * Math.sin(angle);
//            double speed = Math.sqrt(GRAVITATIONAL_CONSTANT * center.getMass() / radius) * 0.7;
//            double vx = center.getVX() - speed * Math.sin(angle);
//            double vy = center.getVY() + speed * Math.cos(angle);
//            createObject(tx, ty, vx, vy, 0);
//        }
//        NBDWriter writer = new NBDWriter();
//
//
//
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
        boolean hasDraggingBody = false;
        double totalMass = 0;
        InteractableBody largestObject = objects.get(0);
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
        InteractableBody mergedBody = null;
        if(!isSplittingEnabled()) mergedBody = createObjectIgnoreRate(
                averagePX,
                averagePY,
                averageVX,
                averageVY,
                totalMass,
                InteractableBody.defaultRadius(totalMass),
                largestObject instanceof StationaryInteractableBody,
                (Color) largestObject.getFill());
        else {
            /**
             * Find the total kinetic energy of the colliding system. If it exceeds the gravitational binding energy of the would-be merged object,
             * split the merged object into some random number of objects between N * 8 and N * 12, where N is the number of colliding objects.
             * Apply a random exit speed to the debris with an average proportional to the square root of kinetic energy, and apply randomness.
             */
            double totalKineticEnergy = 0;
            for(InteractableBody b : objects) {
                double relativeVelocity = Math.sqrt((b.getVX() - averageVX) * (b.getVX() - averageVX) + (b.getVY() - averageVY) * (b.getVY() - averageVY));
                totalKineticEnergy += 0.5 * b.getMass() * relativeVelocity * relativeVelocity;
            }
            double gravitationalBindingEnergy = 3.0 * GRAVITATIONAL_CONSTANT * playbackSpeed * playbackSpeed * totalMass * totalMass / 5.0 / InteractableBody.defaultRadius(totalMass);
            double materialBindingEnergy = MATERIAL_BINDING_CONSTANT / totalMass;
            if(totalKineticEnergy > gravitationalBindingEnergy + materialBindingEnergy) {
                int debrisCount = (int)(Math.random() * 5) + 10;
                double individualMass = totalMass / debrisCount;
                double averageExitKineticEnergy = totalKineticEnergy / debrisCount;
                for(int i = 0; i < debrisCount; i++) {
                    double exitKineticEnergy = 2 * Math.random() * averageExitKineticEnergy;
                    double exitSpeed = Math.sqrt(2 * exitKineticEnergy / individualMass) / playbackSpeed;
                    double exitAngle = Math.random() * 2 * Math.PI;
                    InteractableBody debris = createObject(
                            averagePX,
                            averagePY,
                            averageVX / playbackSpeed + exitSpeed * Math.cos(exitAngle),
                            averageVY / playbackSpeed + exitSpeed * Math.sin(exitAngle),
                            individualMass,
                            InteractableBody.defaultRadius(individualMass),
                            false,
                            (Color)largestObject.getFill());
                    debris.setCollisionGracePeriod((int)Math.ceil(InteractableBody.defaultRadius(individualMass) / exitSpeed * playbackSpeed * playbackSpeed));
                }
            } else mergedBody = createObjectIgnoreRate(
                    averagePX,
                    averagePY,
                    averageVX,
                    averageVY,
                    totalMass,
                    InteractableBody.defaultRadius(totalMass),
                    largestObject instanceof StationaryInteractableBody,
                    (Color) largestObject.getFill());
        }
        if (hasSelectedBody) setSelectedBody(mergedBody);
        if (hasFocusBody) setFocusBody(mergedBody);
        if (largestObject.equals(draggingBody)) setDraggingBody(mergedBody);
        else if (hasDraggingBody) {
            setDraggingBody(null);
            dragging = false;
        }
    }

    private ArrayList<MutableDouble> bounds = new ArrayList<>();

    public void handle(long now) {
        frame++;
        double totalMass = 0;
        if (isCollisionEnabled()) {
            /**
             * Sweep and Prune
             */
            Collections.sort(bounds);
            ArrayList<CollisionGroup> collisionGroups = new ArrayList<>();
            ArrayList<InteractableBody> activeObjects = new ArrayList<>();
            for (MutableDouble bound : bounds) {
                if (bound instanceof LeftBound) {
                    for (InteractableBody b : activeObjects)
                        if (b.getMass() != 0)
                            b.getPotentialIntersections().add(((LeftBound) bound).getOwner());
                    if (((LeftBound) bound).getOwner().getMass() != 0)
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
        selector.updateSelectorSize();
        focusIndicator.updateSelectorSize();
        //update drag indicator
        if (hasDraggingBody() && dragging) {
            dragIndicatorLine.setStartX(draggingBody.getCenterX());
            dragIndicatorLine.setStartY(draggingBody.getCenterY());
            double lengthX = dragIndicatorLine.getEndX() - dragIndicatorLine.getStartX();
            double lengthY = dragIndicatorLine.getEndY() - dragIndicatorLine.getStartY();
            draggingBody.accelerateXBy(lengthX / 200 + ((hasFocusBody() ? focusBody.getVX() : 0) - draggingBody.getVX()) * 0.05);
            draggingBody.accelerateYBy(lengthY / 200 + ((hasFocusBody() ? focusBody.getVY() : 0) - draggingBody.getVY()) * 0.05);
//            draggingBody.accelerateXBy(lengthX / 200);
//            draggingBody.accelerateYBy(lengthY / 200);

        }


        if (now - previousSecond >= 1E9) {
            second++;
            previousSecond = now;
            simulationUI.fpsCounter.setVisible(true);
            simulationUI.fpsCounter.setText("fps: " + frames);
            frames = 0;
        }
        simulationUI.objectCounter.setText("Objects: " + interactableObjects.size());
        frames++;

        if(baking) {
            nbbWriter.bakeFrame(frame, interactableObjects);
            if(frame == nbbWriter.getLengthFrames()) stopBake();
        }

        /**
         * EXPERIMENTATION
         */
    }

    public void startBake(NBBWriter writer) {
        nbbWriter = writer;
        simulationUI.playPauseButton.fire();
        simulationUI.playPauseButton.setDisable(true);
        setFrame(0);
        baking = true;
    }

    public void stopBake() {
        baking = false;
        simulationUI.playPauseButton.setDisable(false);
        simulationUI.playPauseButton.fire();
        nbbWriter = null;
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
        setDraggingBody(null);
        bounds.clear();
        simulationView.setTranslateX(0);
        simulationView.setTranslateY(0);
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

    public void setFrame(int frame) {
        this.frame = frame;
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

    public InteractableBody getDraggingBody() {
        return draggingBody;
    }

    public void setDraggingBody(InteractableBody b) {
        draggingBody = b;
    }

    public boolean hasDraggingBody() {
        return draggingBody != null;
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

    public boolean isSplittingEnabled() {
        return simulationUI.splittingEnabled.isSelected();
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
