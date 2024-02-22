package org.example.chatbmbis;

import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.*;

public class Mediator {
    private static Mediator instance;
    private ChatController chatController;
    private AddContactViewController addViewController;
    private ItemContactController itemContactController;
    private LoginController loginController;
    private Map<Stage, Controller> view = new HashMap<>();
    private Map<ItemContactController, String> itemContactControllers = new HashMap<>();
    private User user;
    private boolean actionApproved = true;


    public Mediator() {

    }

    public static synchronized Mediator getInstance() {
        if (instance == null) {
            instance = new Mediator();
        }
        return instance;
    }

    public void ingresar(String nickname) {
        user.ingresar(nickname);
    }

    public void createChatView(String nickname) {
        Stage stage = null;
        for (Map.Entry<Stage, Controller> entry : view.entrySet()) {
            if (entry.getKey().getTitle().equals("Chat")) {
                ChatController controller = (ChatController) entry.getValue();
                stage = entry.getKey();
                controller.getUserNameLabel().setText(nickname);
                break;
            }
        }
        stage.setResizable(false);
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
        chatController.emptyVBoxMessages();
    }


    public void receiveMessage(String message) {
        //System.out.println(message);
        String[] messageParts = Utils.split(message);
        String keyWord = message.split(" ")[0];
        Message messageObj;
        if (actionRefused(keyWord)) {
            // si la accion fue rechazada
            setActionApproved(false);
            ErrorWindow.instanceErrorWindow(messageParts[1]);
        } else if (actionApproved(keyWord)) {
            // si fue aprobada
            setActionApproved(true);
        } else {
            // si tiene que procesar un msj de texto
            if (messageParts[0].startsWith("#")) {
                // "#2dam bruno :hola"
                messageObj = new Message(messageParts[1], messageParts[0], messageParts[2]);
            } else {
                // "bruno :hola"
                messageObj = new Message(messageParts[0], messageParts[1]);
                if (!chatController.hasContact(messageParts[0])) {
                    chatController.addContactItem(chatController.getvBoxPrivate(), messageObj.getSender());
                    sendMessage("CREATE " + messageObj.getSender());
                }
            }
            user.addMessage(messageParts[0], messageObj);
            // si el chat abierto coincide con el emisor del mensaje..
            if (chatController.getReceptorChatLabel().getText().equals(messageParts[0])) {
                chatController.addMessageToVBox(messageObj);
            } else {
                // si no, mostramos notificacion
                chatController.getItemContactsMap().get(messageParts[0]).showNotificationImg(true);
            }
        }


    }

    private boolean actionApproved(String message) {
        return message.isEmpty() || message.equals("ok") || message.equals("Bienvenido,") || message.equals("Bienvenido!") || message.equals("Si") || message.equals("Diviertete");
    }

    private boolean actionRefused(String message) {
        return message.equals("ERROR");
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

    public boolean actionApproved() {
        return actionApproved;
    }

    public void setActionApproved(boolean actionApproved) {
        this.actionApproved = actionApproved;
    }
}
