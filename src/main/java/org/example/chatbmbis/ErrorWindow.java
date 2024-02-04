package org.example.chatbmbis;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class ErrorWindow extends Application {

    @Override
    public void start(Stage primaryStage) {
        // Método start no se utilizará en este caso
    }

    public static void mostrarMensaje(String mensaje) {
        // Crear una nueva ventana para el mensaje
        Stage ventanaMensaje = new Stage();
        ventanaMensaje.initModality(Modality.APPLICATION_MODAL);
        ventanaMensaje.initStyle(StageStyle.UTILITY);
        ventanaMensaje.setTitle("Mensaje");

        // Crear una etiqueta para mostrar el mensaje
        Label label = new Label(mensaje);

        // Crear un botón "Aceptar"
        Button botonAceptar = new Button("Aceptar");
        botonAceptar.setOnAction(e -> ventanaMensaje.close());

        // Crear el diseño de la ventana
        StackPane layout = new StackPane();
        layout.getChildren().addAll(label, botonAceptar);

        // Crear la escena
        Scene scene = new Scene(layout, 300, 100);

        // Establecer la escena en la ventana
        ventanaMensaje.setScene(scene);

        // Mostrar la ventana y bloquear la interacción con otras ventanas hasta que se cierre esta
        ventanaMensaje.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
