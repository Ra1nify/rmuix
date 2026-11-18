// Copyright 2026, compose-rmuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package io.github.ra1nify.rmuix.kmp.glass

import android.os.Build
import android.view.WindowManager
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.window.DialogWindowProvider

/**
 * Android implementation: asks the platform compositor to blur whatever is behind the dialog window.
 *
 * `Window.setBackgroundBlurRadius` exists from API 31 and requires `FLAG_BLUR_BEHIND`; the radius is in
 * pixels, which is exactly the unit of the stock `coui_dialog_blur_radius` token.
 */
@Composable
internal actual fun ApplyWindowBackgroundBlur(radiusPx: Int) {
    val view = LocalView.current
    SideEffect {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) return@SideEffect
        val window = (view.parent as? DialogWindowProvider)?.window ?: return@SideEffect
        window.setBackgroundBlurRadius(radiusPx)
        window.addFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND)
    }
}
