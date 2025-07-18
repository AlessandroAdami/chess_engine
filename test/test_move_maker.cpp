#include "../include/move_maker.h"
#include "../include/position.h"
#include <gtest/gtest.h>

TEST(MoveMakerTest, PawnMakeMove) {
    Position position;
    std::string fen =
        "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";
    position.loadFEN(fen);

    MoveMaker moveMaker(&position);

    Move e4{6, 4, 4, 4};
    moveMaker.makeMove(e4);

    EXPECT_EQ(position.getFEN(),
              "rnbqkbnr/pppppppp/8/8/4P3/8/PPPP1PPP/RNBQKBNR b KQkq e3 0 1");

    fen = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";
    position.loadFEN(fen);

    Move d3{6, 3, 5, 3};
    moveMaker.makeMove(d3);

    EXPECT_EQ(position.getFEN(),
              "rnbqkbnr/pppppppp/8/8/8/3P4/PPP1PPPP/RNBQKBNR b KQkq - 0 1");

    fen = "rnbqkbnr/ppp1pppp/8/3p4/2P1P3/8/PP1P1PPP/RNBQKBNR b KQkq - 0 2";
    position.loadFEN(fen);

    Move dxe4{3, 3, 4, 4};
    moveMaker.makeMove(dxe4);

    EXPECT_EQ(position.getFEN(),
              "rnbqkbnr/ppp1pppp/8/8/2P1p3/8/PP1P1PPP/RNBQKBNR w KQkq - 0 3");

    fen = "rnbqkbnr/ppp1pppp/8/3p4/2P1P3/8/PP1P1PPP/RNBQKBNR b KQkq - 0 2";
    position.loadFEN(fen);

    Move dxc4{3, 3, 4, 2};
    moveMaker.makeMove(dxc4);

    EXPECT_EQ(position.getFEN(),
              "rnbqkbnr/ppp1pppp/8/8/2p1P3/8/PP1P1PPP/RNBQKBNR w KQkq - 0 3");

    fen = "rnbqkbnr/ppp1p1pp/8/3pPp2/8/8/PPPP1PPP/RNBQKBNR w KQkq f6 0 3";
    position.loadFEN(fen);

    Move exf6EnPassant{3, 4, 2, 5};
    moveMaker.makeMove(exf6EnPassant);

    EXPECT_EQ(position.getFEN(),
              "rnbqkbnr/ppp1p1pp/5P2/3p4/8/8/PPPP1PPP/RNBQKBNR b KQkq - 0 3");
}

TEST(MoveMakerTest, KnightMakeMove) {
    Position position;
    std::string fen =
        "r1bqkbnr/pppppppp/2n5/8/3N4/8/PPPPPPPP/RNBQKB1R b KQkq - 3 2";
    position.loadFEN(fen);

    MoveMaker moveMaker(&position);

    Move nxd4{2, 2, 4, 3};
    moveMaker.makeMove(nxd4);

    EXPECT_EQ(position.getFEN(),
              "r1bqkbnr/pppppppp/8/8/3n4/8/PPPPPPPP/RNBQKB1R w KQkq - 0 3");

    fen = "rnbqkbnr/ppp1p1pp/8/3p1pN1/8/8/PPPPPPPP/RNBQKB1R w KQkq - 0 3";
    position.loadFEN(fen);

    Move nh3{3, 6, 5, 7};
    moveMaker.makeMove(nh3);

    EXPECT_EQ(position.getFEN(),
              "rnbqkbnr/ppp1p1pp/8/3p1p2/8/7N/PPPPPPPP/RNBQKB1R b KQkq - 1 3");
}

