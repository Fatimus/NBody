import javafx.scene.input.MouseButton;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class Body extends InteractableUnit {

    private Circle objectDisplayer;
    private Circle selector;
    private ScatterRegion parentRegion;
    private RingRegion surroundingRegion = null;
    private double mass;

    public Body(double x, double y, double vx, double vy, BodyGroup parentGroup, ScatterRegion parentRegion, BuildState state, double mass) {
        super(x, y, parentGroup, state);
        this.vx = vx;
        this.vy = vy;
        this.mass = mass;
        this.parentRegion = parentRegion;
        state.getChildren().addAll(
                objectDisplayer(),
                createSelector());
    }

    private Circle objectDisplayer() {
        objectDisplayer = new Circle(x, y, mass < 0 ? 3 : Math.log(mass + 2) / Math.log(20) * 5, mass < 0 ? Color.BLUE : Color.BLACK);
        objectDisplayer.setOnMouseEntered(e -> {
            highlight();
        });
        objectDisplayer.setOnMouseExited(e -> {
            if (!selected) unhighlight();
        });
        objectDisplayer.setOnMouseClicked(e -> {
            if (e.getButton() == MouseButton.PRIMARY) select();
        });
        return objectDisplayer;
    }

    private Circle createSelector() {
        selector = new Circle(x, y, objectDisplayer.getRadius() + 2);
        selector.setFill(Color.TRANSPARENT);
        selector.setStroke(Color.rgb(14, 209, 69));
        selector.setStrokeWidth(1);
        selector.setMouseTransparent(true);
        selector.setVisible(false);
        return selector;
    }

    public void setSurroundingRegion(RingRegion surroundingRegion) {
        this.surroundingRegion = surroundingRegion;
    }

    public void setParentRegion(ScatterRegion parentRegion) {
        this.parentRegion = parentRegion;
    }

    public ScatterRegion getParentRegion() {
        return parentRegion;
    }

    public void setMass(double mass) {
        this.mass = mass;
    }

    public double getMass() {
        return mass;
    }

    public void setX(double x) {
        if (parentRegion == null) {
            this.x = x;
            updateVisualComponents();
        }
    }

    public void setY(double y) {
        if (parentRegion == null) {
            this.y = y;
            updateVisualComponents();
        }
    }

    protected void highlight() {
        if (!(highlighted || selected)) {
            objectDisplayer.setFill(Color.rgb(14, 209, 69));
            if (parentGroup != null) parentGroup.exclusivelyHighlight();
            highlighted = true;
        }
    }

    protected void unhighlight() {
        if (highlighted) {
            objectDisplayer.setFill(mass < 0 ? Color.BLUE : Color.BLACK);
            if (parentGroup != null) parentGroup.unhighlight();
            highlighted = false;
        }
    }

    protected void select() {
        if (state.getSelectedUnit() != null && !state.getSelectedUnit().equals(this)) {
            state.getSelectedUnit().deselect();
        }
        state.setSelectedUnit(this);
        if (parentRegion != null) parentRegion.highlight();
        if (parentGroup != null) parentGroup.exclusivelyHighlight();
        specialSelect();
    }

    protected void specialSelect() {
        selector.setVisible(true);
        objectDisplayer.setFill(Color.rgb(14, 209, 69));
        selected = true;
        highlighted = false;
    }

    protected void deselect() {
        if (parentGroup != null) parentGroup.unhighlight();
        state.setSelectedUnit(null);
        selector.setVisible(false);
        objectDisplayer.setFill(mass < 0 ? Color.BLUE : Color.BLACK);
        selected = false;
    }

    private void updateVisualComponents() {
        objectDisplayer.setCenterX(x);
        objectDisplayer.setCenterY(y);
        objectDisplayer.setRadius(mass < 0 ? 3 : Math.log(mass + 2) / Math.log(20) * 5);
        selector.setCenterX(x);
        selector.setCenterY(y);
        selector.setRadius(objectDisplayer.getRadius() + 2);
    }

    public void remove() {
        if (parentGroup != null) parentGroup.getGroupMembers().remove(this);
        if (parentRegion != null) parentRegion.getObjects().remove(this);
        if (surroundingRegion != null) surroundingRegion.remove();
        state.getChildren().removeAll(
                objectDisplayer,
                selector);
    }
}
