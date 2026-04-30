<div align="center">
 <h1> <img src="img/icon.png" width="200px"><br/>Minehop Addon</h1>

 <img src="https://img.shields.io/badge/Meteor Client Addon-6f1ab1?logo=meteor&logoColor=white"/> 
 <br>
 <img src="https://img.shields.io/badge/minecraft-26.1.2(dev)-green"/> 
 <img src="https://img.shields.io/badge/minecraft-1.21.11-darkgreen"/> 
 <img src="https://img.shields.io/badge/minecraft-1.21.10-darkgreen"/> 
</div>
<br/>

A [Meteor Client](https://github.com/MeteorDevelopment/meteor-client) addon that adds Source Engine-style bunnyhopping movement mechanics to Minecraft. <br>
Based on the original [Minehop mod](https://github.com/Plaaasma/minehop-fabric-public).

## Supported versions: 
- **Minecraft 26.1.2 (wip)** ([latest](https://github.com/njlent/minehop-Meteor-client-addon/releases))
- **Minecraft 1.21.11** ([up to v1.2.21](https://github.com/njlent/Minehop-Meteor-client-addon/releases/tag/v1.2.21))
- **Minecraft 1.21.10** ([up to v1.2.1](https://github.com/njlent/minehop-Meteor-client-addon/releases/tag/v1.2.1))

## Features

- **Source Engine Movement Physics**: Implements CS:S/CS:GO style bunnyhopping with air strafing
- **Fully Configurable**: Adjust friction, acceleration, gravity, and other movement parameters in real-time
- **Meteor Integration**: Seamlessly integrates with Meteor Client's module system
- **Easy Toggle**: Enable/disable bunnyhopping with a simple keybind or GUI toggle

## Installation

### Prerequisites
- Minecraft 26.1.2
- [Fabric Loader](https://fabricmc.net/use/) 0.19.2+
- [Fabric API](https://modrinth.com/mod/fabric-api) 0.146.1+26.1.2+
- [Meteor Client](https://meteorclient.com/) 26.1.2-SNAPSHOT
- JAVA 25+


## Usage

### Enabling Bunnyhopping
1. Open Meteor GUI (Right Shift)
2. Go to **Movement** category
3. Toggle **Bunnyhopping** ON

### Movement Controls
- **Jump**: Space (hold for continuous jumping)
- **Strafe**: A/D while in the air
- **Forward**: W (minimal input for best results)

### Tips for Effective Bunnyhopping
1. **Timing**: Jump right as you land to maintain speed
2. **Air Strafing**: Move your mouse smoothly left/right while holding A/D in the air
3. **Minimal Forward Input**: Use W sparingly - most speed comes from strafing
4. **Practice**: Bunnyhopping takes practice to master!

## Configuration

All settings can be adjusted in the Meteor GUI under Movement > Bunnyhopping:

### General Settings
- **Crouch Height Adjustment**: Enable/disable enhanced crouch height adjustment (default: ON)
  - When enabled: Player height changes from 1.8 blocks (standing) to **1.35 blocks** (crouching)
  - When disabled: Vanilla behavior - 1.8 blocks (standing) to **1.5 blocks** (crouching)
  - Enhanced mode provides lower crouch for better movement physics
- **Entity Collisions**: Enable/disable player-to-player collisions while bunnyhopping is active (default: ON)
- **Speed Cap**: Maximum speed multiplier (default: 0.6)

### Movement Settings
- **SV Friction**: Ground friction coefficient (default: 0.35)
  - Lower = more slippery, higher = more grip
- **SV Accelerate**: Ground acceleration (default: 0.1)
  - How quickly you accelerate on the ground
- **SV Air Accelerate**: Air acceleration for strafe control (default: 1.0E99)
  - Essentially infinite for Source-style air control
- **SV Max Air Speed**: Maximum air speed (default: 0.02325)
  - Limits how much speed you can gain from air strafing
- **SV Gravity**: Gravity multiplier (default: 0.066)
  - Lower = floatier, higher = faster falling

### Advanced Settings
- **Speed Multiplier**: Overall speed multiplier (default: 2.2)
  - Scales all movement speeds
- **Speed Coefficient**: Speed calculation coefficient (default: 1.0)
  - Fine-tune speed calculations

<br>
<br>
<br>

> [!IMPORTANT]
> Check out my other Meteor addons:
>
> <table>
>   <tr>
>     <td valign="middle">
>       <img src="https://raw.githubusercontent.com/njlent/Wurstmeteor-Meteor-client-addon/refs/heads/master/img/icon.png" width="50" alt="WurstMeteor Addon icon">
>     </td>
>     <td valign="middle">
>       <a href="https://github.com/njlent/Wurstmeteor-Meteor-client-addon">WurstMeteor Addon - ports selected Wurst Client features</a>
>     </td>
>   </tr>
> </table>
