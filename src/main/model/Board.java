package model;

//Represents the classic 8x8 chessboard
/*
 * TODO: understand the relationship between checker and board
 *  implement castling
 */

import org.json.JSONObject;

public class Board {

    private int[][] position;
    private String name;
    private boolean isWhitesTurn;
    private String canWhiteCastle;
    private String canBlackCastle;
    private int enPassantCol;

    private CheckScanner checkScanner;


    // EFFECTS: constructs new board
    public Board() {
        this.createNewBoard();
        this.name = "New Board";
        this.isWhitesTurn = true;
        this.canWhiteCastle = "RKR";
        this.canBlackCastle = "RKR";
        this.enPassantCol = -1;
        this.checkScanner = new CheckScanner(this);

    }

    // EFFECTS: constructs board with given name
    public Board(String name) {
        this.createNewBoard();
        this.name = name;
        this.isWhitesTurn = true;
        this.canWhiteCastle = "RKR";
        this.canBlackCastle = "RKR";
        this.enPassantCol = -1;
        this.checkScanner = new CheckScanner(this);
    }

    private void createNewBoard() {
        position = new int[8][8];
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                int color = 0;
                if (2 <= j && j <= 5) {
                    position[i][j] = Piece.NONE;
                } else if (j < 2) {
                    color = Piece.WHITE;
                } else {
                    color = Piece.BLACK;
                }
                assignPieceToSquare(i, j, color);
            }
        }
    }

    private void assignPieceToSquare(int i, int j, int color) {
        if (j == 1 || j == 6) {
            position[i][j] = Piece.PAWN * color;
        } else {
            int piece = i % 7;
            switch (piece) {
                case 0: position[i][j] = Piece.ROOK * color;
                    break;
                case 1: case 6: position[i][j] = Piece.KNIGHT * color;
                    break;
                case 2: case 5: position[i][j] = Piece.BISHOP * color;
                    break;
                case 3: position[i][j] = Piece.QUEEN * color;
                    break;
                case 4: position[i][j] = Piece.KING * color;
                    break;
            }
        }
    }

    // MODIFIES: this
    // EFFECTS: if move is legal, moves selected piece to square and captures any piece in
    //          the to square. Otherwise, do nothing
    public void makeMove(Move move) {
        int fromCol = move.getFromCol();
        int fromRow = move.getFromRow();
        int toRow = move.getToRow();
        int piece = this.position[fromCol][fromRow];
        if (this.isLegalMove(move)) {
            movePiece(piece, move);
            nextTurn();
            updateCastling(fromCol, fromRow, piece);
            enPassantCol = updateEnPassantCol(piece, fromCol, fromRow, toRow);
        }
    }

    // EFFECTS: true if the move is legal on current the board
    public boolean isLegalMove(Move move) {
        int piece = this.position[move.getFromCol()][move.getFromRow()];
        boolean rightTurnToMove = (((piece > 0) && isWhitesTurn) || (piece < 0) && (!isWhitesTurn));
        boolean hasPieceMoved = (move.getFromCol() != move.getToCol() || move.getFromRow() != move.getToRow());
        boolean toEnemyOrEmptySquare = (piece * this.position[move.getToCol()][move.getToRow()] <= 0);
        boolean isValidMoveForPiece = false;
        switch (piece) {
            case -Piece.PAWN: case Piece.PAWN: isValidMoveForPiece = this.isLegalPawnMove(move);
                break;
            case -Piece.KNIGHT: case Piece.KNIGHT:
                isValidMoveForPiece = this.isLegalKnightMove(move);
                break;
            case -Piece.BISHOP: case Piece.BISHOP:
                isValidMoveForPiece = this.isLegalBishopMove(move);
                break;
            case -Piece.ROOK: case Piece.ROOK: isValidMoveForPiece = this.isLegalRookMove(move);
                break;
            case -Piece.QUEEN: case Piece.QUEEN:
                isValidMoveForPiece = this.isLegalQueenMove(move);
                break;
            case -Piece.KING: case Piece.KING: isValidMoveForPiece = this.isLegalKingMove(move);
                break;
        }
        return rightTurnToMove && hasPieceMoved && toEnemyOrEmptySquare && isValidMoveForPiece;
    }

    //EFFECTS: moves piece from square to square
    private void movePiece(int piece,Move move) {
        enPassantCaptures(move);
        //castle(move);
        this.position[move.getFromCol()][move.getFromRow()] = Piece.NONE;
        this.position[move.getToCol()][move.getToRow()] = piece;
    }

    //EFFECTS: true if pawn move is legal
    private boolean isLegalPawnMove(Move move) {
        int fromCol = move.getFromCol();
        int fromRow = move.getFromRow();
        int toCol = move.getToCol();
        int toRow = move.getToRow();
        int pawn = this.position[fromCol][fromRow];
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
        boolean oneStep = (fromCol == toCol) && (fromRow + pawn == toRow) && position[toCol][toRow] == Piece.NONE;
        boolean twoStep = (fromCol == toCol) && (fromRow + (2 * pawn) == toRow) && hasNotMoved
                && (this.position[fromCol][fromRow + pawn] == 0)  && position[toCol][toRow] == Piece.NONE;
        boolean captures = (fromRow + pawn == toRow)
                    && (fromCol == toCol - 1 || fromCol == toCol + 1)
                    && this.position[toCol][toRow] * pawn < 0;
        enPassant = (enPassantCol == toCol) && (toRow == enPassantRow);

        return oneStep || twoStep || captures || enPassant;
    }

    //MODIFIES: this
    //EFFECTS: captures en-passant
    private void enPassantCaptures(Move move) {
        if (isLegalPawnMove(move)) {
            int pawn = position[move.getFromCol()][move.getFromRow()];
            int enPassantRow = (pawn > 0) ? 4 : 3;
            boolean enPassant = (enPassantCol == move.getToCol()) && (enPassantRow == move.getFromRow());
            if (enPassant) {
                this.position[enPassantCol][enPassantRow] = Piece.NONE;
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
    private boolean isLegalKnightMove(Move move) {
        int rowDelta = Math.abs(move.getToRow() - move.getFromRow());
        int colDelta = Math.abs(move.getToCol() - move.getFromCol());
        return (rowDelta * colDelta == 2);
    }

    //EFFECTS: true if bishop move is legal
    private boolean isLegalBishopMove(Move move) {
        int fromCol = move.getFromCol();
        int fromRow = move.getFromRow();
        int toCol = move.getToCol();
        int toRow = move.getToRow();
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
            if (this.position[col][row] != Piece.NONE) {
                throughEmptySquares = false;
                break;
            }
        }
        return throughEmptySquares;
    }

    //EFFECTS: true if rook move is legal
    private boolean isLegalRookMove(Move move) {
        int fromCol = move.getFromCol();
        int fromRow = move.getFromRow();
        int toCol = move.getToCol();
        int toRow = move.getToRow();
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
                if (this.position[col][fromRow] != Piece.NONE) {
                    throughEmptySquares = false;
                    break;
                }
            }
        } else {
            for (int row = fromRow + rowDir; row != toRow; row = row + rowDir) {
                if (this.position[fromCol][row] != Piece.NONE) {
                    throughEmptySquares = false;
                    break;
                }
            }
        }
        return throughEmptySquares;
    }

    //EFFECTS: true if queen move is legal
    private boolean isLegalQueenMove(Move move) {
        return isLegalBishopMove(move)
                || isLegalRookMove(move);
    }

    //EFFECTS: true if king move is legal
    private boolean isLegalKingMove(Move move) {
        int fromCol = move.getFromCol();
        int fromRow = move.getFromRow();
        int toCol = move.getToCol();
        int toRow = move.getToRow();
        boolean isLeftOrRight = (Math.abs(toCol - fromCol) == 1) && fromRow == toRow;
        boolean isUpOrDown = (Math.abs(toRow - fromRow) == 1) && fromCol == toCol;
        boolean isDiagonal = (Math.abs(toCol - fromCol) == 1) && (Math.abs(toRow - fromRow) == 1);
        boolean isCastlingLegal = isLegalCastleMove(move);
        return isLeftOrRight || isUpOrDown || isDiagonal || isCastlingLegal;
    }

    //EFFECTS: true if castling is possible
    //TODO: must be adjourned for checks
    private boolean isLegalCastleMove(Move move) {
        int toCol = move.getToCol();
        int row = move.getFromRow();
        if (toCol == 6 && (row == 0 && (canWhiteCastle.equals("KR") || canWhiteCastle.equals("RKR")))) {
            return true;
        } else if (toCol == 2 && (row == 0 && (canWhiteCastle.equals("RK") || canWhiteCastle.equals("RKR")))) {
            return true;
        } else if (toCol == 6 && (row == 0 && (canBlackCastle.equals("KR") || canBlackCastle.equals("RKR")))) {
            return true;
        } else if (toCol == 2 && (row == 0 && (canBlackCastle.equals("RK") || canBlackCastle.equals("RKR")))) {
            return true;
        } else {
            return false;
        }
    }

    private boolean canXShortCastle(int color) {
        return false;
    }

    private boolean canXLongCastle(int color) {
        return false;
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
                if (position[i][j] != Piece.NONE) {
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
        return (row == 7 && pawn == Piece.PAWN) || (row == 0 && pawn == -Piece.PAWN);
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

    // getters and setters

    public void placePiece(int col,int row, int piece) {
        position[col][row] = piece;
    }

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
