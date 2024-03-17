package org.example.chatbmbis;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.example.chatbmbis.utils.ThreadUtils;

import java.io.IOException;

public class LoginController extends Controller {
    private Mediator mediator;
    @FXML
    Button enterButton;
    @FXML
    TextField usernameField, passwordField;

    public LoginController() {}

    @FXML
    private void initialize() {
        // Aquí se llama al método para internacionalizar el texto del botón
        Internacionalizacion.convertIntoOtherLanguage("addButton", enterButton);
        Internacionalizacion.convertIntoOtherLanguage("username", usernameField);
        Internacionalizacion.convertIntoOtherLanguage("password", passwordField);
    }
    @FXML
    private void onClickIngresar() {
        ingresar();
    }

    public void ingresar() {
        if (!usernameField.getText().isBlank()) {
            String nickname = usernameField.getText().replace(" ", "").toLowerCase();
            User user;
            try {
                if (invalidName(nickname)) {
                    ErrorWindow.instanceErrorWindow("ForbiddenSymbols");
                    return;
                }
                user = new User(nickname, "localhost", 23);
            } catch (IOException e) {
                ErrorWindow.instanceErrorWindow("FailConnectToServer");
                return;
            }
            mediator.setUser(user);
            mediator.ingresar();
            ThreadUtils.sleep(200);
            if (mediator.successfulAction()) {
                closeLoginView();
                mediator.createChatView();
            }
        } else {
            ErrorWindow.instanceErrorWindow("EmptyField");
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

    public TextField getUsernameField() {
        return usernameField;
    }

    private boolean invalidName(String nickname) {
        return nickname.contains("/") || nickname.contains("\\") || nickname.contains("<") || nickname.contains(">");
    }
}
