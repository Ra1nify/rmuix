// Copyright 2026, compose-rmuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package io.github.ra1nify.rmuix.kmp.icon.extended

import androidx.compose.ui.graphics.vector.ImageVector
import io.github.ra1nify.rmuix.kmp.icon.RmuixIcons

/**
 * Left-pointing back arrow. Alias of [RmuixIcons.Back].
 *
 * The glyph is identical; this name exists so call sites that talk about “back navigation” can use
 * `RmuixIcons.ArrowBack` without depending on the shorter `Back` spelling. Nothing is duplicated:
 * every weight forwards to the already-generated [RmuixIcons.Back] vector.
 */
val RmuixIcons.ArrowBack: ImageVector
    get() = RmuixIcons.Regular.Back

val RmuixIcons.Light.ArrowBack: ImageVector
    get() = RmuixIcons.Light.Back

val RmuixIcons.Normal.ArrowBack: ImageVector
    get() = RmuixIcons.Normal.Back

val RmuixIcons.Regular.ArrowBack: ImageVector
    get() = RmuixIcons.Regular.Back

val RmuixIcons.Medium.ArrowBack: ImageVector
    get() = RmuixIcons.Medium.Back

val RmuixIcons.Demibold.ArrowBack: ImageVector
    get() = RmuixIcons.Demibold.Back
