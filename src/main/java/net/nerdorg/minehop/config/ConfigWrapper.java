package net.nerdorg.minehop.config;

public class ConfigWrapper {
    public static MinehopConfig config;

    public static void register() {
        // Server-side features disabled for Meteor addon
        // This method is kept for compatibility but does nothing
    }

    public static void loadConfig() {
        if (config == null) {
            config = new MinehopConfig();
        }
    }

    public static void saveConfig(MinehopConfig minehopConfig) {
        config = minehopConfig;
    }
}
