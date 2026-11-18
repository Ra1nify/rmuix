// Copyright 2026, compose-rmuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyListScope
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
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.unit.dp
import io.github.ra1nify.rmuix.kmp.basic.Button
import io.github.ra1nify.rmuix.kmp.basic.ButtonDefaults
import io.github.ra1nify.rmuix.kmp.basic.ButtonDeformAnchor
import io.github.ra1nify.rmuix.kmp.basic.ButtonTouchMotionConfig
import io.github.ra1nify.rmuix.kmp.basic.LocalButtonTouchMotionConfig
import io.github.ra1nify.rmuix.kmp.basic.CategoryTitle
import io.github.ra1nify.rmuix.kmp.basic.Text
import io.github.ra1nify.rmuix.kmp.basic.TextButton
import io.github.ra1nify.rmuix.kmp.shader.SpotlightStyle
import io.github.ra1nify.rmuix.kmp.shader.rmuixSpotlight
import io.github.ra1nify.rmuix.kmp.theme.RmuixTheme
import io.github.ra1nify.rmuix.kmp.utils.PressFeedbackType

fun LazyListScope.buttonSection() {
    item(key = "button") {
        var buttonText by remember { mutableStateOf("Secondary") }
        var submitButtonText by remember { mutableStateOf("Primary") }
        var clickCount by remember { mutableIntStateOf(0) }
        var submitClickCount by remember { mutableIntStateOf(0) }
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

        CategoryTitle(text = "Button")
        Row(
            modifier =
                Modifier
                    .padding(horizontal = 12.dp)
                    .padding(bottom = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Button(
                onClick = {
                    clickCount++
                    buttonText = "Click: $clickCount"
                },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(),
            ) {
                Text(buttonText)
            }
            Spacer(Modifier.width(12.dp))
            Button(
                onClick = {
                    submitClickCount++
                    submitButtonText = "Click: $submitClickCount"
                },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColorsPrimary(),
            ) {
                Text(submitButtonText)
            }
        }

        Row(
            modifier =
                Modifier
                    .padding(horizontal = 12.dp)
                    .padding(bottom = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Button(
                onClick = {},
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColorsDestructive(),
            ) {
                Text("Destructive")
            }
            Spacer(Modifier.width(12.dp))
            Button(
                onClick = {},
                enabled = false,
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColorsPrimary(),
            ) {
                Text("Disabled")
            }
        }

        CategoryTitle(text = "TextButton")
        Row(
            modifier =
                Modifier
                    .padding(horizontal = 12.dp)
                    .padding(bottom = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            TextButton(
                text = "Cancel",
                onClick = {},
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.textButtonColors(),
            )
            Spacer(Modifier.width(12.dp))
            TextButton(
                text = "Confirm",
                onClick = {},
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.textButtonColorsPrimary(),
            )
        }
        ButtonBaselineDemo()
    }
}

@Composable
private fun ButtonBaselineDemo() {
    val focusRequester = remember { FocusRequester() }
    var dragClicks by remember { mutableIntStateOf(0) }
    Column(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Button(
                onClick = {},
                minHeight = 64.dp,
                modifier = Modifier.focusRequester(focusRequester).focusProperties { canFocus = true },
            ) { Text("Tall / focus") }
            TextButton(text = "Focus", onClick = { focusRequester.requestFocus() })
        }
        TextButton(
            text = "A long label stays on one line and ellipsizes",
            onClick = {},
            modifier = Modifier.width(160.dp),
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
