package de.germanycovid.discordbot.commands;

import de.germanycovid.discordbot.DiscordBot;
import java.awt.Color;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

/**
 *
 * @author Kacper Mura
 * 2020 Copyright (c) by germanycovid.de to present.
 * All rights reserved. https://github.com/VocalZero
 *
 */
public class InfoCommand {
    
    private final DiscordBot discord;

    public InfoCommand(DiscordBot discord) {
        this.discord = discord;
    }
    
    public void execute(GuildMessageReceivedEvent event) {
        EmbedBuilder embed = new EmbedBuilder();
        
        DecimalFormat decimalFormat = (DecimalFormat) NumberFormat.getInstance(Locale.US);
        DecimalFormatSymbols decimalFormatSymbols = decimalFormat.getDecimalFormatSymbols();
        decimalFormatSymbols.setGroupingSeparator('.');
        decimalFormat.setDecimalFormatSymbols(decimalFormatSymbols);
        
        embed.setColor(new Color(22, 115, 232));
        embed.setAuthor("» Informationen", null, event.getJDA().getSelfUser().getAvatarUrl());
        embed.setDescription("GermanyCovid – Wir bieten dir täglich mehrere Daten kurzgefasst auf einen Blick an, um Dich mit den neusten Daten vertraut zu machen. Neben den Neuinfektionen und den Todesfällen bieten wir auch die Zahlen zu den Intensivstationen, sowie zu den jeweiligen Bundesländern und Landkreisen an.\n\n** **");
        embed.addField("Datenquelle", "Um verlässliche Daten anzubieten greifen wir ausschließlich auf geprüfte Institute zurück. Hierbei handelt es sich einmal um das Robert Koch-Institut und um die Deutsche Interdisziplinäre Vereinigung für Intensiv- und Notfallmedizin (DIVI) e.V. Bei DIVI greifen wir auf die Daten für die Intensivstationen zu, beim RKI greifen wir auf die Daten, wie Neuinfektionen und Todesfälle zu.\n\n** **", false);
        embed.addField("Verlinkungen", "[» Twitter](https://twitter.com/GermanyCovid)\n[» GitHub](https://github.com/GermanyCovid)\n[» Website](https://rentry.co/germanycorona)\n[» Bot einladen](https://germanycovid.de/discord)", true);
        embed.addField("Statistiken", "» " + decimalFormat.format(this.discord.getBackendManager().getGuildCount()) + " Server, " + decimalFormat.format(this.discord.getBackendManager().getUserCount()) + " Nutzer\n» Ping: " + this.getPing() + "ms\n» Uptime: " + this.getOnlineTime(), true);
        this.discord.getBackendManager().sendMessage(event, embed.build());
    }
    
    private long getPing() {
        try {
            InetAddress host = InetAddress.getByName("discord.com");
            long nanoTime = System.nanoTime();
            Socket socket = new Socket(host, 80);
            socket.close();
            return (System.nanoTime()-nanoTime) / 1000000;
        } catch (IOException ex) {
            Logger.getLogger(InfoCommand.class.getName()).log(Level.SEVERE, null, ex);
            return 0;
        }
    }
    
    private String getOnlineTime() {
        long time = System.currentTimeMillis()-this.discord.getStartTimeMillis();

        long seconds = (time / 1000L) % 60;
        long minutes = (time / 60000L % 60L);
        long hours = (time / 3600000L) % 24;
        long days = (time / 86400000L);
        
        String onlineTime = "";
        if(days > 0) {
            onlineTime += days + " Tage, ";
        }
        if(hours > 0) {
            onlineTime += hours + " Stunden, ";
        }
        if(minutes > 0) {
            onlineTime += minutes + " Minuten";
        }
        if(seconds > 0 && days == 0) {
            onlineTime += ", " + seconds + " Sekunden";
        }
        
        return onlineTime;
    }
    
}
