#include "uci.h"
#include "engine.h"
#include <iostream>
#include <sstream>

const std::string fen =
    "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";

void parsePositionCommand(const std::string &line, Position &pos) {
    if (line.find("startpos") != std::string::npos) {
        pos.loadFEN(fen);
        size_t movesIdx = line.find("moves");
        if (movesIdx != std::string::npos) {
            std::istringstream iss(line.substr(movesIdx + 6));
            std::string moveStr;
            while (iss >> moveStr) {
                Move move = pos.moveParser.uciToMove(moveStr);
                pos.moveMaker.makeLegalMove(move);
            }
        }
    }
}

void uciLoop() {
    std::string line;
    Position position;
    Engine engine(&position);

    while (std::getline(std::cin, line)) {
        if (line == "uci") {
            std::cout << "id name YourEngineName\n";
            std::cout << "id author YourName\n";
            std::cout << "uciok\n";
        } else if (line == "isready") {
            std::cout << "readyok\n";
        } else if (line.rfind("position", 0) == 0) {
            parsePositionCommand(line, position);
        } else if (line.rfind("go", 0) == 0) {
            int movetime = 1000;
            std::istringstream iss(line);
            std::string token;
            while (iss >> token) {
                if (token == "movetime" && iss >> token) {
                    movetime = std::stoi(token);
                }
            }

            Move bestMove = engine.getBestMoveWithTimeLimit(movetime);
            std::cout << "bestmove " << bestMove.toUCI() << "\n";
        } else if (line == "quit") {
            break;
        }
    }
}
