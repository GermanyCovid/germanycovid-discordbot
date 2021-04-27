package de.germanycovid.discordbot.commands;

import de.germanycovid.discordbot.DiscordBot;
import java.awt.Color;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

/**
 *
 * @author Kacper Mura
 * 2020 Copyright (c) by germanycovid.de to present.
 * All rights reserved. https://github.com/VocalZero
 *
 */
public class PrefixCommand {
    
    private final DiscordBot discord;

    public PrefixCommand(DiscordBot discord) {
        this.discord = discord;
    }
    
    public void execute(GuildMessageReceivedEvent event) {
        Message message = event.getMessage();
        String[] args = message.getContentRaw().split(" ");
        
        if (args.length == 1) {
            EmbedBuilder embed = new EmbedBuilder();
            embed.setColor(new Color(235, 52, 94));
            embed.setDescription("Dein Prefix konnte leider nicht übermittelt werden. Um den Prefix zu ändern, nutze ``c!prefix <prefix>``.");
            this.discord.getBackendManager().sendMessage(event, embed.build());
            return;
        }
        
        if(this.discord.getBackendManager().getPrefix(event.getGuild()).equalsIgnoreCase(args[1])) {
            EmbedBuilder embed = new EmbedBuilder();
            embed.setColor(new Color(235, 52, 94));
            embed.setDescription("Den Prefix, welchen du nun verwenden wolltest, ist dein derzeitiger Prefix. Um einen anderen Prefix zu nutzen, so verwende ``c!prefix <prefix>``.");
            this.discord.getBackendManager().sendMessage(event, embed.build());
            return;
        }
        
        this.discord.getBackendManager().setPrefix(event.getGuild(), args[1].toLowerCase());
        EmbedBuilder embed = new EmbedBuilder();
        embed.setColor(new Color(235, 52, 94));
        embed.setDescription("Dein Prefix wurde erfolgreich zu **" + args[1].toLowerCase() + "** geändert.");
        this.discord.getBackendManager().sendMessage(event, embed.build());
    }
    
}
