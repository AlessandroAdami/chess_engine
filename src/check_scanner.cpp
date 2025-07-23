#include "check_scanner.h"
#include "position.h"

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
            if (position->getPiece(Square(row, col)) == wK && color == WHITE) {
                return Square(row, col);
            } else if (position->getPiece(Square(row, col)) == bK &&
                       color == BLACK) {
                return Square(row, col);
            }
        }
    }
    return INVALID_SQUARE;
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
            ColoredPiece cp = position->getPiece(Square(row, col));
            if (cp.color != color)
                continue;

            for (int targetRow = 0; targetRow < 8; ++targetRow) {
                for (int targetCol = 0; targetCol < 8; ++targetCol) {
                    Move move{Square(row, col), Square(targetRow, targetCol),
                              NO_PIECE};
                    if (cp.piece == PAWN) {
                        // Handle pawn promotion
                        int promotionRow = (cp.color == WHITE) ? 0 : 7;
                        if (targetRow == promotionRow) {
                            for (const Piece &promotionPiece :
                                 {QUEEN, ROOK, BISHOP, KNIGHT}) {
                                move.promotionPiece =
                                    ColoredPiece(cp.color, promotionPiece);
                                if (position->movementValidator.isValidMove(
                                        move)) {
                                    return true;
                                }
                            }
                        } else if (position->movementValidator.isValidMove(
                                       move)) {
                            return true;
                        }
                    } else if (position->movementValidator.isValidMove(move)) {
                        return true;
                    }
                }
            }
        }
    }
    return false;
}

bool CheckScanner::isSquareInCheck(Square target, Color color) const {
    for (int row = 0; row < 8; ++row) {
        for (int col = 0; col < 8; ++col) {
            ColoredPiece cp = position->getPiece(Square(row, col));
            if (cp.color == color || cp == NO_PIECE)
                continue;

            Move move{Square(row, col), target};
            if (cp.piece == PAWN) {
                // Handle pawn promotion
                int promotionRow = (cp.color == WHITE) ? 0 : 7;
                if (target.row == promotionRow) {
                    for (const Piece &promotionPiece :
                         {QUEEN, ROOK, BISHOP, KNIGHT}) {
                        move.promotionPiece =
                            ColoredPiece(cp.color, promotionPiece);
                        if (position->movementValidator.isValidPieceMovement(
                                cp.piece, move)) {
                            return true;
                        }
                    }
                } else if (position->movementValidator.isValidPieceMovement(
                               cp.piece, move)) {
                    return true;
                }
            } else if (position->movementValidator.isValidPieceMovement(
                           cp.piece, move)) {
                return true;
            }
        }
    }
    return false;
}