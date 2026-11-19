package javafinalproject;

public interface BoardContract {
    boolean isWalkable(Position pos);
    boolean isExit(Position pos);
    boolean isPressurePlate(Position pos);
}
