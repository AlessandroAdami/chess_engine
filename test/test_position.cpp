#include "../include/position.h"
#include "../include/types.h"
#include <gtest/gtest.h>

TEST(ChessBoardTest, LoadFenFromStartingPosition) {
    Position position;
    position.loadFEN(
        "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1");

    for (Color color : {WHITE, BLACK}) {
        int row = (color == WHITE) ? 7 : 0;
        EXPECT_EQ(position.getPiece(Square{row, 0}), ColoredPiece(color, ROOK));
        EXPECT_EQ(position.getPiece(Square{row, 1}),
                  ColoredPiece(color, KNIGHT));
        EXPECT_EQ(position.getPiece(Square{row, 2}),
                  ColoredPiece(color, BISHOP));
        EXPECT_EQ(position.getPiece(Square{row, 3}),
                  ColoredPiece(color, QUEEN));
        EXPECT_EQ(position.getPiece(Square{row, 4}), ColoredPiece(color, KING));
        EXPECT_EQ(position.getPiece(Square{row, 5}),
                  ColoredPiece(color, BISHOP));
        EXPECT_EQ(position.getPiece(Square{row, 6}),
                  ColoredPiece(color, KNIGHT));
        EXPECT_EQ(position.getPiece(Square{row, 7}), ColoredPiece(color, ROOK));
    }

    for (int i = 0; i < 8; i++) {
        EXPECT_EQ(position.getPiece(Square{6, i}), ColoredPiece(WHITE, PAWN));
        EXPECT_EQ(position.getPiece(Square{1, i}), ColoredPiece(BLACK, PAWN));

        for (int j = 2; j < 6; j++) {
            EXPECT_EQ(position.getPiece(Square{j, i}), NO_Piece);
        }
    }

    EXPECT_EQ(position.getEnpassantSquare(), (Square{-1, -1}));
    EXPECT_EQ(position.getTurn(), WHITE);
    EXPECT_EQ(position.getCastleState(WHITE), KING_SIDE | QUEEN_SIDE);
    EXPECT_EQ(position.getCastleState(BLACK), KING_SIDE | QUEEN_SIDE);
}

TEST(ChessBoardTest, LoadFenFromRandomPosition) {
    Position position;
    position.loadFEN(
        "r4rk1/1pp2pp1/2np1q1p/p1b1p3/2P3b1/2NPPNP1/PP3PBP/R2Q1RK1 w - - 0 11");

    EXPECT_EQ(position.getPiece(Square{0, 0}), ColoredPiece(BLACK, ROOK));
    EXPECT_EQ(position.getPiece(Square{0, 5}), ColoredPiece(BLACK, ROOK));
    EXPECT_EQ(position.getPiece(Square{0, 6}), ColoredPiece(BLACK, KING));
    EXPECT_EQ(position.getPiece(Square{2, 2}), ColoredPiece(BLACK, KNIGHT));
    EXPECT_EQ(position.getPiece(Square{2, 5}), ColoredPiece(BLACK, QUEEN));
    EXPECT_EQ(position.getPiece(Square{3, 4}), ColoredPiece(BLACK, PAWN));
    EXPECT_EQ(position.getPiece(Square{4, 2}), ColoredPiece(WHITE, PAWN));
    EXPECT_EQ(position.getPiece(Square{5, 2}), ColoredPiece(WHITE, KNIGHT));
    EXPECT_EQ(position.getPiece(Square{7, 3}), ColoredPiece(WHITE, QUEEN));
    EXPECT_EQ(position.getPiece(Square{6, 6}), ColoredPiece(WHITE, BISHOP));
    EXPECT_EQ(position.getPiece(Square{7, 6}), ColoredPiece(WHITE, KING));
    EXPECT_EQ(position.getPiece(Square{7, 5}), ColoredPiece(WHITE, ROOK));
}

TEST(ChessBoardTest, MakeMoveIllegal) {
    Position position;
    std::string fen =
        "r1bqkbnr/pp1ppppp/2n5/2p5/4P3/5N2/PPPP1PPP/RNBQKB1R w KQkq - 2 3";
    position.loadFEN(fen);

    Move illegalRandomMove{0, 0, 7, 7};
    position.makeMove(illegalRandomMove);

    EXPECT_EQ(position.getFEN(), fen);

    fen = "r1bqk1nr/pp1p1ppp/2n1p3/8/1bBNP3/8/PPP2PPP/RNBQK2R w KQkq - 2 6";
    position.loadFEN(fen);

    Move illegalCastlingMove{7, 4, 7, 6};
    position.makeMove(illegalCastlingMove);

    EXPECT_EQ(position.getFEN(), fen);
}

