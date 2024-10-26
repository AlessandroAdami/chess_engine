package util;

import model.Board;
import ui.ChessGameApp;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * An action listener that selects handles buttons that set the current board
 */

public class ChessGameButtonManager implements ActionListener {

    private ChessGameApp app;
    private JFrame frame;
    private JButton createNewBoardButton;
    private JTextField newBoardName;

    public ChessGameButtonManager(ChessGameApp app, JFrame frame) {
        this.app = app;
        this.frame = frame;
        createNewBoardButton = new JButton("New Game");
        createNewBoardButton.setFocusable(false);
        createNewBoardButton.setBounds(4000, 200, 90, 25);
        newBoardName = new JTextField();
        newBoardName.setBounds(4000, 200, 100, 25);
        newBoardName.setText("Create board...");
        this.frame.add(createNewBoardButton);
        this.frame.add(newBoardName);
    }

    /**
     * checks what board the button maps to and sets it as current
     * @param e the event to be processed
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        //TODO
        String selectBoardCommand = e.getActionCommand();
    }
}
