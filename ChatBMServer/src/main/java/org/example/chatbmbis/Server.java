package org.example.chatbmbis;

import org.example.chatbmbis.IAs.AIConnector;
import org.example.chatbmbis.Utils.Utils;
import org.example.chatbmbis.constants.Commands;
import org.example.chatbmbis.exceptions.*;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class Server {

    private final int port;
    private final Set<PrivateChat> privateChats;
    private final Set<Channel> channels;
    private final Map<User, PrintStream> userOutMap;
    private final Map<Socket, User> socketUserMap;
    private final Map<User, Socket> userSocketMap;
    private final Set<User> historyUsers;
    private final Set<User> chatbots;
    private final String MSGS_FOLDER_NAME = "messages";

    public Server(int port) {
        this.port = port;
        channels = new HashSet<>();
        privateChats = new HashSet<>();
        userOutMap = new HashMap<>();
        socketUserMap = new HashMap<>();
        userSocketMap = new HashMap<>();
        historyUsers = new HashSet<>();
        chatbots = new HashSet<>();
    }

    public Server(String port) {
        this(Integer.parseInt(port));
    }

    public Server() {
        this(8080);
    }

    public void start() {
        createIA();
        ServerSocket serverSocket;
        try {
            serverSocket = new ServerSocket(port);
            System.out.println("El servidor se ha iniciado correctamente.");
            System.out.println("Está escuchando en el puerto " + port + ".");

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        while (true) {
            Socket socket;
            try {
                socket = serverSocket.accept();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            Thread thread = new Thread(() -> clientHandler(getClientType(socket), socket));
            thread.start();
        }
    }

    private void registerUI(User user, Socket socket) throws NicknameInUseException {
        if (userOutMap.containsKey(user)) {
            throw new NicknameInUseException(user.getNickname());
        }
        try {
            userOutMap.put(user, new PrintStream(socket.getOutputStream()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        historyUsers.add(user);
        socketUserMap.put(socket, user);
        userSocketMap.put(user, socket);
        sendOk(socket);
    }

    private void registerCLI(User user, Socket socket) throws NicknameInUseException {
        try {
            if (userOutMap.containsKey(user)) {
                throw new NicknameInUseException(user.getNickname());
            }
            userOutMap.put(user, new PrintStream(socket.getOutputStream()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        socketUserMap.put(socket, user);
        userSocketMap.put(user, socket);
        historyUsers.add(user);
    }


    private String getClientType(Socket socket) {
        Scanner in = null;
        try {
            in = new Scanner(socket.getInputStream());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return in.nextLine();

    }

    private void handleUIClient(Socket socket) {
        try {
            Scanner in = new Scanner(socket.getInputStream());
            User user = new User(ClientType.UI_CLIENT);
            do {
                user.setNickname(getNicknameFromUICommand(in.nextLine()));
                try {
                    registerUI(user, socket);
                    break;
                } catch (NicknameInUseException e) {
                    sendErrorMsg(socket, e.getMessage());
                }
            } while (!socket.isClosed());
            sendOk(socket);
            sendSavedMessages(user);
            do {
                processCommand(socket, in.nextLine());
            } while (!socket.isClosed());
        } catch (IOException | NoSuchElementException ignore) {

        }
    }

    private void handleCliClient(Socket socket) {
        try {
            sendFileContent(socket, "welcome.txt");
            Scanner in = new Scanner(socket.getInputStream());
            User user = null;
            do {
                sendFileContent(socket, "instructions.txt");
                try {
                    user = new User(getNickname(BackspaceRemover.removeBackspaces(in.nextLine())), ClientType.CLI_CLIENT);
                    registerCLI(user, socket);
                    sendMessage(socket, "Listo para chatear. Puede usar el comando HELP como ayuda.");
                    break;
                } catch (RegisterException e) {
                    sendErrorMsg(socket, e.getMessage());
                }
            } while (true);
            sendSavedMessages(user);
            do {
                processCommand(socket, BackspaceRemover.removeBackspaces(in.nextLine()));
            } while (!socket.isClosed());
        } catch (IOException | NoSuchElementException ignore) {

        }
    }

    private void clientHandler(String clientType, Socket socket) {
        if (clientType.equalsIgnoreCase("UI_CLIENT")) {
            handleUIClient(socket);
        } else {
            handleCliClient(socket);
        }
    }

    private void processCommand(Socket senderSocket, String command) {
        System.out.println("Header que recibe servidor: " + command);
        String[] commandParts = Utils.splitCommandLine(command);
        User user = socketUserMap.get(senderSocket);
        String commandName = commandParts[0].toUpperCase();
        String arg = "";
        if (commandParts.length > 1) {
            arg = commandParts[1].toLowerCase();
        }
        switch (commandName) {
            case "CREATE":
                try {
                    if (arg.startsWith("#")) {
                        createChannel(user, arg);
                    } else {
                        createPrivChat(user.getNickname(), arg);
                    }
                    sendOk(user);
                } catch (ChatException e) {
                    sendErrorMsg(user, e.getMessage());
                }
                break;
            case "PRIVMSG":
                String msg = commandParts[2];
                try {
                    if (arg.startsWith("#")) {
                        broadcast(user, arg, msg);
                    } else {
                        if (arg.equalsIgnoreCase(Commands.IA.name())) {
                            String response = AIConnector.getAISnippetsForQuery(msg);
                            sendMessage(getChatBotByName(Commands.IA.name()), user, response);
                        } else {
                            sendMessage(user, getUserByNickname(arg), msg);
                        }
                    }
                } catch (UserNotExistsException e) {
                    sendErrorMsg(user, e.getMessage());
                }
                break;
            case "JOIN":
                try {
                    join(user, getChannelByName(arg));
                    sendOk(user);
                } catch (ChatNotFoundException e) {
                    sendErrorMsg(user, e.getMessage());
                }
                break;
            case "DELETE":
                try {
                    deletePrivChat(user, arg);
                    sendOk(user);
                } catch (UserNotExistsException e) {
                    sendErrorMsg(user, e.getMessage());
                }
                break;

            case "PART":
                try {
                    part(user, arg);
                } catch (ChatNotFoundException e) {
                    sendErrorMsg(user, e.getMessage());
                }
                break;
            case "LU":
                listAllUsers(user);
                break;
            case "LC":
                listChannels(user);
                break;
            case "HELP":
                sendFileContent(senderSocket, "help.txt");
                break;
            case "EXIT":
                try {
                    if (user.isCLIUser()) {
                        exitMessage(user);
                    }
                    senderSocket.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                socketUserMap.remove(senderSocket);
                userOutMap.remove(user);
                break;
            default:
                sendErrorMsg(user, "El comando " + commandName + " no existe.");
                break;
        }

    }

    private void sendOk(User user) {
        sendMessage(user, Commands.OK.toString().toLowerCase());
    }

    private void sendOk(Socket socket) {
        sendMessage(socket, Commands.OK.toString().toLowerCase());
    }

    private void deletePrivChat(User sender, String chatName) throws UserNotExistsException {
        privateChats.remove(getPrivChatByName(sender, chatName));
    }

    private boolean existsUser(String nickname) {
        return historyUsers.contains(getUserByNickname(nickname)) || chatbots.contains(getChatBotByName(nickname));
    }

    private void part(User sender, String channel) throws ChatNotFoundException {
        getChannelByName(channel).getUsers().remove(sender);
    }

    private Channel getChannelByName(String name) throws ChatNotFoundException {
        try {
            return channels.stream().filter(c -> c.getName().equals(name)).toList().get(0);
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new ChatNotFoundException(name);
        }
    }

    private PrivateChat getPrivChatByName(User user1, String user2) throws UserNotExistsException {
        try {
            return privateChats.stream().filter(c -> c.getUser1().equals(user1.getNickname()) && c.getUser2().equals(user2)).toList().get(0);
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new UserNotExistsException(user2);
        }
    }

    private void sendErrorMsg(User user, String errorMessage) {
        send(userOutMap.get(user), Commands.ERROR + ":" + errorMessage);
    }

    private User getClientByName(String nickname) {
        return socketUserMap.values().stream().filter(c -> c.getNickname().equalsIgnoreCase(nickname)).toList().get(0);
    }

    private void sendErrorMsg(Socket socket, String errorMessage) {
        PrintStream out;
        try {
            out = new PrintStream(socket.getOutputStream());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        if (isUIClient(socket)) {
            // gestionar envio de codigo de error
            send(out, Commands.ERROR + ":" + errorMessage);
        } else {
            // hacer que el user cli reciba un mensaje intuitivo
            send(out, Commands.ERROR + ":" + errorMessage);
        }
    }

    private boolean isUIClient(Socket socket) {
        return socketUserMap.get(socket).getClientType().equals(ClientType.UI_CLIENT);
    }

    private Set<User> getUsersInChannel(Channel channel) {
        return channel.getUsers();
    }

    private boolean isMemberOfChannel(Channel channel, User member) {
        return getUsersInChannel(channel).contains(member);
    }

    public void broadcast(User sender, String channelName, String textMessage) {
        Channel channel;
        try {
            channel = getChannelByName(channelName);
        } catch (ChatNotFoundException e) {
            sendErrorMsg(sender, "El canal " + channelName + " no existe. Consulta el comando LU.");
            return;
        }
        if (isMemberOfChannel(channel, sender)) {
            getUsersInChannel(channel).stream()
                    .filter(u -> !u.equals(sender))
                    .forEach(user -> {
                        //#2dam bruno :hola
                        try {
                            sendMsgToChannelMember(sender, user, channel, textMessage);
                        } catch (UserNotConnectedException e) {
                            persistMsg(sender, user.getNickname(), textMessage, channelName);
                        }
                    });
        } else {
            sendErrorMsg(sender, "Para enviar un mensaje a " + channel + ", primero debes unirte.");
        }

    }

    public void sendMsgToChannelMember(User sender, User receptor, Channel channel, String text) throws UserNotConnectedException {
        //Este nunca lanzaría UserNotFoundExc
        try {
            //MESSAGE #2dam bruno :hola
            send(userOutMap.get(receptor), Commands.MESSAGE + " " + channel.getName() + " " + sender + ":" + text);
        } catch (NullPointerException e) {
            throw new UserNotConnectedException(receptor.getNickname());
        }
    }

    public void sendMessage(User sender, User receptor, String text) throws UserNotExistsException {
        if (getOnlineUsers().contains(receptor)) {
            send(userOutMap.get(receptor), Commands.MESSAGE + " " + sender + ":" + text);
        } else if (getOfflineUsers().contains(receptor)) {
            persistMsg(sender, receptor.getNickname(), text, null);
        } else {
            throw new UserNotExistsException(receptor.getNickname());
        }
    }

    private void join(User sender, Channel channel) throws ChatNotFoundException {
        channel.addUser(sender);
    }

    private void createChannel(User user, String channelName) throws ChatRepeatedException {
        Channel channel = new Channel(channelName);
        if (channels.contains(channel)) {
            throw new ChatRepeatedException(channelName);
        } else {
            channel.addUser(user);
            channels.add(channel);
        }
    }

    private void createPrivChat(String user1, String user2) throws UserNotExistsException {
        if (existsUser(user2)) {
            PrivateChat privateChat = new PrivateChat(user1, user2);
            privateChats.add(privateChat);
        } else {
            throw new UserNotExistsException(user2);
        }
    }

    private void createFileIfNotExists(String fileName) {
        Path filePath = Path.of(fileName);
        if (!Files.exists(filePath)) {
            Path folderPath = filePath.getParent();
            try {
                Files.createDirectories(folderPath); // Esto creará la carpeta si no existe
                Files.createFile(filePath);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

    }

    private void persistMsg(User sender, String fileName, String message, String channel) {
        String targetFile = MSGS_FOLDER_NAME + "/" + fileName + "-messages.csv";
        createFileIfNotExists(targetFile);
        try (PrintStream out = new PrintStream(new FileOutputStream(targetFile, true))) {
            if (channel == null) {
                send(out, sender + ":" + message);
            } else {
                send(out, channel + " " + sender + ":" + message);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void sendSavedMessages(User receptor) {
        String fileName = MSGS_FOLDER_NAME + "/" + receptor + "-messages.csv";
        Path path = Path.of(fileName);
        if (Files.exists(path)) {
            try (BufferedReader in = new BufferedReader(new FileReader(fileName))) {
                String message;
                while ((message = in.readLine()) != null) {
                    System.out.println(message);
                    String[] parts = Utils.splitCommandLine(message);
                    String sender;
                    String msg;
                    if (parts[0].startsWith("#")) {
                        // #dam pepe:hola
                        sender = parts[1];
                        msg = parts[2];
                        sendMsgToChannelMember(getUserByNickname(sender), receptor, getChannelByName(parts[0]), msg);
                    } else {
                        // pepe:hola
                        sender = parts[0];
                        msg = parts[1];
                        sendMessage(getUserByNickname(sender), receptor, msg);

                    }
                }
            } catch (UserNotExistsException | IOException | UserNotConnectedException | ChatNotFoundException e) {
                throw new RuntimeException(e);
            }
            try {
                Files.delete(path);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void sendMessage(Socket socket, String message) {
        PrintStream out = null;
        try {
            out = new PrintStream(socket.getOutputStream());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        send(out, message);
    }

    public void sendMessage(User sender, String text) {
        send(userOutMap.get(sender), text);
    }


    private void listChannels(User sender) {
        if (!channels.isEmpty()) {
            channels.forEach(c -> send(userOutMap.get(sender), c.getName()));
        } else {
            send(userOutMap.get(sender), "No existen canales en el servidor.");
        }
    }

    private void listAllUsers(User sender) {
        sendMessage(sender, "Usuarios en linea:");
        listUsers(getOnlineUsers(), sender);
        sendMessage(sender, "Usuarios desconectados:");
        listUsers(getOfflineUsers(), sender);
        sendMessage(sender, "Chatbots disponibles:");
        listUsers(chatbots, sender);
    }

    private void listUsers(Collection<User> users, User sender) {
        if (users.isEmpty()) {
            send(userOutMap.get(sender), "Vacio");
        } else {
            users.forEach(u -> send(userOutMap.get(sender), "- " + u));
        }
    }


    private Collection<User> getOnlineUsers() {
        return socketUserMap.values();
    }

    private List<User> getOfflineUsers() {
        return historyUsers.stream().filter(u -> !getOnlineUsers().contains(u)).toList();
    }

    private void exitMessage(User sender) {
        sendFileContent(userSocketMap.get(sender), "exitMessage.txt");
    }

    private User getChatBotByName(String nickname) {
        return chatbots.stream().filter(c -> c.getNickname().equalsIgnoreCase(nickname)).toList().get(0);
    }

    private void sendFileContent(Socket socket, String fileName) {
        try {
            InputStream inputStream = getClass().getResourceAsStream("/" + fileName);
            BufferedReader in = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = in.readLine()) != null) {
                System.out.println(line);
                sendMessage(socket, line);
            }

            in.close(); // Cierra el BufferedReader cuando termines de leer.
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private User getUserByNickname(String nickname) {
        return userSocketMap.keySet().stream().filter(u -> u.getNickname().equalsIgnoreCase(nickname)).toList().get(0);
    }

    private void send(PrintStream out, String msg) {
        out.print(msg + "\r\n");
    }

    private String getNickname(Socket socket) throws ArrayIndexOutOfBoundsException {
        Scanner input = null;
        try {
            input = new Scanner(socket.getInputStream());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        String line = input.nextLine();
        System.out.println(line);
        String[] commandParts = Utils.splitCommandLine(line);
        return commandParts[1];
    }

    private String getNickname(String command) throws RegisterSyntaxException {
        String[] commandParts = Utils.splitCommandLine(command);
        if (commandParts[0].equalsIgnoreCase(Commands.REGISTER.name())) {
            return commandParts[1];
        } else {
            throw new RegisterSyntaxException(command);
        }
    }

    private String getNicknameFromUICommand(String command) {
        String[] commandParts = Utils.splitCommandLine(command);
        return commandParts[1];
    }

    private void createIA() {
        chatbots.add(new User(Commands.IA.name().toLowerCase(), ClientType.CHATBOT));
    }

    private void closeSocket(Socket socket) {
        try {
            socket.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        Server server;
        if (args.length == 1) {
            server = new Server(args[0]);
        } else {
            server = new Server();
        }
        server.start();
    }
}