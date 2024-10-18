package model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestCheckScanner {

    private CheckScanner checkScanner;
    private Board Board;
    String rookCheck;
    String bishopCheck;
    String knightCheck;
    String queenCheckDiagonal;
    String queenCheckLine;
    String pawnCheck;
    String doubleCheck;
    String checkMate;

    //TODO: perhaps check for whose turn it is

    @BeforeEach
    void setup() {
        Board = new Board();
        checkScanner = new CheckScanner(Board);
        rookCheck = "rnbqkbn1/pppppppp/8/4r3/8/8/PPPP1PPP/RNBQKBNR w KQq - 0 1";

        bishopCheck = "rnbqkbnr/ppp1pppp/8/1B6/8/8/PPPP1PPP/RNBQKBNR w KQq - 0 1";
        knightCheck = "rnbqkbnr/pppppppp/8/8/8/3n4/PPPPPPPP/RNBQKBNR w KQq - 0 1";
        queenCheckDiagonal = "rnbqkbnr/pppppppp/8/q7/8/8/PPP1PPPP/RNBQKBNR w KQq - 0 1";
        queenCheckLine = "rnbqkbnr/pppppppp/8/8/4q3/8/PPPP1PPP/RNBQKBNR w KQq - 0 1";
        pawnCheck = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPpPP/RNBQKBNR w KQq - 0 1";
        doubleCheck = "rnbqkbnr/pppppppp/8/8/4r3/3n4/PPPP1PPP/RNBQKBNR w KQq - 0 1";
        checkMate = "rnbqkbnr/pppppppp/8/8/8/4p3/PPPPPpPP/RNBQKBNR w KQq - 0 1";
    }

    @Test
    void testKingNotInCheck() {
        //TODO
    }

    @Test
    void testKingInRookCheck() {
        //TODO
    }

    @Test
    void testKingInBishopCheck() {
        //TODO
    }

    @Test
    void testKingInKnightCheck() {
        //TODO
    }

    @Test
    void testKingInQueenCheckDiag() {
        //TODO
    }

    @Test
    void testKingInQueenCheckLine() {
        //TODO
    }

    @Test
    void testKingInPawnCheck() {
        //TODO
    }

    @Test
    void testKingDoubleCheck() {
        //TODO
    }

    @Test
    void testKingCheckMated() {
        //TODO
    }
}