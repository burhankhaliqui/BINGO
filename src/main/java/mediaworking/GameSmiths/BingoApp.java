package mediaworking.GameSmiths;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import mediaworking.GameSmiths.controllers.MenuController;

import java.net.URL;

public class BingoApp extends Application {

    private MediaPlayer backgroundMusicPlayer;

    @Override
    public void start(Stage stage) throws Exception {
        initializeBackgroundMusic();
        showSplashScreen(stage);
    }

    private void initializeBackgroundMusic() {
        try {
            String musicFile = "/mediaworking/GameSmiths/12343.mp3";
            URL resource = getClass().getResource(musicFile);
            if (resource == null) {
                System.err.println("Could not find audio file: " + musicFile);
                return;
            }
            Media sound = new Media(resource.toExternalForm());
            backgroundMusicPlayer = new MediaPlayer(sound);
            backgroundMusicPlayer.setCycleCount(MediaPlayer.INDEFINITE);
            backgroundMusicPlayer.setVolume(0.5); // Set volume to 50%
            backgroundMusicPlayer.play();
        } catch (Exception e) {
            System.err.println("Error loading background music: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void showSplashScreen(Stage stage) {
        try {
            FXMLLoader splashLoader = new FXMLLoader(getClass().getResource("/mediaworking/GameSmiths/Media-Player.fxml"));
            if (splashLoader.getLocation() == null) {
                System.err.println("Could not find FXML file. Check the path.");
                return;
            }
            Parent splashRoot = splashLoader.load();
            Scene splashScene = new Scene(splashRoot, 850, 600);

            stage.setScene(splashScene);
            stage.setTitle("Splash Screen");
            stage.show();

            MediaPlayerController splashController = splashLoader.getController();
            splashController.setOnVideoEnd(() -> {
                loadMainMenu(stage);
            });

        } catch (Exception e) {
            System.err.println("Error loading splash screen: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void loadMainMenu(Stage stage) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/mediaworking/GameSmiths/menu.fxml"));
            Scene scene = new Scene(loader.load());
            stage.setScene(scene);
            stage.setTitle("Bingo Game Menu");

            MenuController controller = loader.getController();
            controller.setStage(stage);
            stage.show();
        } catch (Exception e) {
            System.err.println("Error loading main menu: " + e.getMessage());
            e.printStackTrace();
        }
    }


    @Override
    public void stop() throws Exception {
        if (backgroundMusicPlayer != null) {
            backgroundMusicPlayer.stop();
        }
        super.stop();
    }

    public static void main(String[] args) {
        launch(args);
    }
}