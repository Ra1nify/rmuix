// Copyright 2026, compose-rmuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package io.github.ra1nify.rmuix.kmp.basic

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
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

/**
 * Renders the stock functional-button composition: field, `coui_search_bar_functional_button_start_gap`
 * (12dp), the 1dp x 16dp divider and the 152dp-max capsule, in both themes.
 */
@OptIn(ExperimentalTestApi::class)
class SearchBarScreenshotTest {
    @Test
    fun lightComposition() = render("searchbar-light", dark = false)

    @Test
    fun darkComposition() = render("searchbar-dark", dark = true)

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
                Box(Modifier.background(RmuixTheme.colorScheme.background).padding(16.dp)) {
                    SearchBar(
                        inputField = {
                            InputField(
                                query = "hello",
                                onQueryChange = {},
                                onSearch = {},
                                expanded = true,
                                onExpandedChange = {},
                                label = "Search",
                            )
                        },
                        onExpandedChange = {},
                        expanded = true,
                        modifier = Modifier.widthIn(max = 400.dp),
                        outsideEndAction = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Spacer(Modifier.width(12.dp))
                                Box(
                                    Modifier
                                        .width(1.dp)
                                        .height(16.dp)
                                        .background(RmuixTheme.colorScheme.onSurfaceVariantSummary.copy(alpha = 0.2f)),
                                )
                                SearchBarFunctionButton(text = "Cancel", onClick = {})
                            }
                        },
                    ) { }
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
