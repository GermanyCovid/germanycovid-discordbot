package de.germanycovid.discordbot.commands;

import de.germanycovid.discordbot.DiscordBot;
import java.awt.Color;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

/**
 *
 * @author Kacper Mura
 * 2020 Copyright (c) by germanycovid.de to present.
 * All rights reserved. https://github.com/VocalZero
 *
 */
public class SupportCommand {
    
    private final DiscordBot discord;

    public SupportCommand(DiscordBot discord) {
        this.discord = discord;
    }
    
    public void execute(GuildMessageReceivedEvent event) {
        EmbedBuilder embed = new EmbedBuilder();
        embed.setColor(new Color(22, 115, 232));
        embed.setAuthor("» Support", null, event.getJDA().getSelfUser().getAvatarUrl());
        embed.setDescription("Du benötigst Hilfe? Dann kannst du uns per [GitHub Issue](https://github.com/GermanyCovid/germanycovid-discordbot/issues), [Twitter](https://twitter.com/GermanyCovid) oder auf unserem [Discord-Server](https://www.germanycovid.de/invite) erreichen.");
        this.discord.getBackendManager().sendMessage(event, embed.build());
    }
    
}
