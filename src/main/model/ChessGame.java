package model;

import org.json.JSONObject;

import java.util.Iterator;

//A chess game with a current board and a list of boards

public class ChessGame implements Iterable<Board> {

    private BoardList boards;
    private Board currentBoard;

    //EFFECTS: constructs a chess game with an empty list and a new board
    public ChessGame() {
        this.boards = new BoardList();
        this.currentBoard = new Board();
        boards.addBoard(currentBoard);
    }

    //REQUIRES: currentBoards is in boardList
    //EFFECTS: constructs a chess game with a list of boards and a current board
    public ChessGame(BoardList boardList, Board currentBoard) {
        this.boards = boardList;
        this.currentBoard = currentBoard;
    }

    //EFFECTS: adds board to game
    public void addBoard(Board b) {
        boards.addBoard(b);
    }

    //EFFECTS: returns chess game as a json object
    public JSONObject toJson() {
        JSONObject json = new JSONObject();
        json.put("currentBoard", currentBoard.toJson());
        json.put("boards", boards.toJson());
        return json;
    }

    //EFFECTS: returns iterator over boards
    @Override
    public Iterator<Board> iterator() {
        return boards.iterator();
    }

    //EFFECTS: removes and returns the board if it is in the game
    public Board removeBoard(String boardName) {
        return boards.removeBoard(boardName);
    }

    //EFFECTS: true if there are no boards
    public boolean isBoardListEmpty() {
        return boards.isBoardListEmpty();
    }

    //getters

    //EFFECTS: gets board at index i
    public Board getBoard(int i) {
        return boards.getBoard(i);
    }

    public BoardList getBoards() {
        return boards;
    }

    public Board getCurrentBoard() {
        return currentBoard;
    }

    public void setBoards(BoardList boards) {
        this.boards = boards;
    }

    public void setCurrentBoard(Board b) {
        this.currentBoard = b;
    }
}
