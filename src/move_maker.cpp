#include "move_maker.h"
#include "position.h"
#include <iostream>

MoveMaker::MoveMaker(Position *position) : position(position){};

MoveContext MoveMaker::makeMoveFromString(const std::string &moveStr) {
    Move move = position->moveParser.moveStringToMove(moveStr);
    return makeMove(move);
}

MoveContext MoveMaker::makeMove(const Move &move) {
    if (!position->movementValidator.isValidMove(move)) {
        std::cerr << "Illegal move.\n";
        return MoveContext();
    }
    return makeLegalMove(move);
}

/**
 * @param move the move to be made
 * @cond requires move to be legal
 * @return the context of the move BEFORE it is played
 * Modifies the board in ChessBoard and updates the turn.
 */
MoveContext MoveMaker::makeLegalMove(const Move &move) {
    if (moveCursor < (int)moveHistory.size()) {
        moveHistory.erase(moveHistory.begin() + moveCursor, moveHistory.end());
    }
    MoveContext context = getMoveContext(move);
    moveHistory.push_back(context);
    moveCursor++;

    ColoredPiece movingPiece = position->getPiece(move.from);
    ColoredPiece capturedPiece = context.capturedPiece;
    movePiece(move);

    increaseHalfmoveClock(movingPiece, capturedPiece);
    bool isWhitesTurn = (this->position->getTurn() == WHITE);
    this->position->fullmoveNumber += isWhitesTurn ? 0 : 1;

    this->position->changeTurn();

    position->updateZobristHash(move, context);

    if (position->isCheckmated() || position->isStalemated()) {
        position->isGameOver = true;
    }

    return context;
}

MoveContext MoveMaker::getMoveContext(const Move &move) const {
    return this->position->getMoveContext(move);
}

ColoredPiece MoveMaker::getCapturedPiece(const Move &move) const {
    return position->getCapturedPiece(move);
}

void MoveMaker::increaseHalfmoveClock(const ColoredPiece movingCP,
                                      const ColoredPiece capturedCP) const {
    if (movingCP.piece == PAWN) {
        this->position->halfmoveClock = 0;
    } else if (capturedCP != NO_PIECE) {
        this->position->halfmoveClock = 0;
    } else {
        this->position->halfmoveClock++;
    }
}

MoveContext MoveMaker::movePiece(const Move &move) {
    updateCastleAfterRookCapture(move);
    MoveContext context = getMoveContext(move);
    ColoredPiece movingPiece = position->getPiece(move.from);

    ColoredPiece capturedPiece;

    if (movingPiece.piece != PAWN) {
        position->enPassantSquare = INVALID_SQUARE;
    }

    if (movingPiece.piece == PAWN) {
        capturedPiece = movePawn(move);
        return context;
    } else if (movingPiece.piece == KING) {
        capturedPiece = moveKing(move);
    } else if (movingPiece.piece == ROOK) {
        capturedPiece = moveRook(move);
    } else {
        capturedPiece = position->getPiece(move.to);
    }

    position->setPiece(move.to, movingPiece);
    position->setPiece(move.from, NO_PIECE);

    return context;
}

void MoveMaker::updateCastleAfterRookCapture(const Move &move) {
    Square to = move.to;
    if (to.row == 0) {
        if (to.col == 0) {
            position->castleState.black &= ~QUEEN_SIDE;
        }
        if (to.col == 7) {
            position->castleState.black &= ~KING_SIDE;
        }
    }
    if (to.row == 7) {
        if (to.col == 0) {
            position->castleState.white &= ~QUEEN_SIDE;
        }
        if (to.col == 7) {
            position->castleState.white &= ~KING_SIDE;
        }
    }
}

ColoredPiece MoveMaker::movePawn(const Move &move) {
    if (move.promotionPiece != NO_PIECE) {
        return promotePawn(move);
    }

    Square fromSquare = move.from;
    Square toSquare = move.to;

    int colorIndex = (position->getPiece(fromSquare).color == WHITE) ? 1 : -1;
    ColoredPiece capturedPiece = position->getPiece(toSquare);
    ColoredPiece movingPiece = position->getPiece(fromSquare);

    if (toSquare == this->position->enPassantSquare) {
        capturedPiece =
            position->getPiece(Square(toSquare.row + colorIndex, toSquare.col));
        position->setPiece(Square(toSquare.row + colorIndex, toSquare.col),
                           NO_PIECE);
    }

    if (std::abs(fromSquare.row - toSquare.row) == 2) {
        this->position->enPassantSquare =
            Square(toSquare.row + colorIndex, toSquare.col);
        capturedPiece = NO_PIECE;
    } else {
        this->position->enPassantSquare = INVALID_SQUARE;
    }

    colorIndex = (position->getPiece(fromSquare).color == WHITE) ? 0 : 7;
    if (toSquare.row == colorIndex) {
        position->setPiece(toSquare, move.promotionPiece);
    }

    position->setPiece(fromSquare, NO_PIECE);
    position->setPiece(toSquare, movingPiece);

    return capturedPiece;
}

