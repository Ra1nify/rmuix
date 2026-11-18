// Copyright 2026, compose-rmuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package io.github.ra1nify.rmuix.kmp.basic

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toAwtImage
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.v2.runComposeUiTest
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.ra1nify.rmuix.kmp.theme.ColorSchemeMode
import io.github.ra1nify.rmuix.kmp.theme.RmuixTheme
import io.github.ra1nify.rmuix.kmp.theme.ThemeController
import java.io.File
import javax.imageio.ImageIO
import kotlin.test.Test
import kotlin.test.assertTrue

/** Covers the components the main sample sheet does not render. */
@OptIn(ExperimentalTestApi::class)
class ExtraSamplesScreenshotTest {
    @Test
    fun light() = render("extra-samples-light", dark = false)

    @Test
    fun dark() = render("extra-samples-dark", dark = true)

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
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    FloatingToolbar(modifier = Modifier.width(360.dp)) {
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            IconButton(onClick = {}) {
                                Text(
                                    text = "A",
                                    fontSize = 16.sp,
                                    color = RmuixTheme.colorScheme.onBackground,
                                )
                            }
                            IconButton(onClick = {}) {
                                Text(
                                    text = "B",
                                    fontSize = 16.sp,
                                    color = RmuixTheme.colorScheme.onBackground,
                                )
                            }
                        }
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        FloatingActionButton(onClick = {}) {
                            Text(
                                text = "+",
                                fontSize = 20.sp,
                                color = RmuixTheme.colorScheme.onPrimary,
                            )
                        }
                        IconButton(onClick = {}, enabled = false) {
                            Text(
                                text = "x",
                                fontSize = 16.sp,
                                color = RmuixTheme.colorScheme.disabledOnSurface,
                            )
                        }
                        Box(Modifier.size(40.dp)) {
                            VerticalDivider(modifier = Modifier.align(Alignment.Center))
                        }
                        RedDot(mode = RedDotMode.PointWithNum(9))
                    }
                    HorizontalDivider()
                    NumberPickerGroup(modifier = Modifier.width(360.dp)) {
                        NumberPicker(
                            value = 1,
                            onValueChange = {},
                            range = 0..23,
                            wrapAround = true,
                            modifier = Modifier.width(96.dp),
                        )
                        NumberPicker(
                            value = 30,
                            onValueChange = {},
                            range = 0..59,
                            wrapAround = true,
                            modifier = Modifier.width(96.dp),
                        )
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
