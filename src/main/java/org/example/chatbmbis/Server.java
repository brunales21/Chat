package org.example.chatbmbis;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

public class Server {

    private  ServerSocket serverSocket;
    private int port;

    private static User userNow;
    private Map<Socket, String> usersMap;


    public Server() {
       // this.privateChats = new ArrayList<>();
        //this.channels = new ArrayList<>();
        this.usersMap = new HashMap<>();

    }

    public static  void  setUserNow(User user){
        userNow=user;
    }


    public void iniciar() throws IOException {
        this.serverSocket = new ServerSocket(80);
        while (true) {
            Socket socket1 = serverSocket.accept();
            Scanner in = new Scanner(socket1.getInputStream());
            usersMap.put(socket1, in.nextLine());
            new Thread(() -> clientHandler(socket1)).start();
        }
    }

    private void clientHandler(Socket socket){
        Scanner in = null;
        try {
            in = new Scanner(socket.getInputStream());
            String commandLine;
            while (!socket.isClosed()) {
                if (in.hasNextLine()) {
                    commandLine = in.nextLine();
                    System.out.println(commandLine);
                    processCommand(commandLine);
                }
            }
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }
    private void processCommand(String commandLine) {
        String[] parts = splitParts(commandLine);
        switch (parts[0]) {
            case "CONNECT":
                //login(parts[2], socket);
                break;
            case "CREATE":
                //create(socketUserMap.get(socket).getNickname(), parts[1]);
                break;
            case "PRIVMSG":
                sendMessage(getSocketByUserNickname(parts[1]),parts[2]);
                break;
            case "JOIN":
                //join();
                break;
            case "LU":
                //listUsers(socket);
            case "LC":
                //listChannels(socket);
        }
    }
    public static String[] splitParts(String header) {
        String[] split = header.split(" ");
        if (split.length == 1) {
            return split;
        }
        int colonIndex = header.indexOf(":");
        String text = header.substring(colonIndex + 1); // +1 para excluir el ':'
        String prefix = header.substring(0, colonIndex);

        List<String> partsList = new ArrayList<>(Arrays.asList(prefix.split(" ")));
        partsList.add(text);

        return partsList.toArray(new String[0]);
    }
    public Socket getSocketByUserNickname(String nickname){
       Map.Entry<Socket,String> userEntry = null;
        for (Map.Entry<Socket, String> socketUserEntry : usersMap.entrySet()) {

            String user =socketUserEntry.getValue();

            if (user.equals(nickname)){
                userEntry=socketUserEntry;
                break;
            }
        }
        return userEntry.getKey();
    }
    public void sendMessage(Socket socket,String text){
        PrintStream out = null;
        try {
            out = new PrintStream(socket.getOutputStream());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        out.println(text);

    }

    public static void main(String[] args) {
        Server server = new Server();
        try {
            server.iniciar();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }



/*

    public void createChannel(ChatRoom channel, User user) {

        String id =channel.getId().substring(0);
        if (id.equals("#")){
            if (!channels.contains(channel)){
                channels.add((Channel) channel);
                if (!user.getChatRooms().contains(channel)) {
                    user.addChatRoom(channel);
                    channel.addUser(user);
                }
            }

        }else {
            //Si la lista contiene el chat
            if (!privateChats.contains(channel)){
                privateChats.add((PrivateChat) channel);

                //Si el usuario contiene el chat
                if (!user.getChatRooms().contains(channel)){
                    user.getChatRooms().add(channel);
                    channel.addUser(user);
                }
            }
        }

    }

    public void createPrivateChat(Channel channel, User user) {
        if (!user.getChatRooms().contains(channel)) {
            user.addChatRoom(channel);
            channel.addUser(user);
        }
        if (!channels.contains(channel)){
            channels.add((Channel) channel);
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

    public Map<Socket, User> getUsersMap() {
        return usersMap;
    }

    public void setUsersMap(Map<Socket, User> usersMap) {
        this.usersMap = usersMap;
    }

    public List<Channel> getChannels() {
        return channels;
    }

    public void setChannels(List<Channel> channels) {
        this.channels = channels;
    }

    public List<PrivateChat> getPrivateChats() {
        return privateChats;
    }

    public void setPrivateChats(List<PrivateChat> privateChats) {
        this.privateChats = privateChats;
    }

 */
}