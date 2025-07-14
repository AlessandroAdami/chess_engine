#pragma once

#include "position.h"

enum Algorithm { MINIMAX, ALPHA_BETA, A_STAR };

class ChessEngine {
  public:
    ChessEngine(Position *board);
    Move getBestMove();

  private:
    Position *chessBoard;
    int evaluateBoard() const;
    int evaluateBoard(Position *board) const;
    int evaluateBoardForColor(Position *board, Color color) const;
    int getPieceValue(const ColoredPiece &cp) const;
    Move minimax() const;
    int negaMax(int depth) const;
    Move AStarSearch(int depth, bool isWhitesTurn);
    int searchMoveDepth = 1;
    Move bestMove;
    Algorithm algorithm = MINIMAX;

    friend class ChessEngineTest_EvaluateBoard_Test;
};