// Copyright 2025, compose-rmuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package component

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import io.github.ra1nify.rmuix.kmp.basic.Icon
import io.github.ra1nify.rmuix.kmp.basic.IconButton
import io.github.ra1nify.rmuix.kmp.icon.RmuixIcons
import io.github.ra1nify.rmuix.kmp.icon.extended.Back
import io.github.ra1nify.rmuix.kmp.theme.RmuixTheme.colorScheme

@Composable
fun BackNavigationIcon(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val layoutDirection = LocalLayoutDirection.current
    IconButton(
        modifier = modifier,
        onClick = onClick,
    ) {
        Icon(
            modifier = Modifier.graphicsLayer {
                if (layoutDirection == LayoutDirection.Rtl) scaleX = -1f
            },
            imageVector = RmuixIcons.Back,
            contentDescription = null,
            tint = colorScheme.onBackground,
        )
    }
}
