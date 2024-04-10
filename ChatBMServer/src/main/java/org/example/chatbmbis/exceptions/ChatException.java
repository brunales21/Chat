package org.example.chatbmbis.exceptions;

public class ChatException extends Exception {
    private static String guiMsg;
    private static String cliMsg;
    public ChatException(String cliMessage, String guiMessage) {
        super(cliMessage);
        cliMsg = cliMessage;
        guiMsg = guiMessage;
    }

    public String getGuiMsg() {
        return guiMsg;
    }

    public String getCliMsg() {
        return cliMsg;
    }
}
