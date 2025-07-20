#pragma once
#include <string>
#include <vector>

enum Piece {
    EMPTY = 0,
    PAWN = 1,
    KNIGHT = 2,
    BISHOP = 3,
    ROOK = 4,
    QUEEN = 5,
    KING = 6
};

enum Color { WHITE = 1, BLACK = -1, NONE = 0 };

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

const ColoredPiece NO_PIECE = ColoredPiece(NONE, EMPTY);

char pieceToChar(const ColoredPiece &cp);
ColoredPiece charToColoredPiece(char c);
inline int pieceIndex(ColoredPiece cp) {
    if (cp == NO_PIECE)
        return -1;
    return (cp.color == WHITE ? 0 : 6) + (int)(cp.piece);
}

enum CastlingRights {
    NO_CASTLING = 0,
    KING_SIDE = 1 << 0,
    QUEEN_SIDE = 1 << 1
};

struct Square {
    int row;
    int col;

    Square() : row(-1), col(-1) {}

    Square(int r, int c) : row(r), col(c) {}

    bool operator==(const Square &other) const {
        return row == other.row && col == other.col;
    }

    bool operator!=(const Square &other) const { return !(*this == other); }
};

const Square INVALID_SQUARE = Square(-1, -1);

struct Move {
    Square from;
    Square to;
    ColoredPiece promotionPiece = NO_PIECE;

    Move() : from(INVALID_SQUARE), to(INVALID_SQUARE) {}

    Move(Square f, Square t) : from(f), to(t) {}

    Move(Square f, Square t, ColoredPiece cp)
        : from(f), to(t), promotionPiece(cp) {}

    bool operator==(const Move &other) const {
        return from == other.from && to == other.to &&
               promotionPiece == other.promotionPiece;
    }

    bool operator!=(const Move &other) const { return !(*this == other); }
};

std::string getMoveString(Move move);
bool vectorContainsMove(std::vector<Move> moves, Move move);