// Copyright 2026, compose-rmuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package io.github.ra1nify.rmuix.kmp.squircle

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import io.github.ra1nify.rmuix.kmp.shader.isRuntimeShaderSupported

/**
 * Global opt-out for squircle visuals (default `true`).
 */
val LocalSquircleEnabled = staticCompositionLocalOf { true }

/**
 * Whether shader-backed squircle silhouettes are active in the current composition — checks
 * platform [isRuntimeShaderSupported].
 */
@Composable
@ReadOnlyComposable
fun isSquircleEnabled(): Boolean = LocalSquircleEnabled.current && isRuntimeShaderSupported()
