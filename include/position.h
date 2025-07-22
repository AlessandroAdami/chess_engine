#pragma once

#include "check_scanner.h"
#include "move_maker.h"
#include "move_parser.h"
#include "movement_validator.h"
#include "types.h"
#include "zobrist.h"
#include <string>

/**
 * TODO: add list of pieces squares for efficiency
 * TODO: add docu
 */

class Position {
  public:
    MoveMaker moveMaker;
    CheckScanner scanner;
    MovementValidator movementValidator;
    MoveParser moveParser;
    uint64_t zobristHash;
    Position();
    Position(const Position &p);
    void loadFEN(const std::string &fen);
    std::string getFEN() const;
    void printBoard() const;
    MoveContext getMoveContext(const Move &move);

    ColoredPiece getPiece(Square square) const;
    void setPiece(Square square, ColoredPiece cp);
    Square getEnpassantSquare() const { return enPassantSquare; }
    Color getTurn() const { return turn; }
    int getCastleState(Color color) const {
        return (color == WHITE) ? castleState.white : castleState.black;
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
    CastlingState castleState;
    Zobrist zobrist;
    void initZobristHash();
    int getCastlingRightsAsIndex() const;
    void updateZobristHash(const Move &move);

    friend class MoveMaker;
};