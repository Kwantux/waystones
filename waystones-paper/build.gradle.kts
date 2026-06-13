import net.minecrell.pluginyml.bukkit.BukkitPluginDescription

plugins {
    alias(libs.plugins.paper.run)
    alias(libs.plugins.plugin.yml)
    alias(libs.plugins.shadow)
}

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://repo.opencollab.dev/maven-snapshots/")
    maven("https://repo.opencollab.dev/maven-releases/")
    maven("https://repo.codemc.io/repository/maven-snapshots/")
    maven("https://repo.dmulloy2.net/repository/public/")
}

dependencies {
    bukkitLibrary(libs.cloud.paper)
    bukkitLibrary(libs.sqlite)
    compileOnly(libs.paper)
    compileOnly(libs.floodgate.api)
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(26))
}

tasks {
    runServer {
        minecraftVersion("26.1.2")

        downloadPlugins {
            url("https://download.geysermc.org/v2/projects/floodgate/versions/latest/builds/latest/downloads/spigot")
            url("https://download.geysermc.org/v2/projects/geyser/versions/latest/builds/latest/downloads/spigot")
        }
    }

    shadowJar {
        fun reloc(pkg: String, name: String) = relocate(pkg, "com.kalimero2.team.waystones.paper.shaded.$name")
    }
}

bukkit {
    main = "com.kalimero2.team.waystones.paper.PaperWayStones"
    apiVersion = "1.20"
    load = BukkitPluginDescription.PluginLoadOrder.POSTWORLD
    authors = listOf("byquanton", "kwantux")
    softDepend = listOf("floodgate", "claims-paper", "resource-pack-loader")
}