// Copyright 2026, compose-rmuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package io.github.ra1nify.rmuix.kmp.basic

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toAwtImage
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.v2.runComposeUiTest
import androidx.compose.ui.unit.dp
import io.github.ra1nify.rmuix.kmp.overlay.OverlayBottomSheet
import io.github.ra1nify.rmuix.kmp.theme.ColorSchemeMode
import io.github.ra1nify.rmuix.kmp.theme.RmuixTheme
import io.github.ra1nify.rmuix.kmp.theme.ThemeController
import java.io.File
import javax.imageio.ImageIO
import kotlin.test.Test
import kotlin.test.assertTrue

@OptIn(ExperimentalTestApi::class)
class BottomSheetScreenshotTest {
    @Test
    fun light() = render("bottomsheet-light", dark = false)

    @Test
    fun dark() = render("bottomsheet-dark", dark = true)

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
                Scaffold {
                    Box(Modifier.fillMaxSize().background(RmuixTheme.colorScheme.background)) {
                        Box(Modifier.size(1.dp)) {
                            OverlayBottomSheet(
                                show = true,
                                title = "Share",
                                onDismissRequest = {},
                            ) {
                                Column(Modifier.fillMaxWidth().padding(16.dp)) {
                                    BasicComponent(
                                        title = "Copy link",
                                        summary = "Copy the link to the clipboard",
                                        onClick = {},
                                    )
                                    BasicComponent(
                                        title = "Save to device",
                                        summary = "Download the file",
                                        onClick = {},
                                    )
                                    BasicComponent(
                                        title = "Print",
                                        onClick = {},
                                    )
                                }
                            }
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
