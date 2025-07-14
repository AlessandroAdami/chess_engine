#include "../include/engine.h"
#include "../include/position.h"
#include "../include/types.h"
#include <gtest/gtest.h>

TEST(ChessEngineTest, EvaluatePosition) {
    Position position;
    ChessEngine engine(&position);

    position.loadFEN("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1");

    int score = engine.evaluatePosition();

    EXPECT_EQ(score, 0);

    position.loadFEN("r1bqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1");

    score = engine.evaluatePosition();

    EXPECT_EQ(score, 320);

    position.loadFEN(
        "2bqk1nr/1p1p1ppp/B3p3/p1p1n1Q1/4P3/2N5/PPPP1PPP/R1B2RK1 b k - 0 9");

    score = engine.evaluatePosition();

    EXPECT_EQ(score, 510);
}

TEST(ChessEngineTest, GetBestMove) {
    Position position;
    ChessEngine engine(&position);

    position.loadFEN(
        "r1bqkb1r/pppp1ppp/2n2n2/4p2Q/2B1P3/8/PPPP1PPP/RNB1K1NR w KQkq - 4 4");

    Move actualBestMove = engine.getBestMove();
    
    std::cout << actualBestMove.from.row << actualBestMove.from.col << actualBestMove.to.row << actualBestMove.to.col;

    Move expectedBestMove = Move{3, 7, 1, 5};

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
}