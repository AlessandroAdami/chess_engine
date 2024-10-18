package model.pieces;

import model.Board;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;

public class Bishop extends Piece {

    BufferedImage whiteBishop;
    BufferedImage blackBishop;

    public Bishop(Board board, int col, int row, boolean isWhite) {
        super(board);
        this.col = col;
        this.row = row;
        this.xPos = col * board.tileSize;
        this.yPos = row * board.tileSize;
        this.isWhite = isWhite;
        this.name = "Bishop";

        try {
            whiteBishop = ImageIO.read(new File("./data/whiteBishop.png"));
            blackBishop = ImageIO.read(new File("./data/blackBishop.png"));
        } catch (Exception e) {
            e.printStackTrace();
        }

        this.sprite = isWhite ? whiteBishop : blackBishop;

        this.sprite = this.sprite.getScaledInstance(board.tileSize, board.tileSize,BufferedImage.SCALE_SMOOTH);
    }

    public boolean isValidMovement(int col, int row) {
        return Math.abs(this.col - col) == Math.abs(this.row - row);
    }

    public boolean moveCollidesWithPiece(int col, int row) {
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
        return false;
    }
}