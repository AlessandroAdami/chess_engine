package ui;

import model.Event;
import model.*;
import model.exceptions.BoardSquareOutOfBoundsException;
import model.exceptions.NoPieceException;
import persistence.JsonReader;
import persistence.JsonWriter;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.util.Scanner;

// Chess game application. Allows the user to play various games
// and analyze the current position in each board.
//TODO: - promotion
//   clean this up
//  figure out moving with mouse

public class ChessGameApp extends JFrame implements ActionListener, WindowListener {

    private static final int WIDTH = 1000;
    private static final int HEIGHT = 1000;
    private static final int BOARD_SIZE = HEIGHT - 100;

    private static final String JSON_STORE = "./data/chessgame.json";
    private final JsonWriter jsonWriter;
    private final JsonReader jsonReader;
    private ChessGame chessGame;
    private final Piece piece;
    private Scanner input;

    private JMenuBar menuBar;
    private JPanel gameListPanel;
    private JTextField moveText;
    private JButton moveButton;
    private JLabel boardLabel;
    private JLabel[][] piecesLabels;
    private JButton createNewBoardButton;
    private JTextField newBoardName;
    private JButton deleteBoardButton;
    private JTextField deleteBoardName;
    private JButton selectBoardButton;
    private JTextField selectBoardName;
    private JTextArea boardListText;
    //moving pieces
    private JLabel selectedPiece;
    private int fromCol;
    private int fromRow;
    private int toCol;
    private int toRow;

    //EFFECTS: constructs and runs ChessGameApp
    public ChessGameApp() {
        jsonReader = new JsonReader(JSON_STORE);
        jsonWriter = new JsonWriter(JSON_STORE);
        chessGame = new ChessGame();
        piece = new Piece(BOARD_SIZE / 8);
        runChessGame();
    }

    // MODIFIES: this
    // EFFECTS: processes player's decisions
    private void runChessGame() {
        boolean keepGoing = true;
        String command;

        addWindowListener(this);
        setup();
        initFrame();

        while (keepGoing) {
            showOptions();
            showCurrentBoard();
            command = input.next();
            command = command.toLowerCase();

            if (command.equals("quit")) {
                keepGoing = false;
            } else {
                processCommand(command);
            }
        }
        printLog();
    }

    //EFFECTS: displays the window
    private void initFrame() {
        this.setTitle("Chess App");
        this.setLayout(null);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setResizable(true);
        this.setSize(WIDTH, HEIGHT);
        this.initComponents();
    }

    //MODIFIES: this
    //EFFECTS: initializes components
    private void initComponents() {
        ImageIcon imageIcon = new ImageIcon("FILENAME");
        this.setIconImage(imageIcon.getImage());
        setupMenuBar();
        setupGameListPanel();
        setupMakeMove();
        setupCreateNewBoard();
        setupDeleteBoard();
        setupSelectBoard();
        setupGameListPanel();

        this.setVisible(true);
        paintBoard();
    }

    //MODIFIES: this
    //EFFECTS: sets up menu bar
    private void setupMenuBar() {
        menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");
        JMenuItem loadItem = new JMenuItem("Load games from file");
        JMenuItem saveItem = new JMenuItem("Save games to file");
        loadItem.addActionListener(this);
        saveItem.addActionListener(this);
        fileMenu.add(loadItem);
        fileMenu.add(saveItem);

        menuBar.add(fileMenu);
        this.setJMenuBar(menuBar);
    }

    //MODIFIES: this
    //EFFECTS: sets up list of games
    private void setupGameListPanel() {
        gameListPanel = new JPanel();
        boardListText = new JTextArea();
        gameListPanel.setBounds(0, 0, (WIDTH - BOARD_SIZE) * 2, HEIGHT);
        Border gameListPanelBorder = BorderFactory.createLineBorder(Color.cyan, 3);
        gameListPanel.setBorder(gameListPanelBorder);
        boardListText.setBounds(0, 0, 1000, 300);
        int backgroundColor = gameListPanel.getBackground().getRGB();
        boardListText.setBackground(new Color(backgroundColor));
        updateBoardList();
        boardListText.setEditable(false);
        gameListPanel.add(boardListText);
        this.add(gameListPanel);
    }

