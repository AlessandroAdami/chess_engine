#include "types.h"
#include <array>
#include <vector>

char pieceToChar(const ColoredPiece &cp) {
    bool isWhite = cp.color == WHITE;
    switch (cp.piece) {
    case PAWN:
        return isWhite ? 'P' : 'p';
    case KNIGHT:
        return isWhite ? 'N' : 'n';
    case BISHOP:
        return isWhite ? 'B' : 'b';
    case ROOK:
        return isWhite ? 'R' : 'r';
    case QUEEN:
        return isWhite ? 'Q' : 'q';
    case KING:
        return isWhite ? 'K' : 'k';
    default:
        return '.';
    }
}

ColoredPiece charToColoredPiece(char c) {
    switch (c) {
    case 'P':
        return ColoredPiece(WHITE, PAWN);
    case 'N':
        return ColoredPiece(WHITE, KNIGHT);
    case 'B':
        return ColoredPiece(WHITE, BISHOP);
    case 'R':
        return ColoredPiece(WHITE, ROOK);
    case 'Q':
        return ColoredPiece(WHITE, QUEEN);
    case 'K':
        return ColoredPiece(WHITE, KING);
    case 'p':
        return ColoredPiece(BLACK, PAWN);
    case 'n':
        return ColoredPiece(BLACK, KNIGHT);
    case 'b':
        return ColoredPiece(BLACK, BISHOP);
    case 'r':
        return ColoredPiece(BLACK, ROOK);
    case 'q':
        return ColoredPiece(BLACK, QUEEN);
    case 'k':
        return ColoredPiece(BLACK, KING);
    default:
        return ColoredPiece(NONE, EMPTY);
    }
}

std::string getMoveString(Move move) {
    char fromCol = 'a' + move.from.col;
    char fromRow = '1' + (7 - move.from.row);
    char toCol = 'a' + move.to.col;
    char toRow = '1' + (7 - move.to.row);
    std::string moveStr;
    moveStr += fromCol;
    moveStr += fromRow;
    moveStr += toCol;
    moveStr += toRow;

    return moveStr;
}

bool vectorContainsMove(std::vector<Move> moves, Move move) {
    for (Move m : moves) {
        if (m == move) {
            return true;
        }
    }
    return false;
}



size_t tensorIndex(int plane, int row, int col) {
    return static_cast<size_t>(plane) * BOARD_SIZE * BOARD_SIZE +
           row * BOARD_SIZE + col;
}

void clearPiecePlanes(
    std::array<float, NUM_PLANES * BOARD_SIZE * BOARD_SIZE> &tensor, int r,
    int c) {
    for (int p = 0; p < 12; ++p) {
        tensor[tensorIndex(p, r, c)] = 0.0f;
    }
}

void setPiecePlane(
    std::array<float, NUM_PLANES * BOARD_SIZE * BOARD_SIZE> &tensor,
    const ColoredPiece &cp, int r, int c) {
    if (cp.piece == EMPTY || cp.color == NONE)
        return;
    int plane_base = (cp.color == WHITE) ? 0 : 6;
    int piece_index = static_cast<int>(cp.piece); // PAWN=1 ... KING=6
    int piece_plane = plane_base + (piece_index - 1);
    if (piece_plane >= 0 && piece_plane < 12) {
        tensor[tensorIndex(piece_plane, r, c)] = 1.0f;
    }
}

void fillPlane(std::array<float, NUM_PLANES * BOARD_SIZE * BOARD_SIZE> &tensor,
                int plane, float v) {
    for (int r = 0; r < BOARD_SIZE; ++r)
        for (int c = 0; c < BOARD_SIZE; ++c)
            tensor[tensorIndex(plane, r, c)] = v;
}
