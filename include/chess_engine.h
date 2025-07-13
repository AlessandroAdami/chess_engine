#pragma once

#include "chess_board.h"

enum Algorithm { MINIMAX, ALPHA_BETA, A_STAR };

class ChessEngine {
  public:
    ChessEngine(ChessBoard *board);
    Move getBestMove();

  private:
    ChessBoard *chessBoard;
    int evaluateBoard() const;
    int evaluateBoard(ChessBoard *board) const;
    int evaluateBoardForColor(ChessBoard *board, Color color) const;
    int getPieceValue(const ColoredPiece &cp) const;
    Move minimax() const;
    int negaMax(int depth) const;
    Move AStarSearch(int depth, bool isWhitesTurn);
    int searchMoveDepth = 1;
    Move bestMove;
    Algorithm algorithm = MINIMAX;

    friend class ChessEngineTest_EvaluateBoard_Test;
};