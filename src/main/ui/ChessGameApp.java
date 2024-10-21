package ui;

import model.Event;
import model.*;
import persistence.JsonReader;
import persistence.JsonWriter;
import util.ChessGameActionListener;
import util.ChessGameWindowListener;
import util.SelectButtonListener;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;

/** Chess game application. Allows the user to play various games
 * and analyze the current position in each board.
 */

public class ChessGameApp {

    private JFrame frame;
    private ChessGame chessGame;

    private ArrayList<SelectButtonListener> buttonList;
    private ChessGameActionListener actionListener;
    private ChessGameWindowListener windowListener;

    private JMenuBar menuBar;
    private JButton createNewBoardButton;
    private JTextField newBoardName;


    //EFFECTS: constructs and runs ChessGameApp
    public ChessGameApp() {
        chessGame = new ChessGame();
        init();
    }

    private void init() {
        initFrame();

        initComponents();
        initListeners();
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
        this.actionListener = new ChessGameActionListener(chessGame);
        this.windowListener = new ChessGameWindowListener(this);
        this.buttonList = new ArrayList<>();
    }

    //MODIFIES: this
    //EFFECTS: initializes components
    private void initComponents() {
        ImageIcon imageIcon = new ImageIcon("./data/icon.png");
        frame.setIconImage(imageIcon.getImage());
        setupMenuBar();
        setupCreateNewBoard();
    }

    //MODIFIES: this
    //EFFECTS: sets up menu bar
    private void setupMenuBar() {
        menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");
        JMenuItem loadItem = new JMenuItem("Load games from file");
        JMenuItem saveItem = new JMenuItem("Save games to file");
        fileMenu.add(loadItem);
        fileMenu.add(saveItem);
        fileMenu.addActionListener(actionListener);

        menuBar.add(fileMenu);
        menuBar.setVisible(true);
        frame.setJMenuBar(menuBar);
    }

    //MODIFIES: this
    //EFFECTS: sets up create new board button/text
    private void setupCreateNewBoard() {
        createNewBoardButton = new JButton("New Game");
        createNewBoardButton.setFocusable(false);
        createNewBoardButton.setBounds(4000, 200, 90, 25);
        newBoardName = new JTextField();
        newBoardName.setBounds(4000, 200, 100, 25);
        newBoardName.setText("Create board...");
        frame.add(createNewBoardButton);
        frame.add(newBoardName);
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
    private void loadChessGame() {
        chessGame.loadChessGame();
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
}
