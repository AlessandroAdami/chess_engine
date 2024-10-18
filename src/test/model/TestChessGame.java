package model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for the ChessGame class
 */
public class TestChessGame {

    ChessGame chessGame;
    ChessGame chessGame0;
    Board b0;
    Board b1;
    BoardList list;

    @BeforeEach
    void setup() {
        b0 = new Board("Board 0");
        b1 = new Board("Board 1");
        list = new BoardList();
        list.addBoard(b0);
        list.addBoard(b1);
        chessGame = new ChessGame();
        chessGame.setBoards(list);
        chessGame.setCurrentBoard(b0);
        chessGame0 = new ChessGame(list,b0);
    }

    @Test
    void testChessGame() {
        assertTrue(chessGame.getCurrentBoard().getIsWhiteToMove());
        assertEquals("Board 0", chessGame.getCurrentBoard().getName());
        assertTrue(chessGame.getBoards().getBoard(0).getIsWhiteToMove());
        assertEquals("Board 0", chessGame.getBoards().getBoard(0).getName());
        assertTrue(chessGame.getBoards().getBoard(1).getIsWhiteToMove());
        assertEquals("Board 1", chessGame.getBoards().getBoard(1).getName());

        assertTrue(chessGame0.getCurrentBoard().getIsWhiteToMove());
        assertEquals("Board 0", chessGame0.getCurrentBoard().getName());
        assertTrue(chessGame0.getBoards().getBoard(0).getIsWhiteToMove());
        assertEquals("Board 0", chessGame0.getBoards().getBoard(0).getName());
        assertTrue(chessGame0.getBoards().getBoard(1).getIsWhiteToMove());
        assertEquals("Board 1", chessGame0.getBoards().getBoard(1).getName());
    }
}
