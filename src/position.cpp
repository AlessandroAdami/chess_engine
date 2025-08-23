#include "position.h"
#include "move_maker.h"
#include "types.h"
#include <iostream>
#include <sstream>
#include <string>

const std::string startFEN =
    "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";

Position::Position()
    : scanner(this), movementValidator(this), moveMaker(this),
      moveParser(this) {
    for (int row = 0; row < 8; ++row)
        for (int col = 0; col < 8; ++col)
            board[row][col] = NO_COLORED_PIECE;
    loadFEN(startFEN);
}

Position::Position(const Position &p)
    : scanner(this), movementValidator(this), moveMaker(this),
      moveParser(this) {

    std::string fen = p.getFEN();
    this->loadFEN(fen);
}

/**
 * Loads the board state from a FEN string.
 * The FEN string should be in the format:
 * <state> <color> <castling rights> <enPassant square> <halfmove> <fullmove>
 */
void Position::loadFEN(const std::string &fen) {
    for (int r = 0; r < 8; r++) {
        for (int c = 0; c < 8; c++) {
            board[r][c] = NO_COLORED_PIECE;
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
        this->setEnPassantSquare(INVALID_SQUARE);
    } else {
        char file = enPassant[0];
        char rank = enPassant[1];
        this->setEnPassantSquare(Square(8 - (rank - '0'), file - 'a'));
    }

    this->halfmoveClock = halfmoveClock;
    this->fullmoveNumber = fullmoveNumber;
    this->moveMaker.clearMoveHistory();

    initZobristHash();
    loadPiecesSquares();
    initInputTensor();
}

std::string Position::getFEN() const {
    std::ostringstream oss;
    for (int row = 0; row < 8; ++row) {
        int emptyCount = 0;
        for (int col = 0; col < 8; ++col) {
            if (board[row][col] == NO_COLORED_PIECE) {
                emptyCount++;
            } else {
                if (emptyCount > 0) {
                    oss << emptyCount;
                    emptyCount = 0;
                }
                oss << board[row][col].toChar();
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

void Position::loadPiecesSquares() {
    piecesSquares.white.clear();
    piecesSquares.black.clear();
    for (int row = 0; row < 8; ++row) {
        for (int col = 0; col < 8; ++col) {
            Square s(row, col);
            ColoredPiece cp = getPiece(s);
            if (cp.color == WHITE) {
                piecesSquares.white.insert(s);
            } else if (cp.color == BLACK) {
                piecesSquares.black.insert(s);
            }
        }
    }
}

void Position::printBoard() const {
    std::cout << "\n   a b c d e f g h\n";
    std::cout << "   ~~~~~~~~~~~~~~~\n";
    for (int row = 0; row < 8; ++row) {
        std::cout << 8 - row << "| ";
        for (int col = 0; col < 8; ++col) {
            std::cout << board[row][col].toChar() << " ";
        }
        std::cout << "|" << 8 - row << "\n";
    }
    std::cout << "   ~~~~~~~~~~~~~~~\n";
    std::cout << "   a b c d e f g h\n";
}

ColoredPiece Position::getPiece(Square square) const {
    return board[square.row][square.col];
}

void Position::setPiece(Square square, ColoredPiece cp) {
    board[square.row][square.col] = cp;
    if (cp.color == NONE) {
        removePieceSquare(square, BLACK);
        removePieceSquare(square, WHITE);
        clearPiecePlanes(this->inputTensor, square.row, square.col);
        return;
    }
    if (cp.color == WHITE) {
        removePieceSquare(square, BLACK);
    } else if (cp.color == BLACK) {
        removePieceSquare(square, WHITE);
    }
    addPieceSquare(square, cp.color);
    setPiecePlane(this->inputTensor, cp, square.row, square.col);
}

bool Position::getIsGameOver() const {
    Color color = getTurn();

    return this->scanner.isInCheckmate(color) ||
           this->scanner.isInStalemate(color);
}

void Position::changeTurn() {
    Color turnToSet = (this->turn == WHITE) ? BLACK : WHITE;
    this->turn = turnToSet;
    if (this->turn == WHITE) {
        fillPlane(this->inputTensor, 12, 1.0f);
    } else {
        fillPlane(this->inputTensor, 12, 0.0f);
    }
}

/**
 * Idempotent. Only called by loadFen(fen) to initialize the Zobrist hash.
 */
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

    zobristHash ^=
        zobrist.castlingRightsKey[getCastlingRightsAsIndex(castleState)];

    if (enPassantSquare != INVALID_SQUARE)
        zobristHash ^= zobrist.enPassantFileKey[enPassantSquare.col];
}

void Position::updateZobristHash(const Move &move, MoveContext context) {
    int fromSq = move.from.row * 8 + move.from.col;
    int toSq = move.to.row * 8 + move.to.col;

    ColoredPiece moving = board[move.from.row][move.from.col];
    ColoredPiece captured = board[move.to.row][move.to.col];

    int movingIdx = pieceIndex(moving);
    int capturedIdx = pieceIndex(captured);

    zobristHash ^= zobrist.pieceKeys[movingIdx][fromSq];
    zobristHash ^= zobrist.pieceKeys[movingIdx][toSq];

    if (capturedIdx != -1)
        zobristHash ^= zobrist.pieceKeys[capturedIdx][toSq];

    if (move.promotionPiece != NO_COLORED_PIECE) {
        int promoIdx = pieceIndex(move.promotionPiece);
        zobristHash ^= zobrist.pieceKeys[movingIdx][toSq];
        zobristHash ^= zobrist.pieceKeys[promoIdx][toSq];
    }

    // XOR out old castling rights
    zobristHash ^= zobrist.castlingRightsKey[getCastlingRightsAsIndex(
        context.previousCastleState)];
    // XOR in new castling rights
    zobristHash ^=
        zobrist.castlingRightsKey[getCastlingRightsAsIndex(castleState)];

    // XOR out old enPassant file
    if (enPassantSquare != INVALID_SQUARE)
        zobristHash ^= zobrist.enPassantFileKey[context.previousEnPassant.col];
    // XOR out new enPassant file
    if (enPassantSquare != INVALID_SQUARE)
        zobristHash ^= zobrist.enPassantFileKey[enPassantSquare.col];

    zobristHash ^= zobrist.sideToMoveKey;
}

void Position::initInputTensor() {
    clearAllPlanes(inputTensor);

    for (int row = 0; row < 8; ++row) {
        for (int col = 0; col < 8; ++col) {
            setPiecePlane(inputTensor, board[row][col], row, col);
        }
    }

    if (turn == WHITE) {
        fillPlane(inputTensor, 12, 1.0f);
    }

    if (castleState.white & KING_SIDE)
        fillPlane(inputTensor, 13, 1.0f);
    if (castleState.white & QUEEN_SIDE)
        fillPlane(inputTensor, 14, 1.0f);
    if (castleState.black & KING_SIDE)
        fillPlane(inputTensor, 15, 1.0f);
    if (castleState.black & QUEEN_SIDE)
        fillPlane(inputTensor, 16, 1.0f);

    if (enPassantSquare.isValid()) {
        int file = enPassantSquare.col;
        if (file >= 0 && file < BOARD_SIZE) {
            for (int r = 0; r < BOARD_SIZE; ++r) {
                inputTensor[tensorIndex(17, r, file)] = 1.0f;
            }
        }
    }
}

int Position::getCastlingRightsAsIndex(CastlingState state) const {
    int index = 0;
    if (state.white & KING_SIDE)
        index |= (1 << 0);
    if (state.white & QUEEN_SIDE)
        index |= (1 << 1);
    if (state.black & KING_SIDE)
        index |= (1 << 2);
    if (state.black & QUEEN_SIDE)
        index |= (1 << 3);
    return index;
}

void Position::addPieceSquare(Square s, Color c) {
    if (c == WHITE) {
        piecesSquares.white.insert(s);
    } else if (c == BLACK) {
        piecesSquares.black.insert(s);
    }
}

void Position::removePieceSquare(Square s, Color c) {
    if (c == WHITE) {
        piecesSquares.white.erase(s);
    } else if (c == BLACK) {
        piecesSquares.black.erase(s);
    }
}

void Position::setEnPassantSquare(Square square) {
    this->enPassantSquare = square;
    fillPlane(this->inputTensor, 17, 0.0f);
    if (this->enPassantSquare.isValid()) {
        int file = this->enPassantSquare.col;
        for (int r = 0; r < BOARD_SIZE; ++r) {
            this->inputTensor[tensorIndex(17, r, file)] = 1.0f;
        }
    }
}

void Position::setCastleState(Color color, int state) {
    if (color == WHITE) {
        this->castleState.white = state;
        if (!(state & KING_SIDE) || !(state & QUEEN_SIDE)) {
            fillPlane(this->inputTensor, 13, 0.0f);
            fillPlane(this->inputTensor, 14, 0.0f);
        }
        if (state & KING_SIDE) {
            fillPlane(this->inputTensor, 13, 1.0f);
        }
        if (state & QUEEN_SIDE) {
            fillPlane(this->inputTensor, 14, 1.0f);
        }
    } else {
        this->castleState.black = state;
        if (!(state & KING_SIDE) || !(state & QUEEN_SIDE)) {
            fillPlane(this->inputTensor, 15, 0.0f);
            fillPlane(this->inputTensor, 16, 0.0f);
        }
        if (state & KING_SIDE) {
            fillPlane(this->inputTensor, 15, 1.0f);
        }
        if (state & QUEEN_SIDE) {
            fillPlane(this->inputTensor, 16, 1.0f);
        }
    }
}

Square Position::getEnPassantSquare() const { return this->enPassantSquare; }

Color Position::getTurn() const { return turn; }

int Position::getCastleState(Color color) const {
    return (color == WHITE) ? castleState.white : castleState.black;
}

std::unordered_set<Square> Position::getPiecesSquares(Color color) const {
    return (color == WHITE) ? piecesSquares.white : piecesSquares.black;
}

void Position::increaseMoveCounts(const ColoredPiece movingCP,
                                  const ColoredPiece capturedCP) {
    if (movingCP.piece == PAWN) {
        this->halfmoveClock = 0;
    } else if (capturedCP != NO_COLORED_PIECE) {
        this->halfmoveClock = 0;
    } else {
        this->halfmoveClock++;
    }

    bool isWhitesTurn = (this->getTurn() == WHITE);
    this->fullmoveNumber += isWhitesTurn ? 0 : 1;
}

std::array<float, 18 * 8 * 8> Position::getInputTensor() const {
    return this->inputTensor;
}