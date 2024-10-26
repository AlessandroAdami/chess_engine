package util;

import ui.ChessGameApp;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ChessGameMenuManager implements ActionListener {

    private ChessGameApp app;
    private JMenuBar menuBar;

    private JFrame frame;
    private JMenuItem loadItem;
    private JMenuItem saveItem;

    public ChessGameMenuManager(ChessGameApp app, JFrame frame) {
        this.app = app;
        this.frame = frame;
        menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");
        loadItem = new JMenuItem("Load games from file");
        saveItem = new JMenuItem("Save games to file");
        loadItem.addActionListener(this);
        saveItem.addActionListener(this);
        fileMenu.add(loadItem);
        fileMenu.add(saveItem);
        fileMenu.addActionListener(this);
        menuBar.add(fileMenu);
        menuBar.setVisible(true);
        this.frame.setJMenuBar(menuBar);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == loadItem) {
            System.out.println("one");
            app.loadChessGame();
        } else if (e.getSource() == saveItem) {
            System.out.println("two");
            app.saveChessGame();
        }
    }
}
