package de.germanycovid.discordbot.managers;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.internal.LinkedTreeMap;
import de.germanycovid.discordbot.DiscordBot;
import de.germanycovid.discordbot.objects.GuildData;
import java.awt.Color;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import lombok.Getter;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;
import net.dv8tion.jda.internal.utils.PermissionUtil;

/**
 *
 * @author Kacper Mura
 * 2020 Copyright (c) by germanycovid.de to present.
 * All rights reserved. https://github.com/VocalZero
 *
 */
public class BackendManager {
    
    private final DiscordBot discord;
    @Getter
    private LoadingCache<String, GuildData> guildCache;
    
    public BackendManager(DiscordBot discord) {
        this.discord = discord;
        initCache();
    }
 
    private void initCache() {
        this.guildCache = CacheBuilder.newBuilder().maximumSize(100L).expireAfterWrite(10L, TimeUnit.MINUTES).build(new CacheLoader<String, GuildData>() {
            @Override
            public GuildData load(String id) throws Exception {
                CompletableFuture<GuildData> completableFuture = new CompletableFuture<>();
                discord.getGuildManager().getGuild(id, result -> {
                    completableFuture.complete(result);
                });
                return completableFuture.get();
            }
        });
    }
    
    public GuildData getGuild(Guild guild) {
        try {
            return this.guildCache.get(guild.getId());
        } catch (ExecutionException ex) {
            return null;
        }
    }
    
    public GuildData getGuild(String id) {
        try {
            return this.guildCache.get(id);
        } catch (ExecutionException ex) {
            return null;
        }
    }
    
    public String getPrefix(Guild guild) {
        return this.getGuild(guild).getPrefix();
    }
    
    public void setPrefix(Guild guild, String prefix) {
        this.getGuild(guild).setPrefix(prefix);
        this.discord.getGuildManager().saveGuild(this.getGuild(guild));
    }
    
    public String getChannelId(Guild guild) {
        return this.getGuild(guild).getChannelId();
    }
    
    public void setChannelId(Guild guild, String channel) {
        this.getGuild(guild).setChannelId(channel);
        this.discord.getGuildManager().saveGuild(this.getGuild(guild));
    }
    
    public int getGuildCount() {
        int count = 0;
        for (JDA shard : this.discord.getShardManager().getShards()) {
            count = count+shard.getGuilds().size();
        }
        return count;
    }
        
    public int getUserCount() {
        int count = 0;
        for (JDA shard : this.discord.getShardManager().getShards()) {
            count = shard.getGuilds().stream().map(guild -> guild.getMemberCount()).reduce(count, Integer::sum);
        }
        return count;
    }
    
    public boolean checkForPermissions(Member member) {
        if(member == null) return false;
        return PermissionUtil.checkPermission(member, Permission.ADMINISTRATOR) || member.getId().equals("223891083724193792") || member.getId().equals("359348375864344576");
    }
    
    public void sendMessage(GuildMessageReceivedEvent event, MessageEmbed messageEmbed) {
        try {
            event.getChannel().sendMessage(messageEmbed).queue();
        } catch(InsufficientPermissionException ex) {
            try {
                event.getChannel().sendMessage("I do not have permissions for **" + ex.getPermission().getName().toLowerCase() + "**, please contact an administrator.").queue();
            } catch (InsufficientPermissionException ex1) {
                try {
                    event.getAuthor().openPrivateChannel().queue((channel) -> {
                        EmbedBuilder embed = new EmbedBuilder();
                        embed.setColor(new Color(235, 52, 94));
                        embed.setDescription("I do not have permissions to **" + ex.getPermission().getName().toLowerCase() + "** in " + event.getChannel().getAsMention() + ", please contact an administrator.");
                        try {
                            channel.sendMessage(embed.build()).queue();
                        } catch (InsufficientPermissionException ex2) {
                        }
                    });
                } catch (InsufficientPermissionException ex2) {
                }
            }
        }
    }
    
