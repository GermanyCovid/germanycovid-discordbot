package de.germanycovid.discordbot.handlers;

import de.germanycovid.discordbot.DiscordBot;
import de.germanycovid.discordbot.commands.*;

import java.text.MessageFormat;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.guild.GuildLeaveEvent;
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
    private final InfoCommand infoCommand;
    private final StatsCommand statsCommand;
    private final StatesCommand statesCommand;
    private final DistrictsCommand districtsCommand;
    private final HospitalCommand hospitalCommand;
    private final PrefixCommand prefixCommand;
    private final ChannelCommand channelCommand;
    private final HelpCommand helpCommand;
    private final SupportCommand supportCommand;
    private final VaccinationsCommand vaccinationsCommand;

    public EventHandler(DiscordBot discordBot) {
        this.discordBot = discordBot;
        this.infoCommand = new InfoCommand(discordBot);
        this.statsCommand = new StatsCommand(discordBot);
        this.statesCommand = new StatesCommand(discordBot);
        this.districtsCommand = new DistrictsCommand(discordBot);
        this.hospitalCommand = new HospitalCommand(discordBot);
        this.prefixCommand = new PrefixCommand(discordBot);
        this.channelCommand = new ChannelCommand(discordBot);
        this.helpCommand = new HelpCommand(discordBot);
        this.supportCommand = new SupportCommand(discordBot);
        this.vaccinationsCommand = new VaccinationsCommand(discordBot);
    }

    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
        Message message = event.getMessage();
        String prefix = this.discordBot.getBackendManager().getPrefix(event.getGuild());
        
        if(!this.discordBot.getBackendManager().checkForPermissions(event.getMember())) {
            if(!this.discordBot.getBackendManager().getChannelId(event.getGuild()).isEmpty()) {
                if(!event.getChannel().getId().equals(this.discordBot.getBackendManager().getChannelId(event.getGuild()))) {
                    return;
                }
            }
        }
        
        if (message.getMentionedUsers().stream().filter(t -> t.getId().equals(event.getGuild().getSelfMember().getId())).findFirst().orElse(null) != null) {
            helpCommand.execute(event);
            return;
        }
        
        if (!message.getContentRaw().toLowerCase().startsWith(prefix)) return;
        
        if (message.getContentRaw().toLowerCase().startsWith(prefix + "info") || message.getContentRaw().toLowerCase().startsWith(prefix + "about")
                || message.getContentRaw().toLowerCase().startsWith(prefix + "invite")) {
            infoCommand.execute(event);
        } else if (message.getContentRaw().toLowerCase().startsWith(prefix + "stats") || message.getContentRaw().toLowerCase().startsWith(prefix + "general")) {
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
        } else if (message.getContentRaw().toLowerCase().startsWith(prefix + "help")) {
            helpCommand.execute(event);
        } else if (message.getContentRaw().toLowerCase().startsWith(prefix + "support") || message.getContentRaw().toLowerCase().startsWith(prefix + "discord")
                || message.getContentRaw().toLowerCase().startsWith(prefix + "twitter") || message.getContentRaw().toLowerCase().startsWith(prefix + "github")) {
            supportCommand.execute(event);
        } else if (message.getContentRaw().toLowerCase().startsWith(prefix + "vaccinations")) {
            vaccinationsCommand.execute(event);
        } /*else if (message.getContentRaw().toLowerCase().startsWith(prefix + "feed")) {
            feedCommand.execute(event);
        }*/ else {
            return;
        }
        
        this.discordBot.consoleInfo(MessageFormat.format("[SHARD {0}] {1} ({2}) ran command {3} in {4} (#{5})", event.getJDA().getShardInfo().getShardId(), event.getAuthor().getName(), event.getAuthor().getId(), message.getContentRaw().toLowerCase().split(" ")[0], event.getGuild().getName(), event.getChannel().getName()));
    }

    @Override
    public void onGuildLeave(GuildLeaveEvent event) {
        this.discordBot.getGuildManager().deleteGuild(event.getGuild().getId());
    }
    
}
