package mediaworking.GameSmiths.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import mediaworking.GameSmiths.HumanPlayer;
import mediaworking.GameSmiths.Player;
import mediaworking.GameSmiths.Winner;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MultiplayerBoardController {

    private static final Logger LOGGER = Logger.getLogger(MultiplayerBoardController.class.getName());

    @FXML private GridPane playerBoard;
    @FXML private VBox playerContainer;
    @FXML private Label currentPlayerLabel;
    @FXML private Button exitButton;
    @FXML private Button returnToMenuButton;

    private Stage stage;
    private List<Player> players;
    private Player currentPlayer;
    private boolean gameOver;
    private int currentPlayerIndex;
    private List<List<Button>> playerButtons; // Buttons for all players' boards

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void setupGame(List<String> playerNames, int boardSize) {
        // Initialize players and their boards
        players = new ArrayList<>();
        playerButtons = new ArrayList<>();
        for (String name : playerNames) {
            players.add(new HumanPlayer(name, boardSize));
        }

        if (players.size() < 2 || players.size() > 5) {
            throw new IllegalArgumentException("Number of players must be between 2 and 5.");
        }

        currentPlayerIndex = 0;
        currentPlayer = players.get(currentPlayerIndex);
        gameOver = false;

        initializeBoards();
        updateCurrentPlayerLabel();
        updateBoardVisibility();
    }

    private void initializeBoards() {
        // Create boards for all players but display only the current player's board
        playerButtons.clear();
        for (Player player : players) {
            playerButtons.add(initializePlayerBoard(player));
        }
    }

    private List<Button> initializePlayerBoard(Player player) {
        GridPane board = new GridPane();
        board.setHgap(5);
        board.setVgap(5);
        board.setStyle("-fx-background-color: rgba(255,255,255,0.1); -fx-padding: 10; -fx-background-radius: 10;");

        int size = player.getBoard().getSize();
        List<Button> buttons = new ArrayList<>();

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                int number = player.getBoard().getCard().get(i * size + j);
                Button button = createNumberButton(number);
                board.add(button, j, i);
                buttons.add(button);
            }
        }

        if (player == currentPlayer) {
            playerBoard.getChildren().setAll(board.getChildren());
        }

        return buttons;
    }

    private Button createNumberButton(int number) {
        Button button = new Button(String.valueOf(number));
        button.setMinSize(50, 50);
        button.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);

        // Default styling
        button.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 16px; -fx-effect: none;");

        // On-hover effect: Shining glow
        button.setOnMouseEntered(e -> button.setStyle(
                "-fx-background-color: linear-gradient(to bottom, #4facfe, #00f2fe);" +
                        "-fx-text-fill: white;" +
                        "-fx-font-weight: bold;" +
                        "-fx-font-size: 16px;" +
                        "-fx-effect: dropshadow(three-pass-box, rgba(0, 255, 255, 0.6), 10, 0, 0, 0);"
        ));

        // On-exit effect: Reset to default
        button.setOnMouseExited(e -> button.setStyle(
                "-fx-background-color: #3498db;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-weight: bold;" +
                        "-fx-font-size: 16px;" +
                        "-fx-effect: none;"
        ));

        // On-click action
        button.setOnAction(e -> {
            playButtonClickSound();
            handleNumberClick(number);

            // Override hover and default style on click
            button.setStyle(
                    "-fx-background-color: #e74c3c; " +  // Uniform color for all clicked buttons
                            "-fx-text-fill: white;" +
                            "-fx-font-weight: bold;" +
                            "-fx-font-size: 16px;" +
                            "-fx-effect: none;"
            );
            button.setText("X");
            button.setDisable(true); // Ensure it can't be clicked again
        });

        GridPane.setFillWidth(button, true);
        GridPane.setFillHeight(button, true);

        return button;
    }



    private void playButtonClickSound() {
        String soundFile = "/mediaworking/GameSmiths/button-clickk.mp3";
        try {
            java.net.URL resource = getClass().getResource(soundFile);
            if (resource == null) {
                LOGGER.log(Level.WARNING, "Sound file not found: {0}", soundFile);
                return;
            }
            Media sound = new Media(resource.toExternalForm());
            MediaPlayer mediaPlayer = new MediaPlayer(sound);
            mediaPlayer.play();
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Error playing button click sound", e);
        }
    }

    private void handleNumberClick(int number) {
        if (gameOver) return;

        if (currentPlayer.getBoard().getCard().contains(number)) {
            makeMove(number);
            if (!gameOver) {
                switchPlayer();
                updateBoardVisibility();
            }
        } else {
            showAlert("Invalid Move", "This number is not on the current player's board.");
        }
    }

    private void makeMove(int number) {
        for (Player player : players) {
            player.getBoard().markNumber(number);
        }

        for (int i = 0; i < players.size(); i++) {
            updateButtonState(number, playerButtons.get(i));
        }

        if (checkForWin()) {
            endGame();
        }
    }

    private void updateButtonState(int number, List<Button> buttons) {
        for (Button button : buttons) {
            if (button.getText().equals(String.valueOf(number))) {
                button.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-font-weight: bold;");
                button.setText("X");
                button.setDisable(true);
            }
        }
    }

    private boolean checkForWin() {
        ArrayList<String> winners = Winner.checkForWinners(players.toArray(new Player[0]));
        return !winners.isEmpty();
    }

    private void switchPlayer() {
        currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
        currentPlayer = players.get(currentPlayerIndex);
        updateCurrentPlayerLabel();
    }

    private void updateCurrentPlayerLabel() {
        if (!gameOver) {
            currentPlayerLabel.setText(currentPlayer.getName() + "'s Turn");
            currentPlayerLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");
        } else {
            currentPlayerLabel.setText("Game Over");
            currentPlayerLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: red;");
        }
    }

    private void updateBoardVisibility() {
        GridPane currentBoard = new GridPane();
        currentBoard.getChildren().setAll(playerButtons.get(currentPlayerIndex));
        playerBoard.getChildren().setAll(currentBoard.getChildren());
    }

    private void endGame() {
        gameOver = true;
        ArrayList<String> winners = Winner.checkForWinners(players.toArray(new Player[0]));
        showWinnerScreen(winners);
    }

    private void showWinnerScreen(ArrayList<String> winners) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/mediaworking/GameSmiths/MultiplayerWinnerScreen.fxml"));
            Scene winnerScene = new Scene(loader.load());

            MultiplayerWinnerController controller = loader.getController();
            controller.setupWinners((ArrayList<Player>) players, winners);

            Stage winnerStage = new Stage();
            winnerStage.setScene(winnerScene);
            winnerStage.setTitle("Game Result");
            winnerStage.show();

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error showing winner screen", e);
        }
    }

    @FXML
    private void exitGame() {
        stage.close();
    }

    @FXML
    private void returnToMenu() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/mediaworking/GameSmiths/menu.fxml"));
            Scene scene = new Scene(loader.load());
            MenuController controller = loader.getController();
            controller.setStage(stage);
            stage.setScene(scene);
            stage.setTitle("Bingo Game Menu");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error returning to menu", e);
        }
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
}
}