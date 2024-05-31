package org.chatapp.IAs;

import okhttp3.*;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class OpenAIConnector {
    private static final String API_URL = "https://api.openai.com/v1/chat/completions";
    private static final String MODEL = "gpt-3.5-turbo";
    private static final int MAX_TOKENS = 50;
    private static final String API_KEY = "sk-proj-yNWssV7d5JQUngLpC8iNT3BlbkFJV8j41dxhbCNFTWgNV9FG";

    private static final OkHttpClient client = new OkHttpClient();
    private static final MediaType JSON = MediaType.parse("application/json");

    private static List<String> conversationHistory = new ArrayList<>(); // Historial de conversaciones

    public static String getGPTResponse(String message) {
        try {
            // Crear el cuerpo de la solicitud
            String body = buildRequestBody(message);

            // Realizar la solicitud HTTP
            String response = sendRequest(body);

            // Extraer y devolver la respuesta del modelo
            String botMessage = extractContentFromResponse(response);

            // Actualizar el historial de conversaciones
            updateConversationHistory(message, botMessage);

            return botMessage;

        } catch (IOException e) {
            System.out.println("Mensaje de la excepción: " + e.getMessage());
            e.printStackTrace();
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
        conversationHistory.add(response);
    }

    public static String extractContentFromResponse(String response) {
        JSONObject jsonResponse = new JSONObject(response);
        JSONArray choices = jsonResponse.getJSONArray("choices");
        JSONObject firstChoice = choices.getJSONObject(0);
        JSONObject message = firstChoice.getJSONObject("message");
        return message.getString("content").trim();
    }

    public static void main(String[] args) {
        System.out.println(getGPTResponse("Hola, mi nombre es Laura"));
        System.out.println(conversationHistory);
    }
}
