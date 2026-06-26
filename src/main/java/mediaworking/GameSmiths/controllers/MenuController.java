package mediaworking.GameSmiths.controllers;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.application.Platform;
import javafx.util.Duration;

import java.io.IOException;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MenuController {

    private static final Logger LOGGER = Logger.getLogger(MenuController.class.getName());

    @FXML
    private Button exitButton;

    @FXML
    private Button playButton;

    @FXML
    private Button howToPlayButton;

    @FXML
    private Label statusLabel;

    private Stage stage;

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    @FXML
    public void initialize() {
        LOGGER.info("MenuController initialized");

        assert exitButton != null : "fx:id=\"exitButton\" not injected.";
        assert playButton != null : "fx:id=\"playButton\" not injected.";
        assert howToPlayButton != null : "fx:id=\"howToPlayButton\" not injected.";

        exitButton.setOnAction(event -> {
            playButtonClickSound();
            onExitButtonClicked();
        });

        playButton.setOnAction(event -> {
            playButtonClickSound();
            onPlayButtonClicked();
        });

        howToPlayButton.setOnAction(event -> {
            playButtonClickSound();
            onHowToPlayButtonClicked();
        });

        // Add shine effect to buttons
        addShineEffect(playButton);
        addShineEffect(howToPlayButton);
        addShineEffect(exitButton);
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

    private void addShineEffect(Button button) {
        DropShadow dropShadow = new DropShadow();
        dropShadow.setColor(Color.LIGHTBLUE);
        dropShadow.setRadius(15);
        dropShadow.setSpread(0.5);
        dropShadow.setOffsetX(0);
        dropShadow.setOffsetY(0);

        button.setOnMouseEntered(event -> {
            Timeline timeline = new Timeline(
                    new KeyFrame(Duration.ZERO, new KeyValue(dropShadow.offsetXProperty(), -50)),
                    new KeyFrame(Duration.seconds(0.5), new KeyValue(dropShadow.offsetXProperty(), 50))
            );
            timeline.setCycleCount(1);
            button.setEffect(dropShadow);
            timeline.play();
        });

        button.setOnMouseExited(event -> {
            button.setEffect(null);
        });
    }

    @FXML
    private void onPlayButtonClicked() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/mediaworking/GameSmiths/GameMode.fxml"));
            Scene scene = new Scene(loader.load());
            GameModeController controller = loader.getController();
            controller.setStage(stage);
            stage.setScene(scene);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error loading game mode selection", e);
            statusLabel.setText("Error loading game mode selection.");
        }
    }

    @FXML
    private void onSettingsButtonClicked() {
        statusLabel.setText("Opening settings...");
    }

    @FXML
    private void onHowToPlayButtonClicked() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/mediaworking/GameSmiths/howToPlay.fxml"));
            Scene scene = new Scene(loader.load());
            HowToPlayController controller = loader.getController();
            controller.setStage(stage);
            stage.setScene(scene);
            stage.setTitle("How to Play Bingo");
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error loading How to Play screen", e);
            statusLabel.setText("Error loading How to Play screen.");
        }
    }

    @FXML
    private void onExitButtonClicked() {
        LOGGER.info("Exit button clicked. Confirming exit...");
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Exit Application");
        alert.setHeaderText("Are you sure you want to exit?");
        alert.setContentText("Click OK to exit, or Cancel to return.");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            LOGGER.info("User confirmed exit. Closing application.");
            Platform.exit();
            System.exit(0);
        } else {
            LOGGER.info("Exit cancelled by user.");
        }
    }
}