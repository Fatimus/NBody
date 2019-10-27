import javafx.scene.Group;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class CrosshairFocusIndicator extends Group {

    private Simulation simulation;
    private ImageView left;
    private ImageView right;
    private ImageView bottom;
    private ImageView top;
    private final double SELECTOR_RETICLE_WIDTH = 5;
    private final double SELECTOR_RETICLE_HEIGHT;
    private final double ORIGINAL_SEPARATION = 5 * Math.sqrt(2);

    public CrosshairFocusIndicator(Simulation s) {
        simulation = s;
        Image selectorComponentImage = new Image(getClass().getClassLoader().getResourceAsStream("icons/selector corner purple.png"));
        SELECTOR_RETICLE_HEIGHT = SELECTOR_RETICLE_WIDTH * selectorComponentImage.getHeight() / selectorComponentImage.getWidth();
        left = new ImageView(selectorComponentImage);
        left.setRotate(90);
        right = new ImageView(selectorComponentImage);
        right.setRotate(-90);
        bottom = new ImageView(selectorComponentImage);
        bottom.setRotate(0);
        top = new ImageView(selectorComponentImage);
        top.setRotate(180);
        left.setFitWidth(SELECTOR_RETICLE_WIDTH);
        left.setFitHeight(SELECTOR_RETICLE_HEIGHT);
        right.setFitWidth(SELECTOR_RETICLE_WIDTH);
        right.setFitHeight(SELECTOR_RETICLE_HEIGHT);
        bottom.setFitWidth(SELECTOR_RETICLE_WIDTH);
        bottom.setFitHeight(SELECTOR_RETICLE_HEIGHT);
        top.setFitWidth(SELECTOR_RETICLE_WIDTH);
        top.setFitHeight(SELECTOR_RETICLE_HEIGHT);
        getChildren().addAll(left, right, bottom, top);
        setVisible(false);
    }

    public void updateSelectorSize() {
        if (!simulation.hasFocusBody()) return;
        double resizeFactor = simulation.getSimulationScale().getX();
        left.setTranslateX(-ORIGINAL_SEPARATION / resizeFactor - simulation.getFocusBody().getRadius() - SELECTOR_RETICLE_WIDTH / (2 * resizeFactor));
        left.setTranslateY(-SELECTOR_RETICLE_HEIGHT / (2 * resizeFactor));
        right.setTranslateX(ORIGINAL_SEPARATION / resizeFactor + simulation.getFocusBody().getRadius() - SELECTOR_RETICLE_WIDTH / (2 * resizeFactor));
        right.setTranslateY(-SELECTOR_RETICLE_HEIGHT / (2 * resizeFactor));
        bottom.setTranslateX(-SELECTOR_RETICLE_WIDTH / (2 * resizeFactor));
        bottom.setTranslateY(ORIGINAL_SEPARATION / resizeFactor + simulation.getFocusBody().getRadius() - SELECTOR_RETICLE_HEIGHT / (2 * resizeFactor));
        top.setTranslateX(-SELECTOR_RETICLE_WIDTH / (2 * resizeFactor));
        top.setTranslateY(-ORIGINAL_SEPARATION / resizeFactor - simulation.getFocusBody().getRadius() - SELECTOR_RETICLE_HEIGHT / (2 * resizeFactor));
        left.setFitWidth(SELECTOR_RETICLE_WIDTH / resizeFactor);
        left.setFitHeight(SELECTOR_RETICLE_HEIGHT / resizeFactor);
        right.setFitWidth(SELECTOR_RETICLE_WIDTH / resizeFactor);
        right.setFitHeight(SELECTOR_RETICLE_HEIGHT / resizeFactor);
        bottom.setFitWidth(SELECTOR_RETICLE_WIDTH / resizeFactor);
        bottom.setFitHeight(SELECTOR_RETICLE_HEIGHT / resizeFactor);
        top.setFitWidth(SELECTOR_RETICLE_WIDTH / resizeFactor);
        top.setFitHeight(SELECTOR_RETICLE_HEIGHT / resizeFactor);
        setTranslateX(simulation.getFocusBody().getCenterX());
        setTranslateY(simulation.getFocusBody().getCenterY());
    }
}

