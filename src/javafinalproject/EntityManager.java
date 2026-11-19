//Isaac
package javafinalproject;

import java.util.List;

//Coordinates player movement, entity interactions, and undo/redo history
public class EntityManager {
    private Player player;
    private List<Entity> entities;
    private UndoRedoManager undoRedoManager;

    public EntityManager(Player player, List<Entity> entities) {
        this.player = player;
        this.entities = entities;
        this.undoRedoManager = new UndoRedoManager();
    }

    public Player getPlayer() {
        return player;
    }

    public List<Entity> getEntities() {
        return entities;
    }

    public UndoRedoManager getUndoRedoManager() {
        return undoRedoManager;
    }

    //Attempts to move the player one step in the given direction
    public boolean tryMovePlayer(Direction dir, BoardContract board) {
        Position fromPos = player.getPosition();
        Position targetPos = fromPos.move(dir);

        //Stops if the destination tile is out of bounds
        if (!board.isWalkable(targetPos)) {
            return false;
        }

        Entity blockingEntity = getEntityAt(targetPos);

        //If nothing is blocking the player, than they can move 
        if (blockingEntity == null) {
            player.setPosition(targetPos);
            undoRedoManager.recordMove(
                new UndoRedoManager.MoveRecord(fromPos, targetPos, null, null, null)
            );
            return true;
        }
        
        //If destination tile has a pushable box, and nothing is blocking the box, move the player and box
        if (blockingEntity instanceof PushableBox box) {
            Position boxFromPos = box.getPosition();
            Position boxTargetPos = targetPos.move(dir);

            if (board.isWalkable(boxTargetPos) && getEntityAt(boxTargetPos) == null) {
                box.setPosition(boxTargetPos);
                player.setPosition(targetPos);
                undoRedoManager.recordMove(
                    new UndoRedoManager.MoveRecord(fromPos, targetPos, box, boxFromPos, boxTargetPos)
                );
                return true;
            }
            return false;
        }

        
        //A blocking entity prevents movement
        return false;
    }

    //checks a specific grid spot and tells you if anything is occupying it
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