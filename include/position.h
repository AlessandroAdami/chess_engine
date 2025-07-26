#pragma once

#include "check_scanner.h"
#include "move_maker.h"
#include "move_parser.h"
#include "movement_validator.h"
#include "types.h"
#include "zobrist.h"
#include <string>

/**
 * Representing a chess position, with the board state and other uniquely
 * specifying information. Delegates move making, legality checking, king check
 * validation and move parsing to other modules.
 */

// TODO: test zobrist hash more extensively

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
    void loadPiecesSquares();
    void printBoard() const;
    void addPieceSquare(Square s, Color c);
    void removePieceSquare(Square s, Color c);
    MoveContext getMoveContext(const Move &move);
    ColoredPiece getPiece(Square square) const;
    void setPiece(Square square, ColoredPiece cp);
    bool isSquareEmpty(const Square &square) const;
    ColoredPiece getCapturedPiece(const Move &move) const;
    bool isEnPassant(const Move &move) const;
    bool isCastling(const Move &move) const;
    bool isCheckmated() const;
    bool isStalemated() const;
    bool getIsGameOver() const;
    void changeTurn();
    void setTurn(Color color);
    Square getEnpassantSquare() const { return enPassantSquare; }
    Color getTurn() const { return turn; }
    int getCastleState(Color color) const {
        return (color == WHITE) ? castleState.white : castleState.black;
    }
    std::unordered_set<Square> getPiecesSquares(Color color) {
        return (color == WHITE) ? piecesSquares.white : piecesSquares.black;
    }

  private:
    ColoredPiece board[8][8];
    PiecesSquares piecesSquares;
    Square enPassantSquare;
    Color turn;
    bool isGameOver;
    int halfmoveClock;
    int fullmoveNumber;
    CastlingState castleState;
    Zobrist zobrist;
    void initZobristHash();
    int getCastlingRightsAsIndex(CastlingState state) const;
    void updateZobristHash(const Move &move, MoveContext context);

    friend class MoveMaker;
};