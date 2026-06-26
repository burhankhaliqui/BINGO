package mediaworking.GameSmiths.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.effect.DropShadow;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GameModeController {

    private static final Logger LOGGER = Logger.getLogger(GameModeController.class.getName());

    @FXML
    private Button btnSinglePlayer;

    @FXML
    private Button btnMultiplayer;

    @FXML
    private Button btnBack;

    private Stage stage;
    @FXML
    private Pane siiiii;  // Reference to the Pane

    @FXML
    private AnchorPane ffff;

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    @FXML
    private void initialize() {
        btnSinglePlayer.setOnAction(event -> {
            playButtonClickSound();
            loadSinglePlayerGame();
        });
        btnMultiplayer.setOnAction(event -> {
            playButtonClickSound();
            loadMultiplayerGame();
        });
        btnBack.setOnAction(event -> {
            playButtonClickSound();
            goBackToMainMenu();
        });

        // Apply shine effect on hover
        applyShineEffect(btnSinglePlayer);
        applyShineEffect(btnMultiplayer);
        applyShineEffect(btnBack);
    }

    private void loadSinglePlayerGame() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/mediaworking/GameSmiths/SinglePlayerSetup.fxml"));
            Scene scene = new Scene(loader.load());
            SinglePlayerSetupController controller = loader.getController();
            controller.setStage(stage);
            stage.setScene(scene);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error loading single player setup", e);
        }
    }

    private void loadMultiplayerGame() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/mediaworking/GameSmiths/MultiplayerSetup.fxml"));
            Scene scene = new Scene(loader.load());
            MultiplayerSetupController controller = loader.getController();
            controller.setStage(stage);
            stage.setScene(scene);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error loading multiplayer setup", e);
        }
    }

    private void goBackToMainMenu() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/mediaworking/GameSmiths/menu.fxml"));
            Scene scene = new Scene(loader.load());
            MenuController controller = loader.getController();
            controller.setStage(stage);
            stage.setScene(scene);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error loading main menu", e);
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

    // Method to apply shine hover effect (no color change, only shine)
    private void applyShineEffect(Button button) {
        // Default effect is no shine
        DropShadow shineEffect = new DropShadow();
        shineEffect.setColor(javafx.scene.paint.Color.WHITE);
        shineEffect.setRadius(15);
        shineEffect.setOffsetX(0);
        shineEffect.setOffsetY(0);
        button.setEffect(null);

        // Apply shine effect on hover
        button.setOnMouseEntered(e -> {
            shineEffect.setSpread(0.4);  // Control the shine spread
            button.setEffect(shineEffect);
        });

        // Remove shine effect when mouse exits
        button.setOnMouseExited(e -> {
            button.setEffect(null);
        });
    }
}
