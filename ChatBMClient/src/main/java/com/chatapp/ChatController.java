package com.chatapp;

import com.chatapp.constants.Commands;
import com.chatapp.conversation.Message;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import com.chatapp.mediator.Mediator;

import java.io.IOException;
import java.util.*;


public class ChatController extends Controller {
    private Mediator mediator;
    @FXML
    private VBox vBoxChannels, vBoxContacts, vBoxMessages;
    @FXML
    private Label receptorChatLabel, userNameLabel;
    @FXML
    private TextField textMessageField;
    @FXML
    private ScrollPane spMessages;
    private Map<String, ContactItemController> itemContactsMap = new HashMap<>();
    private Locale locale = Locale.getDefault();
    private ResourceBundle bundle = ResourceBundle.getBundle("bundle.messages", locale);

    @FXML
    protected void onClickChannelOptions() {
        String prom = "promChannel";
        String opt1 = "buttonIzqChannel";
        String opt2 = "buttonDrchChannel";
        mediator.getAddViewController().getButton2().setVisible(true);
        createAddView(prom, opt1, opt2);
    }

    public void sendMessage() {
        String text = textMessageField.getText();
        String receptor = receptorChatLabel.getText();
        if (!text.isBlank() && !receptor.isBlank()) {
            addMessageToVBox(new Message(mediator.getUser().getNickname(), textMessageField.getText()));
            //PRIVMSG MONICA : HOLA SOY BRUNO
            String message = "PRIVMSG " + receptor + ":" + text;
            mediator.sendMessage(message);
            vBoxMessages.setAlignment(Pos.TOP_RIGHT);
        }
        textMessageField.setText("");
    }

    @FXML
    private void onClickSendMessage() {
        sendMessage();
    }

    @FXML
    private void onClickPrivChatOptions() {
        String prom = "promPriv";
        String opt1 = "buttonIzqPriv";
        String opt2 = "buttonDrchPriv";
        mediator.getAddViewController().getButton2().setVisible(false);
        createAddView(prom, opt1, opt2);
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

    public void emptyVBoxMessages() {
        vBoxMessages.getChildren().clear();
    }
    private StackPane createMessageNode(String text, boolean isCurrentUser) {
        Text textNode = new Text(text);
        textNode.setFill(Color.BLACK);
        textNode.setFont(new Font("Arial", 15));
        textNode.setWrappingWidth(200); // Ancho máximo del texto

        // Configuración del mensaje según el remitente
        VBox messageContainer = new VBox();
        messageContainer.setPadding(new Insets(5));
        messageContainer.setMaxWidth(200); // Ancho máximo del contenedor del mensaje

        // Establecer estilo y alineación según el remitente
        if (isCurrentUser) {
            messageContainer.setBackground(new Background(new BackgroundFill(Color.LIGHTBLUE, new CornerRadii(10), Insets.EMPTY)));
            messageContainer.setAlignment(Pos.CENTER_RIGHT); // Alinear a la derecha para mensajes propios
        } else {
            messageContainer.setBackground(new Background(new BackgroundFill(Color.WHITE, new CornerRadii(10), Insets.EMPTY)));
            messageContainer.setAlignment(Pos.CENTER_LEFT); // Alinear a la izquierda para mensajes ajenos
        }

        // Establecer radio de borde al fondo del contenedor del mensaje
        StackPane messageStackPane = new StackPane(messageContainer);
        messageStackPane.setPadding(new Insets(5));
        messageStackPane.setBackground(new Background(new BackgroundFill(Color.TRANSPARENT, new CornerRadii(10), Insets.EMPTY)));

        // Ajustar el texto al tamaño del contenedor
        textNode.boundsInLocalProperty().addListener((observable, oldValue, newValue) -> {
            double width = newValue.getWidth();
            if (width > 200) {
                messageContainer.setMaxWidth(200);
                textNode.setWrappingWidth(200);
            } else {
                messageContainer.setMaxWidth(width + 10); // Añadir un pequeño espacio adicional
                textNode.setWrappingWidth(width);
            }
        });

        // Agregar el texto al contenedor
        messageContainer.getChildren().add(textNode);

        return messageStackPane;
    }

    public void addMessageToVBox(Message message) {
        boolean isCurrentUser = message.getSender().equals(mediator.getUser().getNickname());
        if (message.getSender().equals(receptorChatLabel.getText()) || isCurrentUser || message.getTargetChannel().equals(receptorChatLabel.getText())) {
            StackPane messageStackPane = createMessageNode(message.getSender()+": "+message.getText(), isCurrentUser);
            Platform.runLater(() -> {
                HBox container = new HBox(messageStackPane);
                container.setAlignment(isCurrentUser ? Pos.CENTER_RIGHT : Pos.CENTER_LEFT); // Alinear a la derecha para mensajes propios y a la izquierda para mensajes ajenos
                container.setPadding(new Insets(5));
                vBoxMessages.getChildren().add(container);
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
        ContactItemController itemContactController = loader.getController();
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
        mediator.getUser().setChatMessagesMap(mediator.getUser().getChatDAO().loadChatMessages());
        Map<String, List<Message>> chatMessagesMap = mediator.getUser().getChatMessagesMap();
        for (String chatName : chatMessagesMap.keySet()) {
            if (chatName.startsWith("#")) {
                addContactItem(vBoxChannels, chatName);
            } else {
                addContactItem(vBoxContacts, chatName);
            }
        }
        if (!chatMessagesMap.containsKey("IA")) {
            mediator.addContactItem(vBoxContacts, "IA");
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
                mediator.sendMessage(Commands.EXIT.name());
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

    public VBox getvBoxMessages() {
        return vBoxMessages;
    }

    public ScrollPane getSpMessages() {
        return spMessages;
    }
}