package org.example.chatbmbis;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FileChatDAO implements ChatDAO {
    private Path file;

    public FileChatDAO(String fileName) {
        this.file = Path.of(fileName);
        createIfNotExists(file);
    }

    private void createIfNotExists(Path file) {
        if (!Files.exists(file)) {
            try {
                Files.createFile(file);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
    @Override
    public void saveChatMessages(Map<String, List<Message>> chatMessagesMap) {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(String.valueOf(file)))) {
            out.writeObject(chatMessagesMap);
        } catch (IOException e) {
            System.err.println("Error en la escritura.");
        }
    }

    @Override
    public Map<String, List<Message>> loadChatMessages() {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(String.valueOf(file)))) {
            Map<String, List<Message>> map;
            if ((map = (Map<String, List<Message>>) in.readObject()) != null) {
                return map;
            }
        } catch (IOException | ClassNotFoundException e) {
            return new HashMap<>();
        }
        return new HashMap<>();
    }
}
