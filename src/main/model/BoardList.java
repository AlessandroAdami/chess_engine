package model;

// Represents a list of boards manager. The list can be arbitrarily long.
// Boards are listed in the order they are added to the list.


//TODO: getters and removers should throw exceptions

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class BoardList {

    private ArrayList<Board> boards;

    // EFFECTS: creates new board list with no boards
    public BoardList() {
        boards = new ArrayList<Board>();
    }

    public void addBoard(Board b) {
        EventLog.getInstance().logEvent(new Event("Board \"" + b.getName() + "\" was added to list."));
        boards.add(b);
    }

    //MODIFIES: this
    //EFFECTS: removes board b from list
    public Board removeBoard(Board b) {
        if (boards.remove(b)) {
            EventLog.getInstance().logEvent(new Event("Board \"" + b.getName() + "\" was added to list."));
            return b;
        } else {
            return null;
        }
    }

    //REQUIRES: int is not out of index for boards
    //MODIFIES: this
    //EFFECTS: removes board at index i from list
    public Board removeBoard(int i) {
        if (boards.size() < i) {
            EventLog.getInstance().logEvent(new Event(
                    "Board \"" + boards.get(i).getName() + "\" was removed."));
        }
        return boards.remove(i);
    }

    //MODIFIES: this
    //EFFECTS: removes first board with name from boards
    public Board removeBoard(String name) {
        Board removedBoard = null;
        for (Board b : boards) {
            if (name.equals(b.getName())) {
                EventLog.getInstance().logEvent(new Event(
                        "Board \"" + b.getName() + "\" was deleted and removed from list."));
                removedBoard = removeBoard(b);
                break;
            }
        }
        return removedBoard;
    }

    //EFFECTS: returns a JSON array of all the boards
    public JSONArray toJson() {
        JSONArray jsonArray = new JSONArray();

        for (Board b : boards) {
            jsonArray.put(b.toJson());
        }

        return jsonArray;
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

    public ArrayList<Board> getBoards() {
        return boards;
    }

}
