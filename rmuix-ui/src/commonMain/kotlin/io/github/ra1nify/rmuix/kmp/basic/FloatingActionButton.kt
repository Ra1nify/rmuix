// Copyright 2025, compose-rmuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package io.github.ra1nify.rmuix.kmp.basic

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.NonRestartableComposable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.ra1nify.rmuix.kmp.theme.RmuixTheme

/**
 * A [FloatingActionButton] component with Rmuix style.
 *
 * @param onClick The callback when the [FloatingActionButton] is clicked.
 * @param modifier The modifier to be applied to the [FloatingActionButton].
 * @param shape The shape of the [FloatingActionButton].
 * @param containerColor The color of the [FloatingActionButton].
 * @param shadowElevation The shadow elevation of the [FloatingActionButton].
 * @param minWidth The minimum width of the [FloatingActionButton].
 * @param minHeight The minimum height of the [FloatingActionButton].
 * @param content The [Composable] content of the [FloatingActionButton].
 */
@Composable
@NonRestartableComposable
fun FloatingActionButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    shape: Shape = CircleShape,
    containerColor: Color = RmuixTheme.colorScheme.primary,
    shadowElevation: Dp = FloatingActionButtonDefaults.ShadowElevation,
    minWidth: Dp = FloatingActionButtonDefaults.MinWidth,
    minHeight: Dp = FloatingActionButtonDefaults.MinHeight,
    content: @Composable () -> Unit,
) {
    Surface(
        onClick = onClick,
        modifier =
            modifier
                .semantics { role = Role.Button },
        shape = shape,
        color = containerColor,
        shadowElevation = shadowElevation,
    ) {
        Box(
            modifier =
                Modifier.defaultMinSize(
                    minWidth = minWidth,
                    minHeight = minHeight,
                ),
            contentAlignment = Alignment.Center,
        ) {
            content()
        }
    }
}

/** Contains default values used by [FloatingActionButton]. */
object FloatingActionButtonDefaults {
    /** The default minimum width of the [FloatingActionButton] (`coui_floating_button_fab_normal_size`). */
    val MinWidth = FloatingActionButtonStockSize

    /** The default minimum height of the [FloatingActionButton] (`coui_floating_button_fab_normal_size`). */
    val MinHeight = FloatingActionButtonStockSize

    /** The minimum size of the mini variant (`coui_floating_button_fab_mini_size` = 54dp). */
    val MiniSize = FloatingActionButtonStockMiniSize

    /** The margin the button keeps from the screen edge (`coui_floating_button_fab_side_margin`). */
    val SideMargin = FloatingActionButtonStockSideMargin

    /** The default shadow elevation of the [FloatingActionButton] (`coui_shadow_elevation_caustic_fab`). */
    val ShadowElevation = FloatingActionButtonStockShadowElevation
}
