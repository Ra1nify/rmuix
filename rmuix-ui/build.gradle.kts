// Copyright 2025, compose-rmuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl

plugins {
    alias(libs.plugins.androidKotlinMultiplatformLibrary)
    alias(libs.plugins.baselineprofile)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.dokka)
    alias(libs.plugins.kotlinMultiplatform)
    id("module.kotlin-jvm-toolchain")
    id("module.publication")
    id("module.spotless")
}

rmuixPublication {
    description.set("A UI library for Compose Multiplatform")
}

kotlin {
    withSourcesJar(true)

    android {
        buildToolsVersion = BuildConfig.BUILD_TOOLS_VERSION
        compileSdk {
            version =
                release(BuildConfig.COMPILE_SDK) {
                    minorApiLevel = BuildConfig.COMPILE_SDK_MINOR
                }
        }
        minSdk = BuildConfig.MIN_SDK
        namespace = "${BuildConfig.LIBRARY_ID}.ui"
        withHostTest {}
    }

    jvm("desktop")

    iosArm64()
    iosSimulatorArm64()
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
        commonTest.dependencies {
            implementation(kotlin("test"))
        }

        commonMain.dependencies {
            api(projects.rmuixCore)
            api(projects.rmuixSquircle)
            api(libs.jetbrains.compose.foundation)

            implementation(libs.androidx.navigationevent)
            implementation(libs.jetbrains.compose.window.size)

            implementation(libs.materialKolor.utilities) // Material Color for Multiplatform
        }
    }
}

baselineProfile {
    filter {
        include("io.github.ra1nify.rmuix.kmp.**")
    }
}

val convertBaselineProfile =
    tasks.register<ConvertBaselineProfileTask>("convertBaselineProfile") {
        description = "convertBaselineProfile"
        inputFile.convention(
            provider {
                val generatedUiProfile = layout.projectDirectory.file("src/androidMain/generated/baselineProfiles/baseline-prof.txt")
                val generatedExampleProfile =
                    rootProject.layout.projectDirectory.file(
                        "example/android/src/release/generated/baselineProfiles/baseline-prof.txt",
                    )
                if (generatedUiProfile.asFile.exists()) generatedUiProfile else generatedExampleProfile
            },
        )
        outputFile.set(
            layout.projectDirectory.file("src/androidMain/baselineProfiles/baseline-prof.txt"),
        )
        targetPackage.set("top/yukonga/rmuix/kmp/")
        excludePackages.set(
            listOf(
                "top/yukonga/rmuix/kmp/icon/extended/",
                "top/yukonga/rmuix/kmp/shared/",
            ),
        )
        additionalOutputs.put(
            "top/yukonga/rmuix/kmp/preference/",
            rootProject.layout.projectDirectory
                .file(
                    "rmuix-preference/src/androidMain/baselineProfiles/baseline-prof.txt",
                ).asFile.absolutePath,
        )
        additionalOutputs.put(
            "top/yukonga/rmuix/kmp/menu/",
            rootProject.layout.projectDirectory
                .file(
                    "rmuix-preference/src/androidMain/baselineProfiles/baseline-prof.txt",
                ).asFile.absolutePath,
        )
        additionalOutputs.put(
            "top/yukonga/rmuix/kmp/popup/",
            rootProject.layout.projectDirectory
                .file(
                    "rmuix-preference/src/androidMain/baselineProfiles/baseline-prof.txt",
                ).asFile.absolutePath,
        )
        additionalOutputs.put(
            "top/yukonga/rmuix/kmp/blur/",
            rootProject.layout.projectDirectory
                .file(
                    "rmuix-blur/src/androidMain/baselineProfiles/baseline-prof.txt",
                ).asFile.absolutePath,
        )
        additionalOutputs.put(
            "top/yukonga/rmuix/kmp/nav/",
            rootProject.layout.projectDirectory
                .file(
                    "rmuix-nav/src/androidMain/baselineProfiles/baseline-prof.txt",
                ).asFile.absolutePath,
        )
        additionalOutputs.put(
            "top/yukonga/rmuix/kmp/shader/",
            rootProject.layout.projectDirectory
                .file(
                    "rmuix-shader/src/androidMain/baselineProfiles/baseline-prof.txt",
                ).asFile.absolutePath,
        )
        additionalOutputs.put(
            "top/yukonga/rmuix/kmp/squircle/",
            rootProject.layout.projectDirectory
                .file(
                    "rmuix-squircle/src/androidMain/baselineProfiles/baseline-prof.txt",
                ).asFile.absolutePath,
        )
    }

tasks.matching { it.name == "generateBaselineProfile" }.configureEach {
    finalizedBy(convertBaselineProfile)
}

dependencies {
    baselineProfile(projects.baselineprofile)
}
