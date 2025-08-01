# Chess Engine

A chess engine in C++, with simple command line interface.

## Running the App

To use the application run:
- "cd ../build"
- "cmake .."
- "make"
- "./chess_engine"

## Engine features

### Negamax

The engine uses a [negamax](https://www.chessprogramming.org/Negamax) implementation of the [minimax](https://www.chessprogramming.org/Minimax) algorithm with [alpha-beta](https://www.chessprogramming.org/Minimax) pruning. The minimax algorithm is a simple strategy to model zero-sum games with two players and alternating turns.

### Quiescence Search

[Quiescence](https://www.chessprogramming.org/Quiescence_Search) attempts to achieve stable results by searching further down the tree for positions where no captures (or checks) are possible. This avoids cases where a player has achieved a temporary material advantage that can easily be refuted by the opponent playing a capture. Such a case is possible when the engine stops at an arbitrary depth.

### TTEntries and Zobrist Hashing

[Transposition tables](https://www.chessprogramming.org/Transposition_Table) are data structures storing information the engine has already obtained about a certain node (legally reachable position). For example the node's evaluation, depth, and best move. [Zobrist hashing](https://www.chessprogramming.org/Zobrist_Hashing) allows us to efficiently look up this information by mapping from a position to a TTEntry, avoiding duplicate or unnecessary (looking at suboptimal nodes) work.

### Static exchange evaluation

[Static exchange evaluation](https://www.chessprogramming.org/Static_Exchange_Evaluation) (SEE) is a simple strategy to evaluate whether capturing on a certain square is beneficial for a player. The implementation simulates various possible captures in order of least-value capturing piece. SSE is a fast and human-like way to resolve possibly complex positions where lots of captures are to be evaluated.