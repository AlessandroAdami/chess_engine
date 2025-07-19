#include "types.h"
#include <iostream>
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