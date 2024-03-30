package com.chatapp.mediator;

import com.chatapp.User;
import com.chatapp.constants.Commands;
import com.chatapp.ChatController;
import com.chatapp.Controller;
import com.chatapp.LoginController;
import com.chatapp.conversation.Message;
import com.chatapp.dao.FileChatDAO;
import com.chatapp.utils.Utils;
import com.chatapp.utils.WarningWindow;
import com.chatapp.AddContactViewController;
import javafx.application.Platform;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.*;

public class Mediator {
    private static Mediator instance;
    private ChatController chatController;
    private AddContactViewController addViewController;
    private LoginController loginController;
    private Map<Stage, Controller> view = new HashMap<>();
    private User user;
    private boolean successfulAction = true;

    public Mediator() {
    }

    public static synchronized Mediator getInstance() {
        if (instance == null) {
            instance = new Mediator();
        }
        return instance;
    }

    public void ingresar() {
        if (getUser().getSocket() != null) {
            user.ingresar();
        } else {
            WarningWindow.instanceWarningWindow("ServidorCaido");
        }
    }



    public void onApplicationClose(Stage stage) {
        // Realizar limpieza o acciones previas al cierre
        try {
            if (getUser() != null) {
                if (getUser().getSocket() != null) {
                    // Informamos al servidor que cerramos sesion (asi el servidor gestiona menos hilos)
                    sendMessage(Commands.EXIT.name());
                    // cerramos el socket
                    getUser().getSocket().close();
                }
                // guardamos los chats y mensajes en un fichero binario
                if (((FileChatDAO)getUser().getChatDAO()).getFile() != null) {
                    getUser().getChatDAO().saveChatMessages(getUser().getChatMessagesMap());
                }

            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        stage.close();
        Platform.exit();
        System.exit(0);
    }

    public void createChatView() {
        Stage stage = null;
        for (Map.Entry<Stage, Controller> entry : view.entrySet()) {
            if (entry.getKey().getTitle().equals("Chat")) {
                ChatController controller = (ChatController) entry.getValue();
                stage = entry.getKey();
                controller.getUserNameLabel().setText(user.getNickname());
                break;
            }
        }
        stage.setResizable(true);
        stage.show();

    }

    public void sendMessage(String message) {
        user.sendMessage(message);
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void createAddView(String promptText, String opt1, String opt2) {
        Stage stage = null;
        AddContactViewController addController = null;
        for (Map.Entry<Stage, Controller> entry : view.entrySet()) {
            if (entry.getKey().getTitle().equals("Add")) {
                stage = entry.getKey();
                addController = (AddContactViewController) entry.getValue();
            }
        }

        ResourceBundle bundle = ResourceBundle.getBundle("bundle.messages", Locale.getDefault());
        String promptTextInter = bundle.getString(promptText);
        String opt1Inter = bundle.getString(opt1);
        String opt2Inter = bundle.getString(opt2);

        addController.setPromptText(promptTextInter);
        addController.getButton1().setText(opt1Inter);
        addController.getButton2().setText(opt2Inter);

        stage.setResizable(false);
        stage.show();

    }

    public void addContactItem(VBox vBox, String nickname) {
        chatController.addContactItem(vBox, nickname);
    }

    public void deleteContactItem(String nickname) {
        chatController.removeContactItem(nickname);
        chatController.setReceptorChatLabelText("");
        chatController.emptyListView();
    }

    public void processServerMsg(String message) {
        String[] messageParts = Utils.splitCommandLine(message);
        String keyWord = messageParts[0];
        Message messageObj;
        if (actionRefused(keyWord)) {
            // si la accion fue rechazada
            setSuccessfulAction(false);
            WarningWindow.instanceWarningWindow(messageParts[1]);
        } else if (isActionApproved(keyWord)) {
            // si fue aprobada
            setSuccessfulAction(true);
        } else if (isTxtMessage(keyWord)) {
            // si tiene que procesar un msj de texto
            messageParts[1] = messageParts[1].toLowerCase();
            if (messageParts[1].startsWith("#")) {
                // "MESSAGE #2dam bruno:hola"
                messageObj = new Message(messageParts[2], messageParts[1], messageParts[3]);
            } else {
                // "MESSAGE bruno:hola"
                messageObj = new Message(messageParts[1], messageParts[2]);
                if (!getUser().containsContact(messageParts[1])) {
                    chatController.addContactItem(chatController.getvBoxContacts(), messageObj.getSender());
                    sendMessage(Commands.CREATE.name() + " " + messageObj.getSender());
                }
            }
            user.addMessage(messageParts[1], messageObj);
            chatController.overlayChat(messageParts[1]);
            // si el chat abierto coincide con el emisor del mensaje..
            if (chatController.getReceptorChatLabel().getText().equals(messageParts[1])) {
                chatController.addMessageToListView(messageObj);
            } else {
                // si no, mostramos notificacion
                chatController.getItemContactsMap().get(messageParts[1]).showNotificationImg(true);
            }

        }
    }

    private boolean isTxtMessage(String message) {
        return message.equals(Commands.MESSAGE.name());
    }

    public boolean isActionApproved(String message) {
        return message.equalsIgnoreCase(Commands.OK.name());
    }

    private boolean actionRefused(String message) {
        return message.equals(Commands.ERROR.name());
    }


    public User getUser() {
        return user;
    }

    public void setAddViewController(AddContactViewController addViewController) {
        this.addViewController = addViewController;
    }

    public ChatController getChatController() {
        return chatController;
    }

    public void setChatController(ChatController chatController) {
        this.chatController = chatController;
    }

    public Map<Stage, Controller> getView() {
        return view;
    }

    public void setLoginController(LoginController loginController) {
        this.loginController = loginController;
    }

    public boolean successfulAction() {
        return successfulAction;
    }

    public void setSuccessfulAction(boolean successfulAction) {
        this.successfulAction = successfulAction;
    }

    public AddContactViewController getAddViewController() {
        return addViewController;
    }

    public LoginController getLoginController() {
        return loginController;
    }
}
