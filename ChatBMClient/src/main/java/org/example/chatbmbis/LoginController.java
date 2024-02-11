package org.example.chatbmbis;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class LoginController extends Controller {
    private Mediator mediator;
    @FXML
    Button accessButton;
    @FXML
    TextField usernameField, passwordField;

    public LoginController() {
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