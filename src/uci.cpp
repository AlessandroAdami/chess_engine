#include "uci.h"
#include "engine.h"
#include <iostream>
#include <sstream>

const std::string START_FEN =
    "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";

void parsePositionCommand(const std::string &line, Position &pos) {
    if (line.find("startpos") != std::string::npos) {
        pos.loadFEN(START_FEN);
        size_t movesIdx = line.find("moves");
        if (movesIdx != std::string::npos) {
            std::istringstream iss(line.substr(movesIdx + 6));
            std::string moveStr;
            while (iss >> moveStr) {
                Move move = pos.moveParser.uciToMove(moveStr);
                pos.moveMaker.makeLegalMove(move);
                pos.printBoard();
            }
        }
    }
}

void uciLoop() {
    Position position;
    Engine engine(&position);
    std::string line;

    while (std::getline(std::cin, line)) {
        if (line == "uci") {
            std::cout << "id name SimpleEngine\n";
            std::cout << "id author Lextraz\n";
            std::cout << "uciok\n";
        } else if (line == "isready") {
            std::cout << "readyok\n";
        } else if (line.rfind("position", 0) == 0) {
            parsePositionCommand(line, position);
        } else if (line.rfind("go", 0) == 0) {
            int movetime = 1000; // default one second
            std::istringstream iss(line);
            std::string token;
            while (iss >> token) {
                if (token == "movetime" && iss >> token) {
                    movetime = std::stoi(token);
                }
            }
            Move best = engine.getBestMoveWithTimeLimit(movetime);
            std::cout << "bestmove " << best.toUCI() << "\n";
        } else if (line == "quit") {
            break;
        } else if (line == "ucinewgame") {
            position.loadFEN(START_FEN);
        }
    }
}

int main() {
    uciLoop();
    return 0;
}
