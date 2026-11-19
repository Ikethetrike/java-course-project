package javafinalproject;

import java.util.HashSet;
import java.util.Set;

public class MockBoard implements BoardContract {
    private int[][] grid;
    private Set<Position> pressurePlates;
    private int levelNum;

    public MockBoard(int levelNum) {
        this.levelNum = levelNum;
        this.pressurePlates = new HashSet<>();
        loadLevel(levelNum);
    }

    private void loadLevel(int level) {
        pressurePlates.clear();

        if (level == 1) {
            // EASY: 10x10 grid. 0 = Wall, 1 = Floor. Easy obstacle in the middle.
            grid = new int[][] {
            	{0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 1, 1, 1, 1, 1, 1, 1, 1, 0},
                {0, 1, 0, 0, 1, 1, 0, 0, 1, 0},
                {0, 1, 0, 0, 1, 1, 0, 0, 1, 0},
                {0, 1, 1, 1, 1, 1, 1, 1, 1, 0},
                {0, 1, 1, 1, 1, 1, 1, 1, 1, 0},
                {0, 1, 0, 0, 1, 1, 0, 0, 1, 0},
                {0, 1, 0, 0, 1, 1, 0, 0, 1, 0},
                {0, 1, 1, 1, 1, 1, 1, 1, 1, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0}
            };
            pressurePlates.add(new Position(1, 1));
            pressurePlates.add(new Position(1, 8));
            pressurePlates.add(new Position(8, 1));
            pressurePlates.add(new Position(8, 8));

        } else if (level == 2) {
            // MEDIUM: Divided chambers with narrow doorways
            grid = new int[][] {
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 1, 1, 1, 0, 1, 1, 1, 1, 0},
                {0, 1, 1, 1, 0, 1, 1, 1, 1, 0},
                {0, 1, 1, 1, 0, 1, 1, 1, 1, 0},
                {0, 1, 1, 1, 1, 1, 1, 1, 1, 0}, // Doorway opening in middle column
                {0, 1, 1, 1, 1, 1, 1, 1, 1, 0},
                {0, 1, 1, 1, 0, 1, 1, 1, 1, 0},
                {0, 1, 1, 1, 0, 1, 1, 1, 1, 0},
                {0, 1, 1, 1, 0, 1, 1, 1, 1, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0}
            };
            pressurePlates.add(new Position(1, 8));
            pressurePlates.add(new Position(8, 1));

        } else {
            // HARD: Dense maze with all 4 inner corners as targets
            grid = new int[][] {
            	{0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 1, 1, 0, 0},
                {0, 0, 0, 0, 1, 1, 1, 1, 0, 0},
                {0, 0, 1, 1, 1, 1, 0, 1, 0, 0}, 
                {0, 0, 1, 1, 1, 1, 1, 1, 0, 0},
                {0, 0, 0, 0, 1, 1, 1, 1, 0, 0},
                {0, 0, 0, 0, 0, 0, 1, 1, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0}
                
            };
            // 4 corner target traps
            pressurePlates.add(new Position(4, 2));
            pressurePlates.add(new Position(5, 2));
            pressurePlates.add(new Position(5, 3));
            pressurePlates.add(new Position(6, 4));
            
        }
    }

    @Override
    public boolean isWalkable(Position pos) {
        if (pos.getRow() < 0 || pos.getRow() >= grid.length || 
            pos.getCol() < 0 || pos.getCol() >= grid[0].length) {
            return false;
        }
        return grid[pos.getRow()][pos.getCol()] == 1;
    }

    @Override
    public boolean isExit(Position pos) {
        return false;
    }

    @Override
    public boolean isPressurePlate(Position pos) {
        return pressurePlates.contains(pos);
    }
}