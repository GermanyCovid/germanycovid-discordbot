package de.germanycovid.discordbot.commands;

import de.germanycovid.discordbot.DiscordBot;
import java.awt.Color;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Date;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.utils.AttachmentOption;

/**
 *
 * @author Kacper Mura
 * 2021 Copyright (c) by germanycovid.de to present.
 * All rights reserved. https://github.com/VocalZero
 *
 */
public class VaccinationsCommand {
    
    private final DiscordBot discord;

    public VaccinationsCommand(DiscordBot discord) {
        this.discord = discord;
    }
    
    public void execute(GuildMessageReceivedEvent event) {
        if(this.discord.getBackendManager().isHoliday(new Date())) {
            EmbedBuilder embed = new EmbedBuilder();
            embed.setColor(new Color(235, 52, 94));
            embed.setDescription("An Sams-, Sonn- und Feiertagen werden keine Impfdaten übermittelt.");
            this.discord.getBackendManager().sendMessage(event, embed.build());
        }
        
        InputStream inputStream;
        try {
            URLConnection url = new URL("https://api.germanycovid.de/images/vaccinations").openConnection();
            url.addRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/74.0.3729.169 Safari/537.36");
            inputStream = url.getInputStream();
            event.getChannel().sendMessage("Die allgemeinen Statistiken werden unsererseits täglich um 14:00 Uhr (MESZ) aktualisiert.\nAn Sams-, Sonn- und Feiertagen werden keine Impfdaten übermittelt.").addFile(inputStream, "vaccinations.png", new AttachmentOption[0]).queue();
        } catch (IOException ex) {
            EmbedBuilder embed = new EmbedBuilder();
            embed.setColor(new Color(235, 52, 94));
            embed.setDescription("Leider konnte deine Anfrage nicht bearbeitet werden. Sollte es an uns liegen, so kannst du per [GitHub](https://github.com/GermanyCovid/germanycovid-discordbot/issues) ein Issue stellen.");
            this.discord.getBackendManager().sendMessage(event, embed.build());
            this.discord.consoleError("The image for the statistics could not be loaded.");
        }
    }
    
}
