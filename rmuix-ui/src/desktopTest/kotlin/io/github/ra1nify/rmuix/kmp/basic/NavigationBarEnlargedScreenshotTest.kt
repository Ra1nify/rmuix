// Copyright 2026, compose-rmuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package io.github.ra1nify.rmuix.kmp.basic

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.graphics.toAwtImage
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.runComposeUiTest
import androidx.compose.ui.unit.dp
import io.github.ra1nify.rmuix.kmp.icon.RmuixIcons
import io.github.ra1nify.rmuix.kmp.icon.basic.Check
import io.github.ra1nify.rmuix.kmp.icon.basic.ArrowRight
import io.github.ra1nify.rmuix.kmp.icon.basic.Search
import io.github.ra1nify.rmuix.kmp.theme.RmuixTheme
import java.io.File
import javax.imageio.ImageIO
import kotlin.test.Test

/** Writes the enlarged-variant screenshot the release notes link to. */
@OptIn(ExperimentalTestApi::class)
class NavigationBarEnlargedScreenshotTest {
    @Test
    fun writeEnlargedBarScreenshot() =
        runComposeUiTest {
            setContent {
                Column(Modifier.background(RmuixTheme.colorScheme.surface)) {
                    Bar(enlarged = false, tag = "normal")
                    Bar(enlarged = true, tag = "enlarged")
                }
            }
            // Walk up to the repo root: the test JVM's working directory is the module, and writing a
            // module-relative path leaves a stray `rmuix-ui/docs/...` tree behind.
            var root = File(System.getProperty("user.dir")).absoluteFile
            while (root.parentFile != null && !File(root, "settings.gradle.kts").exists()) root = root.parentFile
            val dir = File(root, "docs/dev/shots").apply { mkdirs() }
            listOf("normal", "enlarged").forEach { tag ->
                val image = onNodeWithTag(tag).captureToImage().toAwtImage()
                ImageIO.write(image, "png", File(dir, "navigation-bar-$tag.png"))
            }
        }

    @Composable
    private fun Bar(
        enlarged: Boolean,
        tag: String,
    ) {
        Box(
            Modifier
                .fillMaxWidth()
                .width(360.dp)
                .testTag(tag)
                .background(RmuixTheme.colorScheme.surface),
        ) {
            NavigationBar(enlarged = enlarged, showDivider = false) {
                NavigationBarItem(
                    selected = true,
                    onClick = {},
                    icon = RmuixIcons.Basic.Search,
                    label = "Search",
                    colors = NavigationBarDefaults.navigationBarItemColors(selectedContentColor = RmuixTheme.colorScheme.primary),
                )
                NavigationBarItem(
                    selected = false,
                    onClick = {},
                    icon = RmuixIcons.Basic.ArrowRight,
                    label = "Settings",
                    colors = NavigationBarDefaults.navigationBarItemColors(unselectedContentColor = Color(0xFF666666)),
                )
                NavigationBarItem(
                    selected = false,
                    onClick = {},
                    icon = RmuixIcons.Basic.Check,
                    label = "Done",
                    colors = NavigationBarDefaults.navigationBarItemColors(unselectedContentColor = Color(0xFF666666)),
                )
            }
        }
    }
}