TEST(MoveMakerTest, BishopMakeMove) {
    Position position;
    std::string fen =
        "rnbqkbnr/ppp1pppp/8/3p4/4P3/8/PPPP1PPP/RNBQKBNR w KQkq - 0 2";
    position.loadFEN(fen);

    MoveMaker moveMaker(&position);

    Move bc4{7, 5, 4, 2};
    moveMaker.makeMove(bc4);

    EXPECT_EQ(position.getFEN(),
              "rnbqkbnr/ppp1pppp/8/3p4/2B1P3/8/PPPP1PPP/RNBQK1NR b KQkq - 1 2");

    fen = "rnbqkbnr/pp2pppp/3p4/2p5/2B1P3/8/PPPP1PPP/RNBQK1NR w KQkq - 0 3";
    position.loadFEN(fen);

    Move bxf7{4, 2, 1, 5};
    moveMaker.makeMove(bxf7);

    EXPECT_EQ(position.getFEN(),
              "rnbqkbnr/pp2pBpp/3p4/2p5/4P3/8/PPPP1PPP/RNBQK1NR b KQkq - 0 3");
}

TEST(MoveMakerTest, RookMakeMove) {
    Position position;
    std::string fen =
        "1nbqkbnr/1ppppppp/r7/p7/7P/4R3/PPPPPPP1/RNBQKBN1 b Qk - 3 3";
    position.loadFEN(fen);

    MoveMaker moveMaker(&position);

    Move re6{2, 0, 2, 4};
    moveMaker.makeMove(re6);

    EXPECT_EQ(position.getFEN(),
              "1nbqkbnr/1ppppppp/4r3/p7/7P/4R3/PPPPPPP1/RNBQKBN1 w Qk - 4 4");

    fen = "1nbqkbnr/1ppppppp/4r3/p7/7P/4R3/PPPPPPP1/RNBQKBN1 w Qk - 4 4";
    position.loadFEN(fen);

    Move rxe6{5, 4, 2, 4};
    moveMaker.makeMove(rxe6);

    EXPECT_EQ(position.getFEN(),
              "1nbqkbnr/1ppppppp/4R3/p7/7P/8/PPPPPPP1/RNBQKBN1 b Qk - 0 4");
}

TEST(MoveMakerTest, QueenMakeMove) {
    Position position;
    std::string fen =
        "rnbqkbnr/ppp1pppp/8/3p4/4P3/8/PPPP1PPP/RNBQKQNR w KQkq - 0 2";
    position.loadFEN(fen);

    MoveMaker moveMaker(&position);

    Move qc4{7, 5, 4, 2};
    moveMaker.makeMove(qc4);

    EXPECT_EQ(position.getFEN(),
              "rnbqkbnr/ppp1pppp/8/3p4/2Q1P3/8/PPPP1PPP/RNBQK1NR b KQkq - 1 2");

    fen = "rnbqkbnr/pp2pppp/3p4/2p5/2Q1P3/8/PPPP1PPP/RNBQK1NR w KQkq - 0 3";
    position.loadFEN(fen);

    Move qxf7{4, 2, 1, 5};
    moveMaker.makeMove(qxf7);

    EXPECT_EQ(position.getFEN(),
              "rnbqkbnr/pp2pQpp/3p4/2p5/4P3/8/PPPP1PPP/RNBQK1NR b KQkq - 0 3");

    fen = "1nbqkbnr/1ppppppp/q7/p7/7P/4Q3/PPPPPPP1/RNBQKBN1 b Qk - 3 3";
    position.loadFEN(fen);

    Move qe6{2, 0, 2, 4};
    moveMaker.makeMove(qe6);

    EXPECT_EQ(position.getFEN(),
              "1nbqkbnr/1ppppppp/4q3/p7/7P/4Q3/PPPPPPP1/RNBQKBN1 w Qk - 4 4");

    fen = "1nbqkbnr/1ppppppp/4q3/p7/7P/4Q3/PPPPPPP1/RNBQKBN1 w Qk - 4 4";
    position.loadFEN(fen);

    Move qxe6{5, 4, 2, 4};
    moveMaker.makeMove(qxe6);

    EXPECT_EQ(position.getFEN(),
              "1nbqkbnr/1ppppppp/4Q3/p7/7P/8/PPPPPPP1/RNBQKBN1 b Qk - 0 4");
}

