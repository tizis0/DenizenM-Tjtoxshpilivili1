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
    compileOnly(project(":paper"))
    compileOnly(project(":denizencore"))
    compileOnly("net.kyori:adventure-nbt:4.26.1")
    compileOnly("org.spigotmc:spigot-api:26.1.2-R0.1-SNAPSHOT")
    compileOnly("org.spigotmc:spigot:26.1.2-R0.1-SNAPSHOT")
    compileOnly("io.papermc.paper:paper-api:26.1.2.build.70-stable")
}

allprojects {
    tasks.withType<JavaCompile> {
        options.release.set(21)
        options.encoding = "UTF-8"
    }
}