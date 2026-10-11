plugins {
    kotlin("jvm") version "2.0.21"
    application
    id("info.solidsoft.pitest") version "1.15.0"
}

group = "com.game"
version = "1.0.0"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.3"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

kotlin {
    jvmToolchain(21)
}

application {
    mainClass.set("com.game.character.MainKt")
}

tasks.withType<Test> {
    useJUnitPlatform()
    jvmArgs("-Dfile.encoding=UTF-8", "-Duser.language=en", "-Duser.country=US")
    testLogging {
        events("passed", "skipped", "failed")
        showStandardStreams = true
        showExceptions = true
        exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
    }
}

pitest {
    junit5PluginVersion.set("1.2.1")
    targetClasses.set(setOf("com.game.character.*"))
    targetTests.set(setOf("com.game.character.*"))
    threads.set(2)
    outputFormats.set(setOf("HTML", "XML"))
    timestampedReports.set(false)
}

tasks.named<JavaExec>("run") {
    jvmArgs(
        "-Dfile.encoding=UTF-8",
        "-Dstdout.encoding=UTF-8",
        "-Dstderr.encoding=UTF-8",
        "-Dsun.stdout.encoding=UTF-8",
        "-Dsun.stderr.encoding=UTF-8"
    )
}