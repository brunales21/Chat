package org.example.chatbmbis;

import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Mediator {
    private ChatController chatController;
    private AddContactViewController addViewController;
    private ItemContactController itemContactController;
    private LoginController loginController;
    private Map<Stage, Controller> view = new HashMap<>();
    private Map<ItemContactController, String> itemContactControllers = new HashMap<>();
    private List<String> contacts;
    private User user;

    private static Mediator instance;


    public Mediator() {
        this.contacts = new ArrayList<>();
        user = new User();
    }

    public static synchronized Mediator getInstance(User user) {
        if (instance == null) {
            instance = new Mediator();
            instance.setUser(user);
        }
        return instance;
    }


    public void ingresar(String nickname) {
        user.ingresar(nickname);
    }

    public void createChatView() {
        Stage stage = null;
        for (Map.Entry<Stage, Controller> entry : view.entrySet()) {
            if (entry.getKey().getTitle().equals("Chat")) {
                stage = entry.getKey();
                break;
            }
        }
        stage.setResizable(false);
        stage.show();

    }

    public void sendMessage(String messageText){
        String [] split = Server.splitParts(messageText);
        if (split[0].equals("PRIVMSG")){
            user.sendSaveMessage(messageText);
        } else if (split[0].equals("JOIN")) {
            user.sendMessage(split[0]+" "+user.getNickname()+":"+split[1]);
        } else  {
            user.sendMessage(messageText);
        }
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

    public void receiveMessage(String header) {
        String[] headerParts = Server.splitParts(header);
        String senderNickname;
        String messageText;
        if (headerParts[1].startsWith("#")){
            //el mensaje para grupo enviara "#2dam:hola:b"
            String [] splitNicknameSender = headerParts[headerParts.length-1].split(";");
            senderNickname=splitNicknameSender[1];
            messageText=splitNicknameSender[0];
            chatController.addMessagesForeingUser(senderNickname + ": " + messageText+":"+headerParts[1]);
        }else  {
            //si el mensaje es privado mandaremos "hola:b"
            senderNickname=headerParts[0];
            messageText=headerParts[1];
            chatController.addMessagesForeingUser(messageText+":"+senderNickname);
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

    public List<String> getContacts() {
        return contacts;
    }

    public User getUser() {
        return user;
    }
}
