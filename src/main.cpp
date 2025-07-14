#include "move_parser.h"
#include "position.h"
#include <iostream>
#include <sstream>
#include <string>
#include <vector>

int main() {
    Position board;
    board.printBoard();

    while (true) {
        std::string moveStr;
        std::cout << "Enter:    - move (e.g. 'e4', 'nxf6', 'o-o', 'd1=q')\n"
                  << "          - 'u' to undo move\n"
                  << "          - 'r' to redo move\n"
                  << "          - 'q' to quit\n"
                  << "Your move: ";
        std::getline(std::cin, moveStr);
        if (moveStr == "q")
            break;

        try {
            if (moveStr.length() == 1 && moveStr[0] == 'u') {
                board.undoMove();
            } else if (moveStr.length() == 1 && moveStr[0] == 'r') {
                board.redoMove();
            } else {
                board.makeMoveFromString(moveStr);
            }
            board.printBoard();

        } catch (const std::exception &e) {
            std::cerr << "Invalid move string: " << e.what() << "\n";
        }
    }
    return 0;
}
