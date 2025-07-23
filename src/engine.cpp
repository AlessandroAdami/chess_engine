#include "engine.h"
#include "types.h"
#include <algorithm>
#include <iostream>

std::unordered_map<uint64_t, Move> principalVariation;

Engine::Engine(Position *position) { this->position = position; }

Move Engine::getBestMove() {
    principalVariation.clear();
    Move bestMove;
    switch (algorithm) {
    case TIME_BOUNDED:
        bestMove = getBestMoveWithTimeLimit(MAX_TIME);
        break;
    case DEPTH_BOUNDED:
        bestMove = minimax();
        break;
    default:
        bestMove = minimax();
    }

    return bestMove;
}

Move Engine::getBestMoveWithTimeLimit(int timeLimitMs) {
    this->timeLimitMs = timeLimitMs;
    this->startTime = std::chrono::steady_clock::now();

    Move bestMove;
    int maxDepthReached = 0;
    Color color = position->getTurn();

    for (int depth = 1; depth <= INF; ++depth) {
        Move currentBest;
        int currentBestScore = -INF;

        std::vector<Move> moves =
            position->movementValidator.getLegalMoves(color);

        std::sort(moves.begin(), moves.end(),
                  [this](const Move &a, const Move &b) {
                      return scoreMove(a, position) > scoreMove(b, position);
                  });

        for (const Move &move : moves) {
            if (isTimeUp())
                return bestMove;

            position->moveMaker.makeMove(move);
            int score =
                -negamax(position, depth - 1, -INF, INF, oppositeColor(color));
            position->moveMaker.unmakeMove();

            if (score > currentBestScore) {
                currentBest = move;
                currentBestScore = score;
            }
        }

        if (!isTimeUp()) {
            bestMove = currentBest;
            maxDepthReached = depth;
        } else {
            break;
        }
    }

    std::cout << "Iterative deepening stopped at depth: " << maxDepthReached
              << std::endl;
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

    Move bestMove = Move(Square(0, 0), Square(0, 0));
    int bestScore = -INF;

    std::vector<Move> moves = position->movementValidator.getLegalMoves(color);

    std::sort(moves.begin(), moves.end(), [this](const Move &a, const Move &b) {
        return scoreMove(a, position) > scoreMove(b, position);
    });

    for (const Move &move : moves) {
        position->moveMaker.makeMove(move);
        int score =
            -negamax(position, depth - 1, -beta, -alpha, oppositeColor(color));
        position->moveMaker.unmakeMove();

        if (score > bestScore) {
            bestScore = score;
            bestMove = move;
        }
        alpha = std::max(alpha, score);
    }

    return bestMove;
}

/**
 * Negamax implementation of minimax, with alpha-beta pruning,
 * hashmap of already-seen positions, and semi-smart move ordering selection.
 */
int Engine::negamax(Position *position, int depth, int alpha, int beta,
                    Color color) {
    uint64_t hash = position->zobristHash;
    if (transpositionTable.find(hash) != transpositionTable.end()) {
        return transpositionTable[hash];
    }
    if (depth == 0 || position->getIsGameOver()) {
        int eval = quiescence(position, -INF, INF, color, MAX_DEPTH - depth);
        transpositionTable[hash] = eval;
        return eval;
    }

    int maxEval = -INF;
    std::vector<Move> moves =
        position->movementValidator.getLegalMoves(position->getTurn());

    Move pvMove;
    auto it = principalVariation.find(position->zobristHash);
    if (it != principalVariation.end()) {
        pvMove = it->second;

        // Move PV move to front if it exists in the list
        auto pvIt = std::find(moves.begin(), moves.end(), pvMove);
        if (pvIt != moves.end()) {
            std::iter_swap(moves.begin(), pvIt);
        }
    }

    std::sort(moves.begin(), moves.end(),
              [this, position](const Move &a, const Move &b) {
                  return scoreMove(a, position) > scoreMove(b, position);
              });

    for (const Move &move : moves) {
        position->moveMaker.makeMove(move);
        int eval =
            -negamax(position, depth - 1, -beta, -alpha, oppositeColor(color));
        position->moveMaker.unmakeMove();

        if (eval > maxEval) {
            maxEval = eval;

            if (depth > 0) {
                principalVariation[position->zobristHash] = move;
            }
        }
        alpha = std::max(alpha, eval);

        if (alpha >= beta) {
            break;
        }
    }

    transpositionTable[hash] = maxEval;

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
            ColoredPiece cp = position->getPiece(Square(row, col));
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
    ColoredPiece attacker = pos->getPiece(Square(move.from.row, move.from.col));
    ColoredPiece victim = pos->getPiece(Square(move.to.row, move.to.col));

    int attackerVal = std::abs(getPieceValue(attacker));
    int victimVal = std::abs(getPieceValue(victim));

    if (victim != NO_PIECE) {
        return 10000 + (victimVal - attackerVal);
    }

    if (move.promotionPiece != NO_PIECE) {
        return 9000 + std::abs(getPieceValue(move.promotionPiece));
    }

    return 0;
}

/**
 * Looks for further best move until all leafs are capture-free positons
 * (quiesceing).
 */
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
            position->getPiece(Square(move.to.row, move.to.col));
        bool isCapture = target != NO_PIECE;
        bool isGoodCapture = staticExchangeEval(position, move.to, color) >= 0;
        if ((isCapture && isGoodCapture) || move.promotionPiece != NO_PIECE) {
            noisyMoves.push_back(move);
        }
    }

    std::sort(noisyMoves.begin(), noisyMoves.end(),
              [this, position](const Move &a, const Move &b) {
                  return scoreMove(a, position) > scoreMove(b, position);
              });

    for (const Move &move : noisyMoves) {
        position->moveMaker.makeMove(move);
        int score = -quiescence(position, -beta, -alpha, oppositeColor(color),
                                plyFromRoot + 1);
        position->moveMaker.unmakeMove();

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
        position.moveMaker.movePiece(move);

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
            if (cp == NO_PIECE)
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

bool Engine::isTimeUp() const {
    auto now = std::chrono::steady_clock::now();
    auto elapsed =
        std::chrono::duration_cast<std::chrono::milliseconds>(now - startTime)
            .count();
    return elapsed >= timeLimitMs;
}