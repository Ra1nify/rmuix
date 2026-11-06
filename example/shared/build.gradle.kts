// Copyright 2025, compose-rmuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl

plugins {
    alias(libs.plugins.aboutLibraries)
    alias(libs.plugins.androidKotlinMultiplatformLibrary)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.kotlinSerialization)
    id("module.kotlin-jvm-toolchain")
    id("module.spotless")
}

val generatedSrcDir: Provider<Directory> = layout.buildDirectory.dir("generated/rmuix-example")

group = BuildConfig.LIBRARY_ID

kotlin {
    android {
        androidResources.enable = true
        buildToolsVersion = BuildConfig.BUILD_TOOLS_VERSION
        compileSdk {
            version =
                release(BuildConfig.COMPILE_SDK) {
                    minorApiLevel = BuildConfig.COMPILE_SDK_MINOR
                }
        }
        minSdk = BuildConfig.MIN_SDK
        namespace = BuildConfig.APPLICATION_SHARED_ID
    }

    jvm("desktop")

    listOf(
        iosArm64(),
        iosSimulatorArm64(),
    ).forEach {
        it.binaries.framework {
            baseName = "shared"
            isStatic = true
            binaryOption("smallBinary", "true")
            binaryOption("preCodegenInlineThreshold", "40")
        }
    }

    macosArm64()

    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        browser()
    }

    js {
        browser()
    }

    applyRmuixSourceSetHierarchy()

    sourceSets {
        commonMain {
            kotlin.srcDir(generatedSrcDir.map { it.dir("kotlin") })
            dependencies {
                api(projects.rmuixUi)
                api(projects.rmuixPreference)
                api(libs.jetbrains.compose.components.resources)
                implementation(projects.rmuixBlur)
                implementation(projects.rmuixSquircle)
                implementation(projects.rmuixIcons)
                implementation(projects.rmuixNav)
                implementation(libs.androidx.navigationevent)
                implementation(libs.aboutlibraries.core)
                implementation(libs.kotlinx.serialization.core)
            }
        }
    }
}

compose.resources {
    publicResClass = true
}

val generateVersionInfo =
    tasks.register<GenerateVersionInfoTask>("generateVersionInfo") {
        description = "GenerateVersionInfoTask"
        versionName.set(BuildConfig.APPLICATION_VERSION_NAME)
        versionCode.set(getGitVersionCode())
        outputFile.set(generatedSrcDir.map { it.file("kotlin/misc/VersionInfo.kt") })
        xcconfigFile.set(layout.projectDirectory.file("../ios/iosApp/Generated.xcconfig"))
    }

aboutLibraries {
    export {
        outputFile = file("src/commonMain/composeResources/files/aboutlibraries.json")
    }
}

tasks.named("generateComposeResClass").configure {
    dependsOn(generateVersionInfo)
}
