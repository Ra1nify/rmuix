// Copyright 2026, compose-rmuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package io.github.ra1nify.rmuix.kmp.window.platform.windows.structure

import com.sun.jna.Structure

@Structure.FieldOrder(
    "leftBorderWidth",
    "rightBorderWidth",
    "topBorderHeight",
    "bottomBorderHeight",
)
internal open class WindowMargins : Structure() {
    @JvmField var leftBorderWidth: Int = -1

    @JvmField var rightBorderWidth: Int = -1

    @JvmField var topBorderHeight: Int = -1

    @JvmField var bottomBorderHeight: Int = -1

    class ByReference :
        WindowMargins(),
        Structure.ByReference
}
