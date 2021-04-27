package de.germanycovid.discordbot.managers;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import de.germanycovid.discordbot.DiscordBot;
import de.germanycovid.discordbot.objects.GuildData;
import java.awt.Color;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;
import net.dv8tion.jda.internal.utils.PermissionUtil;

/**
 *
 * @author Kacper Mura
 * 2020 Copyright (c) by germanycovid.de to present.
 * All rights reserved. https://github.com/VocalZero
 *
 */
public class BackendManager {
    
    private final DiscordBot discord;
    private LoadingCache<String, GuildData> guildCache;
    
    public BackendManager(DiscordBot discord) {
        this.discord = discord;
        initCache();
    }
 
    private void initCache() {
        this.guildCache = (LoadingCache<String, GuildData>) CacheBuilder.newBuilder().maximumSize(100L).expireAfterWrite(10L, TimeUnit.MINUTES).build((CacheLoader) new CacheLoader<String, GuildData>() {
            @Override
            public GuildData load(String id) throws Exception {
                CompletableFuture<GuildData> completableFuture = new CompletableFuture<>();
                discord.getGuildManager().getGuild(id, result -> {
                    completableFuture.complete(result);
                });
                return completableFuture.get();
            }
        });
    }
    
    public LoadingCache<String, GuildData> getGuildCache() {
        return guildCache;
    }
    
    public GuildData getGuild(Guild guild) {
        try {
            return this.guildCache.get(guild.getId());
        } catch (ExecutionException ex) {
            return null;
        }
    }
    
    public GuildData getGuild(String id) {
        try {
            return this.guildCache.get(id);
        } catch (ExecutionException ex) {
            return null;
        }
    }
    
    public String getPrefix(Guild guild) {
        return this.getGuild(guild).getPrefix();
    }
    
    public void setPrefix(Guild guild, String prefix) {
        this.getGuild(guild).setPrefix(prefix);
        this.discord.getGuildManager().saveGuild(this.getGuild(guild));
    }
    
    public String getChannelId(Guild guild) {
        return this.getGuild(guild).getChannelId();
    }
    
    public void setChannelId(Guild guild, String channel) {
        this.getGuild(guild).setChannelId(channel);
        this.discord.getGuildManager().saveGuild(this.getGuild(guild));
    }
    
    public int getGuildCount() {
        int count = 0;
        for (JDA shard : this.discord.getShardManager().getShards()) {
            count = count+shard.getGuilds().size();
        }
        return count;
    }
        
    public int getUserCount() {
        int count = 0;
        for (JDA shard : this.discord.getShardManager().getShards()) {
            count = shard.getGuilds().stream().map(guild -> guild.getMemberCount()).reduce(count, Integer::sum);
        }
        return count;
    }
    
    public boolean checkForPermissions(Member member) {
        if(member == null) {
            return false;
        }
        return PermissionUtil.checkPermission(member, Permission.ADMINISTRATOR) || member.getId().equals("223891083724193792") || member.getId().equals("359348375864344576");
    }
    
    public void sendMessage(GuildMessageReceivedEvent event, MessageEmbed messageEmbed) {
        try {
            event.getChannel().sendMessage(messageEmbed).queue();
        } catch(InsufficientPermissionException ex) {
            try {
                event.getChannel().sendMessage("I do not have permissions for **" + ex.getPermission().getName().toLowerCase() + "**, please contact an administrator.").queue();
            } catch (InsufficientPermissionException ex1) {
                try {
                    event.getAuthor().openPrivateChannel().queue((channel) -> {
                        EmbedBuilder embed = new EmbedBuilder();
                        embed.setColor(new Color(22, 115, 232));
                        embed.setDescription("I do not have permissions to **" + ex.getPermission().getName().toLowerCase() + "** in " + event.getChannel().getAsMention() + ", please contact an administrator.");
                        try {
                            channel.sendMessage(embed.build()).queue();
                        } catch (InsufficientPermissionException ex2) {
                        }
                    });
                } catch (InsufficientPermissionException ex2) {
                }
            }
        }
    }
    
}
