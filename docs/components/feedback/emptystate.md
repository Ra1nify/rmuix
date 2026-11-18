# EmptyState

The ColorOS empty-state page: `COUIEmptyStateView` (`coui_component_empty_state.xml`).

A centred illustration, a 280dp-wide title/subtitle block and an optional action. Stock wraps the text
block in a `COUIMaxHeightScrollView` capped at 92dp so a long message cannot push the action off screen;
the title is capped at two lines and the subtitle at three.

## Import

```kotlin
import io.github.ra1nify.rmuix.kmp.basic.EmptyState
```

## Basic Usage

```kotlin
EmptyState(
    title = "No photos yet",
    subtitle = "Photos you take will show up here.",
    actionText = "Take a photo",
    onActionClick = { takePhoto() },
    illustration = { Image(painter = painterResource(Res.drawable.empty_photos), contentDescription = null) },
)
```

## Properties

| Name         | Type                     | Description                                       | Default |
| :----------- | :----------------------- | :------------------------------------------------ | :------ |
| title        | String                   | Title (`?attr/couiTextHeadlineXS`, 16sp medium)     | - |
| modifier     | Modifier                 | Modifier applied to the page                        | Modifier |
| subtitle     | String?                  | Subtitle (`?attr/couiTextBodyXS`, 12sp)             | null |
| actionText   | String?                  | Action label (`?attr/couiTextButtonM`, 14sp medium) | null |
| onActionClick| (() -> Unit)?            | Called when the action is tapped                    | null |
| illustration | (@Composable () -> Unit)? | The illustration above the text                     | null |
| colors       | EmptyStateColors         | Colours of the page                                 | EmptyStateDefaults.emptyStateColors() |

## EmptyStateDefaults

| Name               | Type   | Source                                                     | Value |
| :----------------- | :----- | :--------------------------------------------------------- | :---- |
| GroupWidth         | Dp     | `coui_component_empty_group_width`                          | 280dp |
| TextGroupMaxHeight | Dp     | `coui_component_empty_text_scroll_max_height`               | 92dp |
| TitleFontSize      | TextUnit | `?attr/couiTextHeadlineXS`                                 | 16sp |
| TitleMaxLines      | Int    | `android:maxLines` of the title                             | 2 |
| SubtitleFontSize   | TextUnit | `?attr/couiTextBodyXS`                                     | 12sp |
| SubtitleTopMargin  | Dp     | `android:layout_marginTop` of the subtitle                  | 2dp |
| SubtitleMaxLines   | Int    | `android:maxLines` of the subtitle                          | 3 |
| ActionFontSize     | TextUnit | `?attr/couiTextButtonM`                                    | 14sp |
| ActionTopMargin    | Dp     | `android:layout_marginTop` of the action                    | 8dp |

## Colours

| Slot          | Source                          | Light       | Dark        |
| :------------ | :------------------------------ | :---------- | :---------- |
| titleColor    | `?attr/couiColorLabelPrimary`    | `#E6000000` | `#E6FFFFFF` |
| subtitleColor | `?attr/couiColorLabelSecondary`  | `#8A000000` | `#8AFFFFFF` |
| actionColor   | `?attr/couiColorPrimaryText`     | `#0080FF`   | `#5C9DFF`   |

## Known gaps

- The `EmptyStateAnimView` illustration is an `EffectiveAnimationView` whose animation asset ships in
  the `coui-support-emptyview` library and is not part of the APK resources, so the illustration is a
  slot instead of a bundled animation. Stock hides it when `setAnimSize` receives a zero dimension.
- Stock's `COUIMaxHeightScrollView` lets a long message scroll inside the 92dp cap; this component only
  caps the height.
