package de.germanycovid.discordbot.objects;

/**
 *
 * @author Kacper Mura
 * 2020 Copyright (c) by germanycovid.de to present.
 * All rights reserved. https://github.com/VocalZero
 *
 */
public class BotConfig {
    
    private String token;
    private MongoDB mongodb;
    
    public static class MongoDB {
        
        private String host;
        private String port;
        private String user;
        private String password;
        private String database;

        public String getHost() {
            return host;
        }

        public void setHost(String host) {
            this.host = host;
        }

        public String getPort() {
            return port;
        }

        public void setPort(String port) {
            this.port = port;
        }

        public String getUser() {
            return user;
        }

        public void setUser(String user) {
            this.user = user;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public String getDatabase() {
            return database;
        }

        public void setDatabase(String database) {
            this.database = database;
        }
        
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public MongoDB getMongoDB() {
        return mongodb;
    }

    public void setMongoDB(MongoDB mongoDB) {
        this.mongodb = mongoDB;
    }
    
}
