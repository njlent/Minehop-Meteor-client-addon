plugins {
    alias(libs.plugins.fabric.loom)
}

// Set build directory to local filesystem to avoid network drive issues
layout.buildDirectory = file("builds")

base {
    archivesName = properties["archives_base_name"] as String
    version = libs.versions.mod.version.get()
    group = properties["maven_group"] as String
}

repositories {
    maven {
        name = "meteor-maven"
        url = uri("https://maven.meteordev.org/releases")
    }
    maven {
        name = "meteor-maven-snapshots"
        url = uri("https://maven.meteordev.org/snapshots")
    }
    maven {
        name = "shedaniel"
        url = uri("https://maven.shedaniel.me/")
    }
}

dependencies {
    // Fabric
    minecraft(libs.minecraft)
    mappings(variantOf(libs.yarn) { classifier("v2") })
    modImplementation(libs.fabric.loader)
    modImplementation(libs.fabric.api)

    // Meteor
    modImplementation(libs.meteor.client)

    // Additional dependencies
    modApi(libs.cloth.config) {
        exclude(group = "net.fabricmc.fabric-api")
    }
    implementation(libs.jda)
}

tasks {
    processResources {
        val propertyMap = mapOf(
            "version" to project.version,
            "mc_version" to libs.versions.minecraft.get()
        )

        inputs.properties(propertyMap)

        filteringCharset = "UTF-8"

        filesMatching("fabric.mod.json") {
            expand(propertyMap)
        }
    }

    jar {
        inputs.property("archivesName", project.base.archivesName.get())

        from("LICENSE") {
            rename { "${it}_${inputs.properties["archivesName"]}" }
        }
    }

    java {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }

    withType<JavaCompile> {
        options.encoding = "UTF-8"
        options.release = 21
        options.compilerArgs.add("-Xlint:deprecation")
        options.compilerArgs.add("-Xlint:unchecked")
    }
}

val publishEasyJar by tasks.registering {
    group = "build"
    description = "Copies the remapped jar into ./release for easy access."
    dependsOn("remapJar")
    doLast {
        val remapJarTask = tasks.named<org.gradle.jvm.tasks.Jar>("remapJar").get()
        val jarFile = remapJarTask.archiveFile.get().asFile
        val releaseDir = layout.projectDirectory.dir("release").asFile
        releaseDir.mkdirs()

        val versionedTarget = releaseDir.resolve(jarFile.name)
        jarFile.copyTo(versionedTarget, overwrite = true)

        val latestTarget = releaseDir.resolve("${project.base.archivesName.get()}-latest.jar")
        jarFile.copyTo(latestTarget, overwrite = true)

        logger.lifecycle("Copied versioned jar to: ${versionedTarget.absolutePath}")
        logger.lifecycle("Copied latest jar to: ${latestTarget.absolutePath}")
    }
}

tasks.named("build") {
    finalizedBy(publishEasyJar)
}

// Exclude disabled source files from compilation
sourceSets {
    main {
        java {
            exclude(
                // Old server-side mod initializer (replaced by MinehopAddon.java)
                "**/Minehop.java",

                // Disabled server-side features
                "**/commands/**",
                "**/data/**",
                "**/replays/**",
                "**/networking/**",
                "**/discord/**",
                "**/hns/**",
                "**/motd/**",
                "**/item/**",
                "**/block/**",

                // Disabled entity system (broken in 1.21)
                "**/entity/**",

                // Disabled rendering utilities (broken in 1.21)
                "**/render/**",

                // Disabled utilities except MovementUtil
                "**/util/ZoneUtil.java",
                "**/util/DiscordUtil.java",
                "**/util/DataUtil.java",

                // Disabled screen files (depend on DataManager)
                "**/screen/**",

                // Disabled client mixins that have 1.21 API issues or depend on disabled features
                "**/mixin/client/GameRendererMixin.java",      // Depends on ModItems
                "**/mixin/client/SoundSystemMixin.java",       // Depends on DataManager and ZoneUtil
                "**/mixin/client/KeyBindingMixin.java",        // References old Minehop class
                "**/mixin/client/PlayerEntityModelMixin.java", // 1.21 API changes
                "**/mixin/client/PlayerEntityRendererMixin.java", // Likely has 1.21 API issues
                "**/mixin/client/SpectatorHudMixin.java",      // May have issues
                "**/mixin/client/MultiplayerScreenMixin.java", // Not needed for core functionality

                // Keep enabled:
                // - MinehopAddon.java (main addon class)
                // - client/SqueedometerHud.java (HUD functionality - needs fixes)
                // - client/SpeedCalculator.java (HUD helper)
                // - mixin/client/InGameHudMixin.java (HUD rendering - needs fixes)
                // - mixin/LivingEntityMixin.java (movement logic)
                // - mixin/ServerPlayNetworkHandlerMixin.java (anti-cheat bypass)
                // - mixin/EntityAccessor.java (accessor)
                // - modules/Bunnyhopping.java (main module)
                // - config/ (configuration)
                // - util/MovementUtil.java
            )
        }
    }
}
