#pragma once

#include "types.h"
#include <vector>

/**
 * Makes moves on the chessboard, handles move history and rewinding.
 */

class Position;
struct MoveContext {
    Move move;
    ColoredPiece movedPiece;
    ColoredPiece capturedPiece;
    Square previousEnPassant;
    CastlingState previousCastleState;
    int previousHalfmoveClock;
    int previousFullmoveNumber;
    Color previousTurn;
    bool previousIsGameOver;
    bool wasEnPassantCapture = false;
    bool wasCastling = false;
    uint64_t previousHash;

    bool operator==(const MoveContext &other) const {
        return move == other.move && movedPiece == other.movedPiece &&
               capturedPiece == other.capturedPiece &&
               previousEnPassant == other.previousEnPassant &&
               previousCastleState == other.previousCastleState &&
               previousHalfmoveClock == other.previousHalfmoveClock &&
               previousFullmoveNumber == other.previousFullmoveNumber &&
               previousTurn == other.previousTurn &&
               previousIsGameOver == other.previousIsGameOver &&
               wasEnPassantCapture == other.wasEnPassantCapture &&
               wasCastling == other.wasCastling &&
               previousHash == other.previousHash;
    }
};

class MoveMaker {
  public:
    MoveMaker(Position *position);
    MoveContext makeMoveFromString(const std::string &moveStr);
    MoveContext makeMove(const Move &move);
    MoveContext makeLegalMove(const Move &move);
    MoveContext movePiece(const Move &move);
    void unmovePiece(const MoveContext &context);
    MoveContext getMoveContext(const Move &move) const;
    void unmakeMove();
    void remakeMove();
    void clearMoveHistory() {
        moveHistory.clear();
        moveCursor = 0;
    }

    ColoredPiece getCapturedPiece(const Move &move) const;

  private:
    Position *position;
    std::vector<MoveContext> moveHistory;
    int moveCursor = 0;
    ColoredPiece movePawn(const Move &move);
    ColoredPiece moveKing(const Move &move);
    ColoredPiece moveRook(const Move &move);
    ColoredPiece promotePawn(const Move &move);
    void increaseHalfmoveClock(const ColoredPiece movingPiece,
                               const ColoredPiece capturedPiece) const;
    void updateCastleAfterRookCapture(const Move &move);
};