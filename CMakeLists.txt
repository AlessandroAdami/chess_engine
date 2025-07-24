cmake_minimum_required(VERSION 3.10)
project(ChessEngine)

set(CMAKE_CXX_STANDARD 20)
set(CMAKE_CXX_STANDARD_REQUIRED ON)
set(CMAKE_CXX_CLANG_TIDY clang-tidy)
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

# Auto-formatting

#add_custom_target(
#    auto_format ALL
#    COMMAND clang-format -i ${ALL_SOURCE_FILES}
#    COMMENT "Auto-formatting source files"
#)

# Main executable
add_executable(chess_engine src/main.cpp)

file(GLOB LIB_RESOURCES "src/*.cpp")

add_library(chesslib ${LIB_RESOURCES})

# Make sure the library can see headers
target_include_directories(chesslib PUBLIC include)

# Link main executable to library
target_link_libraries(chess_engine PRIVATE chesslib)

# --------- GoogleTest Setup ---------

include(FetchContent)

FetchContent_Declare(
  googletest
  GIT_REPOSITORY https://github.com/google/googletest.git
  GIT_TAG v1.17.0
)

# For Windows: Prevent overriding the parent project's compiler/linker settings
set(gtest_force_shared_crt ON CACHE BOOL "" FORCE)
FetchContent_MakeAvailable(googletest)

enable_testing()

# Tests
file(GLOB TEST_SOURCES "test/*.cpp")

add_executable(tests ${TEST_SOURCES})
target_include_directories(tests PRIVATE include)
target_link_libraries(tests
    PRIVATE
    chesslib
    GTest::gtest_main
)

include(GoogleTest)
gtest_discover_tests(tests)