    //EFFECTS: updates the list of boards
    private void updateBoardList() {
        StringBuilder text = new StringBuilder();
        text.append("Your games:");

        for (Board b : chessGame) {
            text.append(System.getProperty("line.separator"));
            text.append("- ").append(b.getName());

        }
        String string = text.toString();
        boardListText.setText(string);
    }

    //EFFECTS: initializes make move button and text
    private void setupMakeMove() {
        moveButton = new JButton("Move");
        moveButton.setFocusable(false);
        moveButton.setBounds(1310, 100, 90, 25);
        moveText = new JTextField();
        moveText.setBounds(1200, 100, 100, 25);
        String textString;
        if (chessGame.getCurrentBoard().getIsWhitesTurn()) {
            textString = "White move...";
        } else {
            textString = "Black move...";
        }
        moveText.setText(textString);
        moveButton.addActionListener(this);

        this.add(moveButton);
        this.add(moveText);
    }

    //MODIFIES: this
    //EFFECTS: sets up create new board button/text
    private void setupCreateNewBoard() {
        createNewBoardButton = new JButton("Create");
        createNewBoardButton.setFocusable(false);
        createNewBoardButton.setBounds(1310, 200, 90, 25);
        newBoardName = new JTextField();
        newBoardName.setBounds(1200, 200, 100, 25);
        newBoardName.setText("Create board...");
        createNewBoardButton.addActionListener(this);
        this.add(createNewBoardButton);
        this.add(newBoardName);
    }

    //MODIFIES: this
    //EFFECTS: sets up delete board button/text
    private void setupDeleteBoard() {
        deleteBoardButton = new JButton("Delete");
        deleteBoardButton.setFocusable(false);
        deleteBoardButton.setBounds(1310, 300, 90, 25);
        deleteBoardName = new JTextField();
        deleteBoardName.setBounds(1200, 300, 90, 25);
        deleteBoardName.setText("Delete board...");
        deleteBoardButton.addActionListener(this);
        this.add(deleteBoardButton);
        this.add(deleteBoardName);
    }

    //MODIFIES: this
    //EFFECTS: sets up select board button/text
    private void setupSelectBoard() {
        selectBoardButton = new JButton("Select");
        selectBoardButton.setFocusable(false);
        selectBoardButton.setBounds(1310, 400, 90, 25);
        selectBoardName = new JTextField();
        selectBoardName.setBounds(1200, 400, 90, 25);
        selectBoardName.setText("Select board...");
        selectBoardButton.addActionListener(this);
        this.add(selectBoardButton);
        this.add(selectBoardName);
    }

    // MODIFIES: this
    // EFFECTS: processes player's commands
    private void processCommand(String command) {
        switch (command) {
            case "play":
                play();
                break;
            case "new game":
                createNewBoard();
                break;
            case "list":
                manageBoards();
                break;
            case "load games":
                loadChessGame();
                break;
            case "save":
                saveChessGame();
                break;
            default:
                System.out.println("Please select valid command...");
                break;
        }
    }

    // MODIFIES: this
    // EFFECTS: initializes boards and current board
    private void setup() {
        input = new Scanner(System.in);
        input.useDelimiter("\n");
    }

    //EFFECTS: displays menu options
    private void showOptions() {
        System.out.println("\nChose from:");
        System.out.println("\tplay -> play current game");
        System.out.println("\tnew game -> create a new game");
        System.out.println("\tlist -> show list of your games");
        System.out.println("\tload games -> reload your past games");
        System.out.println("\tsave -> save your current games");
        System.out.println("\tquit -> quit");
    }

    //EFFECTS: displays current board
    private void showCurrentBoard() {
        if (chessGame.getCurrentBoard() != null) {
            System.out.println(chessGame.getCurrentBoard().getName() + ":");
            displayCurrentBoard();
        }
    }

    //MODIFIES: this
    //EFFECTS: lets player make moves in current board
    private void play() {
        String command;
        while (true) {
            System.out.println("\nSelect:");
            System.out.println("\tMake a move: enter 'fromCol,fromRow,toCol,toRow'");
            System.out.println("\tevaluate -> get evaluation of current position");
            System.out.println("\tback -> go back to menu");

            command = input.next();

            if (command.equals("back")) {
                break;
            }

            try {
                if (command.equals("evaluate")) {
                    System.out.println("Current evaluation = " + chessGame.getCurrentBoard().evaluatePos());
                } else {
                    makeMove(stringToMove(command));
                }
            } catch (NumberFormatException e) {
                System.out.println("\nNot a valid move format, try: '1,1,1,3'");
            } catch (BoardSquareOutOfBoundsException e) {
                System.out.println("\nNot a valid move, select proper squares");
            }

            showCurrentBoard();
        }
    }

