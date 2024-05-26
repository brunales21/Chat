package com.chatapp.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SyntaxUtils {

    public static String DELIMITER = ":";

    public static String[] splitCommandLine(String input) {
        input = deleteRedundantSpaces(input);
        int colonIndex = input.indexOf(SyntaxUtils.DELIMITER);
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

    public static String deleteRedundantSpaces(String input) {
        // Patrón para encontrar uno o más espacios consecutivos
        Pattern pattern = Pattern.compile("\\s+");

        // Matcher para encontrar coincidencias con el patrón en la cadena de entrada
        Matcher matcher = pattern.matcher(input);

        // Reemplazar los espacios consecutivos por un solo espacio
        String resultado = matcher.replaceAll(" ");

        // Eliminar espacios al principio y al final de la cadena resultante
        resultado = resultado.trim();

        return resultado;
    }
}
