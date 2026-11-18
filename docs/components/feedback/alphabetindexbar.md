# AlphabetIndexBar

The ColorOS alphabet fast-scroll bar: `COUITouchSearchView` (`com.coui.appcompat.touchsearchview`), used by the
Settings foldable app list (`folded_app_list_layout.xml`, `couiFirstIsCharacter="true"`).

## Import

```kotlin
import io.github.ra1nify.rmuix.kmp.basic.AlphabetIndexBar
```

## Basic Usage

```kotlin
AlphabetIndexBar(
    onLetterSelected = { letter -> listState.scrollToItem(indexOfLetter(letter)) },
    onLetterLongPressed = { letter -> /* the stock second popup (name list) */ },
    modifier = Modifier.height(469.dp),
)
```

## Properties

| Name | Type | Description | Default |
| :--- | :--- | :--- | :--- |
| onLetterSelected | (String) -> Unit | Called when the pressed key changes | - |
| modifier | Modifier | Applied to the strip; the stock height is 432dp (min 296dp) | Modifier |
| letters | List<String> | The keys (`special_touchsearch_keys`: A-Z plus `#`) | A-Z + "#" |
| onLetterLongPressed | ((String) -> Unit)? | Called when a key is held for the long-press timeout | null |
| popupEnabled | Boolean | Shows the 48dp pressed-letter bubble | true |
| colors | AlphabetIndexBarColors | Strip and popup colours | AlphabetIndexBarDefaults.colors() |
| hapticFeedback | Boolean | Fires the stock key-change vibration (`SegmentTick`) | true |

## AlphabetIndexBarDefaults

| Name | Type | Source | Value |
| :--- | :--- | :--- | :--- |
| BackgroundWidth | Dp | `coui_touchsearch_background_width` | 39dp |
| KeyHeight / KeySpacing | Dp | `coui_touchsearch_each_item_height` / `_item_spacing` | 14dp / 2dp |
| KeyTextSize / DotTextSize | TextUnit | `coui_touchsearch_key_textsize` / `_key_dot_textsize` | 12sp / 20sp |
| DotText | String | `coui_touchsearch_dot` | `·` |
| PaddingVertical | Dp | `coui_touchsearch_padding_top` / `_padding_bottom` | 16dp |
| DefaultHeight / MinHeight | Dp | `coui_touchsearch_default_height` / `_min_height` | 432dp / 296dp |
| LimitAllLettersHeight | Dp | `coui_touchsearch_limit1_height` (26 × 16dp) | 416dp |
| PopupSize | Dp | `coui_touchsearch_popup_first_default_width` / `_height` | 48dp |
| PopupTextSize | TextUnit | `coui_touchsearch_popupwin_first_textsize` (`couiTextAppearanceHeadline4`) | 20sp |
| PopupOffset / PopupMargin | Dp | `coui_touchsearch_popupwin_default_offset` / `_margin` | 23dp / 4dp |
| PopupAppearMillis / PopupDisappearMillis | Int | `POPUP_WINDOW_APPEAR_DURATION` / `_DISAPPEAR_DURATION` | 350 / 300 |

## Colours

| Slot | Source | Light | Dark |
| :--- | :--- | :--- | :--- |
| letterColor | `coui_touchsearchview_key_text_color_unpressed` | `#8C000000` | `#8CFFFFFF` |
| pressedLetterColor | `?attr/couiColorPrimaryTextOnPopup` | theme primary | theme primary |
| dotColor | `?attr/couiColorControls` | `#29000000` | `#40FFFFFF` |
| pressedDotColor | `?attr/couiColorPrimaryTextOnPopup` | theme primary | theme primary |
| popupBackgroundColor | `?attr/couiColorBackgroundTop` | `#FFFFFF` | `#333333` |
| popupTextColor | `?attr/couiColorPrimaryNeutral` | `#E6000000` | `#E6FFFFFF` |

## Known gaps

- The adaptive dot compression is modelled (`alphabetIndexBarLayout()`, ported from `initHeightRangeSpec()` /
  `getShowLettersSize()` / `updateKeys()`): when the strip is shorter than level 0's `432px` (27 keys at the
  16dp pitch at density 1) `getLimitDotLevel()` picks level 0-5, `showLetterSize` rows are laid out, letter
  groups become `·` dots (`getLetterSizeFromDot()` / `mHiddenCharList`) and touching a dot reports its hidden
  letter (`getKeyIndicesWithDots()`); below the smallest level (192px at density 1 for 27 keys) nothing is drawn
  (`LIMIT_DOT_LEVEL6`). The stock's dead `coui_touchsearch_limit1_height` /
  `coui_touchsearch_limit2_height` / `coui_touchsearch_limit3_height` / `coui_touchsearch_limit4_height` /
  `coui_touchsearch_limit5_height` dimens are not used (the code computes the limits from the key count).
- The second popup (the name list opened by a long press, `coui_touchsearch_second_name_background`) and its
  `onNameClick` callback belong to the app's data source and are not part of the component; `onLetterLongPressed`
  is exposed instead.
- The 350ms/300ms popup scale+fade animator, the `coui_touchsearch_collect_normal` star icon key, adaptive
  vibration is modelled: a key change fires `performFeedback()`'s `performHapticFeedback(308)` path as
  `HapticFeedbackType.SegmentTick` (the adaptive linear-motor feedback and the 302 fallback are unportable /
  redundant); the `ExploreByTouchHelper` virtual views are not reproduced.
