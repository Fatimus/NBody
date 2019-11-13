public abstract class Bound extends MutableDouble {

    private InteractableBody owner;

    protected Bound(double value, InteractableBody owner) {
        super(value);
        this.owner = owner;
    }
    public InteractableBody getOwner() {
        return owner;
    }
}
