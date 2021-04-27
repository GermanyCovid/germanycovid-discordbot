package de.germanycovid.discordbot;

import com.google.gson.Gson;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.security.auth.login.LoginException;
import de.germanycovid.discordbot.handlers.EventHandler;
import de.germanycovid.discordbot.managers.BackendManager;
import de.germanycovid.discordbot.managers.GuildManager;
import de.germanycovid.discordbot.managers.LoggerManager;
import de.germanycovid.discordbot.managers.MongoManager;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.sharding.DefaultShardManagerBuilder;
import net.dv8tion.jda.api.sharding.ShardManager;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.utils.cache.CacheFlag;

/**
 *
 * @author Kacper Mura
 * 2020 Copyright (c) by germanycovid.de to present.
 * All rights reserved. https://github.com/VocalZero
 *
 */
public class DiscordBot {
    
    private ShardManager shardManager;
    private Gson gson;
    
    private LoggerManager loggerManager;
    private MongoManager mongoManager;
    private GuildManager guildManager;
    private BackendManager backendManager;
    
    public static void main(String[] args) {
        new DiscordBot().init();
    }
    
    private void init() {
        this.gson = new Gson();
        
        this.loggerManager = new LoggerManager();
        this.mongoManager = new MongoManager(this);
        this.guildManager = new GuildManager(this);
        this.backendManager = new BackendManager(this);
        
        DefaultShardManagerBuilder builder = DefaultShardManagerBuilder.createDefault("ODM2NTc2MjIyNzUwMTc5Mzgw.YIgAUg.MCi_jt5urV1mixNgHHd4k4n1IXs");
        builder.setChunkingFilter(ChunkingFilter.NONE);
        builder.enableCache(CacheFlag.MEMBER_OVERRIDES, CacheFlag.EMOTE);
        builder.disableCache(CacheFlag.ACTIVITY, CacheFlag.CLIENT_STATUS, CacheFlag.VOICE_STATE);
        builder.setBulkDeleteSplittingEnabled(true);
        builder.setMemberCachePolicy(MemberCachePolicy.ONLINE.or(MemberCachePolicy.OWNER));
        builder.setLargeThreshold(50);
        builder.setActivity(Activity.watching("germanycovid.de"));
        builder.addEventListeners(new EventHandler(this));
        try {
            builder.setShardsTotal(2);
            builder.setShards(0, 1);
            this.shardManager = builder.build();
        } catch (LoginException ex) {
            Logger.getLogger(DiscordBot.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public Gson getGson() {
        return gson;
    }

    public ShardManager getShardManager() {
        return shardManager;
    }

    public MongoManager getMongoManager() {
        return mongoManager;
    }

    public GuildManager getGuildManager() {
        return guildManager;
    }

    public BackendManager getBackendManager() {
        return backendManager;
    }
    
    public void consoleInfo(String text) {
        this.loggerManager.sendInfo(text);
    }

    public void consoleWarning(String text) {
        this.loggerManager.sendWarning(text);
    }

    public void consoleError(String text) {
        this.loggerManager.sendError(text);
    }
    
}
