// Copyright 2026, compose-rmuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package io.github.ra1nify.rmuix.kmp.icon.extended

import androidx.compose.ui.graphics.vector.ImageVector
import io.github.ra1nify.rmuix.kmp.icon.RmuixIcons

/**
 * Right-pointing chevron, used as a “go to detail” affordance. Alias of [RmuixIcons.ChevronForward].
 *
 * `ChevronForward` already points right; this name exists so preference/row call sites can use the
 * direction-explicit `ChevronRight` spelling. No path data is duplicated.
 */
val RmuixIcons.ChevronRight: ImageVector
    get() = RmuixIcons.Regular.ChevronForward

val RmuixIcons.Light.ChevronRight: ImageVector
    get() = RmuixIcons.Light.ChevronForward

val RmuixIcons.Normal.ChevronRight: ImageVector
    get() = RmuixIcons.Normal.ChevronForward

val RmuixIcons.Regular.ChevronRight: ImageVector
    get() = RmuixIcons.Regular.ChevronForward

val RmuixIcons.Medium.ChevronRight: ImageVector
    get() = RmuixIcons.Medium.ChevronForward

val RmuixIcons.Demibold.ChevronRight: ImageVector
    get() = RmuixIcons.Demibold.ChevronForward
