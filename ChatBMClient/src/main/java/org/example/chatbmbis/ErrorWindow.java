package org.example.chatbmbis;

import javafx.application.Platform;
import javafx.scene.control.Alert;

import java.util.Locale;
import java.util.ResourceBundle;

public class ErrorWindow {
    private static ResourceBundle bundle = ResourceBundle.getBundle("bundle.messages", Locale.getDefault());

    public static void instanceErrorWindow(String message) {
        String messageTransaleted = bundle.getString(message);
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("");
            alert.setContentText(messageTransaleted);
            alert.showAndWait();
        });
    }
}
