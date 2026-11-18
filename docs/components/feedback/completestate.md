# CompleteState

The ColorOS completion page: `COUICompleteStateView` (`coui_component_complete_state.xml`).

The sibling of [EmptyState](./emptystate): same centred column, but a 100dp illustration, a 20sp title
(`?attr/couiTextHeadlineM`) and an 8dp gap before the subtitle.

## Import

```kotlin
import io.github.ra1nify.rmuix.kmp.basic.CompleteState
```

## Basic Usage

```kotlin
CompleteState(
    title = "All done",
    subtitle = "Everything has been backed up.",
    illustration = { Image(painter = painterResource(Res.drawable.done), contentDescription = null) },
)
```

## Properties

| Name         | Type                     | Description                                     | Default |
| :----------- | :----------------------- | :---------------------------------------------- | :------ |
| title        | String                   | Title (`?attr/couiTextHeadlineM`, 20sp medium)   | - |
| modifier     | Modifier                 | Modifier applied to the page                      | Modifier |
| subtitle     | String?                  | Subtitle (`?attr/couiTextBodyXS`, 12sp)           | null |
| illustration | (@Composable () -> Unit)? | The illustration; a 100dp slot when `null`        | null |
| colors       | EmptyStateColors         | Colours of the page                               | EmptyStateDefaults.emptyStateColors() |

## CompleteStateDefaults

| Name               | Type     | Source                                          | Value |
| :----------------- | :------- | :---------------------------------------------- | :---- |
| IllustrationSize   | Dp       | the 100dp `EffectiveAnimationView` of the layout | 100dp |
| HorizontalPadding  | Dp       | `complete_page_text_padding_hor`                 | 8dp |
| TitleFontSize      | TextUnit | `?attr/couiTextHeadlineM`                        | 20sp |
| TitleTopMargin     | Dp       | `complete_page_text_title_margin_top`            | 8dp |
| SubtitleFontSize   | TextUnit | `?attr/couiTextBodyXS`                           | 12sp |
| SubtitleTopMargin  | Dp       | `complete_page_text_subtitle_margin_top`         | 8dp |

The illustration is an `EffectiveAnimationView` in stock, whose asset ships in the library rather than
the APK, so the slot is left to the caller; when it is `null` the 100dp slot is still reserved, matching
the layout.
