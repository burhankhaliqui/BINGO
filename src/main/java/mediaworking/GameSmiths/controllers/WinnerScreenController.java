package mediaworking.GameSmiths.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class WinnerScreenController {

    private static final Logger LOGGER = Logger.getLogger(WinnerScreenController.class.getName());

    @FXML
    private Label winnerLabel; // Label to display the winner message

    private Stage stage; // Reference to the current stage

    // Setter for the stage
    public void setStage(Stage stage) {
        this.stage = stage;
    }

    // Setter for the winner text
    public void setWinner(String winner) {
        if (winner.equalsIgnoreCase("Draw")) {
            winnerLabel.setText("It's a draw!"); // Handle a draw scenario
        } else {
            winnerLabel.setText(winner + " won the game!"); // Display winner message
        }
    }

    @FXML
    private void returnToMainMenu() {
        if (stage == null) {
            LOGGER.log(Level.SEVERE, "Stage is null. Ensure you call setStage() before using this controller.");
            return;
        }
        try {
            // Load the main menu FXML file
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/mediaworking/GameSmiths/menu.fxml"));
            Pane mainMenuPane = loader.load();

            // Get the MenuController and pass the stage if required
            MenuController menuController = loader.getController();
            menuController.setStage(stage);

            // Set the new scene to the current stage
            Scene mainMenuScene = new Scene(mainMenuPane);
            stage.setScene(mainMenuScene);
            stage.setTitle("Main Menu");

            // Show the main menu
            stage.show();
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error loading the main menu FXML", e);
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Unexpected error while returning to the main menu.", ex);
        }
    }
}
