package net.nerdorg.minehop.entity.custom;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageSources;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.nerdorg.minehop.MinehopAddon;
import net.nerdorg.minehop.data.DataManager;
import net.nerdorg.minehop.networking.PacketHandler;
import net.nerdorg.minehop.util.Logger;

import java.util.List;

public class Zone extends MobEntity {
    private String paired_map = "";
    protected World zoneWorld;

    public Zone(EntityType<? extends MobEntity> entityType, World world) {
        super(entityType, world);
        this.zoneWorld = world;
    }

    public World getWorld() {
        return this.zoneWorld;
    }



    @Override
    public boolean isAttackable() {
        return false;
    }

    @Override
    public boolean cannotDespawn() {
        return true;
    }

    @Override
    public boolean hasNoGravity() {
        return true;
    }

    @Override
    public boolean isPersistent() {
        return true;
    }

    @Override
    public boolean isPushable() {
        return false;
    }

    @Override
    public boolean collidesWith(Entity other) {
        return false;
    }

    public boolean damage(DamageSource source, float amount) {
        if (source.isOf(DamageTypes.GENERIC_KILL)) {
            return super.damage((ServerWorld) this.getWorld(), source, amount);
        }
        else {
            return false;
        }
    }

    public boolean isPushedByFluids() {
        return false;
    }

    protected void pushAway(Entity entity) {
    }

    public boolean doesNotCollide(double offsetX, double offsetY, double offsetZ) {
        return true;
    }

    public void onPlayerCollision(PlayerEntity player) { }

    public void writeCustomDataToNbt(NbtCompound nbt) {
        nbt.putString("map", paired_map);
    }

    public void readCustomDataFromNbt(NbtCompound nbt) {
        paired_map = nbt.getString("map").orElse("");
    }

    public String getPairedMap() {
        return paired_map;
    }

    public void setPairedMap(String paired_map) {
        this.paired_map = paired_map;
    }
}
