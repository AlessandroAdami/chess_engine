package model;

import org.json.JSONObject;

import java.util.Objects;

/**
 * Compressed board stored only with name and position, for storing multiple boards while not actively using most
 */

public class CompressedBoard {

    private String name;
    private String fenPosition;

    public CompressedBoard(Board board) {
        this.name = board.getName();
        this.fenPosition = board.getFenPosition();
    }

    public CompressedBoard(String name) {
        this.name = name;
        this.fenPosition = Board.getFenStartingPosition();
    }

    public String getName() {
        return this.name;
    }

    public Board getBoard() {
        Board board = new Board(name);
        board.loadPositionFromFen(fenPosition);
        return board;
    }

    //EFFECTS: return board as json object
    public JSONObject toJson() {
        JSONObject json = new JSONObject();
        json.put("position", fenPosition);
        json.put("name", name);
        return json;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CompressedBoard)) return false;
        CompressedBoard that = (CompressedBoard) o;
        return Objects.equals(getName(), that.getName()) && Objects.equals(fenPosition, that.fenPosition);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName(), fenPosition);
    }
}
