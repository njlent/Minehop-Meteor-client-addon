package net.nerdorg.minehop.config;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;

@Config(name = "minehop")
public class MinehopConfig implements ConfigData {
    public boolean enabled = false;  // Default to disabled - controlled by Meteor module toggle
    public boolean entity_collisions = true;
    public boolean fall_damage = false;
    public boolean nulls = true;
    public boolean crouch_height_adjustment = false;  // Enable crouch height adjustment by default
    @ConfigEntry.Gui.CollapsibleObject
    public MovementSettings movement = new MovementSettings();
    @ConfigEntry.Gui.Excluded
    public boolean help_command = false;
    @ConfigEntry.Gui.Excluded
    public boolean minehop_motd = false;
    @ConfigEntry.Gui.Excluded
    public boolean client_validation = false; // Disabled - networking API needs update for 1.21.10
    @ConfigEntry.Gui.Excluded
    public String bot_token = "";
    @ConfigEntry.Gui.Excluded
    public String record_channel = "";

    public static class MovementSettings {
        public double sv_friction = 0.35;
        public double sv_accelerate = 0.1;
        public double sv_airaccelerate = 1.0E99;
        public double sv_maxairspeed = 0.02325;
        public double speed_mul = 2.2;
        public double sv_gravity = 0.066;
        public double speed_coefficient = 1;
        public double speed_cap = 0.6;
    }
}
