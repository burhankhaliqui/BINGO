package mediaworking.GameSmiths;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;
import javafx.scene.control.Alert;
import mediaworking.GameSmiths.controllers.MenuController;
import mediaworking.GameSmiths.controllers.SinglePlayerWinnerController;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SinglePlayerBoardController {

    private static final Logger LOGGER = Logger.getLogger(SinglePlayerBoardController.class.getName());

    @FXML private GridPane playerBoard;
    @FXML private GridPane botBoard;
    @FXML private Label currentPlayerLabel;
    @FXML private Button exitButton;
    @FXML private Button returnToMenuButton;

    private Stage stage;
    private HumanPlayer player;
    private BotPlayer bot;
    private boolean gameOver;
    private List<Button> playerButtons;
    private List<Button> botButtons;

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void setupGame(String playerName, int boardSize) {
        this.player = new HumanPlayer(playerName, boardSize);
        this.bot = new BotPlayer("Computer", boardSize);
        this.gameOver = false;

        initializeBoards();
        updateCurrentPlayerLabel();
    }

    private void initializeBoards() {
        playerButtons = initializePlayerBoard(playerBoard, player);
        botButtons = initializePlayerBoard(botBoard, bot);
    }

    private List<Button> initializePlayerBoard(GridPane board, Player player) {
        board.getChildren().clear();
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
                    "-fx-background-color: #e74c3c;" +  // Single color for clicked buttons
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

        if (player.getBoard().getCard().contains(number)) {
            makeMove(number);
            if (!gameOver) {
                updateCurrentPlayerLabel();
                makeBotMove();
            }
        } else {
            showAlert("Invalid Move", "This number is not on your board.");
        }
    }

    private void makeMove(int number) {
        player.getBoard().markNumber(number);
        bot.getBoard().markNumber(number);
        updateButtonState(number, playerButtons);
        updateButtonState(number, botButtons);

        if (checkForWin()) {
            endGame();
        }
    }

    private void makeBotMove() {
        Platform.runLater(() -> {
            try {
                Thread.sleep(1000);
                int botNumber = bot.playTurn();
                makeMove(botNumber);
                updateCurrentPlayerLabel();
            } catch (InterruptedException e) {
                LOGGER.log(Level.SEVERE, "Bot move interrupted", e);
                Thread.currentThread().interrupt();
            }
        });
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
        ArrayList<String> winners = Winner.checkForWinners(player, bot);
        return !winners.isEmpty();
    }

    private void endGame() {
        gameOver = true;
        ArrayList<String> winners = Winner.checkForWinners(player, bot);
        showWinnerScreen(winners);
    }

    private void updateCurrentPlayerLabel() {
        if (!gameOver) {
            currentPlayerLabel.setText(player.getName() + "'s Turn");
            currentPlayerLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");
        } else {
            currentPlayerLabel.setText("Game Over");
            currentPlayerLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: red;");
        }
    }

    private void showWinnerScreen(ArrayList<String> winners) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/mediaworking/GameSmiths/SinglePlayerWinnerScreen.fxml"));
            Scene winnerScene = new Scene(loader.load());

            SinglePlayerWinnerController controller = loader.getController();

            String winner;
            if (winners.contains(player.getName())) {
                winner = player.getName();
            } else if (winners.contains(bot.getName())) {
                winner = bot.getName();
            } else {
                winner = "Tie";
            }

            controller.setupWinners(player, bot, winner);

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
        LOGGER.info("Exit button clicked");
        if (stage != null) {
            stage.close();
        } else {
            LOGGER.warning("Stage is null, attempting to close current window");
            // Attempt to close the current window if stage is null
            Platform.runLater(() -> {
                Stage currentStage = (Stage) exitButton.getScene().getWindow();
                if (currentStage != null) {
                    currentStage.close();
                } else {
                    LOGGER.severe("Unable to close window: current stage is null");
                }
            });
        }
    }

    @FXML
    private void returnToMenu() {
        LOGGER.info("Return to menu button clicked");
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/mediaworking/GameSmiths/menu.fxml"));
            Scene scene = new Scene(loader.load());
            MenuController controller = loader.getController();

            if (stage != null) {
                controller.setStage(stage);
                stage.setScene(scene);
                stage.setTitle("Bingo Game Menu");
            } else {
                LOGGER.warning("Stage is null, attempting to use current window");
                Stage currentStage = (Stage) returnToMenuButton.getScene().getWindow();
                if (currentStage != null) {
                    controller.setStage(currentStage);
                    currentStage.setScene(scene);
                    currentStage.setTitle("Bingo Game Menu");
                } else {
                    LOGGER.severe("Unable to return to menu: current stage is null");
                    showAlert("Error", "Unable to return to menu. Please restart the game.");
                }
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error returning to menu", e);
            showAlert("Error", "An error occurred while returning to the menu. Please try again.");
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