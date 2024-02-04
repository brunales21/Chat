package org.example.chatbmbis;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;

import java.io.IOException;


public class ChatController extends Controller {


    @FXML
    private VBox vBoxGroup, vBoxPrivate, messages;
    @FXML
    private Label friendNicknameChatLabel;
    @FXML
    private TextField textMessageField;
    private Mediator mediator = Mediator.getInstance();
    private static ChatController instance;

    public static synchronized ChatController getInstance() {
        if (instance == null) {
            instance = new ChatController();
        }
        return instance;
    }

    public ChatController() {

    }


    @FXML
    protected void onClickCreateGroup() {
        createAddView("Nombre grupo", "Crear grupo");
    }

    @FXML
    private void onClickCreateChat() {
        createAddView("Nombre usuario", "Añadir usuario");
    }

    @FXML
    private void onClickAddUserToChannel() {
        createAddView("Nombre del grupo", "Añadir al grupo");
    }

    public void createAddView(String promtext, String buttonText) {
        mediator.createAddView(promtext, buttonText);
    }

    @FXML
    private void onClickSendMessage() {
        String header = "PRIVMSG " + friendNicknameChatLabel.getText() + " :" + textMessageField.getText();
        mediator.sendMessage(header);
        messages.setAlignment(Pos.TOP_RIGHT);
        messages.getChildren().add(propietaryMessageStyle(textMessageField.getText()));

    }

    public void addMessagesForeingUser(String textMessage) {
        Label messageLabel = foreignMessageStyle(textMessage);
        messages.setAlignment(Pos.TOP_RIGHT);
        Platform.runLater(() -> {
            messages.getChildren().add(messageLabel);
        });
    }

    public void createContactItem(String nickname) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("contactItemView.fxml"));
        Parent paren = null;
        try {
            paren = loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        ItemContactController itemContactController = loader.getController();
        itemContactController.setCallback(() -> {
            setFriendNicknameChatLabelText(nickname);
        });

        itemContactController.setNicknameLabelText(nickname);
        vBoxPrivate.getChildren().add(paren);
    }

    public Label propietaryMessageStyle(String textmessage) {
        Label label = new Label("Tú: "+textmessage);
        label.setStyle("-fx-background-color:" + String.format("#%02X%02X%02X", 180, 160, 200));
        // Cambiar el color del texto
        label.setTextFill(Color.BLACK);
        // Cambiar el tamaño del texto
        label.setFont(new javafx.scene.text.Font("Arial", 15));
        label.setPrefHeight(42);
        label.setTextAlignment(TextAlignment.CENTER);
        label.setPadding(new Insets(10));
        return label;
    }

    public Label foreignMessageStyle(String text) {
        Label label = new Label(text);
        label.setStyle("-fx-background-color: lightblue");
        // Cambiar el color del texto
        label.setTextFill(Color.BLACK);
        // Cambiar el tamaño del texto
        label.setFont(new javafx.scene.text.Font("Arial", 15));
        label.setPrefHeight(42);
        label.setTextAlignment(TextAlignment.CENTER);
        label.setPadding(new Insets(10));
        return label;
    }

    @FXML
    private void exitAccount() {

    }


    public void createItem(FXMLLoader itemContactController, String nickName) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("contactItemView.fxml"));
        Parent paren = null;
        try {
            paren = itemContactController.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        ItemContactController itemContactController1 = itemContactController.getController();
        itemContactController1.setNicknameLabelText(nickName);
        vBoxPrivate.getChildren().add(paren);

    }


    public Mediator getMediator() {
        return mediator;
    }

    public void setMediator(Mediator mediator) {
        this.mediator = mediator;
    }


    public VBox getvBoxGroup() {
        return vBoxGroup;
    }

    public void setvBoxGroup(VBox vBoxGroup) {
        this.vBoxGroup = vBoxGroup;
    }

    public VBox getvBoxPrivate() {
        return vBoxPrivate;
    }

    public void setvBoxPrivate(VBox vBoxPrivate) {
        this.vBoxPrivate = vBoxPrivate;
    }

    public Label getFriendNicknameChatLabel() {
        return friendNicknameChatLabel;
    }

    public void setFriendNicknameChatLabelText(String friendNickname) {
        friendNicknameChatLabel.setText(friendNickname);
    }

    public VBox getMessages() {
        return messages;
    }

    public void setMessages(VBox messages) {
        this.messages = messages;
    }
}