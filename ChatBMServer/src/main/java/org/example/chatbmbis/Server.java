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
    private final Map<String, PrintStream> userOutMap;
    private final Map<Socket, String> socketUserMap;
    private final Set<String> historyUsers;
    private final String MSGS_FOLDER_NAME = "messages";

    public Server(int port) {
        this.port = port;
        channels = new HashSet<>();
        privateChats = new HashSet<>();
        userOutMap = new HashMap<>();
        socketUserMap = new HashMap<>();
        historyUsers = new HashSet<>();
    }

    public Server(String port) {
        this(Integer.parseInt(port));
    }

    public Server() {
        this(8080);
    }

    private void register(String nickname, Socket socket) throws UserExistsException {
        try {
            if (userOutMap.containsKey(nickname)) {
                throw new UserExistsException(nickname);
            }
            userOutMap.put(nickname, new PrintStream(socket.getOutputStream()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        historyUsers.add(nickname);
        socketUserMap.put(socket, nickname);
        sendOk(socket);
        sendSavedMessages(nickname);
    }

    public void start() {
        ServerSocket serverSocket;
        try {
            serverSocket = new ServerSocket(port);
            System.out.println("El servidor se ha iniciado correctamente.");
            System.out.println("Está escuchando en el puerto: " + port + ".");

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
            Thread thread = new Thread(() -> clientHandler(socket));
            thread.start();
        }
    }

    private void clientHandler(Socket socket) {
        try {
            sendFileContent(socket, "welcome.txt");
            Scanner in = new Scanner(socket.getInputStream());
            String command;
            do {
                sendMessage(socket, "Primero debe registrarse. Ej: "+Commands.REGISTER+" mi_username");
                command = BackspaceRemover.removeBackspaces(in.nextLine());
            } while (!Utils.splitCommandLine(command)[0].equalsIgnoreCase(Commands.REGISTER.toString()));
            do {
                processCommand(socket, command);
                command = BackspaceRemover.removeBackspaces(in.nextLine());
            } while (true);
        } catch (IOException | NoSuchElementException ignore) {

        }
    }

    private void processCommand(Socket senderSocket, String command) {
        System.out.println("Header que recibe servidor: " + command);
        String[] commandParts = Utils.splitCommandLine(command);
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
                        if (arg.equals("IA")) {
                            String response = AIConnector.getAISnippetsForQuery(msg);
                            System.out.println(response);
                            sendMessage("IA", sender, response);
                        } else {
                            //PRIVMSG monica :hola
                            sendMessage(sender, arg, msg);
                        }
                    }
                } catch (UserNotExistsException e) {
                    sendErrorMsg(sender, e.getMessage());
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
                sendFileContent(senderSocket, "help.txt");
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
        sendMessage(sender, Commands.OK.toString().toLowerCase());
    }

    private void sendOk(Socket socket) {
        sendMessage(socket, Commands.OK.toString().toLowerCase());
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
        send(userOutMap.get(sender), Commands.ERROR + ":" + errorMessage);
    }

    private void sendErrorMsg(Socket socket, String errorMessage) {
        // le rebota el msj
        PrintStream out;
        try {
            out = new PrintStream(socket.getOutputStream());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        send(out, Commands.ERROR + ":" + errorMessage);
    }

    private Set<String> getUsersInChannel(String channelName) {
        try {
            return getChannelByName(channelName).getUsers();
        } catch (ChatNotFoundException e) {
            return new HashSet<>();
        }
    }

    private boolean isMemberOfChannel(String channel, String member) {
        return getUsersInChannel(channel).contains(member);
    }

    public void broadcast(String sender, String channel, String textMessage) {
        if (isMemberOfChannel(channel, sender)) {
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
        } else {
            sendErrorMsg(sender, "Para enviar un mensaje a " + channel + ", primero debes unirte.");
        }

    }

    public void sendMsgToChannelMember(String sender, String receptor, String channel, String text) throws UserNotConnectedException {
        //Este nunca lanzaría UserNotFoundExc
        try {
            //MESSAGGE #2dam bruno :hola
            send(userOutMap.get(receptor), Commands.MESSAGE + " " + channel + " " + sender + ":" + text);
        } catch (NullPointerException e) {
            throw new UserNotConnectedException(receptor);
        }
    }

    public void sendMessage(String sender, String receptor, String text) throws UserNotExistsException {
        try {
            send(userOutMap.get(receptor), Commands.MESSAGE + " " + sender + ":" + text);
        } catch (NullPointerException e) {
            if (!historyUsers.contains(receptor)) {
                throw new UserNotExistsException(receptor);
            }
            persistMsg(sender, receptor, text, null);
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

    private void persistMsg(String sender, String fileName, String message, String channel) {
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

    private void sendSavedMessages(String receptor) {
        String fileName = MSGS_FOLDER_NAME + "/" + receptor + "-messages.csv";
        Path path = Path.of(fileName);
        if (Files.exists(path)) {
            try (BufferedReader in = new BufferedReader(new FileReader(fileName))) {
                String message;
                while ((message = in.readLine()) != null) {
                    String[] parts = Utils.splitCommandLine(message);
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

    public void sendMessage(Socket socket, String message) {
        PrintStream out = null;
        try {
            out = new PrintStream(socket.getOutputStream());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        send(out, message);
    }

    public void sendMessage(String sender, String text) {
        send(userOutMap.get(sender), text);
    }


    private void listChannels(String sender) {
        if (!channels.isEmpty()) {
            channels.forEach(c -> send(userOutMap.get(sender), c.getName()));
        } else {
            send(userOutMap.get(sender), "No existen canales en el servidor.");
        }
    }

    private void listAllUsers(String sender) {
        sendMessage(sender, "Usuarios en linea:");
        listUsers(getOnlineUsers(), sender);
        sendMessage(sender, "Usuarios desconectados:");
        listUsers(getOfflineUsers(), sender);
    }

    private void listUsers(Collection<String> users, String sender) {
        users.forEach(u -> send(userOutMap.get(sender), "- " + u));
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

    private void send(PrintStream out, String msg) {
        out.print(msg+"\r\n");
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