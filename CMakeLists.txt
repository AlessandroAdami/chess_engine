cmake_minimum_required(VERSION 3.10)
project(ChessEngine)

set(CMAKE_CXX_STANDARD 23)
set(CMAKE_CXX_STANDARD_REQUIRED ON)
set(CMAKE_CXX_CLANG_TIDY "") # TODO: add linting later
set(CMAKE_EXPORT_COMPILE_COMMANDS ON)
set(CMAKE_BUILD_TYPE Debug)

file(GLOB_RECURSE ALL_SOURCE_FILES
    "${CMAKE_SOURCE_DIR}/src/*.cpp"
    "${CMAKE_SOURCE_DIR}/src/*.h"
    "${CMAKE_SOURCE_DIR}/include/*.h"
    "${CMAKE_SOURCE_DIR}/test/*.cpp"
)

add_custom_target(
    format
    COMMAND clang-format -i ${ALL_SOURCE_FILES}
    WORKING_DIRECTORY ${CMAKE_SOURCE_DIR}
)

# Main executables
add_executable(chess_engine src/main.cpp)
add_executable(chess_uci src/uci.cpp)

set(ENGINE_SOURCES
    src/engine.cpp
    src/position.cpp
    src/move_parser.cpp
    src/move_maker.cpp
)
add_executable(chess_gui src/gui/main.cpp ${ENGINE_SOURCES})

# Copy pieces folder to build directory
set(PIECES_SOURCE_DIR ${CMAKE_SOURCE_DIR}/src/gui/pieces)
set(PIECES_DEST_DIR ${CMAKE_BINARY_DIR}/pieces)

add_custom_command(
    OUTPUT ${PIECES_DEST_DIR}
    COMMAND ${CMAKE_COMMAND} -E copy_directory ${PIECES_SOURCE_DIR} ${PIECES_DEST_DIR}
    COMMENT "Copying SFML chess piece images to build folder"
)

add_custom_target(copy_pieces ALL DEPENDS ${PIECES_DEST_DIR})
add_dependencies(chess_gui copy_pieces)

# Library for engine code
file(GLOB LIB_RESOURCES "src/*.cpp")
list(FILTER LIB_RESOURCES EXCLUDE REGEX ".*uci\\.cpp$")

add_library(chesslib ${LIB_RESOURCES})
target_include_directories(chesslib PUBLIC include)

# Include directories
target_include_directories(chess_engine PRIVATE ${CMAKE_SOURCE_DIR}/src ${CMAKE_SOURCE_DIR}/include)
target_include_directories(chess_uci PRIVATE ${CMAKE_SOURCE_DIR}/src ${CMAKE_SOURCE_DIR}/include)

# Link libraries
target_link_libraries(chess_engine PRIVATE chesslib)
target_link_libraries(chess_uci PRIVATE chesslib)
target_link_libraries(chess_gui sfml-graphics sfml-window sfml-system chesslib)

# GoogleTest Setup
enable_testing()
find_package(GTest REQUIRED)

file(GLOB TEST_SOURCES "test/*.cpp")
add_executable(tests ${TEST_SOURCES})
target_include_directories(tests PRIVATE include)
target_link_libraries(tests PRIVATE chesslib GTest::gtest GTest::gtest_main pthread)

include(GoogleTest)
gtest_discover_tests(tests)
