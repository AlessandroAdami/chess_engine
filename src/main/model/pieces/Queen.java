package model.pieces;

import model.Board;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;

public class Queen extends Piece {

    BufferedImage whiteQueen;
    BufferedImage blackQueen;

    public Queen(Board board, int col, int row, boolean isWhite) {
        super(board);
        this.col = col;
        this.row = row;
        this.xPos = col * board.tileSize;
        this.yPos = row * board.tileSize;
        this.isWhite = isWhite;
        this.name = "Queen";

        try {
            whiteQueen = ImageIO.read(new File("./data/whiteQueen.png"));
            blackQueen = ImageIO.read(new File("./data/blackQueen.png"));
        } catch (Exception e) {
            e.printStackTrace();
        }

        this.sprite = isWhite ? whiteQueen : blackQueen;

        this.sprite = this.sprite.getScaledInstance(board.tileSize, board.tileSize,BufferedImage.SCALE_SMOOTH);
    }

    public boolean isValidMovement(int col, int row) {
        return this.col == col || this.row == row || Math.abs(this.col - col) == Math.abs(this.row - row);
    }

    public boolean moveCollidesWithPiece(int col, int row) {
        if (this.col == col || this.row == row) {

            if (this.col > col) { //moving left
                for (int c = this.col - 1; c > col; c--) {
                    if (board.getPiece(c, this.row) != null)
                        return true;
                }
            }
            if (this.col < col) { //moving right
                for (int c = this.col + 1; c < col; c++) {
                    if (board.getPiece(c, this.row) != null)
                        return true;
                }
            }
            if (this.row > row) { //moving up
                for (int r = this.row - 1; r > row; r--) {
                    if (board.getPiece(this.col, r) != null)
                        return true;
                }
            }
            if (this.row < row) { //moving down
                for (int r = this.row + 1; r < row; r++) {
                    if (board.getPiece(this.col, r) != null)
                        return true;
                }
            }
        } else {
            //up left
            if (this.col > col && this.row > row) {
                for (int i = 1; i < Math.abs(this.col - col); i++) {
                    if (board.getPiece(this.col - i,this.row - i) != null) {
                        return true;
                    }
                }
            }
            //up right
            if (this.col < col && this.row > row) {
                for (int i = 1; i < Math.abs(this.col - col); i++) {
                    if (board.getPiece(this.col + i,this.row - i) != null) {
                        return true;
                    }
                }
            }
            //down left
            if (this.col > col && this.row < row) {
                for (int i = 1; i < Math.abs(this.col - col); i++) {
                    if (board.getPiece(this.col - i,this.row + i) != null) {
                        return true;
                    }
                }
            }
            //down right
            if (this.col < col && this.row < row) {
                for (int i = 1; i < Math.abs(this.col - col); i++) {
                    if (board.getPiece(this.col + i,this.row + i) != null) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}