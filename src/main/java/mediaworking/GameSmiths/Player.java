package mediaworking.GameSmiths;

import mediaworking.GameSmiths.Model.Board;

public abstract class Player {
    private String name; // Player's name
    protected Board board; // Each player has a board

    // Constructor to initialize player name and board size
    public Player(String name, int size) {
        this.name = name; // Initialize player's name
        this.board = new Board(size); // Create a board with the specified size
    }

    // Getter for player's name
    public String getName() {
        return name;
    }

    // Getter for player's board
    public Board getBoard() {
        return board;
    }

    // Abstract playTurn method to be overridden in subclasses
    public abstract int playTurn();
}
