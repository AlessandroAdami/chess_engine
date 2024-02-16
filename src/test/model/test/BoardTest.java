package model.test;

import model.Board;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class BoardTest {

    Board b0; //starting board
    Board b1; //empty board
    Board b2; //white king on e4
    Board b3; //white queen on e4
    Board b4; //black knight on e4
    Board b5; //black bishop on e5
    Board b6; //black rook on e4
    Board b7; // this board:
    /*
                {4, 0, 0, -6, 0, 0, 0, 0},
                {0, -3, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, -3, 0, 0},
                {0, 0, 0, 0, 0, -1, 0, 0},
                {1, 0, 0, 0, 1, 0, 0, 0},
                {0, 0, 0, 0, 0, 3, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0},
                {4, 3, 0, 4, 0, 0, 0, 0},
     */

    @BeforeEach
    public void setup() {
        b0 = new Board();
        b1 = new Board(); b1.clearBoard();
        b2 = new Board(); b2.clearBoard();
        b3 = new Board(); b3.clearBoard();
        b4 = new Board(); b4.clearBoard();
        b5 = new Board(); b5.clearBoard();
        b6 = new Board(); b6.clearBoard();
        b7 = new Board(); b7.clearBoard();
        b2.placePiece(4,3,6);
        b3.placePiece(4,3,5);
        b4.placePiece(4,3,-2);
        b4.nextTurn();
        b5.placePiece(4,4,-3);
        b5.nextTurn();
        b6.placePiece(4,3,-4);
        b6.nextTurn();
        b7.placePiece(4,3,1);
        b7.placePiece(5,4,-1);
        b7.placePiece(5,2,2);
        b7.placePiece(5,5,-2);
        b7.placePiece(3,7,-5);
        b7.placePiece(3,0,4);
        b7.placePiece(0,3,1);
        b7.placePiece(0,0,4);
        b7.placePiece(0,7,4);
        b7.placePiece(1,0,3);
        b7.placePiece(1,6,-3);
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
        assertEquals(9,b3.evaluatePos());
        assertEquals(-3,b4.evaluatePos());
        assertEquals(-3,b5.evaluatePos());
        assertEquals(-5,b6.evaluatePos());
    }

    @Test
    public void testIsLegalPawnMove() {
        assertTrue(b0.isLegalMove(1,1,1,2));
        assertTrue(b0.isLegalMove(1,1,1,3));
        b0.nextTurn();
        assertTrue(b0.isLegalMove(6,6,6,4));
        assertTrue(b0.isLegalMove(4,6,4,5));
        b0.nextTurn();
        assertTrue(b0.isLegalMove(0,1,0,3));
        b0.nextTurn();
        assertTrue(b0.isLegalMove(7,6,7,4));
        assertFalse(b0.isLegalMove(1,1,3,3));
        assertFalse(b0.isLegalMove(0,1,2,3));
        assertFalse(b0.isLegalMove(7,1,5,2));
    }

    @Test
    public void testIsLegalKingMove() {
        assertTrue(b2.isLegalMove(4,3,5,4));
        assertTrue(b2.isLegalMove(4,3,3,2));
        assertTrue(b2.isLegalMove(4,3,3,4));
    }

    @Test
    public void testIsLegalKnightMove() {
        b0.nextTurn();
        assertTrue(b0.isLegalMove(6,7,5,5));
        assertTrue(b4.isLegalMove(4,3,5,5));
        assertTrue(b4.isLegalMove(4,3,2,2));
        assertTrue(b4.isLegalMove(4,3,2,4));
    }

    @Test
    public void testIsLegalBishopMove() {
        assertTrue(b5.isLegalMove(4,4,7,7));
        assertTrue(b5.isLegalMove(4,4,6,6));
        assertTrue(b5.isLegalMove(4,4,0,0));
        assertTrue(b5.isLegalMove(4,4,7,0));
        assertTrue(b5.isLegalMove(4,4,0,7));
    }

    @Test
    public void testIsLegalRookMove() {
        assertTrue(b6.isLegalMove(4, 3, 4, 7));
        assertTrue(b6.isLegalMove(4, 3, 4, 0));
        assertTrue(b6.isLegalMove(4, 3, 7, 3));
        assertTrue(b6.isLegalMove(4, 3, 4, 7));
        assertFalse(b6.isLegalMove(0, 0, 1, 1));
        assertFalse(b6.isLegalMove(1, 1, 2, 7));
        assertFalse(b6.isLegalMove(2, 3, 4, 5));
        assertFalse(b6.isLegalMove(4, 7, 2, 0));
    }

    @Test
    public void testIsLegalQueenMove() {
        assertTrue(b3.isLegalMove(4,3,0,7));
        assertTrue(b3.isLegalMove(4,3,4,0));
        assertTrue(b3.isLegalMove(4,3,7,3));
        assertTrue(b3.isLegalMove(4,3,7,6));
    }
    /*
                {4, 0, 0, -5, 0, 0, 0, 0},
                {0, -3, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, -3, 0, 0},
                {0, 0, 0, 0, 0, -1, 0, 0},
                {1, 0, 0, 0, 1, 0, 0, 0},
                {0, 0, 0, 0, 0, 3, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0},
                {4, 3, 0, 4, 0, 0, 0, 0},
     */

    @Test
    public void testIsLegalMove() {
        assertTrue(b7.isLegalMove(4,3,5,4));
        b7.nextTurn();
        assertTrue(b7.isLegalMove(5,4,4,3));
        b7.nextTurn();
        assertTrue(b7.isLegalMove(5,2,4,4));
        b7.nextTurn();
        assertTrue(b7.isLegalMove(5,5,4,3));
        b7.nextTurn();
        assertTrue(b7.isLegalMove(3,0,3,7));
        b7.nextTurn();
        assertTrue(b7.isLegalMove(3,7,3,0));
        b7.nextTurn();
        assertTrue(b7.isLegalMove(3,0,3,7));
        assertTrue(b7.isLegalMove(1,0,3,2));
        b7.nextTurn();
        assertTrue(b7.isLegalMove(1,6,4,3));
        b7.nextTurn();
        assertTrue(b7.isLegalMove(0,7,3,7));
        b7.nextTurn();
        assertTrue(b7.isLegalMove(3,7,0,7));
        assertTrue(b7.isLegalMove(3,7,0,3));
        b7.nextTurn();
        assertTrue(b7.isLegalMove(4,3,5,4));
        assertFalse(b7.isLegalMove(0,0,0,5));
        b7.nextTurn();
        assertFalse(b7.isLegalMove(0,7,0,1));
        assertFalse(b7.isLegalMove(1,0,7,6));
        assertFalse(b7.isLegalMove(3,7,6,4));
    }


}
