#include "../include/check_scanner.h"
#include "../include/position.h"
#include "../include/types.h"
#include <gtest/gtest.h>

TEST(CheckScannerTest, IsInCheckTest) {
    Position position;
    position.loadFEN(
        "rnb1kbnr/pp2pppp/3p4/2p5/3PP3/2q2N2/PPP2PPP/R1BQKB1R w KQkq - 0 5");

    CheckScanner scanner(&position);

    EXPECT_TRUE(scanner.isInCheck(WHITE));
    EXPECT_FALSE(scanner.isInCheck(BLACK));

    position.loadFEN(
        "r1bqkbnr/pp1ppBpp/2n5/2p5/4P3/8/PPPP1PPP/RNBQK1NR b KQkq - 0 3");

    EXPECT_FALSE(scanner.isInCheck(WHITE));
    EXPECT_TRUE(scanner.isInCheck(BLACK));
}

TEST(CheckScannerTest, IsInCheckmate) {
    Position position;
    position.loadFEN(
        "r1bqkb1r/pp1ppBpp/2n2n2/2p4Q/4P3/8/PPPP1PPP/RNB1K1NR b KQkq - 0 4");

    CheckScanner scanner(&position);

    EXPECT_TRUE(scanner.isInCheckmate(BLACK));
    EXPECT_FALSE(scanner.isInCheckmate(WHITE));

    position.loadFEN(
        "r1b1kb1r/pp1ppppp/2n1B3/2p4Q/4n3/N4N2/PPP2qPP/R1B2K1R w kq - 0 8");

    EXPECT_TRUE(scanner.isInCheckmate(WHITE));
    EXPECT_FALSE(scanner.isInCheckmate(BLACK));
}

TEST(CheckScannerTest, IsInStalemate) {
    Position position;
    position.loadFEN("1q6/5ppb/4p2k/4b2p/3p3P/3P2pK/6P1/8 w - - 2 4");

    CheckScanner scanner(&position);

    EXPECT_TRUE(scanner.isInStalemate(WHITE));

    position.loadFEN("3k4/3P4/3K4/8/8/8/8/8 b - - 0 1");

    EXPECT_TRUE(scanner.isInStalemate(BLACK));
}

TEST(CheckScannerTest, IsSquareInCheck) {
    Position position;
    position.loadFEN(
        "r1bqkb1r/pp1p1ppp/2n1pn2/2p5/2B1P3/5N2/PPPP1PPP/RNBQ1RK1 w kq - 4 5");

    CheckScanner scanner(&position);

    EXPECT_TRUE(scanner.isSquareInCheck(Square(3, 4), BLACK));
    EXPECT_TRUE(scanner.isSquareInCheck(Square(3, 1), BLACK));
    EXPECT_TRUE(scanner.isSquareInCheck(Square(3, 6), BLACK));

    position.loadFEN(
        "r1bqkb1r/pp1p1ppp/2n1pn2/2p5/2B1P3/5N2/PPPP1PPP/RNBQR1K1 b kq - 5 5");

    EXPECT_TRUE(scanner.isSquareInCheck(Square(4, 4), WHITE));
    EXPECT_TRUE(scanner.isSquareInCheck(Square(4, 3), WHITE));
    EXPECT_TRUE(scanner.isSquareInCheck(Square(2, 3), WHITE));
}