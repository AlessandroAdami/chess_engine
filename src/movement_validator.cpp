#include "movement_validator.h"
#include "position.h"

MovementValidator::MovementValidator(Position *position) : position(position) {}

bool MovementValidator::isValidMove(const Move &move) const {
    int fromRow = move.from.row, fromCol = move.from.col;
    int toRow = move.to.row, toCol = move.to.col;
    ColoredPiece movingPiece =
        this->position->getPiece(Square{fromRow, fromCol});
    ColoredPiece capturedPiece = this->position->getCapturedPiece(move);
    if (fromRow < 0 || fromRow >= 8 || fromCol < 0 || fromCol >= 8 ||
        toRow < 0 || toRow >= 8 || toCol < 0 || toCol >= 8) {
        return false;
    }

    if (movingPiece == NO_Piece)
        return false;
    if (getColor(movingPiece) == getColor(capturedPiece))
        return false;
    if (getColor(movingPiece) == WHITE && !position->getIsWhitesTurn())
        return false;
    if (getColor(movingPiece) == BLACK && position->getIsWhitesTurn())
        return false;

    if (!isValidPieceMovement(movingPiece.piece, move))
        return false;

    if (moveLeadsIntoCheck(move))
        return false;

    return true;
}

bool MovementValidator::moveLeadsIntoCheck(Move move) const {

    MoveContext context = position->getMoveContext(move);

    position->movePiece(move);

    Color color = context.previousIsWhitesTurn ? WHITE : BLACK;
    bool isChecked = position->scanner.isInCheck(color);

    position->unmovePiece(context);

    return isChecked;
}

bool MovementValidator::isValidPieceMovement(Piece piece, Move move) const {
    if (move.from.col == move.to.col && move.from.row == move.to.row)
        return false;
    switch (piece) {
    case PAWN:
        return isValidPawnMovement(move);
    case KNIGHT:
        return isValidKnightMovement(move);
    case BISHOP:
        return isValidBishopMovement(move);
    case ROOK:
        return isValidRookMovement(move);
    case QUEEN:
        return isValidQueenMovement(move);
    case KING:
        return isValidKingMovement(move);
    default:
        return false;
    }
}

bool MovementValidator::isValidPawnMovement(Move move) const {
    int colorIndex;
    bool isWhite;
    ColoredPiece cp = this->position->getPiece(move.from);

    if (cp.color == WHITE) {
        colorIndex = 1;
        isWhite = true;
    } else {
        colorIndex = -1;
        isWhite = false;
    }

    int fromCol = move.from.col, fromRow = move.from.row;
    int toCol = move.to.col, toRow = move.to.row;

    if (fromCol == toCol && toRow == fromRow - colorIndex &&
        this->position->getPiece(Square{toRow, toCol}) == NO_Piece)
        return true;

    if (fromRow == (isWhite ? 6 : 1) && fromCol == toCol &&
        toRow == fromRow - (2 * colorIndex) &&
        this->position->getPiece(Square{toRow, toCol}) == NO_Piece &&
        this->position->getPiece(Square{toRow + colorIndex, toCol}) ==
            NO_Piece) {
        return true;
    }
    if (toCol == fromCol - 1 && toRow == fromRow - colorIndex &&
        this->position->getPiece(Square{toRow, toCol}) != NO_Piece)
        return true;
    if (toCol == fromCol + 1 && toRow == fromRow - colorIndex &&
        this->position->getPiece(Square{toRow, toCol}) != NO_Piece)
        return true;

    if (Square{toRow, toCol} == this->position->getEnpassantSquare() &&
        toCol == fromCol - 1 && toRow == fromRow - colorIndex &&
        this->position->getPiece(Square{toRow + colorIndex, toCol}) !=
            NO_Piece)
        return true;
    if (Square{toRow, toCol} == this->position->getEnpassantSquare() &&
        toCol == fromCol + 1 && toRow == fromRow - colorIndex &&
        this->position->getPiece(Square{toRow + colorIndex, toCol}) !=
            NO_Piece)
        return true;

    return false;
}

bool MovementValidator::isValidKnightMovement(Move move) const {
    return std::abs(move.to.col - move.from.col) *
               std::abs(move.to.row - move.from.row) ==
           2;
}