    //MODIFIES: this
    //EFFECTS: makes the move on the current board
    private void makeMove(Move move) throws BoardSquareOutOfBoundsException {
        int toCol = move.getToCol();
        int toRow = move.getToRow();
        chessGame.getCurrentBoard().makeMove(move);
        if (chessGame.getCurrentBoard().isPawnToPromote(toCol, toRow)) {
            promotePawn(toCol, toRow);
        }
    }

    //MODIFIES: this
    //EFFECTS: lets player pick a piece to promote a pawn to
    @SuppressWarnings({"checkstyle:MethodLength", "checkstyle:SuppressWarnings"})
    private void promotePawn(int col, int row) {
        int promotion = 0;
        while (true) {
            System.out.println("Your pawn is promoting!\n");
            System.out.println("\tq -> promote pawn to queen");
            System.out.println("\tr -> promote pawn to rook");
            System.out.println("\tn -> promote pawn to knight");

            String command = input.next();

            switch (command) {
                case "q":
                    promotion = 5;
                    break;
                case "r":
                    promotion = 4;
                    break;
                case "b":
                    promotion = 3;
                    break;
                case "n":
                    promotion = 2;
                    break;
            }

            if (promotion != 0) {
                chessGame.getCurrentBoard().placePiece(col, row,
                        promotion * chessGame.getCurrentBoard().getPiece(col, row));
                break;
            }
        }
    }

    //EFFECTS: allows user to play or delete a game in the list
    @SuppressWarnings({"checkstyle:MethodLength", "checkstyle:SuppressWarnings"})
    private void manageBoards() {
        String command;
        boolean keepGoing = true;
        while (keepGoing) {
            System.out.println("\nChose from:");
            System.out.println("\tselect -> select a game to play");
            System.out.println("\trename -> rename one of the games");
            System.out.println("\tdelete -> delete one of the games");
            printBoards();

            command = input.next();

            switch (command) {
                case "select":
                    selectBoard();
                    keepGoing = false;
                    break;
                case "rename":
                    renameBoard();
                    keepGoing = false;
                    break;
                case "delete":
                    deleteBoard();
                    keepGoing = false;
                    break;
                default:
                    System.out.println("Enter a valid command");
            }
        }
    }

    // MODIFIES: this
    // EFFECTS: lets player select the current game from the list of games
    private void selectBoard() {
        String command;
        boolean keepGoing = true;
        while (keepGoing) {
            System.out.println("Enter the name of a game: ");
            printBoards();
            System.out.println("back -> go back");

            command = input.next();
            for (Board b : chessGame) {
                if (command.equals(b.getName())) {
                    chessGame.setCurrentBoard(b);
                    keepGoing = false;
                    break;
                }
            }
            if (command.equals("back")) {
                break;
            } else if (keepGoing) {
                System.out.println("Enter a valid command.");
            }
        }
    }

    //EFFECTS: select the board as currentBoard
    private void selectBoard(String name) {
        for (Board b : chessGame) {
            if (name.equals(b.getName())) {
                chessGame.setCurrentBoard(b);
                break;
            }
        }
    }

    //MODIFIES: this
    //EFFECTS: renames one of the boards
    private void renameBoard() {
        String command;
        boolean keepGoing = true;
        while (keepGoing) {
            System.out.println("Enter the name of a game: ");
            printBoards();
            System.out.println("back -> go back");

            command = input.next();
            for (Board b : chessGame) {
                if (command.equals(b.getName())) {
                    pickName(b);
                    keepGoing = false;
                    break;
                }
            }
            if (command.equals("back")) {
                break;
            } else if (keepGoing) {
                System.out.println("Enter a valid command.");
            }
        }
    }

    //MODIFIES: this, Board
    //EFFECTS: lets user select name of new board
    private void pickName(Board b) {
        String command = "";
        while (command.equals("")) {
            System.out.println("Enter the new name:");

            String oldName = b.getName();

            command = input.next();
            b.setName(command);
            System.out.println(oldName + " was renamed to " + command);
        }
    }

