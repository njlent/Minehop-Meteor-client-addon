# 26.1.2 update log

Target:

- Minecraft 26.1.2
- Fabric Loader 0.19.2
- Fabric API 0.146.1+26.1.2
- Loom 1.16-SNAPSHOT
- JDK 25

Sources checked:

- Fabric example mod `26.1.2` branch lists Minecraft 26.1.2, Loader 0.19.2, Loom 1.16-SNAPSHOT, Fabric API 0.146.1+26.1.2.
- Fabric Maven contains Fabric API `0.146.1+26.1.2`.
- Local Gradle toolchains list JDK 25 available via Gradle auto-provisioning; shell default Java is 21.

Movement preservation notes:

- Keep the `LivingEntity.travel` override semantics unchanged unless required by renamed Minecraft APIs.
- Preserve the current friction/acceleration/gravity defaults and calculations.
- Preserve no jump cooldown, custom jump Y velocity, speed cap, crouch-height adjustment, and collision toggle behavior.

Progress:

- [x] Created update log.
- [x] Verified target Fabric coordinates from current upstream metadata.
- [x] Update Gradle dependency/toolchain targets.
- [x] Fix Minecraft/Meteor API compile breaks.
- [x] Run full build gate.
- [x] Push completed commits.

Build metadata changes:

- Switched to Loom `1.16-SNAPSHOT` with plugin id `net.fabricmc.fabric-loom`.
- Switched Minecraft/Fabric/Meteor coordinates to 26.1.2 target stack.
- Removed Yarn dependency; 26.1.2 Fabric example uses Mojang-named Minecraft sources directly.
- Set Java compile/toolchain target to 25 and expanded mod metadata placeholders.
- Bumped Gradle wrapper from `9.2.0` to `9.4.0`; Loom `1.16-SNAPSHOT` requires plugin API `9.4.0`.
- Updated `publishEasyJar` to copy `jar`; Loom 1.16 setup has no `remapJar` task here.

Source port notes:

- Ported Minecraft imports from Yarn names to Mojang names.
- Mapped movement vectors `Vec3d` -> `Vec3`, `MathHelper` -> `Mth`, and velocity methods to `getDeltaMovement`/`setDeltaMovement`.
- Mapped player/network classes to `Player` and `ServerGamePacketListenerImpl`.
- Preserved current movement mechanics:
  - friction/acceleration/gravity formulas unchanged;
  - no jump cooldown still sets the new `noJumpDelay` field to `0`;
  - crouch height adjustment still raises by `STAND_HEIGHT - CROUCH_HEIGHT`;
  - custom jump still cancels vanilla jump and keeps horizontal velocity;
  - entity collision toggle still cancels player pushability.

Verification:

- `./gradlew build --stacktrace` passes with Gradle 9.4.0 and JDK 25 toolchain.
- Build output jar copied to `release/minehop-meteor-1.2.22.jar` and `release/minehop-meteor-latest.jar`.
- Bytecode sanity check confirms `LivingEntity.travel(Vec3)`, `jumpFromGround`, `handleOnClimbable`, and server move constants `100.0f`, `300.0f`, `100.0d` exist in the 26.1.2 deobf jar.
- Pushed all update increments to `origin/dev/26.1`.

Jump momentum fix:

- Restored player ground acceleration source by using `getSpeed()` for bunnyhop max ground velocity. The previous port read the private `LivingEntity.speed` field directly, but 26.1.2 player movement speed is exposed through `Player.getSpeed()`.
- Corrected the custom jump sync flag from `hurtMarked` to `needsSync`, matching the 26.1.2 vanilla jump path and the old `velocityDirty` behavior.
- Verified with `./gradlew build --stacktrace`.
