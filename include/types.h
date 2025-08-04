#pragma once
#include <string>
#include <unordered_set>
#include <vector>
#include <cstdint>

enum Piece : int8_t {
    EMPTY = 0,
    PAWN = 1,
    KNIGHT = 2,
    BISHOP = 3,
    ROOK = 4,
    QUEEN = 5,
    KING = 6
};

enum Color : int8_t { WHITE = 1, BLACK = -1, NONE = 0 };

struct ColoredPiece {
    Color color;
    Piece piece;

    ColoredPiece(Color c, Piece p) : color(c), piece(p) {}
    ColoredPiece() : color(NONE), piece(EMPTY) {}

    bool operator==(const ColoredPiece &other) const {
        return color == other.color && piece == other.piece;
    }

    bool operator!=(const ColoredPiece &other) const {
        return !(*this == other);
    }
};

const ColoredPiece NO_COLORED_PIECE = ColoredPiece(NONE, EMPTY);

char pieceToChar(const ColoredPiece &cp);
ColoredPiece charToColoredPiece(char c);
inline int pieceIndex(ColoredPiece cp) {
    if (cp == NO_COLORED_PIECE)
        return -1;
    return (cp.color == WHITE ? 0 : 6) + (int)(cp.piece);
}

enum CastlingRights {
    NO_CASTLING = 0,
    KING_SIDE = 1 << 0,
    QUEEN_SIDE = 1 << 1
};

struct CastlingState {
    int white;
    int black;
    CastlingState(int w, int b) : white(w), black(b) {}
    CastlingState()
        : white(KING_SIDE | QUEEN_SIDE), black(KING_SIDE | QUEEN_SIDE) {}
    bool operator==(const CastlingState &other) const {
        return white == other.white && black == other.black;
    }
    bool operator!=(const CastlingState &other) const {
        return !(*this == other);
    }
};

struct Square {
    int8_t row;
    int8_t col;

    Square() : row(-1), col(-1) {}

    Square(int8_t r, int8_t c) : row(r), col(c) {}

    bool operator==(const Square &other) const {
        return row == other.row && col == other.col;
    }

    bool operator!=(const Square &other) const { return !(*this == other); }
    bool isValid() { return (row < 8 && row > -1 && col < 8 && col > -1); }
};

namespace std {
template <> struct hash<Square> {
    std::size_t operator()(const Square &s) const noexcept {
        int r = s.row + 8;
        int c = s.col + 8;
        return std::hash<int>()(r) ^ (std::hash<int>()(c) << 1);
    }
};
} // namespace std

const Square INVALID_SQUARE = Square(-1, -1);

struct Move {
    Square from;
    Square to;
    ColoredPiece promotionPiece = NO_COLORED_PIECE;

    Move() : from(INVALID_SQUARE), to(INVALID_SQUARE) {}

    Move(Square f, Square t) : from(f), to(t) {}

    Move(Square f, Square t, ColoredPiece cp)
        : from(f), to(t), promotionPiece(cp) {}

    bool operator==(const Move &other) const {
        return from == other.from && to == other.to &&
               promotionPiece == other.promotionPiece;
    }

    bool operator!=(const Move &other) const { return !(*this == other); }

    std::string toUCI() {
        std::string uci;

        uci += static_cast<char>('a' + from.col);
        uci += static_cast<char>('1' + (7 - from.row));

        uci += static_cast<char>('a' + to.col);
        uci += static_cast<char>('1' + (7 - to.row));

        if (promotionPiece != NO_COLORED_PIECE) {
            switch (promotionPiece.piece) {
            case QUEEN:
                uci += 'q';
                break;
            case ROOK:
                uci += 'r';
                break;
            case BISHOP:
                uci += 'b';
                break;
            case KNIGHT:
                uci += 'n';
                break;
            default:
                break;
            }
        }

        return uci;
    }
};

std::string getMoveString(Move move);
bool vectorContainsMove(std::vector<Move> moves, Move move);

struct PiecesSquares {
    std::unordered_set<Square> white;
    std::unordered_set<Square> black;

    PiecesSquares() : white(), black() {}
};