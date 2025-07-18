#include "engine.h"
#include "types.h"
#include <algorithm>
#include <iostream>

ChessEngine::ChessEngine(Position *position) { this->position = position; }

Move ChessEngine::getBestMove() {
    Move bestMove;
    switch (algorithm) {
    default:
        bestMove = minimax();
    }

    return bestMove;
}

/**
 * Minimax with alpha-beta pruning.
 */
Move ChessEngine::minimax() {
    int depth = MAX_DEPTH;
    Color color = position->getTurn();
    int alpha = -INF;
    int beta = INF;

    Move bestMove = Move{0, 0, 0, 0};
    int bestScore = -INF;

    std::vector<Move> moves = position->movementValidator.getLegalMoves(color);

    std::sort(moves.begin(), moves.end(), [this](const Move &a, const Move &b) {
        return scoreMove(a, position) > scoreMove(b, position);
    });

    for (const Move &move : moves) {
        position->makeMove(move);
        int score =
            -negamax(position, depth - 1, -beta, -alpha, oppositeColor(color));
        position->unmakeMove();

        if (score > bestScore) {
            bestScore = score;
            bestMove = move;
        }
        alpha = std::max(alpha, score);
    }

    return bestMove;
}

int ChessEngine::negamax(Position *position, int depth, int alpha, int beta,
                         Color color) {
    if (depth == 0 || position->getIsGameOver()) {
        return evaluateLeaf(position, color, MAX_DEPTH - depth);
    }

    int maxEval = -INF;
    std::vector<Move> moves =
        position->movementValidator.getLegalMoves(position->getTurn());

    std::sort(moves.begin(), moves.end(),
              [this, position](const Move &a, const Move &b) {
                  return scoreMove(a, position) > scoreMove(b, position);
              });

    for (const Move &move : moves) {
        position->makeMove(move);
        int eval =
            -negamax(position, depth - 1, -beta, -alpha, oppositeColor(color));
        position->unmakeMove();

        maxEval = std::max(maxEval, eval);
        alpha = std::max(alpha, eval);

        if (alpha >= beta) {
            break;
        }
    }

    return maxEval;
}

/**
 * Simple material-based evaluation (positive for white, negative for black).
 */
int ChessEngine::evaluate(Position *position) const {
    int score = 0;
    if (position->isCheckmated()) {
        score = position->getIsWhitesTurn() ? -MATE_SCORE : MATE_SCORE;
        return score;
    } else if (position->isStalemated()) {
        return 0;
    }

    for (int row = 0; row < 8; ++row) {
        for (int col = 0; col < 8; ++col) {
            ColoredPiece cp = position->getPiece(Square{row, col});
            score += getPieceValue(cp);
        }
    }

    return score;
}

/**
 * Evaluation relative to a specific color.
 */
int ChessEngine::evaluateLeaf(Position *position, Color color,
                              int plyFromRoot) const {
    if (position->isCheckmated()) {
        int mateScore = MATE_SCORE - plyFromRoot;
        return (position->getTurn() == color) ? -mateScore : mateScore;
    }

    if (position->isStalemated()) {
        return 0;
    }

    int score = evaluate(position);
    return (color == WHITE) ? score : -score;
}

/**
 * Material values for each piece type.
 */
int ChessEngine::getPieceValue(const ColoredPiece &cp) const {
    int value = 0;
    int colorMultiplier = (cp.color == WHITE) ? 1 : -1;

    switch (cp.piece) {
    case PAWN:
        value = 100;
        break;
    case KNIGHT:
        value = 320;
        break;
    case BISHOP:
        value = 330;
        break;
    case ROOK:
        value = 500;
        break;
    case QUEEN:
        value = 900;
        break;
    case KING:
        value = 20000;
        break;
    default:
        value = 0;
    }

    return value * colorMultiplier;
}

int ChessEngine::scoreMove(const Move &move, const Position *pos) const {
    ColoredPiece attacker = pos->getPiece(Square{move.from.row, move.from.col});
    ColoredPiece victim = pos->getPiece(Square{move.to.row, move.to.col});

    int attackerVal = getPieceValue(attacker);
    int victimVal = getPieceValue(victim);

    if (victim != NO_Piece) {
        return 10000 + (victimVal - attackerVal);
    }

    if (move.promotionPiece != NO_Piece) {
        return 9000 + getPieceValue(move.promotionPiece);
    }

    return 0;
}