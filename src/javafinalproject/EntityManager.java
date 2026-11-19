package javafinalproject;

import java.util.List;

public class EntityManager {
    private Player player;
    private List<Entity> entities;
    
    public EntityManager(Player player, List<Entity> entities) {
        this.player = player;
        this.entities = entities;
    }

    public Player getPlayer() {
        return player;
    }

    public List<Entity> getEntities() {
        return entities;
    }

    // Attempts to move the player in a given direction, handling box pushes
    public boolean tryMovePlayer(Direction dir, BoardContract board) {
        Position targetPos = player.getPosition().move(dir);

        // 1. Check if the target tile is a wall or unwalkable on the board
        if (!board.isWalkable(targetPos)) {
            return false;
        }

        // 2. Check if there is an entity at the target position
        Entity blockingEntity = getEntityAt(targetPos);

        if (blockingEntity == null) {
            // Empty floor — just move the player
            player.setPosition(targetPos);
            return true;
        }

        // 3. If there is a pushable box, try to push it
        if (blockingEntity instanceof PushableBox box) {
            Position boxTargetPos = targetPos.move(dir);

            // Ensure the space behind the box is walkable and not occupied by another entity
            if (board.isWalkable(boxTargetPos) && getEntityAt(boxTargetPos) == null) {
                box.setPosition(boxTargetPos);
                player.setPosition(targetPos);
                return true;
            }
            return false; // Box is blocked
        }

        // 4. Other blocking entities (like locked doors or keys) can be handled here
        if (!blockingEntity.isBlocking()) {
            // Non-blocking item (e.g. key pickup)
            player.setPosition(targetPos);
            entities.remove(blockingEntity);
            return true;
        }

        return false;
    }

    public Entity getEntityAt(Position pos) {
        for (Entity e : entities) {
            if (e.getPosition().equals(pos)) {
                return e;
            }
        }
        if (player.getPosition().equals(pos)) {
            return player;
        }
        return null;
    }
}