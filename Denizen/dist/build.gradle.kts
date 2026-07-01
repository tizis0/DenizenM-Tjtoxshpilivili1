plugins {
    java
    id("com.gradleup.shadow") version "9.4.3"
}

group = "com.denizenscript"
version = "1.3.3-SNAPSHOT"

dependencies {
    implementation(project(":plugin"))
    implementation(project(":paper"))
    implementation(project(":v26_2"))
    implementation(project(":v26_1"))
    implementation(project(":v1_21"))
    implementation(project(":denizencore"))
}

sourceSets {
    main {
        java { srcDir("src/denizen/java") }
    }
}

tasks.shadowJar {
    dependsOn(":v1_21:remapSpigot")
    archiveBaseName.set("Denizen")
    archiveClassifier.set("")
    mergeServiceFiles()

}

tasks.build {
    dependsOn(tasks.shadowJar)
}

allprojects {
    tasks.withType<JavaCompile> {
        options.release.set(21)
        options.encoding = "UTF-8"
    }
}