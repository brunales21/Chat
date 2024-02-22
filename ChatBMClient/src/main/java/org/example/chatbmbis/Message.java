package org.example.chatbmbis;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalTime;

public class Message implements Serializable {
    private String sender;
    private String targetChannel;
    private String text;
    private final LocalTime time;

    public Message(String sender, String textMessage) {
        this.sender = sender;
        this.text = textMessage;
        this.targetChannel = "";
        time = LocalTime.now();
    }

    public Message(String sender, String targetChannel, String text) {
        this.sender = sender;
        this.targetChannel = targetChannel;
        this.text = text;
        time = LocalTime.now();
    }

    public String getTargetChannel() {
        return targetChannel;
    }

    public String getSender() {
        return sender;
    }

    public String getText() {
        return text;
    }

    public LocalTime getTime() {
        return time;
    }

    @Override
    public String toString() {
        return "Message{" +
                "sender='" + sender + '\'' +
                ", targetChannel='" + targetChannel + '\'' +
                ", text='" + text + '\'' +
                ", time=" + time +
                '}';
    }
}
