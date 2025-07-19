#pragma once

#include "check_scanner.h"
#include "move_maker.h"
#include "move_parser.h"
#include "movement_validator.h"
#include "types.h"
#include <string>

class Position {
  public:
    Position();
    Position(const Position &p);
    CheckScanner scanner;
    MovementValidator movementValidator;
    void loadFEN(const std::string &fen);
    std::string getFEN() const;
    void printBoard() const;
    MoveContext makeMove(const Move &move);
    MoveContext makeMoveFromString(const std::string &moveStr) {
        Move move = moveParser.moveStringToMove(moveStr);
        return makeMove(move);
    }
    MoveContext movePiece(const Move &move);
    void unmovePiece(const MoveContext &context);
    void unmakeMove();
    void remakeMove();
    MoveContext getMoveContext(const Move &move);

    ColoredPiece getPiece(Square square) const;
    void setPiece(Square square, ColoredPiece cp);
    Square getEnpassantSquare() const { return enPassantSquare; }
    Color getTurn() const { return turn; }
    int getCastleState(Color color) const {
        return castleState[color == WHITE ? 0 : 1];
    }
    bool isSquareEmpty(const Square &square) const;
    ColoredPiece getCapturedPiece(const Move &move) const;
    bool isEnPassant(const Move &move) const;
    bool isCastling(const Move &move) const;
    bool isCheckmated() const;
    bool isStalemated() const;
    bool getIsGameOver() const;
    void changeTurn();
    void setTurn(Color color);

  private:
    ColoredPiece board[8][8];
    Square enPassantSquare;
    Color turn;
    bool isGameOver;
    int halfmoveClock;
    int fullmoveNumber;
    int castleState[2] = {KING_SIDE | QUEEN_SIDE, KING_SIDE | QUEEN_SIDE};
    MoveMaker moveMaker;
    MoveParser moveParser;

    friend class MoveMaker;
};