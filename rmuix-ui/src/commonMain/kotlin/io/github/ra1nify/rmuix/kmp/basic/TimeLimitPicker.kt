// Copyright 2026, compose-rmuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package io.github.ra1nify.rmuix.kmp.basic

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * The labels of a [TimeLimitPicker].
 *
 * @param hour Formats the hour wheel (`0..23` in 24-hour mode, `1..12` otherwise).
 * @param minute Formats the minute wheel, which is always zero-padded here.
 * @param am The morning label (`R.string.morning`).
 * @param pm The afternoon label (`R.string.afternoon`).
 * @param hourUnit The unit drawn after the hour wheel (`R.string.coui_hour`).
 * @param minuteUnit The unit drawn after the minute wheel (`R.string.coui_minute`).
 */
@Stable
data class TimeLimitPickerLabels(
    val hour: (Int) -> String,
    val minute: (Int) -> String,
    val am: String,
    val pm: String,
    val hourUnit: String,
    val minuteUnit: String,
)

/**
 * Defaults for [TimeLimitPicker], from `coui_time_limit_picker.xml` and `COUITimeLimitPicker`.
 */
object TimeLimitPickerDefaults {
    /** `coui_number_picker_width_middle`: the hour wheel's `couiPickerVisualWidth`. */
    val HourWidth = 76.dp

    /** `coui_number_picker_width_middle`: the minute wheel's `couiPickerVisualWidth`. */
    val MinuteWidth = 76.dp

    /** `coui_number_picker_width_small`: the AM/PM wheel. */
    val AmPmWidth = 62.dp

    /**
     * `coui_number_picker_view_padding_vertical`, carried by the row. The wheels are
     * `coui_timepicker_height` (135dp = three 45dp rows) tall, so they are drawn without
     * [NumberPickerDefaults.VerticalPadding].
     */
    val VerticalPadding = 12.dp

    /** `coui_timepicker_text_size`, applied to both unit labels. */
    val UnitTextSize = 16.sp

    /** `android:layout_width` of the hour unit label in `coui_time_limit_picker.xml`. */
    val HourUnitWidth = 50.dp

    /**
     * `android:layout_marginLeft="-10dp"` on the hour unit label: the unit is pulled left so it sits
     * right against the selected hour. A negative margin has no direct Compose equivalent, so it is
     * applied as a layout-neutral [offset].
     */
    val HourUnitOffsetStart = (-10).dp

    /** `android:paddingLeft` of the minute unit label in `coui_time_limit_picker.xml`. */
    val MinuteUnitPaddingStart = 56.dp

    /** `R.string.coui_hour`: the hour unit. */
    val HourUnit = "\u5c0f\u65f6"

    /** `R.string.coui_minute`: the minute unit. */
    val MinuteUnit = "\u5206\u949f"

    /** `R.string.morning`. */
    val AmLabel = "\u4e0a\u5348"

    /** `R.string.afternoon`. */
    val PmLabel = "\u4e0b\u5348"

    /**
     * Builds the stock labels.
     *
     * `COUITimeLimitPicker` calls `setTwoDigitFormatter()` on the hour wheel in 24-hour mode and on the
     * minute wheel unconditionally, so both are zero-padded - unlike [TimePicker], whose minute wheel
     * uses `TimeModel.NUMBER_FORMAT`.
     *
     * @param is24Hour Whether the hour wheel shows `0..23`.
     * @param hourUnit The hour unit label.
     * @param minuteUnit The minute unit label.
     * @param am The morning label.
     * @param pm The afternoon label.
     */
    fun labels(
        is24Hour: Boolean = true,
        hourUnit: String = HourUnit,
        minuteUnit: String = MinuteUnit,
        am: String = AmLabel,
        pm: String = PmLabel,
    ): TimeLimitPickerLabels {
        val hourLabel: (Int) -> String =
            if (is24Hour) {
                { it.toString().padStart(2, '0') }
            } else {
                { it.toString() }
            }
        val minuteLabel: (Int) -> String = { it.toString().padStart(2, '0') }
        return TimeLimitPickerLabels(
            hour = hourLabel,
            minute = minuteLabel,
            am = am,
            pm = pm,
            hourUnit = hourUnit,
            minuteUnit = minuteUnit,
        )
    }
}

/**
 * The ColorOS picker with unit labels: `COUITimeLimitPicker` (`coui_time_limit_picker.xml`).
 *
 * Two wheels with the unit drawn *outside* them - `小时` after the hour wheel, `分钟` after the minute
 * wheel - plus an AM/PM wheel in front of the hour wheel in 12-hour mode. ColorOS Settings uses it for
 * every "定时" screen: `timepower_time_settings.xml` (定时开关机), the dark-mode schedule, the eye-care
 * schedule and the breathing-light schedule. `SettingsTimePicker` subclasses it and hides the units via
 * `setTextVisibility(false)`, which is what [showUnits] models.
 *
 * The default look is the labelled form, because `COUITimeLimitPicker` clears the wheels' own
 * `setUnitText("")` at construction and keeps the two `TextView`s visible.
 *
 * @param hour The selected hour, `0..23` in 24-hour mode and `1..12` otherwise.
 * @param minute The selected minute, `0..59`.
 * @param onTimeChange Called with the new hour and minute when a wheel settles.
 * @param modifier The modifier to be applied to the row.
 * @param is24Hour Whether to show `0..23` without an AM/PM wheel, or `1..12` with one.
 * @param showUnits Whether the `小时` / `分钟` labels are drawn (`setTextVisibility`).
 * @param labels The column and unit labels.
 * @param colors The colours of the wheels.
 * @param enabled Whether the wheels respond to input.
 */
