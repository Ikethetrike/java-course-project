package javafinalproject;

import java.util.HashSet;
import java.util.Set;

//Creates the tile grid (0 = wall, 1 = walkable floor) and pressure plate positions 
public class MockBoard implements BoardContract {
	//2D grid layout
    private int[][] grid;
    //Set the grid positions for pressure plates 
    private Set<Position> pressurePlates;
    private int levelNum;

    //Loads the board layout and pressure plates for the specific level
    public MockBoard(int levelNum) {
        this.levelNum = levelNum;
        this.pressurePlates = new HashSet<>();
        loadLevel(levelNum);
    }

    //Populates the 10x10 grid
    private void loadLevel(int level) {
        pressurePlates.clear();

        if (level == 1) {
            //Level 1
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
            //Level 2
            grid = new int[][] {
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 1, 1, 1, 0, 1, 1, 1, 1, 0},
                {0, 1, 1, 1, 0, 1, 1, 1, 1, 0},
                {0, 1, 1, 1, 0, 1, 1, 1, 1, 0},
                {0, 1, 1, 1, 1, 1, 1, 1, 1, 0}, 
                {0, 1, 1, 1, 1, 1, 1, 1, 1, 0},
                {0, 1, 1, 1, 0, 1, 1, 1, 1, 0},
                {0, 1, 1, 1, 0, 1, 1, 1, 1, 0},
                {0, 1, 1, 1, 0, 1, 1, 1, 1, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0}
            };
            pressurePlates.add(new Position(1, 8));
            pressurePlates.add(new Position(8, 1));

        } else {
            //Level 3
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
    public boolean isPressurePlate(Position pos) {
        return pressurePlates.contains(pos);
    }
}