TEST(ChessBoardTest, MakeMoveLegal) {
    Position position;
    std::string fen =
        "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";
    position.loadFEN(fen);

    Move e4{6, 4, 4, 4};
    position.makeMove(e4);

    fen = "rnbqkbnr/pppppppp/8/8/4P3/8/PPPP1PPP/RNBQKBNR b KQkq e3 0 1";

    EXPECT_EQ(position.getFEN(), fen);

    Move d5{1, 3, 3, 3};
    position.makeMove(d5);

    fen = "rnbqkbnr/ppp1pppp/8/3p4/4P3/8/PPPP1PPP/RNBQKBNR w KQkq d6 0 2";

    EXPECT_EQ(position.getFEN(), fen);

    fen = "rnbqkbnr/ppp1p1pp/8/3pPp2/8/8/PPPP1PPP/RNBQKBNR w KQkq f6 0 3";
    position.loadFEN(fen);

    Move exf6EnPassant{3, 4, 2, 5};
    position.makeMove(exf6EnPassant);

    fen = "rnbqkbnr/ppp1p1pp/5P2/3p4/8/8/PPPP1PPP/RNBQKBNR b KQkq - 0 3";

    EXPECT_EQ(position.getFEN(), fen);

    fen = "rnbqkb1r/ppp2ppp/3p1n2/4p2Q/2B1P3/8/PPPP1PPP/RNB1K1NR w KQkq - 0 4";
    position.loadFEN(fen);

    Move qxf7{3, 7, 1, 5};
    position.makeMove(qxf7);

    fen = "rnbqkb1r/ppp2Qpp/3p1n2/4p3/2B1P3/8/PPPP1PPP/RNB1K1NR b KQkq - 0 4";

    EXPECT_EQ(position.getFEN(), fen);
}

TEST(ChessBoardTest, UndoRedoMove) {
    Position position;
    std::string fen =
        "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";

    Move e4{6, 4, 4, 4};
    position.makeMove(e4);

    fen = "rnbqkbnr/pppppppp/8/8/4P3/8/PPPP1PPP/RNBQKBNR b KQkq e3 0 1";

    EXPECT_EQ(position.getFEN(), fen);

    position.unmakeMove();

    fen = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";
    EXPECT_EQ(position.getFEN(), fen);

    position.remakeMove();

    fen = "rnbqkbnr/pppppppp/8/8/4P3/8/PPPP1PPP/RNBQKBNR b KQkq e3 0 1";

    EXPECT_EQ(position.getFEN(), fen);
}

TEST(ChessBoardTest, GetMoveContext) {
    Position position;
    std::string fen =
        "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";

    Move e4{6, 4, 4, 4};
    MoveContext actualContext = position.getMoveContext(e4);

    MoveContext expectedContext = {
        e4,
        NO_Piece,
        Square{-1, -1},
        {KING_SIDE | QUEEN_SIDE, KING_SIDE | QUEEN_SIDE},
        0,
        1,
        WHITE,
        false,
        false};

    EXPECT_EQ(actualContext, expectedContext);

    fen = "r1bqkbnr/pp1p1ppp/2n1p3/2p5/2B1P3/5N2/PPPP1PPP/RNBQK2R w KQkq - 2 4";

    position.loadFEN(fen);

    Move shortCastle{7, 4, 7, 6};
    actualContext = position.getMoveContext(shortCastle);

    expectedContext = {shortCastle,
                       NO_Piece,
                       Square{-1, -1},
                       {KING_SIDE | QUEEN_SIDE, KING_SIDE | QUEEN_SIDE},
                       2,
                       4,
                       WHITE,
                       false,
                       false,
                       true};

    EXPECT_EQ(actualContext, expectedContext);

    fen = "rnbqkbnr/ppp1p1pp/8/3pPp2/8/8/PPPP1PPP/RNBQKBNR w KQkq f6 0 3";

    position.loadFEN(fen);

    Move exf6EnPassant{3, 4, 2, 5};
    actualContext = position.getMoveContext(exf6EnPassant);

    expectedContext = {exf6EnPassant,
                       ColoredPiece(BLACK, PAWN),
                       Square{2, 5},
                       {KING_SIDE | QUEEN_SIDE, KING_SIDE | QUEEN_SIDE},
                       0,
                       3,
                       WHITE,
                       false,
                       true,
                       false};

    EXPECT_EQ(actualContext, expectedContext);
}