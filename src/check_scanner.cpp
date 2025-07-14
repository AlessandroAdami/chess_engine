#include "check_scanner.h"
#include "position.h"

/**
 * Looks for checks in the board, assumes you are asking about the color whose
 * move it is
 */

CheckScanner::CheckScanner(Position *position) : position(position) {}

bool CheckScanner::isInCheck(Color color) const {
    Square kingSquare = getKingSquare(color);

    return isSquareInCheck(kingSquare, color);
}

Square CheckScanner::getKingSquare(Color color) const {
    ColoredPiece wK = ColoredPiece(WHITE, KING);
    ColoredPiece bK = ColoredPiece(BLACK, KING);
    for (int row = 0; row < 8; ++row) {
        for (int col = 0; col < 8; ++col) {
            if (position->getPiece(Square{row, col}) == wK &&
                color == WHITE) {
                return Square{row, col};
            } else if (position->getPiece(Square{row, col}) == bK &&
                       color == BLACK) {
                return Square{row, col};
            }
        }
    }
    return Square{-1, -1};
}

bool CheckScanner::isInCheckmate(Color color) const {
    if (!isInCheck(color))
        return false;
    return !areThereLegalMoves(color);
}

bool CheckScanner::isInStalemate(Color color) const {
    if (isInCheck(color))
        return false;
    return !areThereLegalMoves(color);
}

bool CheckScanner::areThereLegalMoves(Color color) const {
    for (int row = 0; row < 8; ++row) {
        for (int col = 0; col < 8; ++col) {
            ColoredPiece piece = position->getPiece(Square{row, col});
            if (getColor(piece) != color)
                continue;

            for (int targetRow = 0; targetRow < 8; ++targetRow) {
                for (int targetCol = 0; targetCol < 8; ++targetCol) {
                    Move move{Square{row, col}, Square{targetRow, targetCol}};
                    if (position->movementValidator.isValidMove(move)) {
                        return true;
                    }
                }
            }
        }
    }
    return false;
}

bool CheckScanner::isSquareInCheck(Square square, Color color) const {
    for (int row = 0; row < 8; ++row) {
        for (int col = 0; col < 8; ++col) {
            ColoredPiece cp = position->getPiece(Square{row, col});
            if (getColor(cp) == color || cp == NO_Piece)
                continue;

            Move move{Square{row, col}, square};
            if (position->movementValidator.isValidPieceMovement(cp.piece,
                                                                   move)) {
                return true;
            }
        }
    }
    return false;
}