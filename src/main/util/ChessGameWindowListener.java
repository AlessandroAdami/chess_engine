package util;

import ui.ChessGameApp;

import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

public class ChessGameWindowListener implements WindowListener {

    private ChessGameApp app;

    public ChessGameWindowListener(ChessGameApp app) {
        this.app = app;
    }

    @Override
    public void windowOpened(WindowEvent e) {

    }

    @Override
    public void windowClosing(WindowEvent e) {
        this.app.saveChessGame();
        this.app.printLog();
    }

    @Override
    public void windowClosed(WindowEvent e) {

    }

    @Override
    public void windowIconified(WindowEvent e) {

    }

    @Override
    public void windowDeiconified(WindowEvent e) {

    }

    @Override
    public void windowActivated(WindowEvent e) {

    }

    @Override
    public void windowDeactivated(WindowEvent e) {

    }
}