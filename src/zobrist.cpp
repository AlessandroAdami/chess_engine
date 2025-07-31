#include "zobrist.h"
#include <random>

Zobrist::Zobrist() {
    std::mt19937_64 rng(0xCAFEBABE);
    std::uniform_int_distribution<uint64_t> dist;

    for (int p = 0; p < 12; ++p) {
        for (int sq = 0; sq < 64; ++sq) {
            pieceKeys[p][sq] = dist(rng);
        }
    }

    for (int i = 0; i < 16; ++i) {
        castlingRightsKey[i] = dist(rng);
    }

    for (int i = 0; i < 8; ++i) {
        enPassantFileKey[i] = dist(rng);
    }

    sideToMoveKey = dist(rng);
}