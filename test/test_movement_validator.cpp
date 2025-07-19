#include "../include/movement_validator.h"
#include "../include/position.h"
#include <gtest/gtest.h>

TEST(MovementValidatorTest, PawnGoodMovement) {
    Position position;
    position.loadFEN(
        "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1");

    MovementValidator validator(&position);

    Move pushOne{6, 4, 5, 4};
    EXPECT_TRUE(validator.isValidMove(pushOne));

    Move pushTwo{6, 4, 4, 4};
    EXPECT_TRUE(validator.isValidMove(pushTwo));

    position.loadFEN(
        "rnbqkbnr/ppp1p1pp/8/3p1p2/3PP3/8/PPP2PPP/RNBQKBNR w KQkq - 0 3");

    Move captureLeft{4, 4, 3, 3};
    EXPECT_TRUE(validator.isValidMove(captureLeft));

    Move captureRight{4, 4, 3, 5};
    EXPECT_TRUE(validator.isValidMove(captureRight));

    position.loadFEN(
        "r1bqkbnr/ppp3Pp/2n1p3/3p4/8/8/PPPP1PPP/RNBQKBNR w KQkq - 0 5");

    Move promoteQueen{1, 6, 0, 7, ColoredPiece(WHITE, QUEEN)};
    EXPECT_TRUE(validator.isValidMove(promoteQueen));

    Move promoteRook{1, 6, 0, 7, ColoredPiece(WHITE, ROOK)};
    EXPECT_TRUE(validator.isValidMove(promoteRook));

    Move promoteBishop{1, 6, 0, 7, ColoredPiece(WHITE, BISHOP)};
    EXPECT_TRUE(validator.isValidMove(promoteBishop));

    Move promoteKnight{1, 6, 0, 7, ColoredPiece(WHITE, KNIGHT)};
    EXPECT_TRUE(validator.isValidMove(promoteKnight));

    position.loadFEN(
        "rnbqkbnr/ppp1p1pp/8/3pPp2/8/8/PPPP1PPP/RNBQKBNR w KQkq f6 0 3");

    Move enPassant{3, 4, 2, 5};
    EXPECT_TRUE(validator.isValidMove(enPassant));
}

TEST(MovementValidatorTest, PawnBadMovement) {
    Position position;
    position.loadFEN(
        "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1");

    MovementValidator validator(&position);

    Move randomBadMove{6, 4, 0, 0};
    EXPECT_FALSE(validator.isValidMove(randomBadMove));

    position.loadFEN(
        "r1bqkbnr/ppp1p1pp/2n5/3pPp2/8/2N5/PPPP1PPP/R1BQKBNR w KQkq - 2 4");

    Move enPassantDelayed{3, 4, 2, 5};
    EXPECT_FALSE(validator.isValidMove(enPassantDelayed));

    position.loadFEN(
        "rnbqkbnr/pppp1ppp/8/4p3/4P3/8/PPPP1PPP/RNBQKBNR w KQkq - 0 2");

    Move pawnCaptureForward{4, 4, 3, 4};
    EXPECT_FALSE(validator.isValidMove(pawnCaptureForward));

    position.loadFEN(
        "r3kb1r/pppN1ppp/2n1pB2/8/2B3b1/2N5/PPpQ1PPP/R3K2R b KQkq - 0 10");

    Move sidewaysNoCapture{6, 2, 7, 3};
    EXPECT_FALSE(validator.isValidMove(sidewaysNoCapture));

    Move lastRankNoPromotion{6, 2, 7, 2};
    EXPECT_FALSE(validator.isValidMove(lastRankNoPromotion));
}

TEST(MovementValidatorTest, BishopGoodMovement) {
    Position position;
    position.loadFEN(
        "rnbqkb1r/pppp1ppp/5n2/4p3/2B1P3/8/PPPP1PPP/RNBQK1NR w KQkq - 2 3");

    MovementValidator validator(&position);

    Move upRight{4, 2, 1, 5};
    EXPECT_TRUE(validator.isValidMove(upRight));

    Move upLeft{4, 2, 3, 1};
    EXPECT_TRUE(validator.isValidMove(upLeft));

    Move downRight{4, 2, 7, 5};
    EXPECT_TRUE(validator.isValidMove(downRight));

    Move downLeft{4, 2, 5, 1};
    EXPECT_TRUE(validator.isValidMove(downLeft));
}

