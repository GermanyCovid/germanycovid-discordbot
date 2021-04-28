package de.germanycovid.discordbot.commands;

import de.germanycovid.discordbot.DiscordBot;
import java.awt.Color;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

/**
 *
 * @author Kacper Mura
 * 2020 Copyright (c) by germanycovid.de to present.
 * All rights reserved. https://github.com/VocalZero
 *
 */
public class ChannelCommand {
    
    private final DiscordBot discord;

    public ChannelCommand(DiscordBot discord) {
        this.discord = discord;
    }
    
    public void execute(GuildMessageReceivedEvent event) {
        Message message = event.getMessage(); 
        String[] args = message.getContentRaw().split(" ");
        
        if (!this.discord.getBackendManager().checkForPermissions(event.getMember())) {
            EmbedBuilder embed = new EmbedBuilder();
            embed.setColor(new Color(235, 52, 94));
            embed.setDescription("Du darfst diesen Befehl nicht ausführen. Diesen Befehl können ausschließlich Personen mit den Rechten ``ADMINISTRATOR`` ausführen. Frage hierfür bei dem Besitzer des Servers nach, ob dieser dir helfen kann.");
            this.discord.getBackendManager().sendMessage(event, embed.build());
            return;
        } 
        
        if (args.length == 1) {
            EmbedBuilder embed = new EmbedBuilder();
            embed.setColor(new Color(235, 52, 94));
            embed.setDescription("Lege einen Channel fest, in welchem die Commands ausschließlich verwendet werden sollen. Nutze hierfür ``" + this.discord.getBackendManager().getPrefix(event.getGuild()) + "channel <channel>``. Um den Channel aufzuheben, so verwende ``" + this.discord.getBackendManager().getPrefix(event.getGuild()) + "channel null``.");
            this.discord.getBackendManager().sendMessage(event, embed.build());
            return;
        }
        
        if(args[1].equalsIgnoreCase("null")) {
            this.discord.getBackendManager().setChannelId(event.getGuild(), "");
            EmbedBuilder embed = new EmbedBuilder();
            embed.setColor(new Color(235, 52, 94));
            embed.setDescription("Du hast erfolgreich den Channel ausgestellt. Die Commands können nun in jedem Channel ausgeführt werden.");
            this.discord.getBackendManager().sendMessage(event, embed.build());
            return;
        }
        
        if (message.getMentionedChannels().isEmpty()) {
            EmbedBuilder embed = new EmbedBuilder();
            embed.setColor(new Color(235, 52, 94));
            embed.setDescription("Lege einen Channel fest, in welchem die Commands ausschließlich verwendet werden sollen. Nutze hierfür ``" + this.discord.getBackendManager().getPrefix(event.getGuild()) + "channel <channel>``. Um den Channel aufzuheben, so verwende ``" + this.discord.getBackendManager().getPrefix(event.getGuild()) + "channel null``.");
            this.discord.getBackendManager().sendMessage(event, embed.build());
            return;
        }
        
        TextChannel textChannel = message.getMentionedChannels().stream().findFirst().orElse(null);
        if (textChannel == null) {
            EmbedBuilder embed = new EmbedBuilder();
            embed.setColor(new Color(235, 52, 94));
            embed.setDescription("Lege einen Channel fest, in welchem die Commands ausschließlich verwendet werden sollen. Nutze hierfür ``" + this.discord.getBackendManager().getPrefix(event.getGuild()) + "channel <channel>``. Um den Channel aufzuheben, so verwende ``" + this.discord.getBackendManager().getPrefix(event.getGuild()) + "channel null``.");
            this.discord.getBackendManager().sendMessage(event, embed.build());
            return;
        }
        
        this.discord.getBackendManager().setChannelId(event.getGuild(), textChannel.getId());
        EmbedBuilder embed = new EmbedBuilder();
        embed.setColor(new Color(22, 115, 232));
        embed.setDescription("Dein Channel wurde erfolgreich übermittelt. Commands werden nun ausschließlich in " + textChannel.getAsMention() + " abgewickelt. Du möchtest diesen Channel aufheben? So verwende ``" + this.discord.getBackendManager().getPrefix(event.getGuild()) + "channel null``.");
        this.discord.getBackendManager().sendMessage(event, embed.build());
    }
    
}
