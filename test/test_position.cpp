#include "../include/position.h"
#include "../include/types.h"
#include "zobrist.h"
#include <gtest/gtest.h>

TEST(PositionTest, LoadFenFromStartingPosition) {
    Position position;
    position.loadFEN(
        "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1");

    for (Color color : {WHITE, BLACK}) {
        int row = (color == WHITE) ? 7 : 0;
        EXPECT_EQ(position.getPiece(Square(row, 0)), ColoredPiece(color, ROOK));
        EXPECT_EQ(position.getPiece(Square(row, 1)),
                  ColoredPiece(color, KNIGHT));
        EXPECT_EQ(position.getPiece(Square(row, 2)),
                  ColoredPiece(color, BISHOP));
        EXPECT_EQ(position.getPiece(Square(row, 3)),
                  ColoredPiece(color, QUEEN));
        EXPECT_EQ(position.getPiece(Square(row, 4)), ColoredPiece(color, KING));
        EXPECT_EQ(position.getPiece(Square(row, 5)),
                  ColoredPiece(color, BISHOP));
        EXPECT_EQ(position.getPiece(Square(row, 6)),
                  ColoredPiece(color, KNIGHT));
        EXPECT_EQ(position.getPiece(Square(row, 7)), ColoredPiece(color, ROOK));
    }

    for (int i = 0; i < 8; i++) {
        EXPECT_EQ(position.getPiece(Square(6, i)), ColoredPiece(WHITE, PAWN));
        EXPECT_EQ(position.getPiece(Square(1, i)), ColoredPiece(BLACK, PAWN));

        for (int j = 2; j < 6; j++) {
            EXPECT_EQ(position.getPiece(Square(j, i)), NO_COLORED_PIECE);
        }
    }

    EXPECT_EQ(position.getEnpassantSquare(), (INVALID_SQUARE));
    EXPECT_EQ(position.getTurn(), WHITE);
    EXPECT_EQ(position.getCastleState(WHITE), KING_SIDE | QUEEN_SIDE);
    EXPECT_EQ(position.getCastleState(BLACK), KING_SIDE | QUEEN_SIDE);
}

TEST(PositionTest, LoadFenFromRandomPosition) {
    Position position;
    position.loadFEN(
        "r4rk1/1pp2pp1/2np1q1p/p1b1p3/2P3b1/2NPPNP1/PP3PBP/R2Q1RK1 w - - 0 11");

    EXPECT_EQ(position.getPiece(Square(0, 0)), ColoredPiece(BLACK, ROOK));
    EXPECT_EQ(position.getPiece(Square(0, 5)), ColoredPiece(BLACK, ROOK));
    EXPECT_EQ(position.getPiece(Square(0, 6)), ColoredPiece(BLACK, KING));
    EXPECT_EQ(position.getPiece(Square(2, 2)), ColoredPiece(BLACK, KNIGHT));
    EXPECT_EQ(position.getPiece(Square(2, 5)), ColoredPiece(BLACK, QUEEN));
    EXPECT_EQ(position.getPiece(Square(3, 4)), ColoredPiece(BLACK, PAWN));
    EXPECT_EQ(position.getPiece(Square(4, 2)), ColoredPiece(WHITE, PAWN));
    EXPECT_EQ(position.getPiece(Square(5, 2)), ColoredPiece(WHITE, KNIGHT));
    EXPECT_EQ(position.getPiece(Square(7, 3)), ColoredPiece(WHITE, QUEEN));
    EXPECT_EQ(position.getPiece(Square(6, 6)), ColoredPiece(WHITE, BISHOP));
    EXPECT_EQ(position.getPiece(Square(7, 6)), ColoredPiece(WHITE, KING));
    EXPECT_EQ(position.getPiece(Square(7, 5)), ColoredPiece(WHITE, ROOK));
}

