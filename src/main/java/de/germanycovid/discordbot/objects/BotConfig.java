package de.germanycovid.discordbot.objects;

import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author Kacper Mura
 * 2020 Copyright (c) by germanycovid.de to present.
 * All rights reserved. https://github.com/VocalZero
 *
 */
@Getter
@Setter
public class BotConfig {
    
    private String token;
    private MongoDB mongodb;
    private ServerLists serverLists;

    @Getter
    @Setter
    public static class MongoDB {
        
        private String host;
        private String port;
        private String user;
        private String password;
        private String database;
        
    }

    @Getter
    @Setter
    public static class ServerLists {
        
        private String topggToken;
        private String dblToken;
        private String discordBoats;
        
    }
    
}
