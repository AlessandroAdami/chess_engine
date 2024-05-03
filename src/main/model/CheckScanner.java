package model;

import model.exceptions.BoardSquareOutOfBoundsException;

//A scanner that looks for checks in a position.

public class CheckScanner {

    private Board board;
    private int[][] position;
    private boolean isWhitesTurn;
    private int kingColor;
    private int kingCol;
    private int kingRow;
    private int checkingPieceCol;
    private int checkingPieceRow;
    private int checkingPieceVal;
    private boolean isDoubleCheck;


    //EFFECTS: constructs a new scanner from the given board
    public CheckScanner(Board b) {
        this.board = b;
        this.position = b.getPosition();
        this.isWhitesTurn = b.getIsWhitesTurn();
        this.kingColor = isWhitesTurn ? Piece.WHITE : Piece.BLACK;
        this.kingCol = findKingCol(kingColor);
        this.kingRow = findKingRow(kingColor);
        this.checkingPieceCol = -1;
        this.checkingPieceRow = -1;
        this.checkingPieceVal = -1;
        this.isDoubleCheck = false;
    }

    //EFFECTS: updates the scanner with the current board
    public void updateScanner() {
        this.position = board.getPosition();
        this.isWhitesTurn = board.getIsWhitesTurn();
        this.kingColor = isWhitesTurn ? Piece.WHITE : Piece.BLACK;
        this.kingCol = findKingCol(kingColor);
        this.kingRow = findKingRow(kingColor);
        this.checkingPieceCol = -1;
        this.checkingPieceRow = -1;
        this.checkingPieceVal = -1;
        this.isDoubleCheck = false;
    }

    //MODIFIES: this
    //EFFECTS: true if king is checkmated
    public boolean isCheckMated() {
        return isChecked() && noLegalMoves();
    }

    //MODIFIES: this
    //EFFECTS: true if king is in check
    public boolean isChecked() {
        return isInCheck(kingCol,kingRow,kingColor);
    }

