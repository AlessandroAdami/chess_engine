#pragma once

#include "check_scanner.h"
#include "move_maker.h"
#include "move_parser.h"
#include "movement_validator.h"
#include "types.h"
#include "zobrist.h"
#include <array>
#include <string>

/**
 * Representing a chess position, with the board state and other uniquely
 * specifying information. Delegates move making, legality checking, king check
 * validation and move parsing to other modules.
 */

// TODO: write script to build executable (GUI)
// TODO: this is a god class, extract position-state class !!

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
    void setEnPassantSquare(Square square);
    void setCastleState(Color color, int state);
    ColoredPiece getCapturedPiece(const Move &move) const;
    bool isEnPassant(const Move &move) const;
    bool isCastling(const Move &move) const;
    bool isCheckmated() const;
    bool isStalemated() const;
    bool getIsGameOver() const;
    void changeTurn();
    Square getEnPassantSquare() const { return enPassantSquare; }
    Color getTurn() const { return turn; }
    int getCastleState(Color color) const {
        return (color == WHITE) ? castleState.white : castleState.black;
    }
    std::unordered_set<Square> getPiecesSquares(Color color) {
        return (color == WHITE) ? piecesSquares.white : piecesSquares.black;
    }

  private:
    /**
     * NN expects floats, so we store them directly instead of ints.
     * The position information is encoded in the tensor as follows:
     * 0-11:  white and black pieces planes
     * 12:    turn
     * 13-16: castling planes
     * 17:    en passant plane
     */
    std::array<float, 18 * 8 * 8> inputTensor;
    ColoredPiece board[8][8];
    Square enPassantSquare;
    Color turn;
    CastlingState castleState;
    PiecesSquares piecesSquares;
    bool isGameOver;
    int halfmoveClock;
    int fullmoveNumber;
    Zobrist zobrist;
    void initZobristHash();
    int getCastlingRightsAsIndex(CastlingState state) const;
    void updateZobristHash(const Move &move, MoveContext context);

    friend class MoveMaker;
};