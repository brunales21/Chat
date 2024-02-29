package org.example.chatbmbis;

import org.example.chatbmbis.exceptions.*;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class Server {

    private ServerSocket serverSocket;
    private int port;
    private List<PrivateChat> privateChats;
    private List<Channel> channels;
    private Map<String, PrintStream> userOutMap;
    private Map<Socket, String> socketUserMap;
    private Set<String> historyUsers;


    public Server(int port) {
        this.port = port;
        channels = new ArrayList<>();
        privateChats = new ArrayList<>();
        userOutMap = new HashMap<>();
        socketUserMap = new HashMap<>();
        historyUsers = new HashSet<>();
    }

    private void register(String nickname, Socket socket) {
        try {
            userOutMap.put(nickname, new PrintStream(socket.getOutputStream()));
            historyUsers.add(nickname);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        socketUserMap.put(socket, nickname);
        sendMessagesFromFile(nickname);
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
                    processCommand(socket, BackspaceRemover.removeBackspaces(header));
                }
            }
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    private void processCommand(Socket socket, String header) {
        System.out.println("Header que recibe servidor: " + header);
        String[] headerParts = splitParts(header);
        String sender = socketUserMap.get(socket);
        String command = headerParts[0].toUpperCase();
        String arg = "";
        if (headerParts.length > 1) {
            arg = headerParts[1];
        }
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
                    broadcast(sender, arg, headerParts[2]);
                } else {
                    try {
                        sendMessage(sender, arg, headerParts[2]);
                    } catch (UserNotExistsException e) {
                        sendErrorMsg(sender, e.getMessage());
                    } catch (UserNotConnectedException e) {
                        persistMsg(sender, header, arg);
                    }
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
                } catch (ChatNotFoundException e) {
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
            case "EXIT":
                try {
                    exitMessage(sender);
                    socket.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                socketUserMap.remove(socket);
                userOutMap.remove(sender);
                break;
            case "LU":
                listUsers(sender);
                break;
            case "LC":
                listChannels(sender);
                break;
            case "HELP":
                showFileContent(socket, "help.txt");
                break;
            default:
                sendErrorMsg(sender, "El comando " + command + " no existe.");
                break;
        }

    }

    private void sendOk(String sender) {
        sendMessage(sender, "ok");
    }

    private void deletePrivChat(String sender, String chatName) throws ChatNotFoundException {
        privateChats.remove(getPrivChatByName(sender, chatName));
    }

    private boolean existsUser(String nickname) {
        return userOutMap.get(nickname) != null;
    }

    private void part(String sender, String channel) throws ChatNotFoundException {
        getChannelByName(channel).getUsers().remove(sender);
    }

    private Channel getChannelByName(String name) throws ChatNotFoundException {
        try {
            Channel channel = channels.stream().filter(c -> c.getName().equals(name)).toList().get(0);
            return channel;
        } catch (ArrayIndexOutOfBoundsException e) {
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

    public void broadcast(String sender, String channel, String textMessage) {
        getUsersInChannel(channel).stream()
                .filter(u -> !u.equals(sender))
                .forEach(user -> {
                    //#2dam bruno :hola
                    sendMsgToChannelMember(sender, user, channel, textMessage);
                });
    }

    public void sendMsgToChannelMember(String sender, String receptor, String channel, String text) {
        //Este nunca lanzaría UserNotFoundExc
        //#2dam bruno :hola
        try {
            userOutMap.get(receptor).println("MESSAGE " + channel + " " + sender + " :" + text);
        } catch (NullPointerException e) {
            // guardarle el mensaje
        }
    }

    public void sendMessage(String sender, String receptor, String text) throws UserNotExistsException, UserNotConnectedException {
        try {
            System.out.println("msj que mando: MESSAGE " + sender + " :" + text);
            userOutMap.get(receptor).println("MESSAGE " + sender + " :" + text);
            System.out.println("SE MANDA MSJ DE FILE..");
        } catch (NullPointerException e) {
            if (historyUsers.contains(receptor)) {
                throw new UserNotConnectedException();
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

    private void createPrivChat(String user1, String user2) throws ChatRepeatedException, ChatNotFoundException {
        if (existsUser(user2)) {
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

    private void persistMsg(String sender, String message, String fileName) {
        String targetFile = fileName + "-messages.csv";
        createFileIfNotExists(targetFile);
        try (PrintStream out = new PrintStream(new FileOutputStream(targetFile, true))) {
            out.println(sender + " " + message);
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
                    // monica PRIVMSG bruno :hola
                    String[] parts = splitParts(message);
                    String sender = parts[0];
                    String target = parts[2];
                    String msg = parts[3];
                    if (target.equals(receptor)) {
                        sendMessage(sender, target, msg);
                    }
                    Thread.sleep(300);
                }
            } catch (UserNotExistsException | IOException | UserNotConnectedException e) {
                throw new RuntimeException(e);
            } catch (InterruptedException e) {
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
        channels.forEach(c -> userOutMap.get(sender).println(c.getName()));
    }

    private void listUsers(String sender) {
        socketUserMap.values().forEach(u -> userOutMap.get(sender).println("- " + u));
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