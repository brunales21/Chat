package com.chatapp.dao;

import com.chatapp.conversation.Message;

import java.util.Map;
import java.util.List;

public interface ChatDAO {
    void saveChatMessages(Map<String, List<Message>> chatMessagesMap);
    Map<String, List<Message>> loadChatMessages();
}