TEST(PositionTest, MakeMoveIllegal) {
    Position position;
    std::string fen =
        "r1bqkbnr/pp1ppppp/2n5/2p5/4P3/5N2/PPPP1PPP/RNBQKB1R w KQkq - 2 3";
    position.loadFEN(fen);

    Move illegalRandomMove(Square(0, 0), Square(7, 7));
    position.moveMaker.makeMove(illegalRandomMove);

    EXPECT_EQ(position.getFEN(), fen);

    fen = "r1bqk1nr/pp1p1ppp/2n1p3/8/1bBNP3/8/PPP2PPP/RNBQK2R w KQkq - 2 6";
    position.loadFEN(fen);

    Move illegalCastlingMove(Square(7, 4), Square(7, 6));
    position.moveMaker.makeMove(illegalCastlingMove);

    EXPECT_EQ(position.getFEN(), fen);
}

TEST(PositionTest, MakeMoveLegal) {
    Position position;
    std::string fen =
        "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";
    position.loadFEN(fen);

    Move e4(Square(6, 4), Square(4, 4));
    position.moveMaker.makeMove(e4);

    fen = "rnbqkbnr/pppppppp/8/8/4P3/8/PPPP1PPP/RNBQKBNR b KQkq e3 0 1";

    EXPECT_EQ(position.getFEN(), fen);

    Move d5(Square(1, 3), Square(3, 3));
    position.moveMaker.makeMove(d5);

    fen = "rnbqkbnr/ppp1pppp/8/3p4/4P3/8/PPPP1PPP/RNBQKBNR w KQkq d6 0 2";

    EXPECT_EQ(position.getFEN(), fen);

    fen = "rnbqkbnr/ppp1p1pp/8/3pPp2/8/8/PPPP1PPP/RNBQKBNR w KQkq f6 0 3";
    position.loadFEN(fen);

    Move exf6EnPassant(Square(3, 4), Square(2, 5));
    position.moveMaker.makeMove(exf6EnPassant);

    fen = "rnbqkbnr/ppp1p1pp/5P2/3p4/8/8/PPPP1PPP/RNBQKBNR b KQkq - 0 3";

    EXPECT_EQ(position.getFEN(), fen);

    fen = "rnbqkb1r/ppp2ppp/3p1n2/4p2Q/2B1P3/8/PPPP1PPP/RNB1K1NR w KQkq - 0 4";
    position.loadFEN(fen);

    Move qxf7(Square(3, 7), Square(1, 5));
    position.moveMaker.makeMove(qxf7);

    fen = "rnbqkb1r/ppp2Qpp/3p1n2/4p3/2B1P3/8/PPPP1PPP/RNB1K1NR b KQkq - 0 4";

    EXPECT_EQ(position.getFEN(), fen);

    fen = "rnbqkbnr/pP3ppp/8/8/4p3/8/PPPP1PPP/RNBQKBNR w KQkq - 0 5";
    position.loadFEN(fen);

    Move bxa8q(Square(1, 1), Square(0, 0), ColoredPiece(WHITE, QUEEN));
    position.moveMaker.makeMove(bxa8q);

    fen = "Qnbqkbnr/p4ppp/8/8/4p3/8/PPPP1PPP/RNBQKBNR b KQk - 0 5";

    EXPECT_EQ(position.getFEN(), fen);
}

TEST(PositionTest, UndoRedoMove) {
    Position position;
    std::string fen =
        "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";

    Move e4(Square(6, 4), Square(4, 4));
    position.moveMaker.makeMove(e4);

    fen = "rnbqkbnr/pppppppp/8/8/4P3/8/PPPP1PPP/RNBQKBNR b KQkq e3 0 1";

    EXPECT_EQ(position.getFEN(), fen);

    position.moveMaker.unmakeMove();

    fen = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";
    EXPECT_EQ(position.getFEN(), fen);

    position.moveMaker.remakeMove();

    fen = "rnbqkbnr/pppppppp/8/8/4P3/8/PPPP1PPP/RNBQKBNR b KQkq e3 0 1";

    EXPECT_EQ(position.getFEN(), fen);
}

