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
            System.out.println(currentBoard.getName() + ":");
            currentBoard.displayBoard();
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

    //MODIFIES: this
    //EFFECTS: lets player make moves in current board
    public void play() {
        //!!!
    }

    private void manageBoards() {
        String command;
        command = input.next();
        System.out.println("\nChose from:");
        System.out.println("\tselect -> select a game to play");
        System.out.println("\tdelete -> delete one of the games");

        for (int i = 0; i < boards.getBoards().size(); i++) {
            System.out.println(boards.getBoards().get(i).getName());
        }
        if (command.equals("select")) {
            selectBoard();
        } else if (command.equals("delete")) {
            deleteBoard();
        }
    }

    // MODIFIES: this
    // EFFECTS: lets player select a game in boards
    private void selectBoard() {
        String command;
        command = input.next();
        System.out.println("Enter the name of a game: ");
        for (int i = 0; i <= boards.getBoards().size(); i++) {
            if (command.equals(boards.getBoards().get(i).getName())) {
                currentBoard = boards.getBoards().get(i);
            } else {
                System.out.println("Enter a valid board.");
            }
        }
    }

    //EFFECTS: deletes a board from boards
    private void deleteBoard() {
        String command;
        boolean keepGoing = true;

        while (keepGoing) {
            command = input.next();
            System.out.println("Enter the name of a game:");
            for (int i = 0; i <= boards.getBoards().size(); i++) {
                if (command.equals(boards.getBoards().get(i).getName())) {
                    boards.removeBoard(i);
                    keepGoing = false;
                } else {
                    System.out.println("Enter a valid board.");
                }
            }
        }
    }

    // MODIFIES: this
    // EFFECTS: creates new board and makes it the current board
    private void createNewBoard() {
        System.out.println("Enter new game name: ");
        String name = input.next();
        Board b = new Board(name);
        this.boards.addBoard(b);
        this.currentBoard = b;
    }
}
