package org.example.chatbmbis;

import org.example.chatbmbis.exceptions.ChatException;
import org.example.chatbmbis.exceptions.ChatNotFoundException;
import org.example.chatbmbis.exceptions.ChatRepeatedException;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

public class Server {

    private ServerSocket serverSocket;
    private int port;
    private List<PrivateChat> privateChats;
    private List<Channel> channels;
    private Map<String, PrintStream> userOutMap;
    private Map<Socket, String> socketUserMap;


    public Server(int port) {
        this.port = port;
        channels = new ArrayList<>();
        privateChats = new ArrayList<>();
        userOutMap = new HashMap<>();
        socketUserMap = new HashMap<>();
    }

    private void register(String nickname, Socket socket) {
        try {
            userOutMap.put(nickname, new PrintStream(socket.getOutputStream()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        socketUserMap.put(socket, nickname);
    }

    public void start() throws IOException {
        this.serverSocket = new ServerSocket(port);
        while (true) {
            Socket socket = serverSocket.accept();
            Thread thread = new Thread(() -> clientHandler(socket));
            thread.start();
        }
    }

    private void clientHandler(Socket socket) {
        Scanner in = null;
        try {
            in = new Scanner(socket.getInputStream());
            String header;
            while (!socket.isClosed()) {
                if (in.hasNextLine()) {
                    header = in.nextLine();
                    processCommand(socket, header);
                }
            }
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    private void processCommand(Socket socket, String header) {
        System.out.println("Header que recibe servidor: '" + header + "' de " + socketUserMap.get(socket));
        String[] headerParts = splitParts(header);
        String command = headerParts[0].toUpperCase();
        String arg = headerParts[1];
        String sender = socketUserMap.get(socket);
        switch (command) {
            case "REGISTER":
                register(arg, socket);
                break;
            case "CREATE":
                try {
                    if (arg.startsWith("#")) {
                        createChannel(sender, arg);
                    } else {
                        createPrivChat(sender, arg);
                    }
                    sendOk(sender);
                } catch (ChatException e) {
                    sendErrorMsg(sender, e.getMessage());
                }

                break;
            case "PRIVMSG":
                if (arg.startsWith("#")) {
                    broadcast(sender, headerParts[1], headerParts[2]);
                } else {
                    sendMessage(sender, arg, headerParts[2]);
                }
                break;
            case "JOIN":
                try {
                    join(sender, headerParts[1]);
                    sendOk(sender);
                } catch (ChatNotFoundException e) {
                    sendErrorMsg(sender, e.getMessage());
                }
                break;
            case "DELETE":
                try {
                    deletePrivChat(sender, arg);
                    sendOk(sender);
                } catch (ChatNotFoundException e) {
                    sendErrorMsg(sender, e.getMessage());
                }
                break;
            case "EXIT":
                try {
                    socket.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

            case "LU":
                //listUsers(socket);
            case "LC":
                //listChannels(socket);
        }

    }

    private void sendOk(String sender) {
        sendMessage(sender, "ok");
    }
    private void findReceptor(String nickname) throws ChatNotFoundException {
        if (userOutMap.get(nickname) != null || existsChannel(nickname) ) {
            return;
        }
        throw new ChatNotFoundException(nickname);

    }

    private void deletePrivChat(String sender, String chatName) throws ChatNotFoundException {
        privateChats.remove(getPrivChatByName(sender, chatName));
    }

    private boolean existsUser(String nickname) throws ChatNotFoundException {
        return userOutMap.get(nickname) != null;
    }


    private boolean existsChannel(String name) {
        return channels.stream().anyMatch(c -> c.getName().equals(name));
    }

    private Channel getChannelByName(String name) throws ChatNotFoundException {
        try {
            Channel channel = channels.stream().filter(c -> c.getName().equals(name)).toList().get(0);
            return channel;
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("CANAL NO EXISTE");
            throw new ChatNotFoundException(name);
        }
    }

    private PrivateChat getPrivChatByName(String user1, String user2) throws ChatNotFoundException {
        try {
            PrivateChat privateChat = privateChats.stream().filter(c -> c.getUser1().equals(user1) && c.getUser2().equals(user2)).toList().get(0);
            return privateChat;
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new ChatNotFoundException(user2);
        }
    }
    private void sendErrorMsg(String sender, String errorMessage) {
        // le rebota el msj
        userOutMap.get(sender).println("ERROR :" + errorMessage);
    }

    private List<String> getUsersInChannel(String channelName) {
        try {
            return getChannelByName(channelName).getUsers();
        } catch (ChatNotFoundException e) {
            return new ArrayList<>();
        }
    }

    public void broadcast(String sender, String channelName, String textMessage) {
        getUsersInChannel(channelName).stream()
                .filter(u -> !u.equals(sender))
                .forEach(user -> {
                    //#2dam bruno :hola
                    userOutMap.get(user).println(channelName + " " + sender + " :" + textMessage);
                });
    }

    private void join(String sender, String channelName) throws ChatNotFoundException {
        getChannelByName(channelName).addUser(sender);
    }

    private void createChannel(String nickname, String channelName) throws ChatRepeatedException {
        Channel channel = new Channel(channelName);
        if (channels.contains(channel)) {
            throw new ChatRepeatedException(channelName);
        } else {
            channel.addUser(nickname);
            channels.add(channel);
        }
    }

    private void createPrivChat(String user1, String user2) throws ChatRepeatedException, ChatNotFoundException {
        if (existsUser(user2)) {
            System.out.println(user2 + " EXISTE !!");
            PrivateChat privateChat = new PrivateChat(user1, user2);
            if (privateChats.contains(privateChat)) {
                throw new ChatRepeatedException(user2);
            } else {
                privateChats.add(privateChat);
            }
        } else {
            throw new ChatNotFoundException(user2);
        }
    }




    public static String[] splitParts(String header) {
        String[] split = header.split(" ");
        int colonIndex = header.indexOf(":");
        if (split.length == 1 || colonIndex == -1) {
            return split;
        }

        String textMessage = header.substring(colonIndex + 1); // +1 para excluir el ':'
        String prefix = header.substring(0, colonIndex);

        List<String> partsList = new ArrayList<>(Arrays.asList(prefix.split(" ")));
        partsList.add(textMessage);

        return partsList.toArray(new String[0]);
    }


    public void sendMessage(String sender, String receptor, String text) {
        userOutMap.get(receptor).println(sender + " :" + text);

    }

    public void sendMessage(String sender, String text) {
        userOutMap.get(sender).println(text);
    }

    public static void main(String[] args) {
        Server server = new Server(9001);
        try {
            server.start();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}