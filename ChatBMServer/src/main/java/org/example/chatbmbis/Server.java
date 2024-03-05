package org.example.chatbmbis;

import org.example.chatbmbis.exceptions.*;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.function.Consumer;

public class Server {

    private final int port;
    private final List<PrivateChat> privateChats;
    private final List<Channel> channels;
    private final Map<String, PrintStream> userOutMap;
    private final Map<Socket, String> socketUserMap;
    private final Set<String> historyUsers;

    private final String REGISTER_COMMAND = "REGISTER";


    public Server(int port) {
        this.port = port;
        channels = new ArrayList<>();
        privateChats = new ArrayList<>();
        userOutMap = new HashMap<>();
        socketUserMap = new HashMap<>();
        historyUsers = new HashSet<>();
    }

    private void register(String nickname, Socket socket) throws UserExistsException {
        try {
            if (userOutMap.containsKey(nickname)) {
                throw new UserExistsException(nickname);
            }
            userOutMap.put(nickname, new PrintStream(socket.getOutputStream()));
            historyUsers.add(nickname);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        socketUserMap.put(socket, nickname);
        sendMessagesFromFile(nickname);
    }

    public void start() throws IOException {
        ServerSocket serverSocket = new ServerSocket(port);
        while (true) {
            Socket socket = serverSocket.accept();
            Thread thread = new Thread(() -> clientHandler(socket));
            thread.start();
        }
    }

    private void clientHandler(Socket socket) {
        Scanner in = null;
        boolean registered = false;
        try {
            in = new Scanner(socket.getInputStream());
            String header;
            showFileContent(socket, "welcome.txt");
            while (socket.isConnected()) {
                if (in.hasNextLine()) {
                    header = BackspaceRemover.removeBackspaces(in.nextLine());
                    if (!registered) {
                        while (!splitParts(header)[0].equalsIgnoreCase(REGISTER_COMMAND)) {
                            sendMessage(socket, "Primero debe registrarse. Ej: REGISTER mi_username");
                            header = BackspaceRemover.removeBackspaces(in.nextLine());
                        }
                        registered = true;
                    }
                    processCommand(socket, header);
                }
            }
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }

    }

    private void processCommand(Socket senderSocket, String command) {
        System.out.println("Header que recibe servidor: " + command);
        String[] commandParts = splitParts(command);
        String sender = socketUserMap.get(senderSocket);
        String commandName = commandParts[0].toUpperCase();
        String arg = "";
        if (commandParts.length > 1) {
            arg = commandParts[1];
        }
        switch (commandName) {
            case "REGISTER":
                try {
                    register(arg, senderSocket);
                    sendMessage(senderSocket, "Listo para chatear. Puede usar el comando HELP como ayuda.");
                    sendOk(senderSocket);
                } catch (UserExistsException e) {
                    sendErrorMsg(senderSocket, e.getMessage());
                }
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
                String msg = commandParts[2];
                try {
                    if (arg.startsWith("#")) {
                        //PRIVMSG #dam monica :hola
                        broadcast(sender, arg, msg);
                    } else {
                        //PRIVMSG monica :hola
                        sendMessage(sender, arg, msg);
                    }
                } catch (UserNotExistsException e) {
                    sendErrorMsg(sender, e.getMessage());
                } catch (UserNotConnectedException e) {
                    persistMsg(sender, arg, msg, null);
                }
                break;
            case "JOIN":
                try {
                    join(sender, arg);
                    sendOk(sender);
                } catch (ChatNotFoundException e) {
                    sendErrorMsg(sender, e.getMessage());
                }
                break;
            case "DELETE":
                try {
                    deletePrivChat(sender, arg);
                    sendOk(sender);
                } catch (UserNotExistsException e) {
                    sendErrorMsg(sender, e.getMessage());
                }
                break;

            case "PART":
                try {
                    part(sender, arg);
                } catch (ChatNotFoundException e) {
                    sendErrorMsg(sender, e.getMessage());
                }
                break;
            case "LU":
                listAllUsers(sender);
                break;
            case "LC":
                listChannels(sender);
                break;
            case "HELP":
                showFileContent(senderSocket, "help.txt");
                break;
            case "EXIT":
                try {
                    //exitMessage(sender);
                    senderSocket.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                socketUserMap.remove(senderSocket);
                userOutMap.remove(sender);
                break;
            default:
                sendErrorMsg(sender, "El comando " + commandName + " no existe.");
                break;
        }

    }

    private void sendOk(String sender) {
        sendMessage(sender, "ok");
    }

    private void sendOk(Socket socket) {
        sendMessage(socket, "ok");
    }
    private void deletePrivChat(String sender, String chatName) throws UserNotExistsException {
        privateChats.remove(getPrivChatByName(sender, chatName));
    }

    private boolean existsUser(String nickname) {
        return historyUsers.contains(nickname);
    }

    private void part(String sender, String channel) throws ChatNotFoundException {
        getChannelByName(channel).getUsers().remove(sender);
    }

    private Channel getChannelByName(String name) throws ChatNotFoundException {
        try {
            return channels.stream().filter(c -> c.getName().equals(name)).toList().get(0);
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new ChatNotFoundException(name);
        }
    }

    private PrivateChat getPrivChatByName(String user1, String user2) throws UserNotExistsException {
        try {
            return privateChats.stream().filter(c -> c.getUser1().equals(user1) && c.getUser2().equals(user2)).toList().get(0);
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new UserNotExistsException(user2);
        }
    }

    private void sendErrorMsg(String sender, String errorMessage) {
        // le rebota el msj
        userOutMap.get(sender).println("ERROR :" + errorMessage);
    }

    private void sendErrorMsg(Socket socket, String errorMessage) {
        // le rebota el msj
        PrintStream out;
        try {
            out = new PrintStream(socket.getOutputStream());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        out.println("ERROR :" + errorMessage);
    }

    private List<String> getUsersInChannel(String channelName) {
        try {
            return getChannelByName(channelName).getUsers();
        } catch (ChatNotFoundException e) {
            return new ArrayList<>();
        }
    }

    public void broadcast(String sender, String channel, String textMessage) {
        getUsersInChannel(channel).stream()
                .filter(u -> !u.equals(sender))
                .forEach(user -> {
                    //#2dam bruno :hola
                    try {
                        sendMsgToChannelMember(sender, user, channel, textMessage);
                    } catch (UserNotConnectedException e) {
                        persistMsg(sender, user, textMessage, channel);
                    }
                });
    }

    public void sendMsgToChannelMember(String sender, String receptor, String channel, String text) throws UserNotConnectedException {
        //Este nunca lanzaría UserNotFoundExc
        try {
            //MESSAGGE #2dam bruno :hola
            userOutMap.get(receptor).println("MESSAGE " + channel + " " + sender + " :" + text);
        } catch (NullPointerException e) {
            throw new UserNotConnectedException(receptor);
        }
    }

    public void sendMessage(String sender, String receptor, String text) throws UserNotExistsException, UserNotConnectedException {
        try {
            userOutMap.get(receptor).println("MESSAGE " + sender + " :" + text);
        } catch (NullPointerException e) {
            if (historyUsers.contains(receptor)) {
                throw new UserNotConnectedException(receptor);
            }
            throw new UserNotExistsException(receptor);
        }
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

    private void createPrivChat(String user1, String user2) throws ChatRepeatedException, UserNotExistsException {
        if (existsUser(user2)) {
            PrivateChat privateChat = new PrivateChat(user1, user2);
            if (privateChats.contains(privateChat)) {
                throw new ChatRepeatedException(user2);
            } else {
                privateChats.add(privateChat);
            }
        } else {
            throw new UserNotExistsException(user2);
        }
    }

    private void createFileIfNotExists(String fileName) {
        Path path = Path.of(fileName);
        if (!Files.exists(path)) {
            try {
                Files.createFile(path);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void persistMsg(String sender, String fileName, String message, String channel) {
        String targetFile = fileName + "-messages.csv";
        createFileIfNotExists(targetFile);
        try (PrintStream out = new PrintStream(new FileOutputStream(targetFile, true))) {
            if (channel == null) {
                out.println(sender + " :" + message);
            } else {
                out.println(channel + " " + sender + " :" + message);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void sendMessagesFromFile(String receptor) {
        String fileName = receptor+"-messages.csv";
        Path path = Path.of(fileName);
        if (Files.exists(path)) {
            try (BufferedReader in = new BufferedReader(new FileReader(fileName))) {
                String message;
                while ((message = in.readLine()) != null) {
                    String[] parts = splitParts(message);
                    String sender;
                    String msg;
                    if (parts[0].startsWith("#")) {
                        // #dam monica :hola
                        sender = parts[1];
                        msg = parts[2];
                        sendMsgToChannelMember(sender, receptor, parts[0], msg);
                    } else {
                        // monica :hola
                        sender = parts[0];
                        msg = parts[1];
                        sendMessage(sender, receptor, msg);

                    }
                }
            } catch (UserNotExistsException | IOException | UserNotConnectedException e) {
                throw new RuntimeException(e);
            }
            try {
                Files.delete(path);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
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


    public void sendMessage(Socket socket, String message) {
        PrintStream out = null;
        try {
            out = new PrintStream(socket.getOutputStream());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        out.println(message);

    }

    public void sendMessage(String sender, String text) {
        userOutMap.get(sender).println(text);
    }



    private void listChannels(String sender) {
        if (!channels.isEmpty()) {
            channels.forEach(c -> userOutMap.get(sender).println(c.getName()));
        } else {
            userOutMap.get(sender).println("No existen canales en el servidor.");
        }
    }

    private void listAllUsers(String sender) {
        sendMessage(sender, "Usuarios en linea:");
        listUsers(getOnlineUsers(), sender);
        sendMessage(sender, "Usuarios desconectados:");
        listUsers(getOfflineUsers(), sender);
    }

    private void listUsers(Collection<String> users, String sender) {
        users.forEach(u -> userOutMap.get(sender).println("- " + u));
    }


    private Collection<String> getOnlineUsers() {
        return socketUserMap.values();
    }

    private List<String> getOfflineUsers() {
        return historyUsers.stream().filter(u -> !getOnlineUsers().contains(u)).toList();
    }

    private void exitMessage(String sender) {
        sendMessage(sender, "Hasta pronto!");
    }

    private void showFileContent(Socket socket, String fileName) {
        try {
            BufferedReader in = new BufferedReader(new FileReader(fileName));
            String line;
            while ((line = in.readLine()) != null) {
                System.out.println(line);
                sendMessage(socket, line);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
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