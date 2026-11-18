# Divider

`Divider` is a basic layout component in Rmuix used to separate content in lists and layouts. It provides both horizontal and vertical divider styles.

<div style="position: relative; height: 160px; border-radius: 10px; overflow: hidden; border: 1px solid #777;">
    <iframe id="demoIframe" style="position: absolute; top: 0; left: 0; width: 100%; height: 100%; border: none;" src="../../compose/index.html?id=divider" title="Demo" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" referrerpolicy="strict-origin-when-cross-origin"></iframe>
</div>

## Stock ColorOS static review (2026-11-12)

This pass reads the Settings APK's `content_divider_width` and the `couiColorDivider` colour roles.

- The settings hairline is `content_divider_width` = **0.33dp**; the component default was 0.75dp, i.e. noticeably thicker than every stock separator.
- The stock colour is `couiColorDivider` → `coui_color_divider` / `coui_color_divider_dark` = `#1F000000` / `#33FFFFFF`. The component used the theme's `dividerLine` role (`#0D000000` / `#1AFFFFFF`), which is roughly half as strong.
- Both defaults now resolve through `DividerStockValues`; every component that consumes `DividerDefaults.Thickness` / `DividerColor` inherits the stock values.

**BREAKING:** dividers are 0.33dp thick (was 0.75dp) and use `#1F000000` / `#33FFFFFF` (the theme's `dividerLine` role stays at `#0D000000` / `#1AFFFFFF` for other uses).

The app bar's own divider is modelled by `TopAppBar`: its horizontal inset interpolates from `coui_appbar_divider_expanded_margin_horizontal` (24dp) to `..._collapsed_...` (0dp) and its alpha goes 0 → 1, both over `fraction = accumulatedScrollDistance / expandedBarHeight` clamped at 1 (`COUIDividerAppBarLayout.onDividerChanged()`, `getDividerScrollRange()` = `getMeasuredHeight()`), so the hairline fades in as content scrolls, not with the collapse progress. **Still under review:** some screens use `coui_component_card_instruction_divider_width` (24dp) for short rules, which this component cannot express.
## Import

```kotlin
import io.github.ra1nify.rmuix.kmp.basic.HorizontalDivider // Horizontal divider
import io.github.ra1nify.rmuix.kmp.basic.VerticalDivider   // Vertical divider
```

## Basic Usage

### Horizontal Divider

Horizontal divider is used to separate vertically arranged content:

```kotlin
Column {
    Text("Content Above")
    HorizontalDivider()
    Text("Content Below")
}
```

### Vertical Divider

Vertical divider is used to separate horizontally arranged content:

```kotlin
Row {
    Text("Left Content")
    VerticalDivider()
    Text("Right Content")
}
```

## Properties

### HorizontalDivider Properties

| Property Name | Type     | Description                     | Default Value                | Required |
| ------------- | -------- | ------------------------------- | ---------------------------- | -------- |
| modifier      | Modifier | Modifier applied to the divider | Modifier                     | No       |
| thickness     | Dp       | Thickness of the divider        | DividerDefaults.Thickness    | No       |
| color         | Color    | Color of the divider            | DividerDefaults.DividerColor | No       |
| startIndent   | Dp       | Inset from the inline start (0 = full width)                     | DividerDefaults.StartIndent  | No       |
| endIndent     | Dp       | Inset from the inline end                                          | DividerDefaults.EndIndent    | No       |

### VerticalDivider Properties

| Property Name | Type     | Description                     | Default Value                | Required |
| ------------- | -------- | ------------------------------- | ---------------------------- | -------- |
| modifier      | Modifier | Modifier applied to the divider | Modifier                     | No       |
| thickness     | Dp       | Thickness of the divider        | DividerDefaults.Thickness    | No       |
| color         | Color    | Color of the divider            | DividerDefaults.DividerColor | No       |

### DividerDefaults Object

The DividerDefaults object provides default values for the divider components.

#### Constants

| Constant Name | Type  | Description                  | Default Value                      |
| ------------- | ----- | ---------------------------- | ---------------------------------- |
| Thickness     | Dp    | Default thickness of divider (`content_divider_width`) | 0.33.dp |
| DividerColor  | Color | Default color of divider (`couiColorDivider`) | `#1F000000` / `#33FFFFFF` |
| StartIndent   | Dp    | No indent; a lone divider is full width. ColorOS card rows pass `BasicComponentGroupDefaults.DividerStart` (32dp) instead | 0.dp |
| EndIndent     | Dp    | No indent, see `StartIndent` | 0.dp |

## Advanced Usage

### Custom Thickness Divider

```kotlin
Column {
    Text("Content Above")
    HorizontalDivider(
        thickness = 2.dp
    )
    Text("Content Below")
}
```

### Custom Color Divider

```kotlin
Column {
    Text("Content Above")
    HorizontalDivider(
        color = Color.Red
    )
    Text("Content Below")
}
```

### Divider with Padding

```kotlin
Column {
    Text("Content Above")
    HorizontalDivider(
        modifier = Modifier.padding(horizontal = 16.dp)
    )
    Text("Content Below")
}
```

### Using Vertical Divider Between Buttons

```kotlin
Row(
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically
) {
    Button(onClick = { /* Handle click event */ }) {
        Text("Cancel")
    }
    VerticalDivider(
        modifier = Modifier.height(24.dp)
    )
    Button(onClick = { /* Handle click event */ }) {
        Text("Confirm")
    }
}
```
