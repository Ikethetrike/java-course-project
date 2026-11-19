package javafinalproject;

public abstract class Entity {
    protected Position position;

    public Entity(Position position) {
        this.position = position;
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    // Whether this entity blocks the player or boxes from walking through it
    public abstract boolean isBlocking();
}