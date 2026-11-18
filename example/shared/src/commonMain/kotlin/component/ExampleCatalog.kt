// Copyright 2026, compose-rmuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package component

import androidx.compose.runtime.Immutable

/**
 * One second-level page of the example app's component archive. The categories mirror the docs site's
 * ten categories so the demo app and the documentation classify the controls the same way.
 */
@Immutable
data class ExampleCategory(
    val key: String,
    val title: String,
    val description: String,
    val sectionCount: Int,
)

/**
 * The component archive shown on the example home page. Each entry is rendered by [CategoryPage]
 * (route `Route.Category(key)`) and groups the section composables that used to live on the home page.
 */
object ExampleCatalog {
    val categories: List<ExampleCategory> = listOf(
        ExampleCategory(
            key = "containers",
            title = "Containers & Layout",
            description = "Page rows, grouped content, cards and empty states",
            sectionCount = 4,
        ),
        ExampleCategory(
            key = "text-and-icons",
            title = "Text & Icons",
            description = "Typography and icon display",
            sectionCount = 1,
        ),
        ExampleCategory(
            key = "buttons",
            title = "Buttons & Toolbars",
            description = "Actions, loading states and floating toolbars",
            sectionCount = 4,
        ),
        ExampleCategory(
            key = "input",
            title = "Input & Forms",
            description = "Text, numeric, date/time and secure input",
            sectionCount = 11,
        ),
        ExampleCategory(
            key = "selection",
            title = "Selection & Adjustment",
            description = "Toggles, exclusive choices, sliders and chips",
            sectionCount = 6,
        ),
        ExampleCategory(
            key = "navigation",
            title = "Navigation",
            description = "Bottom bars, drawers, tabs and navigation tests",
            sectionCount = 4,
        ),
        ExampleCategory(
            key = "feedback",
            title = "Feedback & Status",
            description = "Progress, messages, hints, badges and list helpers",
            sectionCount = 8,
        ),
        ExampleCategory(
            key = "overlays",
            title = "Overlays & Menus",
            description = "Dialogs, sheets, glass surfaces and menus",
            sectionCount = 5,
        ),
        ExampleCategory(
            key = "preferences",
            title = "Preferences",
            description = "Settings rows built on BasicComponent",
            sectionCount = 2,
        ),
        ExampleCategory(
            key = "media",
            title = "Media & Effects",
            description = "Images, pickers, croppers, spotlight and blur",
            sectionCount = 5,
        ),
    )

    fun category(key: String): ExampleCategory? = categories.firstOrNull { it.key == key }
}
