import java.util.ArrayList;
import java.util.List;

public class CollisionGroup {

    private ArrayList<InteractableBody> collisionObjects;

    public CollisionGroup(InteractableBody b, List<InteractableBody> objects) {
        collisionObjects = new ArrayList<>();
        collisionObjects.add(b);
        collisionObjects.addAll(objects);
    }

    public ArrayList<InteractableBody> getCollisionObjects(){
        return collisionObjects;
    }
}
