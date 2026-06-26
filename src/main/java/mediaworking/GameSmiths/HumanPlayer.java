package mediaworking.GameSmiths;

import java.util.Scanner;

public class HumanPlayer extends Player {
    private Scanner scanner; // Scanner for input

    public HumanPlayer(String name, int size) {
        super(name, size); // Pass the name and size to the Player constructor
        this.scanner = new Scanner(System.in); // Initialize the scanner
    }

    @Override
    public int playTurn() {
        // Display the player's board
        board.displayBoard();

        // Get number input from the user
        int numberToRemove = getNumberInput(); // Use the method defined below

        // Mark the number on the board
        board.markNumber(numberToRemove);

        return numberToRemove; // Return the number to remove
    }

    // Method to get valid input from the user
    public int getNumberInput() {
        while (true) {
            System.out.print("Enter a number to mark: ");
            if (scanner.hasNextInt()) {
                int number = scanner.nextInt();
                scanner.nextLine(); // Clear the buffer

                // Check if the number is present in the player's card
                if (board.getCard().contains(number)) {
                    return number; // Return the valid number
                } else {
                    System.out.println("Number is not present on your card. Please try again.");
                }
            } else {
                System.out.println("Invalid input. Please enter a valid number.");
                scanner.nextLine(); // Clear the invalid input
            }
        }
    }
}
