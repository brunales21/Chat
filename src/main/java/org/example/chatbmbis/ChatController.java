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
import java.util.List;


public class ChatController extends Controller {
    private Mediator mediator;
    @FXML
    private VBox vBoxGroup, vBoxPrivate, vBoxMessages;
    @FXML
    private Label receptorChatLabel;
    @FXML
    private TextField textMessageField;

    @FXML
    protected void onClickCreateGroup() {
        createAddView("Nombre grupo", "Unirme");
    }

    @FXML
    private void onClickCreateChat() {
        createAddView("Nombre usuario", "Añadir usuario");
    }

    @FXML
    private void onClickAddUserToChannel() {
        createAddView("Nombre del grupo", "Añadir al grupo");
    }

    public void createAddView(String promptText, String buttonText) {
        mediator.createAddView(promptText, buttonText);
    }

    @FXML
    private void onClickSendMessage() {
        String header = "PRIVMSG " + receptorChatLabel.getText() + " :" + textMessageField.getText();
        mediator.sendMessage(header);
        vBoxMessages.setAlignment(Pos.TOP_RIGHT);
        vBoxMessages.getChildren().add(propietaryMessageStyle(textMessageField.getText()));
        textMessageField.setText("");

    }

    public void addMessagesForeingUser(String textMessage) {
        Label messageLabel = foreignMessageStyle(textMessage);
        vBoxMessages.setAlignment(Pos.TOP_RIGHT);
        Platform.runLater(() -> {
            vBoxMessages.getChildren().add(messageLabel);
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
            //setVBoxMessages();
            setReceptorChatLabelText(nickname);
        });

        itemContactController.setNicknameLabelText(nickname);

        if (nickname.startsWith("#")) {
            vBoxGroup.getChildren().add(paren);
        } else {
            vBoxPrivate.getChildren().add(paren);
        }
    }

    public void setVBoxMessages(List<String> messages) {
        vBoxMessages.getChildren().clear();
        for (String message: messages) {
            vBoxMessages.getChildren().add(propietaryMessageStyle(message));
        }
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

    public Label getReceptorChatLabel() {
        return receptorChatLabel;
    }

    public void setReceptorChatLabelText(String friendNickname) {
        receptorChatLabel.setText(friendNickname);
    }

    public VBox getvBoxMessages() {
        return vBoxMessages;
    }

    public void setvBoxMessages(VBox vBoxMessages) {
        this.vBoxMessages = vBoxMessages;
    }
}