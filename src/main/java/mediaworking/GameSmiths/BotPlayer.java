package mediaworking.GameSmiths;

import java.util.Random;

class BotPlayer extends Player {
    public Random random; // For generating random numbers

    public BotPlayer(String name, int size) {
        super(name, size); // Pass the name and size to the Player constructor
        this.random = new Random(); // Initialize the random number generator
    }

    @Override
    public int playTurn() {
        // Display a message that it's the computer's turn
        System.out.println(getName() + "'s (Computer) turn:");

        int numberToRemove;
        boolean found;

        do {
            numberToRemove = random.nextInt(getBoard().getSize() * getBoard().getSize()) + 1; // Random number within the range of the Bingo card
            found = false; // Reset found flag

            // Check if the generated number is on the player's card
            if (getBoard().getCard().contains(numberToRemove)) {
                found = true; // The number is found on the card
            }
        } while (!found); // Repeat until a valid number is found

        // Display the action performed by the bot
//        System.out.println(getName() + " has selected number: " + numberToRemove);

        return numberToRemove; // Return the chosen number
    }


}

