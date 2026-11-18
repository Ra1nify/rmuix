// Copyright 2026, compose-rmuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package io.github.ra1nify.rmuix.kmp.preference

import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.test.Test
import kotlin.test.assertEquals

class SliderPreferenceDefaultsTest {
    @Test
    fun endLabelsMatchSeekBarPreference() {
        // text_descripe_seekbar_value
        assertEquals(14.sp, SliderPreferenceDefaults.LabelTextSize)
        // text_descripe_seekbar_padding
        assertEquals(10.dp, SliderPreferenceDefaults.LabelPadding)
    }
}
