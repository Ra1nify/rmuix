# NumberPicker

`NumberPicker` is a basic interactive component in Rmuix used for selecting a value from a range of numbers by vertical scrolling. The selected item is centered and highlighted, while surrounding items fade out and scale down. Supports infinite scrolling with the `wrapAround` parameter.

<div style="position: relative; height: 300px; border-radius: 10px; overflow: hidden; border: 1px solid #777;">
    <iframe id="demoIframe" style="position: absolute; top: 0; left: 0; width: 100%; height: 100%; border: none;" src="../../compose/index.html?id=numberPicker" title="Demo" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" referrerpolicy="strict-origin-when-cross-origin"></iframe>
</div>

## Stock ColorOS static review (2026-11-12)

This pass reads `com.coui.appcompat.picker.COUINumberPicker`, the `couiPickerBackgroundColor` attribute and the `coui_number_picker_*` / `coui_selected_*` dimens of the Settings APK.

- The stock picker frames the selected row with two hairlines (`onDraw` draws two `drawRect`s of `mBackgroundDividerHeight` above and below the selection area). The library drew no frame at all.
- Hairline height is `coui_number_picker_background_divider_height` = **0.33dp**, clamped to at least one pixel by the widget (`Math.max(dimen, 1)`).
- Hairline colour is `couiPickerBackgroundColor` → `?attr/couiColorDivider` (`#1F000000` / `#33FFFFFF`).
- The hairlines stop `coui_selected_background_horizontal_padding` = **16dp** from each side and sit
  `coui_selected_background_radius` = **24dp** above/below the centre (48dp apart, not at the 45dp row edges).
- `NumberPickerDefaults` gains `DividerHeight` and `DividerInset`; the lines are drawn behind the labels, so they stay put while the wheel scrolls.

### Row metrics and text (follow-up)

