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
public class StatesCommand {
    
    private final DiscordBot discord;

    public StatesCommand(DiscordBot discord) {
        this.discord = discord;
    }
    
    public void execute(GuildMessageReceivedEvent event) {
        Message message = event.getMessage();
        String[] args = message.getContentRaw().split(" ");
        
        if (args.length == 1) {
            EmbedBuilder embed = new EmbedBuilder();
            embed.setColor(new Color(235, 52, 94));
            embed.setDescription("Deine Anfrage konnte so nicht verarbeitet werden. Bitte spezifiziere deinen Command:\n\n``" + this.discord.getBackendManager().getPrefix(event.getGuild()) + "states map`` - Sie die Karte der Inzidenzen für alle Bundesländer\n``" + this.discord.getBackendManager().getPrefix(event.getGuild()) + "states <bundesland>`` - Sie detaillierte Informationen für dein Bundesland. Schreibe hierfür das Bundesland komplett aus oder nutze die amtlichen Abkürzungen (NW, BW, ...)");
            this.discord.getBackendManager().sendMessage(event, embed.build());
            return;
        }
        
        switch(args[1].toLowerCase()) {
            case "map":
                InputStream inputStream;
                try {
                    URLConnection url = new URL("https://api.germanycovid.de/images/states").openConnection();
                    url.addRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/74.0.3729.169 Safari/537.36");
                    inputStream = url.getInputStream();
                    event.getChannel().sendMessage("Diese Karte für die Bundesländer wird unsererseits täglich um 10:00 Uhr (MESZ) aktualisiert.").addFile(inputStream, "states.png", new AttachmentOption[0]).queue();
                } catch (IOException ex) {
                    EmbedBuilder embed = new EmbedBuilder();
                    embed.setColor(new Color(235, 52, 94));
                    embed.setDescription("Leider konnte deine Anfrage nicht bearbeitet werden. Sollte es an uns liegen, so schreibe uns bitte eine E-Mail (support@germanycovid.de).");
                    this.discord.getBackendManager().sendMessage(event, embed.build());
                    this.discord.consoleError("The image for the statistics could not be loaded.");
                }
                break;
            default:
                try {
                    LinkedTreeMap<String, Object> state = null;
                    if(args[1].length() == 2) {
                        state = this.discord.getBackendManager().getStateByAbbreviation(args[1]);
                    } else {
                        state = this.discord.getBackendManager().getStateByName(message.getContentRaw().split(this.discord.getBackendManager().getPrefix(event.getGuild()) + "states ")[1]);
                    }
                    if(state == null) {
                        EmbedBuilder embed = new EmbedBuilder();
                        embed.setColor(new Color(235, 52, 94));
                        embed.setDescription("Dein Bundesland konnte leider nicht gefunden werden. Vergewissere dich, dass Du dieses richtig geschrieben hast. Sollte es an uns liegen, so schreibe uns bitte eine E-Mail (support@germanycovid.de).");
                        this.discord.getBackendManager().sendMessage(event, embed.build());
                        return;
                    }
                    LinkedTreeMap<String, Double> delta = (LinkedTreeMap<String, Double>) state.get("delta");
                    DecimalFormat decimalFormat = (DecimalFormat) NumberFormat.getInstance(Locale.US);
                    DecimalFormatSymbols decimalFormatSymbols = decimalFormat.getDecimalFormatSymbols();
                    decimalFormatSymbols.setGroupingSeparator('.');
                    decimalFormat.setDecimalFormatSymbols(decimalFormatSymbols);
                    EmbedBuilder embed = new EmbedBuilder();
                    embed.setColor(new Color(22, 115, 232));
                    embed.setDescription("** **\n\n");
                    embed.setTitle("Statistiken für " + ((String) state.get("name")) + " (" + ((String) state.get("abbreviation")) + ")");
                    embed.addField("Fälle", decimalFormat.format(Math.round(Double.valueOf(String.valueOf(state.get("cases"))))) + " (+" + decimalFormat.format(Math.round(delta.get("cases"))) + ")", true);
                    embed.addField("Todesfälle", decimalFormat.format(Math.round(Double.valueOf(String.valueOf(state.get("deaths"))))) + " (+" + decimalFormat.format(Math.round(delta.get("deaths"))) + ")", true);
                    embed.addField("Genesen", decimalFormat.format(Math.round(Double.valueOf(String.valueOf(state.get("recovered"))))) + " (+" + decimalFormat.format(Math.round(delta.get("recovered"))) + ")", true);
                    embed.addField("Fälle pro Woche", "" + decimalFormat.format(Math.round(Double.valueOf(String.valueOf(state.get("casesPerWeek"))))), true);
                    embed.addField("Todesfälle pro Woche", "" + decimalFormat.format(Math.round(Double.valueOf(String.valueOf(state.get("deathsPerWeek"))))), true);
                    embed.addField("7-Tages-Inzidenz", "" + decimalFormat.format(Math.round(Double.valueOf(String.valueOf(state.get("weekIncidence"))))), true);
                    embed.addField("Fälle pro 100k Einwohner", "" + decimalFormat.format(Math.round(Double.valueOf(String.valueOf(state.get("casesPer100k"))))), true);
                    this.discord.getBackendManager().sendMessage(event, embed.build());
                } catch (IOException ex) {
                    EmbedBuilder embed = new EmbedBuilder();
                    embed.setColor(new Color(235, 52, 94));
                    embed.setDescription("Leider konnte deine Anfrage nicht bearbeitet werden. Sollte es an uns liegen, so schreibe uns bitte eine E-Mail (support@germanycovid.de).");
                    this.discord.getBackendManager().sendMessage(event, embed.build());
                    this.discord.consoleError("The states data for " + args[1].toLowerCase() + " could not be loaded.");
                }
                break;
        }
    }
    
}
