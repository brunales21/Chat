package org.example.chatbmbis;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class ErrorDialog extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Ventana Principal");

        Button mostrarErrorButton = new Button("Mostrar Error");
        mostrarErrorButton.setOnAction(e -> mostrarVentanaError("Este es un mensaje de error."));

        StackPane root = new StackPane();
        root.getChildren().add(mostrarErrorButton);

        primaryStage.setScene(new Scene(root, 300, 200));
        primaryStage.show();
    }

    public static void mostrarVentanaError(String mensaje) {
        // Crear una nueva ventana para el mensaje de error
        Platform.runLater(() -> {
            Stage ventanaError = new Stage();
            ventanaError.initModality(Modality.APPLICATION_MODAL);
            ventanaError.initStyle(StageStyle.UTILITY);
            ventanaError.setTitle("Error");

            // Crear una etiqueta para mostrar el mensaje de error
            Label label = new Label(mensaje);

            // Crear un botón "Aceptar"
            Button botonAceptar = new Button("Aceptar");
            botonAceptar.setOnAction(e -> ventanaError.close());

            // Crear el diseño de la ventana
            StackPane layout = new StackPane();
            layout.getChildren().addAll(label, botonAceptar);

            // Crear la escena
            Scene scene = new Scene(layout, 300, 100);

            // Establecer la escena en la ventana
            ventanaError.setScene(scene);

            // Mostrar la ventana y bloquear la interacción con otras ventanas hasta que se cierre esta
            ventanaError.showAndWait();
        });
    }
}
