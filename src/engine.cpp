#include "engine.h"
#include "types.h"
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
    Move bestMove = Move{0, 0, 0, 0};
    int depth = MAX_DEPTH;
    Color color = position->getTurn();
    std::pair<int, Move> result = minimax(position, depth, true, color);
    bestMove = result.second;
    return bestMove;
}

std::pair<int,Move> ChessEngine::minimax(Position *position, int depth, bool isMaximizing,
                         Color color) {
    Move bestMove = Move{0,0,0,0};
    if (depth == 0 || position->getIsGameOver()) {
        int eval = evaluateLeaf(position, color, depth);
        return std::make_pair(eval,bestMove);
    }

    Color movingColor = position->getTurn();
    std::vector<Move> moves = position->movementValidator.getLegalMoves(movingColor);

    if (isMaximizing) {
        int maxEval = -INF;
        for (const Move &move : moves) {
            position->makeMove(move);
            std::pair<int,Move> result = minimax(position, depth - 1, false, color);
            int eval = result.first;
            position->unmakeMove();
            if (eval > maxEval) {
                maxEval = eval;
                bestMove = move;
            }
        }
        return std::make_pair(maxEval,bestMove);
    } else {
        int minEval = INF;
        for (const Move &move : moves) {
            position->makeMove(move);
            std::pair<int,Move> result = minimax(position, depth - 1, true, color);
            int eval = result.first;
            position->unmakeMove();
            if (eval < minEval) {
                minEval = eval;
                bestMove = move;
            }
        }
        return std::make_pair(minEval,bestMove);
    }
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
