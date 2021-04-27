package de.germanycovid.discordbot.handlers;

import de.germanycovid.discordbot.DiscordBot;
import de.germanycovid.discordbot.commands.ChannelCommand;
import de.germanycovid.discordbot.commands.InfoCommand;
import de.germanycovid.discordbot.commands.StatesCommand;
import de.germanycovid.discordbot.commands.StatsCommand;
import de.germanycovid.discordbot.commands.DistrictsCommand;
import de.germanycovid.discordbot.commands.HospitalCommand;
import de.germanycovid.discordbot.commands.PrefixCommand;
import java.text.MessageFormat;
import net.dv8tion.jda.api.entities.Message;
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
    private InfoCommand infoCommand;
    private StatsCommand statsCommand;
    private StatesCommand statesCommand;
    private DistrictsCommand districtsCommand;
    private HospitalCommand hospitalCommand;
    private PrefixCommand prefixCommand;
    private ChannelCommand channelCommand;
    
    public EventHandler(DiscordBot discordBot) {
        this.discordBot = discordBot;
        this.infoCommand = new InfoCommand(discordBot);
        this.statsCommand = new StatsCommand(discordBot);
        this.statesCommand = new StatesCommand(discordBot);
        this.districtsCommand = new DistrictsCommand(discordBot);
        this.hospitalCommand = new HospitalCommand(discordBot);
        this.prefixCommand = new PrefixCommand(discordBot);
        this.channelCommand = new ChannelCommand(discordBot);
    }

    @Override
    public void onReady(ReadyEvent event) {

    }

    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
        Message message = event.getMessage();
        String prefix = this.discordBot.getBackendManager().getPrefix(event.getGuild());
        
        if (message.getMentionedUsers().stream().filter(t -> t.getId().equals(event.getGuild().getSelfMember().getId())).findFirst().orElse(null) != null) {
            // HELP
            return;
        }
        
        if (!message.getContentRaw().toLowerCase().startsWith(prefix)) {
            return;
        }
        
        if (message.getContentRaw().toLowerCase().startsWith(prefix + "info")) {
            infoCommand.execute(event);
        } else if (message.getContentRaw().toLowerCase().startsWith(prefix + "stats")) {
            statsCommand.execute(event);
        } else if (message.getContentRaw().toLowerCase().startsWith(prefix + "states")) {
            statesCommand.execute(event);
        } else if (message.getContentRaw().toLowerCase().startsWith(prefix + "districts")) {
            districtsCommand.execute(event);
        } else if (message.getContentRaw().toLowerCase().startsWith(prefix + "hospital")) {
            hospitalCommand.execute(event);
        } else if (message.getContentRaw().toLowerCase().startsWith(prefix + "prefix")) {
            prefixCommand.execute(event);
        } else if (message.getContentRaw().toLowerCase().startsWith(prefix + "channel")) {
            channelCommand.execute(event);
        } else {
            return;
        }
        
        this.discordBot.consoleInfo(MessageFormat.format("[SHARD {0}] {1} ({2}) ran command {3} in {4} (#{5})", event.getJDA().getShardInfo().getShardId(), event.getAuthor().getName(), event.getAuthor().getId(), message.getContentRaw().toLowerCase().split(" ")[0], event.getGuild().getName(), event.getChannel().getName()));
    }
    
}
