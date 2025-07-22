#pragma once

#include "position.h"
#include <chrono>
#include <unordered_map>

enum Algorithm { DEPTH_BOUNDED = 0, TIME_BOUNDED = 1 };

class Engine {
  public:
    Engine(Position *position);
    Move getBestMove();
    Move getBestMoveWithTimeLimit(int timeLimitMs);

  private:
    Algorithm algorithm = TIME_BOUNDED;
    Position *position;
    std::unordered_map<uint64_t, int> transpositionTable;
    const int INF = 1000000;
    const int MATE_SCORE = 100000;
    const int MAX_DEPTH = 2;
    const int MAX_TIME = 5000; // five seconds per move
    std::chrono::steady_clock::time_point startTime;
    int timeLimitMs;
    bool isTimeUp() const;

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

    friend class ChessEngineTest_EvaluatePosition_Test;
};