plugins {
    java
}

group = "com.denizenscript"
version = "1.3.3-SNAPSHOT"

repositories {
    mavenLocal()
    maven("https://repo.papermc.io/repository/maven-public/")
    mavenCentral()
}

sourceSets {
    main {
        java { srcDir("src/main/java") }
        java { srcDir("src/denizen/java") }
    }
}

dependencies {
    compileOnly(project(":plugin"))
    compileOnly(project(":denizencore"))
    compileOnly("net.kyori:adventure-nbt:4.26.1")
    compileOnly("org.spigotmc:spigot-api:1.21.11-R0.2-SNAPSHOT")
    compileOnly("org.spigotmc:spigot:1.21.11-R0.2-SNAPSHOT:remapped-mojang")
    compileOnly("io.papermc.paper:paper-api:1.21.11-R0.1-SNAPSHOT")
}

val specialSource = configurations.create("specialSource")

dependencies {
    specialSource(files(rootProject.file("libs/SpecialSource-shaded.jar")))
}

val mojangMapsJar = tasks.jar.get().archiveFile

tasks.register<JavaExec>("remapObf") {
    dependsOn(tasks.jar)
    classpath = specialSource
    mainClass.set("net.md_5.specialsource.SpecialSource")

    val mapsFile = configurations.detachedConfiguration(
        dependencies.create(mapOf(
            "group" to "org.spigotmc",
            "name" to "minecraft-server",
            "version" to "1.21.11-R0.2-SNAPSHOT",
            "classifier" to "maps-mojang",
            "ext" to "txt"
        ))
    ).singleFile

    args(
        "--in-jar", mojangMapsJar.get().asFile.absolutePath,
        "--srg-in", mapsFile.absolutePath,
        "--out-jar", layout.buildDirectory.file("libs/${project.name}-${project.version}-remapped-obf.jar").get().asFile.absolutePath,
        "--reverse"
    )
}

tasks.register<JavaExec>("remapSpigot") {
    dependsOn("remapObf")
    classpath = specialSource
    mainClass.set("net.md_5.specialsource.SpecialSource")

    val mapsFile = configurations.detachedConfiguration(
        dependencies.create(mapOf(
            "group" to "org.spigotmc",
            "name" to "minecraft-server",
            "version" to "1.21.11-R0.2-SNAPSHOT",
            "classifier" to "maps-spigot",
            "ext" to "csrg"
        ))
    ).singleFile

    args(
        "--in-jar", layout.buildDirectory.file("libs/${project.name}-${project.version}-remapped-obf.jar").get().asFile.absolutePath,
        "--srg-in", mapsFile.absolutePath,
        "--out-jar", layout.buildDirectory.file("libs/${project.name}-${project.version}.jar").get().asFile.absolutePath
    )
}

tasks.build {
    dependsOn("remapSpigot")
}

tasks.withType<JavaCompile> {
    options.release.set(21)
    options.encoding = "UTF-8"
}

configurations.apiElements.get().outgoing.artifacts.clear()
configurations.runtimeElements.get().outgoing.artifacts.clear()

val remappedJar = layout.buildDirectory.file("libs/${project.name}-${project.version}.jar")

artifacts {
    add("runtimeElements", remappedJar) {
        builtBy("remapSpigot")
    }
    add("apiElements", remappedJar) {
        builtBy("remapSpigot")
    }
}