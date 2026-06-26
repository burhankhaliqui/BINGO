package mediaworking.GameSmiths.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import mediaworking.GameSmiths.SinglePlayerBoardController;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SinglePlayerSetupController {

    private static final Logger LOGGER = Logger.getLogger(SinglePlayerSetupController.class.getName());

    @FXML
    private TextField playerNameField;

    @FXML
    private ChoiceBox<String> boardSizeChoice;

    @FXML
    private Button btnStartGame;

    @FXML
    private Button btnBack;

    private Stage stage;

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    @FXML
    private void initialize() {
        boardSizeChoice.getItems().addAll();
        boardSizeChoice.getSelectionModel().selectFirst();

        btnStartGame.setOnAction(event -> {
            playButtonClickSound();
            startGame();
        });
        btnBack.setOnAction(event -> {
            playButtonClickSound();
            goBackToGameMode();
        });
    }

    private void startGame() {
        String playerName = playerNameField.getText().trim();
        String boardSize = boardSizeChoice.getValue();

        if (playerName.isEmpty()) {
            showAlert("Player Name Required", "Please enter a player name before starting the game.");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/mediaworking/GameSmiths/SinglePlayerBoard.fxml"));
            Scene scene = new Scene(loader.load());
            SinglePlayerBoardController controller = loader.getController();

            // Setup the game with single-player settings
            controller.setupGame(playerName, getBoardSizeInt(boardSize));

            stage.setScene(scene);
            stage.setTitle("Bingo Game - Single Player");
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error loading single player board", e);
            showAlert("Error", "An error occurred while starting the game. Please try again.");
        }
    }

    private int getBoardSizeInt(String boardSize) {
        switch (boardSize) {
            case "3": return 3;
            case "5": return 5;
            case "7": return 7;
            default: return 5; // Default to 5x5 if something goes wrong
        }
    }

    private void goBackToGameMode() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/mediaworking/GameSmiths/GameMode.fxml"));
            Scene scene = new Scene(loader.load());
            GameModeController controller = loader.getController();
            controller.setStage(stage);
            stage.setScene(scene);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error loading game mode screen", e);
            showAlert("Error", "An error occurred while returning to the game mode screen. Please try again.");
        }
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

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}