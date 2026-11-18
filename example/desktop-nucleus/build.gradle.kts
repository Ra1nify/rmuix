// Copyright 2026, compose-rmuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

import dev.nucleusframework.desktop.application.dsl.CompressionLevel
import dev.nucleusframework.desktop.application.dsl.NativeImageOptimization
import dev.nucleusframework.desktop.application.dsl.TargetFormat

plugins {
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.composeHotReload)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.nucleus)
    id("module.kotlin-jvm-toolchain")
    id("module.spotless")
}

kotlin {
    jvm("desktop")

    sourceSets {
        named("desktopMain") {
            dependencies {
                implementation(projects.example.shared)
                implementation(compose.desktop.currentOs)
                implementation(libs.nucleus.application)
                implementation(libs.nucleus.decorated.window.tao)
            }
        }
    }
}

nucleus.application {
    mainClass = "Main_desktopKt"
    buildTypes.release.proguard {
        isEnabled = true
        optimize = true
        obfuscate = false
        joinOutputJars = true
        configurationFiles.from(project.file("proguard-rules.pro"))
    }

    graalvm {
        isEnabled = true
        imageName = "rmuix"
        optimization = NativeImageOptimization.SIZE
        toolchain.version = "25i4"
        buildArgs.addAll("-H:-IncludeMethodData")
        buildArgs.addAll("-H:ExcludeResources=.libskiko.")
    }

    nativeDistributions {
        targetFormats(TargetFormat.Dmg, TargetFormat.Exe, TargetFormat.Msi, TargetFormat.Deb)
        packageName = BuildConfig.APPLICATION_NAME
        packageVersion = BuildConfig.APPLICATION_VERSION_NAME
        homepage = "https://github.com/compose-rmuix-ui/rmuix"

        cleanupNativeLibs = true
        compressionLevel = CompressionLevel.Maximum

        macOS {
            bundleID = BuildConfig.APPLICATION_ID
            iconFile = project.file("../desktop/resources/macos/icon.icns")
        }
        linux {
            iconFile = project.file("../desktop/resources/linux/icon.png")
            debMaintainer = "compose-rmuix-ui contributors <https://github.com/compose-rmuix-ui/rmuix>"
        }
        windows {
            iconFile = project.file("../desktop/resources/windows/icon.ico")
        }
    }
}
