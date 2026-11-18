// Copyright 2026, compose-rmuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package io.github.ra1nify.rmuix.kmp.basic

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp

/**
 * The per-column labels of a [TimePicker].
 *
 * @param hour Formats the hour wheel's value. In 24-hour mode that value is the hour itself; in
 *   12-hour mode it is `1..12` (`12` for both midnight and noon).
 * @param minute Formats the minute wheel's value, which is the real minute (`0..59`).
 * @param am The morning label (`R.string.morning`: `AM` / `上午`).
 * @param pm The afternoon label (`R.string.afternoon`: `PM` / `下午`).
 */
@Stable
data class TimePickerLabels(
    val hour: (Int) -> String,
    val minute: (Int) -> String,
    val am: String,
    val pm: String,
)

/**
 * Defaults for [TimePicker], from `coui_time_picker.xml` and `COUITimePicker`.
 */
object TimePickerDefaults {
    /** `coui_number_picker_width_small`: the hour column. */
    val HourWidth = 62.dp

    /** `coui_number_picker_width_small`: the minute column. */
    val MinuteWidth = 62.dp

    /** `coui_number_picker_width_small`: the AM/PM column. */
    val AmPmWidth = 62.dp

    /**
     * `coui_number_picker_view_padding_vertical`, the padding of the row in `coui_time_picker.xml`.
     *
     * As with [DatePicker], the columns are `coui_time_picker_height` (135dp = three 45dp rows) tall
     * and the row supplies the 12dp, so the inner pickers are drawn without
     * [NumberPickerDefaults.VerticalPadding].
     */
    val VerticalPadding = 12.dp

    /** `R.string.morning`. */
    val AmLabel = "\u4e0a\u5348"

    /** `R.string.afternoon`. */
    val PmLabel = "\u4e0b\u5348"

    /** `COUITimePicker.MINUTES_STEP`; the wheel step is configurable per instance. */
    val MinuteStep = 1

    /**
     * Builds the stock column labels.
     *
     * `COUITimePicker` calls `setTwoDigitFormatter()` on the hour wheel **only in 24-hour mode** and on
     * the minute wheel **unconditionally** (`COUINumberPicker.TwoDigitFormatter`), so minutes always render
     * as `00..59` while a 12-hour hour wheel renders `1..12` without a leading zero.
     *
     * @param is24Hour Whether the hour wheel shows `0..23`.
     * @param am The morning label.
     * @param pm The afternoon label.
     */
    fun labels(
        is24Hour: Boolean = true,
        am: String = AmLabel,
        pm: String = PmLabel,
    ): TimePickerLabels {
        val hourLabel: (Int) -> String =
            if (is24Hour) {
                { it.toString().padStart(2, '0') }
            } else {
                { it.toString() }
            }
        val minuteLabel: (Int) -> String = { it.toString().padStart(2, '0') }
        return TimePickerLabels(hour = hourLabel, minute = minuteLabel, am = am, pm = pm)
    }
}

/**
 * The ColorOS wheel time picker (`COUITimePicker`, `coui_time_picker.xml`).
 *
 * An hour wheel and a minute wheel; in 12-hour mode an AM/PM wheel is inserted between them and the
 * hour wheel runs `1..12`. Stock keeps the hour in `0..23` internally and only maps it for display, so
 * [hour] is always the 24-hour value regardless of [is24Hour].
 *
 * Two stock behaviours are reproduced literally:
 * - settling the 12-hour wheel on `12` flips the AM/PM wheel (`COUITimePicker` toggles `mAmPm` when the
 *   hour value becomes `12`), which is what makes a single step from 11 to 12 cross the meridian;
 * - the AM/PM wheel does not wrap (`setWrapSelectorWheel(false)`), while the hour wheel does.
 *
 * Not modelled: stock's optional leading date column (`coui_time_picker_date`, 150dp) that shows
 * `MMMdd日 E` names in pickers which also choose a day, and the `MINUTES_STEP` five-minute mode is
 * exposed as [minuteStep] instead of being toggled internally.
 *
 * @param hour The selected hour, `0..23`.
 * @param minute The selected minute, `0..59`.
 * @param onTimeChange Called with the new 24-hour hour and minute whenever a wheel settles.
 * @param modifier The modifier to be applied to the row.
 * @param is24Hour Whether to show `0..23` (no AM/PM wheel) or `1..12` plus an AM/PM wheel.
 * @param minuteStep How many minutes one wheel step covers (`COUITimePicker` supports `1` and `5`).
 * @param labels The column labels.
 * @param colors The colours of the wheels.
 * @param enabled Whether the wheels respond to input.
 */
