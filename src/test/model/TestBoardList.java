package model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for BoardList class
 */
public class TestBoardList {

    CompressedBoard b0 = new CompressedBoard("Zero");
    CompressedBoard b1 = new CompressedBoard("One");
    Board board0 = b0.getBoard();
    Board board1 = b1.getBoard();
    BoardList boardList = new BoardList();

    @BeforeEach
    public void setup() {
        boardList.addBoard(b0);
        boardList.addBoard(b1);
    }

    @Test
    public void testRemoveBoardString() {
        Board board0 = b0.getBoard();
        Board board1 = b1.getBoard();
        assertEquals(board0,boardList.removeBoard("Zero"));
        assertEquals(board1,boardList.removeBoard("One"));
        assertNull(boardList.removeBoard("NoBoard"));
        assertEquals(0,boardList.getBoards().size());
    }

    @Test
    public void testRemoveBoardBoard() {
        assertEquals(board0,boardList.removeBoard(board0));
        assertEquals(board1,boardList.removeBoard(board1));
        Board boardNotListed = new Board();
        assertNull(boardList.removeBoard(boardNotListed));
        assertEquals(0,boardList.getBoards().size());
    }

    @Test
    public void testRemoveBoardInt() {
        assertEquals(board0,boardList.removeBoard(0));
        assertEquals(board1,boardList.removeBoard(0));
        assertEquals(0,boardList.getBoards().size());
    }

    @Test
    public void testGetBoardString() {
        assertEquals(board0,boardList.getBoard("Zero"));
        assertEquals(board1,boardList.getBoard("One"));
    }

    @Test
    public void testGetBoardInt() {
        assertEquals(board0,boardList.getBoard(0));
        assertEquals(board1,boardList.getBoard(1));
    }

    @Test
    public void testGetBoards() {
        ArrayList<CompressedBoard> boardArrayList = new ArrayList<CompressedBoard>();
        boardArrayList.add(b0);
        boardArrayList.add(b1);
        assertEquals(boardArrayList,boardList.getBoards());
    }

    @Test
    public void testIsBoardListEmpty() {
        assertFalse(boardList.isBoardListEmpty());
        boardList.removeBoard(0);
        assertFalse(boardList.isBoardListEmpty());
        boardList.removeBoard(0);
        assertTrue(boardList.isBoardListEmpty());
    }
}
