package org.example.chatbmbis;

public class BackspaceRemover {

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



    public static void main(String[] args) {
        String input = "regissssss\b\b\b\b\bter bruno";
        String result = removeBackspaces(input);

        System.out.println("Original: " + input);
        System.out.println("Procesado: " + result);
    }
}
