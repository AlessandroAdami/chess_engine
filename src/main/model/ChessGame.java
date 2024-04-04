package model;

import org.json.JSONObject;

public class ChessGame {

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

    //getters
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
