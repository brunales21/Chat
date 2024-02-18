package org.example.chatbmbis;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;

import java.io.IOException;
import java.util.Locale;
import java.util.ResourceBundle;


public class ChatController extends Controller {
    private Mediator mediator;
    @FXML
    private VBox vBoxGroup, vBoxPrivate, vBoxMessages;
    @FXML
    private Label receptorChatLabel, userNameLabel;
    @FXML
    private TextField textMessageField;
    private Locale locale = Locale.getDefault();
    private ResourceBundle bundle = ResourceBundle.getBundle("bundle.messages",locale);

    @FXML
    protected void onClickChannelOptions() {
        String prom ="promChannel";
        String opt1 ="buttonIzqChannel";
        String opt2 ="buttonDrchChannel";
        createAddView(prom, opt1, opt2);
    }

    @FXML
    private void onClickSendMessage() {
        String text = textMessageField.getText();
        String receptor = receptorChatLabel.getText();
        if (!text.isBlank() && !receptor.isBlank()) {
            addMessageToVBox(new Message(mediator.getUser().getNickname(), textMessageField.getText()));
            //PRIVMSG MONICA : HOLA SOY BRUNO
            String message = "PRIVMSG " + receptor + " :" + text;
            mediator.sendMessage(message);
            vBoxMessages.setAlignment(Pos.TOP_RIGHT);
        }
        textMessageField.setText("");
    }

    @FXML
    private void onClickPrivChatOptions() {
        String prom = "promPriv";
        String opt1 ="buttonIzqPriv";
        String opt2 ="buttonDrchPriv";
        createAddView(prom, opt1,opt2);
    }


    public void createAddView(String promptText, String opt1, String opt2) {
        mediator.createAddView(promptText, opt1, opt2);
    }

    public void emptyVBoxMessages() {
        vBoxMessages.getChildren().clear();
    }

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

    public void addContactItem(VBox vBox, String nickname) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("contactItemView.fxml"));
        Parent parent = null;
        try {
            parent = loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        ItemContactController itemContactController = loader.getController();
        itemContactController.setCallback(() -> {
            setReceptorChatLabelText(nickname);
            vBoxMessages.getChildren().clear();
            mediator.getUser().getMessages(nickname).forEach(this::addMessageToVBox);
        });

        itemContactController.setNicknameLabelText(nickname);

        // Añadir el nuevo nodo al final de la lista de nodos hijos del vBoxPrivate
        Parent finalParent = parent;
        Platform.runLater(() -> {
            vBox.getChildren().add(finalParent);
            finalParent.setUserData(loader);
        });
    }


    // Método para verificar si el VBox contiene un nodo por su nombre
    public boolean containsItemContact(String nombre) {
        for (Node nodo : vBoxPrivate.getChildren()) {
            if (nodo.getUserData() instanceof String &&
                    ((String) nodo.getUserData()).equals(nombre)) {
                return true;
            }
        }
        return false;  // Devuelve false si no se encuentra un nodo con el nombre dado
    }


    public void removeContactItem(String nickname) {
        ObservableList<Node> children = vBoxPrivate.getChildren();
        // Iterar sobre los nodos y eliminar el que tenga el nickname deseado
        for (Node child : children) {
            if (child instanceof Parent) {
                ItemContactController itemContactController = ((FXMLLoader) child.getUserData()).getController();
                if (itemContactController.getNicknameLabelText().equals(nickname)) {
                    children.remove(child);
                    break;
                }
            }
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
        Parent parent = null;
        try {
            parent = itemContactController.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        ItemContactController itemContactController1 = itemContactController.getController();
        itemContactController1.setNicknameLabelText(nickName);
        vBoxPrivate.getChildren().add(parent);

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