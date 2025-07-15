#include "../include/engine.h"
#include "../include/position.h"
#include "../include/types.h"
#include <gtest/gtest.h>

TEST(ChessEngineTest, EvaluatePosition) {
    Position position;
    ChessEngine engine(&position);

    position.loadFEN(
        "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1");

    int score = engine.evaluatePosition();

    EXPECT_EQ(score, 0);

    position.loadFEN(
        "r1bqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1");

    score = engine.evaluatePosition();

    EXPECT_EQ(score, 320);

    position.loadFEN(
        "2bqk1nr/1p1p1ppp/B3p3/p1p1n1Q1/4P3/2N5/PPPP1PPP/R1B2RK1 b k - 0 9");

    score = engine.evaluatePosition();

    EXPECT_EQ(score, 510);

    position.loadFEN(
        "r1bqkb1r/pppp1Qpp/2n2n2/4p3/2B1P3/8/PPPP1PPP/RNB1K1NR b KQkq - 0 4");

    score = engine.evaluatePosition();

    EXPECT_EQ(score, engine.WHITE_WIN_SCORE);

    position.loadFEN(
        "rnb1kbnr/pppp1ppp/8/4p3/5PPq/8/PPPPP2P/RNBQKBNR w KQkq - 1 3");

    score = engine.evaluatePosition();

    EXPECT_EQ(score, engine.BLACK_WIN_SCORE);

    position.loadFEN("8/8/8/8/8/4k3/4p3/4K3 w - - 6 9");

    score = engine.evaluatePosition();

    EXPECT_EQ(score, 0);
}

TEST(ChessEngineTest, GetBestMove) {
    Position position;
    ChessEngine engine(&position);

    position.loadFEN("3k4/6n1/2B1Q3/8/8/8/8/2K5 w - - 0 1");

    Move actualBestMove = engine.getBestMove();

    Move expectedBestMove = Move{2, 4, 1, 3};

    EXPECT_EQ(actualBestMove, expectedBestMove);

    position.loadFEN(
        "r1bqkb1r/pppp1ppp/2n2n2/4p2Q/2B1P3/8/PPPP1PPP/RNB1K1NR w KQkq - 4 4");

    actualBestMove = engine.getBestMove();

    expectedBestMove = Move{3, 7, 1, 5};

    EXPECT_EQ(actualBestMove, expectedBestMove);

    position.loadFEN(
        "r2qkb1r/pp2nppp/3p4/2pNN1B1/2BnP3/3P4/PPP2PPP/R2bK2R w KQkq - 1 1");

    actualBestMove = engine.getBestMove();

    expectedBestMove = Move{3, 3, 2, 5};

    EXPECT_EQ(actualBestMove, expectedBestMove);

    position.loadFEN(
        "rnb1kbnr/pppppppp/8/8/2q1P3/8/PPPP1PPP/RNBQKBNR w KQkq - 0 1");

    actualBestMove = engine.getBestMove();

    expectedBestMove = Move{7, 5, 4, 2};

    EXPECT_EQ(actualBestMove, expectedBestMove);

    position.loadFEN("8/Qp4pk/2p3b1/5p1p/3B3P/1P4P1/P1P1rnBK/3r4 b - - 0 1");

    actualBestMove = engine.getBestMove();

    printMove(actualBestMove);

    expectedBestMove = Move{1, 2, 3, 1};

    EXPECT_EQ(actualBestMove, expectedBestMove);
}