package de.germanycovid.discordbot;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.security.auth.login.LoginException;
import de.germanycovid.discordbot.handlers.EventHandler;
import de.germanycovid.discordbot.handlers.ServerListHandler;
import de.germanycovid.discordbot.managers.*;
import de.germanycovid.discordbot.objects.BotConfig;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import lombok.Getter;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.sharding.DefaultShardManagerBuilder;
import net.dv8tion.jda.api.sharding.ShardManager;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 *
 * @author Kacper Mura
 * 2020 Copyright (c) by germanycovid.de to present.
 * All rights reserved. https://github.com/VocalZero
 *
 */
@Getter
public class DiscordBot {
    
    private ShardManager shardManager;
    private Gson gson;
    private long startTimeMillis;

    private LoggerManager loggerManager;
    private ConfigManager configManager;
    private MongoManager mongoManager;
    private GuildManager guildManager;
    private BackendManager backendManager;
    private ServerListHandler serverListHandler;
    
    public static void main(String[] args) {
        new DiscordBot().init();
    }
    
    private void init() {
        this.gson = new GsonBuilder().setPrettyPrinting().create();
        this.startTimeMillis = System.currentTimeMillis();
        this.loggerManager = new LoggerManager();

        this.configManager = new ConfigManager(this);
        this.mongoManager = new MongoManager(this);
        this.guildManager = new GuildManager(this);
        this.backendManager = new BackendManager(this);
        
        DefaultShardManagerBuilder builder = DefaultShardManagerBuilder.createDefault(this.getConfig().getToken());
        builder.setChunkingFilter(ChunkingFilter.NONE);
        builder.enableCache(CacheFlag.MEMBER_OVERRIDES);
        builder.disableCache(CacheFlag.ACTIVITY, CacheFlag.CLIENT_STATUS, CacheFlag.VOICE_STATE, CacheFlag.EMOTE);
        builder.setBulkDeleteSplittingEnabled(true);
        builder.setMemberCachePolicy(MemberCachePolicy.ONLINE.or(MemberCachePolicy.OWNER));
        builder.setLargeThreshold(50);
        builder.setActivity(Activity.watching("germanycovid.de"));
        builder.addEventListeners(new EventHandler(this));
        try {
            builder.setShardsTotal(3);
            builder.setShards(0, 2);
            this.shardManager = builder.build();
        } catch (LoginException ex) {
            Logger.getLogger(DiscordBot.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        this.serverListHandler = new ServerListHandler(this);
    }

    public BotConfig getConfig() {
        return this.configManager.getConfig();
    }
    
    public void consoleInfo(String text) {
        this.loggerManager.sendInfo(text);
    }

    public void consoleError(String text) {
        this.loggerManager.sendError(text);
    }
    
}
