package model;

import model.pieces.Piece;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestCheckScanner {

    private CheckScanner checkScanner;
    private Board board;
    String rookCheck;
    String bishopCheck;
    String knightCheck;
    String queenCheckDiagonal;
    String queenCheckLine;
    String pawnCheck;
    String doubleCheck;
    String checkMate;

    @BeforeEach
    void setup() {
        board = new Board();
        checkScanner = new CheckScanner(board);
        rookCheck = "rnbqkbn1/pppppppp/8/4r3/8/8/PPPP1PPP/RNBQKBNR w KQq - 0 1";

        bishopCheck = "rnbqkbnr/ppp1pppp/8/1B6/8/8/PPPP1PPP/RNBQKBNR b KQq - 0 1";
        knightCheck = "rnbqkbnr/pppppppp/8/8/8/3n4/PPPPPPPP/RNBQKBNR w KQq - 0 1";
        queenCheckDiagonal = "rnbqkbnr/pppppppp/8/q7/8/8/PPP1PPPP/RNBQKBNR w KQq - 0 1";
        queenCheckLine = "rnbqkbnr/pppppppp/8/8/4q3/8/PPPP1PPP/RNBQKBNR w KQq - 0 1";
        pawnCheck = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPpPP/RNBQKBNR w KQq - 0 1";
        doubleCheck = "rnbqkbnr/pppppppp/8/8/4r3/3n4/PPPP1PPP/RNBQKBNR w KQq - 0 1";
        checkMate = "rnbqkbnr/pppppppp/8/8/8/4p3/PPPPPpPP/RNBQKBNR w KQq - 0 1";
    }

    @Test
    void testKingNotInCheck() {
        Piece pawn = board.getPiece(0,6);
        Move move = new Move(board,pawn,0,4);
        assertFalse(checkScanner.isKingChecked(move));
    }

    @Test
    void testKingInRookCheck() {
        board.loadPositionFromFen(rookCheck);
        Piece pawn = board.getPiece(0,6);
        Move move = new Move(board,pawn,0,4);
        assertTrue(checkScanner.isKingChecked(move));
    }

    @Test
    void testKingInBishopCheck() {
        board.loadPositionFromFen(bishopCheck);
        Piece pawn = board.getPiece(0,1);
        Move move = new Move(board,pawn,0,3);
        assertTrue(checkScanner.isKingChecked(move));
    }

    @Test
    void testKingInKnightCheck() {
        board.loadPositionFromFen(knightCheck);
        Piece pawn = board.getPiece(0,6);
        Move move = new Move(board,pawn,0,4);
        assertTrue(checkScanner.isKingChecked(move));
    }

    @Test
    void testKingInQueenCheckDiagonal() {
        board.loadPositionFromFen(queenCheckDiagonal);
        Piece pawn = board.getPiece(0,6);
        Move move = new Move(board,pawn,0,4);
        assertTrue(checkScanner.isKingChecked(move));
    }

    @Test
    void testKingInQueenCheckLine() {
        board.loadPositionFromFen(queenCheckLine);
        Piece pawn = board.getPiece(0,6);
        Move move = new Move(board,pawn,0,4);
        assertTrue(checkScanner.isKingChecked(move));
    }

    @Test
    void testKingInPawnCheck() {
        board.loadPositionFromFen(pawnCheck);
        Piece pawn = board.getPiece(0,6);
        Move move = new Move(board,pawn,0,4);
        assertTrue(checkScanner.isKingChecked(move));
    }

    @Test
    void testKingDoubleCheck() {
        board.loadPositionFromFen(doubleCheck);
        Piece pawn = board.getPiece(0,6);
        Move move = new Move(board,pawn,0,4);
        assertTrue(checkScanner.isKingChecked(move));
    }

    @Test
    void testKingCheckMated() {
        board.loadPositionFromFen(checkMate);
        Piece king = board.findKing(true);
        assertTrue(checkScanner.isGameOver(king));
    }
}