    //EFFECTS: return col of king's position
    private int findKingCol(int color) {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (position[i][j] == Piece.KING * color) {
                    return i;
                }
            }
        }
        return -1;
    }

    //EFFECTS: returns row of king's position
    private int findKingRow(int color) {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (position[i][j] == Piece.KING * color) {
                    return j;
                }
            }
        }
        return -1;
    }

    //MODIFIES: this
    //EFFECTS: returns true if square is attacked by enemy piece
    private boolean isInCheck(int col,int row,int color) {
        boolean rook = isAttackedByRook(col,row,color);
        boolean bishop = isAttackedByBishop(col,row,color);
        boolean knight = isAttackedByKnight(col,row,color);
        boolean pawn = isAttackedByPawn(col,row,color);

        return rook || bishop || knight || pawn;
    }

    //MODIFIES: this
    //EFFECTS: true if square is attacked by enemy pawn
    private boolean isAttackedByPawn(int col, int row, int color) {
        boolean plusCol = false;
        boolean minusCol = false;
        if (row + color < 0 || row + color > 7) {
            return false;
        }
        if (col - 1 >= 0) {
            minusCol = position[col - 1][row + color] == color * -Piece.PAWN;
            if (minusCol) {
                setCheckingPiece(col - 1, row + color,Piece.PAWN);
            }
        }
        if (col + 1 <= 7) {
            plusCol = position[col + 1][row + color] == color * -Piece.PAWN;
            if (plusCol) {
                setCheckingPiece(col + 1, row + color,Piece.PAWN);
            }
        }
        return  minusCol || plusCol;
    }

    //MODIFIES: this
    //EFFECTS: true if square is attacked by enemy knight
    private boolean isAttackedByKnight(int col, int row, int color) {
        for (int colDel = -2; colDel <= 2; colDel++) {
            for (int rowDel = -2; rowDel <= 2; rowDel++) {
                if (colDel * rowDel == 2 || colDel * rowDel == -2) {
                    try {
                        if (position[col + colDel][row + rowDel] == Piece.KNIGHT * color * -1) {
                            setCheckingPiece(col + colDel,row + rowDel,Piece.KNIGHT);
                            return true;
                        }
                    } catch (IndexOutOfBoundsException e) {
                        //continue;
                    }

                }
            }
        }
        return false;
    }

    //MODIFIES: this
    //EFFECTS: true if square is attacked by enemy rook
    private boolean isAttackedByRook(int col, int row, int color) {
        return isAttackedByRookCol(col,row,color) || isAttackedByRookRow(col,row,color);
    }

    //MODIFIES: this
    //EFFECTS: true if square is attacked by enemy rook on the column
    private boolean isAttackedByRookCol(int col, int row, int color) {
        for (int colDelUp = 1; colDelUp <= 7 - col; colDelUp++) {
            if ((position[col + colDelUp][row] == Piece.ROOK * color * -1)
                    || (position[col + colDelUp][row] == Piece.QUEEN * color * -1)) {
                setCheckingPiece(col + colDelUp,row,Piece.ROOK);
                return true;
            } else if (position[col + colDelUp][row] != Piece.NONE) {
                break;
            }
        }
        for (int colDelDown = -1; col + colDelDown >= 0; colDelDown--) {
            if ((position[col + colDelDown][row] == Piece.ROOK * color * -1)
                    || (position[col + colDelDown][row] == Piece.QUEEN * color * -1)) {
                setCheckingPiece(col + colDelDown,row,Piece.ROOK);
                return true;
            } else if (position[col + colDelDown][row] != Piece.NONE) {
                break;
            }
        }
        return false;
    }

    //MODIFIES: this
    //EFFECTS: true if square is attacked by enemy rook on the row
    private boolean isAttackedByRookRow(int col, int row, int color) {
        for (int rowDelRight = 1; rowDelRight <= 7 - row; rowDelRight++) {
            if ((position[col][row + rowDelRight] == Piece.ROOK * color * -1)
                    || (position[col][row + rowDelRight] == Piece.QUEEN * color * -1)) {
                setCheckingPiece(col,row + rowDelRight,Piece.ROOK);
                return true;
            } else if (position[col][row + rowDelRight] != Piece.NONE) {
                break;
            }
        }
        for (int rowDelLeft = -1; row + rowDelLeft >= 0; rowDelLeft--) {
            if ((position[col][row + rowDelLeft] == Piece.ROOK * color * -1)
                    || (position[col][row + rowDelLeft] == Piece.QUEEN * color * -1)) {
                setCheckingPiece(col,row + rowDelLeft,Piece.ROOK);
                return true;
            } else if (position[col][row + rowDelLeft] != Piece.NONE) {
                break;
            }
        }
        return false;
    }

    //MODIFIES: this
    //EFFECTS: true if square is attacked by enemy bishop
    private boolean isAttackedByBishop(int col, int row, int color) {
        return isAttackedByBishopUp(col,row,color) || isAttackedByBishopDown(col,row,color);
    }

    //MODIFIES: this
    //EFFECTS: true if square is attacked by enemy bishop on upward column
    private boolean isAttackedByBishopUp(int col, int row, int color) {
        for (int delUpRight = 1; delUpRight <= 7 - col && delUpRight <= 7 - row; delUpRight++) {
            if ((position[col + delUpRight][row + delUpRight] == Piece.BISHOP * color * -1)
                    || (position[col + delUpRight][row + delUpRight] == Piece.QUEEN * color * -1)) {
                setCheckingPiece(col + delUpRight,row + delUpRight,Piece.BISHOP);
                return true;
            } else if (position[col + delUpRight][row + delUpRight] != Piece.NONE) {
                break;
            }
        }
        for (int delDownLeft = -1; col + delDownLeft >= 0 && row + delDownLeft >= 0; delDownLeft--) {
            if ((position[col + delDownLeft][row + delDownLeft] == Piece.BISHOP * color * -1)
                    || (position[col + delDownLeft][row + delDownLeft] == Piece.QUEEN * color * -1)) {
                setCheckingPiece(col + delDownLeft,row + delDownLeft,Piece.BISHOP);
                return true;
            } else if (position[col + delDownLeft][row + delDownLeft] != Piece.NONE) {
                break;
            }
        }
        return false;
    }

    //EFFECTS: true if square is attacked by enemy bishop on downwards column
    private boolean isAttackedByBishopDown(int col, int row, int color) {
        for (int delUpLeft = 1; col + delUpLeft * -1 >= 0 && delUpLeft <= 7 - row; delUpLeft++) {
            if ((position[col + delUpLeft * -1][row + delUpLeft] == Piece.BISHOP * color * -1)
                    || (position[col + delUpLeft * -1][row + delUpLeft] == Piece.QUEEN * color * -1)) {
                setCheckingPiece(col + delUpLeft * -1,row + delUpLeft,Piece.BISHOP);
                return true;
            } else if (position[col + delUpLeft * -1][row + delUpLeft] != Piece.NONE) {
                break;
            }
        }
        for (int delDownRight = 1; col + delDownRight <= 7 && row + delDownRight * -1 >= 0; delDownRight++) {
            if ((position[col + delDownRight][row + delDownRight * -1] == Piece.BISHOP * color * -1)
                    || (position[col + delDownRight][row + delDownRight * -1] == Piece.QUEEN * color * -1)) {
                setCheckingPiece(col + delDownRight,row + delDownRight * -1,Piece.BISHOP);
                return true;
            } else if (position[col + delDownRight][row + delDownRight * -1] != Piece.NONE) {
                break;
            }
        }
        return false;
    }

    //EFFECTS: true if there are no legal moves in the position
    private boolean noLegalMoves() {
        if (isDoubleCheck) {
            return kingHasNoMoves();
        }
        if (canAttackerBeCaptured()) {
            return false;
        }
        if (canAttackerBeBlocked()) {
            return false;
        }
        return kingHasNoMoves();
    }

    //EFFECTS: true if king doesn't have legal moves
    private boolean kingHasNoMoves() {
        int color = isWhitesTurn ? Piece.WHITE : Piece.BLACK;
        int kingCol = findKingCol(color);
        int kingRow = findKingRow(color);
        for (int i = -1; i <= 1; i++) {
            if ((kingCol + i < 0) || (kingCol + i > 7)) {
                continue;
            }
            for (int j = -1; j <= 1; j++) {
                if ((kingRow + j < 0) || (kingRow + j > 7)) {
                    continue;
                }
                try {
                    Move move = new Move(kingCol,kingRow, kingCol + i, kingRow + j);
                    if (board.isLegalMove(move)) {
                        return false;
                    }
                } catch (BoardSquareOutOfBoundsException e) {
                    //move is illegal
                }
            }
        }
        return true;
    }

    //EFFECTS: true if attacker can be captured
    private boolean canAttackerBeCaptured() {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                Move move;
                try {
                    move = new Move(i,j,checkingPieceCol,checkingPieceRow);
                } catch (BoardSquareOutOfBoundsException e) {
                    continue;
                }
                if (board.isLegalMove(move)) {
                    return true;
                }
            }
        }
        return false;
    }

    //EFFECTS: true if attacker can be blocked
    private boolean canAttackerBeBlocked() {
        switch (checkingPieceVal) {
            case Piece.BISHOP: return canBishopBeBlocked();
            case Piece.ROOK: return canRookBeBlocked();
            default: return false; //cases: pawn and knight
        }
    }


    //EFFECTS: true if attacking bishop can be blocked
    private boolean canBishopBeBlocked() {
        int delCol = (int) Math.signum(kingCol - checkingPieceCol);
        int delRow = (int) Math.signum(kingCol - checkingPieceRow);
        for (int i = checkingPieceCol + delCol; i != kingCol; i = i + delCol) {
            for (int j = checkingPieceRow + delRow; j != kingRow; j = j + delRow) {
                for (int c = 0; c < 8; c++) {
                    for (int r = 0; r < 8; r++) {
                        Move move;
                        try {
                            move = new Move(c,r,i,j);
                        } catch (BoardSquareOutOfBoundsException e) {
                            continue;
                        }
                        if (board.isLegalMove(move)) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    //EFFECTS: true if attacking rook can be blocked
    private boolean canRookBeBlocked() {
        if (kingCol == checkingPieceCol) {
            return canRookColBeBlocked();
        } else {
            return canRookRowBeBlocked();
        }
    }

    //EFFECTS: true if attacking rook can be blocked in the column
    private boolean canRookColBeBlocked() {
        int delCol = (int) Math.signum(kingCol - checkingPieceCol);
        for (int i = checkingPieceCol + delCol; i != kingCol; i = i + delCol) {
            for (int c = 0; c < 8; c++) {
                for (int r = 0; r < 8; r++) {
                    Move move;
                    try {
                        move = new Move(c,r,i,kingRow);
                    } catch (BoardSquareOutOfBoundsException e) {
                        continue;
                    }
                    if (board.isLegalMove(move)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    //EFFECTS: true if attacking rook can be blocked in the row
    private boolean canRookRowBeBlocked() {
        int delRow = (int) Math.signum(kingRow - checkingPieceRow);
        for (int i = checkingPieceRow + delRow; i != kingRow; i = i + delRow) {
            for (int c = 0; c < 8; c++) {
                for (int r = 0; r < 8; r++) {
                    Move move;
                    try {
                        move = new Move(c,r,kingCol,i);
                    } catch (BoardSquareOutOfBoundsException e) {
                        continue;
                    }
                    if (board.isLegalMove(move)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    //MODIFIES: this
    //EFFECTS: sets checking piece and its square, looks for double checks
    private void setCheckingPiece(int col,int row,int piece) {
        if (this.checkingPieceVal != -1) {
            isDoubleCheck = true;
        } else {
            this.checkingPieceCol = col;
            this.checkingPieceRow = row;
            this.checkingPieceVal = piece;
        }

    }

}