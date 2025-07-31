#pragma once

#include "types.h"
#include <string>
#include <vector>

/**
 * Parses move strings (e.g. "e4", "Nf3", "O-O", "exf8=n") into move object for
 * given position
 */

class Position;

class MoveParser {
  public:
    MoveParser(Position *position);
    Move moveStringToMove(const std::string &moveStr);
    Move uciToMove(const std::string &moveStr);
    std::string moveToString(const Move move) const;
    void loadLegalMoves() const;

  private:
    Position *position;
    std::vector<std::pair<Move, std::string>>
    getMoveSANPairs(std::vector<Move> legalMoves) const;
};