    public HashMap<String, LinkedTreeMap<String, Object>> getStates() throws IOException {
        URLConnection url = new URL("https://api.corona-zahlen.org/states").openConnection();
        url.addRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/74.0.3729.169 Safari/537.36");
        url.connect();
        JsonParser jsonParser = new JsonParser();
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader((InputStream) url.getContent(), "UTF-8"));
        JsonElement jsonElement = jsonParser.parse(bufferedReader);
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        HashMap<String, LinkedTreeMap<String, Object>> states = this.discord.getGson().fromJson(jsonObject.get("data"), HashMap.class);
        return states;
    }
    
    public LinkedTreeMap<String, Object> getStateByAbbreviation(String abbreviation) throws IOException {
        HashMap<String, LinkedTreeMap<String, Object>> states = this.getStates();
        Map.Entry<String, LinkedTreeMap<String, Object>> state = states.entrySet().stream().filter(x -> ((String) x.getValue().get("abbreviation")).equalsIgnoreCase(abbreviation)).findFirst().orElse(null);
        if(state == null) return null;
        return state.getValue();
    }
    
    public LinkedTreeMap<String, Object> getStateByName(String name) throws IOException {
        HashMap<String, LinkedTreeMap<String, Object>> states = this.getStates();
        Map.Entry<String, LinkedTreeMap<String, Object>> state = states.entrySet().stream().filter(x -> ((String) x.getValue().get("name")).equalsIgnoreCase(name)).findFirst().orElse(null);
        if(state == null) return null;
        return state.getValue();
    }
    
    public HashMap<String, LinkedTreeMap<String, Object>> getDistricts() throws IOException {
        URLConnection url = new URL("https://api.corona-zahlen.org/districts").openConnection();
        url.addRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/74.0.3729.169 Safari/537.36");
        url.connect();
        JsonParser jsonParser = new JsonParser();
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader((InputStream) url.getContent(), "UTF-8"));
        JsonElement jsonElement = jsonParser.parse(bufferedReader);
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        HashMap<String, LinkedTreeMap<String, Object>> districts = this.discord.getGson().fromJson(jsonObject.get("data"), HashMap.class);
        return districts;
    }
    
    public LinkedTreeMap<String, Object> getDistrictsByName(String name) throws IOException {
        HashMap<String, LinkedTreeMap<String, Object>> districts = this.getDistricts();
        Map.Entry<String, LinkedTreeMap<String, Object>> district = districts.entrySet().stream().filter(x -> ((String) x.getValue().get("name")).equalsIgnoreCase(name)).findFirst().orElse(null);
        if(district == null) return null;
        return district.getValue();
    }
    
    public HashMap<String, LinkedTreeMap<String, Object>> getVaccinations() throws IOException {
        URLConnection url = new URL("https://api.corona-zahlen.org/vaccinations").openConnection();
        url.addRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/74.0.3729.169 Safari/537.36");
        url.connect();
        JsonParser jsonParser = new JsonParser();
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader((InputStream) url.getContent(), "UTF-8"));
        JsonElement jsonElement = jsonParser.parse(bufferedReader);
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        HashMap<String, LinkedTreeMap<String, Object>> vaccinations = this.discord.getGson().fromJson(((JsonObject) jsonObject.get("data")).get("states"), HashMap.class);
        return vaccinations;
    }
    
    public LinkedTreeMap<String, Object> getVaccinationsByName(String name) throws IOException {
        HashMap<String, LinkedTreeMap<String, Object>> vaccinations = this.getVaccinations();
        Map.Entry<String, LinkedTreeMap<String, Object>> vaccination = vaccinations.entrySet().stream().filter(x -> ((String) x.getValue().get("name")).equalsIgnoreCase(name)).findFirst().orElse(null);
        if(vaccination == null) return null;
        return vaccination.getValue();
    }
    
    public LinkedTreeMap<String, Object> getVaccinationsByAbbreviation(String abbreviation) throws IOException {
        HashMap<String, LinkedTreeMap<String, Object>> vaccinations = this.getVaccinations();
        Map.Entry<String, LinkedTreeMap<String, Object>> vaccination = vaccinations.entrySet().stream().filter(x -> x.getKey().equalsIgnoreCase(abbreviation)).findFirst().orElse(null);
        if(vaccination == null) return null;
        return vaccination.getValue();
    }
    
    public boolean isHoliday(Date date) {
        if(date.getDay() == 0 || date.getDay() == 6) return true;
        if(date.getDate() == 1 && date.getMonth() == 0) {
            return true;
        } else if(date.getDate() == 18 && date.getMonth() == 3) {
            return true;
        } else if(date.getDate() == 1 && date.getMonth() == 4) {
            return true;
        } else if(date.getDate() == 13 && date.getMonth() == 4) {
            return true;
        } else if(date.getDate() == 24 && date.getMonth() == 4) {
            return true;
        } else if(date.getDate() == 3 && date.getMonth() == 9) {
            return true;
        } else if(date.getDate() == 31 && date.getMonth() == 9) {
            return true;
        } else if(date.getDate() == 1 && date.getMonth() == 10) {
            return true;
        } else if(date.getDate() == 25 && date.getMonth() == 11) {
            return true;
        } else return date.getDate() == 26 && date.getMonth() == 11;
    }
    
}
