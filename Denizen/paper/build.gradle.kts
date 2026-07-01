plugins { java }

group = "com.denizenscript"
version = "1.3.3-SNAPSHOT"

sourceSets {
    main {
        java { srcDir("src/main/java") }
        java { srcDir("src/denizen/java") }
    }
}

repositories {
    mavenLocal()
    maven("https://maven.citizensnpcs.co/repo")
    maven("https://repo.papermc.io/repository/maven-public/")
    mavenCentral()
}

dependencies {
    compileOnly(project(":plugin"))
    compileOnly(project(":denizencore"))
    compileOnly("io.papermc.paper:paper-api:26.1.2.build.70-stable")
    compileOnly("net.citizensnpcs:citizens-main:2.0.41-SNAPSHOT") { isTransitive = false }
}

allprojects {
    tasks.withType<JavaCompile> {
        options.release.set(21)
        options.encoding = "UTF-8"
    }
}