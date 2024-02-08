package model;

// Representation of a 8x8 chess board. A board is a two-dimensional 8x8 int array.
// This first value refers to the row, the second one to the colum (starting from the black side).
// Note: the pieces are represented as integers in the following fashion:
// - 0 indicates an empty square (not a piece)
// - Pawn: 1 / Knight: 2 / Bishop: 3 / Rook: 4 / Queen: 5 / King: 6
// black pieces are negative, white pieces are positive
// Moves are represented with the standard chess notation: a-h,1-8, N,B,R,K,Q

import java.util.ArrayList;

public class Board {

    protected int[][] position;// the board with pieces
    private int eval;     // evaluation of the position


    // constructs a new board with the pieces in their starting positions
    public Board() {
        position = new int[][]{
                {-4, -2, -3, -5, -6, -3, -2, -4},
                {-1, -1, -1, -1, -1, -1, -1, -1},
                {0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0},
                {1, 1, 1, 1, 1, 1, 1, 1},
                {4, 2, 3, 5, 6, 3, 2, 4},
        };
    }

    // EFFECTS: gets all legal moves of the current position
    // !!!
    public ArrayList<String> getLegalMoves() {
        ArrayList<String> moves = new ArrayList<>();
        return moves;
    }


    // MODIFIES: this
    // EFFECTS:  empties the board
    public void clearBoard() {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                this.position[i][j] = 0;
            }
        }
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


    // EFFECTS: true if the move is legal on current the board
    //          moves is defined by the piece, the square the piece is on
    //          and the square the piece wants to go to.
    public boolean isLegalMove(int fromCol, int fromRow, int toCol, int toRow) {
        int piece = this.position[fromCol][fromRow];
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
        return isValidMoveForPiece;
    }


    private boolean isLegalPawnMove(int fromCol,int fromRow,int toCol,int toRow) {
        int piece = this.position[fromCol][fromRow];
        boolean emptyToPos = (this.position[toRow][toCol] == 0);
        boolean oneStep;
        boolean twoStep;
        boolean captures;
        if (piece > 0) { //executes if the pawn is white
            oneStep = (fromRow == toRow - 1) && emptyToPos;
            twoStep = (fromRow == toRow - 2) && (fromRow == 1)
                    && (this.position[toRow - 1][toCol - 1] == 0)
                    && emptyToPos;
            captures = (fromRow == toRow - 1)
                    && (fromCol == toCol - 1 || fromCol == toCol + 1)
                    && this.position[toRow][toCol] < 0;
        } else { //executes if the pawn is black
            oneStep = (fromRow == toRow + 1) && emptyToPos;
            twoStep = (fromRow == toRow + 2) && (fromRow == 5)
                    && (this.position[toRow + 1][toCol + 1] == 0)
                    && emptyToPos;
            captures = oneStep
                    && (fromCol == toCol - 1 || fromCol == toCol + 1)
                    && this.position[toRow][toCol] < 0;
        }
        return oneStep || twoStep || captures;
    }

    private boolean isLegalKnightMove(int fromCol,int fromRow,int toCol,int toRow) {
        int piece = this.position[fromCol][fromRow];
        int rowDelta = Math.abs(toRow - fromRow);
        int colDelta = Math.abs(toCol - fromCol);
        boolean toEnemyOrEmptySquare = (piece * this.position[toRow][toCol] <= 0);
        boolean goodNightMove = (rowDelta == 2 && colDelta == 1) || (rowDelta == 1 && colDelta == 2);
        return toEnemyOrEmptySquare && goodNightMove;
    }

    //!!!
    private boolean isLegalBishopMove(int fromCol,int fromRow,int toCol,int toRow) {
        int piece = this.position[fromCol][fromRow];
        boolean throughEmptySquares = true;
        for (int i = fromCol; i <= toCol; i++) {
            for (int j = fromRow; j <= toRow; j++) {
                if (this.position[i][j] != 0) {
                    throughEmptySquares = false;
                }
            }
        }
        boolean toEnemyOrEmptySquare = (piece * this.position[toRow][toCol] <= 0);
        return throughEmptySquares && toEnemyOrEmptySquare;
    }

    private boolean isLegalRookMove(int fromCol,int fromRow,int toCol,int toRow) {
        int piece = this.position[fromCol][fromRow];
        //!!!
        return false;
    }

    private boolean isLegalQueenMove(int fromCol,int fromRow,int toCol,int toRow) {
        int piece = this.position[fromCol][fromRow];
        return this.isLegalBishopMove(fromCol, fromRow, toCol, toRow)
                || this.isLegalRookMove(fromCol,fromRow,toCol,toRow);
    }

    // NOTE: for now the king can move into check!!!
    private boolean isLegalKingMove(int fromCol,int fromRow,int toCol,int toRow) {
        int piece = this.position[fromCol][fromRow];
        boolean toEnemyOrEmptySquare = (piece * this.position[toRow][toCol] <= 0);
        boolean isLeftOrRight = (fromCol == toCol + 1 || fromCol == toCol - 1);
        boolean isUpOrDown = (fromRow == toRow + 1 || fromRow == toRow - 1);
        boolean goodKingMove = isLeftOrRight && isUpOrDown;
        return toEnemyOrEmptySquare && goodKingMove;
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
        this.eval = eval;
        return eval;
    }

    // EFFECTS: returns the value of the piece
    private int pieceToVal(int piece) {
        if (piece < 0) {
            return this.blackPieceToVal(piece);
        } else {
            return this.whitePieceToValue(piece);
        }
    }

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

    // REQUIRES: piece, col and row are sensible to board
    // MODIFIES: this
    // EFFECTS: puts place on col,row of this board
    public void placePiece(int col,int row, int piece) {
        position[col][row] = piece;
    }

    // MODIFIES: this
    // EFFECTS: removes any piece from a square
    public void removePiece(int col,int row) {
        position[col][row] = 0;
    }

    public int getEval() {
        return this.eval;
    }

    public int[][] getPosition() {
        return this.position;
    }
}
