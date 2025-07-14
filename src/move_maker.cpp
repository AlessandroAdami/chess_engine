#include "move_maker.h"
#include "chess_board.h"
#include <iostream>

/**
 * This class simply makes moves on the chess board.
 * It assumes each move is legal.
 * The legality of the moves is checked by the MovementValidator class.
 */

MoveMaker::MoveMaker(ChessBoard *chessBoard)
    : chessBoard(chessBoard), board(chessBoard->board){};

/**
 * @param move the move to be made
 * @cond requires move to be legal
 * @return the piece that was captured, or EMPTY if no piece was captured
 * Modifies the board in ChessBoard and updates the turn.
 */
ColoredPiece MoveMaker::makeMove(const Move &move) {
    if (moveCursor < (int)moveHistory.size()) {
        moveHistory.erase(moveHistory.begin() + moveCursor, moveHistory.end());
    }
    MoveContext context = getMoveContext(move);
    moveHistory.push_back(context);
    moveCursor++;

    ColoredPiece movingPiece = chessBoard->getPiece(move.from);
    ColoredPiece capturedPiece = movePiece(move);

    increaseHalfmoveClock(movingPiece, capturedPiece);
    this->chessBoard->fullmoveNumber += this->chessBoard->isWhitesTurn ? 0 : 1;

    this->chessBoard->isWhitesTurn = !this->chessBoard->isWhitesTurn;

    return capturedPiece;
}

/**
 * Gets the move context for the given move.
 */
MoveContext MoveMaker::getMoveContext(const Move &move) const {
    return this->chessBoard->getMoveContext(move);
}

ColoredPiece MoveMaker::getCapturedPiece(const Move &move) const {
    return chessBoard->getCapturedPiece(move);
}

void MoveMaker::increaseHalfmoveClock(const ColoredPiece movingCP,
                                      const ColoredPiece capturedCP) const {
    if (movingCP.piece == PAWN) {
        this->chessBoard->halfmoveClock = 0;
    } else if (capturedCP != NO_Piece) {
        this->chessBoard->halfmoveClock = 0;
    } else {
        this->chessBoard->halfmoveClock++;
    }
}

ColoredPiece MoveMaker::movePiece(const Move &move) {
    ColoredPiece movingPiece = chessBoard->getPiece(move.from);

    ColoredPiece capturedPiece;

    if (movingPiece.piece != PAWN) {
        chessBoard->enPassantSquare = Square{-1, -1};
    }

    if (movingPiece.piece == PAWN) {
        capturedPiece = movePawn(move);
    } else if (movingPiece.piece == KING) {
        capturedPiece = moveKing(move);
    } else if (movingPiece.piece == ROOK) {
        capturedPiece = moveRook(move);
    } else {
        capturedPiece = chessBoard->getPiece(move.to);
    }

    chessBoard->setPiece(move.to, movingPiece);
    chessBoard->setPiece(move.from, NO_Piece);
    return capturedPiece;
}

ColoredPiece MoveMaker::movePawn(const Move &move) {
    if (move.promotionPiece != NO_Piece) {
        return promotePawn(move);
    }

    Square fromSquare = move.from;
    Square toSquare = move.to;

    int colorIndex = (chessBoard->getPiece(fromSquare).color == WHITE) ? 1 : -1;
    ColoredPiece capturedPiece = chessBoard->getPiece(toSquare);
    ColoredPiece movingPiece = chessBoard->getPiece(fromSquare);

    if (toSquare == this->chessBoard->enPassantSquare) {
        capturedPiece = chessBoard->getPiece(
            Square{toSquare.row + colorIndex, toSquare.col});
        chessBoard->setPiece(Square{toSquare.row + colorIndex, toSquare.col},
                             NO_Piece);
    }

    if (std::abs(fromSquare.row - toSquare.row) == 2) {
        this->chessBoard->enPassantSquare =
            Square{toSquare.row + colorIndex, toSquare.col};
        capturedPiece = NO_Piece;
    } else {
        this->chessBoard->enPassantSquare = Square{-1, -1};
    }

    colorIndex = (chessBoard->getPiece(fromSquare).color == WHITE) ? 0 : 7;
    if (toSquare.row == colorIndex) {
        chessBoard->setPiece(toSquare, move.promotionPiece);
    }

    chessBoard->setPiece(fromSquare, NO_Piece);
    chessBoard->setPiece(toSquare, movingPiece);

    return capturedPiece;
}

