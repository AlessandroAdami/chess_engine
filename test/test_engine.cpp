#include "../include/engine.h"
#include "../include/position.h"
#include "../include/types.h"
#include <gtest/gtest.h>

TEST(ChessEngineTest, EvaluatePosition) {
    Position position;
    Engine engine(&position);

    position.loadFEN(
        "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1");

    int depth = 0;

    EXPECT_EQ(engine.evaluate(&position), 0);
    EXPECT_EQ(engine.evaluateLeaf(&position, WHITE, depth), 0);

    position.loadFEN(
        "r1bqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1");

    EXPECT_EQ(engine.evaluate(&position), 320);
    EXPECT_EQ(engine.evaluateLeaf(&position, WHITE, depth), 320);

    position.loadFEN(
        "2bqk1nr/1p1p1ppp/B3p3/p1p1n1Q1/4P3/2N5/PPPP1PPP/R1B2RK1 b k - 0 9");

    EXPECT_EQ(engine.evaluate(&position), 510);
    EXPECT_EQ(engine.evaluateLeaf(&position, WHITE, depth), 510);

    position.loadFEN("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/R1B1KBN1 w Qkq - 0 1");

    EXPECT_EQ(engine.evaluate(&position), -1720);
    EXPECT_EQ(engine.evaluateLeaf(&position, BLACK, depth), 1720);

    position.loadFEN(
        "r1bqkb1r/pppp1Qpp/2n2n2/4p3/2B1P3/8/PPPP1PPP/RNB1K1NR b KQkq - 0 4");

    EXPECT_EQ(engine.evaluate(&position), engine.MATE_SCORE);
    EXPECT_EQ(engine.evaluateLeaf(&position, WHITE, depth), engine.MATE_SCORE);

    position.loadFEN(
        "r1b1R2k/ppp2pqp/1b3B2/n2P4/2p5/5N2/P4PPP/RN4K1 b - - 3 3");

    EXPECT_EQ(engine.evaluate(&position), engine.MATE_SCORE);
    EXPECT_EQ(engine.evaluateLeaf(&position, WHITE, depth), engine.MATE_SCORE);
    EXPECT_EQ(engine.evaluateLeaf(&position, WHITE, 1), engine.MATE_SCORE - 1);

    position.loadFEN(
        "rnb1kbnr/pppp1ppp/8/4p3/5PPq/8/PPPPP2P/RNBQKBNR w KQkq - 1 3");

    EXPECT_EQ(engine.evaluate(&position), -engine.MATE_SCORE);
    EXPECT_EQ(engine.evaluateLeaf(&position, BLACK, depth), engine.MATE_SCORE);

    position.loadFEN("r6k/ppp1Bp1p/1b1P4/n7/2p5/5N1b/P4PqP/RN2R1K1 w - - 0 5");

    EXPECT_EQ(engine.evaluate(&position), -engine.MATE_SCORE);
    EXPECT_EQ(engine.evaluateLeaf(&position, BLACK, depth), engine.MATE_SCORE);
    EXPECT_EQ(engine.evaluateLeaf(&position, BLACK, 5), engine.MATE_SCORE - 5);

    position.loadFEN("8/8/8/8/8/4k3/4p3/4K3 w - - 6 9");

    EXPECT_EQ(engine.evaluate(&position), 0);
    EXPECT_EQ(engine.evaluateLeaf(&position, WHITE, depth), 0);
}

