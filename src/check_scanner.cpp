#include "check_scanner.h"
#include "position.h"
#include "types.h"

CheckScanner::CheckScanner(Position *position) : position(position) {}

bool CheckScanner::isInCheck(Color color) const {
    Square kingSquare = getKingSquare(color);

    return isSquareInCheck(kingSquare, color);
}

Square CheckScanner::getKingSquare(Color color) const {
    ColoredPiece king = ColoredPiece(color, KING);

    std::unordered_set<Square> piecesSquares =
        position->getPiecesSquares(color);

    for (Square square : piecesSquares) {
        if (position->getPiece(square) == king) {
            return square;
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
    std::unordered_set<Square> piecesSquares =
        position->getPiecesSquares(color);
    for (Square from : piecesSquares) {
        ColoredPiece cp = position->getPiece(from);
        if (cp.color != color)
            continue;

        for (int targetRow = 0; targetRow < 8; ++targetRow) {
            for (int targetCol = 0; targetCol < 8; ++targetCol) {
                Move move(from, Square(targetRow, targetCol), NO_PIECE);
                if (cp.piece == PAWN) {
                    // Handle pawn promotion
                    int promotionRow = (cp.color == WHITE) ? 0 : 7;
                    if (targetRow == promotionRow) {
                        for (const Piece &promotionPiece :
                             {QUEEN, ROOK, BISHOP, KNIGHT}) {
                            move.promotionPiece =
                                ColoredPiece(cp.color, promotionPiece);
                            if (position->movementValidator.isValidMove(move)) {
                                return true;
                            }
                        }
                    } else if (position->movementValidator.isValidMove(move)) {
                        return true;
                    }
                } else if (position->movementValidator.isValidMove(move)) {
                    return true;
                }
            }
        }
    }

    return false;
}

bool CheckScanner::isSquareInCheck(Square target, Color color) const {
    Color opponent = (color == WHITE) ? BLACK : WHITE;
    std::unordered_set<Square> opponentPiecesSquares =
        position->getPiecesSquares(opponent);

    for (Square from : opponentPiecesSquares) {
        ColoredPiece cp = position->getPiece(from);
        if (cp.color == color || cp == NO_PIECE)
            continue;

        Move move(from, target);
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
        } else if (position->movementValidator.isValidPieceMovement(cp.piece,
                                                                    move)) {
            return true;
        }
    }

    return false;
}