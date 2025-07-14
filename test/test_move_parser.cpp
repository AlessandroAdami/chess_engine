#include "../include/move_parser.h"
#include "../include/position.h"
#include "../include/types.h"
#include <gtest/gtest.h>

TEST(MoveParser, StringToMove) {
    Position board;
    MoveParser parser(&board);
    std::string fen =
        "rnbqkbnr/ppp1pppp/8/3p4/4P3/8/PPPP1PPP/RNBQKBNR w KQkq - 0 2";
    board.loadFEN(fen);

    std::string moveStr = "nf3";
    Move actualMove = parser.moveStringToMove(moveStr);
    Move expectedMove = Move{7, 6, 5, 5};
    EXPECT_EQ(actualMove, expectedMove);

    moveStr = "exd5";
    actualMove = parser.moveStringToMove(moveStr);
    expectedMove = Move{4, 4, 3, 3};
    EXPECT_EQ(actualMove, expectedMove);

    board.loadFEN(
        "r1bqkb1r/ppp1pppp/2n2n2/3p4/2B1P3/5N2/PPPP1PPP/RNBQK2R w KQkq - 4 4");
    moveStr = "o-o";
    actualMove = parser.moveStringToMove(moveStr);
    expectedMove = Move{7, 4, 7, 6};
    EXPECT_EQ(actualMove, expectedMove);

    board.loadFEN("r3kb1r/pppq1ppp/2n1pn2/3p2B1/2B1P1b1/2NP1N2/PPPQ1PPP/R3K2R "
                  "b KQkq - 3 7");
    moveStr = "o-o-o";
    actualMove = parser.moveStringToMove(moveStr);
    expectedMove = Move{0, 4, 0, 2};
    EXPECT_EQ(actualMove, expectedMove);

    board.loadFEN(
        "r3kb1r/pppN1ppp/2n1pB2/8/2B3b1/2N5/PPpQ1PPP/R3K2R b KQkq - 0 10");
    moveStr = "c1=q";
    actualMove = parser.moveStringToMove(moveStr);
    expectedMove = Move{6, 2, 7, 2, ColoredPiece(BLACK, QUEEN)};
    EXPECT_EQ(actualMove, expectedMove);

    board.loadFEN(
        "r1bqkb1r/pppp1ppp/2n2n2/4p3/4P3/3P1N2/PPP2PPP/RNBQKB1R w KQkq - 1 4");
    moveStr = "nbd2";
    actualMove = parser.moveStringToMove(moveStr);
    expectedMove = Move{7, 1, 6, 3};
    EXPECT_EQ(actualMove, expectedMove);

    moveStr = "nfd2";
    actualMove = parser.moveStringToMove(moveStr);
    expectedMove = Move{5, 5, 6, 3};
    EXPECT_EQ(actualMove, expectedMove);

    board.loadFEN("r1bqkb1r/pppp1ppp/2n2n2/4p3/4P3/1N1P1N2/PPP2PPP/RNBQKB1R w "
                  "KQkq - 1 4");
    moveStr = "nb3d2";
    actualMove = parser.moveStringToMove(moveStr);
    expectedMove = Move{5, 1, 6, 3};
    EXPECT_EQ(actualMove, expectedMove);

    board.loadFEN("1k6/8/8/3B4/8/3B4/8/2Q1K3 w - - 0 1");
    moveStr = "b5e4";
    actualMove = parser.moveStringToMove(moveStr);
    expectedMove = Move{3, 3, 4, 4};
    EXPECT_EQ(actualMove, expectedMove);

    board.loadFEN("1k6/8/8/2q3q1/8/2q1q3/8/3K4 b - - 1 1");
    moveStr = "qc3e5";
    actualMove = parser.moveStringToMove(moveStr);
    expectedMove = Move{5, 2, 3, 4};
    EXPECT_EQ(actualMove, expectedMove);

    board.loadFEN("rnbqkbnr/ppppPppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1");
    moveStr = "exf8=n";
    actualMove = parser.moveStringToMove(moveStr);
    expectedMove = Move{1, 4, 0, 5, ColoredPiece(WHITE, KNIGHT)};
    EXPECT_EQ(actualMove, expectedMove);
}