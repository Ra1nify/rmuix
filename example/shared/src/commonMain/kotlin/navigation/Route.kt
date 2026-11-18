// Copyright 2025, compose-rmuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package navigation

import io.github.ra1nify.rmuix.kmp.nav.core.NavKey
import kotlinx.serialization.Serializable

/**
 * Type-safe navigation keys for the app, backed by rmuix-nav.
 * Each destination is a NavKey (data object/data class) and can be saved/restored in the back stack.
 *
 * Every member must stay `@Serializable`: `Route` is a `@Serializable` sealed interface, so its
 * serializer is *closed* polymorphic and only knows the members that are themselves `@Serializable`.
 * Dropping one of those annotations is not a compile error - it silently removes the member from the
 * polymorphic scope, and the first `rememberSaveable` of the back stack then throws
 * `JsonEncodingException: Serializer for subclass '<X>' is not found in the polymorphic scope of
 * 'Route'`. `tools/stock-audit.ps1 -Check SerializableScope` guards this.
 */
@Serializable
sealed interface Route : NavKey {
    @Serializable
    data object Main : Route

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

    /** One component-category page from [component.ExampleCatalog]. */
    @Serializable
    data class Category(val key: String) : Route
}
