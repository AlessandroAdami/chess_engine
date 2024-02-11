package ui;

import model.Board;
import model.BoardList;

import java.util.Scanner;

// chess game application

public class ChessGame {

    private BoardList boards;
    private Board currentBoard;
    private Scanner input;

    public ChessGame() {
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

        System.out.println("\nGoodbye!");
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

    private void showOptions() {
        System.out.println("\nChose from:");
        System.out.println("\tplay -> play current game");
        System.out.println("\tnew game -> create a new game");
        System.out.println("\tlist -> show list of your games");
        System.out.println("\tquit -> quit");
    }

    private void showCurrentBoard() {
        boolean isCurrentBoardEmpty = currentBoard.isEmpty();
        if (!isCurrentBoardEmpty) {
            System.out.println(currentBoard.getName() + ":");
            currentBoard.displayBoard();
        }
    }

    //MODIFIES: this
    //EFFECTS: lets player make moves in current board
    public void play() {
        String command = "";
        while (command.equals("")) {
            System.out.println("\nMake a move:");
            System.out.println("\nEnter: 'fromCol,fromRow,toCol,toRow");

            command = input.next();

            if (isValidMove(command)) {
                makeMove(command);
            } else {
                System.out.println("Not a valid move.");
            }
        }
    }

    //EFFECTS: true if move is valid in current Board
    private boolean isValidMove(String move) {
        int fromCol = Integer.parseInt(move.substring(0,1));
        int fromRow = Integer.parseInt(move.substring(2,3));
        int toCol   = Integer.parseInt(move.substring(4,5));
        int toRow   = Integer.parseInt(move.substring(6));
        if (((0 <= fromCol) && (0 <= fromRow) && (0 <= toCol) && (0 <= toRow))
                && ((fromCol < 8) && (fromRow < 8) && (toCol < 8) && (toRow < 8))) {
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
    }

    private void manageBoards() {
        String command = "";
        while (command.equals("")) {
            System.out.println("\nChose from:");
            System.out.println("\tselect -> select a game to play");
            System.out.println("\tdelete -> delete one of the games");
            command = input.next();

            if (command.equals("select")) {
                selectBoard();
            } else if (command.equals("delete")) {
                deleteBoard();
            }
        }
    }

    // MODIFIES: this
    // EFFECTS: lets player select the current game from the list of games
    private void selectBoard() {
        String boardName = "";
        while (boardName.equals("")) {
            System.out.println("Enter the name of a game: ");
            for (int i = 0; i < boards.getBoards().size(); i++) {
                System.out.println("- " + boards.getBoards().get(i).getName());
            }

            boardName = input.next();
            for (int i = 0; i < boards.getBoards().size(); i++) {
                if (boardName.equals(boards.getBoards().get(i).getName())) {
                    currentBoard = boards.getBoards().get(i);
                    break;
                } else {
                    System.out.println("Enter a valid board.");
                }
            }
        }
    }

    //MODIFIES: this
    //EFFECTS: deletes a board from boards
    private void deleteBoard() {
        String boardName = "";

        while (boardName.equals("")) {
            System.out.println("Enter the name of a game:");
            for (int i = 0; i < boards.getBoards().size(); i++) {
                System.out.println("- " + boards.getBoards().get(i).getName());
            }

            boardName = input.next();
            for (int i = 0; i < boards.getBoards().size(); i++) {
                if (boardName.equals(boards.getBoards().get(i).getName())) {
                    Board removedBoard = new Board();
                    removedBoard = boards.removeBoard(i);
                    if (currentBoard.equals(removedBoard)) {
                        currentBoard.clearBoard();
                        break;
                    }
                } else {
                    System.out.println("Enter a valid board.");
                }
            }
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
