package mediaworking.GameSmiths;

import javafx.fxml.FXML;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;

public class MediaPlayerController {

    @FXML
    private MediaView mediaView;

    private MediaPlayer mediaPlayer;
    private Runnable onVideoEnd;

    public void setOnVideoEnd(Runnable onVideoEnd) {
        this.onVideoEnd = onVideoEnd;
    }

    public void initialize() {
        loadAndPlayVideo();
    }

    private void loadAndPlayVideo() {
        String resourcePath = "/splash.mp4"; // Ensure this is in the correct location
        Media media = new Media(getClass().getResource(resourcePath).toExternalForm());
        mediaPlayer = new MediaPlayer(media);
        mediaView.setMediaPlayer(mediaPlayer);

        mediaPlayer.setOnReady(() -> {
            mediaView.setFitWidth(850);
            mediaView.setFitHeight(600);
            mediaPlayer.play();
        });

        mediaPlayer.setOnEndOfMedia(() -> {
            mediaPlayer.stop();
            if (onVideoEnd != null) {
                onVideoEnd.run(); // Trigger callback
            }
        });
    }
}
