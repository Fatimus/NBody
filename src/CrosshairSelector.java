import javafx.scene.Group;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class CrosshairSelector extends Group {

    private Simulation simulation;
    private ImageView topLeft;
    private ImageView topRight;
    private ImageView bottomLeft;
    private ImageView bottomRight;
    private final double SELECTOR_RETICLE_WIDTH = 5;
    private final double SELECTOR_RETICLE_HEIGHT;
    private final double ORIGINAL_SEPARATION = 5;

    public CrosshairSelector(Simulation s) {
        simulation = s;
        Image selectorComponentImage = new Image(getClass().getClassLoader().getResourceAsStream("icons/selector corner.png"));
        SELECTOR_RETICLE_HEIGHT = SELECTOR_RETICLE_WIDTH * selectorComponentImage.getHeight() / selectorComponentImage.getWidth();
        topLeft = new ImageView(selectorComponentImage);
        topLeft.setRotate(135);
        topRight = new ImageView(selectorComponentImage);
        topRight.setRotate(-135);
        bottomLeft = new ImageView(selectorComponentImage);
        bottomLeft.setRotate(45);
        bottomRight = new ImageView(selectorComponentImage);
        bottomRight.setRotate(-45);
        topLeft.setFitWidth(SELECTOR_RETICLE_WIDTH);
        topLeft.setFitHeight(SELECTOR_RETICLE_HEIGHT);
        topRight.setFitWidth(SELECTOR_RETICLE_WIDTH);
        topRight.setFitHeight(SELECTOR_RETICLE_HEIGHT);
        bottomLeft.setFitWidth(SELECTOR_RETICLE_WIDTH);
        bottomLeft.setFitHeight(SELECTOR_RETICLE_HEIGHT);
        bottomRight.setFitWidth(SELECTOR_RETICLE_WIDTH);
        bottomRight.setFitHeight(SELECTOR_RETICLE_HEIGHT);
        getChildren().addAll(topLeft, topRight, bottomLeft, bottomRight);
        setVisible(false);
    }

    public void updateSelectorSize() {
        if (!simulation.hasSelectedBody()) return;
        double resizeFactor = simulation.getSimulationScale().getX();
        double rootTwo = Math.sqrt(2);
        topLeft.setTranslateX(-ORIGINAL_SEPARATION / resizeFactor - simulation.getSelectedBody().getRadius() / rootTwo - SELECTOR_RETICLE_WIDTH / (2 * resizeFactor));
        topLeft.setTranslateY(-ORIGINAL_SEPARATION / resizeFactor - simulation.getSelectedBody().getRadius() / rootTwo - SELECTOR_RETICLE_HEIGHT / (2 * resizeFactor));
        topRight.setTranslateX(ORIGINAL_SEPARATION / resizeFactor + simulation.getSelectedBody().getRadius() / rootTwo - SELECTOR_RETICLE_WIDTH / (2 * resizeFactor));
        topRight.setTranslateY(-ORIGINAL_SEPARATION / resizeFactor - simulation.getSelectedBody().getRadius() / rootTwo - SELECTOR_RETICLE_HEIGHT / (2 * resizeFactor));
        bottomLeft.setTranslateX(-ORIGINAL_SEPARATION / resizeFactor - simulation.getSelectedBody().getRadius() / rootTwo - SELECTOR_RETICLE_WIDTH / (2 * resizeFactor));
        bottomLeft.setTranslateY(ORIGINAL_SEPARATION / resizeFactor + simulation.getSelectedBody().getRadius() / rootTwo - SELECTOR_RETICLE_HEIGHT / (2 * resizeFactor));
        bottomRight.setTranslateX(ORIGINAL_SEPARATION / resizeFactor + simulation.getSelectedBody().getRadius() / rootTwo - SELECTOR_RETICLE_WIDTH / (2 * resizeFactor));
        bottomRight.setTranslateY(ORIGINAL_SEPARATION / resizeFactor + simulation.getSelectedBody().getRadius() / rootTwo - SELECTOR_RETICLE_HEIGHT / (2 * resizeFactor));
        topLeft.setFitWidth(SELECTOR_RETICLE_WIDTH / resizeFactor);
        topLeft.setFitHeight(SELECTOR_RETICLE_HEIGHT / resizeFactor);
        topRight.setFitWidth(SELECTOR_RETICLE_WIDTH / resizeFactor);
        topRight.setFitHeight(SELECTOR_RETICLE_HEIGHT / resizeFactor);
        bottomLeft.setFitWidth(SELECTOR_RETICLE_WIDTH / resizeFactor);
        bottomLeft.setFitHeight(SELECTOR_RETICLE_HEIGHT / resizeFactor);
        bottomRight.setFitWidth(SELECTOR_RETICLE_WIDTH / resizeFactor);
        bottomRight.setFitHeight(SELECTOR_RETICLE_HEIGHT / resizeFactor);
        setTranslateX(simulation.getSelectedBody().getCenterX());
        setTranslateY(simulation.getSelectedBody().getCenterY());
    }
}