bool MovementValidator::isValidBishopMovement(Move move) const {
    int fromRow = move.from.row, fromCol = move.from.col;
    int toRow = move.to.row, toCol = move.to.col;

    if (std::abs(fromCol - toCol) != std::abs(fromRow - toRow)) {
        return false;
    }

    int dRow = (toRow > fromRow) ? 1 : -1;
    int dCol = (toCol > fromCol) ? 1 : -1;
    int steps = std::abs(toRow - fromRow);

    for (int i = 1; i < steps; ++i) {
        int checkRow = fromRow + i * dRow;
        int checkCol = fromCol + i * dCol;
        if (!position->isSquareEmpty(Square{checkRow, checkCol})) {
            return false;
        }
    }

    return true;
}

bool MovementValidator::isValidRookMovement(Move move) const {
    int fromRow = move.from.row, fromCol = move.from.col;
    int toRow = move.to.row, toCol = move.to.col;

    if (fromRow != toRow && fromCol != toCol) {
        return false;
    }

    int dRow = (toRow > fromRow) ? 1 : (toRow < fromRow) ? -1 : 0;
    int dCol = (toCol > fromCol) ? 1 : (toCol < fromCol) ? -1 : 0;
    int steps = std::abs(toRow - fromRow + toCol - fromCol);

    for (int i = 1; i < steps; ++i) {
        int checkRow = fromRow + i * dRow;
        int checkCol = fromCol + i * dCol;
        if (!position->isSquareEmpty(Square{checkRow, checkCol})) {
            return false;
        }
    }

    return true;
}

bool MovementValidator::isValidQueenMovement(Move move) const {
    if (isValidRookMovement(move)) {
        return true;
    }
    if (isValidBishopMovement(move)) {
        return true;
    }
    return false;
}

bool MovementValidator::isValidKingMovement(Move move) const {
    int fromRow = move.from.row, fromCol = move.from.col;
    int toRow = move.to.row, toCol = move.to.col;

    Color color =
        getColor(this->position->getPiece(Square{fromRow, fromCol}));
    bool isCastling =
        (std::abs(fromCol - toCol) == 2 && fromRow == toRow && fromCol == 4);
    if (isCastling) {
        if (this->position->scanner.isSquareInCheck(Square{fromRow, 4},
                                                      color)) {
            return false;
        }
        bool isKingside = (toCol == 6);
        bool isQueenside = (toCol == 2);
        if (isKingside &&
            (this->position->getCastleState(color) & KING_SIDE)) {
            for (int col = 5; col <= 6; col++) {
                if (this->position->scanner.isSquareInCheck(
                        Square{fromRow, col}, color)) {
                    return false;
                }
                if (!this->position->isSquareEmpty(Square{toRow, col})) {
                    return false;
                }
            }
        } else if (isQueenside &&
                   (this->position->getCastleState(color) & QUEEN_SIDE)) {
            for (int col = 2; col < 4; col++) {
                if (this->position->scanner.isSquareInCheck(
                        Square{fromRow, col}, color)) {
                    return false;
                }
                if (!this->position->isSquareEmpty(Square{toRow, col})) {
                    return false;
                }
            }
        } else {
            return false;
        }
        return true;
    }
    bool isDiagonalMove = std::abs((fromCol - toCol) * (fromRow - toRow)) == 1;
    bool isStraightMove =
        (fromCol == toCol && std::abs(fromRow - toRow) == 1) ||
        (fromRow == toRow && std::abs(fromCol - toCol) == 1);
    return isDiagonalMove || isStraightMove;
}

/**
 * @param color the color for which to get legal moves.
 * @returns a vector of all legal moves for the given color.
 */
std::vector<Move> MovementValidator::getLegalMoves(Color color) {
    std::vector<Move> legalMoves;
    for (int row = 0; row < 8; ++row) {
        for (int col = 0; col < 8; ++col) {
            ColoredPiece piece = this->position->getPiece(Square{row, col});
            if (getColor(piece) != color)
                continue;

            for (int targetRow = 0; targetRow < 8; ++targetRow) {
                for (int targetCol = 0; targetCol < 8; ++targetCol) {
                    Move move{row, col, targetRow, targetCol};
                    if (piece.piece == PAWN) {
                        // Handle pawn promotion
                        for (const Piece &promotionPiece :
                             {QUEEN, ROOK, BISHOP, KNIGHT}) {
                            if (targetRow == (color == WHITE ? 0 : 7)) {
                                move.promotionPiece =
                                    ColoredPiece(color, promotionPiece);
                            } else {
                                move.promotionPiece = NO_Piece;
                            }
                            if (position->movementValidator.isValidMove(
                                    move)) {
                                legalMoves.push_back(move);
                            }
                        }
                    } else if (position->movementValidator.isValidMove(
                                   move)) {
                        legalMoves.push_back(move);
                    }
                }
            }
        }
    }
    return legalMoves;
}