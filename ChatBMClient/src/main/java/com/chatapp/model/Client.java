package com.chatapp.model;

import com.chatapp.constants.ConnectionConfig;

import java.io.IOException;
import java.net.Socket;

public class Client extends Thread {

    private final String hostname;
    private final int port;
    private Socket socket;
    private String serverResponse;

    public Client(String hostname, int port) {
        this.hostname = hostname;
        this.port = port;
    }

    public Client() {
        this(ConnectionConfig.DEFAULT_HOSTNAME, ConnectionConfig.DEFAULT_PORT);
    }

    public void initSocket() throws IOException {
        socket = new Socket(hostname, port);
    }

    public Socket getSocket() {
        return socket;
    }

    public String getServerResponse() {
        return serverResponse;
    }

    public void setServerResponse(String serverResponse) {
        this.serverResponse = serverResponse;
    }
}
