package net.nerdorg.minehop.config;

public class ConfigWrapper {
    public static MinehopConfig config;

    public static void loadConfig() {
        if (config == null) {
            config = new MinehopConfig();
        }
    }

    public static void saveConfig(MinehopConfig minehopConfig) {
        config = minehopConfig;
    }
}
