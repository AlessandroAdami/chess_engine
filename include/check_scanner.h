#pragma once

#include "types.h"
class ChessBoard;

class CheckScanner {
  public:
    CheckScanner(ChessBoard *board);
    bool isInCheck(Color color) const;
    bool isInCheckmate(Color color) const;
    bool isInStalemate(Color color) const;
    bool isSquareInCheck(Square square, Color color) const;

  private:
    ChessBoard *chessBoard;
    bool areThereLegalMoves(Color color) const;
    Square getKingSquare(Color color) const;
};