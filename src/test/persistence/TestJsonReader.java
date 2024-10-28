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
            assertEquals("New Game", currentBoard.getName());
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

            newBoard.loadPositionFromFen("r2qkb1r/pb1n1p2/2p1pP2/1p4B1/2pP4/2N5/PP3PPP/R2QKB1R w KQkq - 1 12");
            assertTrue(currentBoard.samePosition(newBoard));
            assertEquals("game 1", currentBoard.getName());
            assertTrue(currentBoard.getIsWhiteToMove());
            assertEquals(2, boards.getBoards().size());

            newBoard.loadPositionFromFen("r1bqkb1r/1p1n1ppp/p2ppn2/6B1/3NPP2/2N2Q2/PPP3PP/R3KB1R b KQkq - 2 8");
            assertTrue(board2.samePosition(newBoard));
            assertFalse(board2.getIsWhiteToMove());
            assertEquals("game 2", board2.getName());

        } catch (IOException e) {
            fail("Couldn't read from file");
        }
    }
}
