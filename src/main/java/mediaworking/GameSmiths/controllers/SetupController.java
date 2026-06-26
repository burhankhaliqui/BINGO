package mediaworking.GameSmiths.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SetupController {

    private static final Logger LOGGER = Logger.getLogger(SetupController.class.getName());

    @FXML private TextField playerNameField;
    @FXML private Button addPlayerButton;
    @FXML private Button startGameButton;
    @FXML private VBox playerListContainer;
    @FXML private Label errorMessageLabel;
    @FXML private ComboBox<Integer> boardSizeComboBox;

    private List<String> playerNames;
    private Stage stage;

    public void initialize() {
        playerNames = new ArrayList<>();
        startGameButton.setDisable(true);
        errorMessageLabel.setVisible(false);

        // Initialize board size options
        boardSizeComboBox.getItems().addAll(3, 5, 7);
        boardSizeComboBox.setValue(5); // Default board size
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    @FXML
    private void addPlayer() {
        String playerName = playerNameField.getText().trim();

        if (!playerName.isEmpty() && !playerNames.contains(playerName)) {
            playerNames.add(playerName);
            updatePlayerListUI();
            playerNameField.clear();

            // Enable the start button if at least two players are added
            startGameButton.setDisable(playerNames.size() < 2);
        } else {
            showError("Invalid player name or player already added.");
        }
    }

    private void updatePlayerListUI() {
        playerListContainer.getChildren().clear();
        for (String playerName : playerNames) {
            Label playerLabel = new Label(playerName);
            playerLabel.setStyle("-fx-font-size: 16px;");
            playerListContainer.getChildren().add(playerLabel);
        }
    }

    private void showError(String message) {
        errorMessageLabel.setText(message);
        errorMessageLabel.setVisible(true);
    }

    @FXML
    private void startGame() {
        if (playerNames.size() >= 2) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/mediaworking/GameSmiths/MultiplayerBoard.fxml"));
                VBox boardRoot = loader.load();

                MultiplayerBoardController boardController = loader.getController();
                boardController.setStage(stage);

                int boardSize = boardSizeComboBox.getValue();
                boardController.setupGame(playerNames, boardSize);

                Scene boardScene = new Scene(boardRoot);
                stage.setScene(boardScene);
                stage.setTitle("Multiplayer Bingo Game");
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Error starting the game", e);
                showError("Failed to start the game.");
            }
        } else {
            showError("At least two players are required to start the game.");
        }
    }

    @FXML
    private void returnToMenu() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/mediaworking/GameSmiths/menu.fxml"));
            VBox menuRoot = loader.load();

            MenuController menuController = loader.getController();
            menuController.setStage(stage);

            Scene menuScene = new Scene(menuRoot);
            stage.setScene(menuScene);
            stage.setTitle("Bingo Game Menu");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error returning to menu", e);
        }
    }
}