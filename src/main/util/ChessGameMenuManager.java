package util;

import model.CompressedBoard;
import ui.ChessGameApp;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class ChessGameMenuManager implements ActionListener {

    private ChessGameApp app;
    private JMenuBar menuBar;

    private JFrame frame;
    private JMenuItem loadItem;
    private JMenuItem saveItem;
    private JMenuItem newGame;

    private ArrayList<JMenuItem> games;

    public ChessGameMenuManager(ChessGameApp app, JFrame frame) {
        this.app = app;
        this.frame = frame;
        menuBar = new JMenuBar();

        initMenuBar();

        menuBar.setVisible(true);
        this.frame.setJMenuBar(menuBar);
    }

    private void initMenuBar() {
        //add file menu
        JMenu fileMenu = new JMenu("File");
        loadItem = new JMenuItem("Load games from file");
        saveItem = new JMenuItem("Save games to file");
        loadItem.addActionListener(this);
        saveItem.addActionListener(this);
        fileMenu.add(loadItem);
        fileMenu.add(saveItem);
        fileMenu.addActionListener(this);
        menuBar.add(fileMenu);
        //add games menu
        newGame = new JMenuItem("New Game");
        newGame.addActionListener(this);
        fileMenu.add(newGame);
        initGamesMenu();
    }

    private void initGamesMenu() {
        JMenu gamesMenu = new JMenu("Games");
        for (CompressedBoard b : app) {
            //games.add(new JMenuItem(b.getName()));
            gamesMenu.add(new JMenuItem("Board: " + b.getName()));
        }
        menuBar.add(gamesMenu);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == loadItem) {
            app.loadChessGame();
            System.out.println("Games loaded from file");
        } else if (e.getSource() == saveItem) {
            app.saveChessGame();
            System.out.println("Games saved to file");
        } else if (e.getSource() == newGame) {
            //app.newGame();
        }
    }
}
