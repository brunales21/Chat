package org.example.chatbmbis.Utils;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {
    public static String[] splitCommandLine(String input) {
        input = eliminarEspaciosRedundantes(input);
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

    public static String eliminarEspaciosRedundantes(String input) {
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

    public static void main(String[] args) {

        System.out.println(splitCommandLine("privmsg                b    :              hola      q ta    L").length);
    }
}
