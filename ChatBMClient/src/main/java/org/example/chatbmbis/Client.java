package org.example.chatbmbis;

import java.io.IOException;
import java.net.Socket;

public class Client extends Thread {
    private Socket socket;

    public Client(String hostname, int port) throws IOException {
        socket = new Socket(hostname, port);
    }

    public Socket getSocket() {
        return socket;
    }
}
