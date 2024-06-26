package model;

import model.exceptions.NoPieceException;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

//Manages pieces' images and number values. Note: black must have *-1 value of white pieces.

public class Piece {

    private List<ImageIcon> pieces;
    private int scale;
    private ImageIcon whitePawn;
    private ImageIcon blackPawn;
    private ImageIcon whiteKnight;
    private ImageIcon blackKnight;
    private ImageIcon whiteBishop;
    private ImageIcon blackBishop;
    private ImageIcon whiteRook;
    private ImageIcon blackRook;
    private ImageIcon whiteQueen;
    private ImageIcon blackQueen;
    private ImageIcon whiteKing;
    private ImageIcon blackKing;
    public static final int WHITE   = 1;
    public static final int BLACK   = -1;
    public static final int NONE    = 0;
    public static final int PAWN    = 1;
    public static final int KNIGHT  = 2;
    public static final int BISHOP  = 3;
    public static final int ROOK    = 4;
    public static final int QUEEN   = 5;
    public static final int KING    = 6;

    //EFFECTS: retrieves the images of the pieces and rescales them
    public Piece(int scale) {
        this.scale = scale;
        this.pieces = new ArrayList<>();
        createPieces();
        addPieces();
        scaleImages();
    }

    //MODIFIES: this
    //EFFECTS: gets the images of the pieces
    private void createPieces() {
        whitePawn   = new ImageIcon("./data/whitePawn.png");
        whiteKnight = new ImageIcon("./data/whiteKnight.png");
        whiteBishop = new ImageIcon("./data/whiteBishop.png");
        whiteRook   = new ImageIcon("./data/whiteRook.png");
        whiteQueen  = new ImageIcon("./data/whiteQueen.png");
        whiteKing   = new ImageIcon("./data/whiteKing.png");
        blackPawn   = new ImageIcon("./data/blackPawn.png");
        blackKnight = new ImageIcon("./data/blackKnight.png");
        blackBishop = new ImageIcon("./data/blackBishop.png");
        blackRook   = new ImageIcon("./data/blackRook.png");
        blackQueen  = new ImageIcon("./data/blackQueen.png");
        blackKing   = new ImageIcon("./data/blackKing.png");
    }

    //MODIFIES: this
    //EFFECTS: adds pieces to the list
    private void addPieces() {
        pieces.add(whitePawn);
        pieces.add(whiteKnight);
        pieces.add(whiteBishop);
        pieces.add(whiteRook);
        pieces.add(whiteQueen);
        pieces.add(whiteKing);
        pieces.add(blackPawn);
        pieces.add(blackKnight);
        pieces.add(blackBishop);
        pieces.add(blackRook);
        pieces.add(blackQueen);
        pieces.add(blackKing);
    }

    //MODIFIES: this
    //EFFECTS: scales all the images
    private void scaleImages() {
        for (ImageIcon icon : pieces) {
            Image oldImage = icon.getImage();
            Image scaledImage = oldImage.getScaledInstance(scale, scale, Image.SCALE_SMOOTH);
            icon.setImage(scaledImage);
        }
    }

    //EFFECTS: returns an ImageIcon of piece
    public ImageIcon getPieceImage(int piece) throws NoPieceException {
        if (piece == 0) {
            throw new NoPieceException();
        }
        switch (piece) {
            case  1: return whitePawn;
            case  2: return whiteKnight;
            case  3: return whiteBishop;
            case  4: return whiteRook;
            case  5: return whiteQueen;
            case  6: return whiteKing;
            case -1: return blackPawn;
            case -2: return blackKnight;
            case -3: return blackBishop;
            case -4: return blackRook;
            case -5: return blackQueen;
            case -6: return blackKing;
            default: throw new NoPieceException();
        }
    }
}
