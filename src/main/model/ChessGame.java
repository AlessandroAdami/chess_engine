package model;

import org.json.JSONObject;
import persistence.JsonReader;
import persistence.JsonWriter;

import java.io.IOException;
import java.util.Iterator;

//A chess game with a current board and a list of boards

public class ChessGame implements Iterable<CompressedBoard> {

    private BoardList boards;
    private Board currentBoard;
    private static final String JSON_STORE = "./data/chessgame.json";
    private final JsonWriter jsonWriter;
    private final JsonReader jsonReader;

    //EFFECTS: constructs a chess game with an empty list and a new board
    public ChessGame() {
        this.boards = new BoardList();
        this.currentBoard = new Board();
        boards.addBoard(currentBoard);
        jsonReader = new JsonReader(JSON_STORE);
        jsonWriter = new JsonWriter(JSON_STORE);
    }

    //REQUIRES: currentBoards is in boardList
    //EFFECTS: constructs a chess game with a list of boards and a current board
    public ChessGame(BoardList boardList, Board currentBoard) {
        this.boards = boardList;
        this.currentBoard = currentBoard;
        jsonReader = new JsonReader(JSON_STORE);
        jsonWriter = new JsonWriter(JSON_STORE);
    }

    //EFFECTS: adds board to game
    public void addBoard(Board b) {
        boards.addBoard(b);
    }

    public void addBoard(CompressedBoard b) {
        boards.addBoard(b);
    }

    //EFFECTS: returns chess game as a json object
    public JSONObject toJson() {
        JSONObject json = new JSONObject();
        json.put("currentBoard", currentBoard.toJson());
        json.put("boards", boards.toJson());
        return json;
    }

    //EFFECTS: saves current chess game to file
    public void saveChessGame() {
        try {
            jsonWriter.open();
            jsonWriter.write(this);
            jsonWriter.close();
            EventLog.getInstance().logEvent(new Event("Games saved to " + JSON_STORE));
        } catch (IOException e) {
            EventLog.getInstance().logEvent(new Event("Unable to write to file: " + JSON_STORE));
        }
    }

    //MODIFIES: this
    //EFFECTS: loads chess game from file
    public void loadChessGame() {
        try {
            ChessGame game = jsonReader.read();
            this.boards = game.getBoards();
            this.currentBoard = game.getCurrentBoard();
            EventLog.getInstance().logEvent(new Event("Games loaded from " + JSON_STORE));

        } catch (IOException e) {
            EventLog.getInstance().logEvent(new Event("Unable to write to file: " + JSON_STORE));
        }
    }

    //EFFECTS: returns iterator over boards
    @Override
    public Iterator<CompressedBoard> iterator() {
        return boards.iterator();
    }

    //getters

    public CompressedBoard getBoard(String name) {
        return boards.getBoard(name);
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
        saveCurrentBoard();
        this.currentBoard = b;
    }

    private void saveCurrentBoard() {
        String name = currentBoard.getName();
        for (CompressedBoard b : boards) {
            if (b.getName().equals(name)) {
                b.load(currentBoard);
            }
        }
    }
}
