#include "../include/movement_validator.h"
#include "../include/position.h"
#include <gtest/gtest.h>

TEST(MovementValidatorTest, PawnGoodMovement) {
    Position position;
    position.loadFEN(
        "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1");

    MovementValidator validator(&position);

    Move pushOne(Square(6, 4), Square(5, 4));
    EXPECT_TRUE(validator.isValidMove(pushOne));

    Move pushTwo(Square(6, 4), Square(4, 4));
    EXPECT_TRUE(validator.isValidMove(pushTwo));

    position.loadFEN(
        "rnbqkbnr/ppp1p1pp/8/3p1p2/3PP3/8/PPP2PPP/RNBQKBNR w KQkq - 0 3");

    Move captureLeft(Square(4, 4), Square(3, 3));
    EXPECT_TRUE(validator.isValidMove(captureLeft));

    Move captureRight(Square(4, 4), Square(3, 5));
    EXPECT_TRUE(validator.isValidMove(captureRight));

    position.loadFEN(
        "r1bqkbnr/ppp3Pp/2n1p3/3p4/8/8/PPPP1PPP/RNBQKBNR w KQkq - 0 5");

    Move promoteQueen(Square(1, 6), Square(0, 7), ColoredPiece(WHITE, QUEEN));
    EXPECT_TRUE(validator.isValidMove(promoteQueen));

    Move promoteRook(Square(1, 6), Square(0, 7), ColoredPiece(WHITE, ROOK));
    EXPECT_TRUE(validator.isValidMove(promoteRook));

    Move promoteBishop(Square(1, 6), Square(0, 7), ColoredPiece(WHITE, BISHOP));
    EXPECT_TRUE(validator.isValidMove(promoteBishop));

    Move promoteKnight(Square(1, 6), Square(0, 7), ColoredPiece(WHITE, KNIGHT));
    EXPECT_TRUE(validator.isValidMove(promoteKnight));

    position.loadFEN(
        "rnbqkbnr/ppp1p1pp/8/3pPp2/8/8/PPPP1PPP/RNBQKBNR w KQkq f6 0 3");

    Move enPassant(Square(3, 4), Square(2, 5));
    EXPECT_TRUE(validator.isValidMove(enPassant));
}

TEST(MovementValidatorTest, PawnBadMovement) {
    Position position;
    position.loadFEN(
        "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1");

    MovementValidator validator(&position);

    Move randomBadMove(Square(6, 4), Square(0, 0));
    EXPECT_FALSE(validator.isValidMove(randomBadMove));

    position.loadFEN(
        "r1bqkbnr/ppp1p1pp/2n5/3pPp2/8/2N5/PPPP1PPP/R1BQKBNR w KQkq - 2 4");

    Move enPassantDelayed(Square(3, 4), Square(2, 5));
    EXPECT_FALSE(validator.isValidMove(enPassantDelayed));

    position.loadFEN(
        "rnbqkbnr/pppp1ppp/8/4p3/4P3/8/PPPP1PPP/RNBQKBNR w KQkq - 0 2");

    Move pawnCaptureForward(Square(4, 4), Square(3, 4));
    EXPECT_FALSE(validator.isValidMove(pawnCaptureForward));

    position.loadFEN(
        "r3kb1r/pppN1ppp/2n1pB2/8/2B3b1/2N5/PPpQ1PPP/R3K2R b KQkq - 0 10");

    Move sidewaysNoCapture(Square(6, 2), Square(7, 3));
    EXPECT_FALSE(validator.isValidMove(sidewaysNoCapture));

    Move lastRankNoPromotion(Square(6, 2), Square(7, 2));
    EXPECT_FALSE(validator.isValidMove(lastRankNoPromotion));
}

TEST(MovementValidatorTest, BishopGoodMovement) {
    Position position;
    position.loadFEN(
        "rnbqkb1r/pppp1ppp/5n2/4p3/2B1P3/8/PPPP1PPP/RNBQK1NR w KQkq - 2 3");

    MovementValidator validator(&position);

    Move upRight(Square(4, 2), Square(1, 5));
    EXPECT_TRUE(validator.isValidMove(upRight));

    Move upLeft(Square(4, 2), Square(3, 1));
    EXPECT_TRUE(validator.isValidMove(upLeft));

    Move downRight(Square(4, 2), Square(7, 5));
    EXPECT_TRUE(validator.isValidMove(downRight));

    Move downLeft(Square(4, 2), Square(5, 1));
    EXPECT_TRUE(validator.isValidMove(downLeft));
}

