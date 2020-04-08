package de.unidisk.config;

public class DatabaseConfiguration {
    boolean initializeMockData;
    String configFile;

    public DatabaseConfiguration(boolean initializeMockData, String configFile) {
        this.initializeMockData = initializeMockData;
        this.configFile = configFile;
    }

    public boolean isInitializeMockData() {
        return initializeMockData;
    }

    public String getConfigFile() {
        return configFile;
    }
}
