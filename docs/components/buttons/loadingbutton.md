# LoadingButton

A [Button] that swaps its content for the ColorOS three-dot loading indicator.

`COUILoadingButton` has two rendering paths: when its loading *text* fits it draws the text plus a
literal `"..."` clipped into three bands, one alpha per band; otherwise it draws three circles. Only
the circle path is reproduced here — it does not depend on font metrics, so it behaves the same on
every platform. The dots take the button's content colour, exactly like the stock uses the button's
`TextPaint`.

The button stays clickable while loading, matching `COUIButton`: the stock's loading state only
changes what is drawn.

## Import

```kotlin
import io.github.ra1nify.rmuix.kmp.basic.LoadingButton
```

## Basic Usage

```kotlin
LoadingButton(
    onClick = { submit() },
    loading = submitting,
) {
    Text("Submit")
}
```

Pass `loadingContent` to show something else instead of the dots:

```kotlin
LoadingButton(onClick = {}, loading = true, loadingContent = { CircularProgressIndicator() }) {
    Text("Submit")
}
```

## Properties

| Name             | Type                     | Description                                    | Default |
| :--------------- | :----------------------- | :--------------------------------------------- | :------ |
| onClick          | () -> Unit               | Called when the button is clicked              | - |
| modifier         | Modifier                 | Modifier applied to the button                 | Modifier |
| loading          | Boolean                  | Draw the loading indicator instead of content  | false |
| enabled          | Boolean                  | Whether the button can be clicked              | true |
| enableSpotlight  | Boolean                  | Whether the spotlight effect is enabled        | false |
| spotlightColor   | Color                    | Spotlight colour                               | Color.White |
| cornerRadius     | Dp                       | Corner radius                                  | ButtonDefaults.CornerRadius |
| minWidth         | Dp                       | Minimum width                                  | ButtonDefaults.MinWidth |
| minHeight        | Dp                       | Minimum height                                 | ButtonDefaults.MinHeight |
| colors           | ButtonColors             | Button colours                                 | ButtonDefaults.buttonColors() |
| insideMargin     | PaddingValues            | Internal padding                               | ButtonDefaults.InsideMargin |
| pressFeedbackType| PressFeedbackType        | Press feedback type                            | PressFeedbackType.Sink |
| interactionSource| MutableInteractionSource? | Hoisted interaction source                    | null |
| indication       | Indication?              | Indication                                     | null |
| loadingContent   | (@Composable () -> Unit)? | Replaces the dots while loading                | null |
| content          | @Composable RowScope.() -> Unit | Button content                          | - |

## LoadingButtonDefaults

| Name            | Type | Description                              | Value |
| :-------------- | :--- | :--------------------------------------- | :---- |
| DotRadius       | Dp   | `coui_loading_btn_circle_radius`         | 1dp |
| DotSpacing      | Dp   | `coui_loading_btn_circle_spacing`        | 2dp |
| DOT_START_ALPHA | Float| `DOT_START_ALPHA` (20%)                  | 51 |
| DOT_MID_ALPHA   | Float| `DOT_MID_ALPHA` (50%)                    | 127.5 |
| DOT_END_ALPHA   | Float| `DOT_END_ALPHA` (100%)                   | 255 |
| DOT_STAGGER_MILLIS | Int | Stagger between two dots                | 333 |
| CYCLE_MILLIS    | Int  | Length of one pass                       | 1332 |

## Dot animation

Each dot runs four `ValueAnimator`s on a linear interpolator:

| Segment | Duration (ms) | Dot 1 start | Dot 2 start | Dot 3 start |
| :--- | --: | --: | --: | --: |
| START -> MID | 133 | 0 | 333 | 666 |
| MID -> END | 67 | 133 | 466 | 799 |
| END -> MID | 67 | 467 | 800 | 1133 |
| MID -> START | 133 | 533 | 866 | 1199 |

Between 200ms and 467ms the alpha holds at full brightness, which is what makes the dots read as a
travelling highlight. One dot spans 0–666ms; the three are staggered by 333ms so the whole set is
1332ms, then repeats.
