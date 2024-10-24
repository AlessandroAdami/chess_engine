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
        //white king on e4 that can move anywhere, black king on b4 can move anywhere
        board.loadPositionFromFen("8/8/7P/8/1k2K3/8/8/8 w - - 0 1");
        //up
        Piece king = board.findKing(true);
        Move Ke5 = new Move(board,king,4,3);
        assertTrue(board.isValidMove(Ke5));
        board.makeMove(Ke5);
        // down
        king = board.findKing(false);
        Move Kb3 = new Move(board,king,2,5);
        assertTrue(board.isValidMove(Kb3));
        board.makeMove(Kb3);
        //diagonal
        king = board.findKing(true);
        Move Kf6 = new Move(board,king,5,2);
        assertTrue(board.isValidMove(Kf6));
    }

    @Test
    void testIsValidQueenMove(){
        board.loadPositionFromFen("1k6/8/8/2q5/4Q3/8/8/5K2 w - - 0 1");
        //up
        Piece wQueen = board.getPiece(4,4);
        Move Qe7 = new Move(board,wQueen,4,1);
        assertTrue(board.isValidMove(Qe7));
        board.makeMove(Qe7);
        //down
        Piece bQueen = board.getPiece(2,3);
        Move Qc2 = new Move(board,bQueen,2,6);
        assertTrue(board.isValidMove(Qc2));
        board.makeMove(Qc2);
        //diagonal
        Move Qa3 = new Move(board,wQueen,0,5);
        assertTrue(board.isValidMove(Qa3));
    }

    @Test
    void testIsValidKnightMove() {
        board.loadPositionFromFen("1k6/7P/8/2n5/4N3/8/8/5K2 w - - 0 1");
        //up one right two
        Piece wKnight= board.getPiece(4,4);
        Move Ng5 = new Move(board,wKnight,6,3);
        assertTrue(board.isValidMove(Ng5));
        board.makeMove(Ng5);
        //down two left one
        Piece bKnight = board.getPiece(2,3);
        Move Nb3 = new Move(board,bKnight,1,5);
        assertTrue(board.isValidMove(Nb3));
        board.makeMove(Nb3);
        // up two left one
        Move Nf7 = new Move(board,wKnight,5,1);
        assertTrue(board.isValidMove(Nf7));
    }

    @Test
    void testIsValidBishopMove() {

        board.loadPositionFromFen("1k6/7P/8/2b5/4B3/8/8/5K2 w - - 0 1");
        //up right
        Piece wBishop = board.getPiece(4,4);
        Move Bg6 = new Move(board,wBishop,6,2);
        assertTrue(board.isValidMove(Bg6));
        board.makeMove(Bg6);
        // down left
        Piece bBishop = board.getPiece(2,3);
        Move Ba3 = new Move(board,bBishop,0,5);
        assertTrue(board.isValidMove(Ba3));
        board.makeMove(Ba3);
        // up left
        Move Be8 = new Move(board,wBishop,4,0);
        assertTrue(board.isValidMove(Be8));
    }

    @Test
    void testIsValidRookMove() {
        board.loadPositionFromFen("1k6/8/8/2r5/4R3/8/8/5K2 w - - 0 1");
        //up
        Piece wRook = board.getPiece(4,4);
        Move Re7 = new Move(board,wRook,4,1);
        assertTrue(board.isValidMove(Re7));
        board.makeMove(Re7);
        //down
        Piece bRook = board.getPiece(2,3);
        Move Rc2 = new Move(board,bRook,2,6);
        assertTrue(board.isValidMove(Rc2));
        board.makeMove(Rc2);
        // right
        Move Rh7 = new Move(board,wRook,7,1);
        assertTrue(board.isValidMove(Rh7));
    }
}
