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
import androidx.compose.ui.graphics.toAwtImage
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.v2.runComposeUiTest
import androidx.compose.ui.unit.dp
import io.github.ra1nify.rmuix.kmp.icon.RmuixIcons
import io.github.ra1nify.rmuix.kmp.icon.basic.Search
import io.github.ra1nify.rmuix.kmp.theme.ColorSchemeMode
import io.github.ra1nify.rmuix.kmp.theme.RmuixTheme
import io.github.ra1nify.rmuix.kmp.theme.ThemeController
import java.io.File
import javax.imageio.ImageIO
import kotlin.test.Test
import kotlin.test.assertTrue

@OptIn(ExperimentalTestApi::class)
class RedDotBoxScreenshotTest {
    @Test
    fun light() = render("reddotbox-light", dark = false)

    @Test
    fun dark() = render("reddotbox-dark", dark = true)

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
                        .width(360.dp)
                        .background(RmuixTheme.colorScheme.background)
                        .padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(40.dp)) {
                        RedDotBox(
                            redDot = { RedDot(mode = RedDotMode.PointOnly) },
                            anchorSize = 36.dp,
                            anchorShape = RedDotAnchorShape.Rectangle,
                        ) {
                            Icon(
                                imageVector = RmuixIcons.Basic.Search,
                                contentDescription = "Rectangle anchor",
                                modifier = Modifier.size(36.dp),
                            )
                        }
                        RedDotBox(
                            redDot = { RedDot(mode = RedDotMode.PointOnly) },
                            anchorSize = 36.dp,
                            anchorShape = RedDotAnchorShape.Circle,
                        ) {
                            Box(
                                Modifier
                                    .size(36.dp)
                                    .background(RmuixTheme.colorScheme.secondaryContainer),
                            )
                        }
                        RedDotBox(
                            redDot = { RedDot(mode = RedDotMode.PointWithNum(9)) },
                            anchorSize = 50.dp,
                            anchorShape = RedDotAnchorShape.Circle,
                            redDotKind = RedDotAnchorKind.Number,
                        ) {
                            Box(
                                Modifier
                                    .size(50.dp)
                                    .background(RmuixTheme.colorScheme.secondaryContainer),
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
