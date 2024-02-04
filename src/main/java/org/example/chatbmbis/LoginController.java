package org.example.chatbmbis;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class LoginController extends Controller {

    @FXML
    Button accessButton;
    @FXML
    TextField usernameField, passwordField;
    private Mediator mediator;

    public LoginController() {}

    @FXML
    private void ingresar() {
        mediator.createChatView(usernameField.getText());
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
