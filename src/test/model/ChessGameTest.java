package model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ChessGameTest {

    ChessGame chessGame;
    Board b0;
    Board b1;
    BoardList list;

    @BeforeEach
    void setup() {
        chessGame = new ChessGame();
        b0 = new Board("Board 0");
        b1 = new Board("Board 1");
        list = new BoardList();
        list.addBoard(b0);
        list.addBoard(b1);
        chessGame.setBoards(list);
        chessGame.setCurrentBoard(b0);
    }

    @Test
    void testChessGame() {
        assertTrue(chessGame.getCurrentBoard().getIsWhitesTurn());
        assertEquals("Board 0", chessGame.getCurrentBoard().getName());
        assertTrue(chessGame.getBoards().getBoard(0).getIsWhitesTurn());
        assertEquals("Board 0", chessGame.getBoards().getBoard(0).getName());
        assertTrue(chessGame.getBoards().getBoard(1).getIsWhitesTurn());
        assertEquals("Board 1", chessGame.getBoards().getBoard(1).getName());
    }
}
