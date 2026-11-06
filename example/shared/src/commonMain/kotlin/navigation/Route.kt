// Copyright 2025, compose-rmuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package navigation

import io.github.ra1nify.rmuix.kmp.nav.core.NavKey
import kotlinx.serialization.Serializable

/**
 * Type-safe navigation keys for the app, backed by rmuix-nav.
 * Each destination is a NavKey (data object/data class) and can be saved/restored in the back stack.
 */
@Serializable
sealed interface Route : NavKey {
    @Serializable
    data object Main : Route

    @Serializable
    data object PullToRefresh : Route

    @Serializable
    data object About : Route

    @Serializable
    data object License : Route

    @Serializable
    data class Navigation(val id: String) : Route

    @Serializable
    data object MultiScaffold : Route

    @Serializable
    data object NestedNav : Route

    @Serializable
    data object OverscrollLoadMore : Route
}
