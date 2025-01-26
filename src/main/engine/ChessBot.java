package engine;

import model.Board;
import model.Move;
import model.pieces.Piece;

import java.util.ArrayList;

//TODO: think about how this is will be implemented

public class ChessBot {

    private Board board;

    public ChessBot(Board board) {
        this.board = board;
    }

    //return the best move it found
    public Move getMove() {
        ArrayList<Move> moves = getLegalMoves();
        return moves.get(0);
    }

    /**
     * @return list of all legal moves in the position
     */
    private ArrayList<Move> getLegalMoves() {
        ArrayList<Move> moves = new ArrayList<>();
        for (Piece p : this.board.getPieceList()) {
            for (int c = 0; c < 8; c++) {
                for (int r = 0; r < 8; r++) {
                    if (board.isValidMove(new Move(board,p,c,r))) {
                        moves.add(new Move(board,p,c,r));
                    }
                }
            }
        }
        return moves;
    }

    public void setBoard(Board board) {
        this.board = board;
    }
}
