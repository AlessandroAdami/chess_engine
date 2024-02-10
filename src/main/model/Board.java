package model;

// Representation of a 8x8 chess board. A board is a two-dimensional 8x8 int array.
// This first value refers to the column, the second one to the row.
// Note: the pieces are represented as integers in the following fashion:
// - 0 indicates an empty square (not a piece)
// - Pawn: 1 / Knight: 2 / Bishop: 3 / Rook: 4 / Queen: 5 / King: 6
// black pieces are negative, white pieces are positive
// Moves are represented with the standard chess notation: a-h,1-8, N,B,R,K,Q
// NOTE: position[#col][#row] gets the piece

import java.util.ArrayList;

public class Board {

    protected int[][] position; // the board with pieces
    private String name; // name of the board


    // EFFECTS: constructs a new board with the pieces in their starting positions
    //          and name "New Board"
    public Board() {
        position = new int[][]{
                {4, 1, 0, 0, 0, 0, -1, -4},
                {2, 1, 0, 0, 0, 0, -1, -2},
                {3, 1, 0, 0, 0, 0, -1, -3},
                {5, 1, 0, 0, 0, 0, -1, -5},
                {6, 1, 0, 0, 0, 0, -1, -6},
                {3, 1, 0, 0, 0, 0, -1, -3},
                {2, 1, 0, 0, 0, 0, -1, -2},
                {4, 1, 0, 0, 0, 0, -1, -4},
        };
        name = "New Board";

    }

    // EFFECTS: constructs a new board with the pieces in their starting positions
    //          and given name
    public Board(String name) {
        position = new int[][]{
                {4, 1, 0, 0, 0, 0, -1, -4},
                {2, 1, 0, 0, 0, 0, -1, -2},
                {3, 1, 0, 0, 0, 0, -1, -3},
                {5, 1, 0, 0, 0, 0, -1, -5},
                {6, 1, 0, 0, 0, 0, -1, -6},
                {3, 1, 0, 0, 0, 0, -1, -3},
                {2, 1, 0, 0, 0, 0, -1, -2},
                {4, 1, 0, 0, 0, 0, -1, -4},
        };
        this.name = name;

    }

    // MODIFIES: this
    // EFFECTS: moves selected piece to square and captures any piece in
    //          the to square
    public void movePiece(int fromCol, int fromRow, int toCol, int toRow) {
        int piece = this.position[fromCol][fromRow];
        if (this.isLegalMove(fromCol,fromRow,toCol,toRow)) {
            this.position[fromCol][fromRow] = 0;
            this.position[toCol][toRow] = piece;
        }
    }


    // REQUIRES: fromCol,fromRow,toCol,toRow are in [0,7]
    //           i.e. they are valid squares of a board
    // EFFECTS: true if the move is legal on current the board
    //          moves is defined by the piece, the square the piece is on
    //          and the square the piece wants to go to.
    public boolean isLegalMove(int fromCol, int fromRow, int toCol, int toRow) {
        int piece = this.position[fromCol][fromRow];
        boolean pieceOnStartingSquare = piece != 0;
        boolean hasPieceMoved = (fromCol != toCol || fromRow != toRow);
        boolean toEnemyOrEmptySquare = (piece * this.position[toCol][toRow] <= 0);
        boolean isValidMoveForPiece;
        switch (piece) {
            case -1: case 1: isValidMoveForPiece = this.isLegalPawnMove(fromCol,fromRow,toCol,toRow);
                break;
            case -2: case 2:  isValidMoveForPiece = this.isLegalKnightMove(fromCol,fromRow,toCol,toRow);
            break;
            case -3: case 3: isValidMoveForPiece = this.isLegalBishopMove(fromCol,fromRow,toCol,toRow);
            break;
            case - 4: case 4: isValidMoveForPiece = this.isLegalRookMove(fromCol,fromRow,toCol,toRow);
                break;
            case -5: case 5: isValidMoveForPiece = this.isLegalQueenMove(fromCol,fromRow,toCol,toRow);
                break;
            case -6: case 6: isValidMoveForPiece = this.isLegalKingMove(fromCol,fromRow,toCol,toRow);
                break;
            default: isValidMoveForPiece = false;
        }
        return pieceOnStartingSquare && hasPieceMoved && toEnemyOrEmptySquare && isValidMoveForPiece;
    }

