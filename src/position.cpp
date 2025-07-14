#include "position.h"
#include "types.h"
#include <iostream>
#include <sstream>
#include <string>
#include <vector>

const std::string startFEN =
    "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";

Position::Position()
    : scanner(this), movementValidator(this), moveMaker(this),
      moveParser(this) {
    for (int row = 0; row < 8; ++row)
        for (int col = 0; col < 8; ++col)
            board[row][col] = NO_Piece;
    loadFEN(startFEN);
    this->isGameOver = false;
}
/**
 * Loads the board state from a FEN string.
 * The FEN string should be in the format:
 * <board state> <active color> <castling rights> <en passant square> <halfmove>
 * <fullmove>
 */
void Position::loadFEN(const std::string &fen) {
    for (int r = 0; r < 8; r++) {
        for (int c = 0; c < 8; c++) {
            board[r][c] = NO_Piece;
        }
    }
    std::istringstream iss(fen);
    std::string boardPart, activeColor, castling, enPassant;
    int halfmoveClock, fullmoveNumber;

    iss >> boardPart >> activeColor >> castling >> enPassant >> halfmoveClock >>
        fullmoveNumber;

    int row = 0, col = 0;
    for (char c : boardPart) {
        if (c == '/') {
            ++row;
            col = 0;
        } else if (std::isdigit(c)) {
            col += c - '0';
        } else {
            board[row][col] = charToColoredPiece(c);
            ++col;
        }
    }

    isWhitesTurn = (activeColor == "w");

    castleState[0] = 0;
    castleState[1] = 0;
    for (char c : castling) {
        switch (c) {
        case 'K':
            castleState[0] |= KING_SIDE;
            break;
        case 'Q':
            castleState[0] |= QUEEN_SIDE;
            break;
        case 'k':
            castleState[1] |= KING_SIDE;
            break;
        case 'q':
            castleState[1] |= QUEEN_SIDE;
            break;
        }
    }

    if (enPassant == "-") {
        enPassantSquare = Square{-1, -1};
    } else {
        char file = enPassant[0];
        char rank = enPassant[1];
        enPassantSquare = Square{8 - (rank - '0'), file - 'a'};
    }

    this->halfmoveClock = halfmoveClock;
    this->fullmoveNumber = fullmoveNumber;
    this->moveMaker.clearMoveHistory();
}

std::string Position::getFEN() const {
    std::ostringstream oss;
    for (int row = 0; row < 8; ++row) {
        int emptyCount = 0;
        for (int col = 0; col < 8; ++col) {
            if (board[row][col] == NO_Piece) {
                emptyCount++;
            } else {
                if (emptyCount > 0) {
                    oss << emptyCount;
                    emptyCount = 0;
                }
                oss << pieceToChar(board[row][col]);
            }
        }
        if (emptyCount > 0) {
            oss << emptyCount;
        }
        if (row < 7) {
            oss << '/';
        }
    }

    oss << ' ' << (isWhitesTurn ? 'w' : 'b') << ' ';
    if (castleState[0] & KING_SIDE)
        oss << 'K';
    if (castleState[0] & QUEEN_SIDE)
        oss << 'Q';
    if (castleState[1] & KING_SIDE)
        oss << 'k';
    if (castleState[1] & QUEEN_SIDE)
        oss << 'q';
    if (!(castleState[0] | castleState[1]))
        oss << '-';

    if (enPassantSquare.row == -1 && enPassantSquare.col == -1) {
        oss << " -";
    } else {
        char file = 'a' + enPassantSquare.col;
        char rank = '8' - enPassantSquare.row;
        oss << ' ' << file << rank;
    }

    oss << " " << halfmoveClock << " " << fullmoveNumber;

    return oss.str();
}

