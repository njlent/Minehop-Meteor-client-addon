package net.nerdorg.minehop.modules;

import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.systems.modules.Categories;
import net.nerdorg.minehop.MinehopAddon;
import net.nerdorg.minehop.config.ConfigWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Bunnyhopping extends Module {
    private static final Logger LOG = LoggerFactory.getLogger(Bunnyhopping.class);
    private boolean initialized = false;

    private final SettingGroup sgGeneral = settings.getDefaultGroup();
    private final SettingGroup sgMovement = settings.createGroup("Movement");
    private final SettingGroup sgAdvanced = settings.createGroup("Advanced");

    // General settings
    public final Setting<Boolean> crouchHeightAdjustment = sgGeneral.add(new BoolSetting.Builder()
        .name("crouch-height-adjustment")
        .description("Enable dynamic crouch height adjustment (1.8f standing -> 1.35f crouching).")
        .defaultValue(true)
        .onChanged(value -> {
            if (ConfigWrapper.config != null) {
                ConfigWrapper.config.crouch_height_adjustment = value;
            }
        })
        .build()
    );

    public final Setting<Boolean> fallDamage = sgGeneral.add(new BoolSetting.Builder()
        .name("fall-damage (use nofall from meteor)")
        .description("not working, use nofall from meteor.")
        .defaultValue(false)
        .onChanged(value -> {
            if (ConfigWrapper.config != null) {
                ConfigWrapper.config.fall_damage = value;
            }
        })
        .build()
    );

    public final Setting<Double> speedCap = sgGeneral.add(new DoubleSetting.Builder()
        .name("speed-cap")
        .description("Maximum speed multiplier.")
        .defaultValue(0.6)
        .min(0.1)
        .max(10.0)
        .sliderMax(2.0)
        .onChanged(value -> MinehopAddon.o_speed_cap = value)
        .build()
    );

    // Movement settings
    public final Setting<Double> svFriction = sgMovement.add(new DoubleSetting.Builder()
        .name("sv-friction")
        .description("Ground friction coefficient.")
        .defaultValue(0.35)
        .min(0.0)
        .max(20.0)
        .sliderMax(10.0)
        .onChanged(value -> {
            if (ConfigWrapper.config != null) {
                ConfigWrapper.config.movement.sv_friction = value;
            }
        })
        .build()
    );

    public final Setting<Double> svAccelerate = sgMovement.add(new DoubleSetting.Builder()
        .name("sv-accelerate")
        .description("Ground acceleration.")
        .defaultValue(0.1)
        .min(0.0)
        .max(50.0)
        .sliderMax(20.0)
        .onChanged(value -> {
            if (ConfigWrapper.config != null) {
                ConfigWrapper.config.movement.sv_accelerate = value;
            }
        })
        .build()
    );

    public final Setting<Double> svAirAccelerate = sgMovement.add(new DoubleSetting.Builder()
        .name("sv-air-accelerate")
        .description("Air acceleration (strafe control).")
        .defaultValue(1.0E99)
        .min(0.0)
        .max(1.0E100)
        .sliderMax(1.0E100)
        .onChanged(value -> {
            if (ConfigWrapper.config != null) {
                ConfigWrapper.config.movement.sv_airaccelerate = value;
            }
        })
        .build()
    );

    public final Setting<Double> svMaxAirSpeed = sgMovement.add(new DoubleSetting.Builder()
        .name("sv-max-air-speed")
        .description("Maximum air speed.")
        .defaultValue(0.02325)
        .min(0.0)
        .max(5.0)
        .sliderMax(1.0)
        .onChanged(value -> {
            if (ConfigWrapper.config != null) {
                ConfigWrapper.config.movement.sv_maxairspeed = value;
            }
        })
        .build()
    );

    public final Setting<Double> svGravity = sgMovement.add(new DoubleSetting.Builder()
        .name("sv-gravity")
        .description("Gravity multiplier.")
        .defaultValue(0.066)
        .min(0.0)
        .max(5.0)
        .sliderMax(2.0)
        .onChanged(value -> {
            if (ConfigWrapper.config != null) {
                ConfigWrapper.config.movement.sv_gravity = value;
            }
        })
        .build()
    );

    // Advanced settings
    public final Setting<Double> speedMultiplier = sgAdvanced.add(new DoubleSetting.Builder()
        .name("speed-multiplier")
        .description("Overall speed multiplier.")
        .defaultValue(2.2)
        .min(0.1)
        .max(5.0)
        .sliderMax(3.0)
        .onChanged(value -> {
            if (ConfigWrapper.config != null) {
                ConfigWrapper.config.movement.speed_mul = value;
            }
        })
        .build()
    );

    public final Setting<Double> speedCoefficient = sgAdvanced.add(new DoubleSetting.Builder()
        .name("speed-coefficient")
        .description("Speed calculation coefficient.")
        .defaultValue(1.0)
        .min(0.1)
        .max(5.0)
        .sliderMax(2.0)
        .onChanged(value -> {
            if (ConfigWrapper.config != null) {
                ConfigWrapper.config.movement.speed_coefficient = value;
            }
        })
        .build()
    );

    public Bunnyhopping() {
        super(Categories.Movement, "bunnyhopping", "Enables Source-engine style bunnyhopping movement.");
    }

    @Override
    public void onActivate() {
        LOG.info("Bunnyhopping module activated");
        LOG.info("ConfigWrapper.config is " + (ConfigWrapper.config == null ? "NULL" : "NOT NULL"));

        // Enable bunnyhopping in config
        if (ConfigWrapper.config != null) {
            ConfigWrapper.config.enabled = true;
            LOG.info("Set config.enabled = true");

            // If this is the first activation, load config values into settings
            // Otherwise, sync settings to config
            if (!initialized) {
                LOG.info("First activation - loading config values into settings");
                crouchHeightAdjustment.set(ConfigWrapper.config.crouch_height_adjustment);
                fallDamage.set(ConfigWrapper.config.fall_damage);
                svFriction.set(ConfigWrapper.config.movement.sv_friction);
                svAccelerate.set(ConfigWrapper.config.movement.sv_accelerate);
                svAirAccelerate.set(ConfigWrapper.config.movement.sv_airaccelerate);
                svMaxAirSpeed.set(ConfigWrapper.config.movement.sv_maxairspeed);
                svGravity.set(ConfigWrapper.config.movement.sv_gravity);
                speedMultiplier.set(ConfigWrapper.config.movement.speed_mul);
                speedCoefficient.set(ConfigWrapper.config.movement.speed_coefficient);
                initialized = true;
            } else {
                // Sync current settings to config
                syncSettingsToConfig();
            }
        } else {
            LOG.error("Cannot enable bunnyhopping - config is null!");
        }
    }

    private void syncSettingsToConfig() {
        if (ConfigWrapper.config != null) {
            ConfigWrapper.config.crouch_height_adjustment = crouchHeightAdjustment.get();
            ConfigWrapper.config.fall_damage = fallDamage.get();
            ConfigWrapper.config.movement.sv_friction = svFriction.get();
            ConfigWrapper.config.movement.sv_accelerate = svAccelerate.get();
            ConfigWrapper.config.movement.sv_airaccelerate = svAirAccelerate.get();
            ConfigWrapper.config.movement.sv_maxairspeed = svMaxAirSpeed.get();
            ConfigWrapper.config.movement.sv_gravity = svGravity.get();
            ConfigWrapper.config.movement.speed_mul = speedMultiplier.get();
            ConfigWrapper.config.movement.speed_coefficient = speedCoefficient.get();
        }
    }

    @Override
    public void onDeactivate() {
        LOG.info("Bunnyhopping module deactivated");

        // Disable bunnyhopping when module is turned off
        if (ConfigWrapper.config != null) {
            ConfigWrapper.config.enabled = false;
            LOG.info("Set config.enabled = false");
        }
    }
}