TEST(MovementValidatorTest, BishopBadMovement) {
    Position position;
    position.loadFEN(
        "r1bqkb1r/ppp2ppp/2np1n2/4p3/P1B1P3/1P6/2PP1PPP/RNBQK1NR w KQkq - 0 5");

    MovementValidator validator(&position);

    Move randomBadMove(Square(4, 2), Square(0, 0));
    EXPECT_FALSE(validator.isValidMove(randomBadMove));

    Move travelThroughEnemyPiece(Square(4, 2), Square(0, 6));
    EXPECT_FALSE(validator.isValidMove(travelThroughEnemyPiece));

    Move travelThroughFriendPiece(Square(4, 2), Square(6, 0));
    EXPECT_FALSE(validator.isValidMove(travelThroughFriendPiece));

    Move captureFriendPiece(Square(4, 2), Square(5, 1));
    EXPECT_FALSE(validator.isValidMove(captureFriendPiece));
}

TEST(MovementValidatorTest, RookGoodMovement) {
    Position position;
    position.loadFEN(
        "r1bqkb1r/1ppp1ppp/2n1pn2/p2R4/P7/8/1PPPPPPP/1NBQKBNR w Kkq - 0 5");

    MovementValidator validator(&position);

    Move up(Square(3, 3), Square(1, 3));
    EXPECT_TRUE(validator.isValidMove(up));

    Move down(Square(3, 3), Square(5, 3));
    EXPECT_TRUE(validator.isValidMove(down));

    Move right(Square(3, 3), Square(3, 7));
    EXPECT_TRUE(validator.isValidMove(right));

    Move left(Square(3, 3), Square(3, 0));
    EXPECT_TRUE(validator.isValidMove(left));
}

TEST(MovementValidatorTest, RookBadMovement) {
    Position position;
    position.loadFEN(
        "r1bqk2r/1ppp1ppp/2n1pn2/p1bR4/P2P4/8/1PP1PPPP/1NBQKBNR w Kkq - 1 6");

    MovementValidator validator(&position);

    Move randomBadMove(Square(3, 3), Square(0, 0));
    EXPECT_FALSE(validator.isValidMove(randomBadMove));

    Move travelThroughEnemyPiece(Square(3, 3), Square(0, 3));
    EXPECT_FALSE(validator.isValidMove(travelThroughEnemyPiece));

    Move travelThroughFriendPiece(Square(3, 3), Square(6, 3));
    EXPECT_FALSE(validator.isValidMove(travelThroughFriendPiece));

    Move captureFriendPiece(Square(3, 3), Square(4, 3));
    EXPECT_FALSE(validator.isValidMove(captureFriendPiece));
}

TEST(MovementValidatorTest, QueenGoodMovement) {
    Position position;
    position.loadFEN(
        "rnbqkb1r/pppp1ppp/5n2/4p3/2Q1P3/8/PPPP1PPP/RNBQK1NR w KQkq - 2 3");

    MovementValidator validator(&position);

    Move upRight(Square(4, 2), Square(1, 5));
    EXPECT_TRUE(validator.isValidMove(upRight));

    Move upLeft(Square(4, 2), Square(3, 1));
    EXPECT_TRUE(validator.isValidMove(upLeft));

    Move downRight(Square(4, 2), Square(7, 5));
    EXPECT_TRUE(validator.isValidMove(downRight));

    Move downLeft(Square(4, 2), Square(5, 1));
    EXPECT_TRUE(validator.isValidMove(downLeft));

    position.loadFEN(
        "r1bqkb1r/1ppp1ppp/2n1pn2/p2Q4/P7/8/1PPPPPPP/1NBQKBNR w Kkq - 0 5");

    Move up(Square(3, 3), Square(1, 3));
    EXPECT_TRUE(validator.isValidMove(up));

    Move down(Square(3, 3), Square(5, 3));
    EXPECT_TRUE(validator.isValidMove(down));

    Move right(Square(3, 3), Square(3, 7));
    EXPECT_TRUE(validator.isValidMove(right));

    Move left(Square(3, 3), Square(3, 0));
    EXPECT_TRUE(validator.isValidMove(left));
}

TEST(MovementValidatorTest, QueenBadMovement) {
    Position position;
    position.loadFEN(
        "r1bqk2r/1ppp1ppp/2n1pn2/p1bQ4/P2P4/8/1PP1PPPP/1NBQKBNR w Kkq - 1 6");

    MovementValidator validator(&position);

    Move randomBadMove(Square(3, 3), Square(0, 0));
    EXPECT_FALSE(validator.isValidMove(randomBadMove));

    Move travelThroughEnemyPieceRook(Square(3, 3), Square(0, 3));
    EXPECT_FALSE(validator.isValidMove(travelThroughEnemyPieceRook));

    Move travelThroughFriendPieceRook(Square(3, 3), Square(6, 3));
    EXPECT_FALSE(validator.isValidMove(travelThroughFriendPieceRook));

    Move captureFriendPiece(Square(3, 3), Square(4, 3));
    EXPECT_FALSE(validator.isValidMove(captureFriendPiece));

    position.loadFEN(
        "r1bqkb1r/ppp2ppp/2np1n2/4p3/P1Q1P3/1P6/2PP1PPP/RNBBK1NR w KQkq - 0 5");

    Move travelThroughEnemyPieceBishop(Square(4, 2), Square(0, 6));
    EXPECT_FALSE(validator.isValidMove(travelThroughEnemyPieceBishop));

    Move travelThroughFriendPieceBishop(Square(4, 2), Square(6, 0));
    EXPECT_FALSE(validator.isValidMove(travelThroughFriendPieceBishop));
}

