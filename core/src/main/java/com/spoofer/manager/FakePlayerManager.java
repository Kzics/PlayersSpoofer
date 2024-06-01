package com.spoofer.manager;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.spoofer.PlayersSpoof;
import com.spoofer.obj.IFakeEntity;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static net.minecraft.server.ServerAdvancementManager.GSON;

public class FakePlayerManager {

    private final List<IFakeEntity> fakeEntities;
    private double multiplier;
    private int joinInterval;
    private final PlayersSpoof playersSpoof;

    public FakePlayerManager(PlayersSpoof playersSpoof){
        this.fakeEntities = new ArrayList<>();
        this.playersSpoof = playersSpoof;
    }

    public void setMultiplier(double multiplier) {
        this.multiplier = multiplier;
    }

    public void setJoinInterval(int joinInterval) {
        this.joinInterval = joinInterval;
    }

    public List<String> fetchUsernames() throws Exception {
        final HttpURLConnection conn = getHttpURLConnection();

        StringBuilder response;
        try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8"))) {
            response = new StringBuilder();
            String responseLine;
            while ((responseLine = br.readLine()) != null) {
                response.append(responseLine.trim());
            }
        }

        JsonObject jsonResponse = JsonParser.parseString(response.toString()).getAsJsonObject();
        JsonObject dataObject = jsonResponse.getAsJsonObject("d");
        Type listType = new TypeToken<List<String>>() {}.getType();

        return GSON.fromJson(dataObject.get("Names"), listType);
    }

    private @NotNull HttpURLConnection getHttpURLConnection() throws IOException {
        URL url = new URL("https://www.spinxo.com/services/NameService.asmx/GetNames");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json; utf-8");
        conn.setRequestProperty("Accept", "application/json");
        conn.setDoOutput(true);

        String jsonInputString = "{\"snr\":{\"category\":0,\"UserName\":\"\",\"Hobbies\":\"\",\"ThingsILike\":\"\",\"Numbers\":\"\",\"WhatAreYouLike\":\"\",\"Words\":\"\",\"Stub\":\"username\",\"LanguageCode\":\"fr\",\"NamesLanguageID\":\"45\",\"Rhyming\":false,\"OneWord\":false,\"UseExactWords\":false,\"ScreenNameStyleString\":\"Any\",\"GenderAny\":false,\"GenderMale\":false,\"GenderFemale\":false}}";

        try (OutputStream os = conn.getOutputStream()) {
            byte[] input = jsonInputString.getBytes("utf-8");
            os.write(input, 0, input.length);
        }
        return conn;
    }


    /*public boolean isFakePlayer(Player player){
        for(FakeEntity fakeEntity : fakeEntities){
            if(fakeEntity.getUuid().equals(player.getUniqueId())) return true;
        }
        return false;
    }
    public int getFakePlayerCount() {
        return fakeEntities.size();
    }*/

    public double getMultiplier() {
        return multiplier;
    }

    public int getJoinInterval() {
        return joinInterval;
    }

    public void addFakeEntity(IFakeEntity fakeEntity){
        fakeEntities.add(fakeEntity);
    }

    public void removeFakeEntity(IFakeEntity fakeEntity){
        fakeEntities.remove(fakeEntity);
    }

    public List<IFakeEntity> getFakeEntities(){
        return fakeEntities;
    }
}
