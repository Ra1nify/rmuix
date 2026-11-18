# BasicComponentGroup

A ColorOS card group: the rows passed as `content` share one rounded card, and a hairline is drawn
**between rows only** - never above the first row or below the last one. That is the
`COUICardListHelper` model: every preference row gets a `positionInGroup` of HEAD / MIDDLE / TAIL /
FULL, and `COUIPreference.drawDivider()` returns true only for HEAD and MIDDLE. Because the divider
belongs to the row *above* it, the seam between two groups has none; a `PreferenceCategory` is not a
card member, which is what makes a category header separate groups.

## Import

```kotlin
import io.github.ra1nify.rmuix.kmp.basic.BasicComponentGroup
```

## Basic Usage

```kotlin
BasicComponentGroup {
    BasicComponent(title = "Wi-Fi", summary = "Connected", onClick = {})
    BasicComponent(title = "Bluetooth", summary = "On", onClick = {})
}
BasicComponentGroup {
    BasicComponent(title = "Display", onClick = {})
}
```

## Properties

| Name          | Type                        | Description                                        | Default |
| :------------ | :-------------------------- | :------------------------------------------------- | :------ |
| modifier      | Modifier                    | Modifier applied to the group                      | Modifier |
| cornerRadius  | Dp                          | Card radius                                        | BasicComponentGroupDefaults.CornerRadius (20dp) |
| colors        | BasicComponentGroupColors   | Container and divider colours                      | BasicComponentGroupDefaults.colors() |
| showDividers  | Boolean                     | Draw the hairlines between rows                    | true |
| dividerStart  | Dp                          | Fallback hairline start for rows that cannot report their title x | BasicComponentGroupDefaults.DividerStart (32dp) |
| dividerEnd    | Dp                          | Inline-end inset of the hairline                   | BasicComponentGroupDefaults.DividerEnd (32dp) |
| horizontalMargin | Dp                       | Inline inset of the card from the group edges      | BasicComponentGroupDefaults.HorizontalMargin (16dp) |
| title         | String?                     | Optional section title above the card (`COUIPreferenceCategory`) | null |
| footer        | String?                     | Optional footer below the card (`SettingsCategoryBottomTipPreference`) | null |
| headerContent | (@Composable () -> Unit)?   | Slot between the title and the card | null |
| footerContent | (@Composable () -> Unit)?   | Slot between the card and the footer | null |
| content       | @Composable () -> Unit      | The rows                                           | - |

## BasicComponentGroupDefaults

| Name            | Type  | Description                                       | Value |
| :-------------- | :---- | :------------------------------------------------ | :---- |
| CornerRadius    | Dp    | `?attr/couiRoundCornerXL` = `coui_round_corner_xl` | 20dp |
| CornerExtension | Float | `coui_round_corner_xl_weight`                      | 1.1 |
| GroupPadding    | Dp    | `coui_list_card_head_or_tail_padding` (top + bottom) | 2dp |
| HorizontalMargin| Dp    | `coui_preference_card_margin_horizontal` (`_tiny` 12dp) | 16dp |
| DividerThickness| Dp    | `coui_list_divider_height`                         | 0.33dp |
| DividerStart    | Dp    | `support_preference_title_padding_start`              | 32dp |
| DividerEnd      | Dp    | `coui_preference_divider_default_horizontal_padding`                | 32dp |
| FooterTextSize  | TextUnit | `couiTextAppearanceArticleBody` | 13sp |
| FooterPadding   | PaddingValues | `support_preference_title_padding_start/end` = 32dp, `category_bottom_tip_preference_padding_top/bottom` = 8dp | 32dp / 8dp |
| footerColor()   | BasicComponentColors | `coui_preference_secondary_text_color` → `couiColorSecondNeutral` (`#8A000000` / `#8AFFFFFF`) | summary colours |

**Card surface:** `COUICardListSelectedItemLayout.init()` reads `?attr/couiColorCardBackground` =
`@color/coui_color_card_background` (**#FFFFFF** light / **#1AFFFFFF** dark), not the page
`couiColorBackgroundWithCard` fill; `BasicComponentGroupDefaults.colors()` now uses that pair. The card
rect itself is inset by `coui_preference_card_margin_horizontal` = **16dp** on both sides.

**Divider start:** the stock decoration measures every row's title view
(`COUIBaseListItemViewHolder.getDividerStartAlignView()`), so the hairline starts at 32dp for rows without
a leading icon and at 84dp for a 36dp icon row (32 + 36 + 16). Each [BasicComponent] now reports its title
x to the enclosing group, and the group aligns every seam to the row above it; `dividerStart` is only the
fallback for rows that are not [BasicComponent]s.
