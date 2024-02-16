package model.test;

import model.Board;
import model.BoardList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class BoardListTest {

    Board b0; //starting board
    Board b1; //empty board
    Board b2; //white king on e4
    Board b3; //white queen on e4
    Board b4; //black knight on e4
    Board b5; //black bishop on e5
    BoardList boardList;

    @BeforeEach
    public void setup() {
        b0 = new Board();
        b1 = new Board(); b1.clearBoard();
        b2 = new Board(); b2.clearBoard();
        b3 = new Board(); b3.clearBoard();
        b4 = new Board(); b4.clearBoard();
        b5 = new Board(); b5.clearBoard();
        boardList = new BoardList();
        b2.placePiece(4,3,6);
        b3.placePiece(4,3,5);
        b4.placePiece(4,3,-2);
        b5.placePiece(4,4,-3);
        b0.setName("starting");
        b1.setName("empty");
        b2.setName("King");
        b3.setName("Queen");
        b4.setName("Knight");
        b5.setName("Bishop");
        boardList.addBoard(b0);
        boardList.addBoard(b1);
        boardList.addBoard(b2);
        boardList.addBoard(b3);
        boardList.addBoard(b4);
        boardList.addBoard(b5);
    }

    @Test
    public void testRemoveBoardBoard() {
        assertEquals(b0,boardList.removeBoard("starting"));
        assertEquals(b1,boardList.removeBoard("empty"));
        assertEquals(4,boardList.getBoards().size());
        assertEquals(b5,boardList.removeBoard("Bishop"));
        assertEquals(3,boardList.getBoards().size());
        assertEquals(b2,boardList.removeBoard("King"));
        assertEquals(b3,boardList.removeBoard("Queen"));
        assertEquals(b4,boardList.removeBoard("Knight"));
        assertEquals(0,boardList.getBoards().size());
    }

    @Test
    public void testRemoveBoardStringName() {
        assertEquals(b0,boardList.removeBoard(b0));
        assertEquals(b1,boardList.removeBoard(b1));
        assertEquals(4,boardList.getBoards().size());
        assertEquals(b5,boardList.removeBoard(b5));
        assertEquals(3,boardList.getBoards().size());
        assertEquals(b2,boardList.removeBoard(b2));
        assertEquals(b3,boardList.removeBoard(b3));
        assertEquals(b4,boardList.removeBoard(b4));
        assertEquals(0,boardList.getBoards().size());
    }

    @Test
    public void testGetBoard() {
        assertEquals(b0,boardList.getBoard("starting"));
    }
}
