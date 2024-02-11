package model;

/*
Represents a list of boards, which is what will allow to user to play multiple games simultaneously.

 */

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

    // !!! this should throw an exception
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

    public Board removeBoard(int i) {
        return boards.remove(i);
    }

    public ArrayList<Board> getBoards() {
        return boards;
    }


}
