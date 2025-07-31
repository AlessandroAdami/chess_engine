#include <iostream>
#include <string>
#include "engine.h"
#include "position.h"

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
            // Handle setting position
            // e.g., "position startpos moves e2e4 e7e5"
        } else if (line.rfind("go", 0) == 0) {
            // Start search, e.g. call engine.getBestMove()
            Move best = engine.getBestMove();
            std::cout << "bestmove " << best.toUCI() << "\n";
        } else if (line == "quit") {
            break;
        }
    }
}
