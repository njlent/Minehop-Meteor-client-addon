// ORIGINAL BY hatninja ON GITHUB

package net.nerdorg.minehop.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.nerdorg.minehop.config.ConfigWrapper;
import net.nerdorg.minehop.config.MinehopConfig;
import net.nerdorg.minehop.util.MovementUtil; // REQUIRED for movement calculations
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {
    @Shadow public float xxa;
    @Shadow public float zza;
    @Shadow private int noJumpDelay;
    @Shadow protected boolean jumping;

    @Invoker("handleOnClimbable")
    protected abstract Vec3 invokeHandleOnClimbable(Vec3 velocity);

    @Shadow protected abstract float getJumpPower();

    @Shadow public abstract boolean onClimbable();

    @Shadow public abstract boolean isFallFlying();

    @Shadow protected abstract void updateWalkAnimation(float speed);

    @Shadow public abstract float getYHeadRot();

    @Shadow public abstract boolean hasEffect(Holder<MobEffect> effect);
    @Shadow public abstract MobEffectInstance getEffect(Holder<MobEffect> effect);
    @Shadow public abstract float getSpeed();

    public float prevHeadYaw;
    public int stuckArrowTimer;
    protected float field_6215;

    private boolean wasOnGround;
    private boolean wasCrouching = false;
    private static final float STAND_HEIGHT = 1.8f;
    private static final float CROUCH_HEIGHT = 1.35f;

    public LivingEntityMixin(EntityType<?> type, Level level) {
        super(type, level);
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
    public void travel(Vec3 movementInput, CallbackInfo ci) {
        var config = ConfigWrapper.config;
        if (config == null) { return; }
        double speedCap = config.movement.speed_cap;
        if (!config.enabled) { return; }
        if (this.getType() != EntityType.PLAYER) { return; }
        Player player = (Player)(Object)this;
        if (!player.canSimulateMovement()) { return; }
        if (this.isInWater() || this.isInLava() || this.isFallFlying()) { return; }

        if (MovementUtil.isFlying(player)) { return; }

        double baseFriction = config.movement.sv_friction;
        double activeFriction = this.isShiftKeyDown() ? 0.85 : baseFriction;

        //Reverse multiplication done by the function that calls this one.
        this.xxa /= 0.98F;
        this.zza /= 0.98F;
        double sI = movementInput.x / 0.98F;
        double fI = movementInput.z / 0.98F;

        //Have no jump cooldown, why not?
        this.noJumpDelay = 0;

        //Get Slipperiness and Movement speed.
        BlockPos blockPos = this.getBlockPosBelowThatAffectsMyMovement();
        Level entityWorld = this.level();
        float slipperiness = entityWorld.getBlockState(blockPos).getBlock().getFriction();
        float friction = 1-(slipperiness*slipperiness);

        if (config.crouch_height_adjustment) {
            boolean isCrouching = this.isShiftKeyDown();
            if (isCrouching && !wasCrouching) {
                if (entityWorld.noCollision(this, this.getBoundingBox().inflate(0.0, STAND_HEIGHT - CROUCH_HEIGHT, 0.0))) {
                    this.setPos(this.getX(), this.getY() + (STAND_HEIGHT - CROUCH_HEIGHT), this.getZ());
                }
            }
            wasCrouching = isCrouching;
        }

        //
        //Apply Friction
        //
        boolean fullGrounded = this.wasOnGround && this.onGround();
        if (fullGrounded) {
            Vec3 velFin = this.getDeltaMovement();
            Vec3 horFin = new Vec3(velFin.x,0.0F,velFin.z);
            float speed = (float) horFin.length();
            if (speed > 0.001F) {
                float drop = 0.0F;

                drop += (speed * activeFriction * friction);

                float newspeed = Math.max(speed - drop, 0.0F);
                newspeed /= speed;
                this.setDeltaMovement(
                        horFin.x * newspeed,
                        velFin.y,
                        horFin.z * newspeed
                );
            }
        }
        this.wasOnGround = this.onGround();

        //
        // Accelerate
        //
        float yawDifference = Mth.wrapDegrees(this.getYHeadRot() - this.prevHeadYaw);
        if (yawDifference < 0) {
            yawDifference = yawDifference * -1;
        }

        if (!fullGrounded && !this.onClimbable()) {
            sI = sI * yawDifference;
            fI = fI * yawDifference;
        }

        double perfectAngle = findOptimalStrafeAngle(sI, fI, config, fullGrounded);

        if (sI != 0.0F || fI != 0.0F) {
            Vec3 moveDir = MovementUtil.movementInputToVelocity(new Vec3(sI, 0.0F, fI), 1.0F, this.getYRot());
            Vec3 accelVec = this.getDeltaMovement();

            double projVel = new Vec3(accelVec.x, 0.0F, accelVec.z).dot(moveDir);
            double accelVel = (this.onGround() ? config.movement.sv_accelerate : (config.movement.sv_airaccelerate));

            float maxVel;
            double angleBetween = 0;
            if (fullGrounded) {
                maxVel = (float) (this.getSpeed() * config.movement.speed_mul);
            } else {
                double velVal = this.getDeltaMovement().horizontalDistance();
                if (velVal < 0 || velVal > 0)
                    maxVel = (float) (config.movement.sv_maxairspeed * ((velVal * config.movement.speed_coefficient) / velVal));
                else
                    maxVel = (float) (config.movement.sv_maxairspeed);

                angleBetween = Math.acos(accelVec.normalize().dot(moveDir.normalize()));

                maxVel *= (float) (angleBetween * angleBetween * angleBetween);
            }

            if (projVel + accelVel > maxVel) {
                accelVel = maxVel - projVel;
            }
            Vec3 accelDir = moveDir.scale(Math.max(accelVel, 0.0F));

            Vec3 newVelocity = accelVec.add(accelDir);
            Vec3 newHorizontalVelocity = newVelocity;

            double currentHorizontalSpeed = newHorizontalVelocity.horizontalDistance();

            if (currentHorizontalSpeed > speedCap && !fullGrounded) {
                newHorizontalVelocity = newHorizontalVelocity.scale(speedCap / currentHorizontalSpeed);
            }

            this.setDeltaMovement(new Vec3(newHorizontalVelocity.x(), newVelocity.y(), newHorizontalVelocity.z()));
        }

        this.setDeltaMovement(this.invokeHandleOnClimbable(this.getDeltaMovement()));
        this.move(MoverType.SELF, this.getDeltaMovement());

        Vec3 preVel = this.getDeltaMovement();
        if ((this.horizontalCollision || this.jumping) && this.onClimbable()) {
            preVel = new Vec3(preVel.x * 0.7D, 0.2D, preVel.z * 0.7D);
        }

        double yVel = preVel.y;
        double gravity = config.movement.sv_gravity;
        if (preVel.y <= 0.0D && this.hasEffect(MobEffects.SLOW_FALLING)) {
            gravity = 0.01D;
            this.fallDistance = 0.0D;
        }
        if (this.hasEffect(MobEffects.LEVITATION)) {
            var levitation = this.getEffect(MobEffects.LEVITATION);
            if (levitation != null) {
                yVel += (0.05D * (levitation.getAmplifier() + 1) - preVel.y) * 0.2D;
            }
            this.fallDistance = 0.0D;
        } else if (!entityWorld.isClientSide() && !entityWorld.isLoaded(blockPos)) {
            yVel = 0.0D;
        } else if (!this.isNoGravity()) {
            yVel -= gravity;
        }

        this.setDeltaMovement(preVel.x,yVel,preVel.z);

        this.updateWalkAnimation((float) this.getDeltaMovement().horizontalDistance());
        ci.cancel();
    }

    public double findOptimalStrafeAngle(double sI, double fI, MinehopConfig config, boolean fullGrounded) {
        double highestVelocity = -Double.MAX_VALUE;
        double optimalAngle = 0;
        float currentYaw = this.getYRot();
        for (double angle = currentYaw - 45; angle < currentYaw + 45; angle += 1) {
            Vec3 moveDir = MovementUtil.movementInputToVelocity(new Vec3(sI, 0.0F, fI), 1.0F, (float) angle);
            Vec3 accelVec = this.getDeltaMovement();

            double projVel = new Vec3(accelVec.x, 0.0F, accelVec.z).dot(moveDir);
            double accelVel = (this.onGround() ? config.movement.sv_accelerate : (config.movement.sv_airaccelerate));

            float maxVel;
            if (fullGrounded) {
                maxVel = (float) (this.getSpeed() * config.movement.speed_mul);
            } else {
                maxVel = (float) (config.movement.sv_maxairspeed);

                double angleBetween = Math.acos(accelVec.normalize().dot(moveDir.normalize()));

                maxVel *= (float) (angleBetween * angleBetween * angleBetween);
            }

            if (projVel + accelVel > maxVel) {
                accelVel = maxVel - projVel;
            }
            Vec3 accelDir = moveDir.scale(Math.max(accelVel, 0.0F));

            Vec3 newVelocity = accelVec.add(accelDir);

            if (newVelocity.horizontalDistance() > highestVelocity) {
                highestVelocity = newVelocity.horizontalDistance();
                optimalAngle = angle;
            }
        }
        return optimalAngle;
    }

    @Override
    public EntityDimensions getDimensions(Pose pose) {
        EntityDimensions original = super.getDimensions(pose);

        if (ConfigWrapper.config != null && ConfigWrapper.config.crouch_height_adjustment && this.isShiftKeyDown()) {
            return EntityDimensions.scalable(original.width(), CROUCH_HEIGHT);
        }
        return original;
    }

    @Inject(method = "jumpFromGround", at = @At("HEAD"), cancellable = true)
    void jump(CallbackInfo ci) {
        var config = ConfigWrapper.config;
        if (config == null) { return; }
        if (!config.enabled) { return; }

        Vec3 vecFin = this.getDeltaMovement();
        double yVel = this.getJumpPower();

        this.setDeltaMovement(vecFin.x, yVel, vecFin.z);
        this.needsSync = true;

        ci.cancel();
    }
}
