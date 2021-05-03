package de.germanycovid.discordbot.commands;

import com.google.gson.internal.LinkedTreeMap;
import de.germanycovid.discordbot.DiscordBot;
import java.awt.Color;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Locale;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.utils.AttachmentOption;

/**
 *
 * @author Kacper Mura
 * 2020 Copyright (c) by germanycovid.de to present.
 * All rights reserved. https://github.com/VocalZero
 *
 */
public class DistrictsCommand {
    
    private final DiscordBot discord;

    public DistrictsCommand(DiscordBot discord) {
        this.discord = discord;
    }
    
    public void execute(GuildMessageReceivedEvent event) {
        Message message = event.getMessage();
        String[] args = message.getContentRaw().split(" ");
        
        if (args.length == 1) {
            EmbedBuilder embed = new EmbedBuilder();
            embed.setColor(new Color(235, 52, 94));
            embed.setDescription("Deine Anfrage konnte so nicht verarbeitet werden. Bitte spezifiziere deinen Command:\n\n``" + this.discord.getBackendManager().getPrefix(event.getGuild()) + "districts map`` - Sie die Karte der Inzidenzen für jeden Landkreis\n``" + this.discord.getBackendManager().getPrefix(event.getGuild()) + "districts <Stadt / Landkreis>`` - Sie detaillierte Informationen für dein Landkreis oder deine Kreisfreie Stadt. Schreibe hierfür die Stadt detailliert aus, um die Anfrage zu ermöglichen");
            this.discord.getBackendManager().sendMessage(event, embed.build());
            return;
        }
        
        switch(args[1].toLowerCase()) {
            case "map":
                InputStream inputStream;
                try {
                    URLConnection url = new URL("https://api.germanycovid.de/images/districts").openConnection();
                    url.addRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/74.0.3729.169 Safari/537.36");
                    inputStream = url.getInputStream();
                    event.getChannel().sendMessage("Diese Karte für die Landkreise wird unsererseits täglich um 11:00 Uhr (MESZ) aktualisiert.").addFile(inputStream, "districts.png", new AttachmentOption[0]).queue();
                } catch (IOException ex) {
                    EmbedBuilder embed = new EmbedBuilder();
                    embed.setColor(new Color(235, 52, 94));
                    embed.setDescription("Leider konnte deine Anfrage nicht bearbeitet werden. Sollte es an uns liegen, so kannst du per [GitHub](https://github.com/GermanyCovid/germanycovid-discordbot/issues) ein Issue stellen.");
                    this.discord.getBackendManager().sendMessage(event, embed.build());
                    this.discord.consoleError("The image for the statistics could not be loaded.");
                }
                break;
            default:
                try {
                    LinkedTreeMap<String, Object> district = this.discord.getBackendManager().getDistrictsByName(message.getContentRaw().split(this.discord.getBackendManager().getPrefix(event.getGuild()) + "districts ")[1]);
                    if (district == null) {
                        EmbedBuilder embed = new EmbedBuilder();
                        embed.setColor(new Color(235, 52, 94));
                        embed.setDescription("Dein Landkreis bzw. deine kreisfreie Stadt konnte leider nicht gefunden werden. Vergewissere dich, dass du deinen Landkreis oder deine kreisfreie Stadt richtig geschrieben hast. Sollte es an uns liegen, so kannst du per [GitHub](https://github.com/GermanyCovid/germanycovid-discordbot/issues) ein Issue stellen.");
                        this.discord.getBackendManager().sendMessage(event, embed.build());
                        return;
                    }
                    LinkedTreeMap<String, Double> delta = (LinkedTreeMap<String, Double>) district.get("delta");
                    DecimalFormat decimalFormat = (DecimalFormat) NumberFormat.getInstance(Locale.US);
                    DecimalFormatSymbols decimalFormatSymbols = decimalFormat.getDecimalFormatSymbols();
                    decimalFormatSymbols.setGroupingSeparator('.');
                    decimalFormat.setDecimalFormatSymbols(decimalFormatSymbols);
                    EmbedBuilder embed = new EmbedBuilder();
                    embed.setColor(new Color(22, 115, 232));
                    embed.setTitle("Statistiken für " + ((String) district.get("name")) + " (" + ((String) district.get("state")) + ")");
                    embed.setDescription("** **\n\n");
                    embed.addField("Allgemeine Statistiken", "**Fälle**\n" + decimalFormat.format(Math.round(Double.valueOf(String.valueOf(district.get("cases"))))) + " (+" + decimalFormat.format(Math.round(delta.get("cases"))) + ")" + "\n**7-Tages-Inzidenz**\n" + decimalFormat.format(Math.round(Double.valueOf(String.valueOf(district.get("weekIncidence"))))) + "\n** **", true);
                    embed.addField("** **", "**Todesfälle**\n" + decimalFormat.format(Math.round(Double.valueOf(String.valueOf(district.get("deaths"))))) + " (+" + decimalFormat.format(Math.round(delta.get("deaths"))) + ")" + "\n**Fälle pro Woche**\n" + decimalFormat.format(Math.round(Double.valueOf(String.valueOf(district.get("casesPerWeek"))))) + "\n** **", true);
                    embed.addField("** **", "**Genesen**\n" + decimalFormat.format(Math.round(Double.valueOf(String.valueOf(district.get("recovered"))))) + " (+" + decimalFormat.format(Math.round(delta.get("recovered"))) + ")" + "\n**Fälle pro 100k Einwohner**\n" + decimalFormat.format(Math.round(Double.valueOf(String.valueOf(district.get("casesPer100k"))))) + "\n** **", true);
                    this.discord.getBackendManager().sendMessage(event, embed.build());
                } catch (IOException ex) {
                    EmbedBuilder embed = new EmbedBuilder();
                    embed.setColor(new Color(235, 52, 94));
                    embed.setDescription("Leider konnte deine Anfrage nicht bearbeitet werden. Sollte es an uns liegen, so kannst du per [GitHub](https://github.com/GermanyCovid/germanycovid-discordbot/issues) ein Issue stellen.");
                    this.discord.getBackendManager().sendMessage(event, embed.build());
                    this.discord.consoleError("The districts data for " + args[1].toLowerCase() + " could not be loaded.");
                }
                break;
        }
    }
    
}
