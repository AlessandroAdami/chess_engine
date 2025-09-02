#include <SFML/Graphics.hpp>
#include <iostream>
#include <map>
#include <string>
#include <thread>
#include <atomic>
#include <optional>
#include <mutex>

#include "../../include/position.h"
#include "../../include/types.h"
#include "../../include/engine.h"

//TODO: add possible move highlights and piece selection
//TODO: add formatter again
sf::Vector2i selectedSquare(-1, -1); //TODO change to square

//Position position;
const int squareSize = 150;
const int boardSize = 8;
const int boardPixelSize = squareSize * boardSize;
const int sidebarWidth = 200;
sf::Color light = sf::Color(240, 217, 181);
sf::Color dark  = sf::Color(181, 136, 99);

std::map<char, sf::Texture> pieceTextures;
std::string basePath = "./pieces/";

bool engineForWhite = false;
bool engineForBlack = false;

const sf::IntRect whiteBtnRect(boardPixelSize + 10, 50,  sidebarWidth - 20, 80);
const sf::IntRect blackBtnRect(boardPixelSize + 10, 150, sidebarWidth - 20, 80);

// Background engine thread state
std::thread engineThread;
std::atomic<bool> engineThinking{false};
std::mutex engineMoveMutex;
std::optional<Move> engineReadyMove;

