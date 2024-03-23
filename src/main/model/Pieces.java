package model;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

//Creates a list of images of the pieces in this order:
// 0-5 white
// 6-11 black
// pawn-rook-knight-bishop-queen-king


public class Pieces {

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

    public Pieces(int scale) {
        this.scale = scale;
        this.pieces = new ArrayList<>();
        createPieces();
        addPieces();
        scaleImages();
    }

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

    private void scaleImages() {
        for (int i = 0; i < 11; i++) {
            ImageIcon icon = pieces.get(i);
            Image oldImage = icon.getImage();
            Image scaledImage = oldImage.getScaledInstance(scale,scale, Image.SCALE_SMOOTH);
            icon.setImage(scaledImage);
        }
    }

    public ImageIcon getPieceImage(int piece) {
        switch (piece) {
            case 1: return whitePawn;
            case 2: return whiteKnight;
            case 3: return whiteBishop;
            case 4: return whiteRook;
            case 5: return whiteQueen;
            case 6: return whiteKing;
            case -1: return blackPawn;
            case -2: return blackKnight;
            case -3: return blackBishop;
            case -4: return blackRook;
            case -5: return blackQueen;
            default: return blackKing;
        }
    }

}
