package com.chatapp.controllers;

import com.chatapp.constants.Constants;
import com.chatapp.constants.ErrorTypes;
import com.chatapp.internationalization.Internationalization;
import com.chatapp.model.User;
import com.chatapp.utils.FieldValidator;
import com.chatapp.utils.WarningWindow;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import com.chatapp.mediator.Mediator;

public class LoginController extends Controller {
    private Mediator mediator;
    private User user;

    @FXML
    Button loginButton;
    @FXML
    TextField usernameField, passwordField;

    public LoginController() {}

    @FXML
    private void initialize() {
        // Aquí se llama al método para internacionalizar el texto del botón
        Internationalization.convertIntoOtherLanguage(Constants.LOGIN_TEXT, loginButton);
        Internationalization.convertIntoOtherLanguage(Constants.USERNAME, usernameField);
        Internationalization.convertIntoOtherLanguage(Constants.PASSWORD, passwordField);
    }
    @FXML
    private void onClickLogin() {
        login();
    }


    public void login() {
        if (FieldValidator.validateCredentials(usernameField, passwordField, null)) {
            String nickname = usernameField.getText().replace(" ", "").toLowerCase();
            if (user.connectToServer()) {
                mediator.getUser().setNickname(nickname);
                mediator.getUser().sendLoginCommand();
                if (mediator.getUser().successfulAuthentication()) {
                    closeLoginView();
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

    public void closeLoginView() {
        Stage stage = (Stage) this.loginButton.getScene().getWindow();
        stage.close();
    }

    public void showWindow() {
        Stage stage = (Stage) this.passwordField.getScene().getWindow();
        stage.show();
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

    public Button getEnterButton() {
        return loginButton;
    }

    public TextField getPasswordField() {
        return passwordField;
    }

    public void swapWindow(MouseEvent mouseEvent) {
        closeLoginView();
        mediator.getSignupController().showWindow();
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
