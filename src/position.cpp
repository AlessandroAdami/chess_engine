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
            board[row][col] = NO_PIECE;
    loadFEN(startFEN);
    this->isGameOver = false;
}

Position::Position(const Position &p)
    : scanner(this), movementValidator(this), moveMaker(this),
      moveParser(this) {

    std::string fen = p.getFEN();
    this->loadFEN(fen);

    isGameOver = p.isGameOver;
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
            board[r][c] = NO_PIECE;
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

    turn = (activeColor == "w") ? WHITE : BLACK;

    castleState.white = 0;
    castleState.black = 0;
    for (char c : castling) {
        switch (c) {
        case 'K':
            castleState.white |= KING_SIDE;
            break;
        case 'Q':
            castleState.white |= QUEEN_SIDE;
            break;
        case 'k':
            castleState.black |= KING_SIDE;
            break;
        case 'q':
            castleState.black |= QUEEN_SIDE;
            break;
        }
    }

    if (enPassant == "-") {
        enPassantSquare = INVALID_SQUARE;
    } else {
        char file = enPassant[0];
        char rank = enPassant[1];
        enPassantSquare = Square(8 - (rank - '0'), file - 'a');
    }

    this->halfmoveClock = halfmoveClock;
    this->fullmoveNumber = fullmoveNumber;
    this->moveMaker.clearMoveHistory();

    initZobristHash();
}

std::string Position::getFEN() const {
    std::ostringstream oss;
    for (int row = 0; row < 8; ++row) {
        int emptyCount = 0;
        for (int col = 0; col < 8; ++col) {
            if (board[row][col] == NO_PIECE) {
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

    bool isWhitesTurn = (turn == WHITE);

    oss << ' ' << (isWhitesTurn ? 'w' : 'b') << ' ';
    if (castleState.white & KING_SIDE)
        oss << 'K';
    if (castleState.white & QUEEN_SIDE)
        oss << 'Q';
    if (castleState.black & KING_SIDE)
        oss << 'k';
    if (castleState.black & QUEEN_SIDE)
        oss << 'q';
    if (!(castleState.white | castleState.black))
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
    return getPiece(square) == NO_PIECE;
}

/**
 * @returns the move contex of the given move
 */
MoveContext Position::getMoveContext(const Move &move) {
    MoveContext context;
    context.move = move;
    context.movedPiece = this->getPiece(move.from);
    context.capturedPiece = getCapturedPiece(move);
    context.previousEnPassant = this->enPassantSquare;
    context.previousCastleState = this->castleState;
    context.previousHalfmoveClock = this->halfmoveClock;
    context.previousFullmoveNumber = this->fullmoveNumber;
    context.previousTurn = this->turn;
    context.previousIsGameOver = this->isGameOver;
    context.wasEnPassantCapture = isEnPassant(move);
    context.wasCastling = isCastling(move);
    context.previousHash = this->zobristHash;

    return context;
}

ColoredPiece Position::getCapturedPiece(const Move &move) const {
    ColoredPiece capturedPiece = getPiece(move.to);
    if (isEnPassant(move)) {
        int colorIndex = (getPiece(move.from).color == WHITE) ? 1 : -1;
        Square toSquare = move.to;
        capturedPiece =
            getPiece(Square(toSquare.row + colorIndex, toSquare.col));
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

bool Position::isCheckmated() const {
    return this->scanner.isInCheckmate(this->turn);
}
bool Position::isStalemated() const {
    return this->scanner.isInStalemate(this->turn);
}

bool Position::getIsGameOver() const {
    Color color = getTurn();

    return this->scanner.isInCheckmate(color) ||
           this->scanner.isInStalemate(color);
}

void Position::changeTurn() {
    Color turnToSet = (this->turn == WHITE) ? BLACK : WHITE;
    this->turn = turnToSet;
}

void Position::setTurn(Color color) { this->turn = color; }

void Position::initZobristHash() {
    zobristHash = 0;
    for (int row = 0; row < 8; ++row) {
        for (int col = 0; col < 8; ++col) {
            ColoredPiece cp = board[row][col];
            int index = pieceIndex(cp);
            if (index != -1) {
                int sq = row * 8 + col;
                zobristHash ^= zobrist.pieceKeys[index][sq];
            }
        }
    }

    if (turn == WHITE)
        zobristHash ^= zobrist.sideToMoveKey;

    zobristHash ^= zobrist.castlingRightsKey[getCastlingRightsAsIndex()];

    if (enPassantSquare != INVALID_SQUARE)
        zobristHash ^= zobrist.enPassantFileKey[enPassantSquare.col];
}

void Position::updateZobristHash(const Move &move) {
    int fromSq = move.from.row * 8 + move.from.col;
    int toSq = move.to.row * 8 + move.to.col;

    ColoredPiece moving = board[move.from.row][move.from.col];
    ColoredPiece captured = board[move.to.row][move.to.col];

    int movingIdx = pieceIndex(moving);
    int capturedIdx = pieceIndex(captured);

    // Toggle moving piece from and to
    zobristHash ^= zobrist.pieceKeys[movingIdx][fromSq];
    zobristHash ^= zobrist.pieceKeys[movingIdx][toSq];

    // Toggle captured piece off
    if (capturedIdx != -1)
        zobristHash ^= zobrist.pieceKeys[capturedIdx][toSq];

    // Promotions
    if (move.promotionPiece != NO_PIECE) {
        int promoIdx = pieceIndex(move.promotionPiece);
        zobristHash ^=
            zobrist.pieceKeys[movingIdx][toSq];           // remove pawn at dest
        zobristHash ^= zobrist.pieceKeys[promoIdx][toSq]; // add promoted piece
    }

    // Castling rights and en passant
    // XOR out old castling rights
    zobristHash ^= zobrist.castlingRightsKey[getCastlingRightsAsIndex()];
    // XOR in new castling rights
    zobristHash ^= zobrist.castlingRightsKey[getCastlingRightsAsIndex()];

    if (enPassantSquare != INVALID_SQUARE)
        zobristHash ^= zobrist.enPassantFileKey[enPassantSquare.col];
    if (enPassantSquare != INVALID_SQUARE)
        zobristHash ^= zobrist.enPassantFileKey[enPassantSquare.col];

    // Toggle side to move
    zobristHash ^= zobrist.sideToMoveKey;
}

int Position::getCastlingRightsAsIndex() const {
    int index = 0;
    if (castleState.white & KING_SIDE)
        index |= (1 << 0);
    if (castleState.white & QUEEN_SIDE)
        index |= (1 << 1);
    if (castleState.black & KING_SIDE)
        index |= (1 << 2);
    if (castleState.black & QUEEN_SIDE)
        index |= (1 << 3);
    return index;
}