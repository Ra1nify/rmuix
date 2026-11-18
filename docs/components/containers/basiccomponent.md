# BasicComponent

## Shared Stock Spotlight

Spotlight calls now reuse the [shared Spotlight](../media/spotlight): the official lighting shader, presets and springs replace the old power glow/radial fallback, and blending occurs after content. This shared-engine replacement does not certify each component's system material gate or preset selection. Button/Checkbox spotlight options still default off.

BREAKING: default spotlight tint is white; arbitrary internal 40/140/380dp radii are removed. Explicit tint and radius overrides remain available through the public modifier.


`BasicComponent` is a foundational standard component in Rmuix. It provides customizable content areas on the start and end sides, along with a title and summary, making it suitable for building list items, settings items, and other UI elements.

This project builds upon it to provide some extended components, enabling developers to quickly create UI components that conform to design specifications. See the usage of [Extended Components](../#extended-components) for details.

<div style="position: relative; height: 350px; border-radius: 10px; overflow: hidden; border: 1px solid #777;">
    <iframe id="demoIframe" style="position: absolute; top: 0; left: 0; width: 100%; height: 100%; border: none;" src="../../compose/index.html?id=basicComponent" title="Demo" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" referrerpolicy="strict-origin-when-cross-origin"></iframe>
</div>

## Stock ColorOS static review (2026-11-12)

This pass re-read the Settings APK `coui_preference.xml` layout and its `res/color/coui_preference_*` selectors. The row geometry was already aligned; the text colour roles were not.

- Title follows `coui_preference_title_color` → `couiColorLabelPrimary` (`#E6000000` / `#E6FFFFFF`), not the theme's fully opaque `onBackground`.
- Summary follows `coui_preference_secondary_text_color` → `couiColorSecondNeutral` (`#8A000000` / `#8AFFFFFF`) instead of `onSurfaceVariantSummary`, whose default is 60% / 50%.
- Every disabled text state, including the assignment line, uses `couiColorLabelTertiary` (`#42000000` / `#4DFFFFFF`) instead of the opaque `disabledOnSecondaryVariant` grey.
- The assignment line is **not** the title colour: `coui_preference_assignment_text_color` resolves to `?attr/couiColorPrimaryText`, which `Theme.COUI.Main` (parent `Theme.COUI.Blue`) maps to `coui_color_primary_text_blue` = **`#0080FF`** light / **`#5C9DFF`** dark. The library now pins that pair instead of following `RmuixTheme.colorScheme.primary` (`#3482FF` family).
- The hint badges follow `COUIHintRedDotHelper`: a 6dp point dot, and a 16dp-tall count badge that is 16 / 20 / 26dp wide for 1 / 2 / 3 digits, `?attr/couiRoundCornerFULL` (pill), 10sp `sans-serif-medium` white on `coui_color_additional_red` (`#EB3B2F` / `#EB493D`); 1000 or more renders as three 2dp dots. The icon badge is inset 2dp from the icon's top/end (`support_preference_icon_red_dot_margin_top`), and the end badge has no gap to the widget (`coui_preference.xml`).
- The row now paints `coui_preference_bg_selector`: `couiColorPressBackground` (`#14000000` / `#26FFFFFF`) with the source's 100 ms enter and 360 ms exit fade, and the `clickable` ripple is turned off. The selector's disabled branch is `#00000000`, so disabled rows paint nothing.
- The shared spotlight used to run on every clickable row; it is now an opt-in `enableSpotlight = false`, because the stock selector has no light effect.

**BREAKING:** default title, summary, assignment and disabled text colours change, and clickable rows lose their ripple and default spotlight in favour of the stock press colour; parameter names and order stay the same (`enableSpotlight` is appended).

**Icon alignment (2026-11-17):** `COUICustomListSelectedLinearLayout.operateMultilineIconPosition()`
keeps the leading icon frame centred while title + summary + assignment count at most two lines (the
symmetric tier margins only grow the row), and switches it to top gravity above two lines with
`topMargin = max(centre of the first two text lines - iconHeight / 2, coui_preference_50icon_margin_vertical_default = 8dp)`
while the style's 14dp bottom margin stays. The row is
`max(support_preference_min_height = 48dp, textBlockHeight + 2 * support_preference_text_content_padding_top (10dp), iconHeight + 2 * tierMargin)`
(top case: `topMargin + iconHeight + 14dp`), so a tall icon grows the row exactly like the stock frame and
an icon is no longer pushed down by the row's own 10dp vertical padding. The title / summary / assignment
texts report their real `TextLayoutResult` line metrics, so both the `getLineCount() > 2` test and the
first-two-line centre (`lineTop(0)` / `lineBottom(1)`, plus the `support_preference_margin_between_line`
2dp) are exact rather than estimated; only a custom `content` (no wrapper texts) falls back to the 20dp
line-unit estimate.

**Right-side assignment (`coui_preference_assignment_in_right.xml`):** `assignmentInRight = true` moves the
assignment onto the title row, right-aligned before `endActions`, using
`TextAppearance.COUI.List.Assignment.End` (the summary appearance: 13sp, secondary colour, end ellipsis) and
a `support_preference_title_margin_end_in_right` 16dp gap; `support_preference_reddot_margin_end_in_right_*`
(4dp / 0dp) only matter for the red-dot variants, which the row already draws through `iconBadge` /
`endBadge`. The stock `COUICustomLinearLayoutForPreference` priority squeeze (`assignment_in_right_low_priority_min_width`
80dp) is approximated by the row's 6/10 end-region cap.

**Line spacing (2026-11-17):** the stock appearances set `android:lineSpacingMultiplier` -
`couiTextAppearanceHeadline6` uses `coui_spacing_multiplier_headline_xs` = **1.05** (title) and
`couiTextAppearanceArticleBody` uses `coui_spacing_multiplier_body_s` = **1.1** (summary / assignment).
Compose has no multiplier, so `rememberPreferenceLineHeight()` measures the style's natural line height on
the current platform (two identical lines, `getLineBottom(1) - getLineBottom(0)`) and passes
`natural * multiplier` as the `lineHeight` of the title / summary / assignment texts
([BasicComponentDefaults.TitleLineSpacingMultiplier] / [BasicComponentDefaults.SummaryLineSpacingMultiplier]).

The keyboard-focus title colour (`coui_preference_focus_title_text_color`) turned out to be a **dead token** on 2026-11-14:
it is declared in `public.xml`/`R.java` but no class outside the generated `R` reads it, so there is nothing to mirror.


### Row text sizes (follow-up)

`res/layout/coui_preference.xml` points the title at `COUIPreferenceTitleStyle` (`TextAppearance.COUI.List.Title`) and the summary at `COUIPreferenceSummaryStyle` (`TextAppearance.COUI.Preference.Summary`); `TextAppearance.COUI.List.Assignment` inherits the summary appearance. The layout declares `android:minHeight="@dimen/support_preference_min_height"` (48dp) and `paddingStart/End="@dimen/support_preference_title_padding_start/end"` (32dp), which the component already matched.

- Title: `TextAppearance.COUI.List.Title` -> `couiTextAppearanceHeadline6` = **16sp** `sans-serif-medium` (`lineSpacingMultiplier` 1.05); pinned as `BasicComponentDefaults.TitleTextSize` (rendered `FontWeight.Medium`).
- Summary **and assignment**: `TextAppearance.COUI.Preference.Summary` -> `couiTextAppearanceArticleBody` = **13sp** (`lineSpacingMultiplier` 1.1); `TextAppearance.COUI.List.Assignment` inherits it. `support_preference_summary_size` (12sp) is declared in the APK but read by no class - only the generated `R` - so it is not what the row renders.

**Correction (2026-11-17):** the earlier pass pinned the summary at **12sp** from that unused dimen. Both lines now render the stock **13sp**; the 13sp -> 12sp BREAKING note is reverted.

### Trailing status line (follow-up)

`res/layout/coui_preference_widget_select.xml` shows the stock select row: a right-aligned status line followed by the widget. It declares

- `android:maxWidth="@dimen/coui_preference_status_text_max_width"` = **162dp**,
- `android:paddingStart="@dimen/support_preference_widget_focus_jump_margin_start"` = **24dp**,
- `style="@style/PreferenceStatusText.Tiny"`, which sets the text size to **14dp** (the appearance's body style carries the secondary text colour).

`BasicComponent` gained an optional `statusText` (plus `statusTextColor`, defaulting to the summary colours). It renders inside the end row, before `endActions`, right aligned and capped at `BasicComponentDefaults.StatusTextMaxWidth`; `endActions == null` is no longer required for the end row to exist.

**Select icon:** the drawable `coui_btn_select` only exists as a generated `R.drawable` field - no class in this APK
reads it, and `support_preference_image_padding_start` is used by three *settings-app* views (`LanguageViewHolder`,
`ManageLanguageCurrentAdapter`, `RingtoneMultiCardPreference`) rather than by `COUIPreference`. A select icon after the
status line is therefore the caller's job through `endActions`; there is no stock geometry to copy.
## Import

```kotlin
import io.github.ra1nify.rmuix.kmp.basic.BasicComponent
```

## Basic Usage

The BasicComponent can be used to display title and summary information:

```kotlin
BasicComponent(
    title = "Setting Item Title",
    summary = "This is the description text for the setting item"
)
```

## Component Variants

### Clickable Component

```kotlin
BasicComponent(
    title = "Wi-Fi",
    summary = "Connected to MIUI-WiFi",
    onClick = { /* Handle click event */ }
)
```

### With Start Icon

```kotlin
BasicComponent(
    title = "Nickname",
    summary = "A brief introduction",
    startAction = {
        Icon(
            modifier = Modifier.padding(end = 16.dp),
            imageVector = RmuixIcons.Contacts,
            contentDescription = "Avatar Icon",
            tint = RmuixTheme.colorScheme.onBackground
        )
    },
    onClick = { /* Handle click event */ }
)
```

### With End Actions

```kotlin
var isFlightMode by remember { mutableStateOf(false) }

BasicComponent(
    title = "Flight Mode",
    endActions = {
        Switch(
            checked = isFlightMode,
            onCheckedChange = { isFlightMode = it }
        )
    }
)
```

### Custom Content Variant

In addition to the title and summary variant, BasicComponent also provides an overload that accepts custom content. This is useful when you want full control over the layout inside the component while still reusing the container and interactions:

```kotlin
BasicComponent(
    startAction = {
        Icon(
            modifier = Modifier.padding(end = 16.dp),
            imageVector = RmuixIcons.Contacts,
            contentDescription = "Avatar Icon",
            tint = RmuixTheme.colorScheme.onBackground
        )
    },
    endActions = {
        Text("Details")
    }
) {
    Text(
        text = "Custom Title",
        style = RmuixTheme.textStyles.headline1
    )
    Text(
        text = "Custom content description",
        style = RmuixTheme.textStyles.body2
    )
}
```

## Component States

### Disabled State

```kotlin
BasicComponent(
    title = "Mobile Network",
    summary = "SIM card not inserted",
    enabled = false
)
```

## Properties

### BasicComponent Properties (title and summary variant)

| Property Name     | Type                               | Description                                           | Default Value                         | Required |
| ----------------- | ---------------------------------- | ----------------------------------------------------- | ------------------------------------- | -------- |
| modifier          | Modifier                           | Modifier for the component                            | Modifier                              | No       |
| title             | String?                            | Title of the component                                | null                                  | No       |
| titleColor        | BasicComponentColors               | Title color configuration                             | BasicComponentDefaults.titleColor()   | No       |
| summary           | String?                            | Summary of the component                              | null                                  | No       |
| summaryColor      | BasicComponentColors               | Summary color configuration                           | BasicComponentDefaults.summaryColor() | No       |
| assignment        | String?                            | Optional third line, drawn under the summary          | null                                  | No       |
| assignmentColor   | BasicComponentColors               | Assignment line color configuration                   | BasicComponentDefaults.assignmentColor() | No    |
| assignmentInRight | Boolean                            | Draws the assignment right-aligned on the title row instead of a third line (stock `coui_preference_assignment_in_right.xml`) | false | No |
| startAction       | @Composable (() -> Unit)?          | Composable content on the start side of the component | null                                  | No       |
| endActions        | @Composable (RowScope.() -> Unit)? | Composable content on the end side of the component   | null                                  | No       |
| bottomAction      | @Composable (() -> Unit)?          | Composable content at the bottom of the component     | null                                  | No       |
| insideMargin      | PaddingValues                      | Internal padding of the component                     | BasicComponentDefaults.InsideMargin   | No       |
| onClick           | (() -> Unit)?                      | Callback triggered when the component is clicked      | null                                  | No       |
| onClickLabel      | String?                            | Accessibility label describing the click action       | null                                  | No       |
| role              | Role?                              | Semantic role for accessibility services              | null                                  | No       |
| holdDownState     | Boolean                            | Whether the component is in the pressed state         | false                                 | No       |
| enabled           | Boolean                            | Whether the component is enabled                      | true                                  | No       |
| iconBadge         | Boolean                            | Show a hint dot on the top-end corner of startAction  | false                                 | No       |
| iconBadgeCount    | Int                                | Number inside the icon badge; 0 renders a plain dot   | 0                                     | No       |
| endBadge          | Boolean                            | Show a hint dot before endActions                     | false                                 | No       |
| statusText        | String?                            | Trailing status line shown before `endActions` (`coui_preference_widget_select.xml`) | null | No |
| statusTextColor   | BasicComponentColors               | Status line colour configuration                      | BasicComponentDefaults.summaryColor() | No       |
| iconMarginTiered  | Boolean                            | Pick the icon's vertical margin from the ColorOS tiers | true                                 | No       |
| interactionSource | MutableInteractionSource?          | Interaction source of the component                   | null                                  | No       |
| enableSpotlight   | Boolean                            | Shared light on touch; off because stock rows only paint the press background | false                                 | No       |

### BasicComponent Properties (custom content variant)

| Property Name     | Type                               | Description                                           | Default Value                       | Required |
| ----------------- | ---------------------------------- | ----------------------------------------------------- | ----------------------------------- | -------- |
| modifier          | Modifier                           | Modifier for the component                            | Modifier                            | No       |
| startAction       | @Composable (() -> Unit)?          | Composable content on the start side of the component | null                                | No       |
| endActions        | @Composable (RowScope.() -> Unit)? | Composable content on the end side of the component   | null                                | No       |
| bottomAction      | @Composable (() -> Unit)?          | Composable content at the bottom of the component     | null                                | No       |
| insideMargin      | PaddingValues                      | Internal padding of the component                     | BasicComponentDefaults.InsideMargin | No       |
| onClick           | (() -> Unit)?                      | Callback triggered when the component is clicked      | null                                | No       |
| onClickLabel      | String?                            | Accessibility label describing the click action       | null                                | No       |
| role              | Role?                              | Semantic role for accessibility services              | null                                | No       |
| holdDownState     | Boolean                            | Whether the component is in the pressed state         | false                               | No       |
| enabled           | Boolean                            | Whether the component is enabled                      | true                                | No       |
| interactionSource | MutableInteractionSource?          | Interaction source of the component                   | null                                | No       |
| enableSpotlight   | Boolean                            | Shared light on touch; off by default                 | false                               | No       |
| content           | @Composable ColumnScope.() -> Unit | Composable content of the component                   | -                                   | Yes      |

### BasicComponentDefaults Object

The BasicComponentDefaults object provides default values and color configurations for the BasicComponent.

#### BasicComponentDefaults Constants

| Constant Name      | Type          | Description                                              | Default Value                        |
| ------------------ | ------------- | -------------------------------------------------------- | ------------------------------------ |
| MinHeight          | Dp            | Minimum row height (`support_preference_min_height`)     | 48.dp                                |
| InsideMargin       | PaddingValues | Internal padding of the component                        | PaddingValues(horizontal = 32.dp, vertical = 10.dp) |
| TitleTextSize      | TextUnit      | Title size (`support_preference_title_size`)             | 16.sp                                |
| SummaryTextSize    | TextUnit      | Summary / assignment size (`couiTextAppearanceArticleBody`) | 13.sp                |
| StatusTextSize     | TextUnit      | Trailing status line size (`PreferenceStatusText.Tiny`)  | 14.sp                                |
| StatusTextMaxWidth | Dp            | Maximum width of the status line (`coui_preference_status_text_max_width`) | 162.dp  |
| StatusTextStartMargin | Dp         | Start margin of the status line (`support_preference_widget_focus_jump_margin_start`) | 24.dp |
| IconTextGap        | Dp            | Gap between the leading action and the text column       | 16.dp                                |
| IconMarginDefault  | Dp            | Icon vertical margin when no tier matches                | 14.dp                                |
| IconMargin24       | Dp            | 24dp icon, single line                                   | 12.dp                                |
| IconMargin24Multiline | Dp         | 24dp icon, wrapped text                                  | 20.dp                                |
| IconMargin32       | Dp            | 32dp icon, single line                                   | 10.dp                                |
| IconMargin32Multiline | Dp         | 32dp icon, wrapped text                                  | 16.dp                                |
| IconMargin36       | Dp            | 36dp icon, single line                                   | 12.dp                                |
| IconMargin36Multiline | Dp         | 36dp icon, wrapped text                                  | 14.dp                                |
| IconMargin50       | Dp            | 50dp icon, both cases                                    | 8.dp                                 |
| BadgeSize          | Dp            | Plain hint dot diameter (`coui_dot_diameter`)            | 6.dp                                 |
| BadgeHeight        | Dp            | Count badge height (`coui_height`)                       | 16.dp                                |
| BadgeCornerRadius  | Dp            | Count badge radius (`couiRoundCornerFULL`, pill = height / 2) | 8.dp                            |
| BadgeSmallWidth    | Dp            | Count badge width, one digit (`coui_small_width`)        | 16.dp                                |
| BadgeMediumWidth   | Dp            | Count badge width, two digits / `1000+` ellipsis (`coui_medium_width`) | 20.dp              |
| BadgeLargeWidth    | Dp            | Count badge width, three digits (`coui_large_width`)     | 26.dp                                |
| BadgeTextSize      | TextUnit      | Count badge text size (`coui_hint_text_size`, medium)    | 10.sp                                |
| BadgeEllipsisDotSize | Dp          | `1000+` ellipsis dot diameter                            | 2.dp                                 |
| BadgeEllipsisSpacing | Dp          | Spacing between the three ellipsis dots                  | 2.dp                                 |
| BadgeTextPadding   | Dp            | Horizontal padding inside a count badge (fixed width per tier) | 0.dp                           |
| BadgeGap           | Dp            | Gap between the end badge and trailing content (stock jump dot has none) | 0.dp              |
| AssignmentInRightGap | Dp          | Gap before a right-aligned assignment (`support_preference_title_margin_end_in_right`) | 16.dp |
| ASSIGNMENT_IN_RIGHT_MAX_LINES | Int | Line cap of a right-aligned assignment (`coui_preference_assignment_in_right.xml`) | 2 |

#### BasicComponentDefaults Methods

| Method Name       | Type                 | Description                                        |
| ----------------- | -------------------- | -------------------------------------------------- |
| titleColor()      | BasicComponentColors | Creates title color configuration                  |
| summaryColor()    | BasicComponentColors | Creates summary color configuration                |
| assignmentColor() | BasicComponentColors | Creates assignment line color configuration        |
| iconVerticalMargin() | Dp                | Picks the icon vertical margin from the tier table |

#### Default text colors

| Factory | Enabled | Disabled |
| --- | --- | --- |
| `titleColor()` | `couiColorLabelPrimary` `#E6000000` / `#E6FFFFFF` | `couiColorLabelTertiary` `#42000000` / `#4DFFFFFF` |
| `summaryColor()` | `couiColorSecondNeutral` `#8A000000` / `#8AFFFFFF` | `couiColorLabelTertiary` |
| `assignmentColor()` | Theme accent (`couiColorPrimaryText`; APK default `#0080FF` / `#5C9DFF`) | `couiColorLabelTertiary` |

### BasicComponentColors Class

Used to configure the color states of the component.

| Property Name | Type  | Description             |
| ------------- | ----- | ----------------------- |
| color         | Color | Color in normal state   |
| disabledColor | Color | Color in disabled state |

## Advanced Usage

### Complex Layout Component

```kotlin
BasicComponent(
    title = "Volume",
    summary = "Media Volume: 70%",
    startAction = {
        Icon(
            modifier = Modifier.padding(end = 16.dp),
            imageVector = RmuixIcons.Play,
            contentDescription = "Volume Icon",
            tint = RmuixTheme.colorScheme.onBackground
        )
    },
    endActions = {
        IconButton(onClick = { /* Decrease volume */ }) {
            Icon(
                imageVector = RmuixIcons.Remove,
                contentDescription = "Decrease Volume",
                tint = RmuixTheme.colorScheme.onBackground
            )
        }
        Text("70%")
        IconButton(onClick = { /* Increase volume */ }) {
            Icon(
                imageVector = RmuixIcons.Add,
                contentDescription = "Increase Volume",
                tint = RmuixTheme.colorScheme.onBackground
            )
        }
    }
)
```

### Custom Style Component

```kotlin
BasicComponent(
    title = "Custom Component",
    summary = "Using custom colors",
    titleColor = BasicComponentColors(
        color = Color.Blue,
        disabledColor = Color.Gray
    ),
    summaryColor = BasicComponentColors(
        color = Color.DarkGray,
        disabledColor = Color.LightGray
    ),
    insideMargin = PaddingValues(horizontal = 24.dp, vertical = 12.dp),
    onClick = { /* Handle option click */ }
)
```

### Usage in a List

```kotlin
val options = listOf("Option 1", "Option 2", "Option 3", "Option 4")

Column {
    options.forEach { option ->
        BasicComponent(
            title = option,
            onClick = { /* Handle option click */ }
        )
    }
}
```