TEST(MoveMakerTest, KingMakeMove) {
    Position position;
    std::string fen =
        "rnbqk1nr/pppp1ppp/8/2b1p3/2B1P3/5N2/PPPP1PPP/RNBQK2R b KQkq - 3 3";
    position.loadFEN(fen);

    MoveMaker moveMaker(&position);

    Move ke7{0, 4, 1, 4};
    moveMaker.makeMove(ke7);

    EXPECT_EQ(position.getFEN(),
              "rnbq2nr/ppppkppp/8/2b1p3/2B1P3/5N2/PPPP1PPP/RNBQK2R w KQ - 4 4");

    fen = "rnbq2nr/ppppkppp/8/2b1p3/2B1P3/5N2/PPPP1PPP/RNBQK2R w KQ - 4 4";
    position.loadFEN(fen);

    Move shortCastle{7, 4, 7, 6};
    moveMaker.makeMove(shortCastle);

    EXPECT_EQ(position.getFEN(),
              "rnbq2nr/ppppkppp/8/2b1p3/2B1P3/5N2/PPPP1PPP/RNBQ1RK1 b - - 5 4");

    fen = "rnbqk2r/ppp1ppbp/5np1/3p2B1/3P4/2N5/PPPQPPPP/R3KBNR w KQkq - 2 5";
    position.loadFEN(fen);

    Move longCastle{7, 4, 7, 2};
    moveMaker.makeMove(longCastle);

    EXPECT_EQ(
        position.getFEN(),
        "rnbqk2r/ppp1ppbp/5np1/3p2B1/3P4/2N5/PPPQPPPP/2KR1BNR b kq - 3 5");
}

TEST(MoveMakerTest, UndoMove) {
    Position position;
    std::string fen =
        "rnbqkbnr/ppp1p1pp/8/3pPp2/8/8/PPPP1PPP/RNBQKBNR w KQkq d6 0 3";
    position.loadFEN(fen);

    MoveMaker moveMaker(&position);

    Move exd6{3, 4, 2, 3};
    moveMaker.makeMove(exd6);

    EXPECT_EQ(position.getFEN(),
              "rnbqkbnr/ppp1p1pp/3P4/5p2/8/8/PPPP1PPP/RNBQKBNR b KQkq - 0 3");

    moveMaker.unmakeMove();

    EXPECT_EQ(position.getFEN(),
              "rnbqkbnr/ppp1p1pp/8/3pPp2/8/8/PPPP1PPP/RNBQKBNR w KQkq d6 0 3");

    Move ba6{7, 5, 2, 0};
    moveMaker.makeMove(ba6);
    EXPECT_EQ(position.getFEN(),
              "rnbqkbnr/ppp1p1pp/B7/3pPp2/8/8/PPPP1PPP/RNBQK1NR b KQkq - 1 3");

    moveMaker.unmakeMove();
    EXPECT_EQ(position.getFEN(),
              "rnbqkbnr/ppp1p1pp/8/3pPp2/8/8/PPPP1PPP/RNBQKBNR w KQkq d6 0 3");

    moveMaker.makeMove(ba6);
    Move nxa6{0, 1, 2, 0};
    moveMaker.makeMove(nxa6);
    EXPECT_EQ(position.getFEN(),
              "r1bqkbnr/ppp1p1pp/n7/3pPp2/8/8/PPPP1PPP/RNBQK1NR w KQkq - 0 4");

    moveMaker.unmakeMove();
    EXPECT_EQ(position.getFEN(),
              "rnbqkbnr/ppp1p1pp/B7/3pPp2/8/8/PPPP1PPP/RNBQK1NR b KQkq - 1 3");
}

