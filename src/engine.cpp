#include "engine.h"

ChessEngine::ChessEngine(Position *position) { this->position = position; }

Move ChessEngine::getBestMove() {
    switch (algorithm) {
    default:
        return minimax();
    }
}

/**
 * Minimax with alpha-beta pruning.
 */
Move ChessEngine::minimax() const {
    int depth = searchMoveDepth;
    Color color = position->getIsWhitesTurn() ? WHITE : BLACK;
    std::vector<Move> legalMoves =
        position->movementValidator.getLegalMoves(color);

    if (legalMoves.empty()) {
        return Move{0, 0, 0, 0};
    }

    Move bestMove = legalMoves.front();
    int bestValue = -INF;
    int alpha = -INF;
    int beta = INF;

    for (const Move &move : legalMoves) {
        MoveContext context = position->movePiece(move);
        position->changeTurn();
        int score = -negaMaxAlphaBeta(depth - 1, -beta, -alpha);
        position->unmovePiece(context);
        position->changeTurn();

        if (score > bestValue) {
            bestValue = score;
            bestMove = move;
        }

        if (score > alpha) {
            alpha = score;
        }
    }

    return bestMove;
}

/**
 * NegaMax with alpha-beta pruning (single-threaded).
 */
int ChessEngine::negaMaxAlphaBeta(int depth, int alpha, int beta) const {
    if (depth == 0 || position->getIsGameOver()) {
        Color color = position->getTurn();
        return evaluatePositionForColor(position, color);
    }

    int maxScore = -INF;
    Color color = position->getIsWhitesTurn() ? WHITE : BLACK;
    std::vector<Move> legalMoves =
        position->movementValidator.getLegalMoves(color);

    for (const Move &move : legalMoves) {
        MoveContext context = position->movePiece(move);
        position->changeTurn();
        int score = -negaMaxAlphaBeta(depth - 1, -beta, -alpha);
        position->unmovePiece(context);
        position->changeTurn();

        if (score > maxScore) {
            maxScore = score;
            if (score > alpha)
                alpha = score;
        }

        if (alpha >= beta) {
            return maxScore;
        }
    }

    return maxScore;
}

int ChessEngine::evaluatePosition() const { return evaluatePosition(position); }

/**
 * Simple material-based evaluation (positive for white, negative for black).
 */
int ChessEngine::evaluatePosition(Position *position) const {
    int score = 0;
    if (position->isCheckmate()) {
        score = position->getIsWhitesTurn() ? BLACK_WIN_SCORE : WHITE_WIN_SCORE;
        return score;
    } else if (position->isStalemate()) {
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
int ChessEngine::evaluatePositionForColor(Position *position,
                                          Color color) const {
    int score = evaluatePosition(position);
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