@Composable
fun TimePicker(
    hour: Int,
    minute: Int,
    onTimeChange: (hour: Int, minute: Int) -> Unit,
    modifier: Modifier = Modifier,
    is24Hour: Boolean = true,
    minuteStep: Int = TimePickerDefaults.MinuteStep,
    labels: TimePickerLabels = TimePickerDefaults.labels(is24Hour),
    colors: NumberPickerColors = NumberPickerDefaults.colors(),
    enabled: Boolean = true,
) {
    val step = minuteStep.coerceAtLeast(1)
    val stepCount = (60 / step).coerceAtLeast(1)
    val clampedMinute = minute.coerceIn(0, 59)
    val minuteIndex = (clampedMinute / step).coerceIn(0, stepCount - 1)
    val isPm = hour >= 12
    val displayHour = if (hour % 12 == 0) 12 else hour % 12
    val rtl = LocalLayoutDirection.current == LayoutDirection.Rtl

    fun emitHour(displayValue: Int) {
        // `COUITimePicker`: `12` on the 12-hour wheel both moves into the other meridian and keeps the
        // clock at 12, i.e. `hour = (value % 12) + (pm ? 12 : 0)` with the meridian flipped when the
        // value is exactly 12.
        val pm = if (displayValue == 12) !isPm else isPm
        val base = displayValue % 12
        onTimeChange(base + if (pm) 12 else 0, clampedMinute)
    }

    fun emitAmPm(displayValue: Int) {
        val base = hour % 12
        onTimeChange(base + if (displayValue == 1) 12 else 0, clampedMinute)
    }

    val hourColumn: @Composable () -> Unit = {
        NumberPicker(
            modifier = Modifier.width(TimePickerDefaults.HourWidth),
            value = if (is24Hour) hour.coerceIn(0, 23) else displayHour,
            onValueChange = { emitHour(it) },
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
            modifier = Modifier.width(TimePickerDefaults.MinuteWidth),
            value = minuteIndex,
            onValueChange = { onTimeChange(hour.coerceIn(0, 23), it * step) },
            enabled = enabled,
            range = 0..(stepCount - 1),
            label = { labels.minute(it * step) },
            wrapAround = true,
            colors = colors,
            verticalPadding = 0.dp,
        )
    }
    val amPmColumn: @Composable () -> Unit = {
        NumberPicker(
            modifier = Modifier.width(TimePickerDefaults.AmPmWidth),
            value = if (isPm) 1 else 0,
            onValueChange = { emitAmPm(it) },
            enabled = enabled,
            range = 0..1,
            label = { if (it == 0) labels.am else labels.pm },
            wrapAround = false,
            colors = colors,
            verticalPadding = 0.dp,
        )
    }

    NumberPickerGroup(
        modifier = modifier.padding(vertical = TimePickerDefaults.VerticalPadding),
        // `COUITimePicker` applies `coui_selected_background_horizontal_padding` (16dp) to the shared
        // hairline pair, except in 12-hour mode where `mBackgroundLeft` is 0.
        dividerInset = if (is24Hour) NumberPickerDefaults.DividerInset else 0.dp,
    ) {
        // `coui_time_picker.xml` forces `layoutDirection="ltr"` on the row; only the two-wheel form is
        // mirrored for RTL so the hour stays on the reading side.
        if (rtl) {
            if (!is24Hour) amPmColumn()
            minuteColumn()
            hourColumn()
        } else {
            hourColumn()
            if (!is24Hour) amPmColumn()
            minuteColumn()
        }
    }
}
