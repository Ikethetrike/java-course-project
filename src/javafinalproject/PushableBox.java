//Caleb
package javafinalproject;

public class PushableBox extends Entity {

    public PushableBox(Position position) {
        super(position);
    }

    @Override
    public boolean isBlocking() {
        return true;
    }

    // Attempts to slide the box in a given direction
    public boolean tryPush(Direction dir, java.util.function.Predicate<Position> isWalkableCheck, java.util.function.BiPredicate<Position, PushableBox> hasBoxAtCheck) {
        Position nextPos = position.move(dir);
        
        // Check if the tile behind the box is walkable and empty of other boxes
        if (isWalkableCheck.test(nextPos) && !hasBoxAtCheck.test(nextPos, this)) {
            this.position = nextPos;
            return true;
        }
        return false;
    }
}