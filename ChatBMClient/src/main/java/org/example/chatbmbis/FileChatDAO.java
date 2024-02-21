package org.example.chatbmbis;

import java.io.*;
import java.util.List;
import java.util.Map;

public class FileChatDAO implements ChatDAO {
    private String fileName;

    public FileChatDAO(String fileName) {
        this.fileName = fileName;
    }

    @Override
    public void saveChatMessages(Map<String, List<Message>> chatMessagesMap) {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(fileName))) {
            out.writeObject(chatMessagesMap);
        } catch (IOException e) {
            System.err.println("Error en la escritura.");
        }
    }

    @Override
    public Map<String, List<Message>> loadChatMessages() {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(fileName))) {
            return (Map<String, List<Message>>) in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
