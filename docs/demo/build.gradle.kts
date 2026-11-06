// Copyright 2025, compose-rmuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

plugins {
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.kotlinMultiplatform)
    id("module.kotlin-jvm-toolchain")
    id("module.spotless")
}

kotlin {
    js {
        outputModuleName = "demo"
        browser {
            commonWebpackConfig {
                outputFileName = "demo.js"
            }
        }
        binaries.executable()
    }

    sourceSets {
        commonMain.dependencies {
            api(projects.rmuixUi)
            api(projects.rmuixPreference)
            implementation(projects.rmuixIcons)
            implementation(projects.rmuixNav)
        }
    }
}
