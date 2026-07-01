pluginManagement {
    repositories {
        gradlePluginPortal()
        maven("https://repo.papermc.io/repository/maven-public/")
    }
}

rootProject.name = "DenizenM"

include(":plugin", ":paper", ":v26_2", ":v26_1", ":v1_21")

project(":plugin").projectDir = file("Denizen/plugin")
project(":paper").projectDir  = file("Denizen/paper")
project(":v26_2").projectDir  = file("Denizen/v26_2")
project(":v26_1").projectDir  = file("Denizen/v26_1")
project(":v1_21").projectDir  = file("Denizen/v1_21")

if (file("Denizen-Core/src").exists()) {
    include(":denizencore")
    project(":denizencore").projectDir = file("Denizen-Core")
}

if (file("Denizen/dist").exists()) {
    include(":dist")
    project(":dist").projectDir = file("Denizen/dist")
}