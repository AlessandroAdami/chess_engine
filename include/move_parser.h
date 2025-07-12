#pragma once

#include "types.h"
#include <string>
#include <vector>

class ChessBoard;

class MoveParser {
  public:
    MoveParser(ChessBoard *chessBoard);
    Move moveStringToMove(const std::string &moveStr);

  private:
    ChessBoard *chessBoard;
    std::vector<std::pair<Move, std::string>>
    getMoveSANPairs(std::vector<Move> legalMoves) const;
};