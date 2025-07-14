#pragma once

#include "check_scanner.h"
#include "move_maker.h"
#include "move_parser.h"
#include "movement_validator.h"
#include "types.h"
#include <string>

// TODO: return move context instead of coloredpiece for makeMove

class Position {
  public:
    Position();
    CheckScanner scanner;
    MovementValidator movementValidator;
    void loadFEN(const std::string &fen);
    std::string getFEN() const;
    void printBoard() const;
    ColoredPiece makeMove(const Move &move);
    ColoredPiece makeMoveFromString(const std::string &moveStr) {
        Move move = moveParser.moveStringToMove(moveStr);
        return makeMove(move);
    }
    ColoredPiece movePiece(const Move &move);
    void unmovePiece(const MoveContext &context);
    void undoMove();
    void redoMove();
    MoveContext getMoveContext(const Move &move);

    ColoredPiece getPiece(Square square) const;
    void setPiece(Square square, ColoredPiece cp);
    Square getEnpassantSquare() const { return enPassantSquare; }
    bool getIsWhitesTurn() const { return isWhitesTurn; }
    int getCastleState(Color color) const {
        return castleState[color == WHITE ? 0 : 1];
    }
    bool isSquareEmpty(const Square &square) const;
    ColoredPiece getCapturedPiece(const Move &move) const;
    bool isEnPassant(const Move &move) const;
    bool isCastling(const Move &move) const;
    bool isCheckmate() const;
    bool isStalemate() const;
    bool getIsGameOver() const { return isGameOver; }

  private:
    ColoredPiece board[8][8];
    Square enPassantSquare;
    bool isWhitesTurn;
    bool isGameOver;
    int halfmoveClock;
    int fullmoveNumber;
    int castleState[2] = {KING_SIDE | QUEEN_SIDE, KING_SIDE | QUEEN_SIDE};
    MoveMaker moveMaker;
    MoveParser moveParser;

    friend class MoveMaker;
};