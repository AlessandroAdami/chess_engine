package model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
/**
 * Unit tests for Board class
 */
public class TestBoard {

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

    Board b8;

    @BeforeEach
    void setup() {
        b0 = new Board("starting");
        b1 = new Board();
        b2 = new Board("king");
        b3 = new Board("queen");
        b4 = new Board("knight");
        b5 = new Board("bishop");
        b6 = new Board("rook");
        b7 = new Board("pawns");
        b8 = new Board("promotion");

        //update boards using fen strings

    }

    @Test
    void testGetName() {
        assertEquals("starting",b0.getName());
        assertEquals("New Board",b1.getName());
    }

    @Test
    void testIsEmpty() {
        assertFalse(b0.isEmpty());
        assertTrue(b1.isEmpty());
    }

    @Test
    void testClearBoard() {
        //TODO
    }

    @Test
    void testEvaluatePos() {
        //TODO
    }

    @Test
    void testIsLegalPawnMove(){
        //TODO
    }

    @Test
    void testIsLegalKingMove() {
        //TODO
    }

    @Test
    void testIsLegalQueenMove(){
        //TODO
    }

    @Test
    void testIsLegalKnightMove() {
        //TODO
    }

    @Test
    void testIsLegalBishopMove() {
        //TODO
    }

    @Test
    void testIsLegalRookMove() {
        //TODO

    }

    @Test
    void testIsLegalMove() {
        //TODO
    }

    @Test
    void testMovePiece() {
        //TODO
    }

    @Test
    void testBoardToStringBoard() {
        //TODO
    }

    @Test
    void testIsSamePosition() {
        //TODO
    }

    @Test
    void testIsPawnToPromote() {
        //TODO
    }

    @Test
    void testUpdateWhiteCastling() {
        //TODO
    }

    @Test
    void testUpdateBlackCastling() {
        //TODO
    }

}
