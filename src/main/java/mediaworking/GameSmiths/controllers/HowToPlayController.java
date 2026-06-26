package mediaworking.GameSmiths.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class HowToPlayController {

    private static final Logger LOGGER = Logger.getLogger(HowToPlayController.class.getName());

    private Stage stage;

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    @FXML
    private void returnToMenu() {
        playButtonClickSound();
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/mediaworking/GameSmiths/menu.fxml"));
            Scene scene = new Scene(loader.load());
            MenuController controller = loader.getController();
            controller.setStage(stage);
            stage.setScene(scene);
            stage.setTitle("Bingo Game Menu");
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error returning to menu", e);
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
}