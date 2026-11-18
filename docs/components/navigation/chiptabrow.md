# ChipTabRow

A row of ColorOS chip tabs (`COUIChipTabLayout` / `Widget.COUI.Chip.ChipTab`).

> All values below come from the Settings APK styles and dimens. Nothing here was built, run or checked on a device.

## Geometry

| Token | Value | Notes |
| :--- | ---: | :--- |
| `coui_chip_tab_style_height` | 32dp | `android:minHeight` of the chip |
| `coui_chip_default_min_width` | 52dp | |
| `coui_chip_default_max_width` | 300dp | |
| `coui_chip_suggest_style_chip_horizontal_padding` | 12dp | start and end |
| `coui_chip_tab_style_chip_icon_size` | 16dp | |
| `coui_chip_suggest_style_chip_icon_end_padding` | 4dp | between icon and label |
| `coui_chip_tab_count_start_margin` | 2dp | before a count badge |
| `chipCornerRadius` | -1dp | sentinel: `COUIChipTabDrawable` falls back to `min(width, height) / 2`, i.e. a pill |
| `?attr/couiTextButtonM` | `couiTextButtonM` style | **14sp**, `sans-serif-medium` (label) |
| `?attr/couiTextBodyS` | `couiTextBodyS` style | **13sp** (count badge) |

## Colors

| State | Container | Content |
| :--- | :--- | :--- |
| checked | `?attr/couiColorBackgroundElevated` (`#FFFFFF` / `#1E1E1E`) + `#40FFFFFF` overlay -> `#FFFFFF` / ~`#575757` | `?attr/couiColorLabelPrimary` = `#E6000000` / `#E6FFFFFF` |
| unchecked | `coui_chip_tab_surface_unchecked_surface` (`#FFFFFF` / `#000000`) + `?attr/couiColorContainer4` (`#0A000000` / `#1AFFFFFF`) -> `#F5F5F5` / `#1A1A1A` | `?attr/couiColorLabelPrimary` = `#E6000000` / `#E6FFFFFF` |

## Usage

```kotlin
ChipTabRow(
    items = listOf(ChipTabItem("All"), ChipTabItem("Recent", count = "3")),
    selectedIndex = selected,
    onSelect = { selected = it },
)
```

## Checked-state decoration

| token | value | notes |
| :--- | ---: | :--- |
| `coui_color_checked_background_gradient_start` | `#40FFFFFF` | named "gradient" but painted as a **flat fill**; only while the blur effect is off |
| `coui_color_checked_background_stroke` | `#0A000000` / `#0AFFFFFF` | stroke colour; skipped when the stroke effect is enabled |
| stroke width | `density * 0.6f` = 0.6dp | inset by half, corner radius shrinks by the same half |

> The blur branch is gated on `isBlurEffectEnabled()`; the stroke is drawn unless the stroke effect is enabled, and that switch defaults to off, so stock draws the 0.6dp stroke. `showCheckedStroke` therefore defaults to **true** (pass `false` to omit it).

## Cross-fade between states

`COUIChipTabDrawable.drawDualLayerBackground()` does not snap between the checked and unchecked
containers. Two `LayerAlphaAnimation` instances drive it - `CheckedLayerAlphaAnimation` and
`UncheckedLayerAlphaAnimation`, both `new COUISpringForce().setResponse(0.2f).setBounce(0f)`, i.e. a
critically damped spring with a 0.2s response (**not** a fixed-duration tween). The unchecked layer also
overlays `?attr/couiColorContainer4` while its alpha is above zero.

`ChipTabRow` mirrors this with `ChipTabDefaults.colorTransitionSpec()` (built from
`ChipTabDefaults.ColorTransitionResponse = 0.2f` through `folmeSpring`) passed to
`animateColorAsState`. `COUIChipTabDrawable` overrides `draw()` and does **not** call the inherited
`drawChipBackground()`, so the style's `checkedBackgroundColor = couiColorPrimary` is not painted: the
checked fill is `?attr/couiColorBackgroundElevated` plus the `#40FFFFFF` overlay, and the unchecked fill
is `coui_chip_tab_surface_unchecked_surface` plus `?attr/couiColorContainer4`.