// Copyright 2026, compose-rmuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package io.github.ra1nify.rmuix.kmp.basic

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.v2.runComposeUiTest
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.ra1nify.rmuix.kmp.theme.RmuixTheme
import kotlin.math.abs
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@OptIn(ExperimentalTestApi::class)
class BasicComponentLayoutTest {
    @Test
    fun insetsFollowTheStockPreferenceLayout() = runComposeUiTest {
        setContent {
            RmuixTheme {
                Box(Modifier.size(360.dp, 120.dp).background(Color.White)) {
                    BasicComponent(
                        title = "Title",
                        modifier = Modifier.width(360.dp).testTag("row"),
                        startAction = { Box(Modifier.size(24.dp).background(Color.Red).testTag("start")) },
                        endActions = { Switch(checked = true, onCheckedChange = {}, modifier = Modifier.testTag("end")) },
                    )
                }
            }
        }
        waitForIdle()
        val row = onNodeWithTag("row").fetchSemanticsNode().boundsInRoot
        val start = onNodeWithTag("start").fetchSemanticsNode().boundsInRoot
        val end = onNodeWithTag("end").fetchSemanticsNode().boundsInRoot
        assertTrue(abs(row.height - 48f) <= 0.5f, "row height should be 48dp, was ${row.height}")
        assertTrue(
            abs((start.left - row.left) - 32f) <= 0.5f,
            "startAction should sit at the 32dp inside margin, was ${start.left - row.left}",
        )
        assertTrue(
            abs((row.right - end.right) - 32f) <= 0.5f,
            "endActions should end at the 32dp inside margin, was ${row.right - end.right}",
        )
    }

    @Test
    fun longTitleKeepsEndActionsInsideTheRow() = runComposeUiTest {
        setContent {
            RmuixTheme {
                Box(Modifier.size(320.dp, 120.dp).background(Color.White)) {
                    BasicComponent(
                        title = "A very long title that must truncate instead of pushing the trailing switch out of the row",
                        modifier = Modifier.width(320.dp).testTag("row"),
                        endActions = { Switch(checked = false, onCheckedChange = {}, modifier = Modifier.testTag("end")) },
                    )
                }
            }
        }
        waitForIdle()
        val row = onNodeWithTag("row").fetchSemanticsNode().boundsInRoot
        val end = onNodeWithTag("end").fetchSemanticsNode().boundsInRoot
        assertTrue(end.width > 0f, "the trailing switch must keep a non-zero width")
        assertTrue(
            end.right <= row.right - 32f + 0.5f,
            "the trailing switch must stay inside the row: end=${end.right}, row=${row.right}",
        )
        assertTrue(
            end.left > row.left + 32f,
            "the switch must not be pushed over the start margin",
        )
        assertTrue(
            end.top >= row.top && end.bottom <= row.bottom,
            "the switch must stay vertically inside the row: ${end.top}..${end.bottom} vs ${row.top}..${row.bottom}",
        )
        // A wrapping title grows the row (the stock TextView has no maxLines); it must never clip the row.
        assertTrue(row.height >= 48f, "the row must keep its 48dp minimum, was ${row.height}")
    }

    @Test
    fun multilineIconTopMarginUsesTheRealLineMetrics() = runComposeUiTest {
        val longTitle =
            "This title is deliberately long enough to wrap onto at least three lines so that the " +
                "leading icon must switch to the stock top-gravity alignment instead of centring"
        var titleLineCount = 0
        var secondLineBottomDp = 0f
        setContent {
            RmuixTheme {
                val density = LocalDensity.current
                Box(Modifier.size(360.dp, 200.dp).background(Color.White)) {
                    BasicComponent(
                        title = longTitle,
                        modifier = Modifier.width(360.dp).testTag("row"),
                        startAction = { Box(Modifier.size(24.dp).background(Color.Red).testTag("start")) },
                    )
                    // The text column measures the title at the same 256dp the row gives it.
                    Text(
                        text = longTitle,
                        fontSize = 16.sp,
                        fontWeight = androidx.compose.ui.text.font.FontWeight.Medium,
                        modifier = Modifier.width(256.dp),
                        onTextLayout = { result: TextLayoutResult ->
                            titleLineCount = result.lineCount
                            secondLineBottomDp = with(density) { result.getLineBottom(1).toDp().value }
                        },
                    )
                }
            }
        }
        waitForIdle()
        assertTrue(titleLineCount >= 3, "the title must wrap to at least three lines, was $titleLineCount")
        val row = onNodeWithTag("row").fetchSemanticsNode().boundsInRoot
        val start = onNodeWithTag("start").fetchSemanticsNode().boundsInRoot
        // setIconTopInParent(): centre of the first two lines - iconHeight / 2, min 8dp.
        val expectedMargin = (10f + secondLineBottomDp / 2f - 12f).coerceAtLeast(8f)
        assertTrue(
            abs((start.top - row.top) - expectedMargin) <= 0.6f,
            "icon top margin must follow the real line metrics: expected $expectedMargin, was ${start.top - row.top}",
        )
    }

    @Test
    fun multilineTitleUsesTheStockLineSpacingMultiplier() = runComposeUiTest {
        val title = "This title is long enough to wrap onto two lines at the narrow text column width"
        setContent {
            RmuixTheme {
                Box(Modifier.size(240.dp, 320.dp).background(Color.White)) {
                    BasicComponent(
                        title = title,
                        modifier = Modifier.width(200.dp).testTag("row"),
                    )
                    // Same 136dp text width as the row's content column, without the multiplier.
                    Text(
                        text = title,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.width(136.dp).align(Alignment.BottomStart),
                    )
                }
            }
        }
        waitForIdle()
        val nodes = onAllNodesWithText(title).fetchSemanticsNodes()
        assertEquals(2, nodes.size, "expected the row title and the reference text")
        val row = onNodeWithTag("row").fetchSemanticsNode().boundsInRoot
        val componentTitle = nodes.first { it.boundsInRoot.bottom < row.bottom }
        val referenceTitle = nodes.first { it.boundsInRoot.bottom >= row.bottom }
        assertTrue(componentTitle.boundsInRoot.height > 0f, "the row title must be laid out")
        assertTrue(referenceTitle.boundsInRoot.height > 0f, "the reference text must be laid out")
        val ratio = componentTitle.boundsInRoot.height / referenceTitle.boundsInRoot.height
        assertEquals(
            BasicComponentDefaults.TitleLineSpacingMultiplier,
            ratio,
            0.02f,
            "the title height must be scaled by the stock 1.05 line-spacing multiplier",
        )
    }
}
