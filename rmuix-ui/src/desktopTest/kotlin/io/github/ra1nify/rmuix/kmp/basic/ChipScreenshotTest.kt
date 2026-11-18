// Copyright 2026, compose-rmuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package io.github.ra1nify.rmuix.kmp.basic

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toAwtImage
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.v2.runComposeUiTest
import androidx.compose.ui.unit.dp
import io.github.ra1nify.rmuix.kmp.theme.ColorSchemeMode
import io.github.ra1nify.rmuix.kmp.theme.RmuixTheme
import io.github.ra1nify.rmuix.kmp.theme.ThemeController
import java.io.File
import javax.imageio.ImageIO
import kotlin.test.Test
import kotlin.test.assertTrue

@OptIn(ExperimentalTestApi::class)
class ChipScreenshotTest {
    @Test
    fun lightChips() = render("chips-light", dark = false)

    @Test
    fun darkChips() = render("chips-dark", dark = true)

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
                Box(
                    Modifier
                        .background(RmuixTheme.colorScheme.background)
                        .padding(16.dp)
                        .width(400.dp),
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(ChipDefaults.GroupSpacing),
                            verticalArrangement = Arrangement.spacedBy(ChipDefaults.GroupSpacing),
                        ) {
                            Chip(onClick = {}, label = "All", selected = true)
                            Chip(onClick = {}, label = "Photos")
                            Chip(onClick = {}, label = "Videos")
                            Chip(onClick = {}, label = "Disabled", enabled = false)
                            Chip(onClick = {}, label = "Removable", onClose = {})
                        }
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(ChipDefaults.GroupSpacing),
                            verticalArrangement = Arrangement.spacedBy(ChipDefaults.GroupSpacing),
                        ) {
                            Chip(onClick = {}, label = "Selection", style = ChipStyle.Selection)
                            Chip(
                                onClick = {},
                                label = "Selected",
                                selected = true,
                                style = ChipStyle.Selection,
                            )
                            Chip(
                                onClick = {},
                                label = "Off",
                                enabled = false,
                                style = ChipStyle.Selection,
                            )
                        }
                    }
                }
            }
        }
        waitForIdle()
        val image = onRoot().captureToImage().toAwtImage()
        var root = File(System.getProperty("user.dir")).absoluteFile
        while (root.parentFile != null && !File(root, "settings.gradle.kts").exists()) root = root.parentFile
        val dir = File(root, "docs/dev/shots").apply { mkdirs() }
        val file = File(dir, "$name.png")
        ImageIO.write(image, "png", file)
        assertTrue(file.length() > 0, "screenshot should be written")
    }
}
