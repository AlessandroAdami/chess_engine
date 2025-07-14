#pragma once

#include "position.h"

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
    Move AStarSearch(int depth, bool isWhitesTurn);
    int searchMoveDepth = 1;
    Move bestMove;
    Algorithm algorithm = MINIMAX;

    friend class ChessEngineTest_EvaluatePosition_Test;
};