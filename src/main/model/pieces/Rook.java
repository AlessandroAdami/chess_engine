package model.pieces;

import model.Board;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.File;

public class Rook extends Piece {

    BufferedImage whiteRook;
    BufferedImage blackRook;

    public Rook(Board board, int col, int row, boolean isWhite) {
        super(board);
        this.col = col;
        this.row = row;
        this.xPos = col * board.tileSize;
        this.yPos = row * board.tileSize;
        this.isWhite = isWhite;
        this.name = "Rook";

        try {
            whiteRook = ImageIO.read(new File("./data/whiteRook.png"));
            blackRook = ImageIO.read(new File("./data/blackRook.png"));
        } catch (Exception e) {
            e.printStackTrace();
        }

        this.sprite = isWhite ? whiteRook : blackRook;

        this.sprite = this.sprite.getScaledInstance(board.tileSize, board.tileSize,BufferedImage.SCALE_SMOOTH);
    }

    public boolean isValidMovement(int col, int row) {
        return (this.col == col || this.row == row);
    }

    public boolean moveCollidesWithPiece(int col, int row) {
        if (this.col > col) { //moving left
            for (int c = this.col - 1; c > col; c--) {
                if (board.getPiece(c,this.row) != null)
                    return true;
            }
        }
        if (this.col < col) { //moving right
            for (int c = this.col + 1; c < col; c++) {
                if (board.getPiece(c,this.row) != null)
                    return true;
            }
        }
        if (this.row > row) { //moving up
            for (int r = this.row - 1; r > row; r--) {
                if (board.getPiece(this.col,r) != null)
                    return true;
            }
        }
        if (this.row < row) { //moving down
            for (int r = this.row + 1; r < row; r++) {
                if (board.getPiece(this.col,r) != null)
                    return true;
            }
        }


        return false;
    }
}