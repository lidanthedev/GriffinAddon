plugins {
    java
    `maven-publish`
    id("com.gradleup.shadow") version "8.3.6"
    id("io.freefair.lombok") version "8.11"
}

group = "me.lidan"
version = "1.0"

repositories {
    mavenCentral()
    mavenLocal()
    maven("https://repo.papermc.io/repository/maven-public/") {
        name = "papermc-repo"
    }
    maven("https://oss.sonatype.org/content/groups/public/") {
        name = "sonatype"
    }
    maven("https://mvn.lumine.io/repository/maven-public/")
    maven("https://jitpack.io")
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.21.11-R0.1-SNAPSHOT")
    compileOnly("org.jetbrains:annotations:23.0.0")
    compileOnly("me.lidan:CaveCrawlers:dev") // use the mavenLocal version of CaveCrawlers
    compileOnly("io.lumine:Mythic-Dist:5.3.5")
    implementation("com.github.lidanthedev.Lamp:common:3.3.7")
    implementation("com.github.lidanthedev.Lamp:brigadier:3.3.7")
    implementation("com.github.lidanthedev.Lamp:bukkit:3.3.7")
    implementation("fr.skytasul:glowingentities:1.4.10")
}

val targetJavaVersion = 21

java {
    val javaVersion = JavaVersion.toVersion(targetJavaVersion)
    sourceCompatibility = javaVersion
    targetCompatibility = javaVersion
    if (JavaVersion.current() < javaVersion) {
        toolchain.languageVersion.set(JavaLanguageVersion.of(targetJavaVersion))
    }
}

tasks.compileJava {
    // Preserve parameter names in the bytecode.
    options.compilerArgs.add("-parameters")
}

tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"

    if (targetJavaVersion >= 10 || JavaVersion.current().isJava10Compatible) {
        options.release.set(targetJavaVersion)
    }
}

tasks.shadowJar {
    archiveClassifier.set("")
    relocate("revxrsal.commands", "me.lidan.griffinAddon.lamp")
    relocate("com.github.Xezard.XGlow", "me.lidan.griffinAddon.xglow")
}

tasks.jar {
    enabled = false
}

tasks.build {
    dependsOn(tasks.named("shadowJar"))
}

tasks.processResources {
    val props = mapOf("version" to project.version)
    inputs.properties(props)
    filteringCharset = "UTF-8"
    filesMatching("plugin.yml") {
        expand(props)
    }
}
