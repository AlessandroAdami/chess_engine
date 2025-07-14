#pragma once

#include "types.h"
#include <string>
#include <vector>

class Position;

class MoveParser {
  public:
    MoveParser(Position *chessBoard);
    Move moveStringToMove(const std::string &moveStr);

  private:
    Position *chessBoard;
    std::vector<std::pair<Move, std::string>>
    getMoveSANPairs(std::vector<Move> legalMoves) const;
};