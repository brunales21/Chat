package com.chatapp;

import com.chatapp.mediator.Mediator;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;

import java.io.IOException;

public class ChatApp extends Application {

    private Mediator mediator;

    @Override
    public void start(Stage stage) throws IOException {
        mediator = Mediator.getInstance();

        FXMLLoader fxmlLoader = new FXMLLoader(ChatApp.class.getResource("chatView.fxml"));
        Scene chatView = new Scene(fxmlLoader.load());
        Stage stageChat = new Stage();
        stageChat.setTitle("Chat");
        stageChat.setScene(chatView);
        ChatController chatController = fxmlLoader.getController();
        chatController.setMediator(mediator);
        mediator.setChatController(chatController);
        mediator.getView().put(stageChat, chatController);
        mediator.getChatController().getvBoxMessages().setSpacing(15);
        mediator.getChatController().getSpMessages().setContent(mediator.getChatController().getvBoxMessages());
        mediator.getChatController().getSpMessages().setFitToWidth(true);

        FXMLLoader fxmlLoader3 = new FXMLLoader(ChatApp.class.getResource("addContactView.fxml"));
        Scene adduserView = new Scene(fxmlLoader3.load());
        Stage stageAddChat = new Stage();
        stageAddChat.setScene(adduserView);
        stageAddChat.setTitle("Add");
        AddContactViewController addViewController = fxmlLoader3.getController();
        addViewController.setMediator(mediator);
        mediator.setAddViewController(addViewController);
        mediator.getView().put(stageAddChat, addViewController);

        FXMLLoader fxmlLoader2 = new FXMLLoader(ChatApp.class.getResource("loginView.fxml"));
        Scene scene = new Scene(fxmlLoader2.load(), 328, 498);
        LoginController loginController = fxmlLoader2.getController();
        loginController.setMediator(mediator);
        mediator.setLoginController(loginController);
        stage.setScene(scene);
        stage.setResizable(false);
        stage.setTitle("Inicio de Sesion");
        stage.show();

        setCloseWindow(stage);
        setCloseWindow(stageChat);

        chatController.getTextMessageField().setOnKeyPressed(keyEvent -> {
            if (keyEvent.getCode() == KeyCode.ENTER) {
                chatController.sendMessage();
            }
        });

        loginController.getUsernameField().setOnKeyPressed(keyEvent -> {
            if (keyEvent.getCode() == KeyCode.ENTER) {
                loginController.ingresar();
            }
        });
    }

    private void setCloseWindow(Stage stage) {
        stage.setOnCloseRequest(event -> mediator.getChatController().onApplicationClose());
    }

    public Mediator getMediator() {
        return mediator;
    }

    public void setMediator(Mediator mediator) {
        this.mediator = mediator;
    }

    public static void main(String[] args) {
        launch();
    }

}
