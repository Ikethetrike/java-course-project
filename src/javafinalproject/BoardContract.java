//Isaac
package javafinalproject;


public interface BoardContract {
    //Returns true if entities can move onto this position 
    boolean isWalkable(Position pos);

    //Returns true if this position contains a pressure plate
    boolean isPressurePlate(Position pos);
}