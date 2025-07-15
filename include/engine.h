#pragma once

#include "position.h"
#include <limits>

enum Algorithm { MINIMAX, ALPHA_BETA, A_STAR };

class ChessEngine {
  public:
    ChessEngine(Position *position);
    Move getBestMove();

  private:
    Position *position;
    int evaluatePosition() const;
    int evaluatePosition(Position *position) const;
    int evaluatePositionForColor(Position *position, Color color) const;
    int getPieceValue(const ColoredPiece &cp) const;
    Move minimax() const;
    int negaMaxAlphaBeta(int depth, int alpha, int beta) const;
    int searchMoveDepth = 10;
    Move bestMove;
    Algorithm algorithm = MINIMAX;
    const int INF = std::numeric_limits<int>::max();
    const int MATE_SCORE = INF;

    friend class ChessEngineTest_EvaluatePosition_Test;
};