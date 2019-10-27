import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class RingRegion extends ScatterRegion {

    private double innerRadius;
    private double outerRadius;
    private int rings;
    private int objectsPerRing;
    private double radialRandomizationRange;
    private double angleRandomizationRange;
    private OrbitDirection direction;
    private InteractableUnit centerObject;
//    private ImageView centerPoint;

    private Circle innerBound;
    private Circle outerBound;
    private Circle region;

    public RingRegion(InteractableUnit centerObject, BodyGroup parentGroup, BuildState state, OrbitDirection direction) {
        super(centerObject.getX(), centerObject.getY(), parentGroup, state);
        this.direction = direction;
        this.centerObject = centerObject;
        highlighted = false;
        //DEFAULT
        innerRadius = 100;
        outerRadius = 150;
        rings = 5;
        objectsPerRing = 10;
        radialRandomizationRange = 0;
        angleRandomizationRange = 0;
        massRandomizationRange = 0;
        massPerObject = 0;
        //
        generate();
        state.getChildren().addAll(
                innerBound(),
                region());
        state.getChildren().add(0, outerBound());
    }

    private Circle innerBound() {
        innerBound = new Circle(x, y, innerRadius, Color.TRANSPARENT);
        innerBound.setStroke(Color.rgb(7, 104, 64));
        innerBound.setStrokeWidth(1);
        innerBound.setMouseTransparent(true);
        return innerBound;
    }

    private Circle outerBound() {
        outerBound = new Circle(x, y, outerRadius, Color.TRANSPARENT);
        outerBound.setStroke(Color.rgb(7, 104, 64));
        outerBound.setStrokeWidth(1);
        outerBound.setOnMouseMoved(e -> {
            double distanceFromCenter = Math.sqrt((e.getX() - x) * (e.getX() - x) + (e.getY() - y) * (e.getY() - y));
            if (distanceFromCenter < innerRadius && highlighted) unhighlight();
            else if (distanceFromCenter > innerRadius && !highlighted && !selected) {
                highlight();
            }
        });
        outerBound.setOnMouseExited(e -> {
            if (highlighted) unhighlight();
        });
        outerBound.setOnMouseClicked(e -> {
            if (e.getButton() == MouseButton.PRIMARY) {
                if (Math.sqrt((e.getX() - x) * (e.getX() - x) + (e.getY() - y) * (e.getY() - y)) < innerRadius)
                    state.click();
                if (highlighted) select();
            }
        });
        return outerBound;
    }

    private Circle region() {
        region = new Circle(x, y, (innerRadius + outerRadius) / 2, Color.TRANSPARENT);
        region.setStroke(Color.rgb(14, 209, 69, 0.3));
        region.setStrokeWidth(outerRadius - innerRadius);
        region.setMouseTransparent(true);
        region.setVisible(false);
        return region;
    }

    public void generate() {
        for (int i = 0; i < objects.size(); i++) {
            objects.get(i).remove();
            i--;
        }
        for (int i = 0; i < rings; i++) {
            for (int j = 0; j < objectsPerRing; j++) {
                double radius = innerRadius + (outerRadius - innerRadius) / rings * i + Math.random() * radialRandomizationRange - radialRandomizationRange / 2 + (outerRadius - innerRadius) / rings / 2;
                double angle = 2 * Math.PI / objectsPerRing * j + Math.random() * angleRandomizationRange - angleRandomizationRange / 2;
                double tx = x + Math.cos(angle) * radius;
                double ty = y + Math.sin(angle) * radius;
                double mass = massPerObject + Math.random() * massRandomizationRange - massRandomizationRange / 2;
                double speed = Math.sqrt(centerObject.getMass() * 2 / radius);
                double localvx;
                double localvy;
                if (direction == OrbitDirection.CLOCKWISE) {
                    localvx = -speed * ty / radius;
                    localvy = speed * tx / radius;
                } else {
                    localvx = speed * ty / radius;
                    localvy = -speed * tx / radius;
                }
                objects.add(new Body(tx, ty, localvx, localvy, parentGroup, this, state, mass));
            }
        }
    }

    public void remove() {
        for (Body b : objects) {
            b.remove();
        }
        if (parentGroup != null) parentGroup.getGroupMembers().remove(this);
        state.getChildren().removeAll(
                innerBound,
                outerBound,
                region);
    }

    public void setX(double x) {
        centerObject.setX(x);
        this.x = x;
        innerBound.setCenterX(x);
        outerBound.setCenterX(x);
        region.setCenterX(x);
        for (Body b : objects) b.setX(b.x + x - this.x);
    }

    public void setY(double y) {
        centerObject.setY(y);
        this.y = y;
        innerBound.setCenterY(y);
        outerBound.setCenterY(y);
        region.setCenterY(y);
        for (Body b : objects) b.setY(b.y + y - this.y);
    }

    public void setRings(int rings) {
        this.rings = rings;
    }

    public void setObjectsPerRing(int objectsPerRing) {
        this.objectsPerRing = objectsPerRing;
    }

    public void setRadialRandomizationRange(double radialRandomizationRange) {
        this.radialRandomizationRange = radialRandomizationRange;
    }

    public void setAngleRandomizationRange(double angleRandomizationRange) {
        this.angleRandomizationRange = angleRandomizationRange;
    }

    public void setMassRandomizationRange(double massRandomizationRange) {
        this.massRandomizationRange = massRandomizationRange;
    }

    public void setCenterObject(InteractableUnit centerObject) {
        this.centerObject = centerObject;
        setX(centerObject.getX());
        setY(centerObject.getY());
    }

    public void setDirection(OrbitDirection direction) {
        this.direction = direction;
    }

    public void setInnerRadius(double innerRadius) {
        this.innerRadius = innerRadius;
        innerBound.setRadius(innerRadius);
        region.setRadius((this.outerRadius + this.innerRadius) / 2);
        region.setStrokeWidth(this.outerRadius - this.innerRadius);
    }

    public void setOuterRadius(double outerRadius) {
        this.outerRadius = outerRadius;
        outerBound.setRadius(outerRadius);
        region.setRadius((this.outerRadius + this.innerRadius) / 2);
        region.setStrokeWidth(this.outerRadius - this.innerRadius);
    }

    public void setMassPerObject(double massPerObject) {
        this.massPerObject = massPerObject;
    }

    public void highlight() {
        region.setVisible(true);
        if (parentGroup != null) parentGroup.exclusivelyHighlight();
        centerObject.highlight();
        highlighted = true;
    }

    public void unhighlight() {
        region.setVisible(false);
        if (parentGroup != null) parentGroup.unhighlight();
        centerObject.unhighlight();
        highlighted = false;
    }

    public void select() {
        if (state.getSelectedUnit() != null && !state.getSelectedUnit().equals(this)) {
            state.getSelectedUnit().deselect();
        }
        state.setSelectedUnit(this);
        if (parentGroup != null) parentGroup.exclusivelyHighlight();
        centerObject.unhighlight();
        specialSelect();
    }

    public void specialSelect() {
        selected = true;
        highlighted = false;
        region.setVisible(true);
        region.setStroke(Color.rgb(14, 209, 69, 0.7));
    }

    public void deselect() {
        if (selected) {
            region.setStroke(Color.rgb(14, 209, 69, 0.3));
            region.setVisible(false);
            if (parentGroup != null) parentGroup.unhighlight();
            selected = false;
            state.setSelectedUnit(null);
        }
        for (Body b : objects) b.deselect();
    }

    public enum OrbitDirection {
        CLOCKWISE,
        COUNTERCLOCKWISE
    }
}
