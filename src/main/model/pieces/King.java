package model.pieces;

import model.Board;
import model.Move;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;

public class King extends Piece {

    BufferedImage whiteKing;
    BufferedImage blackKing;

    public King(Board board, int col, int row, boolean isWhite) {
        super(board);
        this.col = col;
        this.row = row;
        this.xPos = col * board.tileSize;
        this.yPos = row * board.tileSize;
        this.isWhite = isWhite;
        this.name = "King";

        try {
            whiteKing = ImageIO.read(new File("./data/whiteKing.png"));
            blackKing = ImageIO.read(new File("./data/blackKing.png"));
        } catch (Exception e) {
            e.printStackTrace();
        }

        this.sprite = isWhite ? whiteKing : blackKing;

        this.sprite = this.sprite.getScaledInstance(board.tileSize, board.tileSize,BufferedImage.SCALE_SMOOTH);
    }

    public boolean isValidMovement(int col, int row) {
        return Math.abs((this.col - col) * (this.row - row)) == 1 || Math.abs(col - this.col) + Math.abs(row - this.row) == 1 || canCastle(col,row);
    }

    private boolean canCastle(int col, int row) {

        if (this.row == row) {
            if (col == 6) {
                Piece rook = board.getPiece(7,row);
                if (rook != null && rook.isFirstMove && isFirstMove) {
                    return board.getPiece(5,row) == null
                            && board.getPiece(6,row) == null
                            && !board.checkScanner.isKingChecked(new Move(board,this,5,row));
                }
            } else if (col == 2) {
                Piece rook = board.getPiece(0,row);
                if (rook != null && rook.isFirstMove && isFirstMove) {
                    return board.getPiece(3,row) == null
                            && board.getPiece(2,row) == null
                            && board.getPiece(1,row) == null
                            && !board.checkScanner.isKingChecked(new Move(board,this,3,row));
                }
            }
        }
        return false;
    }
}