package mediaworking.GameSmiths.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.collections.FXCollections;
import javafx.stage.Stage;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.effect.Reflection;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import java.util.HashSet;

public class MultiplayerSetupController {

    @FXML private RadioButton onlineRadio, offlineRadio, serverRadio, clientRadio;
    @FXML private VBox onlineOptions, offlineOptions, playerNamesBox;
    @FXML private TextField ipAddressField, portField;
    @FXML private ComboBox<Integer> playerCountCombo, boardSizeCombo;
    @FXML private Button startGameButton, exitButton;
    @FXML private Button backButton;

    private Stage stage;
    private static final Logger LOGGER = Logger.getLogger(MultiplayerSetupController.class.getName());
    private ServerController serverController;
    private ClientController clientController;

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    @FXML
    public void initialize() {
        ToggleGroup modeGroup = new ToggleGroup();
        onlineRadio.setToggleGroup(modeGroup);
        offlineRadio.setToggleGroup(modeGroup);

        ToggleGroup roleGroup = new ToggleGroup();
        serverRadio.setToggleGroup(roleGroup);
        clientRadio.setToggleGroup(roleGroup);

        onlineRadio.setOnAction(e -> {
            playButtonClickSound();
            toggleOptions();
        });
        offlineRadio.setOnAction(e -> {
            playButtonClickSound();
            toggleOptions();
        });

        playerCountCombo.setItems(FXCollections.observableArrayList(2, 3, 4,5));
        playerCountCombo.setValue(2);
        playerCountCombo.setOnAction(e -> {
            playButtonClickSound();
            updatePlayerNameFields();
        });

        boardSizeCombo.setItems(FXCollections.observableArrayList(3, 5, 7));
        boardSizeCombo.setValue(3);
        boardSizeCombo.setOnAction(e -> playButtonClickSound());

        serverRadio.setOnAction(e -> playButtonClickSound());
        clientRadio.setOnAction(e -> playButtonClickSound());

        offlineRadio.setSelected(true);
        toggleOptions();
        updatePlayerNameFields();
        if (backButton != null) {
            applyHoverEffect(backButton);
            backButton.setOnAction(e -> handleBackButton());
        }

        if (startGameButton != null) applyHoverEffect(startGameButton);
        if (exitButton != null) applyHoverEffect(exitButton);
    }

    private void toggleOptions() {
        boolean isOnline = onlineRadio.isSelected();
        onlineOptions.setVisible(isOnline);
        onlineOptions.setManaged(isOnline);
        offlineOptions.setVisible(!isOnline);
        offlineOptions.setManaged(!isOnline);
    }

    private void updatePlayerNameFields() {
        playerNamesBox.getChildren().clear();
        int playerCount = playerCountCombo.getValue();
        for (int i = 1; i <= playerCount; i++) {
            TextField nameField = new TextField();
            nameField.setPromptText("Player " + i + " Name");
            playerNamesBox.getChildren().add(nameField);
        }
    }

    @FXML
    private void startGame() {
        playButtonClickSound();
        boolean isOnline = onlineRadio.isSelected();
        int boardSize = boardSizeCombo.getValue();

        if (isOnline) {
            boolean isServer = serverRadio.isSelected();
            String ipAddress = ipAddressField.getText();
            String portText = portField.getText();

            if (!validateIpAddress(ipAddress)) {
                showAlert("Invalid IP Address", "Please enter a valid IP address.");
                return;
            }

            if (!validatePort(portText)) {
                showAlert("Invalid Port", "Please enter a valid port number (1-65535).");
                return;
            }

            int port = Integer.parseInt(portText);
            startOnlineGame(isServer, ipAddress, port, boardSize);
        } else {
            List<String> playerNames = playerNamesBox.getChildren().stream()
                    .map(node -> ((TextField) node).getText().trim())
                    .collect(Collectors.toList());

            if (!validatePlayerNames(playerNames)) {
                showAlert("Invalid Player Names", "Please ensure all player names are unique and non-empty.");
                return;
            }

            startOfflineGame(playerNames, boardSize);
        }
    }

    private boolean validateIpAddress(String ipAddress) {
        String ipRegex = "^(\\d{1,3}\\.){3}\\d{1,3}$";
        return Pattern.matches(ipRegex, ipAddress);
    }

    private boolean validatePort(String port) {
        try {
            int portNumber = Integer.parseInt(port);
            return portNumber > 0 && portNumber <= 65535;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private boolean validatePlayerNames(List<String> playerNames) {
        // Check if all names are non-empty
        if (!playerNames.stream().allMatch(name -> !name.trim().isEmpty())) {
            return false;
        }

        // Check for duplicate names
        HashSet<String> uniqueNames = new HashSet<>();
        for (String name : playerNames) {
            if (!uniqueNames.add(name.trim().toLowerCase())) {
                return false; // Duplicate name found
            }
        }

        return true;
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void startOnlineGame(boolean isServer, String ipAddress, int port, int boardSize) {
        if (isServer) {
            serverController = new ServerController(port);
            new Thread(() -> {
                serverController.startServer();
                javafx.application.Platform.runLater(() -> showGameBoard("Server started. Waiting for players..."));
            }).start();
        } else {
            clientController = new ClientController(ipAddress, port);
            new Thread(() -> {
                clientController.connectToServer();
                javafx.application.Platform.runLater(() -> showGameBoard("Connected to server!"));
            }).start();
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/mediaworking/GameSmiths/OnlineGameBoard.fxml"));
            Scene scene = new Scene(loader.load());
            OnlineGameBoardController controller = loader.getController();
            controller.setStage(stage);

            controller.setupGame(isServer, serverController, clientController, boardSize);

            stage.setScene(scene);
            stage.setTitle("Bingo Game - Online " + (isServer ? "Server" : "Client"));

        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error loading online game board", e);
        }
    }

    private void showGameBoard(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Game Board");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void startOfflineGame(List<String> playerNames, int boardSize) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/mediaworking/GameSmiths/MultiplayerBoard.fxml"));
            Scene scene = new Scene(loader.load());
            MultiplayerBoardController controller = loader.getController();
            controller.setStage(stage);

            controller.setupGame(playerNames, boardSize);

            stage.setScene(scene);
            stage.setTitle("Bingo Game - Multiplayer");

        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error loading multiplayer board", e);
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

    @FXML
    private void handleBackButton() {
        playButtonClickSound();
        goBackToGameMode();
    }

    private void goBackToGameMode() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/mediaworking/GameSmiths/GameMode.fxml"));
            Scene scene = new Scene(loader.load());
            GameModeController controller = loader.getController();
            controller.setStage(stage);
            stage.setScene(scene);
        } catch (IOException e) {
            Logger.getLogger(MultiplayerSetupController.class.getName()).log(Level.SEVERE, "Error loading game mode screen", e);
        }
    }

    private void applyHoverEffect(Button button) {
        // Basic style setup
        button.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px;");

        // Reflection effect for the shine
        Reflection reflection = new Reflection();
        reflection.setFraction(0.7);
        reflection.setTopOpacity(0.5);
        reflection.setBottomOpacity(0.0);
        reflection.setTopOffset(-5);

        // Add hover events
        button.setOnMouseEntered(e -> {
            button.setStyle("-fx-background-color: #2980b9; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px;");
            button.setEffect(reflection);
        });

        button.setOnMouseExited(e -> {
            button.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px;");
            button.setEffect(null);
        });
    }
}