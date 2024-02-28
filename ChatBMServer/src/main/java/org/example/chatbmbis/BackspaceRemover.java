package org.example.chatbmbis;

public class BackspaceRemover {

    public static String removeBackspaces(String input) {
        StringBuilder result = new StringBuilder();
        int backspaceCount = 0;

        for (char c : input.toCharArray()) {
            if (c == '\b') {
                // Si es un retroceso, incrementa el contador de retrocesos
                backspaceCount++;
            } else {
                // Si no es un retroceso, agrega caracteres y aplica retrocesos pendientes
                if (backspaceCount > 0) {
                    result.delete(result.length() - backspaceCount, result.length());
                    backspaceCount = 0;
                }
                result.append(c);
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