TEST(MoveMakerTest, GetCapturedPiece) {
    Position position;
    std::string fen =
        "rnbqkbnr/ppp1p1pp/8/3pPp2/8/8/PPPP1PPP/RNBQKBNR w KQkq d6 0 3";
    position.loadFEN(fen);

    MoveMaker moveMaker(&position);

    Move exd6{3, 4, 2, 3};
    ColoredPiece capturedPiece = moveMaker.getCapturedPiece(exd6);

    EXPECT_EQ(capturedPiece, ColoredPiece(BLACK, PAWN));

    fen = "rnb1kbnr/pppppppp/8/8/2q1P3/8/PPPP1PPP/RNBQKBNR w KQkq - 0 1";
    position.loadFEN(fen);
    Move bxc4{7, 5, 4, 2};
    capturedPiece = moveMaker.getCapturedPiece(bxc4);

    EXPECT_EQ(capturedPiece, ColoredPiece(BLACK, QUEEN));

    fen = "rnbqkbnr/ppp1p1pp/8/3pPp2/8/8/PPPP1PPP/RNBQKBNR w KQkq f6 0 3";
    position.loadFEN(fen);

    Move exf6EnPassant{3, 4, 2, 5};
    capturedPiece = moveMaker.getCapturedPiece(exf6EnPassant);

    EXPECT_EQ(capturedPiece, ColoredPiece(BLACK, PAWN));

    fen = "rnbqkbnr/ppp1p1pp/8/3p1pN1/8/8/PPPPPPPP/RNBQKB1R w KQkq - 0 3";
    position.loadFEN(fen);

    Move nh3{3, 6, 5, 7};
    capturedPiece = moveMaker.getCapturedPiece(nh3);

    EXPECT_EQ(capturedPiece, NO_Piece);

    fen = "rnbq2nr/ppppkppp/8/2b1p3/2B1P3/5N2/PPPP1PPP/RNBQK2R w KQ - 4 4";
    position.loadFEN(fen);

    Move shortCastle{7, 4, 7, 6};
    capturedPiece = moveMaker.getCapturedPiece(shortCastle);

    EXPECT_EQ(capturedPiece, NO_Piece);
}

TEST(MoveMakerTest, GetMoveContext) {
    Position position;
    std::string fen =
        "rnbqkbnr/ppp1p1pp/8/3pPp2/8/8/PPPP1PPP/RNBQKBNR w KQkq d6 0 3";
    position.loadFEN(fen);

    MoveMaker moveMaker(&position);

    Move exd6{3, 4, 2, 3};
    MoveContext context = moveMaker.getMoveContext(exd6);

    MoveContext expectedContext = {
        exd6,
        ColoredPiece(BLACK, PAWN),
        Square{2, 3},
        {KING_SIDE | QUEEN_SIDE, KING_SIDE | QUEEN_SIDE},
        0,
        3,
        true,
        false,
        true,
        false};

    EXPECT_EQ(context, expectedContext);

    fen = "rnbqk1nr/pppp1ppp/8/2b1p3/2B1P3/5N2/PPPP1PPP/RNBQK2R b Qk - 3 3";
    position.loadFEN(fen);
    Move ke7{0, 4, 1, 4};

    context = moveMaker.getMoveContext(ke7);
    expectedContext = {ke7,   NO_Piece, Square{-1, -1}, {QUEEN_SIDE, KING_SIDE},
                       3,     3,        false,          false,
                       false, false};

    EXPECT_EQ(context, expectedContext);

    fen = "rnbqkb1r/ppp1pppp/5n2/8/2p1P3/5N2/PPPP1PPP/RNBQK2R w KQkq - 0 4";
    position.loadFEN(fen);
    Move castle{7, 4, 7, 6};

    context = moveMaker.getMoveContext(castle);
    expectedContext = {castle,
                       NO_Piece,
                       Square{-1, -1},
                       {KING_SIDE | QUEEN_SIDE, KING_SIDE | QUEEN_SIDE},
                       0,
                       4,
                       true,
                       false,
                       false,
                       true};

    EXPECT_EQ(context, expectedContext);
}