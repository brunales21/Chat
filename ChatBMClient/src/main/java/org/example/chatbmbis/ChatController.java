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
import javafx.stage.Stage;

import java.io.IOException;
import java.util.*;


public class ChatController extends Controller {
    private Mediator mediator;
    @FXML
    private VBox vBoxGroup, vBoxPrivate, vBoxMessages;
    @FXML
    private Label receptorChatLabel, userNameLabel;
    @FXML
    private TextField textMessageField;
    private Map<String, ItemContactController> itemContactsMap = new HashMap<>();
    private Locale locale = Locale.getDefault();
    private ResourceBundle bundle = ResourceBundle.getBundle("bundle.messages", locale);

    @FXML
    protected void onClickChannelOptions() {
        String prom = "promChannel";
        String opt1 = "buttonIzqChannel";
        String opt2 = "buttonDrchChannel";
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
        String opt1 = "buttonIzqPriv";
        String opt2 = "buttonDrchPriv";
        createAddView(prom, opt1, opt2);
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
        itemContactController.setMediator(mediator);
        itemContactController.showNotificationImg(false);
        itemContactController.setCallback(() -> {
            itemContactController.showNotificationImg(false);
            setReceptorChatLabelText(nickname);
            vBoxMessages.getChildren().clear();
            mediator.getUser().getMessages(nickname).forEach(this::addMessageToVBox);
        });

        itemContactController.setNicknameLabelText(nickname);

        itemContactsMap.put(nickname, itemContactController);
        mediator.getUser().getChatMessagesMap().put(nickname, mediator.getUser().getMessages(nickname));
        mediator.getUser().addContact(nickname);

        // Añadir el nuevo nodo al final de la lista de nodos hijos del vBoxPrivate
        Parent finalParent = parent;
        Platform.runLater(() -> {
            vBox.getChildren().add(finalParent);
            finalParent.setUserData(loader);
        });
    }


    public void removeContactItem(String nickname) {
        ObservableList<Node> children;
        if (nickname.startsWith("#")) {
            children = vBoxGroup.getChildren();
        } else {
            children = vBoxPrivate.getChildren();
        }
        // Iterar sobre los nodos y eliminar el que tenga el nickname deseado
        for (Node child : children) {
            if (child instanceof Parent) {
                ItemContactController itemContactController = ((FXMLLoader) child.getUserData()).getController();
                if (itemContactController.getNicknameLabelText().equals(nickname)) {
                    children.remove(child);
                    itemContactsMap.remove(nickname);
                    mediator.getUser().removeContact(nickname);
                    break;
                }
            }
        }
    }

    public boolean hasContact(String nickname) {
        return itemContactsMap.get(nickname) != null;
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

    public void loadChatItems() {
        mediator.getUser().setChatMessagesMap(mediator.getUser().getChatDAO().loadChatMessages());
        for (String chatName : mediator.getUser().getChatMessagesMap().keySet()) {
            if (chatName.startsWith("#")) {
                addContactItem(vBoxGroup, chatName);
            } else {
                addContactItem(vBoxPrivate, chatName);
            }

        }
    }

    @FXML
    private void onClickExit() {
        onApplicationClose();
    }

    public void onApplicationClose() {
        // Realizar limpieza o acciones previas al cierre
        try {
            if (mediator.getUser() != null) {
                // Informamos al servidor que cerramos sesion (asi el servidor gestiona menos hilos)
                mediator.sendMessage("EXIT");
                // guardamos los chats y mensajes en un fichero binario
                mediator.getUser().getChatDAO().saveChatMessages(mediator.getUser().getChatMessagesMap());
                // cerramos el socket
                mediator.getUser().getSocket().close();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        getStage().close();
        Platform.exit();
        System.exit(0);
    }

    public void setMediator(Mediator mediator) {
        this.mediator = mediator;
    }

    public Label getUserNameLabel() {
        return userNameLabel;
    }

    public VBox getvBoxGroup() {
        return vBoxGroup;
    }

    public VBox getvBoxPrivate() {
        return vBoxPrivate;
    }

    public Label getReceptorChatLabel() {
        return receptorChatLabel;
    }

    public void setReceptorChatLabelText(String friendNickname) {
        receptorChatLabel.setText(friendNickname);
    }

    private Stage getStage() {
        return (Stage) receptorChatLabel.getScene().getWindow();
    }

    public Map<String, ItemContactController> getItemContactsMap() {
        return itemContactsMap;
    }
}