package org.example.chatbmbis;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

public class Server {

    private ServerSocket serverSocket;
    private int port;
    private Map<String, String> privateChats;
    private Map<String, Channel> channels;
    private Map<String, PrintStream> userOutMap;
    private Map<Socket, String> socketUserMap;



    public Server(int port) {
        this.port = port;
        channels = new HashMap<>();
        privateChats = new HashMap<>();
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
            new Thread(() -> clientHandler(socket)).start();
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
        String arg = headerParts[1].toLowerCase();
        String sender = socketUserMap.get(socket);
        switch (command) {
            case "REGISTER":
                register(arg, socket);
                break;
            case "CREATE":
                if (arg.startsWith("#")) {
                    try {
                        findUser(arg);
                        createChannel(sender, arg);
                    } catch (UserNotFoundException e) {
                        sendErrorMsg(sender ,e.getMessage());
                    }

                } else {
                    try {
                        findUser(arg);
                        createPrivChat(sender, arg);
                    } catch (UserNotFoundException e) {
                        // rebota comando
                        sendErrorMsg(sender, e.getMessage());
                    }
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
                    findUser(arg);
                    join(sender, headerParts[1]);
                } catch (UserNotFoundException e) {
                    sendErrorMsg(sender ,e.getMessage());
                }
                break;
            case "LU":
                //listUsers(socket);
            case "LC":
                //listChannels(socket);
        }

    }

    private PrintStream findUser(String user) throws UserNotFoundException {
        PrintStream out = null;
        Map.Entry channelEntry = null;
        if (user.startsWith("#")){
            try {
                channelEntry = channels.entrySet().stream().filter(c -> c.getKey().equals(user)).toList().get(0);
            }catch (ArrayIndexOutOfBoundsException e){
                throw new UserNotFoundException(user);
            }
        }else {
            out = userOutMap.get(user);
            if (out == null) {
                throw new UserNotFoundException(user);
            }
        }
        return out;
    }

    private void sendErrorMsg(String sender, String errorMessage) {
        // le rebota el msj
        userOutMap.get(sender).println("ERROR :" + errorMessage);
    }

    private List<String> getUsersInChannel(String channelName) {
        return getChannelByName(channelName).getUsers();
    }

    public void broadcast(String sender, String channelName, String textMessage) {
        getUsersInChannel(channelName).stream()
                .filter(u -> !u.equals(sender))
                .forEach(user -> {
                    //#2dam bruno :hola
                    userOutMap.get(user).println(channelName + " " + sender + " :" + textMessage);
                });
    }

    private void join(String sender, String channelName) {
        getChannelByName(channelName).addUser(sender);

    }

    public Channel getChannelByName(String channelName) {
        return channels.get(channelName);
    }

    private void createChannel(String nickname, String channelName) {
        Channel channel = new Channel(channelName);
        channel.addUser(nickname);
        channels.put(channelName, channel);
    }

    private void createPrivChat(String user1, String user2) {
        privateChats.put(user1, user2);
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
        //userOutMap.get(sender).println(sender + " :" + text);

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