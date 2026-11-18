// Copyright 2026, compose-rmuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package component

import LocalNavigator
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.ra1nify.rmuix.kmp.basic.Card
import io.github.ra1nify.rmuix.kmp.basic.CategoryTitle
import io.github.ra1nify.rmuix.kmp.preference.JumpPreference
import navigation.Route
import kotlin.random.Random

fun LazyListScope.otherPageSection() {
    item(key = "other") {
        val navigator = LocalNavigator.current
        CategoryTitle(text = "Other")
        Card(
            modifier = Modifier
                .padding(horizontal = 12.dp),
        ) {
            JumpPreference(
                title = "Navigation test",
                summary = "Navigate to a Navigation Page",
                onClick = { navigator.push(Route.Navigation(Random.nextLong().toString())) },
            )
            JumpPreference(
                title = "MultiScaffold Test",
                summary = "Navigate to a MultiScaffold Page",
                onClick = { navigator.push(Route.MultiScaffold) },
            )
            JumpPreference(
                title = "Nested Navigation Test",
                summary = "A NavDisplay nested inside an entry",
                onClick = { navigator.push(Route.NestedNav) },
            )
        }
    }
}
