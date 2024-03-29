package com.chatapp;

import com.chatapp.constants.Commands;
import com.chatapp.conversation.Message;
import com.chatapp.dao.FileChatDAO;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import com.chatapp.mediator.Mediator;

import java.io.IOException;
import java.util.*;


public class ChatController extends Controller {
    private Mediator mediator;
    @FXML
    private AnchorPane mainAnchorPane;
    @FXML
    private VBox vBoxChannels, vBoxContacts;
    @FXML
    private ListView<Message> messagesListView;
    private ObservableList<Message> messageList = FXCollections.observableArrayList(); // Lista observable para los mensajes
    @FXML
    private Label receptorChatLabel, userNameLabel;
    @FXML
    private TextField textMessageField;
    private Map<String, ContactItemController> itemContactsMap = new HashMap<>();
    private Locale locale = Locale.getDefault();
    private ResourceBundle bundle = ResourceBundle.getBundle("bundle.messages", locale);

    @FXML
    protected void onClickChannelOptions() {
        mediator.getAddViewController().getStage().close();
        String prom = "promChannel";
        String opt1 = "buttonIzqChannel";
        String opt2 = "buttonDrchChannel";
        mediator.getAddViewController().getButton2().setVisible(true);
        mediator.getAddViewController().getButton1().setTranslateX(0);
        mediator.getAddViewController().getHbox().setPrefWidth(556);
        createAddView(prom, opt1, opt2);
    }

    @FXML
    private void onClickPrivChatOptions() {
        mediator.getAddViewController().getStage().close();
        String prom = "promPriv";
        String opt1 = "buttonIzqPriv";
        String opt2 = "buttonDrchPriv";
        mediator.getAddViewController().getButton2().setVisible(false);
        mediator.getAddViewController().getButton1().setTranslateX(37);
        mediator.getAddViewController().getHbox().setPrefWidth(420);
        createAddView(prom, opt1, opt2);
    }

    public void sendMessage() {
        String text = textMessageField.getText();
        String receptor = receptorChatLabel.getText();
        if (!text.isBlank() && !receptor.isBlank()) {
            addMessageToListView(new Message(mediator.getUser().getNickname(), textMessageField.getText()));
            //PRIVMSG MONICA : HOLA SOY BRUNO
            String message = "PRIVMSG " + receptor + ":" + text;
            mediator.sendMessage(message);
        }
        textMessageField.setText("");
    }

    @FXML
    private void onClickSendMessage() {
        sendMessage();
    }

    public void overlayChat(String nickname) {
        // Obtener el controlador del item utilizando el nickname proporcionado
        ContactItemController itemContactController = itemContactsMap.get(nickname);

        if (itemContactController != null) {
            // Obtener el nodo (vista) correspondiente al controlador
            Parent itemNode = itemContactController.getView();

            // Obtener el VBox al que pertenece el item
            VBox vBox = itemNode.getParent() instanceof VBox ? (VBox) itemNode.getParent() : null;

            if (vBox != null) {
                Platform.runLater(() -> {
                    // Eliminar el nodo del VBox si ya estaba presente
                    vBox.getChildren().remove(itemNode);
                    // Agregar el nodo al primer lugar en el VBox
                    vBox.getChildren().add(0, itemNode);
                });
            }
        }
    }


    public void createAddView(String promptText, String opt1, String opt2) {
        mediator.createAddView(promptText, opt1, opt2);
    }

    public void emptyListView() {
        messagesListView.getItems().clear();
    }