TEST(PositionTest, GetMoveContext) {
    Position position;
    std::string fen =
        "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";

    Move e4(Square(6, 4), Square(4, 4));
    MoveContext actualContext = position.getMoveContext(e4);

    MoveContext expectedContext = {
        e4,
        ColoredPiece(WHITE, PAWN),
        NO_COLORED_PIECE,
        INVALID_SQUARE,
        {KING_SIDE | QUEEN_SIDE, KING_SIDE | QUEEN_SIDE},
        0,
        1,
        WHITE,
        false,
        false,
        false,
        actualContext.previousHash};

    EXPECT_EQ(actualContext, expectedContext);

    fen = "r1bqkbnr/pp1p1ppp/2n1p3/2p5/2B1P3/5N2/PPPP1PPP/RNBQK2R w KQkq - 2 4";

    position.loadFEN(fen);

    Move shortCastle(Square(7, 4), Square(7, 6));
    actualContext = position.getMoveContext(shortCastle);

    expectedContext = {shortCastle,
                       ColoredPiece(WHITE, KING),
                       NO_COLORED_PIECE,
                       INVALID_SQUARE,
                       {KING_SIDE | QUEEN_SIDE, KING_SIDE | QUEEN_SIDE},
                       2,
                       4,
                       WHITE,
                       false,
                       false,
                       true,
                       actualContext.previousHash};

    EXPECT_EQ(actualContext, expectedContext);

    fen = "rnbqkbnr/ppp1p1pp/8/3pPp2/8/8/PPPP1PPP/RNBQKBNR w KQkq f6 0 3";

    position.loadFEN(fen);

    Move exf6EnPassant(Square(3, 4), Square(2, 5));
    actualContext = position.getMoveContext(exf6EnPassant);

    expectedContext = {exf6EnPassant,
                       ColoredPiece(WHITE, PAWN),
                       ColoredPiece(BLACK, PAWN),
                       Square(2, 5),
                       {KING_SIDE | QUEEN_SIDE, KING_SIDE | QUEEN_SIDE},
                       0,
                       3,
                       WHITE,
                       false,
                       true,
                       false,
                       actualContext.previousHash};

    EXPECT_EQ(actualContext, expectedContext);
}

TEST(ZobristHashTest, HashStabilityUnderReversibleMove) {
    Position pos;
    uint64_t initialHash = pos.zobristHash;

    Move move(Square(6, 4), Square(5, 4));

    ASSERT_EQ(pos.getPiece(move.from).piece, PAWN);

    pos.moveMaker.makeMove(move);
    uint64_t afterMoveHash = pos.zobristHash;

    pos.moveMaker.unmakeMove();
    uint64_t finalHash = pos.zobristHash;

    EXPECT_NE(initialHash, afterMoveHash);
    EXPECT_EQ(initialHash, finalHash);
}

TEST(ZobristHashTest, PerComponentXorReversibility) {
    Zobrist zobrist = Zobrist();
    uint64_t h = 0;

    int idx = pieceIndex(ColoredPiece(WHITE, ROOK));
    int sq = 7 * 8 + 0;
    uint64_t pieceKey = zobrist.pieceKeys[idx][sq];
    h ^= pieceKey;
    h ^= pieceKey;
    EXPECT_EQ(h, 0);

    for (int i = 0; i < 16; ++i) {
        uint64_t hash = zobrist.castlingRightsKey[i];
        uint64_t state = 0;
        state ^= hash;
        state ^= hash;
        EXPECT_EQ(state, 0);
    }

    for (int f = 0; f < 8; ++f) {
        uint64_t fileKey = zobrist.enPassantFileKey[f];
        uint64_t test = 0;
        test ^= fileKey;
        test ^= fileKey;
        EXPECT_EQ(test, 0);
    }
}

TEST(ZobristHashTest, IdenticalPositionsHaveSameHash) {
    Position pos1;
    Position pos2;

    ASSERT_EQ(pos1.getFEN(), pos2.getFEN())
        << "Initial positions should be the same";

    EXPECT_EQ(pos1.zobristHash, pos2.zobristHash)
        << "Hashes differ for identical positions";

    Move e4(Square(6, 4), Square(4, 4));
    pos1.moveMaker.makeLegalMove(e4);
    pos2.moveMaker.makeLegalMove(e4);

    EXPECT_EQ(pos1.zobristHash, pos2.zobristHash)
        << "Hashes differ for identical positions";
}

