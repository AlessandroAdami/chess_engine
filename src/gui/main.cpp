#include <SFML/Graphics.hpp>
#include <iostream>
#include <map>
#include <string>
#include "../../include/position.h"
#include "../../include/types.h"

const int squareSize = 150;
const int boardSize = 8;
const int boardPixelSize = squareSize * boardSize;
const int sidebarWidth = 200; // new sidebar for turn display

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
            sprite.setPosition(rank * squareSize, file * squareSize);
            sprite.setScale(
                (float)squareSize / sprite.getTexture()->getSize().x,
                (float)squareSize / sprite.getTexture()->getSize().y
            );
            window.draw(sprite);
        }
    }
}

void drawSidebar(sf::RenderWindow& window, const Position& position) {
    sf::RectangleShape sidebar(sf::Vector2f(sidebarWidth, boardPixelSize));
    sidebar.setPosition(boardPixelSize, 0);
    sidebar.setFillColor(sf::Color(50, 50, 50));
    window.draw(sidebar);

    sf::RectangleShape turnIndicator(sf::Vector2f(10,boardPixelSize));
    turnIndicator.setFillColor(position.getTurn() == WHITE ? sf::Color::White : sf::Color::Black);
    turnIndicator.setPosition(boardPixelSize,0);

    window.draw(turnIndicator);
}

int main() {
    Position position;
    if (!loadTextures()) return 1;

    sf::RenderWindow window(
        sf::VideoMode(boardPixelSize + sidebarWidth, boardPixelSize),
        "Chess GUI", sf::Style::Default
    );
    window.setFramerateLimit(60);

    // Logical view now includes board + sidebar
    sf::View view(sf::FloatRect(0, 0, boardPixelSize + sidebarWidth, boardPixelSize));
    window.setView(view);

    sf::Vector2i selectedSquare(-1, -1);
    bool pieceSelected = false;

    while (window.isOpen()) {
        sf::Event event;
        while (window.pollEvent(event)) {
            if (event.type == sf::Event::Closed) {
                window.close();
            }
            else if (event.type == sf::Event::Resized) {
                float w = event.size.width;
                float h = event.size.height;
                float aspect = w / h;

                if (aspect > (float)(boardPixelSize + sidebarWidth) / boardPixelSize) {
                    view.setViewport({
                        (1.f - (boardPixelSize + sidebarWidth) / (h * aspect)) / 2.f,
                        0.f,
                        (boardPixelSize + sidebarWidth) / (h * aspect),
                        1.f
                    });
                } else {
                    view.setViewport({
                        0.f,
                        (1.f - boardPixelSize / (w / aspect)) / 2.f,
                        1.f,
                        boardPixelSize / (w / aspect)
                    });
                }
                window.setView(view);
            }
            else if (event.type == sf::Event::MouseButtonPressed) {
                if (event.mouseButton.button == sf::Mouse::Left) {
                    sf::Vector2f worldPos = window.mapPixelToCoords(
                        {event.mouseButton.x, event.mouseButton.y}, view);

                    int file = static_cast<int>(worldPos.x / squareSize);
                    int rank = static_cast<int>(worldPos.y / squareSize);

                    if (file < 0 || file >= 8 || rank < 0 || rank >= 8)
                        continue;

                    if (!pieceSelected) {
                        selectedSquare = {file, rank};
                        pieceSelected = true;
                    } else {
                        int fromFile = selectedSquare.x;
                        int fromRank = selectedSquare.y;
                        int toFile = file;
                        int toRank = rank;

                        Move move(Square(fromRank, fromFile), Square(toRank, toFile));

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
        window.setView(view);
        drawBoard(window);
        drawPieces(window, position);
        drawSidebar(window, position);
        window.display();
    }
    return 0;
}
