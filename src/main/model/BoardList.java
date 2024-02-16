package model;

// Represents a list of boards. The list can be arbitrarily long.


//TODO: getters and removers should throw exceptions

import java.util.ArrayList;

public class BoardList {

    private ArrayList<Board> boards;

    // EFFECTS: creates new board list with no boards
    public BoardList() {
        boards = new ArrayList<Board>();
    }

    public void addBoard(Board b) {
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
    //EFFECTS: removes board with name from boards
    public Board removeBoard(String name) {
        Board removedBoard = null;
        for (Board b : boards) {
            if (name.equals(b.getName())) {
                removedBoard = removeBoard(b);
                break;
            }
        }
        return removedBoard;
    }

    // getters

    // EFFECTS: returns board with name
    public Board getBoard(String name) {
        for (Board b : boards) {
            if (name.equals(b.getName())) {
                return b;
            }
        }
        return new Board();
    }

    public Board getBoard(int i) {
        return boards.get(i);
    }

    public ArrayList<Board> getBoards() {
        return boards;
    }

    public boolean isBoardListEmpty() {
        return boards.isEmpty();
    }
}
