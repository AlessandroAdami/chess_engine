#pragma once

#include "position.h"

enum Algorithm { MINIMAX, A_STAR };

class Engine {
  public:
    Engine(Position *position);
    Move getBestMove();

  private:
    Position *position;
    int evaluate(Position *position) const;
    int evaluateLeaf(Position *position, Color color, int plyFromRoot) const;
    int getPieceValue(const ColoredPiece &cp) const;
    Move minimax();
    int negamax(Position *position, int depth, int alpha, int beta,
                Color color);
    int quiescence(Position *position, int alpha, int beta, Color color,
                   int plyFromRoot);
    int staticExchangeEval(Position *pos, Square sq, Color sideToMove);
    std::vector<std::pair<Square, ColoredPiece>>
    getSortedAttackers(Position *pos, Square target) const;
    Color oppositeColor(Color color) {
        return (color == WHITE) ? BLACK : WHITE;
    }
    int scoreMove(const Move &move, const Position *pos) const;
    Algorithm algorithm = MINIMAX;
    const int INF = 1000000;
    const int MATE_SCORE = 100000;
    const int MAX_DEPTH = 2;

    friend class ChessEngineTest_EvaluatePosition_Test;
};