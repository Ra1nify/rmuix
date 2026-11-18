// Copyright 2026, compose-rmuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package io.github.ra1nify.rmuix.kmp.basic

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.toAwtImage
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.runComposeUiTest
import androidx.compose.ui.unit.dp
import io.github.ra1nify.rmuix.kmp.theme.RmuixTheme
import java.io.File
import javax.imageio.ImageIO
import kotlin.test.Test

/** Writes the `COUIToolTips` vs `COUIToolTips.DetailFloating` comparison the docs link to. */
@OptIn(ExperimentalTestApi::class)
class TooltipDetailFloatingScreenshotTest {
    @Test
    fun writeDetailFloatingComparison() =
        runComposeUiTest {
            setContent {
                Column(
                    modifier =
                        Modifier
                            .background(RmuixTheme.colorScheme.surface)
                            .padding(16.dp)
                            .testTag("sample"),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    Bubble(
                        label = "COUIToolTips (plain)",
                        container = TooltipDefaults.plainTooltipContainerColor,
                        content = TooltipDefaults.plainTooltipContentColor,
                        cap = TooltipDefaults.PlainTooltipInsideMargin,
                    )
                    Bubble(
                        label = "COUIToolTips.DetailFloating",
                        container = TooltipDefaults.detailFloatingContainerColor(darkTheme = false),
                        content = TooltipDefaults.detailFloatingContentColor(darkTheme = false),
                        cap = TooltipDefaults.DetailFloatingInsideMargin,
                    )
                }
            }
            val image = onNodeWithTag("sample").captureToImage().toAwtImage()
            // Walk up to the repo root: the test JVM's working directory is the module, and writing a
            // module-relative path leaves a stray `rmuix-ui/docs/...` tree behind.
            var root = File(System.getProperty("user.dir")).absoluteFile
            while (root.parentFile != null && !File(root, "settings.gradle.kts").exists()) root = root.parentFile
            val dir = File(root, "docs/dev/shots").apply { mkdirs() }
            ImageIO.write(image, "png", File(dir, "tooltip-detail-floating.png"))
        }

    @Composable
    private fun Bubble(
        label: String,
        container: androidx.compose.ui.graphics.Color,
        content: androidx.compose.ui.graphics.Color,
        cap: androidx.compose.foundation.layout.PaddingValues,
    ) {
        Box(
            modifier =
                Modifier
                    .background(color = container, shape = RoundedCornerShape(TooltipDefaults.PlainTooltipCornerRadius))
                    .padding(cap),
        ) {
            Text(text = label, color = content)
        }
    }
}