    private boolean isLegalPawnMove(int fromCol,int fromRow,int toCol,int toRow) {
        int piece = this.position[fromCol][fromRow];
        boolean oneStep; //pawn moves foreword onw
        boolean twoStep; //pawn moves foreword two
        boolean captures;//pawn takes a piece
        if (piece > 0) { //executes if the pawn is white
            oneStep = (fromRow == toRow - 1);
            twoStep = (fromRow == toRow - 2) && (fromRow == 1)
                    && (this.position[toCol - 1][toRow - 1] == 0);
            captures = (fromRow == toRow - 1)
                    && (fromCol == toCol - 1 || fromCol == toCol + 1)
                    && this.position[toCol][toRow] < 0;
        } else { //executes if the pawn is black
            oneStep = (fromRow == toRow + 1);
            twoStep = (fromRow == toRow + 2) && (fromRow == 6)
                    && (this.position[toCol + 1][toRow + 1] == 0);
            captures = oneStep
                    && (fromCol == toCol - 1 || fromCol == toCol + 1)
                    && this.position[toCol][toRow] < 0;
        }
        return oneStep || twoStep || captures;
    }

    private boolean isLegalKnightMove(int fromCol,int fromRow,int toCol,int toRow) {
        int rowDelta = Math.abs(toRow - fromRow);
        int colDelta = Math.abs(toCol - fromCol);
        return (rowDelta == 2 && colDelta == 1) || (rowDelta == 1 && colDelta == 2);
    }

    // !!! most definitely simplifiable.
    private boolean isLegalBishopMove(int fromCol,int fromRow,int toCol,int toRow) {
        boolean throughEmptySquares = true;
        if (fromCol < toCol) {
            if (fromRow < toRow) {
                throughEmptySquares = isLegalUpRightBishopPath(fromCol,fromRow,toCol,toRow);
            } else {
                throughEmptySquares = isLegalDownRightBishopPath(fromCol,fromRow,toCol,toRow);
            }
        } else if (fromRow < toRow) {
            throughEmptySquares = isLegalUpLeftBishopPath(fromCol,fromRow,toCol,toRow);
        } else {
            throughEmptySquares = isLegalDownLeftBishopPath(fromCol, fromRow, toCol, toRow);
        }
        return throughEmptySquares;
    }

    private boolean isLegalUpRightBishopPath(int fromCol,int fromRow,int toCol,int toRow) {
        boolean throughEmptySquares = true;
        for (int col = fromCol + 1, row = fromRow - 1; col < toCol; col++, row++) {
            if (this.position[col][row] != 0) {
                throughEmptySquares = false;
                break;
            }
        }
        return throughEmptySquares;
    }

    private boolean isLegalDownRightBishopPath(int fromCol,int fromRow,int toCol,int toRow) {
        boolean throughEmptySquares = true;
        for (int col = fromCol + 1, row = fromRow - 1; col < toCol; col++, row--) {
            if (this.position[col][row] != 0) {
                throughEmptySquares = false;
                break;
            }
        }
        return throughEmptySquares;
    }

    private boolean isLegalUpLeftBishopPath(int fromCol,int fromRow,int toCol,int toRow) {
        boolean throughEmptySquares = true;
        for (int col = fromCol - 1, row = fromRow + 1; col > toCol; col--, row++) {
            if (this.position[col][row] != 0) {
                throughEmptySquares = false;
                break;
            }
        }
        return throughEmptySquares;
    }

    private boolean isLegalDownLeftBishopPath(int fromCol,int fromRow,int toCol,int toRow) {
        boolean throughEmptySquares = true;
        for (int col = fromCol -1, row = fromRow -1; col > toCol; col--, row--) {
            if (this.position[col][row] != 0) {
                throughEmptySquares = false;
                break;
            }
        }
        return throughEmptySquares;
    }

    private boolean isLegalRookMove(int fromCol,int fromRow,int toCol,int toRow) {
        boolean goodRookMove = (fromCol == toCol || fromRow == toRow);
        boolean throughEmptySquares = isRookPathClear(fromCol,fromRow,toCol,toRow);
        return goodRookMove && throughEmptySquares;
    }

