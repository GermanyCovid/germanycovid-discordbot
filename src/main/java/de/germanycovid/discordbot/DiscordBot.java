package de.germanycovid.discordbot;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.security.auth.login.LoginException;
import de.germanycovid.discordbot.handlers.EventHandler;
import de.germanycovid.discordbot.managers.BackendManager;
import de.germanycovid.discordbot.managers.GuildManager;
import de.germanycovid.discordbot.managers.LoggerManager;
import de.germanycovid.discordbot.managers.MongoManager;
import de.germanycovid.discordbot.objects.BotConfig;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
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
public class DiscordBot {
    
    private ShardManager shardManager;
    private Gson gson;
    private long startTimeMillis;
    private BotConfig botConfig;
    
    private LoggerManager loggerManager;
    private MongoManager mongoManager;
    private GuildManager guildManager;
    private BackendManager backendManager;
    
    public static void main(String[] args) {
        new DiscordBot().init();
    }
    
    private void init() {
        this.gson = new GsonBuilder().setPrettyPrinting().create();
        this.startTimeMillis = System.currentTimeMillis();
        this.loggerManager = new LoggerManager();
        
        loadConfig();
        
        this.mongoManager = new MongoManager(this);
        this.guildManager = new GuildManager(this);
        this.backendManager = new BackendManager(this);
        
        DefaultShardManagerBuilder builder = DefaultShardManagerBuilder.createDefault(this.botConfig.getToken());
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
    }
    
    private void loadConfig() {
        JSONParser jsonParser = new JSONParser();
        try {
            JSONObject jsonObject = (JSONObject) jsonParser.parse(new FileReader("config.json"));
            BotConfig config = new BotConfig();
            config.setToken((String) jsonObject.get("token"));
            BotConfig.MongoDB mongoDB = new BotConfig.MongoDB();
            mongoDB.setHost((String) ((JSONObject) jsonObject.get("mongodb")).get("host"));
            mongoDB.setPort((String) ((JSONObject) jsonObject.get("mongodb")).get("port"));
            mongoDB.setUser((String) ((JSONObject) jsonObject.get("mongodb")).get("user"));
            mongoDB.setPassword((String) ((JSONObject) jsonObject.get("mongodb")).get("password"));
            mongoDB.setDatabase((String) ((JSONObject) jsonObject.get("mongodb")).get("database"));
            config.setMongoDB(mongoDB);
            botConfig = config;
        } catch (FileNotFoundException ex) {
            BotConfig config = new BotConfig();
            config.setToken("");
            BotConfig.MongoDB mongoDB = new BotConfig.MongoDB();
            mongoDB.setHost("127.0.0.1");
            mongoDB.setPort("27017");
            mongoDB.setUser("");
            mongoDB.setPassword("");
            mongoDB.setDatabase("discordbot");
            config.setMongoDB(mongoDB);
            
            FileWriter fileWriter = null;
            try {
                fileWriter = new FileWriter("config.json");
                fileWriter.write(this.gson.toJson(config));
            } catch (IOException ex1) {
                Logger.getLogger(DiscordBot.class.getName()).log(Level.SEVERE, null, ex1);
                System.exit(0);
            } finally {
                try {
                    fileWriter.flush();
                    fileWriter.close();
                    this.consoleInfo("Please configure your bot in the config.json.");
                    System.exit(0);
                } catch (IOException ex1) {
                    Logger.getLogger(DiscordBot.class.getName()).log(Level.SEVERE, null, ex1);
                    System.exit(0);
                }
            }
            
        } catch (IOException | ParseException ex) {
            Logger.getLogger(DiscordBot.class.getName()).log(Level.SEVERE, null, ex);
            System.exit(0);
        }
    }

    public Gson getGson() {
        return gson;
    }

    public long getStartTimeMillis() {
        return startTimeMillis;
    }

    public BotConfig getBotConfig() {
        return botConfig;
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