TEST(MovementValidatorTest, BishopBadMovement) {
    Position position;
    position.loadFEN(
        "r1bqkb1r/ppp2ppp/2np1n2/4p3/P1B1P3/1P6/2PP1PPP/RNBQK1NR w KQkq - 0 5");

    MovementValidator validator(&position);

    Move randomBadMove{4, 2, 0, 0};
    EXPECT_FALSE(validator.isValidMove(randomBadMove));

    Move travelThroughEnemyPiece{4, 2, 0, 6};
    EXPECT_FALSE(validator.isValidMove(travelThroughEnemyPiece));

    Move travelThroughFriendPiece{4, 2, 6, 0};
    EXPECT_FALSE(validator.isValidMove(travelThroughFriendPiece));

    Move captureFriendPiece{4, 2, 5, 1};
    EXPECT_FALSE(validator.isValidMove(captureFriendPiece));
}

TEST(MovementValidatorTest, RookGoodMovement) {
    Position position;
    position.loadFEN(
        "r1bqkb1r/1ppp1ppp/2n1pn2/p2R4/P7/8/1PPPPPPP/1NBQKBNR w Kkq - 0 5");

    MovementValidator validator(&position);

    Move up{3, 3, 1, 3};
    EXPECT_TRUE(validator.isValidMove(up));

    Move down{3, 3, 5, 3};
    EXPECT_TRUE(validator.isValidMove(down));

    Move right{3, 3, 3, 7};
    EXPECT_TRUE(validator.isValidMove(right));

    Move left{3, 3, 3, 0};
    EXPECT_TRUE(validator.isValidMove(left));
}

TEST(MovementValidatorTest, RookBadMovement) {
    Position position;
    position.loadFEN(
        "r1bqk2r/1ppp1ppp/2n1pn2/p1bR4/P2P4/8/1PP1PPPP/1NBQKBNR w Kkq - 1 6");

    MovementValidator validator(&position);

    Move randomBadMove{3, 3, 0, 0};
    EXPECT_FALSE(validator.isValidMove(randomBadMove));

    Move travelThroughEnemyPiece{3, 3, 0, 3};
    EXPECT_FALSE(validator.isValidMove(travelThroughEnemyPiece));

    Move travelThroughFriendPiece{3, 3, 6, 3};
    EXPECT_FALSE(validator.isValidMove(travelThroughFriendPiece));

    Move captureFriendPiece{3, 3, 4, 3};
    EXPECT_FALSE(validator.isValidMove(captureFriendPiece));
}

TEST(MovementValidatorTest, QueenGoodMovement) {
    Position position;
    position.loadFEN(
        "rnbqkb1r/pppp1ppp/5n2/4p3/2Q1P3/8/PPPP1PPP/RNBQK1NR w KQkq - 2 3");

    MovementValidator validator(&position);

    Move upRight{4, 2, 1, 5};
    EXPECT_TRUE(validator.isValidMove(upRight));

    Move upLeft{4, 2, 3, 1};
    EXPECT_TRUE(validator.isValidMove(upLeft));

    Move downRight{4, 2, 7, 5};
    EXPECT_TRUE(validator.isValidMove(downRight));

    Move downLeft{4, 2, 5, 1};
    EXPECT_TRUE(validator.isValidMove(downLeft));

    position.loadFEN(
        "r1bqkb1r/1ppp1ppp/2n1pn2/p2Q4/P7/8/1PPPPPPP/1NBQKBNR w Kkq - 0 5");

    Move up{3, 3, 1, 3};
    EXPECT_TRUE(validator.isValidMove(up));

    Move down{3, 3, 5, 3};
    EXPECT_TRUE(validator.isValidMove(down));

    Move right{3, 3, 3, 7};
    EXPECT_TRUE(validator.isValidMove(right));

    Move left{3, 3, 3, 0};
    EXPECT_TRUE(validator.isValidMove(left));
}

