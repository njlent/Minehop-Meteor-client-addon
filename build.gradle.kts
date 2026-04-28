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
    implementation(libs.fabric.loader)
    implementation(libs.fabric.api)

    // Meteor
    implementation(libs.meteor.client)
}

tasks {
    processResources {
        val propertyMap = mapOf(
            "version" to project.version,
            "mc_version" to libs.versions.minecraft.get(),
            "loader_version" to libs.versions.fabric.loader.get(),
            "jdk_version" to libs.versions.jdk.get()
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

    withType<JavaCompile> {
        options.encoding = "UTF-8"
        options.release = libs.versions.jdk.get().toInt()
        options.compilerArgs.add("-Xlint:deprecation")
        options.compilerArgs.add("-Xlint:unchecked")
    }
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(libs.versions.jdk.get().toInt()))
    }

    sourceCompatibility = JavaVersion.VERSION_25
    targetCompatibility = JavaVersion.VERSION_25
}

val archiveBaseName = base.archivesName

val publishEasyJar by tasks.registering {
    group = "build"
    description = "Copies the built jar into ./release for easy access."
    dependsOn("jar")
    doLast {
        val jarTask = tasks.named<org.gradle.jvm.tasks.Jar>("jar").get()
        val jarFile = jarTask.archiveFile.get().asFile
        val releaseDir = layout.projectDirectory.dir("release").asFile
        releaseDir.mkdirs()

        val versionedTarget = releaseDir.resolve(jarFile.name)
        jarFile.copyTo(versionedTarget, overwrite = true)

        val latestTarget = releaseDir.resolve("${archiveBaseName.get()}-latest.jar")
        jarFile.copyTo(latestTarget, overwrite = true)

        logger.lifecycle("Copied versioned jar to: ${versionedTarget.absolutePath}")
        logger.lifecycle("Copied latest jar to: ${latestTarget.absolutePath}")
    }
}

tasks.named("build") {
    finalizedBy(publishEasyJar)
}
