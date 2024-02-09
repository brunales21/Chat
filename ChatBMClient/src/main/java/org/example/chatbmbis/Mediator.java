package org.example.chatbmbis;

import javafx.stage.Stage;

import java.util.HashMap;
import java.util.Map;

public class Mediator {
    private ChatController chatController;
    private AddContactViewController addViewController;
    private ItemContactController itemContactController;
    private LoginController loginController;
    private Map<Stage, Controller> view = new HashMap<>();
    private Map<ItemContactController, String> itemContactControllers = new HashMap<>();
    private User user;
    private static Mediator instance;


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

    public void sendHeader(String header) {
        user.sendHeader(header);
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void createAddView(String promptText, String buttonText) {
        Stage stage = null;
        AddContactViewController addController = null;
        for (Map.Entry<Stage, Controller> entry : view.entrySet()) {
            if (entry.getKey().getTitle().equals("Add")) {
                stage = entry.getKey();
                addController = (AddContactViewController) entry.getValue();
            }
        }
        addController.setPromptText(promptText);
        addController.getAddButton().setText(buttonText);
        stage.setResizable(false);
        stage.show();

    }

    public void createContactItem(String nickname) {
        chatController.createContactItem(nickname);
    }
    public void receiveMessage(String message) {
        String[] headerParts = Utils.split(message);
        Message messageObj;
        if (headerParts[0].startsWith("#")) {
            // "#2dam bruno :hola"
            messageObj = new Message(headerParts[1], headerParts[0], headerParts[2]);
        } else {
            // "bruno :hola"
            messageObj = new Message(headerParts[0], headerParts[1]);
        }
        user.addMessage(headerParts[0], messageObj);
        // solo añadir al vbox del chat si este está abierto
        if (chatController.getReceptorChatLabel().getText().equals(headerParts[0])) {
            chatController.addMessageToVBox(messageObj);
        }
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


    public User getUser() {
        return user;
    }
}