TEST(ZobristHashTest, DifferentPositionsHaveDifferentHash) {
    Position position;
    std::vector<Move> moves =
        position.movementValidator.getLegalMoves(position.getTurn());
    ASSERT_FALSE(moves.empty()) << "No legal moves available";

    Move move = moves[0];
    position.moveMaker.makeMove(move);

    uint64_t modifiedHash = position.zobristHash;
    Position original;

    EXPECT_NE(modifiedHash, original.zobristHash)
        << "Modified position should have different hash";
}

TEST(PositionTest, PiecesLists) {
    Position position;

    Move e4(Square(6, 4), Square(4, 4));

    std::unordered_set<Square> whitePiecesSquares =
        position.getPiecesSquares(WHITE);

    EXPECT_TRUE(whitePiecesSquares.contains(Square(7, 7)));
    EXPECT_TRUE(whitePiecesSquares.contains(Square(6, 4)));
    EXPECT_FALSE(whitePiecesSquares.contains(Square(0, 0)));
    EXPECT_FALSE(whitePiecesSquares.contains(Square(1, 3)));

    std::unordered_set<Square> blackPiecesSquares =
        position.getPiecesSquares(BLACK);
    EXPECT_TRUE(blackPiecesSquares.contains(Square(0, 0)));
    EXPECT_TRUE(blackPiecesSquares.contains(Square(1, 3)));
    EXPECT_FALSE(blackPiecesSquares.contains(Square(7, 7)));
    EXPECT_FALSE(blackPiecesSquares.contains(Square(6, 4)));

    position.loadFEN(
        "r1bqkb1r/pp1ppppp/5n2/2p5/3nP3/2N2N2/PPPP1PPP/R1BQKB1R w KQkq - 0 1");

    whitePiecesSquares = position.getPiecesSquares(WHITE);
    EXPECT_TRUE(whitePiecesSquares.contains(Square(5, 5)));
    EXPECT_TRUE(whitePiecesSquares.contains(Square(5, 2)));
    blackPiecesSquares = position.getPiecesSquares(BLACK);
    EXPECT_TRUE(blackPiecesSquares.contains(Square(4, 3)));
    EXPECT_TRUE(blackPiecesSquares.contains(Square(3, 2)));

    Move nxd4(Square(5, 5), Square(4, 3));
    position.moveMaker.makeLegalMove(nxd4);
    whitePiecesSquares = position.getPiecesSquares(WHITE);
    EXPECT_TRUE(whitePiecesSquares.contains(Square(4, 3)));
    EXPECT_FALSE(whitePiecesSquares.contains(Square(5, 5)));
    blackPiecesSquares = position.getPiecesSquares(BLACK);
    EXPECT_FALSE(blackPiecesSquares.contains(Square(4, 3)));

    position.loadFEN(
        "rnbqkbnr/ppp1p1pp/8/3pPp2/8/8/PPPP1PPP/RNBQKBNR w KQkq f6 0 3");
    whitePiecesSquares = position.getPiecesSquares(WHITE);
    EXPECT_TRUE(whitePiecesSquares.contains(Square(3, 4)));
    blackPiecesSquares = position.getPiecesSquares(BLACK);
    EXPECT_TRUE(blackPiecesSquares.contains(Square(3, 5)));
    Move exf6EnPassant(Square(3, 4), Square(2, 5));
    position.moveMaker.makeLegalMove(exf6EnPassant);
    whitePiecesSquares = position.getPiecesSquares(WHITE);
    EXPECT_TRUE(whitePiecesSquares.contains(Square(2, 5)));
    EXPECT_FALSE(whitePiecesSquares.contains(Square(3, 4)));
    blackPiecesSquares = position.getPiecesSquares(BLACK);
    EXPECT_FALSE(blackPiecesSquares.contains(Square(3, 5)));
}