TEST(MovementValidatorTest, KnightGoodMovement) {
    Position position;
    position.loadFEN(
        "rnbqkbnr/pppppppp/8/8/4N3/8/PPP1P1PP/RNBQKB1R w KQkq - 0 1");

    MovementValidator validator(&position);

    Move upTwoRightOne(Square(4, 4), Square(2, 5));
    EXPECT_TRUE(validator.isValidMove(upTwoRightOne));

    Move upOneRightTwo(Square(4, 4), Square(3, 6));
    EXPECT_TRUE(validator.isValidMove(upOneRightTwo));

    Move downOneRightTwo(Square(4, 4), Square(5, 6));
    EXPECT_TRUE(validator.isValidMove(downOneRightTwo));

    Move downTwoRightOne(Square(4, 4), Square(6, 5));
    EXPECT_TRUE(validator.isValidMove(downTwoRightOne));

    Move downTwoLeftOne(Square(4, 4), Square(6, 3));
    EXPECT_TRUE(validator.isValidMove(downTwoLeftOne));

    Move downOneLeftTwo(Square(4, 4), Square(5, 2));
    EXPECT_TRUE(validator.isValidMove(downOneLeftTwo));

    Move upOneLeftTwo(Square(4, 4), Square(3, 2));
    EXPECT_TRUE(validator.isValidMove(upOneLeftTwo));

    Move upTwoLeftOne(Square(4, 4), Square(2, 3));
    EXPECT_TRUE(validator.isValidMove(upTwoLeftOne));
}

TEST(MovementValidatorTest, KnightBadMovement) {
    Position position;
    position.loadFEN(
        "rnbqkbnr/pppppppp/8/8/4N3/8/PPPPPPsPP/RNBQKB1R w KQkq - 0 1");

    MovementValidator validator(&position);

    Move randomBadMove(Square(4, 4), Square(0, 0));
    EXPECT_FALSE(validator.isValidMove(randomBadMove));

    Move captureFriendPiece(Square(4, 4), Square(6, 5));
    EXPECT_FALSE(validator.isValidMove(captureFriendPiece));
}

TEST(MovementValidatorTest, CastlingGoodMove) {
    Position position;
    position.loadFEN("rnbqk2r/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR b KQkq - 0 1");

    MovementValidator validator(&position);

    Move shortCastle(Square(0, 4), Square(0, 6));
    EXPECT_TRUE(validator.isValidMove(shortCastle));

    position.loadFEN("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/R3KBNR w KQkq - 0 1");

    Move longCastle(Square(7, 4), Square(7, 2));
    EXPECT_TRUE(validator.isValidMove(longCastle));
}

TEST(MovementValidatorTest, CastlingBadMove) {
    Position position;
    position.loadFEN(
        "rnb1kbnr/pppppppp/8/8/2q1P3/5N2/PPPP1PPP/RNBQK2R w KQkq - 0 1");

    MovementValidator validator(&position);

    Move shortCastleThroughCheck(Square(7, 4), Square(7, 6));
    EXPECT_FALSE(validator.isValidMove(shortCastleThroughCheck));

    position.loadFEN(
        "r3kbnr/pppp1ppp/8/6B1/8/8/PPPPPPPP/RNBQK1NR b KQkq - 0 1");

    Move longCastleThroughCheck(Square(0, 4), Square(0, 2));
    EXPECT_FALSE(validator.isValidMove(longCastleThroughCheck));

    position.loadFEN(
        "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR b KQkq - 0 1");

    Move shortCastleThroughPieces(Square(0, 4), Square(0, 6));
    EXPECT_FALSE(validator.isValidMove(shortCastleThroughPieces));

    position.loadFEN(
        "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1");

    Move longCastleThroughPieces(Square(7, 4), Square(7, 2));
    EXPECT_FALSE(validator.isValidMove(longCastleThroughPieces));

    position.loadFEN("rnbqk2r/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR b KQq - 0 1");

    Move shortCastleRookHasMoved(Square(0, 4), Square(0, 6));
    EXPECT_FALSE(validator.isValidMove(shortCastleRookHasMoved));

    position.loadFEN("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/R3KBNR w kq - 0 1");

    Move longCastleKingHasMoved(Square(7, 4), Square(7, 2));
    EXPECT_FALSE(validator.isValidMove(longCastleKingHasMoved));
}

