package com.chatapp.app;

import com.chatapp.controllers.AddContactViewController;
import com.chatapp.controllers.ChatController;
import com.chatapp.controllers.LoginController;
import com.chatapp.controllers.SignupController;
import com.chatapp.mediator.Mediator;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;

import java.io.IOException;

public class ChatApp extends Application {

    private Mediator mediator;

    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage stage) throws IOException {
        mediator = Mediator.getInstance();

        FXMLLoader fxmlLoader = new FXMLLoader(ChatApp.class.getResource("/com/chatapp/chatView.fxml"));
        Scene chatView = new Scene(fxmlLoader.load());
        Stage stageChat = new Stage();
        stageChat.setTitle("Chat");
        stageChat.setScene(chatView);
        ChatController chatController = fxmlLoader.getController();
        chatController.setMediator(mediator);
        mediator.setChatController(chatController);
        mediator.getView().put(stageChat, chatController);

        Image icon = new Image(getClass().getResourceAsStream("/imgs/appIcon3.png"));

        // Establecer el ícono en la barra de tareas
        stageChat.getIcons().add(icon);

        FXMLLoader fxmlLoader3 = new FXMLLoader(ChatApp.class.getResource("/com/chatapp/addContactView.fxml"));
        Scene adduserView = new Scene(fxmlLoader3.load());
        Stage stageAddChat = new Stage();
        stageAddChat.getIcons().add(icon);
        stageAddChat.setScene(adduserView);
        stageAddChat.setTitle("Add");
        AddContactViewController addViewController = fxmlLoader3.getController();
        addViewController.setMediator(mediator);
        mediator.setAddViewController(addViewController);
        mediator.getView().put(stageAddChat, addViewController);


        FXMLLoader fxmlLoader1 = new FXMLLoader(ChatApp.class.getResource("/com/chatapp/signupView.fxml"));
        Scene scene1 = new Scene(fxmlLoader1.load(), 993, 578);
        Stage signupStage = new Stage();
        SignupController signupController = fxmlLoader1.getController();
        signupController.setMediator(mediator);
        mediator.setSignupController(signupController);
        signupStage.setScene(scene1);
        signupStage.setResizable(true);
        signupStage.setTitle("Registro");
        signupStage.getIcons().add(icon);
        mediator.getView().put(signupStage, signupController);


        FXMLLoader fxmlLoader2 = new FXMLLoader(ChatApp.class.getResource("/com/chatapp/loginView.fxml"));
        Scene scene = new Scene(fxmlLoader2.load(), 993, 578);
        LoginController loginController = fxmlLoader2.getController();
        loginController.setMediator(mediator);
        mediator.setLoginController(loginController);
        stage.setScene(scene);
        stage.setResizable(true);
        stage.setTitle("Inicio de Sesion");
        stage.getIcons().add(icon);
        stage.show();
        setCloseWindow(stage);
        setCloseWindow(stageChat);
        setCloseWindow(signupStage);

        chatController.getTextMessageField().setOnKeyPressed(keyEvent -> {
            if (keyEvent.getCode() == KeyCode.ENTER) {
                chatController.sendMessage();
            }
        });

        loginController.getUsernameField().setOnKeyPressed(keyEvent -> {
            if (keyEvent.getCode() == KeyCode.ENTER) {
                loginController.login();
            }
        });

        loginController.getPasswordField().setOnKeyPressed(keyEvent -> {
            if (keyEvent.getCode() == KeyCode.ENTER) {
                loginController.login();
            }
        });

        signupController.getUsernameField().setOnKeyPressed(keyEvent -> {
            if (keyEvent.getCode() == KeyCode.ENTER) {
                signupController.signup();
            }
        });

        signupController.getPasswordField().setOnKeyPressed(keyEvent -> {
            if (keyEvent.getCode() == KeyCode.ENTER) {
                signupController.signup();
            }
        });
    }

    private void setCloseWindow(Stage stage) {
        stage.setOnCloseRequest(event -> mediator.onApplicationClose(stage));
    }

    public Mediator getMediator() {
        return mediator;
    }

    public void setMediator(Mediator mediator) {
        this.mediator = mediator;
    }
}
