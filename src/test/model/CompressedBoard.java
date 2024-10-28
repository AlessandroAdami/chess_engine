package model;

/**
 * Compressed board stored only with name and position, for storing multiple boards while not actively using most
 * BoardList takes advantage of this structure
 */

public class CompressedBoard {

    private String name;
    private String fenPosition;
    private String ID;

    public CompressedBoard(Board board) {
        this.name = board.getName();
        this.fenPosition = board.getFenPosition();
    }

    public String getName() {
        return this.name;
    }

    public Board getBoard() {
        Board board = new Board(name);
        board.loadPositionFromFen(fenPosition);
        return board;
    }
}
