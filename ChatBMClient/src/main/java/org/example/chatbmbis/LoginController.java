package org.example.chatbmbis;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class LoginController extends Controller{
    private Mediator mediator;
    @FXML
    Button accessButton;
    @FXML
    TextField usernameField, passwordField;

    public LoginController() {
    }

    @FXML
    private void initialize() {
        // Aquí se llama al método para internacionalizar el texto del botón
        Internacionalizacion.convertIntoOtherLenguaje("addButton", accessButton);
        Internacionalizacion.convertIntoOtherLenguaje("userName", usernameField);
        Internacionalizacion.convertIntoOtherLenguaje("password", passwordField);
    }
    @FXML
    private void ingresar() {
        if (!usernameField.getText().isEmpty()) {
            mediator.setUser(new User(usernameField.getText(), "localhost", 9001));
            mediator.ingresar(usernameField.getText());
            closeLoginView();
            mediator.createChatView(usernameField.getText());
        }
    }

    public void closeLoginView() {
        Stage stage = (Stage) this.accessButton.getScene().getWindow();
        stage.close();
    }

    public Mediator getMediator() {
        return mediator;
    }

    public void setMediator(Mediator mediator) {
        this.mediator = mediator;
    }


}