- `couiPickerRowNumber` = **3** rows; the component defaulted to 5. The default is now `NumberPickerStockRowCount` (3).
- `coui_datepicker_height` / `coui_timepicker_height` / `coui_time_picker_height` = **135dp** = 3 rows x **45dp**, and the parent layout adds `coui_number_picker_view_padding_vertical` (12dp) outside the picker
- `focusTextSize` = `coui_numberpicker_textSize_big` = **20sp** for the centered row and `startTextSize` = `coui_numberpicker_textSize_small` = **16sp** for every other row (`COUINumberPicker` stores them as `mFocusTextSize` / `mNormalTextSize`). The component used the theme's `title1` (32sp) for every row; stock paints every row at `mNormalTextSize`, then clips a band of `mBackgroundRadius` around the centre and repaints only that band at `mFocusTextSize`, so the component now uses 20sp only for the centered row and 16sp elsewhere (no interpolation).
- The style also declares `endTextSize` (14dp) but `COUINumberPicker` never reads that attribute, so it is not modelled.
- `couiFocusTextColor` → `couiColorPrimaryNeutral` (**#E6000000** / **#E6FFFFFF**) and `couiNormalTextColor` → `couiColorHintNeutral` (**#42000000** / **#4DFFFFFF**) replace the theme's `onSurface` / `onSurfaceSecondary` (80% neutral) for the selected and unselected rows.

**BREAKING:** the default picker is 3 rows of 45dp (135dp picker) with 20sp text and the #E6/#42 neutral pair, instead of 5 rows of 45dp with 32sp text and the theme neutrals.

Multi-column layouts are provided by `NumberPickerGroup` (see below); the caller sets each column's width from the stock tokens (`coui_number_picker_width_small` 62dp, `coui_numberpicker_width_middle` 66dp, `coui_numberpicker_width_big` 76dp) rather than the group applying them automatically. **Correction:** the unit-label tokens are applied - `unitLabel` draws at `coui_numberpicker_unit_textSize` 14sp with `coui_number_picker_unit_min_width` 21dp as the floor, `coui_number_picker_text_margin_start` 4dp after the value and `coui_numberpicker_unit_margin_bottom` 2dp (see `NumberPickerDefaults.UnitTextSize` / `UnitMinWidth`). **Correction:** `couiPickerTouchEffectInterval` (the style sets 50, the attribute default is 100) is declared but **never read** by `COUINumberPicker` - only its getter/setter touch `mTouchEffectInterval`, so there is no haptic cadence to align. The widget calls `performFeedback()` on every committed value change: `performHapticFeedback(308)` (vendor effect, with a 302 fallback), mapped here to one `HapticFeedbackType.TextHandleMove` tick per row change; those vendor effect ids cannot be reproduced through the Compose haptics API. The same path also plays `R.raw.coui_numberpicker_click` through `COUISoundLoadUtil`, and that vendor audio asset is not ported (asset licensing plus the lack of a cross-platform sound API) - a documented gap. `numberTextSize` and `endTextSize` are likewise declared but never read (`mFocusTextSize` is `focusTextSize`, `mNormalTextSize` is `startTextSize`).
**Text sizes are declared in `dp`, rendered as `sp`.** The dump declares these `*_text_size` dimens in `dp`
(COUI reads them with `getDimensionPixelSize` and applies them as `COMPLEX_UNIT_PX`, so the real glyph
height scales with density). This library maps them to density-independent `sp` at the same number, which
is the equivalent value at 1x.

## Import

```kotlin
import io.github.ra1nify.rmuix.kmp.basic.NumberPicker
```

## Basic Usage

```kotlin
var value by remember { mutableIntStateOf(5) }

NumberPicker(
    value = value,
    onValueChange = { value = it },
    range = 0..10
)
```

## Component States

### Disabled State

```kotlin
var value by remember { mutableIntStateOf(5) }

NumberPicker(
    value = value,
    onValueChange = { value = it },
    range = 0..10,
    enabled = false
)
```

### Infinite Scrolling

When `wrapAround` is enabled, the picker wraps from the last item back to the first, allowing continuous scrolling.

```kotlin
var value by remember { mutableIntStateOf(0) }

NumberPicker(
    value = value,
    onValueChange = { value = it },
    range = 0..23,
    wrapAround = true
)
```

## Properties

### NumberPicker Properties

| Property Name    | Type               | Description                                                              | Default Value              | Required |
| ---------------- | ------------------ | ------------------------------------------------------------------------ | -------------------------- | -------- |
| value            | Int                | Current selected value. If outside range, it will be coerced             | -                          | Yes      |
| onValueChange    | (Int) -> Unit      | Callback invoked when the selected value changes                         | -                          | Yes      |
| modifier         | Modifier           | Modifier applied to the picker                                           | Modifier                   | No       |
| enabled          | Boolean            | Whether the picker is enabled for user interaction                       | true                       | No       |
| range            | IntRange           | The range of selectable values                                           | 0..10                      | No       |
| label            | (Int) -> String    | Converts a value to its display string                                   | { it.toString() }          | No       |
| visibleItemCount | Int                | Number of visible items. Must be odd and at least 3 (`couiPickerRowNumber` = 3) | NumberPickerStockRowCount | No       |
| wrapAround       | Boolean            | Whether the picker wraps around (infinite scrolling)                     | false                      | No       |
| colors           | NumberPickerColors | Color configuration of the picker                                        | NumberPickerDefaults.colors() | No       |
| textStyle        | TextStyle          | Text style for picker items                                              | RmuixTheme.textStyles.title1 | No       |
| itemHeight       | Dp                 | The height of each item in the picker                                    | NumberPickerDefaults.ItemHeight | No       |
| unitLabel        | String             | Unit shown next to the selected value | "" | No |
| showSelectionLines | Boolean          | Whether this picker draws its own two selection hairlines. `NumberPickerGroup` sets it to `false` and draws one pair across the whole row | true | No |

### NumberPickerDefaults Object

The NumberPickerDefaults object provides default configurations for the NumberPicker component.

#### Properties

| Property Name | Type | Description                     | Default Value |
| ------------- | ---- | ------------------------------- | ------------- |
| ItemHeight    | Dp   | Default height of each item (135dp picker / 3 rows; the 12dp `coui_number_picker_view_padding_vertical` sits outside the picker) | 45.dp |
| VerticalPadding | Dp | Padding above and below the rows (`coui_number_picker_view_padding_vertical`) | 12.dp |
| unitLabel         | String                    | Unit label drawn to the right of the selected value: `coui_numberpicker_unit_textSize` 14sp, 4dp to its left, 2dp above the baseline | "" |
| FocusTextSize | TextUnit | Text size of the centered row (`focusTextSize` → `coui_numberpicker_textSize_big`) | 20.sp |
| NormalTextSize | TextUnit | Text size of every other row (`startTextSize` → `coui_numberpicker_textSize_small`) | 16.sp |
| DividerHeight | Dp   | Height of the two selection hairlines (`coui_number_picker_background_divider_height`) | 0.33.dp |
| DividerInset  | Dp   | Horizontal inset of those hairlines (`coui_selected_background_horizontal_padding`) | 16.dp |
| DividerOffset | Dp   | Distance of each hairline from the picker centre (`coui_selected_background_radius`) | 24.dp |

#### Methods

| Method Name | Return Type        | Description                         |
| ----------- | ------------------ | ----------------------------------- |
| colors()    | NumberPickerColors | Creates default color configuration |

### NumberPickerColors Class

| Property Name              | Type  | Description                                  |
| -------------------------- | ----- | -------------------------------------------- |
| selectedTextColor          | Color | Text color for the selected (center) item    |
| unselectedTextColor        | Color | Text color for unselected items              |
| disabledSelectedTextColor  | Color | Selected text color when disabled            |
| disabledUnselectedTextColor| Color | Unselected text color when disabled          |

## Advanced Usage

### Custom Label Format

```kotlin
var hour by remember { mutableIntStateOf(9) }

NumberPicker(
    value = hour,
    onValueChange = { hour = it },
    range = 0..23,
    label = { it.toString().padStart(2, '0') }  // "00", "01", ... "23"
)
```

### Time Picker

Combine two NumberPickers to create a time picker.

```kotlin
var hour by remember { mutableIntStateOf(16) }
var minute by remember { mutableIntStateOf(30) }

Row(
    horizontalArrangement = Arrangement.Center,
    verticalAlignment = Alignment.CenterVertically,
) {
    NumberPicker(
        value = hour,
        onValueChange = { hour = it },
        range = 0..23,
        label = { it.toString().padStart(2, '0') },
        wrapAround = true,
        modifier = Modifier.weight(1f),
    )
    Text(
        text = ":",
        fontWeight = FontWeight.Bold,
    )
    NumberPicker(
        value = minute,
        onValueChange = { minute = it },
        range = 0..59,
        label = { it.toString().padStart(2, '0') },
        wrapAround = true,
        modifier = Modifier.weight(1f),
    )
}
```

### Custom Visible Item Count

```kotlin
var value by remember { mutableIntStateOf(5) }

NumberPicker(
    value = value,
    onValueChange = { value = it },
    range = 1..100,
    visibleItemCount = 3
)
```

### Custom Colors

```kotlin
var value by remember { mutableIntStateOf(5) }

NumberPicker(
    value = value,
    onValueChange = { value = it },
    range = 0..10,
    colors = NumberPickerDefaults.colors(
        selectedTextColor = Color.Red,
        unselectedTextColor = Color.Gray
    )
)
```

## Multi-column picker

`NumberPickerGroup` lays out several pickers as one row, the way the stock date and time pickers do
(`coui_date_picker.xml`): the columns are centred, the row keeps **LTR** order even in RTL locales, and
the pickers stop drawing their own hairlines so the group draws one continuous pair across the whole
width (through the gaps between columns).

```kotlin
NumberPickerGroup {
    NumberPicker(value = year, onValueChange = { year = it }, range = 1970..2100, modifier = Modifier.width(96.dp))
    NumberPicker(value = month, onValueChange = { month = it }, range = 1..12, modifier = Modifier.width(96.dp))
    NumberPicker(value = day, onValueChange = { day = it }, range = 1..31, modifier = Modifier.width(62.dp))
}
```

Column widths come from the caller: `coui_datepicker_width_year` / `_month` are 96dp and
`coui_number_picker_width_small` (day, hour, minute) is 62dp.
