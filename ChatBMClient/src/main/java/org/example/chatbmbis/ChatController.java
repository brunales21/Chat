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
    private ItemContactController currentItemCc;
    @FXML
    private VBox vBoxGroup, vBoxPrivate, vBoxMessages;
    @FXML
    private Label receptorChatLabel, userNameLabel;
    @FXML
    private TextField textMessageField;

    @FXML
    protected void onClickCreateGroup() {
        createAddView("Nombre grupo", "Unirme");
    }

    @FXML
    private void onClickSendMessage() {
        if (textMessageField.getText().isEmpty() || receptorChatLabel.getText().isEmpty()) {
            textMessageField.setText("");
            return;
        }

        vBoxMessages.getChildren().add(propietaryMessageStyle(textMessageField.getText()));
        String header = "PRIVMSG " + receptorChatLabel.getText() + " :" + textMessageField.getText();
        mediator.sendHeader(header);
        vBoxMessages.setAlignment(Pos.TOP_RIGHT);
        textMessageField.setText("");
    }

    private boolean receptorIsChannel(String name) {
        return name.startsWith("#");
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


    // CONTROLAR cuando el receptorchatlabel sea un canal, porque no coincide con el sender de message
    public void addMessageToVBox(Message message) {
        Label messageLabel;
        if (message.getSender().equals(receptorChatLabel.getText()) || message.getSender().equals(mediator.getUser().getNickname()) || message.getTargetChannel().equals(receptorChatLabel.getText())) {
            if (message.getSender().equals(mediator.getUser().getNickname())) {
                messageLabel = propietaryMessageStyle(message.getText());
            } else {
                messageLabel = foreignMessageStyle(message.getSender(), message.getText());
            }

            vBoxMessages.setAlignment(Pos.TOP_RIGHT);
            Platform.runLater(() -> {
                vBoxMessages.getChildren().add(messageLabel);
            });
        }
    }

    public void createContactItem(String nickname) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("contactItemView.fxml"));
        Parent parent = null;
        try {
            parent = loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        ItemContactController itemContactController = loader.getController();
        currentItemCc = itemContactController;
        itemContactController.setCallback(() -> {
            setReceptorChatLabelText(nickname);
            vBoxMessages.getChildren().clear();
            mediator.getUser().getMessages(nickname).forEach(this::addMessageToVBox);
        });

        itemContactController.setNicknameLabelText(nickname);
        addItemToBox(nickname, parent);
    }

    public void addItemToBox(String nickname, Parent parent) {
        if (nickname.startsWith("#")) {
            vBoxGroup.getChildren().add(parent);
        } else {
            vBoxPrivate.getChildren().add(parent);
        }
    }

    public void setVBoxMessages(List<Message> messages) {
        vBoxMessages.getChildren().clear();
        Label messageLabel;
        for (Message message : messages) {
            if (message.getSender().equals(mediator.getUser().getNickname())) {
                messageLabel = propietaryMessageStyle(message.getText());
            } else {
                messageLabel = foreignMessageStyle(message.getSender(), message.getText());
            }

            Label finalMessageLabel = messageLabel;
            Platform.runLater(() -> {
                vBoxMessages.getChildren().add(finalMessageLabel);
            });
        }
    }

    public Label propietaryMessageStyle(String text) {
        Label label = new Label("Tú: " + text);
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

    public Label foreignMessageStyle(String sender, String text) {
        Label label = new Label(sender + ": " + text);
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

    public ItemContactController getCurrentItemCc() {
        return currentItemCc;
    }

    public void setCurrentItemCc(ItemContactController currentItemCc) {
        this.currentItemCc = currentItemCc;
    }

    public Mediator getMediator() {
        return mediator;
    }

    public void setMediator(Mediator mediator) {
        this.mediator = mediator;
    }

    public Label getUserNameLabel() {
        return userNameLabel;
    }

    public void setUserNameLabel(Label userNameLabel) {
        this.userNameLabel = userNameLabel;
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