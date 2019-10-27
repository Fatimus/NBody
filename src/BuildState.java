
import javafx.scene.input.MouseButton;

import java.util.ArrayList;

public class BuildState extends ZoomPane {

    private Simulation simulation;
    private ArrayList<InteractableUnit> units;
    private InteractableUnit selectedUnit;

    public BuildState(Simulation simulation) {
        this.simulation = simulation;
        units = new ArrayList<>();
        simulation.getWindow().setOnMouseClicked(e -> {
            if (e.getButton() == MouseButton.PRIMARY && e.getPickResult().getIntersectedNode().equals(this)) {
                click();
            }
        });
    }

    public void click() {
        for (InteractableUnit unit : units) {
            unit.deselect();
        }
    }

    public void addUnit(InteractableUnit unit) {
        units.add(unit);
    }

    public void addAll(InteractableUnit... units) {
        for (InteractableUnit unit : units) addUnit(unit);
    }

    public void removeUnit(InteractableUnit unit) {
        units.remove(unit);
        unit.remove();
    }

    public ArrayList<InteractableUnit> getUnits() {
        return units;
    }

    public void setSelectedUnit(InteractableUnit selectedUnit) {
        this.selectedUnit = selectedUnit;
    }

    public InteractableUnit getSelectedUnit() {
        return selectedUnit;
    }
}
