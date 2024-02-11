package org.example.chatbmbis;

import java.io.IOException;
import java.net.Socket;

public class Client extends Thread {
    private final Socket socket;

    public Client(String hostname, int port) {
        try {
            socket = new Socket(hostname, port);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Socket getSocket() {
        return socket;
    }
}
