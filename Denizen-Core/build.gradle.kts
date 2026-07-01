plugins { id("java-library") }

group = "com.denizenscript"
version = "1.91.0-SNAPSHOT"

repositories {
    mavenLocal()
    mavenCentral()
    maven("https://maven.freneticllc.com")
}

dependencies {
    compileOnly("org.yaml:snakeyaml:1.33")
    compileOnly("redis.clients:jedis:4.3.1") { isTransitive = false }
    compileOnly("org.mongodb:mongodb-driver-sync:4.8.1") {
        exclude(group = "org.mongodb", module = "bson-record-codec")
    }
    api("org.json:json:20231013")
    api("com.freneticllc.freneticutilities:freneticdatasyntax:1.1")
    api("org.ow2.asm:asm:9.4")
    api("org.ow2.asm:asm-commons:9.4")
}

allprojects {
    tasks.withType<JavaCompile> {
        options.release.set(21)
        options.encoding = "UTF-8"
    }
}