package com.chatapp.utils;

import java.util.Arrays;

public class Utils {
    public static String[] splitCommandLine(String input) {
        int colonIndex = input.indexOf(":");
        if (colonIndex == -1) {
            return input.split(" ");
        }
        String msg = input.substring(colonIndex + 1);
        String p1 = input.substring(0, colonIndex);
        String[] p1Parts = p1.split(" ");
        String nickname;
        String command;
        if (p1Parts.length == 1) {
            nickname = p1Parts[0];
            return new String[]{nickname, msg};

        } else if (p1Parts.length == 2) {
            command = p1Parts[0];
            nickname = p1Parts[1];
            return new String[]{command, nickname, msg};
        } else {
            command = p1Parts[0];
            String channel = p1Parts[1];
            nickname = p1Parts[2];
            return new String[]{command, channel, nickname, msg};
        }
    }
}
