// Copyright 2026, compose-rmuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package io.github.ra1nify.rmuix.kmp.basic

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toAwtImage
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.v2.runComposeUiTest
import androidx.compose.ui.unit.dp
import io.github.ra1nify.rmuix.kmp.icon.RmuixIcons
import io.github.ra1nify.rmuix.kmp.icon.basic.Check
import io.github.ra1nify.rmuix.kmp.icon.basic.Close
import io.github.ra1nify.rmuix.kmp.icon.basic.Search
import io.github.ra1nify.rmuix.kmp.theme.ColorSchemeMode
import io.github.ra1nify.rmuix.kmp.theme.RmuixTheme
import io.github.ra1nify.rmuix.kmp.theme.ThemeController
import java.io.File
import javax.imageio.ImageIO
import kotlin.test.Test
import kotlin.test.assertTrue

@OptIn(ExperimentalTestApi::class)
class NavSamplesScreenshotTest {
    @Test
    fun light() = render("nav-samples-light", dark = false)

    @Test
    fun dark() = render("nav-samples-dark", dark = true)

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
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        Box(Modifier.size(width = 96.dp, height = 300.dp)) {
                            NavigationRail {
                                NavigationRailItem(
                                    selected = true,
                                    onClick = {},
                                    icon = RmuixIcons.Basic.Search,
                                    label = "Search",
                                )
                                NavigationRailItem(
                                    selected = false,
                                    onClick = {},
                                    icon = RmuixIcons.Basic.Check,
                                    label = "Settings",
                                )
                                NavigationRailItem(
                                    selected = false,
                                    onClick = {},
                                    icon = RmuixIcons.Basic.Close,
                                    label = "Favorites",
                                )
                            }
                        }
                    }
                    FloatingNavigationBar(modifier = Modifier.fillMaxWidth()) {
                        FloatingNavigationBarItem(
                            selected = true,
                            onClick = {},
                            label = "Search",
                            icon = RmuixIcons.Basic.Search,
                            modifier = Modifier.weight(1f),
                        )
                        FloatingNavigationBarItem(
                            selected = false,
                            onClick = {},
                            label = "Settings",
                            icon = RmuixIcons.Basic.Check,
                            modifier = Modifier.weight(1f),
                        )
                        FloatingNavigationBarItem(
                            selected = false,
                            onClick = {},
                            label = "Favorites",
                            icon = RmuixIcons.Basic.Close,
                            modifier = Modifier.weight(1f),
                        )
                    }
                    NavigationBar(modifier = Modifier.fillMaxWidth()) {
                        NavigationBarItem(
                            selected = true,
                            onClick = {},
                            icon = RmuixIcons.Basic.Search,
                            label = "Search",
                            modifier = Modifier.weight(1f),
                        )
                        NavigationBarItem(
                            selected = false,
                            onClick = {},
                            icon = RmuixIcons.Basic.Check,
                            label = "Settings",
                            modifier = Modifier.weight(1f),
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
