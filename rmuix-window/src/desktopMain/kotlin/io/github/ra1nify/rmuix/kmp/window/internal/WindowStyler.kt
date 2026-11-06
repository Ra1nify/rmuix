// Copyright 2026, compose-rmuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package io.github.ra1nify.rmuix.kmp.window.internal

import io.github.ra1nify.rmuix.kmp.window.RmuixWindowBackgroundType

internal interface WindowStyler {
    fun updateIsResizable(value: Boolean)
    fun updateBackground(type: RmuixWindowBackgroundType, isDarkTheme: Boolean)
}
