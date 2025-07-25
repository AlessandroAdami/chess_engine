#include "engine.h"
#include "move_parser.h"
#include "position.h"
#include <iostream>
#include <sstream>
#include <string>
#include <vector>

int main() {
    Position position;
    position.printBoard();
    Engine engine(&position);
    bool activeEngine = false;
    Color engineColor;
    bool enginePlay = false;

    while (true) {
        if (activeEngine && position.getTurn() == engineColor && enginePlay) {
            std::cout << "Engine is thinking ..." << std::endl;
            Move move = engine.getBestMove();
            std::cout << "Engine plays: \""
                      << position.moveParser.moveToString(move) << "\""
                      << std::endl;
            position.moveMaker.makeLegalMove(move);
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
                position.moveMaker.unmakeMove();
                enginePlay = false;
            } else if (moveStr.length() == 1 && moveStr[0] == 'r') {
                position.moveMaker.remakeMove();
                enginePlay = false;
            } else if (moveStr.length() == 1 && moveStr[0] == 'e') {
                activeEngine = true;
                engineColor = position.getTurn();
                enginePlay = true;
                continue;
            } else {
                position.moveMaker.makeMoveFromString(moveStr);
                enginePlay = true;
            }
            position.printBoard();

        } catch (const std::exception &e) {
            std::cerr << "Invalid move string: " << e.what() << "\n";
        }
    }
    return 0;
}
