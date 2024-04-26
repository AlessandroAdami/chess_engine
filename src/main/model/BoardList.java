package model;

//A list of board manager.


//TODO: getters and removers should throw exceptions

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class BoardList implements Iterable<Board> {

    private List<Board> boards;

    // EFFECTS: creates new board list with no boards
    public BoardList() {
        boards = new ArrayList<>();
    }

    public void addBoard(Board b) {
        EventLog.getInstance().logEvent(new Event("Board \"" + b.getName() + "\" was added to list."));
        boards.add(b);
    }

    //MODIFIES: this
    //EFFECTS: removes board b from list
    public Board removeBoard(Board b) {
        if (boards.remove(b)) {
            return b;
        } else {
            return null;
        }
    }

    //REQUIRES: int is not out of index for boards
    //MODIFIES: this
    //EFFECTS: removes board at index i from list
    public Board removeBoard(int i) {
        return boards.remove(i);
    }

    //MODIFIES: this
    //EFFECTS: removes first board with name from boards
    public Board removeBoard(String name) {
        for (Board b : boards) {
            if (name.equals(b.getName())) {
                EventLog.getInstance().logEvent(new Event(
                        "Board \"" + b.getName() + "\" was deleted and removed from list."));
                boards.remove(b);
                return b;
            }
        }
        return null;
    }

    //EFFECTS: returns a JSON array of all the boards
    public JSONArray toJson() {
        JSONArray jsonArray = new JSONArray();

        for (Board b : boards) {
            jsonArray.put(b.toJson());
        }

        return jsonArray;
    }

    //EFFECTS: returns iterator over boards
    public Iterator<Board> iterator() {
        return boards.iterator();
    }

    // EFFECTS: returns board with name, if none are found return null.
    public Board getBoard(String name) {
        for (Board b : boards) {
            if (name.equals(b.getName())) {
                return b;
            }
        }
        return null;
    }

    //getters

    public Board getBoard(int i) {
        return boards.get(i);
    }

    public boolean isBoardListEmpty() {
        return boards.isEmpty();
    }

    public List<Board> getBoards() {
        return boards;
    }

}
