package com.chatapp.controllers;

import com.chatapp.constants.Constants;
import com.chatapp.constants.ErrorTypes;
import com.chatapp.internationalization.Internationalization;
import com.chatapp.utils.NodeUtils;
import com.chatapp.utils.ThreadUtils;
import com.chatapp.utils.WarningWindow;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import com.chatapp.mediator.Mediator;

public class LoginController extends Controller {
    private Mediator mediator;
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
        if (!usernameField.getText().isBlank() && !passwordField.getText().isBlank()) {
            String nickname = usernameField.getText().replace(" ", "").toLowerCase();



            // gestionar inicio de sesión




        } else {
            NodeUtils.cleanTextField(usernameField);
            NodeUtils.cleanTextField(passwordField);
            WarningWindow.instanceWarningWindow(ErrorTypes.EMPTY_FIELD);
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
}
