package org.example.chatbmbis;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

public class Server {

    private ServerSocket serverSocket;
    private int port;
    private List<Channel> channels;
    private static Map<Socket, User> socketUserMap;
    private static Map<User, Socket> userSocketMap;

    private Map<String, List<Message>> chatMap;


    public Server() {
        //this.privateChats = new ArrayList<>();
        //this.channels = new ArrayList<>();
        userSocketMap = new HashMap<>();
        socketUserMap = new HashMap<>();
        chatMap = new HashMap<>();
        channels = new ArrayList<>();

    }


    public void start() throws IOException {
        this.serverSocket = new ServerSocket(80);
        while (true) {
            Socket socket = serverSocket.accept();

            User user = new User();

            socketUserMap.put(socket, user);
            userSocketMap.put(user, socket);

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

    private void register(Socket socket, String nickname) {
        socketUserMap.get(socket).setNickname(nickname);
    }

    private void join(String channelName) {
        channels.add(new Channel(channelName, this));
    }

    private Channel getChannelByName(String channelName) {
        return channels.stream()
                .filter(c -> c.getChannelName().equals(channelName))
                .toList()
                .get(0);
    }

    private void sendMessageToChannel(String channelName, String textMessage) {
        Channel channel = getChannelByName(channelName);
        channel.broadcast(textMessage);
    }

    private void create(String userNickname, String chatroomNickname) {
        if (userNickname.startsWith("#")) {
            Channel channel = new Channel(chatroomNickname);
            try {
                User user = getUserByNickname(userNickname);
                channel.addUser(user);
                user.getChannels().add(channel);
            } catch (UserNotFoundException ignored) {

            }
        } else {
            try {
                User user1 = getUserByNickname(userNickname);
                User user2 = getUserByNickname(chatroomNickname);
                PrivateChat privateChat = new PrivateChat(user1, user2);
                user1.getPrivateChats().add(privateChat);
            } catch (UserNotFoundException e) {
                // cuando el user2 no se encuentre en el servidor
            }
        }
    }

    private void processCommand(Socket socket, String header) {
        System.out.println("Header que recibe servidor: "+header);
        String[] parts = splitParts(header);
        try {
            switch (parts[0]) {
                case "REGISTER":
                    register(socket, parts[1]);
                    break;
                case "CREATE":
                    create(socketUserMap.get(socket).getNickname(), parts[2]);
                    break;
                case "PRIVMSG":
                    if (parts[1].startsWith("#")) {
                        sendMessageToChannel(parts[1], parts[2]);
                    } else {
                        sendMessage(userSocketMap.get(getUserByNickname(parts[1])), socketUserMap.get(socket), parts[2]);
                    }
                    break;
                case "JOIN":
                    join(parts[1]);
                    break;
                case "LU":
                    //listUsers(socket);
                case "LC":
                    //listChannels(socket);
            }

        } catch (UserNotFoundException e) {
            ErrorWindow.mostrarMensaje("Este usuario no existe.");
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

    public static User getUserByNickname(String nickname) throws UserNotFoundException {
        try {
            return userSocketMap.keySet().stream()
                    .filter(u -> Objects.equals(u.getNickname(), nickname))
                    .toList()
                    .get(0);
        } catch (IndexOutOfBoundsException e) {
            throw new UserNotFoundException(nickname);
        }

    }

    public void sendMessage(Socket socket, User sender, String textMessage) {
        PrintStream out = null;
        try {
            out = new PrintStream(socket.getOutputStream());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        out.println(sender.getNickname() + " :" + textMessage);

    }

    public static void main(String[] args) {
        Server server = new Server();
        try {
            server.start();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public ServerSocket getServerSocket() {
        return serverSocket;
    }

    public void setServerSocket(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }


    public Map<Socket, User> getSocketUserMap() {
        return socketUserMap;
    }

    public void setSocketUserMap(Map<Socket, User> socketUserMap) {
        this.socketUserMap = socketUserMap;
    }

    public Map<User, Socket> getUserSocketMap() {
        return userSocketMap;
    }

    public void setUserSocketMap(Map<User, Socket> userSocketMap) {
        this.userSocketMap = userSocketMap;
    }
}