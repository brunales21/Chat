package org.example.chatbmbis.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Utils {
    public static String[] split(String line) {
        String[] split = line.split(" ");
        int colonIndex = line.indexOf(":");
        if (split.length == 1 || colonIndex == -1) {
            return split;
        }

        String textMessage = line.substring(colonIndex + 1); // +1 para excluir el ':'
        String prefix = line.substring(0, colonIndex);

        List<String> partsList = new ArrayList<>(Arrays.asList(prefix.split(" ")));
        partsList.add(textMessage);

        return partsList.toArray(new String[0]);
    }
}