    @FXML
    public void initialize() {
        // Asignar la lista observable al ListView
        messagesListView.setItems(messageList);
        messagesListView.setStyle("-fx-background-color: #fffdf9; -fx-padding: 1px;");

        messagesListView.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(Message msg, boolean empty) {
                super.updateItem(msg, empty);
                if (empty || msg == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    Label messageLabel = new Label(msg.getSender() + ": " + msg.getText());
                    messageLabel.setWrapText(true);

                    VBox messageBox = new VBox();
                    messageBox.getChildren().add(messageLabel);

                    HBox container = new HBox(messageBox);
                    container.setStyle("-fx-background-color: white;");

                    boolean messageOwner = msg.getSender().equals(userNameLabel.getText());
                    container.setAlignment(messageOwner ? Pos.CENTER_RIGHT : Pos.CENTER_LEFT);
                    setGraphic(container);
                    container.setStyle("-fx-background-color: #ffffff;");

                    if (messageOwner) {
                        messageBox.setStyle("-fx-padding: 6px; -fx-background-radius: 15px; -fx-background-color: rgb(131,184,241);");
                    } else {
                        messageBox.setStyle("-fx-padding: 6px; -fx-background-radius: 15px; -fx-background-color: #bbf6a4; ");
                    }

                    // Ajustar el ancho máximo del mensaje al ancho disponible en la ventana
                    messageLabel.setMaxWidth(messagesListView.getWidth() * 0.7); // ajusta el ancho del mensaje al 70% del ancho de la ventana
                    getStage().widthProperty().addListener(new ChangeListener<Number>() {
                        @Override
                        public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                            messageLabel.setMaxWidth(newValue.doubleValue() * 0.7);
                        }
                    });
                }
            }
        });
    }

    public void addMessageToListView(Message message) {
        Platform.runLater(() -> {
            messagesListView.getItems().add(message);
            messagesListView.scrollTo(messagesListView.getItems().size() - 1);
        });
    }


    public void addContactItem(VBox vBox, String nickname) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("contactItemView.fxml"));
        Parent parent = null;
        try {
            parent = loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        ContactItemController itemContactController = loader.getController();
        itemContactController.setMediator(mediator);
        itemContactController.showNotificationImg(false);
        itemContactController.setCallback(() -> {
            itemContactController.showNotificationImg(false);
            setReceptorChatLabelText(nickname);
            messagesListView.getItems().clear();
            mediator.getUser().getMessages(nickname).forEach(this::addMessageToListView);
        });

        itemContactController.setNicknameLabelText(nickname);

        itemContactsMap.put(nickname, itemContactController);
        mediator.getUser().getChatMessagesMap().put(nickname, mediator.getUser().getMessages(nickname));
        mediator.getUser().addContact(nickname);
        itemContactController.setView(parent);
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
            children = vBoxChannels.getChildren();
        } else {
            children = vBoxContacts.getChildren();
        }
        // Iterar sobre los nodos y eliminar el que tenga el nickname deseado
        for (Node child : children) {
            if (child instanceof Parent) {
                ContactItemController itemContactController = ((FXMLLoader) child.getUserData()).getController();
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


    public void loadChatItems() {
        Map<String, List<Message>> chatMessagesMap = mediator.getUser().getChatDAO().loadChatMessages();
        mediator.getUser().setChatMessagesMap(chatMessagesMap);
        for (String chatName : chatMessagesMap.keySet()) {
            if (chatName.startsWith("#")) {
                addContactItem(vBoxChannels, chatName);
            } else {
                addContactItem(vBoxContacts, chatName);
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
                if (mediator.getUser().getSocket() != null) {
                    mediator.sendMessage(Commands.EXIT.name());
                }
                // guardamos los chats y mensajes en un fichero binario
                if (((FileChatDAO)mediator.getUser().getChatDAO()).getFile() != null) {
                    mediator.getUser().getChatDAO().saveChatMessages(mediator.getUser().getChatMessagesMap());
                }
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

    public VBox getvBoxChannels() {
        return vBoxChannels;
    }

    public VBox getvBoxContacts() {
        return vBoxContacts;
    }

    public Label getReceptorChatLabel() {
        return receptorChatLabel;
    }

    public void setReceptorChatLabelText(String friendNickname) {
        receptorChatLabel.setText(friendNickname);
    }

    public Stage getStage() {
        return (Stage) receptorChatLabel.getScene().getWindow();
    }

    public TextField getTextMessageField() {
        return textMessageField;
    }

    public void setTextMessageField(TextField textMessageField) {
        this.textMessageField = textMessageField;
    }

    public Map<String, ContactItemController> getItemContactsMap() {
        return itemContactsMap;
    }

    public ListView getMessagesListView() {
        return messagesListView;
    }

}