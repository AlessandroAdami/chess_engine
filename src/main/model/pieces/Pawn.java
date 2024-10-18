package model.pieces;

import model.Board;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;

public class Pawn extends Piece {

    BufferedImage whitePawn;
    BufferedImage blackPawn;

    public Pawn(Board board, int col, int row, boolean isWhite) {
        super(board);
        this.col = col;
        this.row = row;
        this.xPos = col * board.tileSize;
        this.yPos = row * board.tileSize;
        this.isWhite = isWhite;
        this.name = "Pawn";

        try {
            whitePawn = ImageIO.read(new File("./data/whitePawn.png"));
            blackPawn = ImageIO.read(new File("./data/blackPawn.png"));
        } catch (Exception e) {
            e.printStackTrace();
        }

        this.sprite = isWhite ? whitePawn : blackPawn;

        this.sprite = this.sprite.getScaledInstance(board.tileSize, board.tileSize,BufferedImage.SCALE_SMOOTH);
    }

    public boolean isValidMovement(int col, int row) {
        int colorIndex = isWhite ? 1 : - 1;

        //push pawn 1
        if (this.col == col && row == this.row - colorIndex && board.getPiece(col,row) == null) {
            return true;
        }
        //push pawn 2
        if (this.row == (isWhite ? 6 : 1) && this.col == col && row == this.row - (2 * colorIndex) && board.getPiece(col,row) == null && board.getPiece(col,row + colorIndex) == null) {
            return true;
        }
        //capture left
        if (col == this.col - 1 && row == this.row - colorIndex && board.getPiece(col,row) != null)
            return true;
        //capture right
        if (col == this.col + 1 && row == this.row - colorIndex && board.getPiece(col,row) != null)
            return true;

        //en passant left
        if (board.getTileNum(col,row) == board.enPassantTile && col == this.col - 1 && row == this.row - colorIndex && board.getPiece(col,row+colorIndex) != null)
            return true;

        //en passant right
        if (board.getTileNum(col,row) == board.enPassantTile && col == this.col + 1 && row == this.row - colorIndex && board.getPiece(col,row+colorIndex) != null)
            return true;

        return false;
    }
}