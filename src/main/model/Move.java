package model;

import model.exceptions.BoardSquareOutOfBoundsException;

//Represents a move in a board with a 'from' and a 'to' square

public class Move {

    private final int fromCol;
    private final int fromRow;
    private final int toCol;
    private final int toRow;

    //EFFECTS: constructs a move with starting and final square coordinates
    public Move(int fromCol,int fromRow,int toCol,int toRow) throws BoardSquareOutOfBoundsException {
        if (fromCol < 0 || fromCol > 7
                || fromRow < 0 || fromRow > 7
                || toCol < 0 || toCol > 7
                || toRow < 0 || toRow > 7) {
            throw new BoardSquareOutOfBoundsException();
        }
        this.fromCol = fromCol;
        this.fromRow = fromRow;
        this.toCol = toCol;
        this.toRow = toRow;
    }

    public int getFromCol() {
        return fromCol;
    }

    public int getFromRow() {
        return fromRow;
    }

    public int getToCol() {
        return toCol;
    }

    public int getToRow() {
        return toRow;
    }
}
