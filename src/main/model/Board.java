package model;

// - 0 indicates an empty square (not a piece)
// - Pawn: 1 / Knight: 2 / Bishop: 3 / Rook: 4 / Queen: 5 / King: 6
// black pieces have negative values, white pieces have positive values

/*
 * TODO: understand the relationship between checker and board
 * implement castling
 */

import org.json.JSONObject;

public class Board {

    private int[][] position;
    private String name;
    private boolean isWhitesTurn;
    private String canWhiteCastle;
    private String canBlackCastle;
    private int enPassantCol;

    // EFFECTS: constructs a new board
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
        canWhiteCastle = "RKR";
        canBlackCastle = "RKR";
        enPassantCol = -1;
    }

    // EFFECTS: constructs a new board
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
        canWhiteCastle = "RKR";
        canBlackCastle = "RKR";
        enPassantCol = -1;
    }

    // REQUIRES: fromCol,fromRow,toCol,toRow are in [0,7]
    // MODIFIES: this
    // EFFECTS: if move is legal, moves selected piece to square and captures any piece in
    //          the to square. Otherwise, do nothing
    public void makeMove(int fromCol, int fromRow, int toCol, int toRow) {
        int piece = this.position[fromCol][fromRow];
        if (this.isLegalMove(fromCol, fromRow, toCol, toRow)) {
            movePiece(piece, fromCol,fromRow,toCol,toRow);
            nextTurn();
            updateCastling(fromCol, fromRow, piece);
            enPassantCol = updateEnPassantCol(piece, fromCol, fromRow, toRow);
        }
    }

    // REQUIRES: fromCol,fromRow,toCol,toRow are in [0,7]
    // EFFECTS: true if the move is legal on current the board
    //          moves is defined by the square the piece is on
    //          and the square the piece wants to go to.
    public boolean isLegalMove(int fromCol, int fromRow, int toCol, int toRow) {
        int piece = this.position[fromCol][fromRow];
        boolean rightTurnToMove = (((piece > 0) && isWhitesTurn)
                || (piece < 0) && (!isWhitesTurn));
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
        return rightTurnToMove && hasPieceMoved && toEnemyOrEmptySquare && isValidMoveForPiece;
    }

    //EFFECTS: moves piece from square to square
    private void movePiece(int piece, int fromCol, int fromRow, int toCol, int toRow) {
        enPassantCaptures(fromCol,fromRow,toCol,toRow);
        this.position[fromCol][fromRow] = 0;
        this.position[toCol][toRow] = piece;
    }

    //EFFECTS: true if pawn move is legal
    private boolean isLegalPawnMove(int fromCol,int fromRow,int toCol,int toRow) {
        int pawn = this.position[fromCol][fromRow];
        boolean oneStep;
        boolean twoStep;
        boolean captures;
        boolean hasNotMoved;
        boolean enPassant;
        int enPassantRow;
        if (pawn > 0) {
            hasNotMoved = (fromRow == 1);
            enPassantRow = 5;
        } else {
            hasNotMoved = (fromRow == 6);
            enPassantRow = 2;
        }
        oneStep = (fromCol == toCol) && (fromRow + pawn == toRow) && position[toCol][toRow] == 0;
        twoStep = (fromCol == toCol) && (fromRow + (2 * pawn) == toRow) && hasNotMoved
                && (this.position[fromCol][fromRow + pawn] == 0)  && position[toCol][toRow] == 0;
        captures = (fromRow + pawn == toRow)
                    && (fromCol == toCol - 1 || fromCol == toCol + 1)
                    && this.position[toCol][toRow] * pawn < 0;
        enPassant = (enPassantCol == toCol) && (toRow == enPassantRow);

        return oneStep || twoStep || captures || enPassant;
    }

    //MODIFIES: this
    //EFFECTS: captures en-passant
    private void enPassantCaptures(int fromCol, int fromRow, int toCol, int toRow) {
        if (isLegalPawnMove(fromCol,fromRow,toCol,toRow)) {
            int pawn = position[fromCol][fromRow];
            int enPassantRow = (pawn > 0) ? 4 : 3;
            boolean enPassant = (enPassantCol == toCol) && (enPassantRow == fromRow);
            if (enPassant) {
                this.position[enPassantCol][enPassantRow] = 0;
            }
        }
    }

    //MODIFIES: this
    //EFFECTS: if pawn has advanced two squares, reassign en-passantCol;
    private int updateEnPassantCol(int piece, int col, int fromRow, int toRow) {
        enPassantCol = -1;
        int pawn = Math.abs(piece);
        if (pawn == 1) {
            if (fromRow + (2 * piece) == toRow) {
                enPassantCol = col;
            }
        }
        return enPassantCol;
    }

    //EFFECTS: true if knight move is legal
    private boolean isLegalKnightMove(int fromCol,int fromRow,int toCol,int toRow) {
        int rowDelta = Math.abs(toRow - fromRow);
        int colDelta = Math.abs(toCol - fromCol);
        return (rowDelta * colDelta == 2);
    }

    //EFFECTS: true if bishop move is legal
    private boolean isLegalBishopMove(int fromCol,int fromRow,int toCol,int toRow) {
        boolean goodBishopMove = Math.abs(fromCol - toCol) == Math.abs(fromRow - toRow);
        int colDir = (int) Math.signum(toCol - fromCol);
        int rowDir = (int) Math.signum(toRow - fromRow);
        boolean throughEmptySquares = isBishopPathEmpty(fromCol,fromRow,toCol,colDir,rowDir);
        return goodBishopMove && throughEmptySquares;
    }

    //EFFECTS: true if bishop path is empty
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

    //EFFECTS: true if rook path is empty
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

    //EFFECTS: true if pawn on the square must be promoted
    public boolean isPawnToPromote(int col,int row) {
        int pawn = position[col][row];
        return (row == 7 && pawn == 1) || (row == 0 && pawn == - 1);
    }

    //EFFECTS: changes the turn (whose move it is)
    public void nextTurn() {
        this.isWhitesTurn = !isWhitesTurn;
    }

    //MODIFIES: this
    //EFFECTS: updates who can castle
    private void updateCastling(int col,int row,int piece) {
        if (piece > 0) {
            updateWhiteCastling(col,row,piece);
        } else {
            updateBlackCastling(col,row,piece);
        }
    }

    //MODIFIES: this
    //EFFECTS: updates white's castling
    private void updateWhiteCastling(int col,int row,int piece) {
        if (piece == 6) {
            canWhiteCastle = "";
        } else if (piece == 4) {
            if (col == 0 && row == 0) {
                if (canWhiteCastle.equals("RKR")) {
                    canWhiteCastle = "KR";
                } else if (canWhiteCastle.equals("RK")) {
                    canWhiteCastle = "";
                }
            } else if (col == 7 && row == 0) {
                if (canWhiteCastle.equals("RKR")) {
                    canWhiteCastle = "RK";
                } else if (canWhiteCastle.equals("KR")) {
                    canWhiteCastle = "";
                }
            }
        }
    }

    //MODIFIES: this
    //EFFECTS: updates black's castling
    private void updateBlackCastling(int col,int row,int piece) {
        if (piece == -6) {
            canBlackCastle = "";
        } else if (piece == -4) {
            if (col == 0 && row == 7) {
                if (canBlackCastle.equals("RKR")) {
                    canBlackCastle = "KR";
                } else if (canBlackCastle.equals("RK")) {
                    canBlackCastle = "";
                }
            } else if (col == 7 && row == 7) {
                if (canBlackCastle.equals("RKR")) {
                    canBlackCastle = "RK";
                } else if (canBlackCastle.equals("KR")) {
                    canBlackCastle = "";
                }
            }
        }
    }

    //EFFECTS: return board as json object
    public JSONObject toJson() {
        JSONObject json = new JSONObject();
        json.put("position", positionToOneDArray());
        json.put("name", name);
        json.put("isWhitesTurn", isWhitesTurn);
        json.put("canWhiteCastle", canWhiteCastle);
        json.put("canBlackCastle", canBlackCastle);
        json.put("enPassantCol", enPassantCol);

        return json;
    }

    //EFFECTS: returns position as one dimensional array
    private int[] positionToOneDArray() {
        int[] oneDPosition = new int[64];
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                oneDPosition[(i * 8) + j] = position[i][j];
            }
        }
        return oneDPosition;
    }

    //EFFECTS: returns true if otherPosition is the same as this position
    public boolean samePosition(int[][] otherPosition) {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (position[i][j] != otherPosition[i][j]) {
                    return false;
                }
            }
        }
        return true;
    }

    // MODIFIES: this
    // EFFECTS: puts piece on col,row of this board
    public void placePiece(int col,int row, int piece) {
        position[col][row] = piece;
    }

    // getters and setters
    public int getPiece(int col, int row) {
        return position[col][row];
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPosition(int[][] position) {
        this.position = position;
    }

    public void setCanWhiteCastle(String castle) {
        this.canWhiteCastle = castle;
    }

    public void setCanBlackCastle(String castle) {
        this.canBlackCastle = castle;
    }

    public void setIsWhitesTurn(boolean isWhitesTurn) {
        this.isWhitesTurn = isWhitesTurn;
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

    public String getCanWhiteCastle() {
        return canWhiteCastle;
    }

    public String getCanBlackCastle() {
        return canBlackCastle;
    }

    public int getEnPassantCol() {
        return enPassantCol;
    }
}
