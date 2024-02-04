package org.example.chatbmbis;

import java.io.IOException;
import java.net.Socket;

public class Client extends Thread {
    private String hostname;
    private int port;
    protected Socket socket;

    public Client(String hostname, int port) {
        this.hostname = hostname;
        this.port = port;

    }
    public Client() {

    }
    public void connect() {
        try {
            socket = new Socket(hostname, port);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public String getHostname() {
        return hostname;
    }

    public int getPort() {
        return port;
    }

    public Socket getSocket() {
        return socket;
    }

    public static void main(String[] args) {
        Client c = new Client();
        c.connect();
    }
}
