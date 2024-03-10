package model;

//A scanner that looks for checks in a position

public class CheckScanner {

    private Board board;
    private int[][] position;
    private boolean isWhitesTurn;
    private String canWhiteCastle;
    private String canBlackCastle;
    private int enPassantCol;
    private static final int NO_EN_PASSANT = -1;


    //EFFECTS: constructs a new scanner from the given board
    public CheckScanner(Board b) {
        this.board = b;
        this.position = b.getPosition();
        this.isWhitesTurn = b.getIsWhitesTurn();
        this.canWhiteCastle = b.getCanWhiteCastle();
        this.canBlackCastle = b.getCanBlackCastle();
        this.enPassantCol = b.getEnPassantCol();
    }

    //EFFECTS: true if king is in check
    private boolean isChecked() {
        int color;
        if (isWhitesTurn) {
            color = 1;
        } else {
            color = -1;
        }
        int col = findKingCol(color);
        int row = findKingRow(color);

        return isInCheck(col,row,color);
    }

    //EFFECTS: return col of king's position
    private int findKingCol(int color) {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (position[i][j] == 6 * color) {
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
                if (position[i][j] == 6 * color) {
                    return j;
                }
            }
        }
        return -1;
    }

    //EFFECTS: returns true if square is attacked by enemy piece
    private boolean isInCheck(int col,int row,int color) {
        boolean rook = isAttackedByRook(col,row,color);
        boolean bishop = isAttackedByBishop(col,row,color);
        boolean knight = isAttackedByKnight(col,row,color);
        boolean pawn = isAttackedByPawn(col,row,color);

        return rook || bishop || knight || pawn;
    }

    //EFFECTS: true if square is attacked by enemy pawn
    private boolean isAttackedByPawn(int col, int row, int color) {
        boolean plusCol = false;
        boolean minusCol = false;
        if (row + color < 0 || row + color > 7) {
            return false;
        }
        if (col - 1 >= 0) {
            minusCol = position[col - 1][row + color] == color * -1;
        }
        if (col + 1 <= 7) {
            plusCol = position[col + 1][row + color] == color * -1;
        }
        return  minusCol || plusCol;
    }

    //EFFECTS: true if square is attacked by enemy knight
    private boolean isAttackedByKnight(int col, int row, int color) {
        return false;
    }

    //EFFECTS: true if square is attacked by enemy rook
    private boolean isAttackedByRook(int col, int row, int color) {
        return false;
    }

    //EFFECTS: true if square is attacked by enemy bishop
    private boolean isAttackedByBishop(int col, int row, int color) {
        return false;
    }


}
