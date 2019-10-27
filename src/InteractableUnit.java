public abstract class InteractableUnit {

    protected double x;
    protected double y;
    protected double vx;
    protected double vy;
    protected String name;
    protected BuildState state;
    protected boolean selected;
    protected boolean highlighted;
    protected BodyGroup parentGroup;

    public InteractableUnit(double x, double y, BodyGroup parentGroup, BuildState state) {
        this.x = x;
        this.y = y;
        this.state = state;
        this.parentGroup = parentGroup;
        if(parentGroup != null) parentGroup.addMember(this);
        selected = false;
        highlighted = false;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public String getName() {
        return name;
    }

    public abstract void setX(double x);

    public abstract void setY(double y);

    public double getVX() {
        return vx;
    }

    public double getVY() {
        return vy;
    }

    public void setVX(double vx) {
        this.vx = vx;
    }

    public void setVY(double vy) {
        this.vy = vy;
    }

    public boolean isSelected() {
        return selected;
    }

    public boolean isHighlighted() {
        return highlighted;
    }

    public BodyGroup getParentGroup() {
        return parentGroup;
    }

    public void setParentGroup(BodyGroup parentGroup) {
        this.parentGroup = parentGroup;
    }

    public abstract double getMass();

    public void setName(String name) {
        this.name = name;
    }

    protected abstract void highlight();

    protected abstract void unhighlight();

    protected abstract void select();

    protected abstract void specialSelect();

    protected abstract void deselect();

    public abstract void remove();
}
