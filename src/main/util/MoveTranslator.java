package util;

import model.Board;
import model.Move;
import model.pieces.Piece;

/**
 * Translator to convert an object Move into classic algebraic notation
 * For on the notation consult <a href="https://en.wikipedia.org/wiki/Algebraic_notation_(chess)">...</a>
 */

public class MoveTranslator {

    private final Board board;
    private final Move move;

    public MoveTranslator(Board board,Move move) {
        this.board = board;
        this.move = move;
    }

    //returns move in algebraic notation
    public String algebraicNotationMove() {
        String piece = pieceToString(move.getPiece().getName());//KQRNB
        String letter = colToLetterString(move.getNewRow());    //abcdefgh
        String number = rowToNumberString(move.getNewCol());    //12345678

        String conflict = checkForConflict(move.getPiece());

        return piece + conflict + letter + number;
    }

    //returns the piece label
    private String pieceToString(String p) {
        String piece;

        //get the piece
        switch (p) {
            case "Pawn":   piece = "";
                break;
            case "Knight": piece = "N";
                break;
            case "Bishop": piece = "B";
                break;
            case "Rook":   piece = "R";
                break;
            case "Queen":  piece = "Q";
                break;
            default:       piece = "K";
        }

        return piece;
    }

    //returns the row letter
    private String colToLetterString(int col) {
        switch (col) {
            case 0: return "a";
            case 1: return "b";
            case 2: return "c";
            case 3: return "d";
            case 4: return "e";
            case 5: return "f";
            case 6: return "g";
            default: return "h";
        }
    }

    //returns the row in algebraic notation
    private String rowToNumberString(int row) {
        row++;
        return Integer.toString(row);
    }

    /**
     * Checks if another piece can go there and returns the
     * return the string that removes any ambiguity
     */
    private String checkForConflict(Piece piece) {
        int conflicts = 0;
        Piece conflictingPiece = null;
        Move otherMove;

        for (Piece other : board.getPieceList()) {
            otherMove = new Move(board,other,move.getNewCol(),move.getNewRow());
            if (other.getName().equals(piece.getName()) && board.isValidMove(otherMove)) {
                conflicts++;
                conflictingPiece = other;
                if (conflicts > 1) {
                    return getResolution(board,piece,conflictingPiece,2);
                }
            }
        }
        if (conflicts == 1) {
            return getResolution(board,piece,conflictingPiece,1);
        } else {
            //no conflicts were found
            return "";
        }
    }

    //Returns the col+row / col / row identification to disambiguate the two pieces
    private String getResolution(Board b,Piece piece, Piece other,int conflicts) {
        if (conflicts == 2) {
            return colToLetterString(piece.col) + rowToNumberString(piece.row);
        }
        if (piece.col == other.col) {
            return rowToNumberString(piece.row);
        } else if (piece.row == other.row) {
            return colToLetterString(piece.col);
        } else {
            return "";
        }
    }
}
