#include "../include/position.h"
#include "../include/types.h"
#include <gtest/gtest.h>

TEST(ChessBoardTest, LoadFenFromStartingPosition) {
    Position board;
    board.loadFEN("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1");

    for (Color color : {WHITE, BLACK}) {
        int row = (color == WHITE) ? 7 : 0;
        EXPECT_EQ(board.getPiece(Square{row, 0}), ColoredPiece(color, ROOK));
        EXPECT_EQ(board.getPiece(Square{row, 1}), ColoredPiece(color, KNIGHT));
        EXPECT_EQ(board.getPiece(Square{row, 2}), ColoredPiece(color, BISHOP));
        EXPECT_EQ(board.getPiece(Square{row, 3}), ColoredPiece(color, QUEEN));
        EXPECT_EQ(board.getPiece(Square{row, 4}), ColoredPiece(color, KING));
        EXPECT_EQ(board.getPiece(Square{row, 5}), ColoredPiece(color, BISHOP));
        EXPECT_EQ(board.getPiece(Square{row, 6}), ColoredPiece(color, KNIGHT));
        EXPECT_EQ(board.getPiece(Square{row, 7}), ColoredPiece(color, ROOK));
    }

    for (int i = 0; i < 8; i++) {
        EXPECT_EQ(board.getPiece(Square{6, i}), ColoredPiece(WHITE, PAWN));
        EXPECT_EQ(board.getPiece(Square{1, i}), ColoredPiece(BLACK, PAWN));

        for (int j = 2; j < 6; j++) {
            EXPECT_EQ(board.getPiece(Square{j, i}), NO_Piece);
        }
    }

    EXPECT_EQ(board.getEnpassantSquare(), (Square{-1, -1}));
    EXPECT_TRUE(board.getIsWhitesTurn());
    EXPECT_EQ(board.getCastleState(WHITE), KING_SIDE | QUEEN_SIDE);
    EXPECT_EQ(board.getCastleState(BLACK), KING_SIDE | QUEEN_SIDE);
}

TEST(ChessBoardTest, LoadFenFromRandomPosition) {
    Position board;
    board.loadFEN(
        "r4rk1/1pp2pp1/2np1q1p/p1b1p3/2P3b1/2NPPNP1/PP3PBP/R2Q1RK1 w - - 0 11");

    EXPECT_EQ(board.getPiece(Square{0, 0}), ColoredPiece(BLACK, ROOK));
    EXPECT_EQ(board.getPiece(Square{0, 5}), ColoredPiece(BLACK, ROOK));
    EXPECT_EQ(board.getPiece(Square{0, 6}), ColoredPiece(BLACK, KING));
    EXPECT_EQ(board.getPiece(Square{2, 2}), ColoredPiece(BLACK, KNIGHT));
    EXPECT_EQ(board.getPiece(Square{2, 5}), ColoredPiece(BLACK, QUEEN));
    EXPECT_EQ(board.getPiece(Square{3, 4}), ColoredPiece(BLACK, PAWN));
    EXPECT_EQ(board.getPiece(Square{4, 2}), ColoredPiece(WHITE, PAWN));
    EXPECT_EQ(board.getPiece(Square{5, 2}), ColoredPiece(WHITE, KNIGHT));
    EXPECT_EQ(board.getPiece(Square{7, 3}), ColoredPiece(WHITE, QUEEN));
    EXPECT_EQ(board.getPiece(Square{6, 6}), ColoredPiece(WHITE, BISHOP));
    EXPECT_EQ(board.getPiece(Square{7, 6}), ColoredPiece(WHITE, KING));
    EXPECT_EQ(board.getPiece(Square{7, 5}), ColoredPiece(WHITE, ROOK));
}

TEST(ChessBoardTest, MakeMoveIllegal) {
    Position board;
    std::string fen =
        "r1bqkbnr/pp1ppppp/2n5/2p5/4P3/5N2/PPPP1PPP/RNBQKB1R w KQkq - 2 3";
    board.loadFEN(fen);

    Move illegalRandomMove{0, 0, 7, 7};
    board.makeMove(illegalRandomMove);

    EXPECT_EQ(board.getFEN(), fen);

    fen = "r1bqk1nr/pp1p1ppp/2n1p3/8/1bBNP3/8/PPP2PPP/RNBQK2R w KQkq - 2 6";
    board.loadFEN(fen);

    Move illegalCastlingMove{7, 4, 7, 6};
    board.makeMove(illegalCastlingMove);

    EXPECT_EQ(board.getFEN(), fen);
}

