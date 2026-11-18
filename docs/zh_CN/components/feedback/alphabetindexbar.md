# AlphabetIndexBar

ColorOS 字母快速滚动条：官方 `COUITouchSearchView`（`com.coui.appcompat.touchsearchview`），设置折叠屏应用列表
（`folded_app_list_layout.xml`，`couiFirstIsCharacter="true"`）在使用。

## 引入

```kotlin
import io.github.ra1nify.rmuix.kmp.basic.AlphabetIndexBar
```

## 基本用法

```kotlin
AlphabetIndexBar(
    onLetterSelected = { letter -> listState.scrollToItem(indexOfLetter(letter)) },
    onLetterLongPressed = { letter -> /* 官方第二弹窗（名称列表） */ },
    modifier = Modifier.height(469.dp),
)
```

## 参数

| 参数名 | 类型 | 说明 | 默认值 |
| :--- | :--- | :--- | :--- |
| onLetterSelected | (String) -> Unit | 按下字母变化回调 | - |
| modifier | Modifier | 应用到字母条；官方高度 432dp（最小 296dp） | Modifier |
| letters | List<String> | 键列表（`special_touchsearch_keys`：A–Z 加 `#`） | A–Z + "#" |
| onLetterLongPressed | ((String) -> Unit)? | 长按字母回调 | null |
| popupEnabled | Boolean | 是否显示 48dp 字母气泡 | true |
| colors | AlphabetIndexBarColors | 字母条与气泡配色 | AlphabetIndexBarDefaults.colors() |
| hapticFeedback | Boolean | 按键变化时触发官方振动（`SegmentTick`） | true |

## AlphabetIndexBarDefaults

| 名称 | 类型 | 来源 | 值 |
| :--- | :--- | :--- | :--- |
| BackgroundWidth | Dp | `coui_touchsearch_background_width` | 39dp |
| KeyHeight / KeySpacing | Dp | `coui_touchsearch_each_item_height` / `_item_spacing` | 14dp / 2dp |
| KeyTextSize / DotTextSize | TextUnit | `coui_touchsearch_key_textsize` / `_key_dot_textsize` | 12sp / 20sp |
| DotText | String | `coui_touchsearch_dot` | `·` |
| PaddingVertical | Dp | `coui_touchsearch_padding_top` / `_padding_bottom` | 16dp |
| DefaultHeight / MinHeight | Dp | `coui_touchsearch_default_height` / `_min_height` | 432dp / 296dp |
| LimitAllLettersHeight | Dp | `coui_touchsearch_limit1_height`（26 × 16dp） | 416dp |
| PopupSize | Dp | `coui_touchsearch_popup_first_default_width` / `_height` | 48dp |
| PopupTextSize | TextUnit | `coui_touchsearch_popupwin_first_textsize`（`couiTextAppearanceHeadline4`） | 20sp |
| PopupOffset / PopupMargin | Dp | `coui_touchsearch_popupwin_default_offset` / `_margin` | 23dp / 4dp |
| PopupAppearMillis / PopupDisappearMillis | Int | `POPUP_WINDOW_APPEAR_DURATION` / `_DISAPPEAR_DURATION` | 350 / 300 |

## 颜色

| 槽位 | 来源 | 浅色 | 深色 |
| :--- | :--- | :--- | :--- |
| letterColor | `coui_touchsearchview_key_text_color_unpressed` | `#8C000000` | `#8CFFFFFF` |
| pressedLetterColor | `?attr/couiColorPrimaryTextOnPopup` | 主题 primary | 主题 primary |
| dotColor | `?attr/couiColorControls` | `#29000000` | `#40FFFFFF` |
| pressedDotColor | `?attr/couiColorPrimaryTextOnPopup` | 主题 primary | 主题 primary |
| popupBackgroundColor | `?attr/couiColorBackgroundTop` | `#FFFFFF` | `#333333` |
| popupTextColor | `?attr/couiColorPrimaryNeutral` | `#E6000000` | `#E6FFFFFF` |

## 已知差异

- 高度不足时的"字母组压缩成 `·` 点"已建模（`alphabetIndexBarLayout()`，移植自 `initHeightRangeSpec()` /
  `getShowLettersSize()` / `updateKeys()`）：当高度低于 level 0 的 `432px`（density 1 下 27 键 × 16dp）时，
  `getLimitDotLevel()` 选择 level 0-5，按 `showLetterSize` 行布局，字母组变成 `·` 点
  （`getLetterSizeFromDot()` / `mHiddenCharList`），触摸点会报告其隐藏字母（`getKeyIndicesWithDots()`）；
  低于最小 level（density 1、27 键时为 192px）则不绘制（`LIMIT_DOT_LEVEL6`）。官方那组无人引用的
  `coui_touchsearch_limit1_height` / `coui_touchsearch_limit2_height` /
  `coui_touchsearch_limit3_height` / `coui_touchsearch_limit4_height` /
  `coui_touchsearch_limit5_height` dimen 不参与计算（限制由按键数推导）。
- 长按后的第二弹窗（名称列表，`coui_touchsearch_second_name_background`）与 `onNameClick` 属于应用数据源，
  本组件以 `onLetterLongPressed` 暴露。
- 350ms/300ms 气泡缩放淡入动画、`coui_touchsearch_collect_normal` 星形图标键未复现；自适应振动不可移植，
  但 `performFeedback()` 的 308 路径已映射为 `HapticFeedbackType.SegmentTick`（按键变化触发，302 为官方回退）；
  `ExploreByTouchHelper` 虚拟视图仍未复现。