inline bool shouldEngineMove(const Position& pos) {
    return (pos.getTurn() == WHITE && engineForWhite) ||
           (pos.getTurn() == BLACK && engineForBlack);
}

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
    Square ss(selectedSquare.x,selectedSquare.y);
    //std::vector<Move> legalMovesForSelectedPiece = position.movementValidator.getLegalMovements(ss,position.getTurn());
    for (int rank = 0; rank < boardSize; ++rank) {
        for (int file = 0; file < boardSize; ++file) {
            sf::RectangleShape square(sf::Vector2f(squareSize, squareSize));
            square.setPosition(file * squareSize, rank * squareSize);
            square.setFillColor(((file + rank) % 2 == 0) ? light : dark);
            // do the below for every possible move
            if (sf::Vector2i(file, rank) == selectedSquare) {
                square.setFillColor(sf::Color(100, 100, 100));
            }
            /*
            if (vectorContainsMove(legalMovesForSelectedPiece,Move(ss,Square(file,rank)))) {
                square.setFillColor(sf::Color(100, 100, 100));
            }
            */
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

    sf::RectangleShape turnIndicator(sf::Vector2f(10, boardPixelSize));
    turnIndicator.setFillColor(position.getTurn() == WHITE ? sf::Color::White : sf::Color::Black);
    turnIndicator.setPosition(boardPixelSize, 0);
    window.draw(turnIndicator);

    {
        sf::RectangleShape whiteBtn(sf::Vector2f(whiteBtnRect.width, whiteBtnRect.height));
        whiteBtn.setPosition((float)whiteBtnRect.left, (float)whiteBtnRect.top);
        whiteBtn.setFillColor(engineForWhite ? sf::Color(200,200,200) : sf::Color(100,100,100));
        whiteBtn.setOutlineThickness(2.f);
        whiteBtn.setOutlineColor(sf::Color::White);
        window.draw(whiteBtn);

        sf::RectangleShape icon(sf::Vector2f(24,24));
        icon.setFillColor(sf::Color::White);
        icon.setPosition(whiteBtnRect.left + 10, whiteBtnRect.top + 28);
        window.draw(icon);
    }

    {
        sf::RectangleShape blackBtn(sf::Vector2f(blackBtnRect.width, blackBtnRect.height));
        blackBtn.setPosition((float)blackBtnRect.left, (float)blackBtnRect.top);
        blackBtn.setFillColor(engineForBlack ? sf::Color(200,200,200) : sf::Color(100,100,100));
        blackBtn.setOutlineThickness(2.f);
        blackBtn.setOutlineColor(sf::Color::White);
        window.draw(blackBtn);

        sf::RectangleShape icon(sf::Vector2f(24,24));
        icon.setFillColor(sf::Color::Black);
        icon.setPosition(blackBtnRect.left + 10, blackBtnRect.top + 28);
        window.draw(icon);
    }
}

void maybeStartEngineTurn(Engine& engine, Position& position) {
    if (engineThinking.load()) return;
    if (!shouldEngineMove(position)) return;

    engineThinking.store(true);
    {
        std::lock_guard<std::mutex> lk(engineMoveMutex);
        engineReadyMove.reset();
    }

    engineThread = std::thread([&engine, &position]() {
        try {
            Position otherPosition(position);
            Engine otherEngine(&otherPosition);
            Move best = otherEngine.getBestMove();
            {
                std::lock_guard<std::mutex> lk(engineMoveMutex);
                engineReadyMove = best;
            }
        } catch (const std::exception& e) {
            std::cerr << "Engine error: " << e.what() << "\n";
        }
        engineThinking.store(false);
    });
    engineThread.detach();
}

int main() {
    Position position;
    if (!loadTextures()) return 1;

    Engine engine(&position);

    sf::RenderWindow window(
        sf::VideoMode(boardPixelSize + sidebarWidth, boardPixelSize),
        "Chess GUI", sf::Style::Default
    );
    window.setFramerateLimit(60);

    sf::View view(sf::FloatRect(0, 0, boardPixelSize + sidebarWidth, boardPixelSize));
    window.setView(view);

    bool pieceSelected = false;

    bool running = true;
    while (running) {
        sf::Event event;
        while (window.pollEvent(event)) {
            if (event.type == sf::Event::Closed) {
                running = false;
                break;
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
            else if (event.type == sf::Event::MouseButtonPressed &&
                     event.mouseButton.button == sf::Mouse::Left) {

                sf::Vector2f worldPos = window.mapPixelToCoords(
                    {event.mouseButton.x, event.mouseButton.y}, view);

                int x = (int)worldPos.x;
                int y = (int)worldPos.y;

                if (x >= whiteBtnRect.left && x <= whiteBtnRect.left + whiteBtnRect.width &&
                    y >= whiteBtnRect.top  && y <= whiteBtnRect.top  + whiteBtnRect.height) {
                    engineForWhite = !engineForWhite;
                    if (position.getTurn() == WHITE) {
                        maybeStartEngineTurn(engine, position);
                    }
                    continue;
                }
                if (x >= blackBtnRect.left && x <= blackBtnRect.left + blackBtnRect.width &&
                    y >= blackBtnRect.top  && y <= blackBtnRect.top  + blackBtnRect.height) {
                    engineForBlack = !engineForBlack;
                    if (position.getTurn() == BLACK) {
                        maybeStartEngineTurn(engine, position);
                    }
                    continue;
                }

                if (engineThinking.load() || shouldEngineMove(position)) {
                    continue;
                }

                int file = static_cast<int>(worldPos.x / squareSize);
                int rank = static_cast<int>(worldPos.y / squareSize);
                if (file < 0 || file >= 8 || rank < 0 || rank >= 8)
                    continue;

                ColoredPiece cp = position.getPiece(Square(rank,file));
                bool friendPieceOnSquare = cp.color == position.getTurn();
                if (!pieceSelected && friendPieceOnSquare) {
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
                        pieceSelected = false;

                        if (shouldEngineMove(position)) {
                            maybeStartEngineTurn(engine, position);
                        }
                    } catch (const std::exception& e) {
                        std::cerr << "Invalid move: " << e.what() << std::endl;
                        pieceSelected = false;
                    }
                }
            }
        }

        {
            std::lock_guard<std::mutex> lk(engineMoveMutex);
            if (engineReadyMove.has_value()) {
                try {
                    position.moveMaker.makeLegalMove(*engineReadyMove);
                } catch (const std::exception& e) {
                    std::cerr << "Engine produced illegal move: " << e.what() << "\n";
                }
                engineReadyMove.reset();

                if (shouldEngineMove(position) && !engineThinking.load()) {
                    maybeStartEngineTurn(engine, position);
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
