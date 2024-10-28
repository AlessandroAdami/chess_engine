package ui;

import model.Event;
import model.*;
import util.ChessGameButtonManager;
import util.ChessGameMenuManager;
import util.ChessGameWindowListener;

import javax.swing.*;
import java.awt.*;

/** Chess game application. Allows the user to play various games
 * and analyze the current position in each board.
 */

public class ChessGameApp {

    private JFrame frame;
    private final ChessGame chessGame;

    private ChessGameWindowListener windowListener;
    private ChessGameButtonManager buttonManager;
    private ChessGameMenuManager menuManager;

    //EFFECTS: constructs and runs ChessGameApp
    public ChessGameApp() {
        chessGame = new ChessGame();
        init();
    }

    private void init() {
        initListeners();
        initFrame();
        initComponents();
    }

    private void initFrame() {
        frame = new JFrame();
        frame.setTitle("ChessBud");
        frame.getContentPane().setBackground(new Color(8, 30, 56));
        frame.setLayout(new GridBagLayout());
        frame.setMinimumSize(new Dimension(1000,1000));
        frame.setLocationRelativeTo(null);
        frame.add(chessGame.getCurrentBoard());

        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.addWindowListener(windowListener);
    }

    private void initListeners() {
        this.windowListener = new ChessGameWindowListener(this);
    }

    //MODIFIES: this
    //EFFECTS: initializes components
    private void initComponents() {
        ImageIcon imageIcon = new ImageIcon("./data/icon.png");
        frame.setIconImage(imageIcon.getImage());
        menuManager = new ChessGameMenuManager(this,frame);
        buttonManager = new ChessGameButtonManager(this,frame);
    }

    //EFFECTS: select the board as currentBoard
    private void selectBoard(String name) {
        chessGame.setCurrentBoard(chessGame.getBoard(name));
    }

    //MODIFIES: this
    //EFFECTS: deletes board
    private void deleteBoard(String name) {
        Board removedBoard = chessGame.removeBoard(name);
        if (removedBoard != null) {
            if (chessGame.getCurrentBoard().equals(removedBoard)) {
                setNewBoard();
            }
        }
    }

    //EFFECTS: sets the first board in boards as current board
    private void setNewBoard() {
        if (chessGame.isBoardListEmpty()) {
            Board b = new Board();
            chessGame.addBoard(b);
            this.chessGame.setCurrentBoard(b);
        } else {
            this.chessGame.setCurrentBoard(chessGame.getBoard(0));
        }
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

    //updates all components including (board list and current board)
    public void updateComponents() {
        frame.getContentPane().removeAll();  // Clear the frame’s content pane

        // Reset and reinitialize components
        initComponents();

        // Refresh frame with updated game data
        frame.add(chessGame.getCurrentBoard());

        // Revalidate and repaint to ensure changes take effect
        frame.revalidate();
        frame.repaint();
    }

    // MODIFIES: this
    // EFFECTS: creates new board and makes it the current board
    private void createNewBoard() {
        this.createNewBoard("New Game");
    }

    // MODIFIES: this
    // EFFECTS: creates new board and makes it the current board
    private void createNewBoard(String name) {
        Board b = new Board(name);
        this.chessGame.addBoard(b);
        this.chessGame.setCurrentBoard(b);
    }

    //EFFECTS: prints the log of events
    public void printLog() {
        for (Event e : EventLog.getInstance()) {
            System.out.println(e.getDate() + ":\n" + e.getDescription() + "\n\n");
        }
    }

    public ChessGame getChessGame() {
        return this.chessGame;
    }
}
