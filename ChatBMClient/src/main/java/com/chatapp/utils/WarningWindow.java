package com.chatapp.utils;

import com.chatapp.constants.Constants;
import com.chatapp.controllers.Controller;
import javafx.application.Platform;
import javafx.scene.control.Alert;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;
import java.util.stream.Collectors;

public class WarningWindow {

    private static final ResourceBundle bundle = ResourceBundle.getBundle(Constants.BUNDLE_MESSAGES, Locale.getDefault());

    public static void instanceWarningWindow(String error) {
        if (error.isEmpty()) {
            return;
        }
        String errorName = SyntaxUtils.splitCommandLine(error)[1];
        if (getErrorTypes().contains(errorName)) {
            String translatedMessage = bundle.getString(errorName.replaceAll(" ", ""));
            Platform.runLater(() -> {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("");
                alert.setContentText(translatedMessage);
                alert.showAndWait();
            });
        }
    }
    // Método estático para leer y procesar el archivo y devolver los nombres de los errores
    public static List<String> getErrorTypes() {
        List<String> errores = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new InputStreamReader(Objects.requireNonNull(Controller.class.getResourceAsStream("/bundle/messages_es.properties"))))) {
            errores = br.lines()
                    .filter(linea -> !linea.trim().isEmpty() && !linea.trim().startsWith("//"))
                    .map(linea -> linea.split("=")[0].trim())
                    .collect(Collectors.toList());
        } catch (IOException e) {
            e.printStackTrace();
        }

        return errores;
    }
}
