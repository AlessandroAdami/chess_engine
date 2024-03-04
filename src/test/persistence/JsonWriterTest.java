package persistence;

import model.Board;
import model.BoardList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;


import model.ChessGame;

import java.io.IOException;

public class JsonWriterTest {

    ChessGame newChessGame;
    ChessGame complexChessGame;
    Board newBoard;
    Board board2;

    @BeforeEach
    public void setup() {
        newChessGame = new ChessGame();
        complexChessGame = new ChessGame();
        newBoard = new Board();
        board2 = new Board();
        board2.setName("Board 2");
        board2.setCanWhiteCastle("KR");
        board2.setCanBlackCastle("RK");
        board2.setIsWhitesTurn(false);
        complexChessGame.addBoard(board2);
    }

    @Test
    public void testWriterInvalidFile() {
        try {
            JsonWriter writer = new JsonWriter("./data/my\0illegal:fileName.json");
            writer.open();
            fail("IOException was expected");
        } catch (IOException e) {
            // pass
        }
    }

    @Test
    public void testWriterNewChessGame() {
        try {
            JsonWriter writer = new JsonWriter("./data/writerNewChessGameTest.json");
            writer.open();
            writer.write(newChessGame);
            writer.close();

            JsonReader reader = new JsonReader("./data/writerNewChessGameTest.json");
            newChessGame = reader.read();
            Board currentBoard = newChessGame.getCurrentBoard();
            BoardList boards = newChessGame.getBoards();

            assertTrue(currentBoard.samePosition(newBoard.getPosition()));
            assertEquals("New Board", currentBoard.getName());
            assertTrue(currentBoard.getIsWhitesTurn());
            assertEquals("RKR", currentBoard.getCanWhiteCastle());
            assertEquals("RKR", currentBoard.getCanBlackCastle());
            assertEquals(1, boards.getBoards().size());
        } catch (IOException e) {
            fail("Exception should not have been thrown");
        }
    }

    @Test
    public void testWriterComplexChessGame() {
        try {
            JsonWriter writer = new JsonWriter("./data/writerComplexChessGameTest.json");
            writer.open();
            writer.write(complexChessGame);
            writer.close();

            JsonReader reader = new JsonReader("./data/writerComplexChessGameTest.json");
            ChessGame cg = reader.read();
            Board currentBoard = cg.getCurrentBoard();
            BoardList boards = cg.getBoards();
            Board board2 = boards.getBoard(1);

            assertTrue(currentBoard.samePosition(newBoard.getPosition()));
            assertEquals("New Board", currentBoard.getName());
            assertTrue(currentBoard.getIsWhitesTurn());
            assertEquals("RKR", currentBoard.getCanWhiteCastle());
            assertEquals("RKR", currentBoard.getCanBlackCastle());
            assertEquals(2, boards.getBoards().size());

            assertTrue(board2.samePosition(newBoard.getPosition()));
            assertEquals("Board 2", board2.getName());
            assertFalse(board2.getIsWhitesTurn());
            assertEquals("KR", board2.getCanWhiteCastle());
            assertEquals("RK", board2.getCanBlackCastle());

        } catch (IOException e) {
            fail("Exception should not have been thrown");
        }
    }
}
