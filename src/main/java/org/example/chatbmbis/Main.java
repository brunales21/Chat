package org.example.chatbmbis;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
public class Main extends Application {
    @Override
    public void start(Stage stage) throws IOException {

        Mediator mediator = new Mediator();

        //Crear vista chat
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("chatView.fxml"));
        Scene chatView = new Scene(fxmlLoader.load());
        Stage stageChat = new Stage();
        stageChat.setScene(chatView);
        stageChat.setTitle("Chat");
        ChatController chatController = fxmlLoader.getController();
        chatController.setMediator(mediator);
        mediator.setChatController(chatController);
        mediator.getView().put(stageChat, chatController);

        //Crear vista añadir usuario
        FXMLLoader fxmlLoader3 = new FXMLLoader(Main.class.getResource("addUserView.fxml"));
        Scene adduserView = new Scene(fxmlLoader3.load());
        Stage stageUser = new Stage();
        stageUser.setScene(adduserView);
        stageUser.setTitle("Add");
        AddContactViewController addViewController = fxmlLoader3.getController();
        addViewController.setMediator(mediator);
        mediator.setAddViewController(addViewController);
        mediator.getView().put(stageUser, addViewController);


        //cread la vista login
        FXMLLoader fxmlLoader2 = new FXMLLoader(Main.class.getResource("loginView.fxml"));
        Scene scene = new Scene(fxmlLoader2.load(), 350, 500);
        LoginController loginController = fxmlLoader2.getController();
        loginController.setMediator(mediator);
        mediator.setLoginController(loginController);
        stage.setTitle("Hello!");
        stage.setScene(scene);
        stage.show();
    }
    public static void main(String[] args) {
        launch();
    }

}