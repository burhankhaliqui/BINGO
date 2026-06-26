package mediaworking.GameSmiths;

import java.util.Scanner;

public class Menu {
    private final Scanner scanner;

    public Menu(Scanner scanner) {
        this.scanner = scanner;
    }

    // Main menu with options to play, settings, or exit
    public int displayMainMenu() {
        System.out.println("Welcome to Bingo!");
        System.out.println("Select an option:");
        System.out.println("1. Play");
        System.out.println("2. Settings (Coming Soon)");
        System.out.println("3. Exit");
        System.out.println("4. How to Play");
        System.out.print("Enter your choice (1, 2, 3, or 4): ");
        return getValidChoice(1, 4);  // Updated to allow choosing option 4 (How to Play)
    }

    // Game mode menu, now with options for playing offline and online
    public int displayGameModeMenu() {
        System.out.println("Select Game Mode:");
        System.out.println("1. Single Player");
        System.out.println("2. Multiplayer");
        System.out.print("Enter your choice (1 or 2): ");
        return getValidChoice(1, 2);
    }

    // Multiplayer menu, with options for online and offline play
//    public int displayMultiplayerMenu() {
//        System.out.println("Select Multiplayer Option:");
//        System.out.println("1. Play Online (Coming Soon1)");
//        System.out.println("2. Play Offline");
//        System.out.print("Enter your choice (1 or 2): ");
//        return getValidChoice(1, 2);
//    }

    // Get the size of the Bingo card
    public int getBoardSize() {
        System.out.print("Enter the size of the Bingo card (between 4 and 10): ");
        return getValidChoice(4, 10);
    }

    // Get the number of players (2 to 5) for multiplayer
    public int getNumPlayers() {
        System.out.print("Enter the number of players (2 to 5): ");
        return getValidChoice(2, 5);
    }

    // Get the type of player (human or bot) for each player in the game
    public int getPlayerType(int playerIndex) {
        System.out.print("Enter 1 for Human Player, 2 for Bot (Player " + (playerIndex + 1) + "): ");
        return getValidChoice(1, 2);
    }

    // Get the human player's name
    public String getPlayerName() {
        System.out.print("Enter Human Player's name: ");
        return scanner.nextLine();
    }

    // Validates and ensures the input is within the specified range
    public int getValidChoice(int min, int max) {
        int choice = -1;
        while (choice < min || choice > max) {
            while (!scanner.hasNextInt()) {
                System.out.println("Invalid input. Please enter a valid number.");
                scanner.next();
            }
            choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline
            if (choice < min || choice > max) {
                System.out.println("Invalid choice. Please select a number between " + min + " and " + max + ".");
            }
        }
        return choice;
    }
}
