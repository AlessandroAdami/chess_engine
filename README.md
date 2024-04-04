# Rookie Rook

## To learn while having fun!

My goal with this application is to let users test their 
skills in different opening or chosen settings.
Some key parts of RookieRook will be:

- A chess interface 
- A chess bot (possibly with different difficulties settings)
- Some sort of board evaluation or move evaluation & post-game analysis (or suggestions)
- A tracker that builds a library of the user's games, specifically:
  - Classify wins, losses and draws.
  - Win-rate stats with respect to different openings (and possible difficulty settings)
- Tips on related opening the User might like based on their approach to the game

Ultimately, the goal is to provide an interface that allows the player to understand 
their weaknesses and test their strengths, while also testing some new ideas.

## User Stories
### Part 1
- As a user, I want to be able to create a new game and specify a name for it.
- As a user, I want to be able to play and save multiple games.
- As a user, I want to be able to get an evaluation of the game I am playing.
- As a user, I want to be able to rename or delete any of my games.
### Part 2
- As a user, I want to be able to select the option to save my games when I quit the application.
- As a user, I want to be able to play my old games from the saved position after I close and reopen the application.

### Part 3
#### Instructions for Grader:
- To add a new board to the list, write the board name in the cell and click the "Create" button
- The "Delete" and "Select" button work similarly, and will modify the first board in the list with the given name
- To save (or load) the data to (from) file, simply go the menuBar with the "File" menu and select the wanted option 

### Part 4
#### Task 2
Thu Apr 04 13:35:30 PDT 2024:
Board "New Board" was added to list.


Thu Apr 04 13:35:31 PDT 2024:
Board "New Board" was added to list.


Thu Apr 04 13:35:39 PDT 2024:
Board "Italian" was added to list.


Thu Apr 04 13:35:44 PDT 2024:
Board "Slav" was added to list.


Thu Apr 04 13:35:49 PDT 2024:
Board "Slav" was deleted and removed from list.
#### Task 3
There are a few thing that could really be improved design-wise. The Board class has really low cohesion, it does many
things that it shouldn't be doing. Adding a LegalMoveChecker class might help at that. Furthermore, there really is a 
lot of extra direct connections between classes, ChessGameApp should only know about ChessGame 
(not Board and BoardList). Furthermore, ChessGameApp is currently handling a lot of things that ChessGame
should handle, such as setting the current board, deleting a board from the list, keeping a list of boards, etc.
Finally, there is a lot of repetition based on the arbitrary int values connected to the pieces, it would make
sense to add an enumeration (or some other object) and use constants instead (e.g. set PAWN = 1).