TEST(MovementValidatorTest, QueenBadMovement) {
    Position position;
    position.loadFEN(
        "r1bqk2r/1ppp1ppp/2n1pn2/p1bQ4/P2P4/8/1PP1PPPP/1NBQKBNR w Kkq - 1 6");

    MovementValidator validator(&position);

    Move randomBadMove{3, 3, 0, 0};
    EXPECT_FALSE(validator.isValidMove(randomBadMove));

    Move travelThroughEnemyPieceRook{3, 3, 0, 3};
    EXPECT_FALSE(validator.isValidMove(travelThroughEnemyPieceRook));

    Move travelThroughFriendPieceRook{3, 3, 6, 3};
    EXPECT_FALSE(validator.isValidMove(travelThroughFriendPieceRook));

    Move captureFriendPiece{3, 3, 4, 3};
    EXPECT_FALSE(validator.isValidMove(captureFriendPiece));

    position.loadFEN(
        "r1bqkb1r/ppp2ppp/2np1n2/4p3/P1Q1P3/1P6/2PP1PPP/RNBBK1NR w KQkq - 0 5");

    Move travelThroughEnemyPieceBishop{4, 2, 0, 6};
    EXPECT_FALSE(validator.isValidMove(travelThroughEnemyPieceBishop));

    Move travelThroughFriendPieceBishop{4, 2, 6, 0};
    EXPECT_FALSE(validator.isValidMove(travelThroughFriendPieceBishop));
}

TEST(MovementValidatorTest, KnightGoodMovement) {
    Position position;
    position.loadFEN(
        "rnbqkbnr/pppppppp/8/8/4N3/8/PPP1P1PP/RNBQKB1R w KQkq - 0 1");

    MovementValidator validator(&position);

    Move upTwoRightOne{4, 4, 2, 5};
    EXPECT_TRUE(validator.isValidMove(upTwoRightOne));

    Move upOneRightTwo{4, 4, 3, 6};
    EXPECT_TRUE(validator.isValidMove(upOneRightTwo));

    Move downOneRightTwo{4, 4, 5, 6};
    EXPECT_TRUE(validator.isValidMove(downOneRightTwo));

    Move downTwoRightOne{4, 4, 6, 5};
    EXPECT_TRUE(validator.isValidMove(downTwoRightOne));

    Move downTwoLeftOne{4, 4, 6, 3};
    EXPECT_TRUE(validator.isValidMove(downTwoLeftOne));

    Move downOneLeftTwo{4, 4, 5, 2};
    EXPECT_TRUE(validator.isValidMove(downOneLeftTwo));

    Move upOneLeftTwo{4, 4, 3, 2};
    EXPECT_TRUE(validator.isValidMove(upOneLeftTwo));

    Move upTwoLeftOne{4, 4, 2, 3};
    EXPECT_TRUE(validator.isValidMove(upTwoLeftOne));
}

TEST(MovementValidatorTest, KnightBadMovement) {
    Position position;
    position.loadFEN(
        "rnbqkbnr/pppppppp/8/8/4N3/8/PPPPPPsPP/RNBQKB1R w KQkq - 0 1");

    MovementValidator validator(&position);

    Move randomBadMove{4, 4, 0, 0};
    EXPECT_FALSE(validator.isValidMove(randomBadMove));

    Move captureFriendPiece{4, 4, 6, 5};
    EXPECT_FALSE(validator.isValidMove(captureFriendPiece));
}

TEST(MovementValidatorTest, CastlingGoodMove) {
    Position position;
    position.loadFEN("rnbqk2r/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR b KQkq - 0 1");

    MovementValidator validator(&position);

    Move shortCastle{0, 4, 0, 6};
    EXPECT_TRUE(validator.isValidMove(shortCastle));

    position.loadFEN("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/R3KBNR w KQkq - 0 1");

    Move longCastle{7, 4, 7, 2};
    EXPECT_TRUE(validator.isValidMove(longCastle));
}

