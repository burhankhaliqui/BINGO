module mediaworking.javafxfinalsetupburhan {
    requires javafx.controls;
    requires javafx.fxml;

    requires java.logging;
    requires java.desktop;
    requires javafx.media;

    opens mediaworking.GameSmiths to javafx.fxml;
    exports mediaworking.GameSmiths;
    exports mediaworking.GameSmiths.controllers;
    opens mediaworking.GameSmiths.controllers to javafx.fxml;
    exports mediaworking.GameSmiths.Model;
    opens mediaworking.GameSmiths.Model to javafx.fxml;
}