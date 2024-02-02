package org.example.chatbmbis;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Mediator {
    AddViewController addViewController;
    public static ChatController chatController;
    ItemContactController itemContactController;
    LoginController loginController;

    private Map<Stage, Controller> view = new HashMap<>();
    public static Map<ItemContactController,String> itemContactControllers = new HashMap<>();

    private List<String> contacts;

    private User user;

    private static Mediator instance;


    public Mediator(){
        this.contacts=new ArrayList<>();
    }

    public static synchronized Mediator getInstance() {
        if (instance == null) {
            instance = new Mediator();
        }
        return instance;
    }



    public void createChatView(String nickName){
        Stage stage = null;
        for (Map.Entry<Stage, Controller> entry : view.entrySet()) {
           if (entry.getKey().getTitle().equals("Chat")){
               stage=entry.getKey();
           }
        }
        chatController.setNickName(nickName);

        user = new User(nickName,"localhost",80);
        PrintStream printStream = null;
        try {
            printStream = new PrintStream(user.socket.getOutputStream());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        printStream.println(nickName);

        stage.setResizable(false);
        stage.show();
        user.start();

    }

    public void sendMessageToClient(String mesage){
        user.sendMessage(mesage);
    }



    public void createAddView (String promtext,String buttonText){

        Stage stage = null;
        AddViewController addController= null;
        for (Map.Entry<Stage, Controller> entry : view.entrySet()) {
            if (entry.getKey().getTitle().equals("Add")){
                stage=entry.getKey();
                addController = (AddViewController) entry.getValue();
            }
        }
        addController.setPrompText(promtext);
        addController.getAddOrcreate().setText(buttonText);
        stage.setResizable(false);
        stage.show();

    }

    public void createChat(String idButton,String idChat){
        switch (idButton){
            case "Crear grupo":
               /* ChatRoom channel = new ChatRoom("#"+idChat);
                User user = (User) server.getUsersMap()
                    .values()
                    .stream()
                    .filter(u -> Objects.equals(u.getNickname(),chatController.getNickName()));


               server.createChannel(channel,user);

                break;
            case "Añadir usuario":
                ChatRoom privateChat = new ChatRoom(idChat);
                /*User user2 = (User) server.getUsersMap()
                        .values()
                        .stream()
                        .filter(u -> Objects.equals(u.getNickname(),chatController.getNickName()));
               // server.createChannel(privateChat,user2);
                createItemChat(idChat);
                break;
                //case "Añadir al grupo":
            */
        }

    }

    public void createItemChat(String nickName){
        FXMLLoader loader = new FXMLLoader(getClass().getResource("contactItemView.fxml"));
        Parent paren = null;
        try {
            paren = loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        ItemContactController itemContactController1 = loader.getController();
        String name = itemContactController1.getNickNameLabel();

        itemContactController1.setCallback(()->{
           chatController.setNickNameFriend(name);
        });

        itemContactController1.setNickNameLabel(name);
        itemContactControllers.put(itemContactController1,nickName);
        contacts.add(nickName);
        chatController.getvBoxPrivate().getChildren().add(paren);

    }

    public void reciveMessage(String text){
        String[] comandParts = Server.splitParts(text);
        chatController.addMessagesForeingUser(comandParts[0]);
    }


    public AddViewController getAddViewController() {
        return addViewController;
    }

    public void setAddViewController(AddViewController addViewController) {
        this.addViewController = addViewController;
    }

    public  ChatController getChatController() {
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
}
