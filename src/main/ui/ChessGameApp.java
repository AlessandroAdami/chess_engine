package ui;

import model.Event;
import model.*;
import util.ChessGameMenuManager;
import util.ChessGameWindowListener;

import javax.swing.*;
import java.awt.*;
import java.util.Iterator;

/** Chess game application. Allows the user to play various games
 * and analyze the current position in each board.
 */

public class ChessGameApp implements Iterable<CompressedBoard> {

    private JFrame frame;
    private final ChessGame chessGame;

    private ChessGameWindowListener windowListener;
    private ChessGameMenuManager menuManager;

    /**
     * Starts the app
     */
    public ChessGameApp() {
        chessGame = new ChessGame();
        this.windowListener = new ChessGameWindowListener(this);
        initFrame();
    }

    private void initFrame() {
        frame = new JFrame();
        frame.setTitle("ChessBud");
        frame.getContentPane().setBackground(new Color(8, 30, 56));
        frame.setLayout(new GridBagLayout());
        frame.setMinimumSize(new Dimension(800,800));
        frame.setLocationRelativeTo(null);
        frame.add(chessGame.getCurrentBoard());

        ImageIcon imageIcon = new ImageIcon("./data/icon.png");
        frame.setIconImage(imageIcon.getImage());
        menuManager = new ChessGameMenuManager(this,frame);

        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.addWindowListener(windowListener);
    }

    //EFFECTS: select the board as currentBoard
    public void setBoard(String name) {
        CompressedBoard b = chessGame.getBoard(name);
        if (b != null) {
            chessGame.setCurrentBoard(b.getBoard());
            updateComponents();
        }
    }

    //updates all components including (board list and current board)
    public void updateComponents() {
        frame.getContentPane().removeAll();  // Clear the frame’s content pane

        // Reset and reinitialize menuManager
        menuManager = new ChessGameMenuManager(this,frame);

        // Refresh frame with updated game data
        frame.add(chessGame.getCurrentBoard());

        // Revalidate and repaint to ensure changes take effect
        frame.revalidate();
        frame.repaint();
    }

    //EFFECTS: saves current chess game to file
    public void saveChessGame() {
        chessGame.saveChessGame();
    }

    //MODIFIES: this
    //EFFECTS: loads chess game from file
    public void loadChessGame() {
        chessGame.loadChessGame();
        updateComponents();
    }

    //EFFECTS: prints the log of events
    public void printLog() {
        for (Event e : EventLog.getInstance()) {
            System.out.println(e.getDate() + ":\n" + e.getDescription() + "\n\n");
        }
    }

    public void newGame() {
        chessGame.addBoard(new CompressedBoard());
        updateComponents();
    }

    public Board getCurrentBoard() {
        return this.chessGame.getCurrentBoard();
    }

    @Override
    public Iterator<CompressedBoard> iterator() {
        return this.chessGame.iterator();
    }
}
