#include <SFML/Graphics.hpp>
#include <iostream>
#include <map>
#include <string>
#include "../../include/position.h"
#include "../../include/types.h"

const int squareSize = 150;
const int boardSize = 8;

std::map<char, sf::Texture> pieceTextures;

std::string basePath = "./pieces/";

bool loadTextures() {
    std::map<char, std::string> files = {
        {'P', "wP.png"}, {'N', "wN.png"},
        {'B', "wB.png"}, {'R', "wR.png"},
        {'Q', "wQ.png"}, {'K', "wK.png"},
        {'p', "bP.png"}, {'n', "bN.png"},
        {'b', "bB.png"}, {'r', "bR.png"},
        {'q', "bQ.png"}, {'k', "bK.png"},
    };

    for (auto& [piece, path] : files) {
        sf::Texture tex;
        std::string fullPath = basePath + path;
        if (!tex.loadFromFile(fullPath)) {
            std::cerr << "Failed to load " << path << std::endl;
            return false;
        }
        pieceTextures[piece] = tex;
    }
    return true;
}

void drawBoard(sf::RenderWindow& window) {
    for (int rank = 0; rank < boardSize; ++rank) {
        for (int file = 0; file < boardSize; ++file) {
            sf::RectangleShape square(sf::Vector2f(squareSize, squareSize));
            square.setPosition(file * squareSize, (7 - rank) * squareSize);

            if ((file + rank) % 2 == 0)
                square.setFillColor(sf::Color(240, 217, 181)); // light
            else
                square.setFillColor(sf::Color(181, 136, 99));  // dark

            window.draw(square);
        }
    }
}

void drawPieces(sf::RenderWindow& window, const Position& position) {
    for (int rank = 0; rank < boardSize; ++rank) {
        for (int file = 0; file < boardSize; ++file) {
            ColoredPiece cp = position.getPiece(Square(file, rank)); 
            char piece = cp.toChar();
            if (piece == '.') continue;

            sf::Sprite sprite;
            sprite.setTexture(pieceTextures[piece]);
            sprite.setPosition(rank * squareSize,file * squareSize);
            sprite.setScale(
                (float)squareSize / sprite.getTexture()->getSize().x,
                (float)squareSize / sprite.getTexture()->getSize().y
            );
            window.draw(sprite);
        }
    }
}

int main() {
    Position position;
    if (!loadTextures()) return 1;

    sf::RenderWindow window(sf::VideoMode(squareSize * boardSize, squareSize * boardSize),
                            "Chess GUI");
    window.setFramerateLimit(60);


    sf::Vector2i selectedSquare(-1, -1);
    bool pieceSelected = false;

    while (window.isOpen()) {
        sf::Event event;
        while (window.pollEvent(event)) {
            if (event.type == sf::Event::Closed) {
                window.close();
            }
            else if (event.type == sf::Event::MouseButtonPressed) {
                if (event.mouseButton.button == sf::Mouse::Left) {
                    int file = event.mouseButton.x / squareSize;
                    int rank = event.mouseButton.y / squareSize;

                    if (!pieceSelected) {
                        // first click
                        selectedSquare = {file, rank};
                        pieceSelected = true;
                    } else {
                        // second click → attempt move
                        int fromFile = selectedSquare.x;
                        int fromRank = selectedSquare.y;
                        int toFile = file;
                        int toRank = rank;

                        Move move(Square(fromRank,fromFile),Square(toRank,toFile));

                        try {
                            position.moveMaker.makeMove(move);
                        } catch (const std::exception& e) {
                            std::cerr << "Invalid move: " << e.what() << std::endl;
                        }

                        pieceSelected = false;
                    }
                }
            }
        }

        window.clear();
        drawBoard(window);
        drawPieces(window, position);
        window.display();
    }
    return 0;
}
