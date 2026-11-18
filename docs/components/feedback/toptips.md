# TopTips

The ColorOS top tips card: `COUIDefaultTopTipsView` (`coui_default_toptips.xml`), the floating hint bar
that drops in above the content.

An optional leading image (24dp, or 36dp when a title is present), an optional two-line title, the message, an optional inline action and an
optional close button. With a title the card is a 20dp rounded card with 16dp padding; without one it is a
single-line pill (`couiRoundCornerFULL` = 72dp) with 12dp padding - the two shapes stock animates between in
`updateRadius(hasTitle(), isSingleLineText())`.

## Import

```kotlin
import io.github.ra1nify.rmuix.kmp.basic.TopTips
```

## Basic Usage

```kotlin
TopTips(
    title = "Battery saver is on",
    content = "Background activity is restricted.",
    actionText = "Settings",
    onActionClick = { openBatterySettings() },
    onClose = { hideTips() },
    icon = { Icon(RmuixIcons.Basic.Battery, contentDescription = null) },
)

// single-line, no title
TopTips(content = "Copied to clipboard", onClose = { hideTips() })
```

## Properties

| Name                   | Type                     | Description                                              | Default |
| :--------------------- | :----------------------- | :------------------------------------------------------- | :------ |
| content                | String                   | The message (`?style/couiTextBodyS`, 13sp)                | - |
| modifier               | Modifier                 | Modifier applied to the card                              | Modifier |
| title                  | String?                  | Title (`?attr/couiTextHeadlineXS`, 16sp medium, 2 lines)   | null |
| icon                   | (@Composable () -> Unit)? | Leading image: 24dp (`icon_btn_size`) without a title, 36dp (`title_icon_size`) with one | null |
| actionText             | String?                  | Inline action (`?attr/couiColorPrimaryText`, 13sp)         | null |
| onActionClick          | (() -> Unit)?            | Called when the action is tapped                          | null |
| onClose                | (() -> Unit)?            | When non-null a close button is drawn                     | null |
| closeContentDescription| String                   | Close button content description                          | "Close" |
| colors                 | TopTipsColors            | Card, title, message and action colours                   | TopTipsDefaults.topTipsColors() |
| marquee                | Boolean                  | Single-line rolling message (`COUIMarqueeTopTips`), see Marquee | false |

## TopTipsDefaults

| Name               | Type     | Source                                                       | Value |
| :----------------- | :------- | :----------------------------------------------------------- | :---- |
| SingleLineRadius   | Dp       | `?attr/couiRoundCornerFULL` = `coui_round_corner_full`         | 72dp |
| MultiLineRadius    | Dp       | `coui_toptips_view_radius_os16`                               | 20dp |
| SingleLinePadding  | Dp       | `coui_toptips_view_padding`                                   | 12dp |
| MultiLinePadding   | Dp       | `coui_toptips_view_padding_multi_line`                        | 16dp |
| IconSize           | Dp       | `coui_toptips_view_icon_btn_size`                             | 24dp |
| IconStartMargin    | Dp       | `coui_toptips_view_icon_start_margin`                         | 16dp |
| ImageSize          | Dp       | `coui_toptips_view_icon_btn_size` (no title)                  | 24dp |
| TitleIconSize      | Dp       | `coui_toptips_view_title_icon_size` (with title)              | 36dp |
| TitleIconTextGap   | Dp       | `coui_toptips_view_title_icon_text_gap` (with title)          | 12dp |
| TitleFontSize      | TextUnit | `coui_toptips_view_default_title_size`                        | 16sp |
| TitleMinHeight     | Dp       | `coui_toptips_view_title_min_height`                          | 22dp |
| TitleMaxLines      | Int      | `android:maxLines` of the title                               | 2 |
| TitleContentGap    | Dp       | `coui_toptips_view_title_content_gap`                         | 0dp |
| ContentFontSize    | TextUnit | `?style/couiTextBodyS`                                        | 13sp |
| ContentMinHeight   | Dp       | `coui_toptips_view_content_min_height`                        | 20dp |
| ContentStartMargin | Dp       | `coui_toptips_view_content_start_margin`                      | 8dp |
| ContentTopMargin   | Dp       | `coui_toptips_view_content_top_margin`                        | 12dp |
| ActionFontSize     | TextUnit | `coui_toptips_view_default_text_size`                         | 13sp |
| ActionCloseGap     | Dp       | `coui_toptips_view_action_close_gap`                          | 12dp |
| ContentEndMargin   | Dp       | `coui_toptips_view_content_end_margin` / `coui_toptips_view_btn_margin` | 16dp |
| MarqueeVelocity | Dp | `coui_top_tips_scroll_speed` / refresh rate | 25dp/s |
| MarqueeSpacing | Dp | `coui_top_tips_scroll_text_interval` | 80dp |
| MarqueeFadeEdgeLength | Dp | `coui_top_tips_fading_edge_size` | 26dp |
| MarqueeInitialDelayMillis | Int | `DEFAULT_SCROLL_DELAY_DURATION` | 1000 |

