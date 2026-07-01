import org.apache.tools.ant.filters.ReplaceTokens

plugins { java }

group = "com.denizenscript"
version = "1.3.3-SNAPSHOT"

repositories {
    mavenLocal()
    maven("https://maven.citizensnpcs.co/repo")
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://jitpack.io")
    mavenCentral()
}

sourceSets {
    main {
        java { srcDir("src/main/java") }
        java { srcDir("src/denizen/java") }
        resources { srcDir("src/denizen/resources") }
    }
}

tasks.processResources {
    filesMatching("plugin.yml") {
        filter(
            ReplaceTokens::class, "tokens" to mapOf(
                "denizenVersion" to (rootProject.findProperty("denizenVersion")?.toString() ?: "1.3.3-SNAPSHOT"),
                "buildNumber" to (rootProject.findProperty("buildNumber")?.toString() ?: "custom"),
                "buildClass" to (rootProject.findProperty("buildClass")?.toString() ?: "DEV")
            )
        )
    }
}

dependencies {
    compileOnly(project(":denizencore"))

    compileOnly("org.spigotmc:spigot-api:26.2-R0.1-SNAPSHOT")

    compileOnly("net.citizensnpcs:citizens-main:2.0.41-SNAPSHOT") { isTransitive = false }

    compileOnly("com.github.MilkBowl:VaultAPI:1.7")
    compileOnly("net.kyori:adventure-nbt:4.26.1")
    compileOnly("it.unimi.dsi:fastutil-core:8.5.8")
    compileOnly("org.json:json:20231013")
}

allprojects {
    tasks.withType<JavaCompile> {
        options.release.set(21)
        options.encoding = "UTF-8"
    }
}