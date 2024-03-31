package com.chatapp.utils;

import javafx.application.Platform;
import javafx.scene.control.Alert;

import java.util.Locale;
import java.util.ResourceBundle;

public class WarningWindow {
    private static ResourceBundle bundle = ResourceBundle.getBundle("bundle.messages", Locale.getDefault());

    public static void instanceWarningWindow(String message) {
        String messageTransaleted = bundle.getString(message.replaceAll(" ", ""));
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("");
            alert.setContentText(messageTransaleted);
            alert.showAndWait();
        });
    }
}