    //MODIFIES: this
    //EFFECTS: deletes a board from boards
    private void deleteBoard() {
        String command;
        while (true) {
            System.out.println("Enter the name of a game:");
            printBoards();
            System.out.println("back -> go back");

            command = input.next();

            Board removedBoard = chessGame.removeBoard(command);
            if (removedBoard != null) {
                if (chessGame.getCurrentBoard().equals(removedBoard)) {
                    setNewBoard();
                }
                System.out.println(removedBoard.getName() + " was deleted.");
                break;
            } else if (command.equals("back")) {
                break;
            } else {
                System.out.println("Please enter a valid command");
            }
        }
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
    private void saveChessGame() {
        try {
            jsonWriter.open();
            jsonWriter.write(chessGame);
            jsonWriter.close();
            System.out.println("Games saved to " + JSON_STORE);
        } catch (IOException e) {
            System.out.println("Unable to write to file: " + JSON_STORE);
        }
    }

    //MODIFIES: this
    //EFFECTS: loads chess game from file
    private void loadChessGame() {
        try {
            chessGame = jsonReader.read();
            System.out.println("Games loaded from " + JSON_STORE);

        } catch (IOException e) {
            System.out.println("Unable to read from file" + JSON_STORE);
        }
    }

    //EFFECTS: prints out all the names of the boards in order
    private void printBoards() {
        for (Board board : chessGame) {
            System.out.println("- " + board.getName());
        }
    }

    // EFFECTS: displays a board with number strings as established above
    private void displayCurrentBoard() {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                System.out.print(chessGame.getCurrentBoard().boardToStringBoard()[i][j] + " ");
            }
            System.out.println();
        }
        if (chessGame.getCurrentBoard().getIsWhitesTurn()) {
            System.out.println("White to move.");
        } else {
            System.out.println("Black to move.");
        }
    }

    // MODIFIES: this
    // EFFECTS: creates new board and makes it the current board
    private void createNewBoard() {
        String name = "";
        while (name.equals("")) {
            System.out.println("Enter new game name: ");
            name = input.next();
            Board b = new Board(name);
            this.chessGame.addBoard(b);
            this.chessGame.setCurrentBoard(b);
        }
    }

    // MODIFIES: this
    // EFFECTS: creates new board and makes it the current board
    private void createNewBoard(String name) {
        Board b = new Board(name);
        this.chessGame.addBoard(b);
        this.chessGame.setCurrentBoard(b);
    }

    //MODIFIES: this
    //EFFECTS: draws the board
    private void paintBoard() {
        boardLabel = new JLabel();
        ImageIcon oldIcon = new ImageIcon("./data/chessBoard.png");
        Image oldImage = oldIcon.getImage();
        int size = HEIGHT - 120;
        Image scaledImage = oldImage.getScaledInstance(size, size, Image.SCALE_SMOOTH);
        ImageIcon chessBoardImage = new ImageIcon(scaledImage);
        boardLabel.setIcon(chessBoardImage);
        boardLabel.setHorizontalTextPosition(JLabel.CENTER);
        boardLabel.setVerticalTextPosition(JLabel.TOP);
        boardLabel.setBounds(250, 0, size, size + 50);
        boardLabel.setVerticalAlignment(JLabel.CENTER);
        boardLabel.setHorizontalAlignment(JLabel.CENTER);
        boardLabel.setText(chessGame.getCurrentBoard().getName());
        setupPieces();

        this.add(boardLabel);
        this.setIconImage(scaledImage);
        boardLabel.addMouseListener(new Input());
    }

    //MODIFIES: this
    //EFFECTS: updates drawing board
    private void updateBoard() {
        boardLabel.setText(chessGame.getCurrentBoard().getName());
        setupPieces();
    }

    //MODIFIES: this
    //EFFECTS: repaints the board
    private void updateBoard(Move move) {
        boardLabel.setText(chessGame.getCurrentBoard().getName());
        updatePieces(move);
    }

    //MODIFIES: this, board
    //EFFECTS: draws the pieces on the board
    private void setupPieces() {
        boardLabel.removeAll();
        piecesLabels = new JLabel[8][8];
        int squareImageScale = (HEIGHT - 120) / 8;
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                try {
                    ImageIcon piece = this.piece.getPieceImage(chessGame.getCurrentBoard().getPiece(i, j));
                    JLabel pieceLabel = new JLabel();
                    pieceLabel.setIcon(piece);
                    pieceLabel.setBounds(
                            i * squareImageScale,
                            -80 + (8 - j) * squareImageScale,
                            squareImageScale,
                            squareImageScale);
                    pieceLabel.setVerticalAlignment(JLabel.CENTER);
                    pieceLabel.setHorizontalAlignment(JLabel.CENTER);
                    boardLabel.add(pieceLabel);
                    piecesLabels[i][j] = pieceLabel;
                } catch (NoPieceException e) {
                    //draw nothing if there is no piece in the square
                }
            }
        }
    }

    //MODIFIES: this, board
    //EFFECTS: redraws the pieces on the board
    private void updatePieces(Move move) {
        int toCol = move.getToCol();
        int toRow = move.getToRow();
        int fromCol = move.getFromCol();
        int fromRow = move.getFromRow();
        int squareImageScale = (HEIGHT - 120) / 8;
        JLabel deletePieceLabel = piecesLabels[toCol][toRow];
        if (deletePieceLabel != null) {
            boardLabel.remove(deletePieceLabel);
            piecesLabels[toCol][toRow] = null;
        }
        JLabel movedPiece = piecesLabels[fromCol][fromRow];
        piecesLabels[fromCol][fromRow] = null;
        piecesLabels[toCol][toRow] = movedPiece;
        movedPiece.setBounds(
                toCol * squareImageScale,
                -80 + (8 - toRow) * squareImageScale,
                squareImageScale,
                squareImageScale);

        boardLabel.validate();
        boardLabel.repaint();
    }

    //MODIFIES: this
    //EFFECTS: updates the whole game
    private void updateGame() {
        updateBoard();
        updateBoardList();
    }

    //MODIFIES: this
    //EFFECTS: handles user actions
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == menuBar.getMenu(0).getItem(0)) {
            loadChessGame();
            updateGame();
        } else if (e.getSource() == menuBar.getMenu(0).getItem(1)) {
            saveChessGame();
        } else if (e.getSource() == moveButton) {
            try {
                Move move = stringToMove(moveText.getText());
                makeMove(move);
                updateBoard(move);
            } catch (BoardSquareOutOfBoundsException ex) {
                System.out.println(ex.getMessage());
            }
        } else if (e.getSource() == createNewBoardButton) {
            createNewBoard(newBoardName.getText());
            updateGame();
        } else if (e.getSource() == deleteBoardButton) {
            deleteBoard(deleteBoardName.getText());
            updateGame();
        } else if (e.getSource() == selectBoardButton) {
            selectBoard(selectBoardName.getText());
            updateBoard();
        }
    }

    //EFFECTS: returns a move string into a move array
    private Move stringToMove(String moveString) throws BoardSquareOutOfBoundsException {
        int fromCol = Integer.parseInt(moveString.substring(0, 1));
        int fromRow = Integer.parseInt(moveString.substring(2, 3));
        int toCol = Integer.parseInt(moveString.substring(4, 5));
        int toRow = Integer.parseInt(moveString.substring(6));
        return new Move(fromCol, fromRow, toCol, toRow);
    }

    //EFFECTS: prints the log of events
    private void printLog() {
        for (Event e : EventLog.getInstance()) {
            System.out.println(e.getDate() + ":\n" + e.getDescription() + "\n\n");
        }
    }

    @Override
    public void windowClosed(WindowEvent e) {
    }

    @Override
    public void windowOpened(WindowEvent e) {
    }

    //EFFECTS: saves games and prints log events as program is closing
    @Override
    public void windowClosing(WindowEvent e) {
        saveChessGame();
        printLog();
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

    private void resetMove() {
        fromRow = -1;
        fromCol = -1;
        toRow = -1;
        toCol = -1;
    }

    private class Input extends MouseAdapter {


        @Override
        public void mousePressed(MouseEvent e) {

        }

        @Override
        public void mouseReleased(MouseEvent e) {

        }


        @Override
        public void mouseDragged(MouseEvent e) {

        }
    }
}
