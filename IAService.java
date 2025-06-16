package com.akihabara.market.service;

import java.util.Properties;
import java.io.FileInputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import com.google.gson.*;


public class IAService {
	public String sugerirNombreProducto(String categoria, String franquicia) {
        try {
        	Properties props = new Properties();
    		props.load(new FileInputStream("config.properties"));
            String apiKey = props.getProperty("OPENROUTER_API_KEY");
            
            String prompt = "Dame un nombre creativo para un producto de categoria " + categoria + " y de la franquicia " + franquicia + ". SOLO DAME EL NOMBRE, NADA MAS";
            
            HttpClient client = HttpClient.newHttpClient();


            JsonObject message = new JsonObject();
            message.addProperty("role", "user");
            message.addProperty("content", prompt);


            JsonArray messages = new JsonArray();
            messages.add(message);


            JsonObject body = new JsonObject();
            body.addProperty("model", "mistralai/mistral-7b-instruct:free");
            body.add("messages", messages);


            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI("https://openrouter.ai/api/v1/chat/completions"))
                    .header("Authorization", "Bearer " + apiKey)
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(body.toString()))
                    .build();


            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());


            // Extraer el texto generado
            JsonObject json = JsonParser.parseString(response.body()).getAsJsonObject();
            String resultado = json
                    .getAsJsonArray("choices")
                    .get(0)
                    .getAsJsonObject()
                    .getAsJsonObject("message")
                    .get("content")
                    .getAsString();


            return resultado;

        } catch (Exception e) {
            System.out.println("Error al comunicar con OpenRouter: " + e.getMessage());
            return null;
        }
	}
}