TEST(ChessBoardTest, MakeMoveLegal) {
    Position board;
    std::string fen =
        "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";
    board.loadFEN(fen);

    Move e4{6, 4, 4, 4};
    board.makeMove(e4);

    fen = "rnbqkbnr/pppppppp/8/8/4P3/8/PPPP1PPP/RNBQKBNR b KQkq e3 0 1";

    EXPECT_EQ(board.getFEN(), fen);

    Move d5{1, 3, 3, 3};
    board.makeMove(d5);

    fen = "rnbqkbnr/ppp1pppp/8/3p4/4P3/8/PPPP1PPP/RNBQKBNR w KQkq d6 0 2";

    EXPECT_EQ(board.getFEN(), fen);

    fen = "rnbqkbnr/ppp1p1pp/8/3pPp2/8/8/PPPP1PPP/RNBQKBNR w KQkq f6 0 3";
    board.loadFEN(fen);

    Move exf6EnPassant{3, 4, 2, 5};
    board.makeMove(exf6EnPassant);

    fen = "rnbqkbnr/ppp1p1pp/5P2/3p4/8/8/PPPP1PPP/RNBQKBNR b KQkq - 0 3";

    EXPECT_EQ(board.getFEN(), fen);

    fen = "rnbqkb1r/ppp2ppp/3p1n2/4p2Q/2B1P3/8/PPPP1PPP/RNB1K1NR w KQkq - 0 4";
    board.loadFEN(fen);

    Move qxf7{3, 7, 1, 5};
    board.makeMove(qxf7);

    fen = "rnbqkb1r/ppp2Qpp/3p1n2/4p3/2B1P3/8/PPPP1PPP/RNB1K1NR b KQkq - 0 4";

    EXPECT_EQ(board.getFEN(), fen);
}

TEST(ChessBoardTest, UndoRedoMove) {
    Position board;
    std::string fen =
        "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";

    Move e4{6, 4, 4, 4};
    board.makeMove(e4);

    fen = "rnbqkbnr/pppppppp/8/8/4P3/8/PPPP1PPP/RNBQKBNR b KQkq e3 0 1";

    EXPECT_EQ(board.getFEN(), fen);

    board.undoMove();

    fen = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";
    EXPECT_EQ(board.getFEN(), fen);

    board.redoMove();

    fen = "rnbqkbnr/pppppppp/8/8/4P3/8/PPPP1PPP/RNBQKBNR b KQkq e3 0 1";

    EXPECT_EQ(board.getFEN(), fen);
}

TEST(ChessBoardTest, GetMoveContext) {
    Position board;
    std::string fen =
        "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";

    Move e4{6, 4, 4, 4};
    MoveContext actualContext = board.getMoveContext(e4);

    MoveContext expectedContext = {
        e4,
        NO_Piece,
        Square{-1, -1},
        {KING_SIDE | QUEEN_SIDE, KING_SIDE | QUEEN_SIDE},
        0,
        1,
        true,
        false,
        false};

    EXPECT_EQ(actualContext, expectedContext);

    fen = "r1bqkbnr/pp1p1ppp/2n1p3/2p5/2B1P3/5N2/PPPP1PPP/RNBQK2R w KQkq - 2 4";

    board.loadFEN(fen);

    Move shortCastle{7, 4, 7, 6};
    actualContext = board.getMoveContext(shortCastle);

    expectedContext = {shortCastle,
                       NO_Piece,
                       Square{-1, -1},
                       {KING_SIDE | QUEEN_SIDE, KING_SIDE | QUEEN_SIDE},
                       2,
                       4,
                       true,
                       false,
                       false,
                       true};

    EXPECT_EQ(actualContext, expectedContext);

    fen = "rnbqkbnr/ppp1p1pp/8/3pPp2/8/8/PPPP1PPP/RNBQKBNR w KQkq f6 0 3";

    board.loadFEN(fen);

    Move exf6EnPassant{3, 4, 2, 5};
    actualContext = board.getMoveContext(exf6EnPassant);

    expectedContext = {exf6EnPassant,
                       ColoredPiece(BLACK, PAWN),
                       Square{2, 5},
                       {KING_SIDE | QUEEN_SIDE, KING_SIDE | QUEEN_SIDE},
                       0,
                       3,
                       true,
                       false,
                       true,
                       false};

    EXPECT_EQ(actualContext, expectedContext);
}