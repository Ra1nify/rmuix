// Copyright 2026, compose-rmuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package io.github.ra1nify.rmuix.kmp.window.platform.windows

import com.sun.jna.platform.win32.WinDef
import com.sun.jna.platform.win32.WinUser
import com.sun.jna.platform.win32.WinUser.WindowProc

internal abstract class BasicWindowProc(
    val originalHwnd: WinDef.HWND,
) : WindowProc {
    val originalWindowProc =
        User32Ex.INSTANCE.SetWindowLongPtr(originalHwnd, WinUser.GWL_WNDPROC, this)

    override fun callback(
        hwnd: WinDef.HWND,
        uMsg: Int,
        wParam: WinDef.WPARAM,
        lParam: WinDef.LPARAM,
    ): WinDef.LRESULT = User32Ex.INSTANCE.CallWindowProc(
        proc = originalWindowProc,
        hWnd = hwnd,
        uMsg = uMsg,
        wParam = wParam,
        lParam = lParam,
    )
}
