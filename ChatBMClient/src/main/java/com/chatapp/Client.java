package com.chatapp;

import java.io.IOException;
import java.net.Socket;

public class Client extends Thread {
    private Socket socket;
    private final String hostname;
    private final int port;
    protected static final String DEFAULT_HOSTNAME = "brunales.com";
    protected static final int DEFAULT_PORT = 8080;
    private String serverResponse;

    public Client(String hostname, int port) {
        this.hostname = hostname;
        this.port = port;
    }

    public Client() {
        this(DEFAULT_HOSTNAME, DEFAULT_PORT);
    }

    public void connect() throws IOException {
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
