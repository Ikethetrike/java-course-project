//Caleb
package javafinalproject;

import java.util.ArrayDeque;
import java.util.Deque;

public class UndoRedoManager {
    public static class MoveRecord {
        public final Position playerFrom;
        public final Position playerTo;
        public final PushableBox movedBox;
        public final Position boxFrom;
        public final Position boxTo;

        public MoveRecord(Position playerFrom, Position playerTo,
                          PushableBox movedBox, Position boxFrom, Position boxTo) {
            this.playerFrom = playerFrom;
            this.playerTo = playerTo;
            this.movedBox = movedBox;
            this.boxFrom = boxFrom;
            this.boxTo = boxTo;
        }
    }

    private final Deque<MoveRecord> undoStack = new ArrayDeque<>();
    private final Deque<MoveRecord> redoStack = new ArrayDeque<>();

    public void recordMove(MoveRecord record) {
        undoStack.push(record);
        redoStack.clear();
    }

    public boolean undo(Player player) {
        if (undoStack.isEmpty()) return false;
        MoveRecord rec = undoStack.pop();

        player.setPosition(rec.playerFrom);
        if (rec.movedBox != null && rec.boxFrom != null) {
            rec.movedBox.setPosition(rec.boxFrom);
        }

        redoStack.push(rec);
        return true;
    }

    public boolean redo(Player player) {
        if (redoStack.isEmpty()) return false;
        MoveRecord rec = redoStack.pop();

        if (rec.movedBox != null && rec.boxTo != null) {
            rec.movedBox.setPosition(rec.boxTo);
        }
        player.setPosition(rec.playerTo);

        undoStack.push(rec);
        return true;
    }

    public void clear() {
        undoStack.clear();
        redoStack.clear();
    }
}