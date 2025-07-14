#pragma once

#include "types.h"
#include <string>
#include <vector>

class Position;

class MoveParser {
  public:
    MoveParser(Position *position);
    Move moveStringToMove(const std::string &moveStr);

  private:
    Position *position;
    std::vector<std::pair<Move, std::string>>
    getMoveSANPairs(std::vector<Move> legalMoves) const;
};