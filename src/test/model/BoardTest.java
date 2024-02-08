package model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class BoardTest {

    Board b0; //starting board
    Board b1; //empty board
    Board b2; //white king on e4
    Board b3; //white queen on e4
    Board b4; //black knight on e4
    Board b5; //black night & bishop on e4,e5
    Board b6; //black rooks on a1,a8
    Board b7; //white pawns on d4,e4

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
        b5.placePiece(4,3,-2);
        b5.placePiece(4,4,-3);
        b6.placePiece(0,0,-4);
        b6.placePiece(7,0,-4);
        b7.placePiece(3,3,1);
        b7.placePiece(4,3,1);

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
        assertEquals(-6,b5.evaluatePos());
        assertEquals(-10,b6.evaluatePos());
        assertEquals(2,b7.evaluatePos());
    }

    @Test
    public void testIsLegalMove() {
        assertTrue(b0.isLegalMove(1,1,1,3));
        assertTrue(b0.isLegalMove(1,0,2,2));
        assertTrue(b0.isLegalMove(6,7,5,5));
        assertTrue(b2.isLegalMove(4,3,5,4));
        assertTrue(b2.isLegalMove(4,3,3,2));
        assertTrue(b2.isLegalMove(4,3,3,4));
        assertTrue(b3.isLegalMove(4,3,0,7));
        assertTrue(b3.isLegalMove(4,3,4,0));
        assertTrue(b3.isLegalMove(4,3,7,3));
        assertTrue(b3.isLegalMove(4,3,7,6));
        assertTrue(b4.isLegalMove(4,3,5,5));
        assertTrue(b4.isLegalMove(4,3,2,2));
        assertTrue(b4.isLegalMove(4,3,2,4));
        assertTrue(b5.isLegalMove(4,4,7,7));
        assertTrue(b5.isLegalMove(4,4,0,0));
        assertTrue(b5.isLegalMove(4,3,7,1));
        assertTrue(b6.isLegalMove(0,0,7,0));
        assertTrue(b6.isLegalMove(0,7,0,1));
        assertFalse(b0.isLegalMove(4,0,7,6));
        assertFalse(b1.isLegalMove(6,3,7,6));
        assertFalse(b2.isLegalMove(4,3,5,5));
        assertFalse(b4.isLegalMove(4,3,1,0));
        assertFalse(b5.isLegalMove(4,4,2,2));
        assertFalse(b6.isLegalMove(0,0,0,7));
    }

}
