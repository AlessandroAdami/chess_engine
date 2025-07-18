#pragma once

#include "position.h"

enum Algorithm { MINIMAX, A_STAR };

class ChessEngine {
  public:
    ChessEngine(Position *position);
    Move getBestMove();

  private:
    Position *position;
    int evaluate(Position *position) const;
    int evaluateLeaf(Position *position, Color color, int plyFromRoot) const;
    int getPieceValue(const ColoredPiece &cp) const;
    Move minimax();
    std::pair<int,Move> minimax(Position *position, int depth, bool isMaximizing, Color color);
    Algorithm algorithm = MINIMAX;
    const int INF = 1000000;
    const int MATE_SCORE = 100000;
    const int MAX_DEPTH = 4;

    friend class ChessEngineTest_EvaluatePosition_Test;
};