package ui;

import model.Board;
import model.BoardList;
import model.ChessGame;
import model.exceptions.NoPieceException;
import persistence.JsonReader;
import persistence.JsonWriter;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.Scanner;

// Chess game application. Allows the user to play various games
// and analyze the current position in each board.
//TODO: - use Joptionpane to handle creating new games!!!
//      - it can also do menus!!!
//      - progress bar for evaluations
//      - override exit on close to save the game before closing the application
//      - add method to make moves like e4 and not 1,0,1,1
//      - change EXIT_ON_CLOSE to prompt saving games

// System.exit(0) closes the program
// menuItem.setMnemonic(KeyEvent.KE) allows to bind a menu item to a key typing
// the same can be done with menus Alt + KE
public class ChessGameApp extends JFrame implements ActionListener {

    private static final int WIDTH  = 1000;
    private static final int HEIGHT = 1000;
    private static final int BOARD_SIZE = HEIGHT - 100;


    private static final String JSON_STORE = "./data/chessgame.json";
    private JsonWriter jsonWriter;
    private JsonReader jsonReader;
    private Scanner input;
    private ChessGame chessGame;
    private BoardList boards;
    private Board currentBoard;
    private Pieces pieces;
    private JMenuBar menuBar;
    private JPanel gameListPanel;
    private ButtonGroup gamesButtonGroup;
    private JTextField moveText;
    private JButton confirmMoveButton;
    private JLabel boardLabel;
    private JLabel[][] piecesLabels;

    //EFFECTS: constructs and runs ChessGameApp
    public ChessGameApp() {
        jsonReader = new JsonReader(JSON_STORE);
        jsonWriter = new JsonWriter(JSON_STORE);
        chessGame = new ChessGame();
        pieces = new Pieces(BOARD_SIZE / 8);
        runChessGame();
    }

    // MODIFIES: this
    // EFFECTS: processes player's decisions
    private void runChessGame() {
        boolean keepGoing = true;
        String command;

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
        System.out.println("\nNice moves!");
    }

    //EFFECTS: displays the window
    private void initFrame() {
        this.setTitle("Chess App");
        this.setLayout(null);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setResizable(true);
        this.setSize(WIDTH,HEIGHT);
        ImageIcon imageIcon = new ImageIcon("FILENAME");
        this.setIconImage(imageIcon.getImage());
        menuBar = new JMenuBar();
        setupMenuBar();
        setupGameListPanel();
        setupMakeMove();

        this.setVisible(true);
        paintBoard();
    }

    //MODIFIES: this
    //EFFECTS: sets up menu bar
    private void setupMenuBar() {
        JMenu fileMenu = new JMenu("File");
        JMenuItem loadItem = new JMenuItem("Load games from file");
        JMenuItem saveItem = new JMenuItem("Save games to file");
        loadItem.addActionListener(this);
        saveItem.addActionListener(this);
        fileMenu.add(loadItem);
        fileMenu.add(saveItem);

        JMenu gamesMenu = new JMenu("Games");
        JMenuItem newGameItem = new JMenuItem("New Game");
        JMenuItem manageGamesItem = new JMenuItem("Manage Games");
        newGameItem.addActionListener(this);
        manageGamesItem.addActionListener(this);
        gamesMenu.add(newGameItem);
        gamesMenu.add(manageGamesItem);

        menuBar.add(fileMenu);
        menuBar.add(gamesMenu);
        this.setJMenuBar(menuBar);
    }

    private void setupGameListPanel() {
        gameListPanel = new JPanel();
        gamesButtonGroup = new ButtonGroup();
        gameListPanel.setBounds(0,0, (WIDTH - BOARD_SIZE) * 2, HEIGHT);
        Border gameListPanelBorder = BorderFactory.createLineBorder(Color.cyan,3);
        gameListPanel.setBorder(gameListPanelBorder);

        for (int i = 0; i < boards.getBoards().size(); i++) {
            Board b = boards.getBoard(i);
            JRadioButton boardButton = new JRadioButton(b.getName());
            boardButton.setBounds(0, i * 100,300,100);
            boardButton.setFocusable(false);
            boardButton.addActionListener(e -> currentBoard = b);
            gamesButtonGroup.add(boardButton);
            gameListPanel.add(boardButton);
        }

        this.add(gameListPanel);
    }

