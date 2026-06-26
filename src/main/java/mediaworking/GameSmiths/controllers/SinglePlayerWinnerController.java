package mediaworking.GameSmiths.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.control.Button;
import mediaworking.GameSmiths.Player;

public class SinglePlayerWinnerController {

    @FXML private Label winnerLabel;
    @FXML private Label player1Label;
    @FXML private Label player2Label;
    @FXML private GridPane player1Board;
    @FXML private GridPane player2Board;

    public void setupWinners(Player player, Player bot, String winner) {
        if (winner.equals("Tie")) {
            winnerLabel.setText("It's a Tie!");
        } else {
            winnerLabel.setText("Winner: " + winner);
        }

        player1Label.setText(player.getName());
        player2Label.setText(bot.getName());

        displayBoard(player1Board, player);
        displayBoard(player2Board, bot);
    }

    private void displayBoard(GridPane board, Player player) {
        board.getChildren().clear();
        int size = player.getBoard().getSize();

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                int number = player.getBoard().getCard().get(i * size + j);
                Button button = new Button(String.valueOf(number));
                button.setMinSize(40, 40);
                button.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);

                if (number == 0) {  // 0 indicates a marked number
                    button.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-font-weight: bold;");
                    button.setText("X");
                } else {
                    button.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-weight: bold;");
                }

                board.add(button, j, i);
            }
        }
    }


    @FXML
    private void closeWindow() {
        winnerLabel.getScene().getWindow().hide();
    }
}