package model;

//A list of board manager.

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class BoardList implements Iterable<CompressedBoard> {

    private final List<CompressedBoard> boards;

    // EFFECTS: creates new board list with no boards
    public BoardList() {
        boards = new ArrayList<>();
    }

    public void addBoard(Board b) {
        EventLog.getInstance().logEvent(new Event("Board \"" + b.getName() + "\" was added to list."));
        boards.add(new CompressedBoard(b));
    }

    public void addBoard(CompressedBoard b) {
        EventLog.getInstance().logEvent(new Event("Board \"" + b.getName() + "\" was added to list."));
        boards.add(b);
    }

    //MODIFIES: this
    //EFFECTS: removes board b from list
    public Board removeBoard(Board b) {
        if (boards.remove(new CompressedBoard(b))) {
            return b;
        } else {
            return null;
        }
    }

    //REQUIRES: int is not out of index for boards
    //MODIFIES: this
    //EFFECTS: removes board at index i from list
    public Board removeBoard(int i) {
        CompressedBoard compressedBoard = boards.remove(i);
        return compressedBoard.getBoard();
    }

    //MODIFIES: this
    //EFFECTS: removes first board with name from boards
    public Board removeBoard(String name) {
        for (CompressedBoard b : boards) {
            if (name.equals(b.getName())) {
                EventLog.getInstance().logEvent(new Event(
                        "Board \"" + b.getName() + "\" was deleted and removed from list."));
                boards.remove(b);
                return b.getBoard();
            }
        }
        return null;
    }

    //EFFECTS: returns a JSON array of all the boards
    public JSONArray toJson() {
        JSONArray jsonArray = new JSONArray();

        for (CompressedBoard b : boards) {
            jsonArray.put(b.toJson());
        }
        return jsonArray;
    }

    //EFFECTS: returns iterator over boards
    public Iterator<CompressedBoard> iterator() {
        return boards.iterator();
    }

    // EFFECTS: returns board with name, if none are found return null.
    public Board getBoard(String name) {
        for (CompressedBoard b : boards) {
            if (name.equals(b.getName())) {
                return b.getBoard();
            }
        }
        return null;
    }


    //getters

    public Board getBoard(int i) {
        return boards.get(i).getBoard();
    }

    public boolean isBoardListEmpty() {
        return boards.isEmpty();
    }

    public List<CompressedBoard> getBoards() {
        return boards;
    }
}
