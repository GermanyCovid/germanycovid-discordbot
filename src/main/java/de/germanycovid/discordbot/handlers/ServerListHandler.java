package de.germanycovid.discordbot.handlers;

import com.google.gson.JsonObject;
import de.germanycovid.discordbot.DiscordBot;
import de.germanycovid.discordbot.objects.BotConfig;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Timer;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 *
 * @author Kacper Mura
 * 2020 Copyright (c) by germanycovid.de to present.
 * All rights reserved. https://github.com/VocalZero
 *
 */
public class ServerListHandler {
    
    private final DiscordBot discord;

    public ServerListHandler(DiscordBot discord) {
        this.discord = discord;
        Timer timer = new Timer(3600000, (ActionEvent e) -> {
            sendTopGGUpdate();
            sendDBLUpdate();
            sendDiscordBoatsUpdate();
        });
        timer.setInitialDelay(120000);
        timer.setRepeats(true);
        timer.start();
    }
    
    private void sendTopGGUpdate() {
        BotConfig botConfig = this.discord.getBotConfig();
        if(botConfig.getServerLists() == null || botConfig.getServerLists().getTopggToken() == null
                || botConfig.getServerLists().getTopggToken().isEmpty()) return;
        try {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("server_count", this.discord.getBackendManager().getGuildCount());
            jsonObject.addProperty("shard_count", this.discord.getShardManager().getShardsTotal());

            OkHttpClient client = new OkHttpClient().newBuilder().build();
            MediaType mediaType = MediaType.parse("application/json");
            RequestBody body = RequestBody.create(mediaType, jsonObject.toString());
            Request request = new Request.Builder()
                    .url("https://top.gg/api/bots/836576222750179380/stats")
                    .method("POST", body)
                    .addHeader("Authorization", botConfig.getServerLists().getTopggToken())
                    .addHeader("Content-Type", "application/json")
                    .build();
            Response response = client.newCall(request).execute();
            response.close();
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(ServerListHandler.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ServerListHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void sendDBLUpdate() {
        BotConfig botConfig = this.discord.getBotConfig();
        if(botConfig.getServerLists() == null || botConfig.getServerLists().getDblToken() == null
                || botConfig.getServerLists().getDblToken().isEmpty()) return;
        try {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("guilds", this.discord.getBackendManager().getGuildCount());
            jsonObject.addProperty("users", this.discord.getBackendManager().getUserCount());

            OkHttpClient client = new OkHttpClient().newBuilder().build();
            MediaType mediaType = MediaType.parse("application/json");
            RequestBody body = RequestBody.create(mediaType, jsonObject.toString());
            Request request = new Request.Builder()
                    .url("https://discordbotlist.com/api/v1/bots/836576222750179380/stats")
                    .method("POST", body)
                    .addHeader("Authorization", botConfig.getServerLists().getDblToken())
                    .addHeader("Content-Type", "application/json")
                    .build();
            Response response = client.newCall(request).execute();
            response.close();
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(ServerListHandler.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ServerListHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void sendDiscordBoatsUpdate() {
        BotConfig botConfig = this.discord.getBotConfig();
        if(botConfig.getServerLists() == null || botConfig.getServerLists().getDiscordBoats() == null
                || botConfig.getServerLists().getDiscordBoats().isEmpty()) {
            return;
        }
        try {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("server_count", this.discord.getBackendManager().getGuildCount());
            
            OkHttpClient client = new OkHttpClient().newBuilder().build();
            MediaType mediaType = MediaType.parse("application/json");
            RequestBody body = RequestBody.create(mediaType, jsonObject.toString());
            Request request = new Request.Builder()
                    .url("https://discord.boats/api/bot/836576222750179380")
                    .method("POST", body)
                    .addHeader("Authorization", botConfig.getServerLists().getDiscordBoats())
                    .addHeader("Content-Type", "application/json")
                    .build();
            Response response = client.newCall(request).execute();
            response.close();
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(ServerListHandler.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ServerListHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
