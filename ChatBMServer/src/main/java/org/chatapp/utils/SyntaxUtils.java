package org.chatapp.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SyntaxUtils {

    public static String DELIMITER = ":";

    public static String removeBackspaces(String input) {
        StringBuilder result = new StringBuilder();

        if (!input.contains("\b")) {
            return input;
        }

        int backspaceCount = 0;
        int normalCharCount = 0;

        // Verifica y omite retrocesos consecutivos al principio de la cadena
        while (input.startsWith("\b")) {
            input = input.substring(1);
        }

        for (char c : input.toCharArray()) {
            if (c == '\b') {
                // Si es un retroceso, incrementa el contador de retrocesos
                backspaceCount++;
            } else {
                // Si no es un retroceso, agrega caracteres y aplica retrocesos pendientes
                if (backspaceCount > 0) {
                    // Verifica si hay más retrocesos que caracteres normales
                    if (normalCharCount < backspaceCount) {
                        // Deja de contar retrocesos y restablece el contador
                        result = new StringBuilder();
                    } else {
                        result.delete(result.length() - backspaceCount, result.length());
                    }
                    backspaceCount = 0;
                    normalCharCount = 0;
                }
                result.append(c);
                normalCharCount++;
            }
        }

        return result.toString();
    }

    public static String[] splitCommandLine(String input) {
        input = deleteRedundantSpaces(input);
        int colonIndex = input.indexOf(DELIMITER);
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
