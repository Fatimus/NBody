public class RightBound extends MutableDouble {

    private InteractableBody owner;

    public RightBound(double value, InteractableBody owner) {
        super(value);
        this.owner = owner;
    }

    public InteractableBody getOwner() {
        return owner;
    }
}