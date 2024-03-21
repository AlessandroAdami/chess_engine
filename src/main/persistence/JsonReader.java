package persistence;

// A reader that reads ChessGame from JSON data stored in file

import model.Board;
import model.BoardList;
import model.ChessGame;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Stream;

public class JsonReader {
    private String source;

    // EFFECTS: constructs a reader to read from source file
    public JsonReader(String source) {
        this.source = source;
    }

    // EFFECTS: reads chess-game from file and returns it;
    // throws IOException if an error occurs reading data from file
    public ChessGame read() throws IOException {
        String jsonData = readFile(source);
        JSONObject jsonObject = new JSONObject(jsonData);
        return parseChessGame(jsonObject);
    }

    // EFFECTS: reads source file as string and returns it
    private String readFile(String source) throws IOException {
        StringBuilder contentBuilder = new StringBuilder();

        try (Stream<String> stream = Files.lines(Paths.get(source), StandardCharsets.UTF_8)) {
            stream.forEach(contentBuilder::append);
        }

        return contentBuilder.toString();
    }

    //EFFECTS: parses chess game from JSON object and returns it
    private ChessGame parseChessGame(JSONObject jsonObject) {
        ChessGame cg = new ChessGame();

        addCurrentBoard(cg,jsonObject);
        addBoardList(cg,jsonObject);

        return cg;
    }

    //MODIFIES: cg
    //EFFECTS: adds current board to chess game
    private void addCurrentBoard(ChessGame cg, JSONObject jsonObject) {
        JSONObject currentJsonBoard = jsonObject.getJSONObject("currentBoard");
        Board currentBoard = parseBoard(currentJsonBoard);
        cg.setCurrentBoard(currentBoard);
    }

    //MODIFIES: cg
    // EFFECTS: adds board list to chess game
    private void addBoardList(ChessGame cg, JSONObject jsonObject) {
        BoardList bl = new BoardList();
        addBoards(bl, jsonObject);
        cg.setBoards(bl);
    }

    // MODIFIES: bl
    // EFFECTS: parses boards from JSON object and adds them to board list
    private void addBoards(BoardList bl, JSONObject jsonObject) {
        JSONArray jsonArray = jsonObject.getJSONArray("boards");
        for (Object json : jsonArray) {
            JSONObject nextBoard = (JSONObject) json;
            addBoard(bl, nextBoard);
        }
    }

    //MODIFIES: bl
    //EFFECTS: adds parsed board to bl
    private void addBoard(BoardList bl, JSONObject board) {
        bl.addBoard(parseBoard(board));
    }

    //TODO: change parseBoard to create a new check-scanner
    //      (and eventually bot) for the board

    // MODIFIES: bl
    // EFFECTS: returns parsed board to board list
    private Board parseBoard(JSONObject jsonObject) {
        String name = jsonObject.getString("name");
        JSONArray jsonPosition = jsonObject.getJSONArray("position");
        boolean isWhitesTurn = jsonObject.getBoolean("isWhitesTurn");
        String canWhiteCastle = jsonObject.getString("canWhiteCastle");
        String canBlackCastle = jsonObject.getString("canBlackCastle");

        int[][] position = toPositionArray(jsonPosition);
        Board board = new Board(name);
        board.setPosition(position);
        board.setIsWhitesTurn(isWhitesTurn);
        board.setCanWhiteCastle(canWhiteCastle);
        board.setCanBlackCastle(canBlackCastle);

        return board;
    }

    //EFFECTS: returns the JSONArray as a (board) position array
    private int[][] toPositionArray(JSONArray jsonPositionArray) {
        int[][] position = new int[8][8];
        int[] numArray = toNumArray(jsonPositionArray);
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                position[i][j] = numArray[(i * 8) + j];
            }
        }

        return position;
    }

    //EFFECTS: returns the JSONArray as an integer array
    private int[] toNumArray(JSONArray jsonPositionArray) {
        int[] numArray = new int[64];
        for (int i = 0; i < 64; i++) {
            numArray[i] = jsonPositionArray.getInt(i);
        }
        return numArray;
    }

}
