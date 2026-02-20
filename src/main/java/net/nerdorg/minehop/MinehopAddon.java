package net.nerdorg.minehop;

import com.mojang.logging.LogUtils;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.JanksonConfigSerializer;
import meteordevelopment.meteorclient.addons.GithubRepo;
import meteordevelopment.meteorclient.addons.MeteorAddon;
import meteordevelopment.meteorclient.systems.hud.HudGroup;
import meteordevelopment.meteorclient.systems.modules.Modules;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.client.option.ServerList;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.nerdorg.minehop.config.ConfigWrapper;
import net.nerdorg.minehop.config.MinehopConfig;
// import net.nerdorg.minehop.entity.custom.Zone; // Disabled - entities not used in Meteor addon
import net.nerdorg.minehop.modules.Bunnyhopping;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MinehopAddon extends MeteorAddon {
    public static final String MOD_ID = "minehop-meteor";
    public static final Logger LOG = LogUtils.getLogger();
    public static final Logger LOGGER = LOG; // Alias for compatibility
    // Removed custom category - using Meteor's built-in Movement category
    public static final HudGroup HUD_GROUP = new HudGroup("Minehop");

    // Stub class for disabled Zone functionality
    public static class Zone {}

    // Static fields from original Minehop class
    // Note: DataManager types are disabled but fields kept for compatibility
    public static List mapList = new ArrayList<>(); // Raw type - DataManager.MapData disabled
    public static List recordList = new ArrayList<>(); // Raw type - DataManager.RecordData disabled
    public static List personalRecordList = new ArrayList<>(); // Raw type - DataManager.RecordData disabled
    public static HashMap<String, Zone> zoneList = new HashMap<>();
    public static MinecraftServer server;
    public static boolean override_config = false;

    // Static fields from MinehopClient
    public static int jump_count = 0;
    public static boolean jumping = false;
    public static double last_jump_speed = 0;
    public static double start_jump_speed = 0;
    public static double old_jump_speed = 0;
    public static long last_jump_time = 0;
    public static long old_jump_time = 0;
    public static double last_efficiency;
    public static double gauge;
    public static boolean wasOnGround = false;

    public static boolean hideSelf = false;
    public static boolean hideReplay = false;
    public static boolean hideOthers = false;

    public static long startTime = 0;
    public static float lastSendTime = 0;

    public static List<String> spectatorList = new ArrayList<>();
    public static HashMap<String, List<Double>> efficiencyListMap = new HashMap<>();
    public static HashMap<String, Double> efficiencyUpdateMap = new HashMap<>();
    public static HashMap<String, List<Double>> gaugeListMap = new HashMap<>();
    public static HashMap<String, Double> efficiencyMap = new HashMap<>();
    public static List<String> groundedList = new ArrayList<>();
    public static List replayList = new ArrayList<>();
    public static HashMap<String, HashMap<String, Long>> timerManager = new HashMap<>();
    public static HashMap<String, Zone> playerMapLocation = new HashMap<>();
    public static HashMap<String, Double> speedCapMap = new HashMap<>();
    public static HashMap lastEfficiencyMap = new HashMap<>(); // Raw type for compatibility

    // Override config fields
    public static boolean receivedConfig = false;
    public static boolean o_hns = true;
    public static double o_sv_friction = 4.0;
    public static double o_sv_accelerate = 10.0;
    public static double o_sv_airaccelerate = 10.0;
    public static double o_sv_maxairspeed = 0.6;
    public static double o_speed_mul = 1.0;
    public static double o_sv_gravity = 0.08;
    public static double o_speed_coefficient = 1.0;
    public static boolean o_enabled = true;
    public static double o_speed_cap = 0.6;

    @Override
    public void onInitialize() {
        LOG.info("Initializing Minehop Meteor Addon");

        // Server-side initialization
        ServerLifecycleEvents.SERVER_STARTING.register(s -> server = s);

        // Register attributes for custom entities
        // TODO: Re-enable when entity attributes are fixed
        // FabricDefaultAttributeRegistry.register(ModEntities.RESET_ENTITY, ResetEntity.setAttributes());
        // FabricDefaultAttributeRegistry.register(ModEntities.ZONE, Zone.setAttributes());

        // Register and load config
        AutoConfig.register(MinehopConfig.class, JanksonConfigSerializer::new);
        ConfigWrapper.loadConfig();
        LOG.info("Config loaded: " + (ConfigWrapper.config != null ? "SUCCESS" : "FAILED"));

        // Register managers and handlers - DISABLED FOR MINIMAL BUILD
        // ConfigWrapper.register(); // This references ModItems and other disabled features
        // DataManager.register();
        // MobManager.register();
        // HNSManager.register();
        // ReplayManager.register();
        // ReplayEvents.register();
        // MotdManager.register();

        // Register items, blocks, and entities - DISABLED TO PREVENT CRASHES
        // ModItems.registerModItems();
        // ModBlocks.registerModBlocks();
        // ModBlockEntities.registerBlockEntities();

        // Client-side initialization - DISABLED
        // initializeClient();

        // Register commands - DISABLED
        // registerCommands();

        // Register modules
        Modules.get().add(new Bunnyhopping());

        // Register HUD
        // TODO: Re-enable when client code is fixed
        // Hud.get().register(SqueedometerHud.INFO);

        // Register client tick event for jump tracking
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player != null) {
                if (client.options.jumpKey.isPressed()) {
                    jumping = true;
                }
                else {
                    jumping = false;
                }
            }
        });
    }

    private void initializeClient() {
        MinecraftClient minecraft = MinecraftClient.getInstance();
        minecraft.execute(() -> {
            ServerList serverList = new ServerList(minecraft);
            serverList.loadFile();
            if (!isServerInList(serverList, "play.minehop.net")) {
                serverList.add(new ServerInfo("§c§l§nOfficial Minehop Server", "play.minehop.net", ServerInfo.ServerType.OTHER), false);
                serverList.swapEntries(0, serverList.size() - 1);
                serverList.saveFile();
            }
        });

        // TODO: Re-enable when client code is fixed
        // ClientPacketHandler.registerReceivers();
        ConfigWrapper.loadConfig();

        // TODO: Re-enable when client code is fixed
        // KeyInputHandler.register();
        // JoinEvent.register();

        // Register entity renderers
        // TODO: Fix entity renderers for Minecraft 1.21 API changes
        // EntityRendererRegistry.register(ModEntities.GAMEMODE_ENTITY, GamemodeRenderer::new);
        // EntityModelLayerRegistry.registerModelLayer(ModModelLayers.GAMEMODE_ENTITY, GamemodeModel::getTexturedModelData);
        // EntityRendererRegistry.register(ModEntities.RESET_ENTITY, ResetRenderer::new);
        // EntityModelLayerRegistry.registerModelLayer(ModModelLayers.RESET_ENTITY, ResetModel::getTexturedModelData);
        // EntityRendererRegistry.register(ModEntities.START_ENTITY, StartRenderer::new);
        // EntityModelLayerRegistry.registerModelLayer(ModModelLayers.START_ENTITY, ResetModel::getTexturedModelData);
        // EntityRendererRegistry.register(ModEntities.END_ENTITY, EndRenderer::new);
        // EntityModelLayerRegistry.registerModelLayer(ModModelLayers.END_ENTITY, ResetModel::getTexturedModelData);
        // EntityRendererRegistry.register(ModEntities.REPLAY_ENTITY, ReplayRenderer::new);
        // EntityModelLayerRegistry.registerModelLayer(ModModelLayers.REPLAY_ENTITY, ReplayModel::getTexturedModelData);

        // Client tick events
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (!client.isInSingleplayer()) {
                override_config = true;
            }
            if (client.player != null) {
                if (client.options.jumpKey.isPressed()) {
                    jumping = true;
                } else {
                    jumping = false;
                }
            }
        });

        // Block render layers
        // TODO: Re-enable when Fabric API is properly configured
        // BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.BOOSTER_BLOCK, RenderLayer.getTranslucent());
    }

    private void registerCommands() {
        // Commands disabled - not needed for Meteor addon
        // ConfigCommands.register();
        // GamemodeCommands.register();
        // SpawnCommands.register();
        // MapUtilCommands.register();
        // ZoneManagementCommands.register();
        // BoostCommands.register();
        // VisiblityCommands.register();
        // SpectateCommands.register();
        // SocialsCommands.register();
        // ReplayCommands.register();
        // HelpCommands.register();
        // TestCommands.register();
    }

    private boolean isServerInList(ServerList serverList, String ip) {
        for (int i = 0; i < serverList.size(); i++) {
            ServerInfo info = serverList.get(i);
            if (info.address.equals(ip)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void onRegisterCategories() {
        // No custom category - Bunnyhopping module uses Meteor's built-in Movement category
    }

    @Override
    public String getPackage() {
        return "net.nerdorg.minehop";
    }

    @Override
    public GithubRepo getRepo() {
        return new GithubRepo("njlent", "minehop");
    }
}
