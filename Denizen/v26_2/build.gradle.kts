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
    implementation("net.kyori:adventure-nbt:4.26.1")
    compileOnly("org.spigotmc:spigot-api:26.2-R0.1-SNAPSHOT")
    compileOnly("org.spigotmc:spigot:26.2-R0.1-SNAPSHOT")
    compileOnly("io.papermc.paper:paper-api:26.2.build.36-alpha")
}

allprojects {
    tasks.withType<JavaCompile> {
        options.release.set(21)
        options.encoding = "UTF-8"
    }
}