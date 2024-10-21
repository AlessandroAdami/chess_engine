package model;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.stream.Collectors;

import model.pieces.*;
import org.json.JSONObject;

public class Board extends JPanel {

    //TODO: NB: a game is uniquely identifies by a series of fen strings
    // that  indicates a way to keep track of the moves in the game

    private static final String fenStartingPosition = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";

    private String fenPosition;

    private String name;

    public int tileSize = 85;

    int cols = 8;
    int rows = 8;

    ArrayList<Piece> pieceList = new ArrayList<>();

    public CheckScanner checkScanner = new CheckScanner(this);

    public Piece selectedPiece;

    Input input = new Input(this);

    public int enPassantTile = -1;

    private boolean isWhiteToMove = true;
    private boolean isGameOver = false;

    public Board(String name) {
        this.name = name;
        this.setPreferredSize(new Dimension(cols * tileSize, rows * tileSize));

        this.addMouseListener(input);
        this.addMouseMotionListener(input);

        loadPositionFromFen(fenStartingPosition);
    }

    public Board() {
        this("New Game");
    }

    public Piece getPiece(int col, int row) {
        for (Piece p : pieceList) {
            if (p.col == col && p.row == row) {
                return p;
            }
        }
        return null;
    }

    //true if move is valid in current board
    public boolean isValidMove(Move move) {

        if (isGameOver) {
            return false;
        }

        if (move.piece.isWhite != isWhiteToMove) {
            return false;
        }

        if (sameTeam(move.piece,move.capture)) {
            return false;
        }
        if (!move.piece.isValidMovement(move.newCol,move.newRow)) {
            return false;
        }
        if (move.piece.moveCollidesWithPiece(move.newCol,move.newRow)) {
            return false;
        }
        return !checkScanner.isKingChecked(move);
    }

    public boolean sameTeam(Piece p1, Piece p2) {
        if (p1 == null || p2 == null) {
            return false;
        }

        return p1.isWhite == p2.isWhite;
    }

    public void makeMove(Move move) {

        if (move.piece.name.equals("Pawn")) {
            movePawn(move);
        } else {
            enPassantTile = -1;
        }

        if (move.piece.name.equals("King")) {
            moveKing(move);
        }

        move.piece.col = move.newCol;
        move.piece.row = move.newRow;

        move.piece.xPos = move.newCol * tileSize;
        move.piece.yPos = move.newRow * tileSize;

        move.piece.isFirstMove = false;

        capture(move.capture);

        isWhiteToMove = !isWhiteToMove;

        updateGameState();

    }

    private void moveKing(Move move) {

        if (Math.abs(move.piece.col - move.newCol) == 2) {
            Piece rook;
            if (move.piece.col < move.newCol) {
                rook = getPiece(7,move.piece.row);
                rook.col = 5;
            } else {
                rook = getPiece(0,move.piece.row);
                rook.col = 3;
            }
            rook.xPos = rook.col * tileSize;
        }

        move.piece.col = move.newCol;
        move.piece.row = move.newRow;

        move.piece.xPos = move.newCol * tileSize;
        move.piece.yPos = move.newRow * tileSize;

        move.piece.isFirstMove = false;

        capture(move.capture);

    }

    public void movePawn(Move move) {
        //en passant
        int colorIndex = move.piece.isWhite ? 1 : - 1;

        if (getTileNum(move.newCol,move.newRow) == enPassantTile) {
            move.capture = getPiece(move.newCol,move.newRow + colorIndex);
        }

        if (Math.abs(move.piece.row - move.newRow) == 2) {
            enPassantTile = getTileNum(move.newCol,move.newRow + colorIndex);
        } else {
            enPassantTile = -1;
        }

        //promotions
        colorIndex = move.piece.isWhite ? 0 : 7;
        if (move.newRow == colorIndex) {
            promotePawn(move);
        }

    }

    //TODO: update so that there is the option to choose what to promote to
    private void promotePawn(Move move) {
        JPanel promoteOption = new JPanel();
        promoteOption.setPreferredSize(new Dimension(4 * tileSize, 4 * tileSize));
        this.add(promoteOption);



        pieceList.add(new Queen(this, move.newCol,move.newRow,move.piece.isWhite));
        capture(move.piece);
    }

    public void capture(Piece piece) {
        pieceList.remove(piece);
    }

    public int getTileNum(int col, int row) {
        return row * rows + col;
    }

    Piece findKing(boolean isWhite) {
        for (Piece p : pieceList) {
            if (isWhite == p.isWhite && p.name.equals("King")) {
                return p;
            }
        }
        return null;
    }