    private void setupMakeMove() {
        confirmMoveButton = new JButton("Confirm");
        confirmMoveButton.setFocusable(false);
        confirmMoveButton.setBounds(1310,100,90,25);
        moveText = new JTextField();
        moveText.setBounds(1200, 100, 100,25);
        String textString;
        if (currentBoard.getIsWhitesTurn()) {
            textString = "White move...";
        } else {
            textString = "Black move...";
        }
        moveText.setText(textString);
        confirmMoveButton.addActionListener(this);

        this.add(confirmMoveButton);
        this.add(moveText);
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
        boards = new BoardList();
        currentBoard = new Board();
        boards.addBoard(currentBoard);
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
        if (currentBoard != null) {
            System.out.println(currentBoard.getName() + ":");
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
                    System.out.println("Current evaluation = " + currentBoard.evaluatePos());
                } else if (isValidMove(command)) {
                    makeMove(command);
                } else {
                    System.out.println("Not a valid move.");
                }
            } catch (NumberFormatException e) {
                System.out.println("\nNot a valid move format, try: '1,1,1,3'");
            }

            showCurrentBoard();
        }
    }

    //EFFECTS: true if move is valid in current Board
    private boolean isValidMove(String move) throws NumberFormatException {
        int fromCol = Integer.parseInt(move.substring(0,1));
        int fromRow = Integer.parseInt(move.substring(2,3));
        int toCol   = Integer.parseInt(move.substring(4,5));
        int toRow   = Integer.parseInt(move.substring(6));
        if (0 <= fromCol && 0 <= fromRow && 0 <= toCol && 0 <= toRow
                && fromCol < 8 && fromRow < 8 && toCol < 8 && toRow < 8) {
            return currentBoard.isLegalMove(fromCol,fromRow,toCol,toRow);
        } else {
            return false;
        }
    }

    //MODIFIES: this
    //EFFECTS: makes the move on the current board
    private void makeMove(String move) {
        int fromCol = Integer.parseInt(move.substring(0,1));
        int fromRow = Integer.parseInt(move.substring(2,3));
        int toCol   = Integer.parseInt(move.substring(4,5));
        int toRow   = Integer.parseInt(move.substring(6));
        currentBoard.makeMove(fromCol,fromRow,toCol,toRow);
        if (currentBoard.isPawnToPromote(toCol,toRow)) {
            promotePawn(toCol,toRow);
        }
        repaintPiece(fromCol,fromRow,toCol,toRow);
    }

    //MODIFIES: this
    //EFFECTS: lets player pick a piece to promote a pawn to
    private void promotePawn(int col,int row) {
        int promotion = 0;
        while (true) {
            System.out.println("Your pawn is promoting!\n");
            System.out.println("\tq -> promote pawn to queen");
            System.out.println("\tr -> promote pawn to rook");
            System.out.println("\tn -> promote pawn to knight");
            System.out.println("\tb -> promote pawn to bishop");

            String command = input.next();

            switch (command) {
                case "q": promotion = 5;
                break;
                case "r": promotion = 4;
                break;
                case "b": promotion = 3;
                break;
                case "n": promotion = 2;
                break;
            }

            if (promotion != 0) {
                currentBoard.placePiece(col,row, promotion * currentBoard.getPiece(col,row));
                break;
            }
        }
    }

    //EFFECTS: allows user to play or delete a game in the list
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
                case "select": selectBoard();
                    keepGoing = false;
                    break;
                case "rename": renameBoard();
                    keepGoing = false;
                    break;
                case "delete": deleteBoard();
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
            for (Board b : boards.getBoards()) {
                if (command.equals(b.getName())) {
                    currentBoard = b;
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
            for (Board b : boards.getBoards()) {
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

            Board removedBoard = boards.removeBoard(command);
            if (removedBoard != null) {
                if (currentBoard.equals(removedBoard)) {
                    setNewBoard();
                }
                System.out.println(currentBoard.getName() + " was deleted.");
                break;
            } else if (command.equals("back")) {
                break;
            } else {
                System.out.println("Please enter a valid command");
            }
        }
    }

    //EFFECTS: sets the first board in boards as current board
    private void setNewBoard() {
        if (boards.isBoardListEmpty()) {
            Board b = new Board();
            boards.addBoard(b);
            this.currentBoard = b;
        } else {
            this.currentBoard = boards.getBoard(0);
        }
    }

    //EFFECTS: saves current chess game to file
    private void saveChessGame() {
        try {
            chessGame.setCurrentBoard(currentBoard);
            chessGame.setBoards(boards);
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
            currentBoard = chessGame.getCurrentBoard();
            boards = chessGame.getBoards();
            System.out.println("Games loaded from " + JSON_STORE);

        } catch (IOException e) {
            System.out.println("Unable to read from file" + JSON_STORE);
        }
    }

    //EFFECTS: prints out all the names of the boards in order
    private void printBoards() {
        for (Board board : boards.getBoards()) {
            System.out.println("- " + board.getName());
        }
    }

    // EFFECTS: displays a board with number strings as established above
    private void displayCurrentBoard() {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                System.out.print(currentBoard.boardToStringBoard()[i][j] + " ");
            }
            System.out.println();
        }
        if (currentBoard.getIsWhitesTurn()) {
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
            this.boards.addBoard(b);
            this.currentBoard = b;
        }
    }

    //MODIFIES: this
    //EFFECTS: draws the board
    private void paintBoard() {
        boardLabel = new JLabel();
        ImageIcon oldIcon = new ImageIcon("./data/chessBoard.png");
        Image oldImage = oldIcon.getImage();
        int size = HEIGHT - 120;
        Image scaledImage = oldImage.getScaledInstance(size,size, Image.SCALE_SMOOTH);
        ImageIcon chessBoardImage = new ImageIcon(scaledImage);
        Border boardBorder = BorderFactory.createLineBorder(Color.cyan,3);//change this to colorless
        boardLabel.setText(currentBoard.getName());
        boardLabel.setIcon(chessBoardImage);
        boardLabel.setHorizontalTextPosition(JLabel.CENTER);
        boardLabel.setVerticalTextPosition(JLabel.TOP);
        boardLabel.setBounds(250, 0, size, size + 50);
        boardLabel.setVerticalAlignment(JLabel.CENTER);
        boardLabel.setHorizontalAlignment(JLabel.CENTER);
        boardLabel.setBorder(boardBorder);
        setupPiecesLabels();
        this.add(boardLabel);
        this.setIconImage(scaledImage);
    }

    //MODIFIES: this, board
    //EFFECTS: draws the pieces on the board
    private void setupPiecesLabels() {
        piecesLabels = new JLabel[8][8];
        int squareImageScale = (HEIGHT - 120) / 8;
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                try {
                    ImageIcon piece = pieces.getPieceImage(currentBoard.getPiece(i,j));
                    JLabel pieceLabel = new JLabel();
                    pieceLabel.setIcon(piece);
                    pieceLabel.setBounds(
                              i * squareImageScale,
                            - 80 + (8 - j) * squareImageScale,
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
    private void repaintPieces() {
        int squareImageScale = (HEIGHT - 120) / 8;
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                JLabel pieceLabel = piecesLabels[i][j];
                if (pieceLabel != null) {
                    pieceLabel.setBounds(
                            i * squareImageScale,
                            - 80 + (8 - j) * squareImageScale,
                            squareImageScale,
                            squareImageScale);
                    pieceLabel.setVerticalAlignment(JLabel.CENTER);
                    pieceLabel.setHorizontalAlignment(JLabel.CENTER);
                }

            }
        }
    }

    //MODIFIES: this
    //EFFECTS: repaints the piece moved on the board
    private void repaintPiece(int fromCol,int fromRow,int toCol,int toRow) {
        JLabel movedPieceLabel = piecesLabels[fromCol][fromRow];
        piecesLabels[toCol][toRow] = movedPieceLabel;
        piecesLabels[fromCol][fromRow] = null;
        repaintPieces();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == menuBar.getMenu(0).getItem(0)) {
            loadChessGame();
            //repaintBoard(something);
        } else if (e.getSource() == menuBar.getMenu(0).getItem(1)) {
            saveChessGame();
        } else if (e.getSource() == menuBar.getMenu(1).getItem(0)) {
            System.out.println("create new game");
        } else if (e.getSource() == menuBar.getMenu(1).getItem(1)) {
            System.out.println("manage games");
        } else if (e.getSource() == confirmMoveButton) {
            String move = moveText.getText();
            if (isValidMove(move)) {
                makeMove(move);
            }

        }
    }
}
