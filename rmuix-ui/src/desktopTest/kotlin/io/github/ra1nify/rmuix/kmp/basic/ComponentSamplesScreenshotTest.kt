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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toAwtImage
import androidx.compose.ui.state.ToggleableState
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
class ComponentSamplesScreenshotTest {
    @Test
    fun lightSamples() = render("samples-light", dark = false)

    @Test
    fun darkSamples() = render("samples-dark", dark = true)

    private fun render(
        name: String,
        dark: Boolean,
    ) = runComposeUiTest {
        setContent {
            val controller = remember { ThemeController(colorSchemeMode = if (dark) ColorSchemeMode.Dark else ColorSchemeMode.Light) }
            RmuixTheme(controller = controller) {
                Box(Modifier.width(400.dp).background(RmuixTheme.colorScheme.background).padding(16.dp)) {
                    Samples()
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

    @Composable
    private fun Samples() {
        Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp), verticalAlignment = Alignment.CenterVertically) {
                Checkbox(state = ToggleableState.On, onClick = {})
                Checkbox(state = ToggleableState.Off, onClick = {})
                RadioButton(selected = true, onClick = {})
                RadioButton(selected = false, onClick = {})
                RedDot(mode = RedDotMode.PointWithNum(count = 3), withStroke = true)
            }
            Slider(value = 0.35f, onValueChange = {})
            LinearProgressIndicator(progress = 0.6f)
            CircularProgressIndicator(progress = 0.6f, size = 72.dp)
            TabRow(tabs = listOf("One", "Two", "Three"), selectedTabIndex = 0, onTabSelected = {})
            ChipTabRow(
                items = listOf(ChipTabItem("All"), ChipTabItem("Photos"), ChipTabItem("Videos")),
                selectedIndex = 0,
                onSelect = {},
            )
            TextField(value = "Hello", onValueChange = {})
            TextField(value = "", onValueChange = {}, lineMode = true, label = "Line mode")
            Stepper(value = 3, onValueChange = {}, minValue = 0, maxValue = 10)
            Card(modifier = Modifier.fillMaxWidth()) {
                Text(text = "Card content", modifier = Modifier.padding(16.dp))
            }
            TextField(
                value = "Hello",
                onValueChange = {},
                label = "Clearable",
                showClearButton = true,
                modifier = Modifier.fillMaxWidth(),
            )
            InfiniteProgressIndicator(size = 32.dp)
            Box(Modifier.fillMaxWidth()) {
                val hostState = remember { SnackbarHostState() }
                LaunchedEffect(hostState) { hostState.showSnackbar("Settings saved", actionLabel = "Undo") }
                SnackbarHost(state = hostState)
            }
            NavigationBar {
                NavigationBarItem(
                    selected = true,
                    onClick = {},
                    icon = RmuixIcons.Basic.Search,
                    label = "Inbox",
                )
                NavigationBarItem(
                    selected = false,
                    onClick = {},
                    icon = RmuixIcons.Basic.Search,
                    label = "Files",
                )
                NavigationBarItem(
                    selected = false,
                    onClick = {},
                    icon = RmuixIcons.Basic.Search,
                    label = "Me",
                )
            }
        }
    }
}