    public void loadPositionFromFen(String fenString) {

        pieceList.clear();
        String[] parts = fenString.split(" ");

        // set up pieces
        String position = parts[0];
        int row = 0;
        int col = 0;
        for (int i = 0; i < position.length(); i++) {
            char ch = position.charAt(i);
            if (ch == '/') {
                row++;
                col = 0;
            } else if (Character.isDigit(ch)) {
                col += Character.getNumericValue(ch);
            } else {
                boolean isWhite = Character.isUpperCase(ch);
                char pieceChar = Character.toLowerCase(ch);

                switch (pieceChar) {
                    case 'r':
                        pieceList.add(new Rook(this,col,row,isWhite));
                        break;
                    case 'n':
                        pieceList.add(new Knight(this,col,row,isWhite));
                        break;
                    case 'b':
                        pieceList.add(new Bishop(this,col,row,isWhite));
                        break;
                    case 'q':
                        pieceList.add(new Queen(this,col,row,isWhite));
                        break;
                    case 'k':
                        pieceList.add(new King(this,col,row,isWhite));
                        break;
                    case 'p':
                        pieceList.add(new Pawn(this,col,row,isWhite));
                        break;
                }
                col++;
            }
        }
        //color to move
        isWhiteToMove = parts[1].equals("w");

        //castling
        Piece bqr = getPiece(0,0);
        if (bqr instanceof Rook) {
            bqr.isFirstMove = parts[2].contains("q");
        }
        Piece bkr = getPiece(7,0);
        if (bqr instanceof Rook) {
            bqr.isFirstMove = parts[2].contains("k");
        }
        Piece wqr = getPiece(0,7);
        if (bqr instanceof Rook) {
            bqr.isFirstMove = parts[2].contains("Q");
        }
        Piece wkr = getPiece(7,7);
        if (bqr instanceof Rook) {
            bqr.isFirstMove = parts[2].contains("K");
        }

        //en passant square
        if (parts[3].equals("-")) {
            enPassantTile = -1;
        } else {
            enPassantTile = (7 - (parts[3].charAt(1) - '1')) * 8 + (parts[3].charAt(0) - 'a');
        }
    }


    private void updateGameState() {
        Piece king = findKing(isWhiteToMove);

        if (checkScanner.isGameOver(king)) {
            if (checkScanner.isKingChecked(new Move(this,king,king.col,king.row))) {
                System.out.println(isWhiteToMove ? "Black Wins!" : "White Wins!");
            } else {
                System.out.println("Stalemate!");
            }
        } else if (insufficientMaterial(true) && insufficientMaterial(false)) {
            System.out.println("Insufficient Material");
            isGameOver = true;
        }
    }

    //TODO: this is not entirely correct
    private boolean insufficientMaterial(boolean isWhite) {
        ArrayList<String> names = pieceList.stream()
                .filter(p -> p.isWhite == isWhite)
                .map(p -> p.name)
                .collect(Collectors.toCollection(ArrayList::new));

        if (names.contains("Queen") || names.contains("Rook") || names.contains("Pawn")) {
            return false;
        }
        return names.size() < 3;
    }

    public void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;

        //paint board
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                g2d.setColor((c + r) % 2 == 0 ? new Color(227, 198, 181) : new Color(157, 105, 3));
                g2d.fillRect(c * tileSize, r * tileSize, tileSize, tileSize);
            }
        }
        // paint hightlights
        if (selectedPiece != null) {
            for (int r = 0; r < rows; r++) {
                for (int c = 0; c < cols; c++) {

                    if (isValidMove(new Move(this, selectedPiece, c, r))) {
                        g2d.setColor(new Color(133, 217, 191));
                        g2d.fillRect(c * tileSize, r * tileSize, tileSize, tileSize);
                    }
                }
            }
        }

        //paint pieces
        for (Piece piece : pieceList) {
            piece.paint(g2d);
        }
    }

    //TODO: complete json methods

    //EFFECTS: return board as json object
    public JSONObject toJson() {
        JSONObject json = new JSONObject();
        json.put("position", fenPosition);
        json.put("name", name);

        return json;
    }

    public int evaluatePos() {
        return 1;
    }

    public boolean samePosition(Board other) {
        return fenPosition.equals(other.getFenPosition());
    }

    public boolean isEmpty() {
        return pieceList.isEmpty();
    }

    //getters & setters

    public ArrayList<Piece> getPieceList() {
        return pieceList;
    }

    public boolean getIsWhiteToMove() {
        return isWhiteToMove;
    }

    public void setIsWhiteToMove(boolean isWhiteToMove) {
        this.isWhiteToMove = isWhiteToMove;
    }

    public String getFenPosition() {
        return fenPosition;
    }

    public String getCanTheyCastle(boolean isWhite) {
        String[] parts = fenPosition.split(" ");
        String castleStr = parts[2];
        String queen = isWhite ? "Q" : "q";
        String king  = isWhite ? "K" : "k";

        if (castleStr.contains(king) && castleStr.contains(queen)) {
            return queen + king;
        } else if (castleStr.contains(king)) {
            return king;
        } else if (castleStr.contains(queen)) {
            return queen;
        }
        return "";
    }

    //TODO: finish this
    public void setCastle(boolean isWhite, String castleStr) {

        String[] parts = fenPosition.split(" ");
        String[] castleArray = parts[2].split(""); //= [K][Q][k][q]

        String[] newCastleArray = new String[4];

        for (int i = 0; i < castleArray.length; i++) {
            if (castleArray[i].equals("K")) {
                for (int j = 0; j < i; j++) {
                    newCastleArray[j] = "";
                }
                newCastleArray[0] = "K";
            }

            if (castleArray[1].equals("Q"))
                return;
        }

        this.fenPosition = String.join(" ", parts);
    }
}
