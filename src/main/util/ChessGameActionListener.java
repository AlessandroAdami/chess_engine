package util;

import model.ChessGame;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ChessGameActionListener implements ActionListener {

    private ChessGame chessGame;

    public ChessGameActionListener(ChessGame chessGame) {
        this.chessGame = chessGame;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        switch (e.getActionCommand()) {
            case "Save games to file":
                chessGame.saveChessGame();
                break;
            case "Load games from file":
                chessGame.loadChessGame();

        }
    }
}
