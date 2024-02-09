package org.example.chatbmbis;

public class UserNotFoundException extends Exception {
    public UserNotFoundException(String nickname) {
        super("User " + nickname + " not found.");
    }
}
