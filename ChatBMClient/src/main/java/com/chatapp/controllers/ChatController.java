package com.chatapp.controllers;

import com.chatapp.constants.Commands;
import com.chatapp.constants.Constants;
import com.chatapp.mediator.Mediator;
import com.chatapp.model.Message;
import com.chatapp.utils.SyntaxUtils;
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
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class ChatController extends Controller {

    private final ObservableList<Message> messageList = FXCollections.observableArrayList(); // Lista observable para los mensajes
    private final Map<String, ItemContactController> contactsMap = new HashMap<>();
    private final Locale locale = Locale.getDefault();
    private final ResourceBundle bundle = ResourceBundle.getBundle(Constants.BUNDLE_MESSAGES, locale);
    private Mediator mediator;
    @FXML
    private AnchorPane mainAnchorPane;
    @FXML
    private ImageView userPictureProfile;
    @FXML
    private VBox vBoxChannels, vBoxContacts;
    @FXML
    private ListView<Message> messagesListView;
    @FXML
    private Label receptorChatLabel, userNameLabel;
    @FXML
    private TextField textMessageField;
    @FXML
    private ImageView chatLabelPicture;

    @FXML
    protected void onClickChannelOptions() {
        mediator.getAddViewController().getStage().close();
        mediator.getAddViewController().getButton2().setVisible(true);
        mediator.getAddViewController().getButton1().setTranslateX(0);
        mediator.getAddViewController().getHbox().setPrefWidth(556);
        createAddView(Constants.CHANNEL_NAME, Constants.LEFT_CHANNEL_BUTTON, Constants.RIGHT_BUTTON_CHANNEL);
    }

    @FXML
    private void onClickPrivChatOptions() {
        mediator.getAddViewController().getStage().close();
        mediator.getAddViewController().getButton2().setVisible(false);
        mediator.getAddViewController().getButton1().setTranslateX(37);
        mediator.getAddViewController().getHbox().setPrefWidth(420);
        createAddView(Constants.NICKNAME, Constants.LEFT_CONTACT_BUTTON, Constants.RIGHT_CONTACT_BUTTON);
    }

    public void sendMessage() {
        String text = textMessageField.getText();
        String receptor = receptorChatLabel.getText();
        if (!text.isBlank() && !receptor.isBlank()) {
            addMessageToListView(new Message(mediator.getUser().getNickname(), textMessageField.getText()));
            //PRIVMSG MONICA:HOLA SOY BRUNO
            String message = Commands.PRIVMSG + " " + receptor + SyntaxUtils.DELIMITER + text;
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
        ItemContactController itemContactController = contactsMap.get(nickname);

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
        messagesListView.setStyle("-fx-background-color: #d2f1ff;");
        messagesListView.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(Message msg, boolean empty) {
                super.updateItem(msg, empty);
                if (empty || msg == null) {
                    setText(null);
                    setGraphic(null);
                    setStyle("-fx-background-color: transparent; -fx-border-width: 0; -fx-padding: 5px;");
                } else {
                    Label messageLabel = new Label(msg.getSender() + ": " + msg.getText());
                    messageLabel.setWrapText(true);
                    messageLabel.setFont(Font.font("System", FontWeight.NORMAL, 14));
                    messageLabel.setTextFill(Color.web("#333"));

                    // Crear un Label para mostrar la hora
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
                    Label timeLabel = new Label(msg.getTime().format(formatter));
                    timeLabel.setStyle("-fx-font-size: 10px; -fx-text-fill: #888888;"); // Estilo para la hora

                    VBox messageBox = new VBox();

                    messageBox.getChildren().addAll(messageLabel, timeLabel);
                    messageBox.getStyleClass().add("message-container");

                    HBox container = new HBox(messageBox);
                    container.setStyle("-fx-background-color: transparent;");
                    boolean messageOwner = msg.getSender().equals(mediator.getUser().getNickname());

                    if (messageOwner) {
                        messageLabel.setText(msg.getText());
                        messageBox.getStyleClass().add("message-sender");
                        container.setAlignment(Pos.CENTER_RIGHT);
                    } else {
                        messageBox.getStyleClass().add("message-receiver");
                        container.setAlignment(Pos.CENTER_LEFT);
                    }

                    messageLabel.setMaxWidth(messagesListView.getWidth() * 0.7);
                    messagesListView.widthProperty().addListener(new ChangeListener<Number>() {
                        @Override
                        public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                            messageLabel.setMaxWidth(newValue.doubleValue() * 0.7);
                        }
                    });

                    setGraphic(container);
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
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/chatapp/contactItemView.fxml"));
        Parent parent = null;
        try {
            parent = loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        ItemContactController itemContactController = loader.getController();
        itemContactController.setMediator(mediator);
        itemContactController.showNotificationImg(false);
        Parent finalParent1 = parent;
        itemContactController.setCallback(() -> {
            // Restablecer el estilo de fondo de todos los elementos
            for (Node child : vBox.getChildren()) {
                child.setStyle("");
            }

            // Aplicar el estilo de fondo azul al elemento seleccionado
            finalParent1.setStyle("-fx-background-color: #ADD8E6;");

            if (nickname.startsWith("#")) {
                setChatLabelPicture("/images/channel_picture2.png");
            } else if (nickname.equalsIgnoreCase(Commands.IA.name())) {
                setChatLabelPicture("/images/ai.png");
            } else {
                setChatLabelPictureLetter(nickname.charAt(0));
            }
            itemContactController.showNotificationImg(false);
            setReceptorChatLabelText(nickname);
            messagesListView.getItems().clear();
            mediator.getUser().getMessages(nickname).forEach(this::addMessageToListView);
        });


        itemContactController.setNicknameLabelText(nickname);

        contactsMap.put(nickname, itemContactController);
        mediator.getUser().getChatMessagesMap().put(nickname, mediator.getUser().getMessages(nickname));
        mediator.getUser().addContact(nickname);
        itemContactController.setView(parent);
        if (nickname.startsWith("#")) {
            itemContactController.setChannelProfilePicture();
        } else if (nickname.equalsIgnoreCase(Commands.IA.name())) {
            itemContactController.setAIProfilePicture();
        } else {
            itemContactController.setUserProfilePicture(nickname.charAt(0));
        }
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
        deleteItemChatController(children, nickname);
    }

    private void deleteItemChatController(ObservableList<Node> children, String nickname) {
        for (Node child : children) {
            if (child instanceof Parent) {
                ItemContactController itemContactController = ((FXMLLoader) child.getUserData()).getController();
                if (itemContactController.getNicknameLabelText().equals(nickname)) {
                    children.remove(child);
                    contactsMap.remove(nickname);
                    mediator.getUser().removeContact(nickname);
                    mediator.getUser().getChatMessagesMap().remove(nickname);
                    break;
                }
            }
        }
    }

    public boolean hasContact(String nickname) {
        return contactsMap.get(nickname) != null;
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
        mediator.onApplicationClose(getStage());
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

    public Map<String, ItemContactController> getContactsMap() {
        return contactsMap;
    }

    private void setChatLabelPictureLetter(char letter) {
        chatLabelPicture.setImage(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/letters/letra-" + letter + ".png".toLowerCase()))));
        chatLabelPicture.setVisible(true);
    }

    public void setUserPictureLetter(char letter) {
        userPictureProfile.setImage(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/letters/letra-" + letter + ".png".toLowerCase()))));
    }

    public ImageView getChatLabelPicture() {
        return chatLabelPicture;
    }

    private void setChatLabelPicture(String path) {
        chatLabelPicture.setImage(new Image(Objects.requireNonNull(getClass().getResourceAsStream(path))));
        chatLabelPicture.setVisible(true);
    }
}