void Position::printBoard() const {
    std::cout << "\n   a b c d e f g h\n";
    std::cout << "   ~~~~~~~~~~~~~~~\n";
    for (int row = 0; row < 8; ++row) {
        std::cout << 8 - row << "| ";
        for (int col = 0; col < 8; ++col) {
            std::cout << pieceToChar(board[row][col]) << " ";
        }
        std::cout << "|" << 8 - row << "\n";
    }
    std::cout << "   ~~~~~~~~~~~~~~~\n";
    std::cout << "   a b c d e f g h\n";
}

/**
 * Makes a move on the chessboard if it's valid.
 * @returns the piece that was moved, or an empty ColoredPiece if the move was
 * illegal.
 */
MoveContext Position::makeMove(const Move &move) {
    if (!movementValidator.isValidMove(move)) {
        std::cerr << "Illegal move.\n";
        return MoveContext();
    }
    MoveContext context = this->moveMaker.makeMove(move);

    if (isCheckmate() || isStalemate()) {
        isGameOver = true;
    }

    return context;
}

/**
 * Undoes the most recent move and updates the game state accordingly.
 */
void Position::undoMove() { this->moveMaker.undoMove(); }

/**
 * Redoes the most recent undone move and updates the game state accordingly.
 * If there are no moves to redo, this function does nothing.
 */
void Position::redoMove() { this->moveMaker.redoMove(); }

/**
 * Moves a piece without having an effect on the game state
 * (e.g. does not change the turn, does not add to move history).
 * This is used for previewing moves.
 */
MoveContext Position::movePiece(const Move &move) {
    return this->moveMaker.movePiece(move);
}

/**
 * Like movePiece, but undoes the move and restores the previous position state.
 */
void Position::unmovePiece(const MoveContext &context) {
    this->moveMaker.unmovePiece(context);
}

/**
 * @returns the piece at the given square.
 */
ColoredPiece Position::getPiece(Square square) const {
    return board[square.row][square.col];
}

void Position::setPiece(Square square, ColoredPiece cp) {
    board[square.row][square.col] = cp;
}

/**
 * @returns true if the given square is empty (i.e., contains no piece).
 */
bool Position::isSquareEmpty(const Square &square) const {
    return getPiece(square) == NO_Piece;
}

/**
 * @returns the move contex of the given move
 */
MoveContext Position::getMoveContext(const Move &move) {
    MoveContext context;
    context.move = move;
    context.capturedPiece = getCapturedPiece(move);
    context.previousEnPassant = this->enPassantSquare;
    context.previousCastleState[0] = this->castleState[0];
    context.previousCastleState[1] = this->castleState[1];
    context.previousHalfmoveClock = this->halfmoveClock;
    context.previousFullmoveNumber = this->fullmoveNumber;
    context.previousIsWhitesTurn = this->isWhitesTurn;
    context.previousIsGameOver = this->isGameOver;
    context.wasEnPassantCapture = isEnPassant(move);
    context.wasCastling = isCastling(move);

    return context;
}

ColoredPiece Position::getCapturedPiece(const Move &move) const {
    ColoredPiece capturedPiece = getPiece(move.to);
    if (isEnPassant(move)) {
        int colorIndex = (getPiece(move.from).color == WHITE) ? 1 : -1;
        Square toSquare = move.to;
        capturedPiece =
            getPiece(Square{toSquare.row + colorIndex, toSquare.col});
    }
    return capturedPiece;
}

bool Position::isEnPassant(const Move &move) const {
    ColoredPiece movingPiece = getPiece(move.from);
    if (movingPiece.piece != PAWN)
        return false;
    return move.to == this->enPassantSquare;
}

bool Position::isCastling(const Move &move) const {
    ColoredPiece movingPiece = getPiece(move.from);
    if (movingPiece.piece != KING)
        return false;
    Square fromSquare = move.from;
    Square toSquare = move.to;
    return std::abs(fromSquare.col - toSquare.col) == 2;
}

bool Position::isCheckmate() const {
    return this->scanner.isInCheckmate(isWhitesTurn ? WHITE : BLACK);
}
bool Position::isStalemate() const {
    return this->scanner.isInStalemate(isWhitesTurn ? WHITE : BLACK);
}
