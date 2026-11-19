package javafinalproject;

public enum Direction {
    UP(-1, 0), //Move up one row, decrease the row index by one
    DOWN(1, 0), //Move down one row, increase the row index by one
    LEFT(0, -1), //Move left one column, decrease the column index by one
    RIGHT(0, 1); //Move right one column, increase the column index by one

	//Chance row coordinate in this direction
    public final int dRow;
    
    //Change in column coordinate in this direction
    public final int dCol;

    Direction(int dRow, int dCol) {
        this.dRow = dRow;
        this.dCol = dCol;
    }
}