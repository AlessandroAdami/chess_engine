package ui;

import model.Board;
import model.BoardList;
import model.ChessGame;
import persistence.JsonReader;
import persistence.JsonWriter;

import java.io.IOException;
import java.util.Scanner;

// Chess game application. Allows the user to play various games
// and analyze the current position in each board.

public class ChessGameApp {
    private static final String JSON_STORE = "./data/chessgame.json";
    private JsonWriter jsonWriter;
    private JsonReader jsonReader;
    private Scanner input;
    private ChessGame chessGame;
    private BoardList boards;
    private Board currentBoard;


    //EFFECTS: constructs a new ChessGameApp with a chess game
    //         a reader and writer to file and a user input
    public ChessGameApp() {
        jsonReader = new JsonReader(JSON_STORE);
        jsonWriter = new JsonWriter(JSON_STORE);
        runChessGame();
    }

    // MODIFIES: this
    // EFFECTS: processes player's decisions
    private void runChessGame() {
        boolean keepGoing = true;
        String command;

        setup();

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
        boolean isCurrentBoardNull = currentBoard == null;
        if (!isCurrentBoardNull) {
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
        currentBoard.movePiece(fromCol,fromRow,toCol,toRow);
        if (currentBoard.isPawnToPromote(toCol,toRow)) {
            promotePawn(toCol,toRow);
        }
    }

    //MODIFIES: this
    //EFFECTS: lets player pick a piece to promote a pawn to
    private void promotePawn(int col,int row) {
        int promotion = 0;
        while (true) {
            System.out.println("\nSelect:");
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
            System.out.println("Saved games to " + JSON_STORE);
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
            System.out.println("Loaded games from " + JSON_STORE);

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
}
