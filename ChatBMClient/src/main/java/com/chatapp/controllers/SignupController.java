package com.chatapp.controllers;

import com.chatapp.constants.ErrorTypes;
import com.chatapp.mediator.Mediator;
import com.chatapp.utils.NodeUtils;
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

    @FXML
    private PasswordField passwordField2;


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
                WarningWindow.instanceWarningWindow(ErrorTypes.FORBIDDEN_CHARS);
                return;
            }
            if (!passwordsMatch()) {
                WarningWindow.instanceWarningWindow(ErrorTypes.PASSWORDS_MISMATCH);
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
                WarningWindow.instanceWarningWindow(ErrorTypes.SERVER_DOWN);
            }
        } else {
            NodeUtils.cleanTextField(usernameField);
            NodeUtils.cleanTextField(passwordField);
            WarningWindow.instanceWarningWindow(ErrorTypes.EMPTY_FIELD);
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

    public void swapWindow() {
        closeSignupView();
        mediator.getLoginController().showWindow();
    }

    private boolean passwordsMatch() {
        return passwordField.getText().equals(passwordField2.getText());
    }
}
