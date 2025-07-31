#include "move_parser.h"
#include "position.h"
#include "types.h"
#include <algorithm>
#include <cctype>
#include <stdexcept>
#include <vector>

std::vector<Move> legalMoves;

MoveParser::MoveParser(Position *position) : position(position) {}

void MoveParser::loadLegalMoves() const {
    bool isWhitesTurn = (position->getTurn() == WHITE);
    Color color = isWhitesTurn ? WHITE : BLACK;
    legalMoves = position->movementValidator.getLegalMoves(color);
}

/**
 * Converts a move from algebraic notation (e.g. "e4", "Nf3", "O-O", "exf8=n")
 * to a Move object.
 */
Move MoveParser::moveStringToMove(const std::string &moveStr) {
    std::string normalizedInput = moveStr;
    std::transform(normalizedInput.begin(), normalizedInput.end(),
                   normalizedInput.begin(), ::tolower);

    loadLegalMoves();

    std::vector<std::pair<Move, std::string>> moveSANPairs =
        getMoveSANPairs(legalMoves);
    for (const std::pair<Move, std::string> &moveSAN : moveSANPairs) {
        if (moveSAN.second == normalizedInput) {
            return moveSAN.first;
        }
    }

    throw std::invalid_argument("Invalid or ambiguous move string: " + moveStr);
}

/**
 * Generates a vector of pairs containing legal moves and their corresponding
 * string representations in Standard Algebraic Notation (SAN).
 */
std::vector<std::pair<Move, std::string>>
MoveParser::getMoveSANPairs(std::vector<Move> legalMoves) const {
    std::vector<std::pair<Move, std::string>> moveSANPairs;
    for (const Move &move : legalMoves) {
        std::string moveRepresentation = moveToString(move);

        moveSANPairs.emplace_back(move, moveRepresentation);
    }

    return moveSANPairs;
}

std::string MoveParser::moveToString(const Move move) const {
    loadLegalMoves();
    std::string moveRepresentation;
    ColoredPiece cp = position->getPiece(move.from);
    Piece type = cp.piece;

    char fromCol = 'a' + move.from.col;
    char toCol = 'a' + move.to.col;
    char toRow = '1' + (7 - move.to.row);

    bool isCapture = this->position->getCapturedPiece(move) != NO_COLORED_PIECE;

    if (type == KING && std::abs(move.to.col - move.from.col) == 2) {
        moveRepresentation = (move.to.col > move.from.col) ? "o-o" : "o-o-o";
    } else if (type == PAWN) {
        if (isCapture) {
            moveRepresentation += fromCol;
            moveRepresentation += 'x';
        }
        moveRepresentation += toCol;
        moveRepresentation += toRow;

        if (move.promotionPiece.piece != EMPTY) {
            moveRepresentation += '=';
            moveRepresentation +=
                std::tolower(pieceToChar(move.promotionPiece));
        }
    } else {
        moveRepresentation += std::tolower(pieceToChar(cp));

        bool neeedDisambiguation = false;
        bool needCol = false;
        bool needRow = false;
        for (const Move &otherMove : legalMoves) {
            bool sameDestination = otherMove.to == move.to;
            bool differentSource = otherMove.from != move.from;
            if (sameDestination && differentSource) {
                ColoredPiece otherPiece = position->getPiece(otherMove.from);
                if (otherPiece == cp) {
                    neeedDisambiguation = true;
                    if (otherMove.from.row == move.from.row)
                        needCol = true;
                    else if (otherMove.from.col == move.from.col)
                        needRow = true;
                }
            }
        }

        if (needCol && needRow) {
            moveRepresentation += fromCol;
            moveRepresentation += '1' + (7 - move.from.row);
        } else if (needCol) {
            moveRepresentation += fromCol;
        } else if (needRow) {
            moveRepresentation += '1' + (7 - move.from.row);
        } else if (neeedDisambiguation) {
            moveRepresentation += fromCol;
        }

        if (isCapture)
            moveRepresentation += 'x';

        moveRepresentation += toCol;
        moveRepresentation += toRow;
    }

    std::transform(moveRepresentation.begin(), moveRepresentation.end(),
                   moveRepresentation.begin(), ::tolower);

    return moveRepresentation;
}

Move uciToMove(const std::string &uci, const Position *position) {
    if (uci.size() < 4)
        return Move(); // Invalid move

    // Parse from square
    int fromCol = uci[0] - 'a';
    int fromRow = uci[1] - '1';
    Square from(fromRow, fromCol);

    // Parse to square
    int toCol = uci[2] - 'a';
    int toRow = uci[3] - '1';
    Square to(toRow, toCol);

    ColoredPiece promotion = NO_COLORED_PIECE;

    if (uci.size() == 5) {
        Piece promotedPiece;
        switch (uci[4]) {
        case 'q':
            promotedPiece = QUEEN;
            break;
        case 'r':
            promotedPiece = ROOK;
            break;
        case 'b':
            promotedPiece = BISHOP;
            break;
        case 'n':
            promotedPiece = KNIGHT;
            break;
        default:
            promotedPiece = EMPTY;
        }

        if (promotedPiece != EMPTY) {
            Color turn = position->getTurn();
            promotion = ColoredPiece{turn, promotedPiece};
        }
    }

    return Move(from, to, promotion);
}