## Colours

| Slot           | Source                          | Light       | Dark        |
| :------------- | :------------------------------ | :---------- | :---------- |
| containerColor | `?attr/couiColorFillThin`        | `#0A000000` | `#14FFFFFF` |
| titleColor     | `?attr/couiColorLabelPrimary`    | `#E6000000` | `#E6FFFFFF` |
| contentColor   | `?attr/couiColorLabelSecondary`  | `#8A000000` | `#8AFFFFFF` |
| actionColor    | `?attr/couiColorPrimaryText`     | `#0080FF` | `#5C9DFF` |

## Known gaps

## Marquee (`COUIMarqueeTopTips`)

`marquee = true` renders the message like `COUIMarqueeTextView` inside `COUIMarqueeTopTips`:

- the message is forced to a single line;
- after a **1000ms** delay (`DEFAULT_SCROLL_DELAY_DURATION`; the view posts `mStartScrollRunnable` in its
  constructor) it scrolls leftwards at **25dp/s** (`coui_top_tips_scroll_speed` / display refresh rate);
- repeated copies are separated by **80dp** (`coui_top_tips_scroll_text_interval`, `generateTextDistance()`);
- both edges fade over **26dp** (`coui_top_tips_fading_edge_size`;
  `getLeftFadingEdgeStrength()` / `getRightFadingEdgeStrength()` return `1.0` while rolling), and only while
  the text actually overflows;
- short messages that fit stay still.

| Constant | Token | Value |
| :--- | :--- | :--- |
| MarqueeVelocity | `coui_top_tips_scroll_speed` | 25dp/s |
| MarqueeSpacing | `coui_top_tips_scroll_text_interval` | 80dp |
| MarqueeFadeEdgeLength | `coui_top_tips_fading_edge_size` | 26dp |
| MarqueeInitialDelayMillis | `DEFAULT_SCROLL_DELAY_DURATION` | 1000ms |
| MarqueeStartLocation | `coui_top_tips_scroll_text_start_location` | 10dp |

Stock only rolls when someone calls `COUIMarqueeTopTips.startRoll()` -> `COUIDefaultTopTipsView.startRoll()`
-> `COUIMarqueeTextView.continueRoll()`; its constructor's `postDelayed(StartScrollRunnable, 1000L)` is
guarded by `isMarqueeEnable`, which is **false** for a plain `COUIDefaultTopTips`, so the opt-in
`marquee = true` here matches the stock default multi-line rendering. There is no `stopRoll()` API - set
`marquee = false` to stop. The initial `coui_top_tips_scroll_text_start_location` (10dp) inset is applied
(10dp start padding, mirrored in RTL layouts); the stock character-level RTL flip
(`checkAllCharactersDirections()` -> `canvas.translate(-x)`) is not reproduced - the layout direction
governs the scroll direction instead.

- The single-line to multi-line **radius animation** is not reproduced; the radius is chosen statically.
- `coui_ai_payment_toptips` (the AI-payment variant with dual buttons) is out of scope.
