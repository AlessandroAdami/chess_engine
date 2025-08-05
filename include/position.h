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
 * Representing a chess position
 * Delegates the state of the game, move making, legality checking, king check
 * validation and move parsing to other modules.
 */

// TODO: test inputTensor changes

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
    ColoredPiece getPiece(Square square) const;
    void setPiece(Square square, ColoredPiece cp);
    void setEnPassantSquare(Square square);
    void setCastleState(Color color, int state);
    bool getIsGameOver() const;
    void changeTurn();
    std::array<float, 18 * 8 * 8> getInputTensor() const;
    Square getEnPassantSquare() const;
    Color getTurn() const;
    int getCastleState(Color color) const;
    std::unordered_set<Square> getPiecesSquares(Color color) const;
    void increaseMoveCounts(const ColoredPiece movingCP,
                            const ColoredPiece capturedCP);

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
    int halfmoveClock;
    int fullmoveNumber;
    Zobrist zobrist;
    void initZobristHash();
    void initInputTensor();
    int getCastlingRightsAsIndex(CastlingState state) const;
    void updateZobristHash(const Move &move, MoveContext context);

    friend class MoveMaker; // TODO:remove
};