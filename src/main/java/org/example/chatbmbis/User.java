package org.example.chatbmbis;

import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class User extends Client {
    private String nickname;
    private String command;
    private List<ChatRoom> chatRooms;
    Mediator mediator;


    public User(String name, String hostname, int port) {
        super(hostname, port);
        this.nickname = name;
        this.chatRooms = new ArrayList<>();
        mediator = Mediator.getInstance();
        try {
            this.socket = new Socket(hostname, port);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public User() {
        super();
    }

    public User(String nickname) {
        this.nickname = nickname;
    }

    @Override
    public void run() {
        while (!socket.isClosed()) {
            Scanner in = null;
            try {
                in = new Scanner(socket.getInputStream());
                mediator.receiveMessage(in.nextLine());
            } catch (NoSuchElementException ignored) {

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void sendMessage(String message) {
        PrintStream out = null;
        try {
            out = new PrintStream(socket.getOutputStream());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        out.println(message);

    }

    public void createChannel(String channelName) {
        sendMessage("CREATE #" + channelName);
    }

    //Asociar a controlador de la vista
    public void createPrivateChat(String idUser) {
        sendMessage("CREATE " + idUser);
    }


    //Mandar a la vista el mensaje
    public void receive(String message) {

        Scanner in = null;
        try {
            in = new Scanner(socket.getInputStream());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        while (true) {
            try {
                //Recibe el nickname de quien lo envia y el mensaje
                String[] comand = message.split(";");
                System.out.println(comand[1]);

            } catch (NoSuchElementException ignore) {

            }
        }


    }

    public List<ChatRoom> getChatRooms() {
        return chatRooms;
    }

    public void addChatRoom(ChatRoom chatRoom) {
        chatRooms.add(chatRoom);
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }
}
