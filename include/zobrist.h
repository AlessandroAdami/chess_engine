#pragma once
#include <cstdint>
#include <random>

struct Zobrist {
    uint64_t pieceKeys[12][64];
    uint64_t sideToMoveKey;
    uint64_t castlingRightsKey[16];
    uint64_t enPassantFileKey[8];

    Zobrist();
};