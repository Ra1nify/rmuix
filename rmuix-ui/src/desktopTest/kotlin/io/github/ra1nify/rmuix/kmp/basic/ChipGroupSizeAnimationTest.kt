// Copyright 2026, compose-rmuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package io.github.ra1nify.rmuix.kmp.basic

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.runComposeUiTest
import androidx.compose.ui.unit.dp
import kotlin.test.Test
import kotlin.test.assertTrue

/**
 * `COUIChipGroup` animates the group's bounds when the chip count changes
 * (`mGroupHeightChangeAnimation` / `mGroupWidthChangeAnimation`, spring force response 0.35 / bounce 0).
 * This pins that a size change eases through an intermediate height instead of jumping.
 */
@OptIn(ExperimentalTestApi::class)
class ChipGroupSizeAnimationTest {
    @Test
    fun sizeChangeEasesThroughAnIntermediateHeight() =
        runComposeUiTest {
            mainClock.autoAdvance = false
            setContent { Sample() }
            mainClock.advanceTimeBy(1_000)
            waitForIdle()

            val narrowHeight = heightPx()
            onNodeWithTag("grow").performClick() // widen the group -> fewer rows
            mainClock.advanceTimeBy(50) // a few frames into the spring
            val midHeight = heightPx()
            mainClock.advanceTimeBy(2_000)
            waitForIdle()
            val wideHeight = heightPx()

            assertTrue(wideHeight < narrowHeight, "widening removes rows (narrow=$narrowHeight wide=$wideHeight)")
            assertTrue(
                midHeight < narrowHeight && midHeight > wideHeight,
                "the group eases between heights (mid=$midHeight narrow=$narrowHeight wide=$wideHeight)",
            )
        }

    private fun androidx.compose.ui.test.ComposeUiTest.heightPx(): Int =
        onNodeWithTag("group").fetchSemanticsNode().size.height

    @Composable
    private fun Sample() {
        var wide by remember { mutableStateOf(false) }
        Column {
            Button(onClick = { wide = !wide }, modifier = Modifier.testTag("grow")) {
                Text("grow")
            }
            Box(Modifier.width(if (wide) 520.dp else 220.dp)) {
                ChipGroup(modifier = Modifier.testTag("group")) {
                    repeat(12) { index ->
                        Chip(onClick = {}, label = "Chip ${index + 1}")
                    }
                }
            }
        }
    }
}
