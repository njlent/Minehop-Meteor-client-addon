package net.nerdorg.minehop.util;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

public class MovementUtil {
    public static Vec3 movementInputToVelocity(Vec3 movementInput, float speed, float yaw) {
        double d = movementInput.lengthSqr();
        Vec3 vec3 = (d > 1.0D ? movementInput.normalize() : movementInput).scale(speed);
        float f = Mth.sin(yaw * 0.017453292F);
        float g = Mth.cos(yaw * 0.017453292F);
        return new Vec3(vec3.x * (double)g - vec3.z * (double)f, vec3.y, vec3.z * (double)g + vec3.x * (double)f);
    }

    public static boolean isFlying(Player player) {
        return player != null && player.getAbilities().flying;
    }
}
