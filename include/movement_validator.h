#pragma once

#include "types.h"
#include <vector>
class Position;

/**
 * Checks legality of moves for given position
 */

class MovementValidator {
  public:
    MovementValidator(Position *position);
    bool isValidMove(const Move &move) const;
    bool isValidPieceMovement(Piece piece, Move move) const;
    std::vector<Move> getLegalMoves(Color color);

  private:
    Position *position;
    bool isValidPawnMovement(Move move) const;
    bool isValidKnightMovement(Move move) const;
    bool isValidBishopMovement(Move move) const;
    bool isValidRookMovement(Move move) const;
    bool isValidQueenMovement(Move move) const;
    bool isValidKingMovement(Move move) const;
    bool moveLeadsIntoCheck(Move move) const;
};