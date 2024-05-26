package com.chatapp.daos;

import com.chatapp.model.Message;

import java.util.Map;
import java.util.List;

public interface ChatDAO {
    void saveChatMessages(Map<String, List<Message>> chatMessagesMap);
    Map<String, List<Message>> loadChatMessages();
}
