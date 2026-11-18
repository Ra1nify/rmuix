// Copyright 2026, compose-rmuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.ra1nify.rmuix.kmp.basic.Card
import io.github.ra1nify.rmuix.kmp.basic.Icon
import io.github.ra1nify.rmuix.kmp.basic.IconButton
import io.github.ra1nify.rmuix.kmp.basic.RichTooltipBox
import io.github.ra1nify.rmuix.kmp.basic.CategoryTitle
import io.github.ra1nify.rmuix.kmp.basic.TooltipBox
import io.github.ra1nify.rmuix.kmp.basic.TooltipDefaults
import io.github.ra1nify.rmuix.kmp.basic.rememberTooltipState
import io.github.ra1nify.rmuix.kmp.icon.RmuixIcons
import io.github.ra1nify.rmuix.kmp.icon.extended.Edit
import io.github.ra1nify.rmuix.kmp.icon.extended.Info

fun LazyListScope.tooltipSection() {
    item(key = "tooltip") {
        CategoryTitle(text = "Tooltip")
        val richState = rememberTooltipState(isPersistent = true)
        Card(
            modifier = Modifier
                .padding(horizontal = 12.dp)
                .padding(bottom = 12.dp),
        ) {
            Column(
                modifier = Modifier.padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                ) {
                    TooltipBox(text = "Edit") {
                        IconButton(onClick = {}) {
                            Icon(imageVector = RmuixIcons.Edit, contentDescription = "Edit")
                        }
                    }
                    // `COUIToolTips.DetailFloating`: opaque surface, neutral text, 12dp on all four margins.
                    TooltipBox(
                        text = "DetailFloating",
                        containerColor = TooltipDefaults.detailFloatingContainerColor(),
                        contentColor = TooltipDefaults.detailFloatingContentColor(),
                        containerMargins = TooltipDefaults.DetailFloatingContainerMargins,
                    ) {
                        IconButton(onClick = {}) {
                            Icon(imageVector = RmuixIcons.Info, contentDescription = "DetailFloating")
                        }
                    }
                    RichTooltipBox(
                        title = "Rich tooltip",
                        text = "Rich tooltips show a title, supporting text, and an optional action. " +
                            "Move onto the tooltip to use the action, or tap outside to dismiss.",
                        actionText = "Got it",
                        onActionClick = {},
                        state = richState,
                    ) {
                        IconButton(onClick = {}) {
                            Icon(imageVector = RmuixIcons.Info, contentDescription = "Rich tooltip")
                        }
                    }
                }
            }
        }
    }
}
