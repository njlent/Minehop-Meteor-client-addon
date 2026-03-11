# Minehop Addon Docs

## Scope

Minimal Meteor Client addon. Current repo only ships the active bunnyhop path.

## Active Surface

### Core
- `src/main/java/net/nerdorg/minehop/MinehopAddon.java` - addon entrypoint
- `src/main/java/net/nerdorg/minehop/modules/Bunnyhopping.java` - Meteor module and settings

### Config
- `src/main/java/net/nerdorg/minehop/config/MinehopConfig.java` - in-memory settings model
- `src/main/java/net/nerdorg/minehop/config/ConfigWrapper.java` - config holder/bootstrap

### Mixins
- `src/main/java/net/nerdorg/minehop/mixin/LivingEntityMixin.java` - movement override
- `src/main/java/net/nerdorg/minehop/mixin/ServerPlayNetworkHandlerMixin.java` - local speed-check relax
- `src/main/java/net/nerdorg/minehop/mixin/EntityAccessor.java` - world accessor for the movement mixin

### Utility + Resources
- `src/main/java/net/nerdorg/minehop/util/MovementUtil.java` - movement math helpers
- `src/main/resources/fabric.mod.json` - addon metadata
- `src/main/resources/minehop.mixins.json` - enabled mixins
- `src/main/resources/assets/minehop/logo.png` - icon

## Removed Legacy Surface

Deleted from the active tree:
- old standalone Fabric mod initializer
- commands
- blocks/items/entities
- replay/data/networking systems
- HUD/client-render mixins
- Discord/MOTD/HNS helpers
- unused textures/models/lang assets

If any of this returns, rebuild it for the Meteor addon directly. Do not revive stale upstream code.

## Build

```bash
./gradlew build
```

Outputs:
- intermediate build artifacts in `builds/`
- remapped jars in `release/`

Current release task writes:
- `release/minehop-meteor-{version}.jar`
- `release/minehop-meteor-latest.jar`

## Verification

Terminal gate:

```bash
./gradlew build
```

Manual smoke:
1. Drop the latest jar into `.minecraft/mods`.
2. Launch Minecraft with Meteor Client.
3. Open Meteor GUI.
4. Enable `Bunnyhopping` under Movement.
5. Verify movement, crouch height option, collision toggle, and speed settings.

## Known Issues

- Boost blocks remain intentionally absent.
- Legacy HUD metrics remain intentionally absent.
- Build still shows one deprecation warning from `LivingEntityMixin` using `isChunkLoaded(BlockPos)`.

## Change Rule

Keep the addon narrow. Preserve working bunnyhop behavior first; only add new systems when they are needed and tested.
