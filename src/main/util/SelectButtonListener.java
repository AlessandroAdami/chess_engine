package util;

import model.Board;
import model.ChessGame;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * An action listener that selects handles buttons that set the current board
 */

public class SelectButtonListener implements ActionListener {

    private ChessGame game;

    public SelectButtonListener(ChessGame game, JButton b) {
        this.game = game;
    }

    /**
     * checks what board the button maps to and sets it as current
     * @param e the event to be processed
     */
    @Override
    public void actionPerformed(ActionEvent e) {

        String selectBoardCommand = e.getActionCommand();

        for (Board b : game) {
            if (selectBoardCommand.equals(b.getName())) {
                game.setCurrentBoard(b);
            }
        }
    }
}
