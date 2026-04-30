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
- [ ] Update Gradle dependency/toolchain targets.
- [ ] Fix Minecraft/Meteor API compile breaks.
- [ ] Run full build gate.
- [ ] Push completed commits.
