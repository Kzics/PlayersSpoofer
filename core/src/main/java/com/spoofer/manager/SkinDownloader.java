package com.spoofer.manager;

import com.google.gson.*;
import com.mojang.authlib.properties.Property;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class SkinDownloader {

    private static final String API_URL = "https://api.mineskin.org/get/list/start?size=600";
    private static final String UUID_API_URL = "https://api.mineskin.org/get/uuid/";
    private static final int CACHE_SIZE = 100;
    private static final Queue<String> uuidCache = new LinkedList<>();
    private static final Random random = new Random();

    public static String fetchRandomUUID() throws Exception {
        if (uuidCache.isEmpty()) {
            fillCache();
        }
        return uuidCache.poll();
    }

    public static Property fetchRandomTexture() throws Exception {
        String uuid = fetchRandomUUID();
        return fetchTextureFromUUID(uuid);
    }

    private static Property fetchTextureFromUUID(String uuid) throws Exception {
        final HttpURLConnection conn = getHttpURLConnection(UUID_API_URL + uuid);

        StringBuilder response;
        try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
            response = new StringBuilder();
            String responseLine;
            while ((responseLine = br.readLine()) != null) {
                response.append(responseLine.trim());
            }
        }

        JsonObject jsonResponse = JsonParser.parseString(response.toString()).getAsJsonObject();
        JsonObject dataObject = jsonResponse.getAsJsonObject("data");
        JsonObject textureObject = dataObject.getAsJsonObject("texture");

        String value = textureObject.get("value").getAsString();
        String signature = textureObject.get("signature").getAsString();

        return new Property("textures", value, signature);
    }

    private static void fillCache() throws Exception {
        final HttpURLConnection conn = getHttpURLConnection(API_URL);

        StringBuilder response;
        try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
            response = new StringBuilder();
            String responseLine;
            while ((responseLine = br.readLine()) != null) {
                response.append(responseLine.trim());
            }
        }

        JsonObject jsonResponse = JsonParser.parseString(response.toString()).getAsJsonObject();
        JsonArray skinsArray = jsonResponse.getAsJsonArray("skins");
        List<String> uuids = new ArrayList<>();
        for (JsonElement element : skinsArray) {
            JsonObject skinObject = element.getAsJsonObject();
            uuids.add(skinObject.get("uuid").getAsString());
        }

        Collections.shuffle(uuids, random);
        uuidCache.addAll(uuids);
        while (uuidCache.size() > CACHE_SIZE) {
            uuidCache.poll();
        }
    }

    private static HttpURLConnection getHttpURLConnection(String urlString) throws Exception {
        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Accept", "application/json");
        return conn;
    }
}
