//Isaac
package javafinalproject;

//Base class for all objects that occupy a grid cell in the game

public abstract class Entity {
   //current grid position of this entity
	protected Position position;

    public Entity(Position position) {
        this.position = position;
    }
    //Returns the entity's current grid position 
    public Position getPosition() {
        return position;
    }
    //Updates the entity's grid position
    public void setPosition(Position position) {
        this.position = position;
    }

    // Whether this entity blocks the player or boxes from walking through it
    public abstract boolean isBlocking();
}