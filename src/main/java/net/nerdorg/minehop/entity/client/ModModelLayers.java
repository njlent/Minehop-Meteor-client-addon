package net.nerdorg.minehop.entity.client;

import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.util.Identifier;
import net.nerdorg.minehop.MinehopAddon;

public class ModModelLayers {
    public static final EntityModelLayer GAMEMODE_ENTITY =
            new EntityModelLayer(new Identifier(MinehopAddon.MOD_ID, "gamemode_entity"), "main");
    public static final EntityModelLayer RESET_ENTITY =
            new EntityModelLayer(new Identifier(MinehopAddon.MOD_ID, "reset_entity"), "main");
    public static final EntityModelLayer START_ENTITY =
            new EntityModelLayer(new Identifier(MinehopAddon.MOD_ID, "start_entity"), "main");
    public static final EntityModelLayer END_ENTITY =
            new EntityModelLayer(new Identifier(MinehopAddon.MOD_ID, "end_entity"), "main");
    public static final EntityModelLayer REPLAY_ENTITY =
            new EntityModelLayer(new Identifier(MinehopAddon.MOD_ID, "replay_entity"), "main");
    public static final EntityModelLayer CUSTOM_MODEL =
            new EntityModelLayer(new Identifier(MinehopAddon.MOD_ID, "custom_model"), "main");
}