TEST(MovementValidatorTest, IntoCheckBadMove) {
    Position position;
    std::string fen =
        "rnbqkbnr/pppp1Bpp/8/4p3/4P3/8/PPPP1PPP/RNBQK1NR b KQkq - 1 1";
    position.loadFEN(fen);

    MovementValidator validator(&position);

    Move d6IntoCheck(Square(1, 3), Square(2, 3));
    EXPECT_FALSE(validator.isValidMove(d6IntoCheck));
    EXPECT_EQ(position.getFEN(), fen);

    fen = "rnbqk1nr/pppp1ppp/8/4p3/1b2P3/5N2/PPPP1PPP/RNBQKB1R w KQkq - 2 3";
    position.loadFEN(fen);

    Move d4IntoCheck(Square(6, 3), Square(4, 3));
    EXPECT_FALSE(validator.isValidMove(d4IntoCheck));
    EXPECT_EQ(position.getFEN(), fen);

    fen = "rkb2b1r/pppppppp/4qn2/4B3/8/8/PPPP1PPP/RNBQKBNR w KQkq - 1 2";
    position.loadFEN(fen);

    Move bishopCheckIntoCheck(Square(3, 4), Square(1, 2));
    EXPECT_FALSE(validator.isValidMove(bishopCheckIntoCheck));
    EXPECT_EQ(position.getFEN(), fen);
}

TEST(MovementValidatorTest, GetLegalMoves) {
    Position position;
    std::string fen =
        "rnbqkbnr/pppp1Bpp/8/4p3/4P3/8/PPPP1PPP/RNBQK1NR b KQkq - 1 1";
    position.loadFEN(fen);

    MovementValidator validator(&position);

    std::vector<Move> legalMoves = validator.getLegalMoves(BLACK);

    EXPECT_FALSE(
        vectorContainsMove(legalMoves, Move(Square(0, 0), Square(0, 0))));
    EXPECT_FALSE(
        vectorContainsMove(legalMoves, Move(Square(1, 3), Square(3, 3))));
    EXPECT_FALSE(
        vectorContainsMove(legalMoves, Move(Square(0, 1), Square(2, 2))));

    EXPECT_TRUE(
        vectorContainsMove(legalMoves, Move(Square(0, 4), Square(1, 5))));
    EXPECT_TRUE(
        vectorContainsMove(legalMoves, Move(Square(0, 4), Square(1, 4))));

    fen = "r1bqk2r/pp1pppbp/2n2np1/8/3NP3/2N5/PPP1BPPP/R1BQK2R w KQkq - 3 7";
    position.loadFEN(fen);

    legalMoves = validator.getLegalMoves(WHITE);

    EXPECT_TRUE(
        vectorContainsMove(legalMoves, Move(Square(7, 4), Square(7, 6))));
    EXPECT_TRUE(
        vectorContainsMove(legalMoves, Move(Square(4, 3), Square(2, 2))));
    EXPECT_TRUE(
        vectorContainsMove(legalMoves, Move(Square(4, 4), Square(3, 4))));
    EXPECT_TRUE(
        vectorContainsMove(legalMoves, Move(Square(7, 3), Square(6, 3))));

    fen = "r3kb1r/pppN1ppp/2n1pB2/8/2B3b1/2N5/PPpQ1PPP/R3K2R b KQkq - 0 10";
    position.loadFEN(fen);

    legalMoves = validator.getLegalMoves(BLACK);

    EXPECT_TRUE(
        vectorContainsMove(legalMoves, Move(Square(6, 2), Square(7, 2),
                                            ColoredPiece(BLACK, QUEEN))));
    EXPECT_TRUE(
        vectorContainsMove(legalMoves, Move(Square(6, 2), Square(7, 2),
                                            ColoredPiece(BLACK, ROOK))));
    EXPECT_TRUE(
        vectorContainsMove(legalMoves, Move(Square(6, 2), Square(7, 2),
                                            ColoredPiece(BLACK, BISHOP))));
    EXPECT_TRUE(
        vectorContainsMove(legalMoves, Move(Square(6, 2), Square(7, 2),
                                            ColoredPiece(BLACK, KNIGHT))));
    EXPECT_FALSE(
        vectorContainsMove(legalMoves, Move(Square(6, 2), Square(7, 2),
                                            ColoredPiece(BLACK, PAWN))));
}