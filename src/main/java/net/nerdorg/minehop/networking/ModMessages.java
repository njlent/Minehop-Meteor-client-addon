package net.nerdorg.minehop.networking;

import net.minecraft.util.Identifier;
import net.nerdorg.minehop.MinehopAddon;

public class ModMessages {
    public static final Identifier CONFIG_SYNC_ID = Identifier.of(MinehopAddon.MOD_ID, "config");
    public static final Identifier ZONE_SYNC_ID = Identifier.of(MinehopAddon.MOD_ID, "zone");
    public static final Identifier SELF_V_TOGGLE = Identifier.of(MinehopAddon.MOD_ID, "self_v_toggle");
    public static final Identifier OTHER_V_TOGGLE = Identifier.of(MinehopAddon.MOD_ID, "other_v_toggle");
    public static final Identifier REPLAY_V_TOGGLE = Identifier.of(MinehopAddon.MOD_ID, "replay_v_toggle");
    public static final Identifier MAP_FINISH = Identifier.of(MinehopAddon.MOD_ID, "map_finish");
    public static final Identifier SEND_TIME = Identifier.of(MinehopAddon.MOD_ID, "send_time");
    public static final Identifier SEND_EFFICIENCY = Identifier.of(MinehopAddon.MOD_ID, "send_efficiency");
    public static final Identifier SEND_SPECTATORS = Identifier.of(MinehopAddon.MOD_ID, "send_spectators");
    public static final Identifier CLIENT_SPEC_EFFICIENCY = Identifier.of(MinehopAddon.MOD_ID, "client_spec_efficiency");
    public static final Identifier SERVER_SPEC_EFFICIENCY = Identifier.of(MinehopAddon.MOD_ID, "server_spec_efficiency");
    public static final Identifier OPEN_MAP_SCREEN = Identifier.of(MinehopAddon.MOD_ID, "open_map_screen");
    public static final Identifier SEND_RECORDS = Identifier.of(MinehopAddon.MOD_ID, "send_records");
    public static final Identifier SEND_MAPS = Identifier.of(MinehopAddon.MOD_ID, "send_maps");
    public static final Identifier SEND_PERSONAL_RECORDS = Identifier.of(MinehopAddon.MOD_ID, "send_personal_records");
    public static final Identifier UPDATE_POWER = Identifier.of(MinehopAddon.MOD_ID, "update_power");
    public static final Identifier HANDSHAKE_ID = Identifier.of(MinehopAddon.MOD_ID, "handshake_id");
    public static final Identifier ANTI_CHEAT_CHECK = Identifier.of(MinehopAddon.MOD_ID, "anti_cheat_check");
    public static final Identifier SET_PLAYER_CHEATER = Identifier.of(MinehopAddon.MOD_ID, "set_player_cheater");
}
