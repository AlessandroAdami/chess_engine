#include "move_parser.h"
#include "position.h"
#include "engine.h"
#include <iostream>
#include <sstream>
#include <string>
#include <vector>

//TODO: fix undo and redom move behavior with engine active

int main() {
    Position position;
    position.printBoard();
    Engine engine(&position);
    bool activeEngine = false;
    Color engineColor;

    while (true) {
        if (activeEngine && position.getTurn() == engineColor) {
            std::cout << "Engine is thinking";
            Move move = engine.getBestMove();
            position.makeMove(move);
            position.printBoard();
            continue;
        }
        std::string moveStr;
        std::cout << "Enter:    - move (e.g. 'e4', 'nxf6', 'o-o', 'd1=q')\n"
                  << "          - 'u' to undo move\n"
                  << "          - 'r' to redo move\n"
                  << "          - 'e' to activate engine for current color\n"
                  << "          - 'q' to quit\n"
                  << "Your move: ";
        std::getline(std::cin, moveStr);
        if (moveStr == "q")
            break;

        try {
            if (moveStr.length() == 1 && moveStr[0] == 'u') {
                position.unmakeMove();
            } else if (moveStr.length() == 1 && moveStr[0] == 'r') {
                position.remakeMove();
            } else if (moveStr.length() == 1 && moveStr[0] == 'e') {
                activeEngine = true;
                engineColor = position.getTurn();
                continue;
            } else {
                position.makeMoveFromString(moveStr);
            }
            position.printBoard();

        } catch (const std::exception &e) {
            std::cerr << "Invalid move string: " << e.what() << "\n";
        }
    }
    return 0;
}
