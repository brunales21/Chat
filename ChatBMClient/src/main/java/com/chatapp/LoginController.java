package com.chatapp;

import com.chatapp.internationalization.Internacionalizacion;
import com.chatapp.utils.ThreadUtils;
import com.chatapp.utils.WarningWindow;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import com.chatapp.mediator.Mediator;

import java.io.IOException;

public class LoginController extends Controller {
    private Mediator mediator;
    @FXML
    Button enterButton;
    @FXML
    TextField usernameField, passwordField;

    public LoginController() {}

    public boolean initUser() {
        User user = new User();
        mediator.setUser(user);
        try {
            user.connect();
            user.sendUserType();
            return true;
        } catch (IOException e) {
            WarningWindow.instanceWarningWindow("ServidorCaido");
            return false;
        }
    }

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
            if (invalidName(nickname)) {
                WarningWindow.instanceWarningWindow("ForbiddenSymbols");
                return;
            }
            if (mediator.getLoginController().initUser()) {
                // entra si pudo conectar con el servidor
                mediator.getUser().setNickname(nickname);
                mediator.ingresar();
                ThreadUtils.sleep(400);
                if (mediator.getUser().isRegistered()) {
                    closeLoginView();
                    mediator.createChatView();
                } else {
                    WarningWindow.instanceWarningWindow("UserExists");
                }
            }
        } else {
            WarningWindow.instanceWarningWindow("EmptyField");
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
