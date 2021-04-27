package de.germanycovid.discordbot.commands;

import de.germanycovid.discordbot.DiscordBot;
import java.awt.Color;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

/**
 *
 * @author kacpe
 */
public class InfoCommand {
    
    private final DiscordBot discord;

    public InfoCommand(DiscordBot discord) {
        this.discord = discord;
    }
    
    public void execute(GuildMessageReceivedEvent event) {
        Message message = event.getMessage();
        String[] args = message.getContentRaw().split(" ");
        
        EmbedBuilder embed = new EmbedBuilder();
        embed.setColor(new Color(22, 115, 232));
        embed.setAuthor("» Informationen", null, event.getJDA().getSelfUser().getAvatarUrl());
        embed.setDescription("Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et\n\n** **");
        embed.addField("Datenquelle", "Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut\n\n** **", false);
        embed.addField("Verlinkungen", "» Twitter\n» GitHub\n» Website\n» Bot einladen", true);
        embed.addField("Statistiken", "» 0 Server, 0 Nutzer\n» Ping: 0ms\n» Uptime: 0 Tage, 0 Stunden", true);
        this.discord.getBackendManager().sendMessage(event, embed.build());
    }
    
}
