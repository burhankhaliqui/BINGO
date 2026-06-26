package mediaworking.GameSmiths.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import mediaworking.GameSmiths.Player;

import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MultiplayerWinnerController {

    private static final Logger LOGGER = Logger.getLogger(MultiplayerWinnerController.class.getName());

    @FXML
    private Label winnerLabel;

    @FXML
    private VBox playersContainer;

    @FXML
    private Button returnButton;

    private Stage stage;

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void setupWinners(ArrayList<Player> players, ArrayList<String> winners) {
        if (winners.size() > 1) {
            winnerLabel.setText("It's a tie!");
        } else if (!winners.isEmpty()) {
            winnerLabel.setText("Winner: " + winners.get(0));
        } else {
            winnerLabel.setText("No winner");
        }

        playersContainer.getChildren().clear();

        for (Player player : players) {
            VBox playerBox = new VBox(10);
            playerBox.setStyle("-fx-background-color: rgba(255,255,255,0.2); -fx-padding: 10; -fx-border-radius: 10; -fx-background-radius: 10; -fx-border-color: black; -fx-border-width: 2;");
            Label playerLabel = new Label(player.getName() + "'s Board");
            playerLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #333;");
            GridPane board = createBoard(player);

            playerBox.getChildren().addAll(playerLabel, board);
            playersContainer.getChildren().add(playerBox);
        }
    }

    private GridPane createBoard(Player player) {
        GridPane board = new GridPane();
        int size = (int) Math.sqrt(player.getBoard().getCard().size());

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                int number = player.getBoard().getCard().get(i * size + j);
                Label label = new Label(String.valueOf(number));
                label.setStyle("-fx-background-color: white; -fx-border-color: black; -fx-border-width: 1; " +
                        "-fx-padding: 5; -fx-alignment: center; -fx-font-weight: bold; -fx-min-width: 30; -fx-min-height: 30;");

                if (player.getBoard().isMarked(number)) {
                    label.setStyle(label.getStyle() + "-fx-background-color: lightcoral;");
                }

                board.add(label, j, i);
            }
        }
        return board;
    }


    @FXML
    private void returnToMainMenu() {
        System.out.println("Return to Main Menu button clicked!"); // Debugging line
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/mediaworking/GameSmiths/menu.fxml"));
            Scene scene = new Scene(loader.load());

            MenuController controller = loader.getController();
            controller.setStage(stage);
            Stage WinnerStage = new Stage();

            WinnerStage.setScene(scene);
            WinnerStage.setTitle("Bingo Game Menu");
            WinnerStage.show();

        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error loading FXML file: /mediaworking/GameSmiths/menu.fxml", e);
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Unexpected error while returning to main menu.");
        }}}
