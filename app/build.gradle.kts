//import com.github.szsoftware.gradle.TargetsCargo
//import org.gradle.kotlin.dsl.implementation

plugins {
    // Apply the org.jetbrains.kotlin.jvm Plugin to add support for Kotlin.
    alias(libs.plugins.kotlin.jvm)

    /**
     * The library and bindings are build with the help of my own toolchain.
     * The plugin is in beta state and not yet published.
     * I put, quick and dirty, a fat jar, here.
     * Sorry for the inconveniences.
     *
     * However, the examples should run under x86_64_linux without the necessity
     * of (re)building the rust library and (re)generating the bindings.
     *
     * If curious to build, uncomment and consider the new tasks in task group "rust",
     * such as "buildLibraries".
     * The sample executable binary build is not covered by the plugin
     * and must be built via command line "cargo build" in app/rust.
     *
     * Configuration is done in extension cargoUniffi {}
     * For transparency I mentioned the default options due to lack of docs for the plugin.
     *
     * jna developers should be interested in
     * src/main/kotlin/uniffi/example_library/example_library.kt
     * where all jna library loading magic happens.
     *
     */
    //id("com.github.szsoftware.gradle.cargo.uniffi.plugin") version "0.1.0"

    // Apply the application plugin to add support for building a CLI application in Java.
    application
}

/*
cargoUniffi {
    rustSrcDir = "rust"

    kotlinDir = "src/main/kotlin"

    cargoTargets {
        +TargetsCargo.x86_64_unknown_linux_gnu
    }
    //createRustSkeleton = true
}
*/

dependencies {

    implementation("net.java.dev.jna:jna:5.16.0")

    // Use the Kotlin JUnit 5 integration.
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")

    // Use the JUnit 5 integration.
    testImplementation(libs.junit.jupiter.engine)

    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    // This dependency is used by the application.
    //implementation(libs.guava)
}

// Apply a specific Java toolchain to ease working on different environments.
java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}

application {
    // Define the main class for the application.
    mainClass = "org.example.AppKt"
}

tasks.named<Test>("test") {
    // Use JUnit Platform for unit tests.
    useJUnitPlatform()
}

tasks.register<DefaultTask>("runNativeSampleApp") {
    group = "application" // Task wird zur Gruppe "application" hinzugefügt.
    description = "Runs the sample application, written in Rust."

    notCompatibleWithConfigurationCache("")

    doLast {
        println("Running the sample application...")
        exec {
            commandLine("${projectDir}/src/main/resources/linux-x86-64/sample-app")
        }
    }
}

tasks.register<DefaultTask>("buildSampleApp") {
    group = "rust" // Task wird zur Gruppe "application" hinzugefügt.
    description = "Runs the sample application, written in Rust."

    notCompatibleWithConfigurationCache("")

    doLast {
        println("Building the sample application...")
        exec {
            workingDir("${projectDir}/rust")
            commandLine("cargo", "build")
        }

        copy {
            from("${projectDir}/rust/target/debug/sample-app")
            into("${projectDir}/src/main/resources/linux-x86-64")
        }
    }
}
