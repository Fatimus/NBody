import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import org.omg.PortableInterceptor.SYSTEM_EXCEPTION;

import java.util.ArrayList;

public class BodyGroup extends InteractableUnit {

    private ArrayList<InteractableUnit> groupMembers;
    private ImageView deselectedIndicator;
    private ImageView selectedIndicator;
    private ImageView highlightedIndicator;
    private boolean exclusivelySelected;
    private boolean exclusivelyHighlighted;

    public BodyGroup(double x, double y, BodyGroup parentGroup, BuildState state) {
        super(x, y, parentGroup, state);
        groupMembers = new ArrayList<>();
        state.getChildren().add(0, setDeselectedIndicator());
        state.getChildren().add(0, setSelectedIndicator());
        state.getChildren().add(0, setHighlightedIndicator());
        exclusivelySelected = false;
    }

    private ImageView setDeselectedIndicator() {
        Image indicatorImage = new Image(getClass().getClassLoader().getResourceAsStream("icons/group indicator.png"));
        deselectedIndicator = new ImageView(indicatorImage);
        deselectedIndicator.setOnMouseEntered(e -> {
            if (!(selected || exclusivelySelected)) highlight();
        });
        deselectedIndicator.setFitHeight(80);
        deselectedIndicator.setFitWidth(80);
        deselectedIndicator.setTranslateX(x - 40);
        deselectedIndicator.setTranslateY(y - 40);
//        deselectedIndicator.setOnMouseMoved(e -> {
//            if (e.getX() > x - 40 && e.getX() < x + 40 && e.getY() > y - 40 && e.getY() < y + 40) {
//                if (!(selected || exclusivelySelected || highlighted)) highlight();
//                if (e.getButton() == MouseButton.PRIMARY) select();
//            } else {
//                if (highlighted) unhighlight();
//                if (e.getButton() == MouseButton.PRIMARY) deselect();
//            }
//        });
        deselectedIndicator.setOnMouseEntered(e -> {
            highlight();
        });
        return deselectedIndicator;
    }

    private ImageView setHighlightedIndicator() {
        Image indicatorImage = new Image(getClass().getClassLoader().getResourceAsStream("icons/highlighted group indicator.png"));
        highlightedIndicator = new ImageView(indicatorImage);
        highlightedIndicator.setOnMouseExited(e -> {
            if (!(selected || exclusivelySelected)) unhighlight();
        });
        highlightedIndicator.setOnMouseClicked(e -> {
            if (e.getButton() == MouseButton.PRIMARY) select();
        });
        highlightedIndicator.setTranslateX(x - 40);
        highlightedIndicator.setTranslateY(y - 40);
        highlightedIndicator.setFitHeight(80);
        highlightedIndicator.setFitWidth(80);
        highlightedIndicator.setVisible(false);
        return highlightedIndicator;
    }

    private ImageView setSelectedIndicator() {
        Image indicatorImage = new Image(getClass().getClassLoader().getResourceAsStream("icons/selected group indicator.png"));
        selectedIndicator = new ImageView(indicatorImage);
        selectedIndicator.setTranslateX(x - 47);
        selectedIndicator.setTranslateY(y - 47);
        selectedIndicator.setFitHeight(94);
        selectedIndicator.setFitWidth(94);
        selectedIndicator.setVisible(false);
        return selectedIndicator;
    }

    public void addMember(InteractableUnit member) {
        groupMembers.add(member);
        if (member.getParentGroup() != null) member.getParentGroup().removeMember(member);
        member.setParentGroup(this);
    }

    public void removeMember(InteractableUnit member) {
        groupMembers.remove(member);
        member.setParentGroup(null);
    }

    public void addAll(InteractableUnit... members) {
        for (InteractableUnit unit : members) addMember(unit);
    }

    public void remove() {
        for (InteractableUnit unit : groupMembers) {
            unit.remove();
        }
        if (parentGroup != null) parentGroup.getGroupMembers().remove(this);
        state.getChildren().removeAll(
                deselectedIndicator,
                selectedIndicator,
                highlightedIndicator);
    }

    public void setX(double x) {
        for (InteractableUnit unit : groupMembers) unit.setX(unit.x + x - this.x);
        this.x = x;
        updateIndicatorLocation();
    }

    public void setY(double y) {
        for (InteractableUnit unit : groupMembers) unit.setY(unit.y + y - this.y);
        this.y = y;
        updateIndicatorLocation();
    }

    public double getMass() {
        double totalMass = 0;
        for (InteractableUnit unit : groupMembers) {
            totalMass += unit.getMass();
        }
        return totalMass;
    }

    public ArrayList<InteractableUnit> getGroupMembers() {
        return groupMembers;
    }

    protected void highlight() {
        if (!(selected || highlighted)) {
            deselectedIndicator.setVisible(false);
            highlightedIndicator.setVisible(true);
            if (parentGroup != null) parentGroup.exclusivelyHighlight();
            highlighted = true;
        }
    }

    protected void unhighlight() {
        if (!groupMembers.contains(state.getSelectedUnit()) && !selected) {
            deselectedIndicator.setVisible(true);
            highlightedIndicator.setVisible(false);
            if (parentGroup != null) parentGroup.unhighlight();
            highlighted = false;
        }
    }

    protected void select() {
        if (state.getSelectedUnit() != null && !state.getSelectedUnit().equals(this)) {
            state.getSelectedUnit().deselect();
        }
        state.setSelectedUnit(this);
        if (parentGroup != null) parentGroup.exclusivelyHighlight();
        specialSelect();
    }

    protected void specialSelect() {
        highlightedIndicator.setVisible(false);
        deselectedIndicator.setVisible(false);
        selectedIndicator.setVisible(true);
        for (InteractableUnit unit : groupMembers) unit.specialSelect();
        selected = true;
        highlighted = false;
    }

    protected void deselect() {
        deselectedIndicator.setVisible(true);
        selectedIndicator.setVisible(false);
        for (InteractableUnit unit : groupMembers) unit.deselect();
//        if (parentGroup != null) parentGroup.deselect();
        if (parentGroup != null) parentGroup.unhighlight();
        state.setSelectedUnit(null);
        selected = false;
    }

    protected void exclusivelySelect() {
        deselectedIndicator.setVisible(false);
        selectedIndicator.setVisible(true);
        highlightedIndicator.setVisible(false);
        if (parentGroup != null) parentGroup.exclusivelySelect();
        exclusivelySelected = true;
        highlighted = false;
    }

    protected void exclusivelyHighlight() {
        if (!(selected || highlighted)) {
            deselectedIndicator.setVisible(false);
            highlightedIndicator.setVisible(true);
            if (parentGroup != null) parentGroup.exclusivelyHighlight();
            exclusivelySelected = false;
            highlighted = true;
        }
    }

    private void updateIndicatorLocation() {
        selectedIndicator.setTranslateX(x - 47);
        selectedIndicator.setTranslateY(y - 47);
        deselectedIndicator.setTranslateX(x - 40);
        deselectedIndicator.setTranslateY(y - 40);
    }
}
