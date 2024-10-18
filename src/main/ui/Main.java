package ui;

import model.Board;
import model.CheckScanner;
import model.ChessGame;

import javax.swing.*;
import java.awt.*;

//starts the game.

public class Main {
    public static void main(String[] args) {

        JFrame frame = new JFrame();
        frame.getContentPane().setBackground(Color.black);
        frame.setLayout(new GridBagLayout());
        frame.setMinimumSize(new Dimension(1000,1000));
        frame.setLocationRelativeTo(null);

        ChessGame chessGame = new ChessGame();
        Board board = chessGame.getCurrentBoard();
        frame.add(board);

        frame.setVisible(true);
    }
}
