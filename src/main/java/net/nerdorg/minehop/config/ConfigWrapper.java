package net.nerdorg.minehop.config;

import me.shedaniel.autoconfig.AutoConfig;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.nerdorg.minehop.MinehopAddon;
// Disabled imports - not needed for Meteor addon
// import net.nerdorg.minehop.commands.SpectateCommands;
// import net.nerdorg.minehop.data.DataManager;
// import net.nerdorg.minehop.item.ModItems;
// import net.nerdorg.minehop.networking.PacketHandler;
// import net.nerdorg.minehop.util.ZoneUtil;

import java.util.*;

public class ConfigWrapper {
    public static MinehopConfig config;

    public static void register() {
        // Server-side features disabled for Meteor addon
        // This method is kept for compatibility but does nothing
    }

    public static void loadConfig() {
        config = AutoConfig.getConfigHolder(MinehopConfig.class).getConfig();
    }

    public static void saveConfig(MinehopConfig minehopConfig) {
        AutoConfig.getConfigHolder(MinehopConfig.class).setConfig(minehopConfig);
        AutoConfig.getConfigHolder(MinehopConfig.class).save();
    }
}
