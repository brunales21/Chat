package com.chatapp;

import com.chatapp.mediator.Mediator;
import com.chatapp.utils.ThreadUtils;
import com.chatapp.utils.WarningWindow;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

public class SignupController extends Controller {
    private Mediator mediator;

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;


    public SignupController() {
    }

    @FXML
    private void onClickSignup() {
        signup();
    }



    public void signup() {
        if (!usernameField.getText().isBlank() && !passwordField.getText().isBlank()) {
            String nickname = usernameField.getText().replace(" ", "").toLowerCase();
            if (invalidName(nickname)) {
                WarningWindow.instanceWarningWindow("ForbiddenSymbols");
                return;
            }
            if (mediator.initUser()) {
                // entra si pudo conectar con el servidor
                mediator.getUser().setNickname(nickname);
                mediator.signup();
                ThreadUtils.sleep(400);
                if (mediator.getUser().isAuthenticated()) {
                    closeSignupView();
                    mediator.createChatView();
                } else {
                    WarningWindow.instanceWarningWindow(mediator.getUser().getServerResponse());
                }
            } else {
                WarningWindow.instanceWarningWindow("ServidorCaido");
            }
        } else {
            usernameField.setText("");
            passwordField.setText("");
            WarningWindow.instanceWarningWindow("EmptyField");
        }
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

    public void setUsernameField(TextField usernameField) {
        this.usernameField = usernameField;
    }

    public PasswordField getPasswordField() {
        return passwordField;
    }

    public void setPasswordField(PasswordField passwordField) {
        this.passwordField = passwordField;
    }

    private boolean invalidName(String nickname) {
        return nickname.contains("/") || nickname.contains("\\") || nickname.contains("<") || nickname.contains(">");
    }
    public void showWindow() {
        Stage stage = (Stage) this.passwordField.getScene().getWindow();
        stage.show();
    }
    public void closeSignupView() {
        Stage stage = (Stage) this.passwordField.getScene().getWindow();
        stage.close();
    }

    public void swapWindow(MouseEvent mouseEvent) {
        closeSignupView();
        mediator.getLoginController().showWindow();
    }
}
