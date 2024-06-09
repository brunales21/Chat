package org.chatapp.IAs;

import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class AIConnector {
    private static final String API_KEY = "2201124e-4fac-41a2-b960-5f3dc78dd70d<__>1PMTegETU8N2v5f4ZESdF2ua";

    public static String getAISnippetsForQuery(String query) {
        HttpClient client = HttpClient.newHttpClient();

        // Codificar la consulta para evitar caracteres ilegales en la URL
        String encodedQuery = null;
        try {
            encodedQuery = URLEncoder.encode(query, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.ydc-index.io/search?query=" + encodedQuery))
                .header("X-API-Key", API_KEY)
                .build();

        HttpResponse<String> response = null;
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        if (response.statusCode() == 200) {
            // Extraer solo el primer resultado si hay hits
            JSONObject jsonResponse = new JSONObject(response.body());
            JSONObject firstHit = jsonResponse.getJSONArray("hits").getJSONObject(0);
            return firstHit.getString("description"); // Obtener solo el mensaje
        } else {
            System.err.println("Error al obtener los snippets: " + response.statusCode());
            return null;
        }
    }
}