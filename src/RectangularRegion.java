import javafx.scene.input.MouseButton;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class RectangularRegion extends ScatterRegion {

    private double width;
    private double height;
    private int rows;
    private int columns;
    private double randomizationRadius;
    private Rectangle region;

    public RectangularRegion(double x, double y, BodyGroup parentGroup, BuildState state) {
        super(x, y, parentGroup, state);
//        rows = 5;
//        columns = 5;
//        randomizationRadius = 0;
//        massRandomizationRange = 0;
//        massPerObject = 0;
        state.getChildren().add(0, region());
    }

    private Rectangle region() {
        region = new Rectangle(x, y, width, height);
        region.setFill(Color.TRANSPARENT);
        region.setStroke(Color.rgb(7, 104, 64));
        region.setStrokeWidth(1);
        region.setOnMouseEntered(e -> {
            if (!(selected || highlighted)) highlight();
        });
        region.setOnMouseExited(e -> {
            if (highlighted) unhighlight();
        });
        region.setOnMouseClicked(e -> {
            if (e.getButton() == MouseButton.PRIMARY) select();
        });
        return region;
    }

    public void generate() {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                double angle = Math.random() * 2 * Math.PI;
                double offset = Math.random() * randomizationRadius;
                double tx = x + width / columns * j + width / columns / 2 + offset * Math.cos(angle);
                double ty = y + height / rows * i + height / rows / 2 + offset * Math.sin(angle);
                objects.add(new Body(tx, ty, vx, vy, parentGroup, this, state, massPerObject + Math.random() * massRandomizationRange - massRandomizationRange / 2));
            }
        }
    }

    public void remove() {
        for (Body b : objects) {
            b.remove();
            state.getChildren().remove(region);
        }
    }

    public void setX(double x) {
        this.x = x;
        region.setTranslateX(x);
        for (Body b : objects) b.setX(b.x + x - this.x);
    }

    public void setY(double y) {
        this.y = y;
        region.setTranslateY(y);
        for (Body b : objects) b.setY(b.y + y - this.y);
    }

    public void setRows(int rows) {
        this.rows = rows;
    }

    public void setColumns(int columns) {
        this.columns = columns;
    }

    public void setWidth(double width) {
        this.width = width;
        region.setWidth(width);
    }

    public void setHeight(double height) {
        this.height = height;
        region.setHeight(height);
    }

    public int getRows() {
        return rows;
    }

    public int getColumns() {
        return columns;
    }

    public double getWidth() {
        return width;
    }

    public double getHeight() {
        return height;
    }

    public void highlight() {
        highlighted = true;
        region.setFill(Color.rgb(14, 209, 69, 0.3));
        if (parentGroup != null) parentGroup.exclusivelyHighlight();
    }

    public void unhighlight() {
        highlighted = false;
        region.setFill(Color.TRANSPARENT);
        if (parentGroup != null) parentGroup.unhighlight();
    }

    public void select() {
        if (state.getSelectedUnit() != null && !state.getSelectedUnit().equals(this)) {
            state.getSelectedUnit().deselect();
        }
        state.setSelectedUnit(this);
        if (parentGroup != null) parentGroup.exclusivelyHighlight();
        specialSelect();
    }

    public void specialSelect() {
        selected = true;
        highlighted = false;
        region.setVisible(true);
        region.setFill(Color.rgb(14, 209, 69, 0.7));
    }

    public void deselect() {
        selected = false;
        region.setFill(Color.TRANSPARENT);
        if (parentGroup != null) parentGroup.unhighlight();
        for (Body b : objects) b.deselect();
        state.setSelectedUnit(null);
    }
}
