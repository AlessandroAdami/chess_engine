#include "chess_engine.h"
#include <limits>
#include <queue>

const int INF = std::numeric_limits<int>::max();
const int MATE_SCORE = INF;

ChessEngine::ChessEngine(ChessBoard *board) { this->chessBoard = board; }

Move ChessEngine::getBestMove() {
    switch (algorithm) {
    default:
        return minimax();
    }
}

/**
 * Uses the minimax algorithm to find the best move.
 * This is a simple implementation without alpha-beta pruning.
 */
Move ChessEngine::minimax() const {
    int depth = searchMoveDepth * 2;
    Move bestMove;
    int bestValue = -INF;
    Color color = chessBoard->getIsWhitesTurn() ? WHITE : BLACK;
    std::vector<Move> legalMoves =
        chessBoard->movementValidator.getLegalMoves(color);
    for (const Move &move : legalMoves) {
        MoveContext context = chessBoard->getMoveContext(move);
        chessBoard->movePiece(move);
        int moveValue = -negaMax(depth - 1);
        chessBoard->unmovePiece(context);
        if (moveValue > bestValue) {
            bestValue = moveValue;
            bestMove = move;
        }
    }
    return bestMove;
}

/**
 * NegaMax algorithm implementation.
 * This is a recursive function that evaluates the board state.
 * It returns a score for the current position.
 */
int ChessEngine::negaMax(int depth) const {
    if (depth == 0 || chessBoard->getIsGameOver()) {
        return evaluateBoard(chessBoard);
    }
    int max = -INF;
    Color color = chessBoard->getIsWhitesTurn() ? WHITE : BLACK;
    std::vector<Move> legalMoves =
        chessBoard->movementValidator.getLegalMoves(color);

    for (const Move &move : legalMoves) {
        MoveContext context = chessBoard->getMoveContext(move);
        chessBoard->movePiece(move);
        int score = -negaMax(depth - 1);
        chessBoard->unmovePiece(context);
        if (score > max) {
            max = score;
        }
    }
    return max;
}

Move ChessEngine::AStarSearch(int depth, bool isWhitesTurn) {
    return Move{0, 0, 0, 0};
}

int ChessEngine::evaluateBoard() const { return evaluateBoard(chessBoard); }

/**
 * A simple board evaluation (positive for white, negative for black).
 */
int ChessEngine::evaluateBoard(ChessBoard *board) const {
    if (board->isCheckmate()) {
        return board->getIsWhitesTurn() ? -MATE_SCORE : MATE_SCORE;
    } else if (board->isStalemate()) {
        return 0;
    }

    int score = 0;

    for (int row = 0; row < 8; ++row) {
        for (int col = 0; col < 8; ++col) {
            ColoredPiece cp = board->getPiece(Square{row, col});
            if (cp != NO_Piece) {
                score += getPieceValue(cp);
            }
        }
    }

    return score;
}

/**
 * Evaluates the board for a specific color.
 * A positive score means 'color' is advantaged, negative means disadvantaged.
 */
int ChessEngine::evaluateBoardForColor(ChessBoard *board, Color color) const {
    int score = evaluateBoard(board);
    int colorMultiplier = (color == WHITE) ? 1 : -1;
    return score * colorMultiplier;
}

int ChessEngine::getPieceValue(const ColoredPiece &cp) const {
    int value;
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