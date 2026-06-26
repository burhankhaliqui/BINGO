package mediaworking.GameSmiths.controllers;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;

public class OnlineGameBoardController {

    private static final Logger LOGGER = Logger.getLogger(OnlineGameBoardController.class.getName());

    @FXML private Label statusLabel;
    @FXML private GridPane boardGrid;
    @FXML private Button exitButton;
    @FXML private TextArea chatArea;  // Chat area for displaying messages
    @FXML private TextField messageField;  // Text field for user to type messages
    @FXML private Button sendButton;  // Button to send chat messages

    private Stage stage;
    private boolean isServer;
    private ServerController serverController;
    private ClientController clientController;
    private int boardSize;
    private Button[][] boardButtons;
    private boolean isMyTurn;
    private ExecutorService executorService;
    private Future<?> moveListenerTask;
    private boolean isGameOver = false; // Flag to track if the game is over

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void setupGame(boolean isServer, ServerController serverController, ClientController clientController, int boardSize) {
        this.isServer = isServer;
        this.serverController = serverController;
        this.clientController = clientController;
        this.boardSize = boardSize;
        this.isMyTurn = isServer; // Player 1 (Server) starts first
        this.executorService = Executors.newSingleThreadExecutor();

        initializeBoard();
        disableCloseButton();
        initializeNumbers();
        initializeChat(); // Initialize chat functionality

        if (isServer) {
            statusLabel.setText("Waiting for Player 2 to connect...");
            moveListenerTask = executorService.submit(this::listenForClientMoves);
        } else {
            statusLabel.setText("Connected to Player 1. Waiting for game to start...");
            moveListenerTask = executorService.submit(this::listenForServerMoves);
        }

        updateTurnLabel();
    }

    private void initializeBoard() {
        boardButtons = new Button[boardSize][boardSize];
        boardGrid.getChildren().clear();

        for (int i = 0; i < boardSize; i++) {
            for (int j = 0; j < boardSize; j++) {
                Button button = new Button();
                button.setPrefSize(50, 50);
                button.setOnAction(e -> handleButtonClick(button));
                boardButtons[i][j] = button;
                boardGrid.add(button, j, i);
            }
        }

        initializeNumbers();
    }

    private void initializeNumbers() {
        List<Integer> numbers = new ArrayList<>();
        for (int i = 1; i <= boardSize * boardSize; i++) {
            numbers.add(i);
        }
        Collections.shuffle(numbers);

        int index = 0;
        for (int i = 0; i < boardSize; i++) {
            for (int j = 0; j < boardSize; j++) {
                boardButtons[i][j].setText(String.valueOf(numbers.get(index++)));
            }
        }
    }

    @FXML
    private void handleButtonClick(Button button) {
        if (!isMyTurn || button.isDisabled() || isGameOver) {
            return; // Block actions if it's not the player's turn, button is disabled, or game is over
        }

        String number = button.getText();
        button.setDisable(true);
        sendMove(number);
        checkForWin();  // Check for win after each move
        isMyTurn = false;
        updateTurnLabel();
    }

