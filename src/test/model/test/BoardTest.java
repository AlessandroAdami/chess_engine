package model.test;

import model.Board;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class BoardTest {

    Board b0; //starting board
    Board b1; //empty board
    Board b2; //white king on e4
    Board b3; // this board:
    /*
                {0, 0, 0, 0, 0, 0, 0, 0},
                {0, r, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, r, 0, r, 0},
                {0, 0, 0, 0, 0, 0, 0, 0},
                {0, r, 0, 0, Q, 0, r, 0},
                {0, 0, 0, 0, 0, r, 0, 0},
                {0, 0, r, 0, r, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0},
     */
    Board b4; //black knight on e4
    Board b5; //this board:
    /*
                {0, 0, 0, 0, 0, 0, 0, 0},
                {0, R, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, R, 0},
                {0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, b, 0, 0, 0},
                {0, 0, 0, 0, 0, R, 0, 0},
                {0, 0, R, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0},
     */
    Board b6; //this board:
    /*
                {0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, P, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0},
                {0, P, 0, 0, r, 0, P, 0},
                {0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, P, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0},
     */
    Board b7; //this board:
    /*
                {0, 0, 0, 0, 0, 0, 0, 0},
                {p, 0, p, 0, 0, 0, 0, 0},
                {0, 0, P, 0, 0, 0, 0, 0},
                {0, 0, 0, r, 0, p, 0, 0},
                {0, 0, 0, 0, P, 0, R, 0},
                {0, 0, p, 0, 0, 0, 0, 0},
                {P, 0, P, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0},
     */

    @BeforeEach
    public void setup() {
        b0 = new Board("starting");
        b1 = new Board(); b1.clearBoard();
        b2 = new Board("king"); b2.clearBoard();
        b3 = new Board("queen"); b3.clearBoard();
        b4 = new Board("knight"); b4.clearBoard();
        b5 = new Board("bishop"); b5.clearBoard();
        b6 = new Board("rook"); b6.clearBoard();
        b7 = new Board("pawns"); b7.clearBoard();

        b2.placePiece(4,3,6);

        b3.placePiece(4,3,5);
        b3.placePiece(1,3,-4);
        b3.placePiece(1,6,-4);
        b3.placePiece(2,1,-4);
        b3.placePiece(4,1,-4);
        b3.placePiece(4,5,-4);
        b3.placePiece(5,2,-4);
        b3.placePiece(6,3,-4);
        b3.placePiece(6,5,-4);

        b4.placePiece(4,3,-2);
        b4.nextTurn();

        b5.placePiece(4,3,-3);
        b5.placePiece(1,6,4);
        b5.placePiece(2,1,4);
        b5.placePiece(5,2,4);
        b5.placePiece(6,5,4);
        b5.nextTurn();

        b6.placePiece(4,3,-4);
        b6.placePiece(1,3,1);
        b6.placePiece(4,1,1);
        b6.placePiece(4,6,1);
        b6.placePiece(6,3,1);
        b6.nextTurn();

        b7.placePiece(0,1,1);
        b7.placePiece(2,1,1);
        b7.placePiece(2,4,1);
        b7.placePiece(4,3,1);
        b7.placePiece(6,3,4);
        b7.placePiece(0,6,-1);
        b7.placePiece(2,2,-1);
        b7.placePiece(2,6,-1);
        b7.placePiece(5,4,-1);
        b7.placePiece(3,4,-4);
    }

    @Test
    public void testGetName() {
        assertEquals("starting",b0.getName());
        assertEquals("New Board",b1.getName());
    }

    @Test
    public void testIsEmpty() {
        assertFalse(b0.isEmpty());
        assertTrue(b1.isEmpty());
    }

    @Test
    public void testClearBoard() {
        assertEquals(0,b1.getPosition()[0][0]);
        assertEquals(0,b1.getPosition()[1][2]);
        assertEquals(0,b1.getPosition()[6][7]);
        assertEquals(0,b1.getPosition()[7][7]);
    }

    @Test
    public void testEvaluatePos() {
        assertEquals(0,b0.evaluatePos());
        assertEquals(0,b1.evaluatePos());
        assertEquals(105,b2.evaluatePos());
        assertEquals(-31,b3.evaluatePos());
        assertEquals(-3,b4.evaluatePos());
        assertEquals(17,b5.evaluatePos());
        assertEquals(-1,b6.evaluatePos());
    }

    @Test
    public void testIsLegalPawnMove() {
        assertTrue(b7.isLegalMove(0,1,0,2));
        assertTrue(b7.isLegalMove(0,1,0,3));
        assertTrue(b7.isLegalMove(4,3,3,4));
        assertTrue(b7.isLegalMove(4,3,5,4));
        assertFalse(b7.isLegalMove(2,1,2,2));
        assertFalse(b7.isLegalMove(2,1,2,3));
        assertFalse(b7.isLegalMove(2,1,3,2));
        b7.nextTurn();
        assertTrue(b7.isLegalMove(0,6,0,5));
        assertTrue(b7.isLegalMove(0,6,0,4));
        assertTrue(b7.isLegalMove(2,6,2,5));
        assertTrue(b7.isLegalMove(5,4,4,3));
        assertTrue(b7.isLegalMove(5,4,6,3));
        assertFalse(b7.isLegalMove(2,6,2,4));
    }

    @Test
    public void testIsLegalKingMove() {
        assertTrue(b2.isLegalMove(4,3,3,2));
        assertTrue(b2.isLegalMove(4,3,4,4));
        assertTrue(b2.isLegalMove(4,3,5,3));
        assertFalse(b2.isLegalMove(4,3,3,7));
        assertFalse(b2.isLegalMove(4,3,6,4));
        assertFalse(b2.isLegalMove(4,3,6,5));
    }

    @Test
    public void testIsLegalQueenMove() {
        assertTrue(b3.isLegalMove(4,3,1,3));
        assertTrue(b3.isLegalMove(4,3,1,6));
        assertTrue(b3.isLegalMove(4,3,2,1));
        assertTrue(b3.isLegalMove(4,3,2,5));
        assertTrue(b3.isLegalMove(4,3,4,2));
        assertTrue(b3.isLegalMove(4,3,4,5));
        assertTrue(b3.isLegalMove(4,3,5,2));
        assertTrue(b3.isLegalMove(4,3,6,3));
        assertTrue(b3.isLegalMove(4,3,6,5));
        assertFalse(b3.isLegalMove(4,3,0,3));
        assertFalse(b3.isLegalMove(4,3,0,7));
        assertFalse(b3.isLegalMove(4,3,1,0));
        assertFalse(b3.isLegalMove(4,3,4,0));
        assertFalse(b3.isLegalMove(4,3,4,6));
        assertFalse(b3.isLegalMove(4,3,6,1));
        assertFalse(b3.isLegalMove(4,3,7,0));
        assertFalse(b3.isLegalMove(4,3,7,3));
        assertFalse(b3.isLegalMove(4,3,7,6));
    }

    @Test
    public void testIsLegalKnightMove() {
        b0.nextTurn();
        assertTrue(b0.isLegalMove(6,7,5,5));
        assertTrue(b4.isLegalMove(4,3,2,2));
        assertTrue(b4.isLegalMove(4,3,2,4));
        assertTrue(b4.isLegalMove(4,3,3,1));
        assertTrue(b4.isLegalMove(4,3,3,5));
        assertTrue(b4.isLegalMove(4,3,5,1));
        assertTrue(b4.isLegalMove(4,3,5,5));
        assertTrue(b4.isLegalMove(4,3,6,2));
        assertTrue(b4.isLegalMove(4,3,6,4));
        assertFalse(b4.isLegalMove(4,3,0,0));
    }

    @Test
    public void testIsLegalBishopMove() {
        assertTrue(b5.isLegalMove(4,3,1,6));
        assertTrue(b5.isLegalMove(4,3,2,1));
        assertTrue(b5.isLegalMove(4,3,2,5));
        assertTrue(b5.isLegalMove(4,3,5,2));
        assertTrue(b5.isLegalMove(4,3,5,4));
        assertTrue(b5.isLegalMove(4,3,6,5));
        assertFalse(b5.isLegalMove(4,3,0,7));
        assertFalse(b5.isLegalMove(4,3,1,0));
        assertFalse(b5.isLegalMove(4,3,6,1));
        assertFalse(b5.isLegalMove(4,3,7,0));
        assertFalse(b5.isLegalMove(4,3,7,6));
    }

    @Test
    public void testIsLegalRookMove() {
        assertTrue(b6.isLegalMove(4, 3, 1, 3));
        assertTrue(b6.isLegalMove(4, 3, 4, 1));
        assertTrue(b6.isLegalMove(4, 3, 4, 5));
        assertTrue(b6.isLegalMove(4, 3, 4, 6));
        assertTrue(b6.isLegalMove(4, 3, 6, 3));
        assertFalse(b6.isLegalMove(4, 3, 0, 3));
        assertFalse(b6.isLegalMove(4, 3, 4, 0));
        assertFalse(b6.isLegalMove(4, 3, 4, 7));
        assertFalse(b6.isLegalMove(4, 3, 7, 3));
    }


    @Test
    public void testIsLegalMove() {
        assertFalse(b0.isLegalMove(4,3,0,0));
        assertFalse(b0.isLegalMove(6,6,6,4));
        assertFalse(b0.isLegalMove(0,0,0,1));
        b0.nextTurn();
        assertFalse(b0.isLegalMove(1,1,1,3));
        assertFalse(b0.isLegalMove(6,6,6,6));
    }

    @Test
    public void testMovePiece() {
        assertTrue(b0.getIsWhitesTurn());
        b0.movePiece(1,1,1,3);
        assertEquals(0,b0.getPiece(1,1));
        assertEquals(1,b0.getPiece(1,3));
        assertFalse(b0.getIsWhitesTurn());
        b0.movePiece(0,1,0,3);
        assertEquals(1,b0.getPiece(0,1));
        assertEquals(0,b0.getPiece(0,3));
        assertFalse(b0.getIsWhitesTurn());
        b0.movePiece(0,6,0,4);
        assertEquals(0,b0.getPiece(0,6));
        assertEquals(-1,b0.getPiece(0,4));
        assertTrue(b0.getIsWhitesTurn());
        b0.movePiece(1,3,0,4);
        assertFalse(b0.getIsWhitesTurn());
        assertEquals(0,b0.getPiece(1,3));
        assertEquals(1,b0.getPiece(0,4));
    }

    @Test
    public void testBoardToStringBoard() {
        assertEquals("0",b1.boardToStringBoard()[0][0]);
        assertEquals("0",b1.boardToStringBoard()[7][7]);
        assertEquals("k",b0.boardToStringBoard()[0][4]);
        assertEquals("q",b0.boardToStringBoard()[0][3]);
        assertEquals("R",b0.boardToStringBoard()[7][7]);
        assertEquals("P",b0.boardToStringBoard()[6][7]);
        assertEquals("N",b0.boardToStringBoard()[7][1]);
        assertEquals("b",b0.boardToStringBoard()[0][2]);
    }
}
