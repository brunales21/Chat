package org.example.chatbmbis;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Locale;
import java.util.ResourceBundle;

public class LoginController extends Controller{
    private Mediator mediator;
    @FXML
    Button enterButton;
    @FXML
    TextField usernameField, passwordField;

    public LoginController() {
    }

    @FXML
    private void initialize() {
        // Aquí se llama al método para internacionalizar el texto del botón
        Internacionalizacion.convertIntoOtherLanguage("addButton", enterButton);
        Internacionalizacion.convertIntoOtherLanguage("username", usernameField);
        Internacionalizacion.convertIntoOtherLanguage("password", passwordField);
    }
    @FXML
    private void ingresar() {
        if (!usernameField.getText().isEmpty()) {
            try {
                mediator.setUser(new User(usernameField.getText(), "localhost", 9001));
            } catch (IOException e) {
                ErrorWindow.instanceErrorWindow("FailConectToServer");
                return;
            }
            mediator.ingresar(usernameField.getText());
            ThreadUtils.sleep(100);
            if (mediator.actionApproved()) {
                closeLoginView();
                mediator.createChatView();
            }
        }else {
            ErrorWindow.instanceErrorWindow("CellEmpty");
        }
    }

    public void closeLoginView() {
        Stage stage = (Stage) this.enterButton.getScene().getWindow();
        stage.close();
    }

    public Mediator getMediator() {
        return mediator;
    }

    public void setMediator(Mediator mediator) {
        this.mediator = mediator;
    }


}
