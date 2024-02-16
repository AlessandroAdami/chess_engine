package model;

// Representation of a 8x8 chess board. A board is a two-dimensional 8x8 int array.
// This first value refers to the column, the second one to the row.
// Note: the pieces are represented as integers in the following fashion:
// - 0 indicates an empty square (not a piece)
// - Pawn: 1 / Knight: 2 / Bishop: 3 / Rook: 4 / Queen: 5 / King: 6
// black pieces have negative values, white pieces have positive values
// Moves are represented with the standard chess notation: a-h,1-8, N,B,R,K,Q
// TODO: lookup enums and understand how they could simplify code


public class Board {

    private int[][] position; // the board with pieces
    private String name; // name of the board
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
    // EFFECTS: moves selected piece to square and captures any piece in
    //          the to square
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

    private boolean isLegalPawnMove(int fromCol,int fromRow,int toCol,int toRow) {
        int piece = this.position[fromCol][fromRow];
        boolean oneStep; //pawn moves foreword one
        boolean twoStep; //pawn moves foreword two
        boolean captures;//pawn takes a piece
        if (piece > 0) { //executes if the pawn is white
            oneStep = (fromCol == toCol) && (fromRow + 1 == toRow);
            twoStep = (fromCol == toCol) && (fromRow + 2 == toRow) && (fromRow == 1)
                    && (this.position[fromCol][fromRow + 1] == 0);
            captures = (fromRow + 1 == toRow)
                    && (fromCol == toCol - 1 || fromCol == toCol + 1)
                    && this.position[toCol][toRow] < 0;
        } else { //executes if the pawn is black
            oneStep = (fromCol == toCol) && (fromRow == toRow + 1);
            twoStep = (fromCol == toCol) && (fromRow - 2 == toRow) && (fromRow == 6)
                    && (this.position[fromCol][fromRow - 1] == 0);
            captures = (fromRow == toRow + 1)
                    && (fromCol == toCol - 1 || fromCol == toCol + 1)
                    && this.position[toCol][toRow] > 0;
        }
        return oneStep || twoStep || captures;
    }

    private boolean isLegalKnightMove(int fromCol,int fromRow,int toCol,int toRow) {
        int rowDelta = Math.abs(toRow - fromRow);
        int colDelta = Math.abs(toCol - fromCol);
        return (rowDelta == 2 && colDelta == 1) || (rowDelta == 1 && colDelta == 2);
    }

    // TODO: most definitely simplifiable.
    private boolean isLegalBishopMove(int fromCol,int fromRow,int toCol,int toRow) {
        boolean throughEmptySquares;
        if (fromCol < toCol) {
            if (fromRow < toRow) {
                throughEmptySquares = isLegalUpRightBishopPath(fromCol,fromRow,toCol);
            } else {
                throughEmptySquares = isLegalDownRightBishopPath(fromCol,fromRow,toCol);
            }
        } else if (fromRow < toRow) {
            throughEmptySquares = isLegalUpLeftBishopPath(fromCol,fromRow,toCol);
        } else {
            throughEmptySquares = isLegalDownLeftBishopPath(fromCol, fromRow, toCol);
        }
        return throughEmptySquares;
    }

    private boolean isLegalUpRightBishopPath(int fromCol,int fromRow,int toCol) {
        boolean throughEmptySquares = true;
        for (int col = fromCol + 1, row = fromRow + 1; col < toCol; col++, row++) {
            if (this.position[col][row] != 0) {
                throughEmptySquares = false;
                break;
            }
        }
        return throughEmptySquares;
    }

    private boolean isLegalUpLeftBishopPath(int fromCol,int fromRow,int toCol) {
        boolean throughEmptySquares = true;
        for (int col = fromCol - 1, row = fromRow + 1; col > toCol; col--, row++) {
            if (this.position[col][row] != 0) {
                throughEmptySquares = false;
                break;
            }
        }
        return throughEmptySquares;
    }

    private boolean isLegalDownRightBishopPath(int fromCol,int fromRow,int toCol) {
        boolean throughEmptySquares = true;
        for (int col = fromCol + 1, row = fromRow - 1; col < toCol; col++, row--) {
            if (this.position[col][row] != 0) {
                throughEmptySquares = false;
                break;
            }
        }
        return throughEmptySquares;
    }

    private boolean isLegalDownLeftBishopPath(int fromCol,int fromRow,int toCol) {
        boolean throughEmptySquares = true;
        for (int col = fromCol - 1, row = fromRow - 1; col > toCol; col--, row--) {
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
        boolean isRookPathClear;
        if (fromCol != toCol) {
            isRookPathClear = isLegalColMoveRookPath(fromCol,fromRow,toCol);
        } else {
            isRookPathClear = isLegalRowMoveRookPath(fromCol,fromRow,toRow);
        }
        return isRookPathClear;
    }

    private boolean isLegalColMoveRookPath(int fromCol,int fromRow,int toCol) {
        boolean isRookPathClear = true;
        if (fromCol > toCol) {
            for (int c = fromCol - 1; c > toCol; c--) {
                if (this.position[c][fromRow] != 0) {
                    isRookPathClear = false;
                    break;
                }
            }
        } else {
            for (int c = fromCol + 1; c < toCol; c++) {
                if (this.position[c][fromRow] != 0) {
                    isRookPathClear = false;
                    break;
                }
            }
        }
        return isRookPathClear;
    }

    private boolean isLegalRowMoveRookPath(int fromCol,int fromRow,int toRow) {
        boolean isRookPathClear = true;
        if (fromRow > toRow) {
            for (int r = fromRow - 1; r > toRow; r--) {
                if (this.position[fromCol][r] != 0) {
                    isRookPathClear = false;
                    break;
                }
            }
        } else {
            for (int r = fromRow + 1; r < toRow; r++) {
                if (this.position[fromCol][r] != 0) {
                    isRookPathClear = false;
                    break;
                }
            }
        }
        return isRookPathClear;
    }

    private boolean isLegalQueenMove(int fromCol, int fromRow,int toCol,int toRow) {
        return (this.isLegalBishopMove(fromCol, fromRow, toCol, toRow)
                || this.isLegalRookMove(fromCol,fromRow,toCol,toRow));
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
    public String[][] boardToStringBoard() {
        String[][] stringBoard = new String[8][8];
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                stringBoard[i][j] = pieceToChar(position[i][j]);
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
    private String pieceToChar(int piece) {
        String val = "";
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
            case -6: case 6: val = "k";
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

    // REQUIRES: col,row are in [0,7]
    //           i.e. they point to a valid square of the board of a board
    //           piece is a valid piece
    // MODIFIES: this
    // EFFECTS: puts place on col,row of this board
    public void placePiece(int col,int row, int piece) {
        position[col][row] = piece;
    }

    //EFFECTS: changes the turn (whose move it is)
    public void nextTurn() {
        this.isWhitesTurn = !isWhitesTurn;
    }

    // getters and setters

    public void setName(String name) {
        this.name = name;
    }

    public int[][] getPosition() {
        return this.position;
    }

    public String getName() {
        return name;
    }

    public boolean getIsWhitesTurn() {
        return isWhitesTurn;
    }
}
