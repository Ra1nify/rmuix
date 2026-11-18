// Copyright 2025, compose-rmuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.unit.dp
import io.github.ra1nify.rmuix.kmp.basic.Button
import io.github.ra1nify.rmuix.kmp.basic.ButtonDefaults
import io.github.ra1nify.rmuix.kmp.basic.ButtonDeformAnchor
import io.github.ra1nify.rmuix.kmp.basic.ButtonTouchMotionConfig
import io.github.ra1nify.rmuix.kmp.basic.Icon
import io.github.ra1nify.rmuix.kmp.basic.LocalButtonTouchMotionConfig
import io.github.ra1nify.rmuix.kmp.basic.Text
import io.github.ra1nify.rmuix.kmp.basic.TextButton
import io.github.ra1nify.rmuix.kmp.icon.RmuixIcons
import io.github.ra1nify.rmuix.kmp.icon.extended.Contacts
import io.github.ra1nify.rmuix.kmp.icon.extended.Favorites
import io.github.ra1nify.rmuix.kmp.shader.SpotlightStyle
import io.github.ra1nify.rmuix.kmp.shader.rmuixSpotlight
import io.github.ra1nify.rmuix.kmp.theme.RmuixTheme
import io.github.ra1nify.rmuix.kmp.utils.PressFeedbackType

@Composable
fun ButtonDemo() {
    Box(
        modifier =
            Modifier
                .fillMaxSize()
                .background(demoBackground()),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            Modifier
                .padding(16.dp)
                .widthIn(max = 600.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            var spotlightClicks by remember { mutableIntStateOf(0) }
            val spotlightDark = RmuixTheme.colorScheme.background.luminance() < RmuixTheme.colorScheme.onBackground.luminance()
            Text("Shared stock spotlight / clicks: $spotlightClicks")
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = { spotlightClicks++ },
                    pressFeedbackType = PressFeedbackType.None,
                    modifier =
                        Modifier.rmuixSpotlight(
                            shape = RoundedCornerShape(50),
                            style = SpotlightStyle.OpaqueMedium,
                            darkTheme = spotlightDark,
                        ),
                    colors = ButtonDefaults.buttonColorsPrimary(),
                ) { Text("Opaque") }
                Button(
                    onClick = { spotlightClicks++ },
                    pressFeedbackType = PressFeedbackType.None,
                    modifier =
                        Modifier.rmuixSpotlight(
                            shape = RoundedCornerShape(50),
                            style = SpotlightStyle.OpaqueSecondary,
                            darkTheme = spotlightDark,
                        ),
                ) { Text("Secondary") }
            }
            var buttonText1 by remember { mutableStateOf("Button") }
            var buttonText2 by remember { mutableStateOf("TextButton") }
            var clickCount1 by remember { mutableIntStateOf(0) }
            var clickCount2 by remember { mutableIntStateOf(0) }
            val focusRequester = remember { FocusRequester() }
            var dragClicks by remember { mutableIntStateOf(0) }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                Button(
                    onClick = {
                        clickCount1++
                        buttonText1 = "Button: $clickCount1"
                    },
                ) {
                    Icon(
                        imageVector = RmuixIcons.Favorites,
                        contentDescription = "Favorites",
                        modifier = Modifier.size(24.dp),
                    )
                    Text(
                        text = buttonText1,
                        style = RmuixTheme.textStyles.button,
                        modifier = Modifier.padding(start = 8.dp),
                    )
                }
                TextButton(
                    text = buttonText2,
                    onClick = {
                        clickCount2++
                        buttonText2 = "TextButton: $clickCount2"
                    },
                    colors = ButtonDefaults.textButtonColorsPrimary(),
                )
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                Button(
                    enabled = false,
                    onClick = {},
                    colors = ButtonDefaults.buttonColorsPrimary(),
                ) {
                    Icon(
                        imageVector = RmuixIcons.Contacts,
                        contentDescription = null,
                        modifier = Modifier.size(24.dp),
                    )
                    Text(
                        text = "Disabled Button",
                        style = RmuixTheme.textStyles.button,
                        modifier = Modifier.padding(start = 8.dp),
                    )
                }
                TextButton(
                    text = "Disabled TextButton",
                    enabled = false,
                    onClick = {},
                )
            }
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Button(
                    onClick = {},
                    modifier = Modifier.height(64.dp).focusRequester(focusRequester).focusProperties { canFocus = true },
                ) { Text("Tall / focus") }
                TextButton(text = "Focus", onClick = { focusRequester.requestFocus() })
            }
            TextButton(
                text = "A long label stays on one line and ellipsizes",
                onClick = {},
                modifier = Modifier.widthIn(min = 160.dp, max = 160.dp),
            )
            Text("Drag across an edge / scroll to cancel. Clicks: $dragClicks")
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Button(onClick = { dragClicks++ }) { Text("Drag") }
                Button(
                    onClick = { dragClicks++ },
                    pressFeedbackType = PressFeedbackType.None,
                ) { Text("No motion") }
            }
            Text("Optional anchors: left / automatic; default stays centered")
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                CompositionLocalProvider(
                    LocalButtonTouchMotionConfig provides ButtonTouchMotionConfig(deformAnchor = ButtonDeformAnchor.LeftCenter),
                ) {
                    Button(onClick = {}) { Text("Left anchor") }
                }
                CompositionLocalProvider(
                    LocalButtonTouchMotionConfig provides ButtonTouchMotionConfig(autoDeformAnchor = true),
                ) {
                    Button(onClick = {}) { Text("Auto anchor") }
                }
            }
        }
    }
}
