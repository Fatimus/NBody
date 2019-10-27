public class LeftBound extends MutableDouble {

    private InteractableBody owner;

    public LeftBound(double value, InteractableBody owner) {
        super(value);
        this.owner = owner;
    }

    public InteractableBody getOwner() {
        return owner;
    }
}
