# Development Notes

## Project Structure

This is a minimal Meteor Client addon that implements Source Engine-style bunnyhopping movement.

### Active Files

#### Core Addon
- `src/main/java/net/nerdorg/minehop/MinehopAddon.java` - Main addon class
- `src/main/java/net/nerdorg/minehop/modules/Bunnyhopping.java` - Meteor module for GUI controls

#### Configuration
- `src/main/java/net/nerdorg/minehop/config/MinehopConfig.java` - Config data class
- `src/main/java/net/nerdorg/minehop/config/ConfigWrapper.java` - Config management

#### Mixins
- `src/main/java/net/nerdorg/minehop/mixin/LivingEntityMixin.java` - Core movement physics
- `src/main/java/net/nerdorg/minehop/mixin/EntityAccessor.java` - Entity field accessor
- `src/main/java/net/nerdorg/minehop/mixin/ServerPlayNetworkHandlerMixin.java` - Network handler

#### Resources
- `src/main/resources/fabric.mod.json` - Mod metadata
- `src/main/resources/minehop.mixins.json` - Mixin configuration
- `src/main/resources/assets/minehop/logo.png` - Mod icon

### Disabled Features

The following features from the original Minehop mod are disabled in this addon:

- **Blocks & Items**: Boost blocks, custom items (disabled due to Minecraft 1.21 registry changes)
- **Entities**: Custom entities like zones, reset entities, etc.
- **Commands**: All server-side commands
- **HUD Elements**: Speed displays, efficiency gauges, etc.
- **Client Mixins**: Rendering modifications, spectator HUD, etc.
- **Networking**: Custom packet handlers
- **Data Management**: Map data, records, replays

These features remain in the codebase but are not compiled or used. They can be re-enabled in future versions once the Minecraft 1.21 API changes are properly addressed.

## Building

```bash
./gradlew build
```

The output JAR will be in `build/libs/minehop-meteor-{version}.jar`

Note: The build directory is configured to use `~/.gradle-builds/minehop-meteor` to avoid issues with network drives.

## Version History

### 1.2.0 (Current)
- Moved to Movement category
- Fixed default movement values
- Improved config synchronization

### 1.1.0
- Initial Meteor addon release
- Converted from standalone Fabric mod

## Known Issues

1. **Fall Damage Toggle**: The fall damage setting doesn't work. Use Meteor's NoFall module instead.
2. **Boost Blocks**: Disabled due to registry initialization issues
3. **Unused Files**: Many files from the original mod remain in `src/` but are not compiled

## Future Improvements

- Clean up unused source files
- Re-enable boost blocks with proper registry handling
- Add HUD elements as Meteor HUD widgets
- Implement commands as Meteor commands
- Add multiplayer sync for server-side config overrides

## Testing

1. Build the addon
2. Copy JAR to `.minecraft/mods`
3. Launch Minecraft with Meteor Client
4. Open Meteor GUI (Right Shift)
5. Go to Movement category
6. Enable Bunnyhopping module
7. Test movement in-game

## Contributing

When making changes:
1. Test thoroughly in both singleplayer and multiplayer
2. Ensure config values sync properly between GUI and actual movement
3. Check that module enable/disable works correctly
4. Verify no crashes on startup or when joining servers

