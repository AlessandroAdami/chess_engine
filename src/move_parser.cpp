#include "move_parser.h"
#include "chess_board.h"
#include <algorithm>
#include <cctype>
#include <stdexcept>
#include <vector>

MoveParser::MoveParser(ChessBoard *chessBoard) : chessBoard(chessBoard) {}

/**
 * Converts a move from algebraic notation (e.g. "e4", "Nf3", "O-O", "exf8=n")
 * to a Move object.
 */
Move MoveParser::moveStringToMove(const std::string &moveStr) {
    std::string normalizedInput = moveStr;
    std::transform(normalizedInput.begin(), normalizedInput.end(),
                   normalizedInput.begin(), ::tolower);

    bool isWhitesTurn = chessBoard->getIsWhitesTurn();
    Color color = isWhitesTurn ? WHITE : BLACK;
    std::vector<Move> legalMoves =
        chessBoard->movementValidator.getLegalMoves(color);

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
        std::string moveRepresentation;

        ColoredPiece cp =
            chessBoard->getPiece(Square{move.fromRow, move.fromCol});
        Piece type = cp.piece;

        char fromCol = 'a' + move.fromCol;
        char toCol = 'a' + move.toCol;
        char toRow = '1' + (7 - move.toRow);

        bool isCapture = this->chessBoard->getCapturedPiece(move) != NO_Piece;

        if (type == KING && std::abs(move.toCol - move.fromCol) == 2) {
            moveRepresentation = (move.toCol > move.fromCol) ? "o-o" : "o-o-o";
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

            // === DISAMBIGUATION START ===
            bool neeedDisambiguation = false;
            bool needCol = false;
            bool needRow = false;
            for (const Move &otherMove : legalMoves) {
                bool sameDestination = otherMove.toRow == move.toRow &&
                                       otherMove.toCol == move.toCol;
                bool differentSource = otherMove.fromRow != move.fromRow ||
                                       otherMove.fromCol != move.fromCol;
                if (sameDestination && differentSource) {
                    ColoredPiece otherPiece = chessBoard->getPiece(
                        Square{otherMove.fromRow, otherMove.fromCol});
                    if (otherPiece == cp) {
                        neeedDisambiguation = true;
                        if (otherMove.fromRow == move.fromRow)
                            needCol = true;
                        else if (otherMove.fromCol == move.fromCol)
                            needRow = true;
                    }
                }
            }

            if (needCol && needRow) {
                moveRepresentation += fromCol;
                moveRepresentation += '1' + (7 - move.fromRow);
            } else if (needCol) {
                moveRepresentation += fromCol;
            } else if (needRow) {
                moveRepresentation += '1' + (7 - move.fromRow);
            } else if (neeedDisambiguation) {
                moveRepresentation += fromCol;
            }
            // === DISAMBIGUATION END ===

            if (isCapture)
                moveRepresentation += 'x';

            moveRepresentation += toCol;
            moveRepresentation += toRow;
        }

        std::transform(moveRepresentation.begin(), moveRepresentation.end(),
                       moveRepresentation.begin(), ::tolower);

        moveSANPairs.emplace_back(move, moveRepresentation);
    }

    return moveSANPairs;
}