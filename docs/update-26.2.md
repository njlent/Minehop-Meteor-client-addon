# 26.2 update log

Target:

- Minecraft 26.2
- Meteor Client 26.2-SNAPSHOT
- Fabric Loader 0.19.3
- Fabric API 0.154.2+26.2
- Loom 1.17-SNAPSHOT
- Gradle 9.6.1
- JDK 25

Sources checked:

- Meteor Client `master` commit [`01334e10`](https://github.com/MeteorDevelopment/meteor-client/commit/01334e10b93b516c7811a2d5296dddced5215e33) uses this Minecraft, Fabric, Loom, Gradle, and JDK stack.
- The published Meteor dependency for this target is `meteordevelopment:meteor-client:26.2-SNAPSHOT`.

Source port:

- Moved player entity type references from `EntityType.PLAYER` to `EntityTypes.PLAYER`, matching Minecraft and Meteor Client 26.2.
- Kept the movement formulas, defaults, jump behavior, speed cap, crouch-height adjustment, and collision toggle unchanged.

Verification:

- `./gradlew clean build --stacktrace` passes with Gradle 9.6.1 and the JDK 25 toolchain.
- The build produces `release/minehop-meteor-1.2.23+26.2.jar` and `release/minehop-meteor-latest.jar`.
- The 26.2 deobfuscated Minecraft jar still contains `LivingEntity.travel(Vec3)`, `jumpFromGround`, `handleOnClimbable`, the movement fields used by the mixin, and server movement constants `100.0f`, `300.0f`, and `100.0d`.
- In-game verification remains manual before a pull request or release.
