package org.example.chatbmbis;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.HashMap;
import java.util.Map;

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

    public static synchronized Mediator getInstance(User user) {
        if (instance == null) {
            instance = new Mediator();
            instance.setUser(user);
        }
        return instance;
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
        addController.setPromptText(promptText);
        addController.getButton1().setText(opt1);
        addController.getButton2().setText(opt2);

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
        String[] messageParts = Utils.split(message);
        Message messageObj;
        if (isErrorMessage(message)) {
            actionApproved = false;
            instanceErrWindow(messageParts[1]);
            return;
        } else if (messageApproved(messageParts[0])) {
            actionApproved = true;
            return;
        }

        String chatroomName = messageParts[0];
        String sender = messageParts[1];

        if (chatroomName.startsWith("#")) {
            // "#2dam bruno :hola"
            messageObj = new Message(sender, chatroomName, messageParts[2]);
        } else {
            // "bruno :hola"
            messageObj = new Message(chatroomName, messageParts[1]);
            if (!user.getContacts().contains(messageObj.getSender()) && !chatController.containsItemContact(messageObj.getSender())) {
                chatController.addContactItem(chatController.getvBoxPrivate(), messageObj.getSender());
                getUser().getContacts().add(messageObj.getSender());
                sendMessage("CREATE "+messageObj.getSender());
            }
        }
        user.addMessage(chatroomName, messageObj);
        // solo añadir al vbox del chat si este está abierto
        if (chatController.getReceptorChatLabel().getText().equals(chatroomName)) {
            chatController.addMessageToVBox(messageObj);
        }

    }

    private void instanceErrWindow(String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("");
            alert.setContentText(message);
            alert.showAndWait();
        });
    }

    private boolean messageApproved(String message) {
        return message.equals("ok");
    }
    private boolean isErrorMessage(String message) {
        if (message.split(" ")[0].equals("ERROR")) {
            return true;
        }
        return false;
    }

    public User getUser() {
        return user;
    }

    public AddContactViewController getAddViewController() {
        return addViewController;
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

    public ItemContactController getItemContactController() {
        return itemContactController;
    }

    public void setItemContactController(ItemContactController itemContactController) {
        this.itemContactController = itemContactController;
    }

    public Map<Stage, Controller> getView() {
        return view;
    }

    public void setView(Map<Stage, Controller> view) {
        this.view = view;
    }

    public static void setInstance(Mediator instance) {
        Mediator.instance = instance;
    }

    public LoginController getLoginController() {
        return loginController;
    }

    public void setLoginController(LoginController loginController) {
        this.loginController = loginController;
    }

    public Map<ItemContactController, String> getItemContactControllers() {
        return itemContactControllers;
    }

    public void setItemContactControllers(Map<ItemContactController, String> itemContactControllers) {
        this.itemContactControllers = itemContactControllers;
    }

    public boolean actionApproved() {
        return actionApproved;
    }

    public void setActionApproved(boolean actionApproved) {
        this.actionApproved = actionApproved;
    }
}