ColoredPiece MoveMaker::promotePawn(const Move &move) {
    ColoredPiece promotionPiece = move.promotionPiece;
    ColoredPiece capturedPiece = position->getPiece(move.to);
    promotionPiece.color = position->getPiece(move.from).color;
    position->setPiece(move.to, promotionPiece);
    position->setPiece(move.from, NO_PIECE);

    return capturedPiece;
}

ColoredPiece MoveMaker::moveKing(const Move &move) {
    ColoredPiece king = position->getPiece(move.from);

    Square fromSquare = move.from;
    Square toSquare = move.to;

    if (std::abs(fromSquare.col - toSquare.col) == 2) {
        ColoredPiece rook;
        if (fromSquare.col < toSquare.col) {
            rook = position->getPiece(Square(fromSquare.row, 7));
            position->setPiece(Square(fromSquare.row, 7), NO_PIECE);
            position->setPiece(Square(fromSquare.row, 5), rook);
        } else {
            rook = position->getPiece(Square(fromSquare.row, 0));
            position->setPiece(Square(fromSquare.row, 0), NO_PIECE);
            position->setPiece(Square(fromSquare.row, 3), rook);
        }
    }

    position->getPiece(toSquare);
    ColoredPiece capturedPiece = position->getPiece(toSquare);
    ColoredPiece movingPiece = position->getPiece(fromSquare);
    position->setPiece(toSquare, movingPiece);
    position->setPiece(fromSquare, NO_PIECE);
    if (king.color == WHITE) {
        position->castleState.white = NO_CASTLING;
    } else {
        position->castleState.black = NO_CASTLING;
    }
    return capturedPiece;
}

ColoredPiece MoveMaker::moveRook(const Move &move) {
    position->getPiece(move.from);
    ColoredPiece rook = position->getPiece(move.from);

    int fromCol = move.from.col;

    if (fromCol == 7) {
        if (rook.color == WHITE) {
            position->castleState.white &= ~KING_SIDE;
        } else {
            position->castleState.black &= ~KING_SIDE;
        }

    } else if (fromCol == 0) {
        if (rook.color == WHITE) {
            position->castleState.white &= ~QUEEN_SIDE;
        } else {
            position->castleState.black &= ~QUEEN_SIDE;
        }
    }

    return position->getPiece(move.to);
}

void MoveMaker::unmakeMove() {
    if (moveCursor == 0)
        return;
    moveCursor--;

    const MoveContext &context = moveHistory[moveCursor];

    unmovePiece(context);
}

void MoveMaker::unmovePiece(const MoveContext &context) {
    const Move &move = context.move;

    ColoredPiece movedPiece = context.movedPiece;
    position->setPiece(move.from, movedPiece);
    position->setPiece(move.to, context.capturedPiece);
    position->zobristHash = context.previousHash;

    Square from = move.from;
    Square to = move.to;

    if (context.wasEnPassantCapture) {
        bool previousIsWhitesTurn = context.previousTurn == WHITE;
        int colorIndex = previousIsWhitesTurn ? 1 : -1;
        position->setPiece(Square(to.row + colorIndex, to.col),
                           context.capturedPiece);
        position->setPiece(to, NO_PIECE);
    }

    if (context.wasCastling) {
        if (to.col == 6) {
            ColoredPiece rook = position->getPiece(Square(to.row, 5));
            position->setPiece(Square(to.row, 7), rook);
            position->setPiece(Square(to.row, 5), NO_PIECE);
        } else if (to.col == 2) {
            ColoredPiece rook = position->getPiece(Square(to.row, 3));
            position->setPiece(Square(to.row, 0), rook);
            position->setPiece(Square(to.row, 3), NO_PIECE);
        }
    }

    position->enPassantSquare = context.previousEnPassant;
    position->castleState = context.previousCastleState;
    position->turn = context.previousTurn;
    position->isGameOver = context.previousIsGameOver;
    position->halfmoveClock = context.previousHalfmoveClock;
    position->fullmoveNumber = context.previousFullmoveNumber;
}

void MoveMaker::remakeMove() {
    if (moveCursor >= (int)moveHistory.size())
        return;

    const MoveContext &context = moveHistory[moveCursor];
    moveCursor++;

    movePiece(context.move);
    this->position->changeTurn();
}