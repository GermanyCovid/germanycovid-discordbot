package de.germanycovid.discordbot.commands;

import de.germanycovid.discordbot.DiscordBot;
import java.awt.Color;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.utils.AttachmentOption;

/**
 *
 * @author Kacper Mura
 * 2020 Copyright (c) by germanycovid.de to present.
 * All rights reserved. https://github.com/VocalZero
 *
 */
public class StatesCommand {
    
    private final DiscordBot discord;

    public StatesCommand(DiscordBot discord) {
        this.discord = discord;
    }
    
    public void execute(GuildMessageReceivedEvent event) {
        Message message = event.getMessage();
        String[] args = message.getContentRaw().split(" ");
        
        if (args.length == 1) {
            EmbedBuilder embed = new EmbedBuilder();
            embed.setColor(new Color(235, 52, 94));
            embed.setDescription("Deine Anfrage konnte so nicht verarbeitet werden. Bitte spezifiziere deinen Command:\n\n``c!states map`` - Sie die Karte der Inzidenzen für alle Bundesländer\n``c!states <bundesland>`` - Sie detaillierte Informationen für dein Bundesland. Schreibe hierfür das Bundesland komplett aus oder nutze die amtlichen Abkürzungen (NW, BW, ...)");
            this.discord.getBackendManager().sendMessage(event, embed.build());
            return;
        }
        
        switch(args[1].toLowerCase()) {
            case "map":
                InputStream inputStream;
                try {
                    URLConnection url = new URL("https://api.germanycovid.de/images/states").openConnection();
                    url.addRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/74.0.3729.169 Safari/537.36");
                    inputStream = url.getInputStream();
                    event.getChannel().sendFile(inputStream, "states.png", new AttachmentOption[0]).queue();
                } catch (IOException ex) {
                    EmbedBuilder embed = new EmbedBuilder();
                    embed.setColor(new Color(235, 52, 94));
                    embed.setDescription("Leider konnte deine Anfrage nicht bearbeitet werden. Sollte es an uns liegen, so schreibe uns bitte eine E-Mail (support@germanycovid.de).");
                    this.discord.getBackendManager().sendMessage(event, embed.build());
                    this.discord.consoleError("The image for the statistics could not be loaded.");
                }
                break;
            default:
                
                break;
        }
    }
    
}
