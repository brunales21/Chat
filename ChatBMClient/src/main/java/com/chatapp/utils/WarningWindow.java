package com.chatapp.utils;

import com.chatapp.constants.Constants;
import javafx.application.Platform;
import javafx.scene.control.Alert;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;
import java.util.ResourceBundle;

public class WarningWindow {

    private static final ResourceBundle bundle = ResourceBundle.getBundle(Constants.BUNDLE_MESSAGES, Locale.getDefault());

    public static void instanceWarningWindow(String error) {
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
    public static ArrayList<String> getErrorTypes() {
        // ArrayList para almacenar los nombres de los errores
        ArrayList<String> errores = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader("src/main/resources/bundle/messages_es.properties"))) {
            String linea;

            // Leer cada línea del archivo
            while ((linea = br.readLine()) != null) {
                // Verificar si la línea está vacía o es un comentario
                if (!linea.trim().isEmpty() && !linea.trim().startsWith("//")) {
                    // Dividir la línea por "=" y tomar la primera parte como nombre de error
                    String[] partes = linea.split("=");
                    // Añadir el nombre de error a la lista
                    errores.add(partes[0].trim());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Devolver los nombres de los errores encontrados
        return errores;
    }
}