@Composable
fun TimeLimitPicker(
    hour: Int,
    minute: Int,
    onTimeChange: (hour: Int, minute: Int) -> Unit,
    modifier: Modifier = Modifier,
    is24Hour: Boolean = true,
    showUnits: Boolean = true,
    labels: TimeLimitPickerLabels = TimeLimitPickerDefaults.labels(is24Hour),
    colors: NumberPickerColors = NumberPickerDefaults.colors(),
    enabled: Boolean = true,
) {
    val clampedHour = if (is24Hour) hour.coerceIn(0, 23) else hour.coerceIn(1, 12)
    val clampedMinute = minute.coerceIn(0, 59)
    val isPm = clampedHour >= 12
    val displayHour = if (clampedHour % 12 == 0) 12 else clampedHour % 12
    val rtl = LocalLayoutDirection.current == LayoutDirection.Rtl
    // `@color/coui_picker_text_color` lives in the `com.support.picker` library and is not part of the
    // APK's values, so the unit labels take the wheels' selected colour (`couiColorPrimaryNeutral`).
    val unitColor = colors.selectedTextColor(enabled)

    val hourColumn: @Composable () -> Unit = {
        NumberPicker(
            modifier = Modifier.width(TimeLimitPickerDefaults.HourWidth),
            value = if (is24Hour) clampedHour else displayHour,
            onValueChange = { onTimeChange(it, clampedMinute) },
            enabled = enabled,
            range = if (is24Hour) 0..23 else 1..12,
            label = labels.hour,
            wrapAround = true,
            colors = colors,
            verticalPadding = 0.dp,
        )
    }
    val minuteColumn: @Composable () -> Unit = {
        NumberPicker(
            modifier = Modifier.width(TimeLimitPickerDefaults.MinuteWidth),
            value = clampedMinute,
            onValueChange = { onTimeChange(clampedHour, it) },
            enabled = enabled,
            range = 0..59,
            label = labels.minute,
            wrapAround = true,
            colors = colors,
            verticalPadding = 0.dp,
        )
    }
    val amPmColumn: @Composable () -> Unit = {
        NumberPicker(
            modifier = Modifier.width(TimeLimitPickerDefaults.AmPmWidth),
            value = if (isPm) 1 else 0,
            onValueChange = { index -> onTimeChange((clampedHour % 12) + if (index == 1) 12 else 0, clampedMinute) },
            enabled = enabled,
            range = 0..1,
            label = { if (it == 0) labels.am else labels.pm },
            wrapAround = false,
            colors = colors,
            verticalPadding = 0.dp,
        )
    }

    NumberPickerGroup(
        modifier = modifier.padding(vertical = TimeLimitPickerDefaults.VerticalPadding),
        // `COUITimeLimitPicker`: one hairline pair across the row, inset 16dp.
        dividerInset = NumberPickerDefaults.DividerInset,
    ) {
        // `coui_time_limit_picker.xml` forces `layoutDirection="ltr"`; only the order of the whole row
        // is mirrored for RTL.
        if (rtl) {
            if (showUnits) UnitLabel(text = labels.minuteUnit, color = unitColor, paddingStart = 0.dp)
            minuteColumn()
            if (showUnits) UnitLabel(text = labels.hourUnit, color = unitColor, paddingStart = 0.dp)
            hourColumn()
            if (!is24Hour) amPmColumn()
        } else {
            if (!is24Hour) amPmColumn()
            hourColumn()
            if (showUnits) {
                UnitLabel(
                    text = labels.hourUnit,
                    color = unitColor,
                    paddingStart = 0.dp,
                    width = TimeLimitPickerDefaults.HourUnitWidth,
                    bold = true,
                    offsetStart = TimeLimitPickerDefaults.HourUnitOffsetStart,
                )
            }
            minuteColumn()
            if (showUnits) {
                UnitLabel(
                    text = labels.minuteUnit,
                    color = unitColor,
                    paddingStart = TimeLimitPickerDefaults.MinuteUnitPaddingStart,
                )
            }
        }
    }
}

@Composable
private fun UnitLabel(
    text: String,
    color: Color,
    paddingStart: Dp,
    width: Dp? = null,
    bold: Boolean = false,
    offsetStart: Dp = 0.dp,
) {
    Text(
        modifier =
            Modifier
                .offset(x = offsetStart)
                .then(if (width != null) Modifier.width(width) else Modifier)
                .padding(start = paddingStart),
        text = text,
        fontSize = TimeLimitPickerDefaults.UnitTextSize,
        fontWeight = if (bold) FontWeight.Bold else FontWeight.Normal,
        color = color,
        maxLines = 1,
    )
}