TEST(ChessEngineTest, GetBestMoveCheckMateInOne) {
    Position position;
    Engine engine(&position);

    position.loadFEN("3k4/1p1pppn1/2BpQp2/1p1ppp2/8/8/8/2K5 w - - 0 1");

    Move actualBestMove = engine.getBestMove();

    Move expectedBestMove = Move{2, 4, 1, 3};

    EXPECT_EQ(actualBestMove, expectedBestMove);

    position.loadFEN(
        "r1bqkb1r/pppp1ppp/2n2n2/4p2Q/2B1P3/8/PPPP1PPP/RNB1K1NR w KQkq - 4 4");

    actualBestMove = engine.getBestMove();

    expectedBestMove = Move{3, 7, 1, 5};

    EXPECT_EQ(actualBestMove, expectedBestMove);

    position.loadFEN("8/r7/1k5p/2p4P/1p6/1K1Pn3/PR2QB2/8 b - - 3 3");

    actualBestMove = engine.getBestMove();

    expectedBestMove = Move{1, 0, 5, 0};

    EXPECT_EQ(actualBestMove, expectedBestMove);
}

TEST(ChessEngineTest, GetBestMoveCheckMateInTwo) {
    Position position;
    Engine engine(&position);

    position.loadFEN(
        "r2qkb1r/pp2nppp/3p4/2pNN1B1/2BnP3/3P4/PPP2PPP/R2bK2R w KQkq - 1 1");

    Move actualBestMove = engine.getBestMove();

    Move expectedBestMove = Move{3, 3, 2, 5};

    EXPECT_EQ(actualBestMove, expectedBestMove);

    position.loadFEN("6k1/pp4p1/2p5/2bp4/8/P5Pb/1P3rrP/2BRRN1K b - - 0 1");

    actualBestMove = engine.getBestMove();

    expectedBestMove = Move{6, 6, 7, 6};

    EXPECT_EQ(actualBestMove, expectedBestMove);

    position.loadFEN("8/2r5/1k5p/1pp4P/8/K2P4/PR2QB2/2q5 b - - 0 1");

    actualBestMove = engine.getBestMove();

    expectedBestMove = Move{7, 2, 5, 2};

    EXPECT_EQ(actualBestMove, expectedBestMove);
}

TEST(ChessEngineTest, GetBestMoveCheckMateInThree) {
    Position position;
    Engine engine(&position);

    position.loadFEN("8/Q5pk/8/7p/3B3P/6P1/4rnBK/3r4 b - - 0 1");

    Move actualBestMove = engine.getBestMove();

    Move expectedBestMove = Move{6, 5, 4, 6};

    EXPECT_EQ(actualBestMove, expectedBestMove);

    position.loadFEN(
        "r1b3kr/ppp1Bp1p/1b6/n2P4/2p3q1/2Q2N2/P4PPP/RN2R1K1 w - - 1 1");

    actualBestMove = engine.getBestMove();

    expectedBestMove = Move{5, 2, 0, 7};

    EXPECT_EQ(actualBestMove, expectedBestMove);

    position.loadFEN(
        "7k/pbp3bp/3p4/1p5q/3n2p1/5rB1/PP1NrN1P/1Q1BRRK1 b - - 0 1");

    actualBestMove = engine.getBestMove();

    expectedBestMove = Move{5, 5, 5, 6};

    EXPECT_EQ(actualBestMove, expectedBestMove);

    position.loadFEN("k7/p2p4/Pp1P4/1P4P1/8/1p2PP1P/pP6/K7 w - - 1 2");

    actualBestMove = engine.getBestMove();

    expectedBestMove = Move{3, 6, 2, 6};

    EXPECT_EQ(actualBestMove, expectedBestMove);
}

TEST(ChessEngineTest, GetBestMoveSimple) {
    Position position;
    Engine engine(&position);

    position.loadFEN(
        "rnb1kbnr/pppppppp/8/8/2q1P3/8/PPPP1PPP/RNBQKBNR w KQkq - 0 1");

    Move actualBestMove = engine.getBestMove();

    Move expectedBestMove = Move{7, 5, 4, 2};

    EXPECT_EQ(actualBestMove, expectedBestMove);

    position.loadFEN("k7/8/p7/Pp6/1Pp5/2P5/3P4/4b1K1 b - - 0 1");

    actualBestMove = engine.getBestMove();

    expectedBestMove = Move{7, 4, 6, 3};

    EXPECT_EQ(actualBestMove, expectedBestMove);
}