    private void sendMove(String number) {
        String message = "MOVE:" + number;
        try {
            if (isServer) {
                serverController.sendGameUpdate(message);
            } else {
                clientController.sendGameUpdate(message);
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error sending move: " + message, e);
            Platform.runLater(() -> statusLabel.setText("Error sending move"));
        }
    }

    private void listenForClientMoves() {
        try {
            while (!Thread.currentThread().isInterrupted() && !isGameOver) {
                String move = serverController.receiveGameUpdate();
                if (move != null) {
                    processIncomingMessage(move);
                }
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error receiving game update", e);
            Platform.runLater(() -> statusLabel.setText("Connection lost with Player 2."));
        }
    }

    private void listenForServerMoves() {
        try {
            while (!Thread.currentThread().isInterrupted() && !isGameOver) {
                String move = clientController.receiveGameUpdate();
                if (move != null) {
                    processIncomingMessage(move);
                }
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error receiving game update", e);
        }
    }

    private void processIncomingMessage(String message) {
        if (isGameOver) return; // Ignore messages if the game is over

        if (message.startsWith("MOVE:")) {
            String number = message.substring(5);
            Platform.runLater(() -> handleOpponentMove(number));
        } else if (message.startsWith("CHAT:")) {
            String chatMessage = message.substring(5);
            Platform.runLater(() -> appendChatMessage(chatMessage, "Opponent"));
        } else if (message.startsWith("WINNER:")) {
            String winner = message.substring(7);
            Platform.runLater(() -> showWinnerScreen(winner));
        }
    }

    private void handleOpponentMove(String number) {
        for (int i = 0; i < boardSize; i++) {
            for (int j = 0; j < boardSize; j++) {
                if (boardButtons[i][j].getText().equals(number)) {
                    boardButtons[i][j].setDisable(true);
                    break;
                }
            }
        }

        if (isBoardComplete()) {
            notifyPlayersAndShowWinner(isServer ? "Player 2" : "Player 1");
        } else {
            isMyTurn = true;
            updateTurnLabel();
        }
    }

    private boolean isBoardComplete() {
        for (int i = 0; i < boardSize; i++) {
            for (int j = 0; j < boardSize; j++) {
                if (!boardButtons[i][j].isDisabled()) {
                    return false;
                }
            }
        }
        return true;
    }

    private void checkForWin() {
        int completedLines = checkCompletedLines();

        if (completedLines >= boardSize) {
            notifyPlayersAndShowWinner(isServer ? "Player 1" : "Player 2");
        }
    }

    private int checkCompletedLines() {
        int count = 0;
        for (int i = 0; i < boardSize; i++) {
            if (checkRow(i)) count++;
            if (checkColumn(i)) count++;
        }
        if (checkLeftDiagonal()) count++;
        if (checkRightDiagonal()) count++;
        return count;
    }

    private boolean checkRow(int row) {
        for (int j = 0; j < boardSize; j++) {
            if (!boardButtons[row][j].isDisabled()) return false;
        }
        return true;
    }

    private boolean checkColumn(int col) {
        for (int i = 0; i < boardSize; i++) {
            if (!boardButtons[i][col].isDisabled()) return false;
        }
        return true;
    }

    private boolean checkLeftDiagonal() {
        for (int i = 0; i < boardSize; i++) {
            if (!boardButtons[i][i].isDisabled()) return false;
        }
        return true;
    }

    private boolean checkRightDiagonal() {
        for (int i = 0; i < boardSize; i++) {
            if (!boardButtons[i][boardSize - 1 - i].isDisabled()) return false;
        }
        return true;
    }

    private void notifyPlayersAndShowWinner(String winner) {
        String message = "WINNER:" + winner;
        try {
            if (isServer) {
                serverController.sendGameUpdate(message);
            } else {
                clientController.sendGameUpdate(message);
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error sending winner message: " + message, e);
            Platform.runLater(() -> statusLabel.setText("Error sending winner message"));
        }
        showWinnerScreen(winner);
    }

    private void showWinnerScreen(String winner) {
        isGameOver = true; // Mark the game as over
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/mediaworking/GameSmiths/winner-screen.fxml"));
            Parent root = loader.load();
            WinnerScreenController controller = loader.getController();
            controller.setStage(stage);
            controller.setWinner(winner);

            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error loading winner screen", e);
            statusLabel.setText(winner + " wins!");
        }
        disableAllButtons();
    }

    private void disableAllButtons() {
        for (int i = 0; i < boardSize; i++) {
            for (int j = 0; j < boardSize; j++) {
                boardButtons[i][j].setDisable(true);
            }
        }
    }

    private void updateTurnLabel() {
        statusLabel.setText(isServer
                ? (isMyTurn ? "Player 1's turn" : "Player 2's turn")
                : (isMyTurn ? "Player 2's turn" : "Player 1's turn"));
    }

    @FXML
    private void exitGame() {
        try {
            if (isServer) {
                serverController.stopServer();
            } else {
                clientController.disconnect();
            }
            if (moveListenerTask != null) {
                moveListenerTask.cancel(true);
            }
            executorService.shutdownNow();
            stage.close();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error during game exit", e);
        }
    }

    // --- Chat Box Methods ---
    private void initializeChat() {
        sendButton.setOnAction(e -> sendChatMessage());
        messageField.setOnAction(e -> sendChatMessage());
    }

    @FXML
    private void sendChatMessage() {
        String message = messageField.getText();
        if (message.isEmpty()) return;

        try {
            if (isServer) {
                serverController.sendChatMessage(message);
            } else {
                clientController.sendChatMessage(message);
            }
            appendChatMessage(message, "You");
            messageField.clear();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error sending chat message", e);
        }
    }

    private void appendChatMessage(String message, String sender) {
        chatArea.appendText(sender + ": " + message + "\n");
    }
    private void disableCloseButton() {
        stage.setOnCloseRequest(event -> {
            event.consume();
        });
    }
}
