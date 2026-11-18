// Copyright 2026, compose-rmuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package io.github.ra1nify.rmuix.kmp.squircle

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import io.github.ra1nify.rmuix.kmp.shader.isRuntimeShaderSupported

/**
 * Whether shader-backed squircle silhouettes are active in the current composition.
 *
 * ColorOS 17 enables continuous-curvature corners globally, so there is no user-facing toggle:
 * the answer is purely a platform capability check ([isRuntimeShaderSupported]). Where the runtime
 * shader is unavailable every squircle API degrades to a plain rounded rectangle, which is why this
 * value is forwarded to `addSquircleRect(squircleEnabled = …)` and [SquircleShape] so that fills,
 * shadows and borders degrade together instead of splitting into two shape families.
 */
@Composable
@ReadOnlyComposable
fun isSquircleEnabled(): Boolean = isRuntimeShaderSupported()