ColoredPiece MoveMaker::promotePawn(const Move &move) {
    ColoredPiece promotionPiece = move.promotionPiece;
    ColoredPiece capturedPiece = chessBoard->getPiece(move.to);
    promotionPiece.color = chessBoard->getPiece(move.from).color;
    chessBoard->setPiece(move.to, promotionPiece);
    chessBoard->setPiece(move.from, NO_Piece);

    return capturedPiece;
}

ColoredPiece MoveMaker::moveKing(const Move &move) {
    ColoredPiece king = chessBoard->getPiece(move.from);

    Square fromSquare = move.from;
    Square toSquare = move.to;

    if (std::abs(fromSquare.col - toSquare.col) == 2) {
        ColoredPiece rook;
        if (fromSquare.col < toSquare.col) {
            rook = chessBoard->getPiece(Square{fromSquare.row, 7});
            chessBoard->setPiece(Square{fromSquare.row, 7}, NO_Piece);
            chessBoard->setPiece(Square{fromSquare.row, 5}, rook);
        } else {
            rook = chessBoard->getPiece(Square{fromSquare.row, 0});
            chessBoard->setPiece(Square{fromSquare.row, 0}, NO_Piece);
            chessBoard->setPiece(Square{fromSquare.row, 3}, rook);
        }
    }

    chessBoard->getPiece(toSquare);
    ColoredPiece capturedPiece = chessBoard->getPiece(toSquare);
    ColoredPiece movingPiece = chessBoard->getPiece(fromSquare);
    chessBoard->setPiece(toSquare, movingPiece);
    chessBoard->setPiece(fromSquare, NO_Piece);
    chessBoard->castleState[king.color == WHITE ? 0 : 1] = NO_CASTLING;
    return capturedPiece;
}

ColoredPiece MoveMaker::moveRook(const Move &move) {
    chessBoard->getPiece(move.from);
    ColoredPiece rook = chessBoard->getPiece(move.from);

    int fromCol = move.from.col;

    if (fromCol == 7) {
        chessBoard->castleState[rook.color == WHITE ? 0 : 1] &= ~KING_SIDE;
    } else if (fromCol == 0) {
        chessBoard->castleState[rook.color == WHITE ? 0 : 1] &= ~QUEEN_SIDE;
    }

    return chessBoard->getPiece(move.to);
}

void MoveMaker::undoMove() {
    if (moveCursor == 0)
        return;
    moveCursor--;

    const MoveContext &context = moveHistory[moveCursor];

    unmovePiece(context);
}

void MoveMaker::unmovePiece(const MoveContext &context) {
    const Move &move = context.move;

    ColoredPiece movedPiece = chessBoard->getPiece(move.to);
    chessBoard->setPiece(move.from, movedPiece);
    chessBoard->setPiece(move.to, context.capturedPiece);

    Square from = move.from;
    Square to = move.to;

    if (context.wasEnPassantCapture) {
        int colorIndex = context.previousIsWhitesTurn ? 1 : -1;
        chessBoard->setPiece(Square{to.row + colorIndex, to.col},
                             context.capturedPiece);
        chessBoard->setPiece(to, NO_Piece);
    }

    if (context.wasCastling) {
        if (to.col == 6) {
            ColoredPiece rook = chessBoard->getPiece(Square{to.row, 5});
            chessBoard->setPiece(Square{to.row, 7}, rook);
            chessBoard->setPiece(Square{to.row, 5}, NO_Piece);
        } else if (to.col == 2) {
            ColoredPiece rook = chessBoard->getPiece(Square{to.row, 3});
            chessBoard->setPiece(Square{to.row, 0}, rook);
            chessBoard->setPiece(Square{to.row, 3}, NO_Piece);
        }
    }

    chessBoard->enPassantSquare = context.previousEnPassant;
    chessBoard->castleState[0] = context.previousCastleState[0];
    chessBoard->castleState[1] = context.previousCastleState[1];
    chessBoard->isWhitesTurn = context.previousIsWhitesTurn;
    chessBoard->isGameOver = context.previousIsGameOver;
    chessBoard->halfmoveClock = context.previousHalfmoveClock;
    chessBoard->fullmoveNumber = context.previousFullmoveNumber;
}

void MoveMaker::redoMove() {
    if (moveCursor >= (int)moveHistory.size())
        return;

    const MoveContext &context = moveHistory[moveCursor];
    moveCursor++;

    movePiece(context.move);
    this->chessBoard->isWhitesTurn = !this->chessBoard->isWhitesTurn;
}