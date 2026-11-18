// Copyright 2026, compose-rmuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package io.github.ra1nify.rmuix.kmp.basic

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toAwtImage
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.isRoot
import androidx.compose.ui.test.v2.runComposeUiTest
import androidx.compose.ui.unit.dp
import io.github.ra1nify.rmuix.kmp.icon.RmuixIcons
import io.github.ra1nify.rmuix.kmp.icon.basic.Check
import io.github.ra1nify.rmuix.kmp.icon.basic.Search
import io.github.ra1nify.rmuix.kmp.theme.ColorSchemeMode
import io.github.ra1nify.rmuix.kmp.theme.RmuixTheme
import io.github.ra1nify.rmuix.kmp.theme.ThemeController
import java.io.File
import javax.imageio.ImageIO
import kotlin.test.Test
import kotlin.test.assertTrue

@OptIn(ExperimentalTestApi::class)
class TooltipScreenshotTest {
    @Test
    fun light() = render("tooltip-light", dark = false)

    @Test
    fun dark() = render("tooltip-dark", dark = true)

    private fun render(
        name: String,
        dark: Boolean,
    ) = runComposeUiTest {
        setContent {
            val controller =
                remember {
                    ThemeController(colorSchemeMode = if (dark) ColorSchemeMode.Dark else ColorSchemeMode.Light)
                }
            RmuixTheme(controller = controller) {
                Column(
                    Modifier
                        .width(420.dp)
                        .background(RmuixTheme.colorScheme.background)
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(24.dp)) {
                        TooltipBox(
                            text = "Brightness",
                            state = rememberTooltipState(initialIsVisible = true),
                        ) {
                            IconButton(onClick = {}) {
                                Icon(
                                    imageVector = RmuixIcons.Basic.Search,
                                    contentDescription = "Search",
                                )
                            }
                        }
                        TooltipBox(
                            text = "A much longer tooltip label that should wrap when it reaches the width cap",
                            state = rememberTooltipState(initialIsVisible = true),
                        ) {
                            IconButton(onClick = {}) {
                                Icon(
                                    imageVector = RmuixIcons.Basic.Check,
                                    contentDescription = "Check",
                                )
                            }
                        }
                    }
                }
            }
        }
        waitForIdle()
        // The tooltips live in their own popup roots, so the main root alone does not show them.
        var root = File(System.getProperty("user.dir")).absoluteFile
        while (root.parentFile != null && !File(root, "settings.gradle.kts").exists()) root = root.parentFile
        val dir = File(root, "docs/dev/shots").apply { mkdirs() }
        val roots = onAllNodes(isRoot())
        roots.fetchSemanticsNodes().indices.forEach { index ->
            val image = roots[index].captureToImage().toAwtImage()
            ImageIO.write(image, "png", File(dir, "$name-$index.png"))
        }
        assertTrue(true)
    }
}
