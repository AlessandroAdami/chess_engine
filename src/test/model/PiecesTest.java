package model;

import model.exceptions.NoPieceException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ui.Pieces;

import javax.swing.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

public class PiecesTest {

    private Pieces pieces;
    private static final int SCALE = 100;

    @BeforeEach
    void setup() {
        pieces = new Pieces(SCALE);
    }

    @Test
    void testConstructor() {
        assertEquals(SCALE, pieces.getScale());
        assertEquals(12,pieces.getPieces().size());
    }

    @Test
    void testGetPieceImageEmptySquare() {
        try {
            pieces.getPieceImage(0);
            fail("Expected NoPieceException");
        } catch (NoPieceException e) {
            //pass
        }
    }

    @Test
    void testGetPieceImageInvalidInteger() {
        try {
            pieces.getPieceImage(12);
            fail("Expected NoPieceException");
        } catch (NoPieceException e) {
            //pass
        }
    }

    @Test
    void testGetPieceImageValidInteger() {
        try {
            ImageIcon whitePawn   = pieces.getPieceImage(1);
            ImageIcon blackPawn   = pieces.getPieceImage(-1);
            ImageIcon whiteKnight = pieces.getPieceImage(2);
            ImageIcon blackKnight = pieces.getPieceImage(-2);
            ImageIcon whiteBishop = pieces.getPieceImage(3);
            ImageIcon blackBishop = pieces.getPieceImage(-3);
            ImageIcon whiteRook   = pieces.getPieceImage(4);
            ImageIcon blackRook   = pieces.getPieceImage(-4);
            ImageIcon whiteQueen  = pieces.getPieceImage(5);
            ImageIcon blackQueen  = pieces.getPieceImage(-5);
            ImageIcon whiteKing   = pieces.getPieceImage(6);
            ImageIcon blackKing   = pieces.getPieceImage(-6);

            assertEquals(whitePawn,  pieces.getPieceAtIndex(0));
            assertEquals(blackPawn,  pieces.getPieceAtIndex(6));
            assertEquals(whiteKnight,pieces.getPieceAtIndex(1));
            assertEquals(blackKnight,pieces.getPieceAtIndex(7));
            assertEquals(whiteBishop,pieces.getPieceAtIndex(2));
            assertEquals(blackBishop,pieces.getPieceAtIndex(8));
            assertEquals(whiteRook,  pieces.getPieceAtIndex(3));
            assertEquals(blackRook,  pieces.getPieceAtIndex(9));
            assertEquals(whiteQueen, pieces.getPieceAtIndex(4));
            assertEquals(blackQueen, pieces.getPieceAtIndex(10));
            assertEquals(whiteKing,  pieces.getPieceAtIndex(5));
            assertEquals(blackKing,  pieces.getPieceAtIndex(11));

        } catch (NoPieceException e) {
            fail("Unexpected NoPieceException");
        }
    }

    @Test
    void testScaleImages() {
        List<ImageIcon> pieceList = pieces.getPieces();
        for (ImageIcon img : pieceList) {
            assertEquals(SCALE,img.getIconHeight());
            assertEquals(SCALE,img.getIconWidth());
        }
    }
}
