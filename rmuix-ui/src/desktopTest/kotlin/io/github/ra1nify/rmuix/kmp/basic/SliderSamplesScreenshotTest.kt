// Copyright 2026, compose-rmuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package io.github.ra1nify.rmuix.kmp.basic

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
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
class SliderSamplesScreenshotTest {
    @Test
    fun light() = render("slider-samples-light", dark = false)

    @Test
    fun dark() = render("slider-samples-dark", dark = true)

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
                        .width(400.dp)
                        .background(RmuixTheme.colorScheme.background)
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(18.dp),
                ) {
                    Slider(value = 0f, onValueChange = {}, modifier = Modifier.fillMaxWidth())
                    Slider(value = 0.35f, onValueChange = {}, modifier = Modifier.fillMaxWidth())
                    Slider(value = 0.7f, onValueChange = {}, modifier = Modifier.fillMaxWidth())
                    Slider(value = 1f, onValueChange = {}, modifier = Modifier.fillMaxWidth())
                    Slider(
                        value = 0.5f,
                        onValueChange = {},
                        enabled = false,
                        modifier = Modifier.fillMaxWidth(),
                    )
                    Slider(
                        value = 0.4f,
                        onValueChange = {},
                        steps = 3,
                        showKeyPoints = true,
                        keyPoints = listOf(0f, 0.25f, 0.5f, 0.75f, 1f),
                        modifier = Modifier.fillMaxWidth(),
                    )
                    Slider(
                        value = 0.6f,
                        onValueChange = {},
                        reverseDirection = true,
                        modifier = Modifier.fillMaxWidth(),
                    )
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
