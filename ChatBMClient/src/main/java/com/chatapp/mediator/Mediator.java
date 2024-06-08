package com.chatapp.mediator;

import com.chatapp.constants.Commands;
import com.chatapp.constants.Constants;
import com.chatapp.controllers.*;
import com.chatapp.daos.impl.FileChatDAO;
import com.chatapp.model.Message;
import com.chatapp.model.User;
import com.chatapp.utils.SyntaxUtils;
import com.chatapp.utils.WarningWindow;
import javafx.application.Platform;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.*;

public class Mediator {

    private static Mediator instance;
    private final Map<Stage, Controller> view = new HashMap<>();
    private ChatController chatController;
    private AddContactViewController addViewController;
    private LoginController loginController;
    private SignupController signupController;
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

    public void filterInput() {
        // El servidor siempre envia un mensaje de bienvenida, este metodo sirve para omitir esa parte. (no nos interesa para gui)
        // Si no la gestionamos, cuando recibamos mensajes importantes del servidor, estaremos recibiendo ruido.
        try {
            Scanner in = new Scanner(getUser().getSocket().getInputStream());
            String line;
            do {
                line = in.nextLine();
            } while (!line.endsWith("."));
        } catch (IOException e) {
            throw new RuntimeException(e);
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
                if (((FileChatDAO) getUser().getChatDAO()).getFile() != null) {
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
        getChatController().loadChatItems();
        Stage stage = null;
        for (Map.Entry<Stage, Controller> entry : view.entrySet()) {
            if (entry.getKey().getTitle().equals(Constants.CHAT_VIEW_TITLE)) {
                ChatController controller = (ChatController) entry.getValue();
                stage = entry.getKey();
                controller.getUserNameLabel().setText("Bienvenid@, " + user.getNickname());
                break;
            }
        }
        stage.setResizable(true);
        stage.show();
    }

    public void sendMessage(String message) {
        user.sendMessage(message);
    }

    public void createAddView(String promptText, String opt1, String opt2) {
        Stage stage = null;
        AddContactViewController addController = null;
        for (Map.Entry<Stage, Controller> entry : view.entrySet()) {
            if (entry.getKey().getTitle().equals(Constants.ADD_VIEW_TITLE)) {
                stage = entry.getKey();
                addController = (AddContactViewController) entry.getValue();
            }
        }

        ResourceBundle bundle = ResourceBundle.getBundle(Constants.BUNDLE_MESSAGES, Locale.getDefault());
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
        user.setServerResponse(message);
        String[] messageParts = SyntaxUtils.splitCommandLine(message);
        String keyWord = messageParts[0];
        if (actionRefused(keyWord)) {
            // si la accion fue rechazada
            setSuccessfulAction(false);
            WarningWindow.instanceWarningWindow(message);
        } else if (isActionApproved(keyWord)) {
            // si fue aprobada
            setSuccessfulAction(true);
        } else if (isTxtMessage(keyWord)) {
            // si tiene que procesar un msj de texto
            messageHandler(messageParts);
        }
    }

    private void messageHandler(String[] messageParts) {
        Message messageObj;
        messageParts[1] = messageParts[1].toLowerCase();
        if (messageParts[1].startsWith("#")) {
            // "MESSAGE #2dam bruno:hola"
            messageObj = new Message(messageParts[2], messageParts[1], messageParts[3]);
            if (!getUser().containsContact(messageParts[1])) {
                chatController.addContactItem(chatController.getvBoxContacts(), messageObj.getSender());
            }
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
            chatController.getContactsMap().get(messageParts[1]).showNotificationImg(true);
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

    public void setUser(User user) {
        this.user = user;
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

    public boolean successfulAction() {
        return successfulAction;
    }

    public void setSuccessfulAction(boolean successfulAction) {
        this.successfulAction = successfulAction;
    }

    public AddContactViewController getAddViewController() {
        return addViewController;
    }

    public void setAddViewController(AddContactViewController addViewController) {
        this.addViewController = addViewController;
    }

    public LoginController getLoginController() {
        return loginController;
    }

    public void setLoginController(LoginController loginController) {
        this.loginController = loginController;
    }

    public SignupController getSignupController() {
        return signupController;
    }

    public void setSignupController(SignupController signupController) {
        this.signupController = signupController;
    }
}
