package persistence;

import model.Board;
import model.BoardList;
import model.ChessGame;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

public class TestJsonReader {
    Board newBoard;

    @BeforeEach
    public void setup() {
        newBoard = new Board();
    }

    @Test
    void testReaderNonExistentFile() {
        JsonReader reader = new JsonReader("./data/noSuchFile.json");
        try {
            ChessGame chessGame = reader.read();
            fail("IOException expected");
        } catch (IOException e) {
            // pass
        }
    }

    @Test
    void testReaderNewChessGame() {
        JsonReader reader = new JsonReader("./data/readerNewChessGameTest.json");
        try {
            ChessGame cg = reader.read();
            Board currentBoard = cg.getCurrentBoard();
            BoardList boards = cg.getBoards();

            assertTrue(currentBoard.samePosition(newBoard));
            assertEquals("New Board", currentBoard.getName());
            assertTrue(currentBoard.getIsWhiteToMove());
            assertEquals(1, boards.getBoards().size());
        } catch (IOException e) {
            fail("Couldn't read from file");
        }
    }

    @Test
    void testReaderComplexChessGame() {
        JsonReader reader = new JsonReader("./data/readerComplexChessGameTest.json");
        try {
            ChessGame cg = reader.read();
            Board currentBoard = cg.getCurrentBoard();
            BoardList boards = cg.getBoards();
            Board board2 = boards.getBoard(1);

            assertTrue(currentBoard.samePosition(newBoard));
            assertEquals("New Board", currentBoard.getName());
            assertTrue(currentBoard.getIsWhiteToMove());
            assertEquals(2, boards.getBoards().size());

            assertTrue(board2.samePosition(newBoard));
            assertEquals("Board 2", board2.getName());


        } catch (IOException e) {
            fail("Couldn't read from file");
        }
    }
}
