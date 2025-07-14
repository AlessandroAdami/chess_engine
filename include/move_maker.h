#pragma once

#include "types.h"
#include <vector>

class Position;
struct MoveContext {
    Move move;
    ColoredPiece capturedPiece;
    Square previousEnPassant;
    int previousCastleState[2];
    int previousHalfmoveClock;
    int previousFullmoveNumber;
    bool previousIsWhitesTurn;
    bool previousIsGameOver;
    bool wasEnPassantCapture = false;
    bool wasCastling = false;

    bool operator==(const MoveContext &other) const {
        return move == other.move && capturedPiece == other.capturedPiece &&
               previousEnPassant == other.previousEnPassant &&
               previousCastleState[0] == other.previousCastleState[0] &&
               previousCastleState[1] == other.previousCastleState[1] &&
               previousHalfmoveClock == other.previousHalfmoveClock &&
               previousFullmoveNumber == other.previousFullmoveNumber &&
               previousIsWhitesTurn == other.previousIsWhitesTurn &&
               previousIsGameOver == other.previousIsGameOver &&
               wasEnPassantCapture == other.wasEnPassantCapture &&
               wasCastling == other.wasCastling;
    }
};

class MoveMaker {
  public:
    MoveMaker(Position *chessBoard);
    ColoredPiece makeMove(const Move &move);
    ColoredPiece movePiece(const Move &move);
    void unmovePiece(const MoveContext &context);
    MoveContext getMoveContext(const Move &move) const;
    void undoMove();
    void redoMove();
    void clearMoveHistory() {
        moveHistory.clear();
        moveCursor = 0;
    }
    ColoredPiece getCapturedPiece(const Move &move) const;

  private:
    Position *chessBoard;
    ColoredPiece (*board)[8];
    std::vector<MoveContext> moveHistory;
    int moveCursor = 0;
    ColoredPiece movePawn(const Move &move);
    ColoredPiece moveKing(const Move &move);
    ColoredPiece moveRook(const Move &move);
    ColoredPiece promotePawn(const Move &move);
    void increaseHalfmoveClock(const ColoredPiece movingPiece,
                               const ColoredPiece capturedPiece) const;
};