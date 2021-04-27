package de.germanycovid.discordbot.handlers;

import de.germanycovid.discordbot.DiscordBot;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

/**
 *
 * @author Kacper Mura
 * 2020 Copyright (c) by germanycovid.de to present.
 * All rights reserved. https://github.com/VocalZero
 *
 */
public class EventHandler extends ListenerAdapter {
    
    private final DiscordBot discordBot;

    public EventHandler(DiscordBot discordBot) {
        this.discordBot = discordBot;
    }

    @Override
    public void onReady(ReadyEvent event) {

    }

    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {

    }
    
}
