package model;

// Representation of a 8x8 chess board. A board is a two-dimensional 8x8 int array.
// This first value refers to the column, the second one to the row.
// Note: the pieces are represented as integers in the following fashion:
// - 0 indicates an empty square (not a piece)
// - Pawn: 1 / Knight: 2 / Bishop: 3 / Rook: 4 / Queen: 5 / King: 6
// black pieces have negative values, white pieces have positive values
// Moves are represented with the standard chess notation: a-h,1-8, N,B,R,K,Q (n,b,r,k,q)


public class Board {

    private int[][] position; // the board with pieces
    private String name;
    private boolean isWhitesTurn;


    // EFFECTS: constructs a new board
    //          with the pieces in their starting positions
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
        isWhitesTurn = true;

    }

    // EFFECTS: constructs a new board
    //          with the pieces in their starting positions
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
        isWhitesTurn = true;
    }

    // REQUIRES: fromCol,fromRow,toCol,toRow are in [0,7]
    //           i.e. they are valid squares of a board
    // MODIFIES: this
    // EFFECTS: if move is legal, moves selected piece to square and captures any piece in
    //          the to square. Otherwise, do nothing
    public void movePiece(int fromCol, int fromRow, int toCol, int toRow) {
        int piece = this.position[fromCol][fromRow];
        if (this.isLegalMove(fromCol,fromRow,toCol,toRow)) {
            this.position[fromCol][fromRow] = 0;
            this.position[toCol][toRow] = piece;
            nextTurn();
        }
    }


    // REQUIRES: fromCol,fromRow,toCol,toRow are in [0,7]
    //           i.e. they are valid squares of a board
    // EFFECTS: true if the move is legal on current the board
    //          moves is defined by the square the piece is on
    //          and the square the piece wants to go to.
    public boolean isLegalMove(int fromCol, int fromRow, int toCol, int toRow) {
        int piece = this.position[fromCol][fromRow];
        boolean whiteTurnToMove = (((piece > 0) && isWhitesTurn)
                || (piece < 0) && (!isWhitesTurn));
        boolean pieceOnStartingSquare = (piece != 0);
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
        return whiteTurnToMove && pieceOnStartingSquare && hasPieceMoved && toEnemyOrEmptySquare && isValidMoveForPiece;
    }

    //EFFECTS: true if pawn move is legal
    private boolean isLegalPawnMove(int fromCol,int fromRow,int toCol,int toRow) {
        int piece = this.position[fromCol][fromRow];
        boolean oneStep; //pawn moves foreword one
        boolean twoStep; //pawn moves foreword two
        boolean captures;//pawn takes a piece
        if (piece > 0) { //executes if the pawn is white
            oneStep = (fromCol == toCol) && (fromRow + 1 == toRow) && position[toCol][toRow] == 0;
            twoStep = (fromCol == toCol) && (fromRow + 2 == toRow) && (fromRow == 1)
                    && (this.position[fromCol][fromRow + 1] == 0)  && position[toCol][toRow] == 0;
            captures = (fromRow + 1 == toRow)
                    && (fromCol == toCol - 1 || fromCol == toCol + 1)
                    && this.position[toCol][toRow] < 0;
        } else { //executes if the pawn is black
            oneStep = (fromCol == toCol) && (fromRow == toRow + 1) && position[toCol][toRow] == 0;
            twoStep = (fromCol == toCol) && (fromRow - 2 == toRow) && (fromRow == 6)
                    && (this.position[fromCol][fromRow - 1] == 0)  && position[toCol][toRow] == 0;
            captures = (fromRow == toRow + 1)
                    && (fromCol == toCol - 1 || fromCol == toCol + 1)
                    && this.position[toCol][toRow] > 0;
        }
        return oneStep || twoStep || captures;
    }

    //EFFECTS: true if knight move is legal
    private boolean isLegalKnightMove(int fromCol,int fromRow,int toCol,int toRow) {
        int rowDelta = Math.abs(toRow - fromRow);
        int colDelta = Math.abs(toCol - fromCol);
        return (rowDelta == 2 && colDelta == 1) || (rowDelta == 1 && colDelta == 2);
    }

    //EFFECTS: true if bishop move is legal
    private boolean isLegalBishopMove(int fromCol,int fromRow,int toCol,int toRow) {
        boolean goodBishopMove = Math.abs(fromCol - toCol) == Math.abs(fromRow - toRow);
        int colDir = (int) Math.signum(toCol - fromCol);
        int rowDir = (int) Math.signum(toRow - fromRow);
        boolean throughEmptySquares = isBishopPathEmpty(fromCol,fromRow,toCol,colDir,rowDir);
        return goodBishopMove && throughEmptySquares;
    }

    private boolean isBishopPathEmpty(int fromCol, int fromRow, int toCol, int colDir, int rowDir) {
        boolean throughEmptySquares = true;
        for (int col = fromCol + colDir, row = fromRow + rowDir; col != toCol; col = col + colDir, row = row + rowDir) {
            if (this.position[col][row] != 0) {
                throughEmptySquares = false;
                break;
            }
        }
        return throughEmptySquares;
    }

    //EFFECTS: true if rook move is legal
    private boolean isLegalRookMove(int fromCol,int fromRow,int toCol,int toRow) {
        boolean goodRookMove = (fromCol == toCol || fromRow == toRow);
        int colDir = (int) Math.signum(toCol - fromCol);
        int rowDir = (int) Math.signum(toRow - fromRow);
        boolean throughEmptySquares = isRookPathEmpty(fromCol,fromRow,toCol,toRow, colDir, rowDir);
        return goodRookMove && throughEmptySquares;
    }

    private boolean isRookPathEmpty(int fromCol, int fromRow, int toCol, int toRow, int colDir, int rowDir) {
        boolean throughEmptySquares = true;
        if (rowDir == 0) {
            for (int col = fromCol + colDir; col != toCol; col = col + colDir) {
                if (this.position[col][fromRow] != 0) {
                    throughEmptySquares = false;
                    break;
                }
            }
        } else {
            for (int row = fromRow + rowDir; row != toRow; row = row + rowDir) {
                if (this.position[fromCol][row] != 0) {
                    throughEmptySquares = false;
                    break;
                }
            }
        }
        return throughEmptySquares;
    }

    //EFFECTS: true if queen move is legal
    private boolean isLegalQueenMove(int fromCol, int fromRow,int toCol,int toRow) {
        return isLegalBishopMove(fromCol, fromRow, toCol, toRow)
                || isLegalRookMove(fromCol,fromRow,toCol,toRow);
    }

    //EFFECTS: true if king move is legal
    private boolean isLegalKingMove(int fromCol,int fromRow,int toCol,int toRow) {
        boolean isLeftOrRight = (Math.abs(toCol - fromCol) == 1) && fromRow == toRow;
        boolean isUpOrDown = (Math.abs(toRow - fromRow) == 1) && fromCol == toCol;
        boolean isDiagonal = (Math.abs(toCol - fromCol) == 1) && (Math.abs(toRow - fromRow) == 1);
        return isLeftOrRight || isUpOrDown || isDiagonal;
    }

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
        int val;
        switch (piece) {
            case 1: case -1: val = 1;
            break;
            case 2: case -2: case 3: case -3: val = 3;
            break;
            case 4: case -4: val = 5;
            break;
            case 5: case -5: val = 9;
            break;
            case 6: case -6: val = 105;
            break;
            default: val = 0;
        }
        if (piece < 0) {
            val = val * -1;
        }
        return val;
    }

    // EFFECTS: sends each piece to a string for printing purposes.
    //          it also puts the board in the right position array-wise
    //          notice the resulting board has black on top
    public String[][] boardToStringBoard() {
        String[][] stringBoard = new String[8][8];
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                stringBoard[i][j] = pieceToString(position[i][j]);
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

    //REQUIRES: piece is a valid piece
    //EFFECTS: sends each int piece to the corresponding one-string
    private String pieceToString(int piece) {
        String val;
        switch (piece) {
            case 0: val = "0";
            break;
            case -1: case 1: val = "P";
            break;
            case -2: case 2: val = "N";
            break;
            case -3: case 3: val = "B";
            break;
            case -4: case 4: val = "R";
            break;
            case -5: case 5: val = "Q";
            break;
            default: val = "K";
            break;
        }
        if (piece < 0) {
            val = val.toLowerCase();
        }
        return val;
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

    //EFFECTS: true if board is empty
    public boolean isEmpty() {
        boolean isEmpty = true;
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (position[i][j] != 0) {
                    isEmpty = false;
                    break;
                }
            }
        }
        return isEmpty;
    }

    //EFFECTS: changes the turn (whose move it is)
    public void nextTurn() {
        this.isWhitesTurn = !isWhitesTurn;
    }

    // REQUIRES: col,row are in [0,7]
    //           i.e. they point to a valid square of the board of a board
    //           piece is a valid piece
    // MODIFIES: this
    // EFFECTS: puts piece on col,row of this board
    public void placePiece(int col,int row, int piece) {
        position[col][row] = piece;
    }

    // getters and setters

    // REQUIRES: col,row are in [0,7]
    //           i.e. they point to a valid square of the board of a board
    public int getPiece(int col, int row) {
        return position[col][row];
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }


    public int[][] getPosition() {
        return this.position;
    }

    public boolean getIsWhitesTurn() {
        return isWhitesTurn;
    }
}
