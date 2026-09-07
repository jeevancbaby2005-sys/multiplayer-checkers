Multiplayer Checkers Game
1. Project Overview

This project is a Java and JavaFX checkers application that allows users to play checkers through a graphical user interface. The application supports player usernames, games against a computer opponent, and games against another player.

The project combines object-oriented programming, JavaFX GUI development, game logic, networking, and event-driven programming. The graphical interface allows players to interact with the game while the application manages movement, captures, turns, kings, and game-ending conditions.

2. Main Features

The application includes several features designed to make the checkers game interactive.

Graphical 8×8 checkers board
Unique username validation
Play against a computer
Play against another player
Player waiting/matching system
Player messaging
Regular piece movement
Jump and capture moves
Multiple-jump support
King promotion
Turn management
Win and draw detection
Opponent disconnect handling
Game restart functionality

The JavaFX interface uses controls such as buttons, labels, text fields, list views, and grid panes to create the different screens of the application.

3. How the Game Works

When the application starts, the player enters a unique username. The program checks the username against the list of currently connected users and rejects empty, duplicate, or reserved names.

After logging in, the player can choose between playing against the computer or another player. When playing against another person, the client communicates with the game system to find an opponent.

The checkers board is represented using a two-dimensional character array. Different characters represent empty spaces, regular pieces, and kings. The drawBoard() method converts this internal board representation into the JavaFX graphical board.

Movement is validated before a piece is moved. The program checks whether the selected piece belongs to the current player, whether the destination is valid, and whether a jump is required. Captured pieces are removed from the board, and pieces that reach the appropriate end of the board are promoted to kings.

4. Development Timeline

I began by creating the basic JavaFX interface and designing the different screens required by the game. After the GUI was working, I implemented the checkers board and the representation of the pieces.

The next stage focused on movement rules. I implemented normal movement, jumps, captures, and king promotion. After the basic game rules were working, I added additional logic for multiple jumps and determining when a player could no longer make a legal move.

I then worked on communication between players and added functionality for usernames, opponents, messaging, waiting for players, and playing against the computer.

The final stage focused on improving the user experience, handling game-ending conditions, and fixing invalid moves and turn-management problems.

5. Bugs and Challenges

One of the main challenges was making sure that players could not make illegal moves. The program uses methods such as invalid(), rest(), multiple(), and lock() to determine whether a move is permitted.

Another challenge was implementing forced jumps. If a capture is available, the player must perform a jump instead of making a normal move. The program also checks whether another capture is available after a jump.

King movement required additional logic because kings can move in both directions. I also had to handle the difference between regular pieces and kings when checking possible moves.

Another challenge was keeping the graphical interface synchronized with the underlying board state. After a move is received or made, the board needs to be redrawn so that the pieces displayed on screen match the internal game state.

6. What I Learned

This project gave me experience combining multiple parts of software development into one application. I practiced Java object-oriented programming, JavaFX GUI development, event handling, arrays, collections, networking, and debugging.

I also learned that implementing a game requires careful attention to edge cases. A movement system must account for board boundaries, invalid destinations, captures, multiple jumps, kings, turns, and game-ending conditions.

Working on the GUI also showed me how the user interface and underlying program logic need to communicate with each other. A change to the board state needs to be reflected visually, while user actions need to trigger the appropriate game logic.

7. Video Demonstration

The video below demonstrates the Checkers application running and explains the main features of the project, including the graphical interface, player interaction, movement, and game functionality.

Watch the Checkers Game Demonstration
https://drive.google.com/drive/folders/1NNHf1KEV7yF7I-8kUFJzVqnZK40ZahcL?dmr=1&ec=wgc-drive-%5Bmodule%5D-goto

