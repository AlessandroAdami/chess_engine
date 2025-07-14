#pragma once

#include "types.h"
class Position;

class CheckScanner {
  public:
    CheckScanner(Position *board);
    bool isInCheck(Color color) const;
    bool isInCheckmate(Color color) const;
    bool isInStalemate(Color color) const;
    bool isSquareInCheck(Square square, Color color) const;

  private:
    Position *position;
    bool areThereLegalMoves(Color color) const;
    Square getKingSquare(Color color) const;
};