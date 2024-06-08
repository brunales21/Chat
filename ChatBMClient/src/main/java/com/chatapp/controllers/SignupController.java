package com.chatapp.controllers;

import com.chatapp.constants.ErrorTypes;
import com.chatapp.mediator.Mediator;
import com.chatapp.model.User;
import com.chatapp.utils.FieldValidator;
import com.chatapp.utils.WarningWindow;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class SignupController extends Controller {

    private Mediator mediator;
    private User user;

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
        signUp();
    }

    public void signUp() {
        if (FieldValidator.validateCredentials(usernameField, passwordField, passwordField2)) {
            String nickname = usernameField.getText().replace(" ", "").toLowerCase();
            if (user.connectToServer()) {
                mediator.getUser().setNickname(nickname);
                mediator.getUser().sendSignupCommand();
                if (mediator.getUser().successfulAuthentication()) {
                    closeSignupView();
                    mediator.createChatView();
                    mediator.getUser().start();
                } else {
                    WarningWindow.instanceWarningWindow(mediator.getUser().getServerResponse());
                    if (mediator.getUser().getServerResponse().equals(ErrorTypes.SERVER_DOWN)) {
                        mediator.getUser().setConnected(false);
                    }
                }
            } else {
                WarningWindow.instanceWarningWindow(ErrorTypes.SERVER_DOWN);
            }
            user.setServerResponse("");
        }
    }

    public void setMediator(Mediator mediator) {
        this.mediator = mediator;
    }

    public TextField getUsernameField() {
        return usernameField;
    }

    public PasswordField getPasswordField() {
        return passwordField;
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

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public PasswordField getPasswordField2() {
        return passwordField2;
    }
}
