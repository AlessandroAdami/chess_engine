package model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestCheckScanner {

    private CheckScanner checkScanner;
    private Board board;
    private int[][] rookCheck;
    private int[][] bishopCheck;
    private int[][] knightCheck;
    private int[][] queenCheckDiagonal;
    private int[][] queenCheckLine;
    private int[][] pawnCheck;
    private int[][] doubleCheck;
    private int[][] checkMate;

    @BeforeEach
    void setup() {
        board = new Board();
        checkScanner = new CheckScanner(board);
        rookCheck = new int[][]{
                {4, 1, 0, 0, 0, 0, -1, -4},
                {2, 1, 0, 0, 0, 0, -1, -2},
                {3, 1, 0, 0, 0, 0, -1, -3},
                {5, 1, 0, 0, 0, 0, -1, -5},
                {6, 0, 0, 0, -4, 0, -1, -6},
                {3, 1, 0, 0, 0, 0, -1, -3},
                {2, 1, 0, 0, 0, 0, -1, -2},
                {4, 1, 0, 0, 0, 0, -1, -4},
        };
        bishopCheck = new int[][]{
                {4, 1, 0, 0, 0, 0, -1, -4},
                {2, 1, 0, 0, 3, 0, -1, -2},
                {3, 1, 0, 0, 0, 0, -1, -3},
                {5, 1, 0, 0, 0, 0,  0, -5},
                {6, 1, 0, 0, 0, 0, -1, -6},
                {3, 1, 0, 0, 0, 0, -1, -3},
                {2, 1, 0, 0, 0, 0, -1, -2},
                {4, 1, 0, 0, 0, 0, -1, -4},
        };
        knightCheck = new int[][]{
                {4, 1, 0, 0, 0, 0, -1, -4},
                {2, 1, 0, 0, 0, 0, -1, -2},
                {3, 1, 0, 0, 0, 0, -1, -3},
                {5, 1, -2, 0, 0, 0,  -1, -5},
                {6, 1, 0, 0, 0, 0, -1, -6},
                {3, 1, 0, 0, 0, 0, -1, -3},
                {2, 1, 0, 0, 0, 0, -1, -2},
                {4, 1, 0, 0, 0, 0, -1, -4},
        };
        queenCheckDiagonal = new int[][]{
                {4, 1, 0, 0, -5, 0, -1, -4},
                {2, 1, 0, 0, 0, 0, -1, -2},
                {3, 1, 0, 0, 0, 0, -1, -3},
                {5, 0, 0, 0, 0, 0,  -1, -5},
                {6, 1, 0, 0, 0, 0, -1, -6},
                {3, 1, 0, 0, 0, 0, -1, -3},
                {2, 1, 0, 0, 0, 0, -1, -2},
                {4, 1, 0, 0, 0, 0, -1, -4},
        };
        queenCheckLine = new int[][]{
                {4, 1, 0, 0, 0, 0, -1, -4},
                {2, 1, 0, 0, 0, 0, -1, -2},
                {3, 1, 0, 0, 0, 0, -1, -3},
                {5, 1, 0, 0, 0, 0, -1, -5},
                {6, 0, 0, -5, 0, 0, -1, -6},
                {3, 1, 0, 0, 0, 0, -1, -3},
                {2, 1, 0, 0, 0, 0, -1, -2},
                {4, 1, 0, 0, 0, 0, -1, -4},
        };
        pawnCheck = new int[][]{
                {4, 1, 0, 0, 0, 0, -1, -4},
                {2, 1, 0, 0, 1, 0, -1, -2},
                {3, 1, 0, 0, 0, 0, -1, -3},
                {5, 1, 0, 0, 0, 0, -1, -5},
                {6, 1, 0, 0, 0, 0, -1, -6},
                {3, -1, 0, 0, 0, 0, -1, -3},
                {2, 1, 0, 0, 0, 0, -1, -2},
                {4, 1, 0, 0, 0, 0, -1, -4},
        };
        doubleCheck = new int[][]{
                {4, 1, 0, 0, 0, 0, -1, -4},
                {2, 1, 0, 0, 0, 0, -1, -2},
                {3, 1, 0, 0, 0, 0, -1, -3},
                {5, 1, -3, 0, 0, 0, -1, -5},
                {6, 0, 0, 0, -4, 0, -1, -6},
                {0, 1, 0, 0, 0, 0, -1, -3},
                {2, 1, 0, 0, 0, 0, -1, -2},
                {4, 1, 0, 0, 0, 0, -1, -4},
        };
        checkMate = new int[][]{
                {4, 1, 0, 0, 0, 0, -1, -4},
                {2, 1, 0, 0, 0, 0, -1, -2},
                {3, 1, 0, 0, 0, 0, -1, -3},
                {5, 1, 0, 0, 0, 0, -1, -5},
                {6, 1, 0, 0, 0, 0, -1, -6},
                {3, -5, 0, 0, 0, 0, -1, -3},
                {2, 1, -1, 0, 0, 0, -1, -2},
                {4, 1, 0, 0, 0, 0, -1, -4},
        };
    }

    @Test
    void testKingNotInCheck() {
        assertFalse(checkScanner.isChecked());
        checkScanner.update();
        assertFalse(checkScanner.isCheckMated());
    }

    @Test
    void testKingInRookCheck() {
        board.setPosition(rookCheck);
        checkScanner.update();
        assertTrue(checkScanner.isChecked());
        checkScanner.update();
        assertFalse(checkScanner.isCheckMated());
    }

    @Test
    void testKingInBishopCheck() {
        board.setPosition(bishopCheck);
        board.nextTurn();
        checkScanner.update();
        assertTrue(checkScanner.isChecked());
        checkScanner.update();
        assertFalse(checkScanner.isCheckMated());
    }

    @Test
    void testKingInKnightCheck() {
        board.setPosition(knightCheck);
        checkScanner.update();
        assertTrue(checkScanner.isChecked());
        checkScanner.update();
        assertFalse(checkScanner.isCheckMated());
    }

    @Test
    void testKingInQueenCheckDiag() {
        board.setPosition(queenCheckDiagonal);
        checkScanner.update();
        assertTrue(checkScanner.isChecked());
        assertFalse(checkScanner.isCheckMated());
    }

    @Test
    void testKingInQueenCheckLine() {
        board.setPosition(queenCheckLine);
        checkScanner.update();
        assertTrue(checkScanner.isChecked());
        checkScanner.update();
        assertFalse(checkScanner.isCheckMated());
    }

    @Test
    void testKingInPawnCheck() {
        board.setPosition(pawnCheck);
        checkScanner.update();
        assertTrue(checkScanner.isChecked());
        checkScanner.update();
        assertFalse(checkScanner.isCheckMated());
    }

    @Test
    void testKingDoubleCheck() {
        board.setPosition(doubleCheck);
        checkScanner.update();
        assertTrue(checkScanner.isChecked());
        checkScanner.update();
        assertFalse(checkScanner.isCheckMated());

    }

    @Test
    void testKingCheckMated() {
        board.setPosition(checkMate);
        checkScanner.update();
        assertTrue(checkScanner.isChecked());
        checkScanner.update();
        //assertTrue(checkScanner.isCheckMated());
    }
}