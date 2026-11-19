package javafinalproject;

import java.util.ArrayList;
import java.util.List;

public class TestMain {
    public static void main(String[] args) {
        BoardContract board = new MockBoard();
        Player player = new Player(new Position(1, 1));
        List<Entity> entities = new ArrayList<>();
        entities.add(new PushableBox(new Position(1, 2)));

        EntityManager manager = new EntityManager(player, entities);

        System.out.println("Initial Player Pos: (" + player.getPosition().getRow() + ", " + player.getPosition().getCol() + ")");
        
        // Try pushing the box to the right (Direction.RIGHT)
        boolean moved = manager.tryMovePlayer(Direction.RIGHT, board);
        System.out.println("Move Right successful? " + moved);
        System.out.println("New Player Pos: (" + player.getPosition().getRow() + ", " + player.getPosition().getCol() + ")");
        
        Entity box = entities.get(0);
        System.out.println("Box Pos after push: (" + box.getPosition().getRow() + ", " + box.getPosition().getCol() + ")");
    }
}