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
public class HelpCommand {
    
    private final DiscordBot discord;

    public HelpCommand(DiscordBot discord) {
        this.discord = discord;
    }
    
    public void execute(GuildMessageReceivedEvent event) {
        Message message = event.getMessage();
        String[] args = message.getContentRaw().split(" ");
        
        EmbedBuilder embed = new EmbedBuilder();
        embed.setColor(new Color(22, 115, 232));
        embed.setAuthor("» Hilfe", null, event.getJDA().getSelfUser().getAvatarUrl());
        embed.setDescription("GermanyCovid – Wir bieten dir täglich mehrere Daten kurzgefasst auf einen Blick an, um Dich mit den neusten Daten vertraut zu machen. Neben den Neuinfektionen und den Todesfällen bieten wir auch die Zahlen zu den Intensivstationen, sowie zu den jeweiligen Bundesländern und Landkreisen an.\n\n** **");
        embed.addField("Befehle", "**" + this.discord.getBackendManager().getPrefix(event.getGuild()) + "info** - Hier findest Du Fakten und Informationen zum Bot.\n"
                + "**" + this.discord.getBackendManager().getPrefix(event.getGuild()) + "stats** - Siehe die wichtigsten Daten für die Bundesrepublik.\n"
                + "**" + this.discord.getBackendManager().getPrefix(event.getGuild()) + "states** - Finde die Daten für die Bundesländer\n"
                + "**" + this.discord.getBackendManager().getPrefix(event.getGuild()) + "districts** - Finde die Daten für die 412 Landkreise bzw. Kreisfreien Städte\n"
                + "**" + this.discord.getBackendManager().getPrefix(event.getGuild()) + "hospital** - Finde die wichtigsten Daten von den Intensivstationen.", false);
        if(this.discord.getBackendManager().checkForPermissions(event.getMember())) {
            embed.addField("Admin Befehle", "**" + this.discord.getBackendManager().getPrefix(event.getGuild()) + "prefix [prefix]** - Änder den Prefix\n"
                + "**" + this.discord.getBackendManager().getPrefix(event.getGuild()) + "channel [id]** - Gebe einen Channel an, wo nur noch Commands ausführbar sein sollen", false);
        }
        this.discord.getBackendManager().sendMessage(event, embed.build());
    }
    
}
