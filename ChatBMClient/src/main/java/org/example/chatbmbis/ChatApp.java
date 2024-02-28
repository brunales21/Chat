package org.example.chatbmbis;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
public class ChatApp extends Application {

    private Mediator mediator;
    @Override
    public void start(Stage stage) throws IOException {

        Mediator mediator = Mediator.getInstance();
        setMediator(mediator);
        //Crear vista chat
        FXMLLoader fxmlLoader = new FXMLLoader(ChatApp.class.getResource("chatView.fxml"));
        Scene chatView = new Scene(fxmlLoader.load());
        Stage stageChat = new Stage();
        stageChat.setTitle("Chat");
        stageChat.setScene(chatView);
        ChatController chatController = fxmlLoader.getController();
        chatController.setMediator(mediator);
        mediator.setChatController(chatController);
        mediator.getView().put(stageChat, chatController);

        //Crear vista añadir usuario
        FXMLLoader fxmlLoader3 = new FXMLLoader(ChatApp.class.getResource("addContactView.fxml"));
        Scene adduserView = new Scene(fxmlLoader3.load());
        Stage stageAddChat = new Stage();
        stageAddChat.setScene(adduserView);
        stageAddChat.setTitle("Add");
        AddContactViewController addViewController = fxmlLoader3.getController();
        addViewController.setMediator(mediator);
        mediator.setAddViewController(addViewController);
        mediator.getView().put(stageAddChat, addViewController);

        //crear la vista login
        FXMLLoader fxmlLoader2 = new FXMLLoader(ChatApp.class.getResource("loginView.fxml"));
        Scene scene = new Scene(fxmlLoader2.load(), 350, 500);
        LoginController loginController = fxmlLoader2.getController();
        loginController.setMediator(mediator);
        mediator.setLoginController(loginController);
        stage.setScene(scene);
        stage.setTitle("Inicio de Sesion");
        stage.show();

        setCloseWindow(stage);
        setCloseWindow(stageChat);
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