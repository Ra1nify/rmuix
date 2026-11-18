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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
class ComponentGalleryScreenshotTest {
    @Test
    fun lightGallery() = renderGallery("light", dark = false)

    @Test
    fun darkGallery() = renderGallery("dark", dark = true)

    private fun renderGallery(
        name: String,
        dark: Boolean,
    ) = runComposeUiTest {
        setContent {
            val controller = remember { ThemeController(colorSchemeMode = if (dark) ColorSchemeMode.Dark else ColorSchemeMode.Light) }
            RmuixTheme(controller = controller) {
                Box(Modifier.width(400.dp).background(RmuixTheme.colorScheme.background).padding(16.dp)) {
                    Gallery()
                }
            }
        }
        waitForIdle()
        val image = onRoot().captureToImage().toAwtImage()
        // Tests run with the module directory as the working directory; walk up to the repo root.
        var root = File(System.getProperty("user.dir")).absoluteFile
        while (root.parentFile != null && !File(root, "settings.gradle.kts").exists()) {
            root = root.parentFile
        }
        val dir = File(root, "docs/dev/shots").apply { mkdirs() }
        val file = File(dir, "gallery-$name.png")
        ImageIO.write(image, "png", file)
        assertTrue(file.length() > 0, "screenshot should be written")
    }

    @Composable
    private fun Gallery() {
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            SmallTopAppBar(
                title = "Settings",
                subtitle = "Connected to Wi-Fi",
            )
            Box(Modifier.fillMaxWidth()) {
                BasicComponent(
                    title = "Wi-Fi",
                    summary = "Connected",
                    startAction = {
                        Icon(
                            imageVector = RmuixIcons.Basic.Search,
                            contentDescription = null,
                            modifier = Modifier.size(24.dp),
                        )
                    },
                    endActions = { Switch(checked = true, onCheckedChange = {}) },
                )
            }
            Box(Modifier.fillMaxWidth()) {
                BasicComponent(
                    title = "Bluetooth",
                    summary = "Off",
                    endActions = { Switch(checked = false, onCheckedChange = {}) },
                )
            }
            InputField(
                query = "",
                onQueryChange = {},
                onSearch = {},
                expanded = true,
                onExpandedChange = {},
                label = "Search settings",
            )
            var hour by remember { mutableIntStateOf(8) }
            var minute by remember { mutableIntStateOf(30) }
            NumberPickerGroup(modifier = Modifier.fillMaxWidth()) {
                NumberPicker(
                    value = hour,
                    onValueChange = { hour = it },
                    range = 0..23,
                    label = { it.toString().padStart(2, '0') },
                    modifier = Modifier.width(62.dp),
                    wrapAround = true,
                )
                Text(text = ":")
                NumberPicker(
                    value = minute,
                    onValueChange = { minute = it },
                    range = 0..59,
                    label = { it.toString().padStart(2, '0') },
                    modifier = Modifier.width(62.dp),
                    wrapAround = true,
                )
            }
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
                Button(onClick = {}) { Text("OK") }
                TextButton(text = "Cancel", onClick = {})
            }
            Text(text = "Hairline divider")
            HorizontalDivider()
        }
    }
}
