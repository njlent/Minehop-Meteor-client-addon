// ORIGINAL BY hatninja ON GITHUB

package net.nerdorg.minehop.mixin;

import net.minecraft.entity.*;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.nerdorg.minehop.config.ConfigWrapper;
import net.nerdorg.minehop.config.MinehopConfig;
import net.nerdorg.minehop.util.MovementUtil; // REQUIRED for movement calculations
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {
    @Shadow private float movementSpeed;
    @Shadow public float sidewaysSpeed;
    @Shadow public float forwardSpeed;
    @Shadow private int jumpingCooldown;
    @Shadow protected boolean jumping;

    @Shadow protected abstract Vec3d applyClimbingSpeed(Vec3d velocity);
    @Shadow protected abstract float getJumpVelocity();
    @Shadow public abstract boolean isClimbing();
    @Shadow public abstract boolean isGliding();

    @Shadow public abstract float getYaw(float tickDelta);

    @Shadow public abstract void updateLimbs(boolean flutter);

    @Shadow public abstract float getHeadYaw();

    @Shadow public abstract boolean isHoldingOntoLadder();

    @Shadow protected abstract boolean shouldSwimInFluids();

    @Shadow public abstract boolean hasStatusEffect(RegistryEntry<StatusEffect> effect);
    @Shadow public abstract StatusEffectInstance getStatusEffect(RegistryEntry<StatusEffect> effect);

    public float prevHeadYaw;
    public int stuckArrowTimer;
    protected float field_6215;

    private boolean wasOnGround;
    private boolean wasCrouching = false;
    private static final float STAND_HEIGHT = 1.8f;
    private static final float CROUCH_HEIGHT = 1.35f;

    public LivingEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Inject(method = "isPushable", at = @At("HEAD"), cancellable = true)
    public void isPushable(CallbackInfoReturnable<Boolean> cir) {
        var config = ConfigWrapper.config;
        if (config != null && config.enabled && !config.entity_collisions && this.getType() == EntityType.PLAYER) {
            cir.setReturnValue(false);
        }
    }

    /**
     * @Author lolrow and Plaaasma
     * @Reason Source-style movement override for the active Meteor bunnyhop path.
     */

    @Inject(method = "travel", at = @At("HEAD"), cancellable = true)
    public void travel(Vec3d movementInput, CallbackInfo ci) {
        var config = ConfigWrapper.config;
        if (config == null) { return; }
        double speedCap = config.movement.speed_cap;
        if (!config.enabled) { return; }
        if (this.getType() != EntityType.PLAYER) { return; }
        if (!this.canMoveVoluntarily() && !this.isLogicalSideForUpdatingMovement()) { return; }
        if (this.isTouchingWater() || this.isInLava() || this.isGliding()) { return; }

        LivingEntity self = (LivingEntity)(Object)this;
        if (this.getType() == EntityType.PLAYER && MovementUtil.isFlying((PlayerEntity) self)) { return; }

        double baseFriction = config.movement.sv_friction;
        double activeFriction = this.isSneaking() ? 0.85 : baseFriction;

        //Reverse multiplication done by the function that calls this one.
        this.sidewaysSpeed /= 0.98F;
        this.forwardSpeed /= 0.98F;
        double sI = movementInput.x / 0.98F;
        double fI = movementInput.z / 0.98F;

        //Have no jump cooldown, why not?
        this.jumpingCooldown = 0;

        //Get Slipperiness and Movement speed.
        BlockPos blockPos = this.getVelocityAffectingPos();
        World entityWorld = ((EntityAccessor) self).getWorldField();
        float slipperiness = entityWorld.getBlockState(blockPos).getBlock().getSlipperiness();
        float friction = 1-(slipperiness*slipperiness);

        if (config.crouch_height_adjustment) {
            boolean isCrouching = this.isSneaking();
            if (isCrouching && !wasCrouching) {
                if (entityWorld.isSpaceEmpty(this, this.getBoundingBox().expand(0.0, STAND_HEIGHT - CROUCH_HEIGHT, 0.0))) {
                    this.setPosition(this.getX(), this.getY() + (STAND_HEIGHT - CROUCH_HEIGHT), this.getZ());
                }
            }
            wasCrouching = isCrouching;
        }

        //
        //Apply Friction
        //
        boolean fullGrounded = this.wasOnGround && this.isOnGround();
        if (fullGrounded) {
            Vec3d velFin = this.getVelocity();
            Vec3d horFin = new Vec3d(velFin.x,0.0F,velFin.z);
            float speed = (float) horFin.length();
            if (speed > 0.001F) {
                float drop = 0.0F;

                drop += (speed * activeFriction * friction);

                float newspeed = Math.max(speed - drop, 0.0F);
                newspeed /= speed;
                this.setVelocity(
                        horFin.x * newspeed,
                        velFin.y,
                        horFin.z * newspeed
                );
            }
        }
        this.wasOnGround = this.isOnGround();

        //
        // Accelerate
        //
        float yawDifference = MathHelper.wrapDegrees(this.getHeadYaw() - this.prevHeadYaw);
        if (yawDifference < 0) {
            yawDifference = yawDifference * -1;
        }

        if (!fullGrounded && !this.isClimbing()) {
            sI = sI * yawDifference;
            fI = fI * yawDifference;
        }

        double perfectAngle = findOptimalStrafeAngle(sI, fI, config, fullGrounded);

        if (sI != 0.0F || fI != 0.0F) {
            Vec3d moveDir = MovementUtil.movementInputToVelocity(new Vec3d(sI, 0.0F, fI), 1.0F, this.getYaw());
            Vec3d accelVec = this.getVelocity();

            double projVel = new Vec3d(accelVec.x, 0.0F, accelVec.z).dotProduct(moveDir);
            double accelVel = (this.isOnGround() ? config.movement.sv_accelerate : (config.movement.sv_airaccelerate));

            float maxVel;
            double angleBetween = 0;
            if (fullGrounded) {
                maxVel = (float) (this.movementSpeed * config.movement.speed_mul);
            } else {
                double velVal = this.getVelocity().horizontalLength();
                if (velVal < 0 || velVal > 0)
                    maxVel = (float) (config.movement.sv_maxairspeed * ((velVal * config.movement.speed_coefficient) / velVal));
                else
                    maxVel = (float) (config.movement.sv_maxairspeed);

                angleBetween = Math.acos(accelVec.normalize().dotProduct(moveDir.normalize()));

                maxVel *= (float) (angleBetween * angleBetween * angleBetween);
            }

            if (projVel + accelVel > maxVel) {
                accelVel = maxVel - projVel;
            }
            Vec3d accelDir = moveDir.multiply(Math.max(accelVel, 0.0F));

            Vec3d newVelocity = accelVec.add(accelDir);
            Vec3d newHorizontalVelocity = newVelocity;

            double currentHorizontalSpeed = newHorizontalVelocity.horizontalLength();

            if (currentHorizontalSpeed > speedCap && !fullGrounded) {
                newHorizontalVelocity = newHorizontalVelocity.multiply(speedCap / currentHorizontalSpeed);
            }

            this.setVelocity(new Vec3d(newHorizontalVelocity.getX(), newVelocity.getY(), newHorizontalVelocity.getZ()));
        }

        this.setVelocity(this.applyClimbingSpeed(this.getVelocity()));
        this.move(MovementType.SELF, this.getVelocity());

        Vec3d preVel = this.getVelocity();
        if ((this.horizontalCollision || this.jumping) && this.isClimbing()) {
            preVel = new Vec3d(preVel.x * 0.7D, 0.2D, preVel.z * 0.7D);
        }

        double yVel = preVel.y;
        double gravity = config.movement.sv_gravity;
        if (preVel.y <= 0.0D && this.hasStatusEffect(StatusEffects.SLOW_FALLING)) {
            gravity = 0.01D;
            this.fallDistance = 0.0F;
        }
        if (this.hasStatusEffect(StatusEffects.LEVITATION)) {
            var levitation = this.getStatusEffect(StatusEffects.LEVITATION);
            if (levitation != null) {
                yVel += (0.05D * (levitation.getAmplifier() + 1) - preVel.y) * 0.2D;
            }
            this.fallDistance = 0.0F;
        } else if (!entityWorld.isClient() && !entityWorld.isChunkLoaded(blockPos)) {
            yVel = 0.0D;
        } else if (!this.hasNoGravity()) {
            yVel -= gravity;
        }

        this.setVelocity(preVel.x,yVel,preVel.z);

        this.updateLimbs(self instanceof Flutterer);
        ci.cancel();
    }

    public double findOptimalStrafeAngle(double sI, double fI, MinehopConfig config, boolean fullGrounded) {
        double highestVelocity = -Double.MAX_VALUE;
        double optimalAngle = 0;
        float currentYaw = this.getYaw();
        for (double angle = currentYaw - 45; angle < currentYaw + 45; angle += 1) {
            Vec3d moveDir = MovementUtil.movementInputToVelocity(new Vec3d(sI, 0.0F, fI), 1.0F, (float) angle);
            Vec3d accelVec = this.getVelocity();

            double projVel = new Vec3d(accelVec.x, 0.0F, accelVec.z).dotProduct(moveDir);
            double accelVel = (this.isOnGround() ? config.movement.sv_accelerate : (config.movement.sv_airaccelerate));

            float maxVel;
            if (fullGrounded) {
                maxVel = (float) (this.movementSpeed * config.movement.speed_mul);
            } else {
                maxVel = (float) (config.movement.sv_maxairspeed);

                double angleBetween = Math.acos(accelVec.normalize().dotProduct(moveDir.normalize()));

                maxVel *= (float) (angleBetween * angleBetween * angleBetween);
            }

            if (projVel + accelVel > maxVel) {
                accelVel = maxVel - projVel;
            }
            Vec3d accelDir = moveDir.multiply(Math.max(accelVel, 0.0F));

            Vec3d newVelocity = accelVec.add(accelDir);

            if (newVelocity.horizontalLength() > highestVelocity) {
                highestVelocity = newVelocity.horizontalLength();
                optimalAngle = angle;
            }
        }
        return optimalAngle;
    }

    @Override
    public EntityDimensions getDimensions(EntityPose pose) {
        EntityDimensions original = super.getDimensions(pose);

        if (ConfigWrapper.config != null && ConfigWrapper.config.crouch_height_adjustment && this.isSneaking()) {
            return EntityDimensions.changing(original.width(), CROUCH_HEIGHT);
        }
        return original;
    }

    @Inject(method = "jump", at = @At("HEAD"), cancellable = true)
    void jump(CallbackInfo ci) {
        var config = ConfigWrapper.config;
        if (config == null) { return; }
        if (!config.enabled) { return; }

        Vec3d vecFin = this.getVelocity();
        double yVel = this.getJumpVelocity();

        this.setVelocity(vecFin.x, yVel, vecFin.z);
        this.velocityDirty = true;

        ci.cancel();
    }
}