    private boolean isRookPathClear(int fromCol,int fromRow,int toCol,int toRow) {
        int direction;
        boolean throughEmptySquares = true;
        if (fromCol == toCol) {
            direction = Integer.compare(toRow, fromRow);
            for (int row = fromRow + direction; row != toRow; row += direction) {
                if (this.position[fromCol][row] != 0) {
                    throughEmptySquares = false;
                    break;
                }
            }
        } else {
            direction = Integer.compare(toCol,fromCol);
            for (int col = fromCol + direction; col != toCol; col += direction) {
                if (this.position[col][fromRow] != 0) {
                    throughEmptySquares = false;
                    break;
                }
            }
        }
        return throughEmptySquares;
    }

    private boolean isLegalQueenMove(int fromCol, int fromRow,int toCol,int toRow) {
        return this.isLegalBishopMove(fromCol, fromRow, toCol, toRow)
                || this.isLegalRookMove(fromCol,fromRow,toCol,toRow);
    }

    private boolean isLegalKingMove(int fromCol,int fromRow,int toCol,int toRow) {
        boolean isLeftOrRight = (fromCol == toCol + 1 || fromCol == toCol - 1);
        boolean isUpOrDown = (fromRow == toRow + 1 || fromRow == toRow - 1);
        return isLeftOrRight || isUpOrDown;
    }



    // MODIFIES: this
    // EFFECTS: evaluates the current board position
    public int evaluatePos() {
        int eval = 0;
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                eval = eval + this.pieceToVal(this.position[i][j]);
            }
        }
        return eval;
    }

    // REQUIRES: piece is a piece
    // EFFECTS: returns the value of the piece
    private int pieceToVal(int piece) {
        if (piece < 0) {
            return this.blackPieceToVal(piece);
        } else {
            return this.whitePieceToValue(piece);
        }
    }

    // REQUIRES: piece is a piece
    // EFFECTS: maps white piece to its evaluation
    private int whitePieceToValue(int piece) {
        int val;
        switch (piece) {
            case 1: val = 1;
            break;
            case 2: case 3: val = 3;
            break;
            case 4: val = 5;
            break;
            case 5: val = 9;
            break;
            case 6: val = 105;
            break;
            default: val = 0;
        }
        return val;
    }

    // REQUIRES: piece is a piece
    // EFFECTS: maps black piece to its evaluation
    private int blackPieceToVal(int piece) {
        int val;
        switch (piece) {
            case -1: val = -1;
                break;
            case -2: case -3: val = -3;
                break;
            case -4: val = -5;
                break;
            case -5: val = -9;
                break;
            case -6: val = -105;
                break;
            default: val = 0;
        }
        return val;
    }


    // EFFECTS: displays a board with number strings as established above
    public void displayBoard() {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                System.out.print(this.boardToStringBoard()[i][j] + " ");
            }
            System.out.println();
        }
    }

    // EFFECTS: sends each piece to a string for printing purposes.
    //          it also puts the board in the right position array-wise
    private String[][] boardToStringBoard() {
        String[][] stringBoard = new String[8][8];
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                stringBoard[i][j] = String.valueOf(this.position[i][j]);
            }
        }
        String[][] straightStringBoard = new String[8][8];
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                straightStringBoard[7 - j][7 - i] = stringBoard[i][j];
            }
        }
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 4; j++) {
                String stick = straightStringBoard[i][j];
                straightStringBoard[i][j] = straightStringBoard[i][7 - j];
                straightStringBoard[i][7 - j] = stick;
            }
        }
        return straightStringBoard;
    }

    // MODIFIES: this
    // EFFECTS: empties the board
    public void clearBoard() {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                this.position[i][j] = 0;
            }
        }
    }

    // REQUIRES: piece, col and row are sensible to board
    // MODIFIES: this
    // EFFECTS: puts place on col,row of this board
    public void placePiece(int col,int row, int piece) {
        position[col][row] = piece;
    }

    // REQUIRES: col and row are sensible to board
    // MODIFIES: this
    // EFFECTS: removes any piece from a square
    public void removePiece(int col,int row) {
        position[col][row] = 0;
    }

    public int[][] getPosition() {
        return this.position;
    }

    public String getName() {
        return name;
    }
}
