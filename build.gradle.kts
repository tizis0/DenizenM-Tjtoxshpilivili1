plugins {
    java
    id("io.papermc.paperweight.patcher") version "2.0.0-beta.21"
    id("com.gradleup.shadow") version "9.4.3"
}

dependencies {
    implementation(project(":denizencore"))
    implementation(project(":plugin"))
    implementation(project(":paper"))
    implementation(project(":v26_2"))
    implementation(project(":v26_1"))
    implementation(project(":v1_21"))
}

allprojects {
    repositories {
        mavenLocal()
        mavenCentral()
        maven("https://maven.freneticllc.com")
    }
}

repositories {
    mavenLocal()
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://maven.citizensnpcs.co/repo")
    mavenCentral()
}

paperweight {
    upstreams.register("denizen") {
        repo = github("DenizenScript", "Denizen")
        ref = providers.gradleProperty("denizenRef")
        applyUpstreamNested = false

        patchDir("dist") {
            upstreamPath = "dist/src/main/java"
            patchesDir = layout.projectDirectory.dir("patches/dist")
            outputDir = layout.projectDirectory.dir("Denizen/dist/src/denizen/java")
        }

        patchDir("plugin") {
            upstreamPath = "plugin/src/main/java"
            patchesDir = layout.projectDirectory.dir("patches/plugin")
            outputDir = layout.projectDirectory.dir("Denizen/plugin/src/denizen/java")
        }
        patchDir("resources") {
            upstreamPath = "plugin/src/main/resources"
            patchesDir = layout.projectDirectory.dir("patches/resources")
            outputDir = layout.projectDirectory.dir("Denizen/plugin/src/denizen/resources")
        }
        patchDir("paper") {
            upstreamPath = "paper/src/main/java"
            patchesDir = layout.projectDirectory.dir("patches/paper")
            outputDir = layout.projectDirectory.dir("Denizen/paper/src/denizen/java")
        }
        patchDir("v26_2") {
            upstreamPath = "v26_2/src/main/java"
            patchesDir = layout.projectDirectory.dir("patches/v26_2")
            outputDir = layout.projectDirectory.dir("Denizen/v26_2/src/denizen/java")
        }
        patchDir("v26_1") {
            upstreamPath = "v26_1/src/main/java"
            patchesDir = layout.projectDirectory.dir("patches/v26_1")
            outputDir = layout.projectDirectory.dir("Denizen/v26_1/src/denizen/java")
        }
        patchDir("v1_21") {
            upstreamPath = "v1_21/src/main/java"
            patchesDir = layout.projectDirectory.dir("patches/v1_21")
            outputDir = layout.projectDirectory.dir("Denizen/v1_21/src/denizen/java")
        }
    }
}

fun gitExec(dir: File, vararg args: String) {
    val result = ProcessBuilder(*args)
        .directory(dir)
        .inheritIO()
        .start()
        .waitFor()
    if (result != 0) error("Git command failed: ${args.joinToString(" ")}")
}

val coreDir = layout.projectDirectory.dir("Denizen-Core").asFile
val corePatchesDir = layout.projectDirectory.dir("patches/core").asFile
val coreRef = providers.gradleProperty("denizenCoreRef")

tasks.register("setupDenizenCore") {
    group = "denizen-core"
    onlyIf { !coreDir.resolve(".git").exists() }
    doLast {
        gitExec(layout.projectDirectory.asFile,
            "git", "clone",
            "--depth=1",
            "--filter=blob:none",
            "--sparse",
            "--branch", coreRef.get(),
            "https://github.com/DenizenScript/Denizen-Core.git",
            coreDir.absolutePath
        )
        gitExec(coreDir, "git", "sparse-checkout", "set", "src")

        coreDir.resolve("build.gradle.kts").writeText("""
            plugins { java }
            
            group = "com.denizenscript"
            version = "1.91.0-SNAPSHOT"
            
            repositories {
                mavenCentral()
                maven("https://maven.freneticllc.com/")
            }
            
            dependencies {
                compileOnly("org.yaml:snakeyaml:1.33")
                compileOnly("redis.clients:jedis:4.3.1") { isTransitive = false }
                compileOnly("org.mongodb:mongodb-driver-sync:4.8.1") {
                    exclude(group = "org.mongodb", module = "bson-record-codec")
                }
                implementation("org.json:json:20231013")
                implementation("com.freneticllc.freneticutilities:freneticdatasyntax:1.1")
                implementation("org.ow2.asm:asm:9.4")
                implementation("org.ow2.asm:asm-commons:9.4")
            }
        """.trimIndent())
    }
}

tasks.register("fetchDenizenCore") {
    group = "denizen-core"
    dependsOn("setupDenizenCore")
    doLast {
        if (coreDir.resolve(".git").exists()) {
            gitExec(coreDir,
                "git", "--no-pager", "fetch", "-q",
                "--depth=1",
                "origin", coreRef.get()
            )
        }
    }
}

tasks.register("checkoutDenizenCore") {
    group = "denizen-core"
    dependsOn("fetchDenizenCore")
    doLast {
        gitExec(coreDir, "git", "--no-pager", "checkout", "-q", coreRef.get())
    }
}

tasks.register("applyDenizenCorePatches") {
    group = "denizen-core"
    dependsOn("checkoutDenizenCore")
    doLast {
        gitExec(coreDir, "git", "checkout", "-B", "patched")
        val patches = corePatchesDir.listFiles()
            ?.filter { it.extension == "patch" }
            ?.sorted()
            ?: emptyList()
        if (patches.isNotEmpty()) {
            gitExec(coreDir, "git", "am", "--3way", "--no-gpg-sign", *patches.map { it.absolutePath }.toTypedArray())
        }
    }
}

tasks.register("rebuildDenizenCorePatches") {
    group = "denizen-core"
    doLast {
        corePatchesDir.mkdirs()
        corePatchesDir.listFiles()?.filter { it.extension == "patch" }?.forEach { it.delete() }
        gitExec(coreDir, "git", "format-patch", coreRef.get(),
            "-o", corePatchesDir.absolutePath,
            "--no-stat", "--zero-commit", "--full-index"
        )
    }
}

tasks.register("applyAllProjectPatches") {
    group = "denizen"
    dependsOn("applyDenizenPatches", "applyDenizenCorePatches")
}

tasks.register("rebuildAllProjectPatches") {
    group = "denizen"
    dependsOn("rebuildDenizenPatches", "rebuildDenizenCorePatches")
}

tasks.shadowJar {
    dependsOn(":v1_21:remapSpigot")
    archiveBaseName.set("Denizen")
    archiveClassifier.set("")
    mergeServiceFiles()

    relocate("com.freneticllc", "com.denizenscript.shaded.com.freneticllc")
    relocate("org.json", "com.denizenscript.shaded.org.json")
    relocate("org.ow2.asm", "com.denizenscript.shaded.org.ow2.asm")
    relocate("org.objectweb", "com.denizenscript.shaded.org.objectweb")
    relocate("org.apache", "com.denizenscript.shaded.org.apache")
    relocate("net.kyori.option", "com.denizenscript.shaded.net.kyori.option")
    relocate("net.kyori.adventure.nbt", "com.denizenscript.shaded.net.adventure.nbt")
}

tasks.build {
    dependsOn(tasks.shadowJar)
}

java {
    toolchain { languageVersion = JavaLanguageVersion.of(25) }
}