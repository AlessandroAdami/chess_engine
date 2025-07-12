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
    MoveContext context = getMoveContext(move);
    moveHistory.push_back(context);
    moveCursor++;

    ColoredPiece movingPiece = board[move.fromRow][move.fromCol];
    ColoredPiece capturedPiece = movePiece(move);

    increaseHalfmoveClock(movingPiece, capturedPiece);
    this->chessBoard->fullmoveNumber += this->chessBoard->isWhitesTurn ? 0 : 1;

    this->chessBoard->isWhitesTurn = !this->chessBoard->isWhitesTurn;

    return capturedPiece;
}

MoveContext MoveMaker::getMoveContext(const Move &move) {
    if (moveCursor < (int)moveHistory.size()) {
        moveHistory.erase(moveHistory.begin() + moveCursor, moveHistory.end());
    }
    MoveContext context;
    context.move = move;
    context.capturedPiece = getCapturedPiece(move);
    context.previousEnPassant = chessBoard->enPassantSquare;
    context.previousCastleState[0] = chessBoard->castleState[0];
    context.previousCastleState[1] = chessBoard->castleState[1];
    context.previousHalfmoveClock = chessBoard->halfmoveClock;
    context.previousFullmoveNumber = chessBoard->fullmoveNumber;
    context.previousIsWhitesTurn = chessBoard->isWhitesTurn;
    context.previousIsGameOver = chessBoard->isGameOver;
    context.wasEnPassantCapture = isEnPassant(move);
    context.wasCastling = isCastling(move);

    return context;
}

ColoredPiece MoveMaker::getCapturedPiece(const Move &move) const {
    ColoredPiece capturedPiece = board[move.toRow][move.toCol];
    if (isEnPassant(move)) {
        int colorIndex =
            (board[move.fromRow][move.fromCol].color == WHITE) ? 1 : -1;
        capturedPiece = board[move.toRow + colorIndex][move.toCol];
    }
    return capturedPiece;
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

bool MoveMaker::isEnPassant(const Move &move) const {
    ColoredPiece movingPiece = board[move.fromRow][move.fromCol];
    if (movingPiece.piece != PAWN)
        return false;
    return Square{move.toRow, move.toCol} == this->chessBoard->enPassantSquare;
}

bool MoveMaker::isCastling(const Move &move) const {
    ColoredPiece movingPiece = board[move.fromRow][move.fromCol];
    if (movingPiece.piece != KING)
        return false;
    return std::abs(move.fromCol - move.toCol) == 2;
}

ColoredPiece MoveMaker::movePiece(const Move &move) {
    int fromCol = move.fromCol, fromRow = move.fromRow;
    int toCol = move.toCol, toRow = move.toRow;

    ColoredPiece movingPiece = board[fromRow][fromCol];

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
        capturedPiece = board[toRow][toCol];
    }

    board[toRow][toCol] = movingPiece;
    board[fromRow][fromCol] = NO_Piece;
    return capturedPiece;
}

ColoredPiece MoveMaker::movePawn(const Move &move) {
    if (move.promotionPiece != NO_Piece) {
        return promotePawn(move);
    }

    int fromRow = move.fromRow, fromCol = move.fromCol;
    int toRow = move.toRow, toCol = move.toCol;

    int colorIndex = (board[fromRow][fromCol].color == WHITE) ? 1 : -1;
    ColoredPiece capturedPiece = board[toRow][toCol];
    ColoredPiece movingPiece = board[fromRow][fromCol];

    if (Square{toRow, toCol} == this->chessBoard->enPassantSquare) {
        capturedPiece = board[toRow + colorIndex][toCol];
        board[toRow + colorIndex][toCol] = NO_Piece;
    }

    if (std::abs(fromRow - toRow) == 2) {
        this->chessBoard->enPassantSquare = Square{toRow + colorIndex, toCol};
        capturedPiece = NO_Piece;
    } else {
        this->chessBoard->enPassantSquare = Square{-1, -1};
    }

    colorIndex = (board[fromRow][fromCol].color == WHITE) ? 0 : 7;
    if (toRow == colorIndex) {
        board[toRow][toCol] = move.promotionPiece;
    }

    board[fromRow][fromCol] = NO_Piece;
    board[toRow][toCol] = movingPiece;

    return capturedPiece;
}

ColoredPiece MoveMaker::promotePawn(const Move &move) {
    ColoredPiece promotionPiece = move.promotionPiece;
    ColoredPiece capturedPiece = board[move.toRow][move.toCol];
    promotionPiece.color = board[move.fromRow][move.fromCol].color;
    int toRow = move.toRow, toCol = move.toCol;
    board[toRow][toCol] = promotionPiece;
    board[move.fromRow][move.fromCol] = NO_Piece;

    return capturedPiece;
}

ColoredPiece MoveMaker::moveKing(const Move &move) {
    int fromCol = move.fromCol, fromRow = move.fromRow;
    int toCol = move.toCol, toRow = move.toRow;
    ColoredPiece king = board[fromRow][fromCol];

    if (std::abs(fromCol - toCol) == 2) {
        ColoredPiece rook;
        if (fromCol < toCol) {
            rook = board[fromRow][7];
            board[fromRow][7] = NO_Piece;
            board[fromRow][5] = rook;
        } else {
            rook = board[fromRow][0];
            board[fromRow][0] = NO_Piece;
            board[fromRow][3] = rook;
        }
    }

    ColoredPiece capturedPiece = board[toRow][toCol];
    board[toRow][toCol] = board[fromRow][fromCol];
    board[fromRow][fromCol] = NO_Piece;
    chessBoard->castleState[king.color == WHITE ? 0 : 1] = NO_CASTLING;
    return capturedPiece;
}

ColoredPiece MoveMaker::moveRook(const Move &move) {
    int fromCol = move.fromCol, fromRow = move.fromRow;
    int toCol = move.toCol, toRow = move.toRow;
    ColoredPiece rook = board[fromRow][fromCol];

    if (fromCol == 7) {
        chessBoard->castleState[rook.color == WHITE ? 0 : 1] &= ~KING_SIDE;
    } else if (fromCol == 0) {
        chessBoard->castleState[rook.color == WHITE ? 0 : 1] &= ~QUEEN_SIDE;
    }

    return board[toRow][toCol];
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

    board[move.fromRow][move.fromCol] = board[move.toRow][move.toCol];
    board[move.toRow][move.toCol] = context.capturedPiece;

    if (context.wasEnPassantCapture) {
        int colorIndex = context.previousIsWhitesTurn ? 1 : -1;
        board[move.toRow + colorIndex][move.toCol] = context.capturedPiece;
        board[move.toRow][move.toCol] = NO_Piece;
    }

    if (context.wasCastling) {
        if (move.toCol == 6) {
            board[move.toRow][7] = board[move.toRow][5];
            board[move.toRow][5] = NO_Piece;
        } else if (move.toCol == 2) {
            board[move.toRow][0] = board[move.toRow][3];
            board[move.toRow][3] = NO_Piece;
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