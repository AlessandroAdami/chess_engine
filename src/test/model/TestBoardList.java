package model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for BoardList class
 */
public class TestBoardList {

    Board b0 = new Board("Zero");
    Board b1 = new Board("One");
    Board b2 = new Board("Two");
    Board b3 = new Board("Three");
    Board b4 = new Board("Four");
    Board b5 = new Board("Five");
    BoardList boardList = new BoardList();

    @BeforeEach
    public void setup() {
        boardList.addBoard(b0);
        boardList.addBoard(b1);
        boardList.addBoard(b2);
        boardList.addBoard(b3);
        boardList.addBoard(b4);
        boardList.addBoard(b5);
    }

    @Test
    public void testRemoveBoardString() {
        assertEquals(b0,boardList.removeBoard("Zero"));
        assertEquals(b1,boardList.removeBoard("One"));
        assertNull(boardList.removeBoard("NoBoard"));
        assertEquals(4,boardList.getBoards().size());
        assertEquals(b5,boardList.removeBoard("Five"));
        assertEquals(3,boardList.getBoards().size());
        assertEquals(b2,boardList.removeBoard("Two"));
        assertEquals(b3,boardList.removeBoard("Three"));
        assertEquals(b4,boardList.removeBoard("Four"));
        assertEquals(0,boardList.getBoards().size());
    }

    @Test
    public void testRemoveBoardBoard() {
        assertEquals(b0,boardList.removeBoard(b0));
        assertEquals(b1,boardList.removeBoard(b1));
        Board boardNotListed = new Board();
        assertNull(boardList.removeBoard(boardNotListed));
        assertEquals(4,boardList.getBoards().size());
        assertEquals(b5,boardList.removeBoard(b5));
        assertEquals(3,boardList.getBoards().size());
        assertEquals(b2,boardList.removeBoard(b2));
        assertEquals(b3,boardList.removeBoard(b3));
        assertEquals(b4,boardList.removeBoard(b4));
        assertEquals(0,boardList.getBoards().size());
    }

    @Test
    public void testRemoveBoardInt() {
        assertEquals(b0,boardList.removeBoard(0));
        assertEquals(b1,boardList.removeBoard(0));
        assertEquals(4,boardList.getBoards().size());
        assertEquals(b5,boardList.removeBoard(3));
        assertEquals(3,boardList.getBoards().size());
        assertEquals(b2,boardList.removeBoard(0));
        assertEquals(b3,boardList.removeBoard(0));
        assertEquals(b4,boardList.removeBoard(0));
        assertEquals(0,boardList.getBoards().size());
    }

    @Test
    public void testGetBoardString() {
        assertEquals(b0,boardList.getBoard("Zero"));
        assertEquals(b1,boardList.getBoard("One"));
        assertEquals(6,boardList.getBoards().size());
        assertEquals(b5,boardList.getBoard("Five"));
        assertNull(boardList.getBoard("NoBoard"));
        assertEquals(6,boardList.getBoards().size());
        assertEquals(b2,boardList.getBoard("Two"));
        assertEquals(b3,boardList.getBoard("Three"));
        assertEquals(b4,boardList.getBoard("Four"));
        assertEquals(6,boardList.getBoards().size());
    }

    @Test
    public void testGetBoardInt() {
        assertEquals(b0,boardList.getBoard(0));
        assertEquals(b1,boardList.getBoard(1));
        assertEquals(6,boardList.getBoards().size());
        assertEquals(b5,boardList.getBoard(5));
        assertEquals(6,boardList.getBoards().size());
        assertEquals(b2,boardList.getBoard(2));
        assertEquals(b3,boardList.getBoard(3));
        assertEquals(b4,boardList.getBoard(4));
        assertEquals(6,boardList.getBoards().size());
    }

    @Test
    public void testGetBoards() {
        ArrayList<Board> boardArrayList = new ArrayList<Board>();
        boardArrayList.add(b0);
        boardArrayList.add(b1);
        boardArrayList.add(b2);
        boardArrayList.add(b3);
        boardArrayList.add(b4);
        boardArrayList.add(b5);
        assertEquals(boardArrayList,boardList.getBoards());
    }

    @Test
    public void testIsBoardListEmpty() {
        assertFalse(boardList.isBoardListEmpty());
        boardList.removeBoard(0);
        assertFalse(boardList.isBoardListEmpty());
        boardList.removeBoard(0);
        boardList.removeBoard(0);
        assertFalse(boardList.isBoardListEmpty());
        boardList.removeBoard(0);
        boardList.removeBoard(0);
        boardList.removeBoard(0);
        assertTrue(boardList.isBoardListEmpty());
    }
}
