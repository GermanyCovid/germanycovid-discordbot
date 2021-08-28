package de.germanycovid.discordbot.managers;

import com.mongodb.ConnectionString;
import com.mongodb.MongoException;
import com.mongodb.async.client.MongoClient;
import com.mongodb.async.client.MongoClients;
import com.mongodb.async.client.MongoCollection;
import com.mongodb.async.client.MongoDatabase;
import de.germanycovid.discordbot.DiscordBot;
import de.germanycovid.discordbot.objects.BotConfig;
import java.text.MessageFormat;
import java.util.logging.Level;
import java.util.logging.Logger;

import lombok.Getter;
import org.bson.Document;

/**
 *
 * @author Kacper Mura
 * 2020 Copyright (c) by germanycovid.de to present.
 * All rights reserved. https://github.com/VocalZero
 *
 */
public class MongoManager {
    
    private final DiscordBot discord;
    private MongoClient client;
    private MongoDatabase database;
    @Getter
    private MongoCollection<Document> guilds;
    
    public MongoManager(DiscordBot discord) {
        this.discord = discord;
        try {
            BotConfig botConfig = this.discord.getConfig();
            if(botConfig.getMongodb() == null) this.client = MongoClients.create(new ConnectionString("mongodb://127.0.0.1"));
            if(botConfig.getMongodb().getUser().isEmpty() || botConfig.getMongodb().getPassword().isEmpty()) {
                this.client = MongoClients.create(new ConnectionString(MessageFormat.format("mongodb://{0}:{1}", botConfig.getMongodb().getHost(), botConfig.getMongodb().getPort())));
            } else {
                this.client = MongoClients.create(new ConnectionString(MessageFormat.format("mongodb://{0}:{1}@{2}:{3}/?authSource={0}", botConfig.getMongodb().getUser(), botConfig.getMongodb().getPassword(), botConfig.getMongodb().getHost(), botConfig.getMongodb().getPort())));
            }
            this.database = client.getDatabase("discordbot");
            this.guilds = this.database.getCollection("guilds");
            this.discord.consoleInfo("The connection to the MongoDB database has been established.");
        } catch(MongoException ex) {
            discord.consoleError("The connection to the MongoDB database could not be established.");
            Logger.getLogger(MongoManager.class.getName()).log(Level.SEVERE, null, ex);
            Runtime.getRuntime().exit(0);
        }
    }
    
}
