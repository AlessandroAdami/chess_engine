package model.pieces;

import model.Board;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;

public class Knight extends Piece {

    BufferedImage whiteKnight;
    BufferedImage blackKnight;

    public Knight(Board board, int col, int row, boolean isWhite) {
        super(board);
        this.col = col;
        this.row = row;
        this.xPos = col * board.tileSize;
        this.yPos = row * board.tileSize;
        this.isWhite = isWhite;
        this.name = "Knight";

        try {
            whiteKnight = ImageIO.read(new File("./data/whiteKnight.png"));
            blackKnight = ImageIO.read(new File("./data/blackKnight.png"));
        } catch (Exception e) {
            e.printStackTrace();
        }

        this.sprite = isWhite ? whiteKnight : blackKnight;

        this.sprite = this.sprite.getScaledInstance(board.tileSize, board.tileSize,BufferedImage.SCALE_SMOOTH);
    }

    public boolean isValidMovement(int col,int row) {
        return Math.abs(col - this.col) * Math.abs(row - this.row) == 2;
    }
}
