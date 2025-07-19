#include "engine.h"
#include "types.h"
#include <algorithm>
#include <iostream>

Engine::Engine(Position *position) { this->position = position; }

Move Engine::getBestMove() {
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
Move Engine::minimax() {
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

int Engine::negamax(Position *position, int depth, int alpha, int beta,
                    Color color) {
    if (depth == 0 || position->getIsGameOver()) {
        return quiescence(position, -INF, INF, color, MAX_DEPTH - depth);
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
int Engine::evaluate(Position *position) const {
    int score = 0;
    if (position->isCheckmated()) {
        bool isWhitesTurn = position->getTurn() == WHITE;
        score = isWhitesTurn ? -MATE_SCORE : MATE_SCORE;
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
int Engine::evaluateLeaf(Position *position, Color color,
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
int Engine::getPieceValue(const ColoredPiece &cp) const {
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

int Engine::scoreMove(const Move &move, const Position *pos) const {
    ColoredPiece attacker = pos->getPiece(Square{move.from.row, move.from.col});
    ColoredPiece victim = pos->getPiece(Square{move.to.row, move.to.col});

    int attackerVal = std::abs(getPieceValue(attacker));
    int victimVal = std::abs(getPieceValue(victim));

    if (victim != NO_Piece) {
        return 10000 + (victimVal - attackerVal);
    }

    if (move.promotionPiece != NO_Piece) {
        return 9000 + std::abs(getPieceValue(move.promotionPiece));
    }

    return 0;
}

int Engine::quiescence(Position *position, int alpha, int beta, Color color,
                       int plyFromRoot) {
    int stand_pat = evaluateLeaf(position, color, plyFromRoot);

    if (stand_pat >= beta)
        return beta;
    if (alpha < stand_pat)
        alpha = stand_pat;

    std::vector<Move> moves = position->movementValidator.getLegalMoves(color);

    std::vector<Move> noisyMoves;
    for (const Move &move : moves) {
        ColoredPiece target =
            position->getPiece(Square{move.to.row, move.to.col});
        bool isCapture = target != NO_Piece;
        bool isGoodCapture = staticExchangeEval(position, move.to, color) >= 0;
        if ((isCapture && isGoodCapture) || move.promotionPiece != NO_Piece) {
            noisyMoves.push_back(move);
        }
    }

    std::sort(noisyMoves.begin(), noisyMoves.end(),
              [this, position](const Move &a, const Move &b) {
                  return scoreMove(a, position) > scoreMove(b, position);
              });

    for (const Move &move : noisyMoves) {
        position->makeMove(move);
        int score = -quiescence(position, -beta, -alpha, oppositeColor(color),
                                plyFromRoot + 1);
        position->unmakeMove();

        if (score >= beta)
            return beta;
        if (score > alpha)
            alpha = score;
    }

    return alpha;
}

int Engine::staticExchangeEval(Position *pos, Square target, Color sideToMove) {
    Position position = *pos;
    std::vector<int> gains;
    int gain = 0;

    auto attackers = getSortedAttackers(&position, target);

    Color currentSide = sideToMove;

    while (!attackers.empty()) {
        auto [from, attacker] = attackers.front();
        attackers.erase(attackers.begin());

        ColoredPiece target_piece = position.getPiece(target);
        gain = getPieceValue(target_piece) - gain;
        gains.push_back(gain);

        Move move;
        move.from = from, move.to = target;
        position.movePiece(move);

        attackers = getSortedAttackers(&position, target);
        currentSide = oppositeColor(currentSide);
    }

    for (int i = gains.size() - 2; i >= 0; --i) {
        gains[i] = std::min(-gains[i + 1], gains[i]);
    }

    return gains.empty() ? 0 : gains[0];
}

std::vector<std::pair<Square, ColoredPiece>>
Engine::getSortedAttackers(Position *pos, Square target) const {
    std::vector<std::pair<Square, ColoredPiece>> attackers;

    for (int row = 0; row < 8; ++row) {
        for (int col = 0; col < 8; ++col) {
            Square from{row, col};
            ColoredPiece cp = pos->getPiece(from);
            if (cp == NO_Piece)
                continue;
            Move move;
            move.from = from, move.to = target;

            if (pos->movementValidator.isValidMove(move)) {
                attackers.emplace_back(from, cp);
            }
        }
    }

    // Sort by value of attacking piece (a better attacker has smaller value)
    std::sort(attackers.begin(), attackers.end(),
              [this](const auto &a, const auto &b) {
                  int valA = std::abs(getPieceValue(a.second));
                  int valB = std::abs(getPieceValue(b.second));
                  return valA < valB;
              });

    return attackers;
}
