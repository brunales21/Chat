package com.chatapp.model;

import java.io.Serializable;
import java.time.LocalTime;

public class Message implements Serializable {

    private final LocalTime time;
    private final String sender;
    private final String targetChannel;
    private final String text;

    public Message(String sender, String textMessage) {
        this.sender = sender.toLowerCase();
        this.text = textMessage;
        this.targetChannel = "";
        time = LocalTime.now();
    }

    public Message(String sender, String targetChannel, String text) {
        this.sender = sender;
        this.targetChannel = targetChannel.toLowerCase();
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
        return "Message{" + "sender='" + sender + '\'' + ", targetChannel='" + targetChannel + '\'' + ", text='" + text + '\'' + ", time=" + time + '}';
    }
}
