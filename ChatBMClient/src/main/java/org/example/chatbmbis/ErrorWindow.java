package org.example.chatbmbis;

import javafx.application.Platform;
import javafx.scene.control.Alert;

public class ErrorWindow {

    public static void instanceErrorWindow(String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("");
            alert.setContentText(message);
            alert.showAndWait();
        });
    }
}
