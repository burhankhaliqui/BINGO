package mediaworking.GameSmiths;

import java.util.ArrayList;
import java.util.Scanner;
import java.io.*;
import java.net.*;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Menu menu = new Menu(scanner); // Create an instance of Menu
        boolean exitGame = false;

        while (!exitGame) {
            int mainMenuChoice = menu.displayMainMenu(); // Display the main menu and get user choice
            switch (mainMenuChoice) {
                case 1: // Play
                    playGame(scanner, menu);
                    break;
                case 2:
                    System.out.println("Settings feature .");
                    break;
                case 3:
                    exitGame = true;
                    System.out.println("Thank you for playing!");
                    break;
                case 4:
                    displayHowToPlay();  // How to play option
                    break;
                default:
                    System.out.println("Invalid choice. Please select again.");
                    break;
            }
        }
    }

    private static void playGame(Scanner scanner, Menu menu) {
        int gameModeChoice = menu.displayGameModeMenu();
        if (gameModeChoice == 1) {
            singlePlayerMode(menu);
        } else {
            multiplayerMode(scanner, menu);
        }
    }

    private static void singlePlayerMode( Menu menu) {
        int size = menu.getBoardSize();
        System.out.print("Enter your name: ");
        String playerName = menu.getPlayerName();
        HumanPlayer humanPlayer = new HumanPlayer(playerName, size);
        BotPlayer botPlayer = new BotPlayer("Computer", size);

        boolean gameWon = false;
        ArrayList<String> winners;

        // Game loop
        while (!gameWon) {
            System.out.println("\n--- " + humanPlayer.getName() + "'s Turn ---");
            int numberToRemove = humanPlayer.playTurn();
            System.out.println(humanPlayer.getName() + " has chosen the number: " + numberToRemove);
            botPlayer.getBoard().markNumber(numberToRemove);

            winners = Winner.checkForWinners(humanPlayer, botPlayer);
            gameWon = checkSinglePlayerGameOutcome(winners, humanPlayer, botPlayer);

            if (gameWon) {
                break;
            }

            System.out.println("\n--- " + botPlayer.getName() + "'s Turn ---");
            int botNumberToRemove = botPlayer.playTurn();
            System.out.println(botPlayer.getName() + " has chosen the number: " + botNumberToRemove);
            humanPlayer.getBoard().markNumber(botNumberToRemove);
            winners = Winner.checkForWinners(humanPlayer, botPlayer);
            gameWon = checkSinglePlayerGameOutcome(winners, humanPlayer, botPlayer);
        }
    }

    private static void multiplayerMode(Scanner scanner, Menu menu) {
        System.out.println("Select Multiplayer Option:");
        System.out.println("1. Play Online");
        System.out.println("2. Play Offline");
        int multiplayerOption = menu.getValidChoice( 1, 2);

        if (multiplayerOption == 1) {
            playOnline(scanner, menu); // Handle online game logic
        } else {
            playOffline( menu); // Handle offline game logic
        }
    }
    // Method to start the game in online multiplayer mode
    public static void playOnline(Scanner scanner, Menu menu) {
        System.out.println("Do you want to play as a server or a client? (Enter 'server' or 'client')");
        String choice = scanner.nextLine().trim().toLowerCase();

        if (choice.equals("server")) {
            // Server logic
            int size = menu.getBoardSize();
            int numPlayers = menu.getNumPlayers();
            ArrayList<Player> players = new ArrayList<>();
            ArrayList<PrintWriter> clientOutputs = new ArrayList<>();
            ArrayList<BufferedReader> clientInputs = new ArrayList<>();
            ServerSocket serverSocket = null;
            ArrayList<String> winners = new ArrayList<>();
            boolean gameWon = false;

            try {
                System.out.print("Enter server player name: ");
                String serverPlayerName = scanner.nextLine();
                HumanPlayer serverPlayer = new HumanPlayer(serverPlayerName, size);
                players.add(serverPlayer);

                serverSocket = new ServerSocket(12345);
                System.out.println("Server is running and waiting for connections...");

                for (int i = 0; i < numPlayers - 1; i++) {
                    Socket clientSocket = serverSocket.accept();
                    System.out.println("A player has connected.");

                    BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                    PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);

                    clientInputs.add(in);
                    clientOutputs.add(out);

                    out.println("Enter your name: ");
                    String playerName = in.readLine();
                    System.out.println(playerName + " has joined the game.");
                    players.add(new HumanPlayer(playerName, size));

                    out.println("Welcome " + playerName + "! The game will start soon.");
                }

                System.out.println("All players connected. The game will start in 10 seconds. Get ready!");
                for (PrintWriter out : clientOutputs) {
                    out.println("All players connected. The game will start in 10 seconds. Get ready!");
                }
                Thread.sleep(10000);

                // Display initial boards to all players
                for (int i = 0; i < players.size(); i++) {
                    Player currentPlayer = players.get(i);
                    String boardString = currentPlayer.getBoard().toString();

                    if (i == 0) { // Server player
                        System.out.println("\n--- Your Initial Board ---");
                        System.out.println(boardString);
                    } else { // Client players
                        PrintWriter out = clientOutputs.get(i - 1);
                        out.println("Here is your initial board:\n" + boardString);
                    }
                }

                while (!gameWon) {
                    for (int i = 0; i < players.size(); i++) {
                        Player currentPlayer = players.get(i);
                        int numberToRemove = 0;

                        for (int j = 0; j < players.size(); j++) {
                            if (j == 0) {
                                System.out.println(currentPlayer.getName() + " is making their turn...");
                            } else {
                                PrintWriter out = clientOutputs.get(j - 1);
                                out.println(currentPlayer.getName() + " is making their turn...");
                            }
                        }

                        if (i == 0) {
                            while (true) {
                                try {
                                    numberToRemove = serverPlayer.getNumberInput();
                                    break;
                                } catch (IllegalArgumentException e) {
                                    System.out.println(e.getMessage());
                                }
                            }
                        } else {
                            PrintWriter out = clientOutputs.get(i - 1);
                            BufferedReader in = clientInputs.get(i - 1);

                            while (true) {
                                try {
                                    out.println("It's your turn! Choose a number to mark on your board.");
                                    String input = in.readLine();
                                    if (input == null) throw new IOException("Player disconnected.");

                                    numberToRemove = Integer.parseInt(input);

                                    if (!currentPlayer.getBoard().getCard().contains(numberToRemove)) {
                                        out.println("Invalid number! The number is not on your board. Try again.");
                                        continue;
                                    }
                                    break;
                                } catch (IOException e) {
                                    System.out.println(currentPlayer.getName() + " has disconnected.");
                                    handleDisconnection(players, clientOutputs, clientInputs, i);
                                    i--;
                                    break;
                                } catch (NumberFormatException e) {
                                    out.println("Invalid input. Please enter a valid number.");
                                }
                            }
                        }

                        System.out.println(currentPlayer.getName() + " chose: " + numberToRemove);

                        for (Player p : players) {
                            p.getBoard().markNumber(numberToRemove);
                        }

                        // Display updated boards to all players
                        for (int j = 0; j < players.size(); j++) {
                            Player updatedPlayer = players.get(j);
                            String updatedBoard = updatedPlayer.getBoard().toString();

                            if (j == 0) { // Server player
                                System.out.println("\n--- Your Updated Board ---");
                                System.out.println(updatedBoard);
                            } else { // Client players
                                PrintWriter out = clientOutputs.get(j - 1);
                                out.println("Here is your updated board:\n" + updatedBoard);
                            }
                        }

                        winners = Winner.checkForWinners(players.toArray(new Player[0]));
                        gameWon = !winners.isEmpty();
                        if (gameWon) {
                            break;
                        }
                    }
                }

                System.out.println("Game Over! Winners: " + String.join(", ", winners));
                for (PrintWriter out : clientOutputs) {
                    out.println("Game Over! Winners: " + String.join(", ", winners));
                }

            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (serverSocket != null) serverSocket.close();
                    for (BufferedReader in : clientInputs) in.close();
                    for (PrintWriter out : clientOutputs) out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } else if (choice.equals("client")) {
            // Client logic
            String serverAddress = "localhost";
            int port = 12345;

            try (Socket socket = new Socket(serverAddress, port);
                 BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                 PrintWriter output = new PrintWriter(socket.getOutputStream(), true)) {

                System.out.print("Enter your name: ");
                String playerName = scanner.nextLine();
                output.println(playerName);

                String serverMessage;
                while ((serverMessage = input.readLine()) != null) {
                    if (serverMessage.startsWith("Welcome")) {
                        System.out.println(serverMessage);
                    } else if (serverMessage.startsWith("Here is your initial board:")) {
                        System.out.println(serverMessage);
                    } else if (serverMessage.startsWith("Here is your updated board:")) {
                        System.out.println(serverMessage);
                    } else if (serverMessage.startsWith("It's your turn")) {
                        System.out.println(serverMessage);
                        System.out.print("Enter your number: ");
                        String number = scanner.nextLine();
                        output.println(number);
                    } else if (serverMessage.startsWith("Game Over")) {
                        System.out.println(serverMessage);
                        break;
                    } else {
                        System.out.println(serverMessage);
                    }
                }

            } catch (IOException e) {
                System.out.println("Connection error: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            System.out.println("Invalid choice. Please restart and enter 'server' or 'client'.");
        }
    }

    private static void handleDisconnection(
            ArrayList<Player> players,
            ArrayList<PrintWriter> clientOutputs,
            ArrayList<BufferedReader> clientInputs,
            int index) {
        players.remove(index);
        clientOutputs.remove(index - 1);
        clientInputs.remove(index - 1);
    }

    private static void playOffline( Menu menu) {
        int size = menu.getBoardSize();
        int numPlayers = menu.getNumPlayers();
        ArrayList<Player> players = new ArrayList<>();

        for (int i = 0; i < numPlayers; i++) {
            int playerType = menu.getPlayerType(i);
            if (playerType == 1) {
                String playerName = menu.getPlayerName();
                players.add(new HumanPlayer(playerName, size));
            } else {
                players.add(new BotPlayer("Bot " + (i + 1), size));
            }
        }

        boolean gameWon = false;
        ArrayList<String> winners;

        // Game loop for offline mode
        while (!gameWon) {
            for (Player player : players) {
                System.out.println("\n--- " + player.getName() + "'s Turn ---");
                int numberToRemove = player.playTurn();
                System.out.println(player.getName() + " has chosen the number: " + numberToRemove);

                for (Player p : players) {
                    p.getBoard().markNumber(numberToRemove);
                }

                winners = Winner.checkForWinners(players.toArray(new Player[0]));
                gameWon = checkMultiplayerGameOutcome(winners, players);

                if (gameWon) {
                    break;
                }
            }
        }
    }

    private static boolean checkSinglePlayerGameOutcome(ArrayList<String> winners, Player humanPlayer, Player botPlayer) {
        if (!winners.isEmpty()) {
            System.out.println("Game Over!");
            if (winners.size() == 1) {
                System.out.println(winners.get(0) + " has won the game!");
            } else {
                System.out.println("It's a draw! Both players have won!");
            }
            displayWinnerBoards(humanPlayer, botPlayer);
            return true;
        }
        return false;
    }

    private static boolean checkMultiplayerGameOutcome(ArrayList<String> winners, ArrayList<Player> players) {
        if (!winners.isEmpty()) {
            System.out.println("Game Over!");
            if (winners.size() == 1) {
                System.out.println(winners.get(0) + " has won the game!");
            } else {
                System.out.println("It's a draw! The following players have won: " + winners);
            }
            displayWinnerBoards(players);
            return true;
        }
        return false;
    }

    private static void displayWinnerBoards(Player humanPlayer, Player botPlayer) {
        System.out.println("\n--- Final Boards ---");
        System.out.println("Player: " + humanPlayer.getName());
        humanPlayer.getBoard().displayBoard();
        System.out.println("Player: " + botPlayer.getName());
        botPlayer.getBoard().displayBoard();
    }

    private static void displayWinnerBoards(ArrayList<Player> players) {
        System.out.println("\n--- Final Boards ---");
        for (Player player : players) {
            System.out.println("Player: " + player.getName());
            player.getBoard().displayBoard();
        }
    }

    // Method to display "How to Play" instructions
    private static void displayHowToPlay() {
        System.out.println("\n--- How to Play Bingo ---");
        System.out.println("i. The game is played with a Bingo card of numbers.");
        System.out.println("ii. Players take turns choosing numbers.");
        System.out.println("iii. The goal is to mark off a complete row, column, or diagonal on your card.");
        System.out.println("iv. When a player gets a Bingo, the game ends.");
        System.out.println("v. You can play against a bot or with other players.");
        System.out.println("vi. Enjoy the game!");
}
}