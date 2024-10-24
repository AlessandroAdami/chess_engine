package model;

import model.pieces.Piece;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
/**
 * Unit tests for Board class
 */
public class TestBoard {

    Board board = new Board("board name");

    @Test
    void testGetName() {
        assertEquals("board name",board.getName());
    }

    @Test
    void testEvaluatePos() {
        assertEquals(0,board.evaluatePos());
        board.loadPositionFromFen("r1bqkbnr/pppp1ppp/n3P3/8/8/8/PPP2PPP/RNBQK1NR w KQkq - 0 1");
        assertEquals(-3,board.evaluatePos());
        board.loadPositionFromFen("r2qkbn1/pppp1ppp/8/8/4P3/2N5/P1P2PPP/R1BQKB1R w KQq - 0 1");
        assertEquals(7,board.evaluatePos());
    }

    @Test
    void testIsValidPawnMove(){
        //push twice first move
        board = new Board("board name");
        Piece e2Pawn = board.getPiece(4,6);
        Move e4 = new Move(board,e2Pawn,4,4);
        assertTrue(board.isValidMove(e4));
        board.makeMove(e4);
        //push one square first move
        Piece e7Pawn = board.getPiece(4,1);
        Move e6 = new Move(board,e7Pawn,4,2);
        assertTrue(board.isValidMove(e6));
        //enPassant
        board.loadPositionFromFen("rnbqkbnr/ppp1p1pp/8/3pPp2/8/8/PPPP1PPP/RNBQKBNR w KQkq f6 0 3");
        Piece e5Pawn = board.getPiece(4,3);
        Move exf6 = new Move(board,e5Pawn,5,2);
        assertTrue(board.isValidMove(exf6));
        //capture
        board.loadPositionFromFen("rnbqkbnr/ppp1pppp/8/3p4/4P3/8/PPPP1PPP/RNBQKBNR w KQkq - 0 2");
        Piece e4Pawn = board.getPiece(4,4);
        Move exd5 = new Move(board,e4Pawn,3,3);
        assertTrue(board.isValidMove(exd5));
    }

    @Test
    void testIsValidKingMove() {
        //TODO
    }

    @Test
    void testIsValidQueenMove(){
        //TODO
    }

    @Test
    void testIsValidKnightMove() {
        //TODO
    }

    @Test
    void testIsValidBishopMove() {
        //TODO
    }

    @Test
    void testIsValidRookMove() {
        //TODO
    }

    @Test
    void testMakeMove() {
        //TODO
    }

    @Test
    void testUpdateCastling() {
        //TODO
    }

}
