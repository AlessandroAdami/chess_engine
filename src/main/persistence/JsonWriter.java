package persistence;

// A writer that writes JSON representation of ChessGame to file
// Note: the JSON object saves the board as a 1D int[64] array
//       instead of a 2D int[8][8] array


import model.BoardList;
import model.ChessGame;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

public class JsonWriter {
    private static final int indentFactor = 4;
    private PrintWriter writer;
    private String destination;

    // EFFECTS: constructs a writer to write to destination file
    public JsonWriter(String destination) {
        this.destination = destination;
    }

    // MODIFIES: this
    // EFFECTS: opens writer; throws FileNotFoundException
    // if destination file cannot be opened for writing

    public void open() throws FileNotFoundException {
        writer = new PrintWriter(new File(destination));
    }

    // MODIFIES: this
    // EFFECTS: writes JSON representation of ChessGame to file
    public void write(ChessGame cg) {
        JSONObject json = cg.toJson();
        saveToFile(json.toString(indentFactor));
    }

    // MODIFIES: this
    // EFFECTS: closes writer
    public void close() {
        writer.close();
    }

    // MODIFIES: this
    // EFFECTS: writes string to file
    private void saveToFile(String json) {
        writer.print(json);
    }
}
