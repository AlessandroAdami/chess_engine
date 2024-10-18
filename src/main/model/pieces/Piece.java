package model.pieces;

import model.Board;


import java.awt.*;

public class Piece {

    public int col, row;
    public int xPos, yPos;

    public boolean isWhite;
    public String name;
    public int value;

    public boolean isFirstMove = true;

    Image sprite;

    Board board;

    public Piece(Board board) {
        this.board = board;
    }

    public boolean isValidMovement(int col,int row) {
        return true;
    }

    public boolean moveCollidesWithPiece(int col,int row) {
        return false;
    }

    public void paint(Graphics2D g2d) {
        g2d.drawImage(sprite,xPos,yPos,null);
    }

    public String getName() {
        return name;
    }
}
