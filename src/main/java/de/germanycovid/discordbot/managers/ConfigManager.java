package de.germanycovid.discordbot.managers;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import de.germanycovid.discordbot.DiscordBot;
import de.germanycovid.discordbot.objects.BotConfig;
import lombok.Getter;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ConfigManager {

    private final DiscordBot discord;
    @Getter
    private BotConfig config;

    public ConfigManager(DiscordBot discord) {
        this.discord = discord;
        this.config = this.loadConfig();
    }

    private BotConfig loadConfig() {
        try {
            JsonElement jsonElement = JsonParser.parseReader(new FileReader("config.json"));
            return this.discord.getGson().fromJson(jsonElement, BotConfig.class);
        } catch (FileNotFoundException ex) {
            this.createConfig();
            return null;
        }
    }

    private void createConfig() {
        BotConfig config = new BotConfig();
        config.setToken("");

        BotConfig.MongoDB mongoDB = new BotConfig.MongoDB();
        mongoDB.setHost("127.0.0.1");
        mongoDB.setPort("27017");
        mongoDB.setUser("");
        mongoDB.setPassword("");
        mongoDB.setDatabase("discordbot");
        config.setMongodb(mongoDB);

        BotConfig.ServerLists serverLists = new BotConfig.ServerLists();
        serverLists.setTopggToken("");
        serverLists.setDiscordBoats("");
        serverLists.setDblToken("");
        config.setServerLists(serverLists);

        FileWriter fileWriter = null;
        try {
            fileWriter = new FileWriter("config.json");
            fileWriter.write(this.discord.getGson().toJson(config));
        } catch (IOException ex1) {
            Logger.getLogger(DiscordBot.class.getName()).log(Level.SEVERE, null, ex1);
            System.exit(0);
        } finally {
            try {
                fileWriter.flush();
                fileWriter.close();
                this.discord.consoleInfo("Please configure your bot in the config.json.");
                System.exit(0);
            } catch (IOException ex1) {
                Logger.getLogger(DiscordBot.class.getName()).log(Level.SEVERE, null, ex1);
                System.exit(0);
            }
        }
    }

}
