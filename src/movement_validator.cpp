#include "movement_validator.h"
#include "position.h"
#include "types.h"
#include <algorithm>

MovementValidator::MovementValidator(Position *position) : position(position) {}

bool MovementValidator::isValidMove(const Move &move) const {
    int fromRow = move.from.row, fromCol = move.from.col;
    int toRow = move.to.row, toCol = move.to.col;
    ColoredPiece movingPiece = this->position->getPiece(move.from);
    ColoredPiece capturedPiece = this->position->getCapturedPiece(move);
    if (fromRow < 0 || fromRow >= 8 || fromCol < 0 || fromCol >= 8 ||
        toRow < 0 || toRow >= 8 || toCol < 0 || toCol >= 8) {
        return false;
    }

    if (movingPiece == NO_PIECE)
        return false;
    if (movingPiece.color == capturedPiece.color)
        return false;
    if (movingPiece.color != position->getTurn())
        return false;

    if (!isValidPieceMovement(movingPiece.piece, move))
        return false;

    if (moveLeadsIntoCheck(move))
        return false;

    return true;
}

bool MovementValidator::moveLeadsIntoCheck(Move move) const {

    MoveContext context = position->getMoveContext(move);

    position->moveMaker.movePiece(move);

    Color color = context.previousTurn;
    bool isChecked = position->scanner.isInCheck(color);

    position->moveMaker.unmovePiece(context);

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
    int promotionRow = (cp.color == WHITE) ? 0 : 7;

    if (toRow == promotionRow && move.promotionPiece == NO_PIECE)
        return false;

    if (fromCol == toCol && toRow == fromRow - colorIndex &&
        this->position->getPiece(move.to) == NO_PIECE)
        return true;

    if (fromRow == (isWhite ? 6 : 1) && fromCol == toCol &&
        toRow == fromRow - (2 * colorIndex) &&
        this->position->getPiece(move.to) == NO_PIECE &&
        this->position->getPiece(Square(toRow + colorIndex, toCol)) ==
            NO_PIECE) {
        return true;
    }
    if (toCol == fromCol - 1 && toRow == fromRow - colorIndex &&
        this->position->getPiece(move.to) != NO_PIECE)
        return true;
    if (toCol == fromCol + 1 && toRow == fromRow - colorIndex &&
        this->position->getPiece(move.to) != NO_PIECE)
        return true;

    if (move.to == this->position->getEnpassantSquare() &&
        toCol == fromCol - 1 && toRow == fromRow - colorIndex &&
        this->position->getPiece(Square(toRow + colorIndex, toCol)) != NO_PIECE)
        return true;
    if (move.to == this->position->getEnpassantSquare() &&
        toCol == fromCol + 1 && toRow == fromRow - colorIndex &&
        this->position->getPiece(Square(toRow + colorIndex, toCol)) != NO_PIECE)
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
        if (!position->isSquareEmpty(Square(checkRow, checkCol))) {
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
        if (!position->isSquareEmpty(Square(checkRow, checkCol))) {
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

    ColoredPiece cp = this->position->getPiece(move.from);
    Color color = cp.color;
    bool isCastling =
        (std::abs(fromCol - toCol) == 2 && fromRow == toRow && fromCol == 4);
    if (isCastling) {
        if (this->position->scanner.isSquareInCheck(Square(fromRow, 4),
                                                    color)) {
            return false;
        }
        bool isKingside = (toCol == 6);
        bool isQueenside = (toCol == 2);
        if (isKingside && (this->position->getCastleState(color) & KING_SIDE)) {
            for (int col = 5; col <= 6; col++) {
                if (this->position->scanner.isSquareInCheck(
                        Square(fromRow, col), color)) {
                    return false;
                }
                if (!this->position->isSquareEmpty(Square(toRow, col))) {
                    return false;
                }
            }
        } else if (isQueenside &&
                   (this->position->getCastleState(color) & QUEEN_SIDE)) {
            for (int col = 2; col < 4; col++) {
                if (this->position->scanner.isSquareInCheck(
                        Square(fromRow, col), color)) {
                    return false;
                }
                if (!this->position->isSquareEmpty(Square(toRow, col))) {
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
    std::unordered_set<Square> piecesSquares =
        position->getPiecesSquares(color);

    for (Square from : piecesSquares) {
        std::vector<Move> pieceMoves;
        ColoredPiece cp = this->position->getPiece(from);
        switch (cp.piece) {
        case PAWN:
            pieceMoves = getLegalPawnMovements(from, color);
            break;
        case KNIGHT:
            pieceMoves = getLegalKnightMovements(from, color);
            break;
        case BISHOP:
            pieceMoves = getLegalBishopMovements(from, color);
            break;
        case ROOK:
            pieceMoves = getLegalRookMovements(from, color);
            break;
        case QUEEN:
            pieceMoves = getLegalQueenMovements(from, color);
            break;
        case KING:
            pieceMoves = getLegalKingMovements(from, color);
            break;
        default: // NO_PIECE
            break;
        }
        std::erase_if(pieceMoves, [&](const Move &move) {
            return moveLeadsIntoCheck(move);
        });
        legalMoves.insert(legalMoves.end(), pieceMoves.begin(),
                          pieceMoves.end());
    }

    return legalMoves;
}

std::vector<Move> MovementValidator::getLegalPawnMovements(Square from,
                                                           Color color) const {
    std::vector<Move> pawnMoves;

    int direction = (color == WHITE) ? -1 : 1;
    int startRow = (color == WHITE) ? 6 : 1;
    int promotionRow = (color == WHITE) ? 0 : 7;

    Square oneStep(from.row + direction, from.col);
    Square twoStep(from.row + 2 * direction, from.col);

    if (oneStep.isValid() && position->getPiece(oneStep) == NO_PIECE) {
        if (oneStep.row == promotionRow) {
            for (Piece p : {QUEEN, ROOK, BISHOP, KNIGHT}) {
                ColoredPiece cp(color, p);
                pawnMoves.push_back(Move(from, oneStep, cp));
            }
        } else {
            pawnMoves.push_back(Move(from, oneStep));
        }

        if (from.row == startRow && position->getPiece(twoStep) == NO_PIECE) {
            pawnMoves.push_back(Move(from, twoStep));
        }
    }

    for (int dc : {-1, 1}) {
        Square diag(from.row + direction, from.col + dc);
        if (diag.isValid()) {
            ColoredPiece target = position->getPiece(diag);
            if (target != NO_PIECE && target.color != color) {
                if (diag.row == promotionRow) {
                    for (Piece p : {QUEEN, ROOK, BISHOP, KNIGHT}) {
                        ColoredPiece cp(color, p);
                        pawnMoves.push_back(Move(from, diag, cp));
                    }
                } else {
                    pawnMoves.push_back(Move(from, diag));
                }
            }
        }
    }

    Square ep = position->getEnpassantSquare();
    if (ep.isValid() && ep.row == from.row + direction &&
        std::abs(ep.col - from.col) == 1) {
        pawnMoves.push_back(Move(from, ep));
    }

    return pawnMoves;
}

std::vector<Move>
MovementValidator::getLegalKnightMovements(Square from, Color color) const {
    std::vector<Move> knightMoves;
    std::vector<Square> toSquares;
    int fromRow = from.row, fromCol = from.col;
    toSquares.push_back(Square(fromRow + 2, fromCol + 1));
    toSquares.push_back(Square(fromRow + 2, fromCol - 1));
    toSquares.push_back(Square(fromRow + 1, fromCol + 2));
    toSquares.push_back(Square(fromRow + 1, fromCol - 2));
    toSquares.push_back(Square(fromRow - 1, fromCol + 2));
    toSquares.push_back(Square(fromRow - 1, fromCol - 2));
    toSquares.push_back(Square(fromRow - 2, fromCol + 1));
    toSquares.push_back(Square(fromRow - 2, fromCol - 1));

    for (Square to : toSquares) {
        bool isValidSquare =
            (to.col < 8 && to.col > -1 && to.row < 8 && to.row > -1);
        bool isNotFriend = position->getPiece(to).color != color;
        if (isValidSquare && isNotFriend) {
            knightMoves.push_back(Move(from, to));
        }
    }
    return knightMoves;
}

std::vector<Move>
MovementValidator::getLegalBishopMovements(Square from, Color color) const {
    std::vector<Move> bishopMoves;
    static const std::vector<std::pair<int, int>> directions = {
        {1, 1}, {1, -1}, {-1, 1}, {-1, -1}};

    for (auto [dr, dc] : directions) {
        int r = from.row + dr, c = from.col + dc;
        while (r >= 0 && r < 8 && c >= 0 && c < 8) {
            Square to(r, c);
            ColoredPiece cp = position->getPiece(to);
            if (cp == NO_PIECE) {
                bishopMoves.push_back(Move(from, to));
            } else {
                if (cp.color != color)
                    bishopMoves.push_back(Move(from, to));
                break;
            }
            r += dr;
            c += dc;
        }
    }

    return bishopMoves;
}

std::vector<Move> MovementValidator::getLegalRookMovements(Square from,
                                                           Color color) const {
    std::vector<Move> rookMoves;
    static const std::vector<std::pair<int, int>> directions = {
        {1, 0}, {-1, 0}, {0, 1}, {0, -1}};

    for (auto [dr, dc] : directions) {
        int r = from.row + dr, c = from.col + dc;
        while (r >= 0 && r < 8 && c >= 0 && c < 8) {
            Square to(r, c);
            ColoredPiece cp = position->getPiece(to);
            if (cp == NO_PIECE) {
                rookMoves.push_back(Move(from, to));
            } else {
                if (cp.color != color)
                    rookMoves.push_back(Move(from, to));
                break;
            }
            r += dr;
            c += dc;
        }
    }

    return rookMoves;
}

std::vector<Move> MovementValidator::getLegalQueenMovements(Square from,
                                                            Color color) const {
    std::vector<Move> queenMoves;

    static const std::vector<std::pair<int, int>> directions = {
        {1, 0}, {-1, 0}, {0, 1}, {0, -1}, {1, 1}, {1, -1}, {-1, 1}, {-1, -1}};

    for (auto [dr, dc] : directions) {
        int r = from.row + dr, c = from.col + dc;
        while (r >= 0 && r < 8 && c >= 0 && c < 8) {
            Square to(r, c);
            ColoredPiece cp = position->getPiece(to);
            if (cp == NO_PIECE) {
                queenMoves.push_back(Move(from, to));
            } else {
                if (cp.color != color)
                    queenMoves.push_back(Move(from, to));
                break;
            }
            r += dr;
            c += dc;
        }
    }

    return queenMoves;
}

std::vector<Move> MovementValidator::getLegalKingMovements(Square from,
                                                           Color color) const {
    std::vector<Move> kingMoves;
    static const std::vector<std::pair<int, int>> deltas = {
        {-1, -1}, {-1, 0}, {-1, 1}, {0, -1}, {0, 1}, {1, -1}, {1, 0}, {1, 1}};

    for (auto [dr, dc] : deltas) {
        int r = from.row + dr, c = from.col + dc;
        if (r >= 0 && r < 8 && c >= 0 && c < 8) {
            Square to(r, c);
            ColoredPiece cp = position->getPiece(to);
            if (cp == NO_PIECE || cp.color != color) {
                kingMoves.push_back(Move(from, to));
            }
        }
    }

    int fromRowCastling = (color == WHITE) ? 7 : 0;
    Square fromCastling(fromRowCastling, 4);
    if (from == fromCastling) {
        for (Square to :
             {Square(fromRowCastling, 2), Square(fromRowCastling, 6)}) {
            Move move(from, to);
            if (isValidKingMovement(move)) {
                kingMoves.push_back(move);
            }
        }
    }
    return kingMoves;
}