import javafx.scene.layout.Pane;
import javafx.scene.transform.Scale;

public class ZoomPane extends Pane {

    private Scale zoomScale;

    public ZoomPane() {
        zoomScale = new Scale();
        getTransforms().add(zoomScale);
    }

    public void zoomIn(double x, double y) {
        zoomScale.setX(zoomScale.getX() * 1.1);
        zoomScale.setY(zoomScale.getY() * 1.1);
        setTranslateX(getTranslateX() - 0.1 * (x - getTranslateX()));
        setTranslateY(getTranslateY() - 0.1 * (y - getTranslateY()));
    }

    public void zoomOut(double x, double y) {
        zoomScale.setX(zoomScale.getX() / 1.1);
        zoomScale.setY(zoomScale.getY() / 1.1);
        setTranslateX(getTranslateX() + (1 - 1 / 1.1) * (x - getTranslateX()));
        setTranslateY(getTranslateY() + (1 - 1 / 1.1) * (y - getTranslateY()));
    }

    public Scale getZoomScale() {
        return zoomScale;
    }
}