TEST(MovementValidatorTest, CastlingBadMove) {
    Position position;
    position.loadFEN(
        "rnb1kbnr/pppppppp/8/8/2q1P3/5N2/PPPP1PPP/RNBQK2R w KQkq - 0 1");

    MovementValidator validator(&position);

    Move shortCastleThroughCheck{7, 4, 7, 6};
    EXPECT_FALSE(validator.isValidMove(shortCastleThroughCheck));

    position.loadFEN(
        "r3kbnr/pppp1ppp/8/6B1/8/8/PPPPPPPP/RNBQK1NR b KQkq - 0 1");

    Move longCastleThroughCheck{0, 4, 0, 2};
    EXPECT_FALSE(validator.isValidMove(longCastleThroughCheck));

    position.loadFEN(
        "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR b KQkq - 0 1");

    Move shortCastleThroughPieces{0, 4, 0, 6};
    EXPECT_FALSE(validator.isValidMove(shortCastleThroughPieces));

    position.loadFEN(
        "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1");

    Move longCastleThroughPieces{7, 4, 7, 2};
    EXPECT_FALSE(validator.isValidMove(longCastleThroughPieces));

    position.loadFEN("rnbqk2r/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR b KQq - 0 1");

    Move shortCastleRookHasMoved{0, 4, 0, 6};
    EXPECT_FALSE(validator.isValidMove(shortCastleRookHasMoved));

    position.loadFEN("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/R3KBNR w kq - 0 1");

    Move longCastleKingHasMoved{7, 4, 7, 2};
    EXPECT_FALSE(validator.isValidMove(longCastleKingHasMoved));
}

TEST(MovementValidatorTest, IntoCheckBadMove) {
    Position position;
    std::string fen =
        "rnbqkbnr/pppp1Bpp/8/4p3/4P3/8/PPPP1PPP/RNBQK1NR b KQkq - 1 1";
    position.loadFEN(fen);

    MovementValidator validator(&position);

    Move d6IntoCheck{1, 3, 2, 3};
    EXPECT_FALSE(validator.isValidMove(d6IntoCheck));
    EXPECT_EQ(position.getFEN(), fen);

    fen = "rnbqk1nr/pppp1ppp/8/4p3/1b2P3/5N2/PPPP1PPP/RNBQKB1R w KQkq - 2 3";
    position.loadFEN(fen);

    Move d4IntoCheck{6, 3, 4, 3};
    EXPECT_FALSE(validator.isValidMove(d4IntoCheck));
    EXPECT_EQ(position.getFEN(), fen);

    fen = "rkb2b1r/pppppppp/4qn2/4B3/8/8/PPPP1PPP/RNBQKBNR w KQkq - 1 2";
    position.loadFEN(fen);

    Move bishopCheckIntoCheck{3, 4, 1, 2};
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

    EXPECT_FALSE(vectorContainsMove(legalMoves, Move{0, 0, 0, 0}));
    EXPECT_FALSE(vectorContainsMove(legalMoves, Move{1, 3, 3, 3}));
    EXPECT_FALSE(vectorContainsMove(legalMoves, Move{0, 1, 2, 2}));

    EXPECT_TRUE(vectorContainsMove(legalMoves, Move{0, 4, 1, 5}));
    EXPECT_TRUE(vectorContainsMove(legalMoves, Move{0, 4, 1, 4}));

    fen = "r1bqk2r/pp1pppbp/2n2np1/8/3NP3/2N5/PPP1BPPP/R1BQK2R w KQkq - 3 7";
    position.loadFEN(fen);

    legalMoves = validator.getLegalMoves(WHITE);

    EXPECT_TRUE(vectorContainsMove(legalMoves, Move{7, 4, 7, 6}));
    EXPECT_TRUE(vectorContainsMove(legalMoves, Move{4, 3, 2, 2}));
    EXPECT_TRUE(vectorContainsMove(legalMoves, Move{4, 4, 3, 4}));
    EXPECT_TRUE(vectorContainsMove(legalMoves, Move{7, 3, 6, 3}));

    fen = "r3kb1r/pppN1ppp/2n1pB2/8/2B3b1/2N5/PPpQ1PPP/R3K2R b KQkq - 0 10";
    position.loadFEN(fen);

    legalMoves = validator.getLegalMoves(BLACK);

    EXPECT_TRUE(vectorContainsMove(
        legalMoves, Move{6, 2, 7, 2, ColoredPiece(BLACK, QUEEN)}));
    EXPECT_TRUE(vectorContainsMove(
        legalMoves, Move{6, 2, 7, 2, ColoredPiece(BLACK, ROOK)}));
    EXPECT_TRUE(vectorContainsMove(
        legalMoves, Move{6, 2, 7, 2, ColoredPiece(BLACK, BISHOP)}));
    EXPECT_TRUE(vectorContainsMove(
        legalMoves, Move{6, 2, 7, 2, ColoredPiece(BLACK, KNIGHT)}));
    EXPECT_FALSE(vectorContainsMove(
        legalMoves, Move{6, 2, 7, 2, ColoredPiece(BLACK, PAWN)}));
}