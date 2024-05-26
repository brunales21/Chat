package org.chatapp.IAs;

import okhttp3.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class OpenAIConnector {
    private static final String API_URL = "https://api.openai.com/v1/chat/completions";
    private static final String MODEL = "gpt-3.5-turbo";
    private static final int MAX_TOKENS = 50;
    private static final String API_KEY = "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";

    private static final OkHttpClient client = new OkHttpClient();
    private static final MediaType JSON = MediaType.parse("application/json");

    private static List<String> conversationHistory = new ArrayList<>(); // Historial de conversaciones

    public static String getGPTResponse(String message) {
        try {
            // Crear el cuerpo de la solicitud
            String body = buildRequestBody(message);

            // Realizar la solicitud HTTP
            String response = sendRequest(body);

            // Actualizar el historial de conversaciones
            updateConversationHistory(message, response);

            // Extraer y devolver la respuesta del modelo
            return extractContentFromResponse(response);

        } catch (IOException e) {
            System.out.println("Mensaje de la excepcion: "+e.getMessage());
            System.out.println(e.getCause());
        }
        return null;
    }

    private static String buildRequestBody(String message) {
        StringBuilder requestBodyBuilder = new StringBuilder();
        requestBodyBuilder.append("{\"model\": \"").append(MODEL).append("\", \"messages\": [");

        // Agregar mensajes anteriores al cuerpo de la solicitud
        for (String previousMessage : conversationHistory) {
            requestBodyBuilder.append("{\"role\": \"user\", \"content\": \"").append(previousMessage).append("\"},");
        }

        // Agregar el nuevo mensaje al cuerpo de la solicitud
        requestBodyBuilder.append("{\"role\": \"user\", \"content\": \"").append(message).append("\"}");

        requestBodyBuilder.append("]}");
        return requestBodyBuilder.toString();
    }

    private static String sendRequest(String body) throws IOException {
        // Crear la solicitud HTTP
        Request request = new Request.Builder()
                .url(API_URL)
                .addHeader("Authorization", "Bearer " + API_KEY)
                .addHeader("Content-Type", "application/json")
                .post(RequestBody.create(body, JSON))
                .build();

        // Enviar la solicitud y obtener la respuesta
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected response code: " + response);
            }
            return response.body().string();
        }
    }

    private static void updateConversationHistory(String message, String response) {
        // Agregar el mensaje y la respuesta al historial de conversaciones
        conversationHistory.add(message);
        conversationHistory.add(extractContentFromResponse(response));
    }

    public static String extractContentFromResponse(String response) {
        int startMarker = response.indexOf("content") + 11; // Marcador para el inicio del contenido
        int endMarker = response.indexOf("}", startMarker); // Marcador para el final del contenido
        System.out.println(response);
        return response.substring(startMarker, endMarker-1); // Devolver el contenido de la respuesta
    }

    public static void main(String[] args) {
        System.out.println(getGPTResponse("Hola, mi nombre es laura"));


        System.out.println(conversationHistory);
    }
}
