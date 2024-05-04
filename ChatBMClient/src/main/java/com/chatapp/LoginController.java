package com.chatapp;

import com.chatapp.internationalization.Internacionalizacion;
import com.chatapp.utils.ThreadUtils;
import com.chatapp.utils.WarningWindow;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import com.chatapp.mediator.Mediator;

import java.io.IOException;
import java.util.Scanner;

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
        Internacionalizacion.convertIntoOtherLanguage("loginText", loginButton);
        Internacionalizacion.convertIntoOtherLanguage("username", usernameField);
        Internacionalizacion.convertIntoOtherLanguage("password", passwordField);
    }
    @FXML
    private void onClickLogin() {
        login();
    }


    public void login() {
        if (!usernameField.getText().isBlank() && !passwordField.getText().isBlank()) {
            String nickname = usernameField.getText().replace(" ", "").toLowerCase();
            if (mediator.initUser()) {
                // entra si pudo conectar con el servidor
                mediator.getUser().setNickname(nickname);
                mediator.login();
                ThreadUtils.sleep(400);
                if (mediator.getUser().isAuthenticated()) {
                    closeLoginView();
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
