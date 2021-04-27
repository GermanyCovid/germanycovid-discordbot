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
        embed.setDescription("Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et\n\n** **");
        embed.addField("Befehle", "**c!info** - Hier findest Du Fakten und Informationen zum Bot.\n"
                + "**c!stats** - Siehe die wichtigsten Daten für die Bundesrepublik.\n"
                + "**c!states** - Finde die Daten für die Bundesländer\n"
                + "**c!districts** - Finde die Daten für die 412 Landkreise bzw. Kreisfreien Städte\n"
                + "**c!hospital** - Finde die wichtigsten Daten von den Intensivstationen.", false);
        if(this.discord.getBackendManager().checkForPermissions(event.getMember())) {
            embed.addField("Admin Befehle", "**c!prefix [prefix]** - Änder den Prefix\n"
                + "**c!channel [id]** - Gebe einen Channel an, wo nur noch Commands ausführbar sein sollen", false);
        }
        this.discord.getBackendManager().sendMessage(event, embed.build());
    }
    
}
