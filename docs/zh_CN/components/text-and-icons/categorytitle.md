# CategoryTitle

ColorOS 的分组标题：`COUIPreferenceCategory` 的标题视图。

只携带官方度量——12sp **Medium**（`couiTextAppearanceSmallButton` 的 `sans-serif-medium`）、最小高度 16dp、
行首 32dp、行尾 8dp、上方 20dp（12dp 分类项 margin + 8dp 标题 margin）、下方 8dp，颜色 `couiColorSecondNeutral`。
字距交给主题默认文本样式。

## 引入

```kotlin
import io.github.ra1nify.rmuix.kmp.basic.CategoryTitle
```

## 基本用法

```kotlin
CategoryTitle(text = "网络与互联网")
BasicComponentGroup {
    /* 行 */
}
```

## 参数

| 参数名       | 类型           | 说明             | 默认值 |
| :----------- | :------------- | :--------------- | :----- |
| text         | String         | 分组标题         | - |
| modifier     | Modifier       | 应用到标题       | Modifier |
| textColor    | Color          | 标题颜色         | CategoryTitleDefaults.textColor() |
| insideMargin | PaddingValues  | 标题文字四周内距 | CategoryTitleDefaults.InsideMargin |

## CategoryTitleDefaults

| 名称          | 类型          | 说明                                                        | 值 |
| :------------ | :------------ | :---------------------------------------------------------- | :---- |
| TextSize      | TextUnit      | `Widget.COUI.List.Category.Title` → `couiTextAppearanceSmallButton`（及 `setTextSize(2, 12.0f)`） | 12sp |
| MinHeight     | Dp            | `coui_preference_category_text_height`                       | 16dp |
| MarginStart   | Dp            | `support_preference_category_layout_title_margin_start_large`（类默认 margin type；Settings XML 都未覆盖） | 32dp |
| MarginTop     | Dp            | `coui_preference_category_margintop_small`（分类项上 margin；settings XML 里显式设置的都是 `small`） | 12dp |
| TextWeight    | FontWeight    | `couiTextAppearanceSmallButton` → `sans-serif-medium` | Medium |
| MarginEnd     | Dp            | `coui_category_title_pading_end_with_reddot_default`          | 8dp |
| VerticalMargin| Dp            | `coui_common_category_text_padding_top` / `_bottom`（标题视图上下 margin） | 8dp |
| InsideMargin  | PaddingValues | 上面的边距转成内距（标题视图自身 padding 为 0dp） | 行首 32dp / 行尾 8dp / 上 20dp / 下 8dp |

颜色为 `?attr/couiColorSecondNeutral` = `coui_color_secondary_neutral`：亮色 `#8A000000`、暗色 `#8AFFFFFF`。
`COUIPreferenceCategory.initStartRect()` 只在 **`title_type` 为 medium/large** 或 **small 标题带
`icon_with_title` 图标** 时扩到 `?attr/couiColorPrimaryNeutral`（`#E6000000` / `#E6FFFFFF`）。**更正：** 这不是
"title-only 风格"——Settings 没有任何 XML 设置 `title_type` / `icon_with_title`，也没有反编译类调用
`setTitleType()` / `setIconWithTitle()`，因此所有 Settings 分类都用 `SecondNeutral`；启用这些风格的调用方
通过 `textColor` 显式传入另一颜色。
`COUIPreferenceCategory` 只在「仅标题」样式下放宽到 `?attr/couiColorPrimaryNeutral`（`#E6000000` / `#E6FFFFFF`），
该样式未建模。

## 取代 SmallTitle

`SmallTitle` 是 miuix 遗留、ColorOS 无对应（行首 28dp、上下 8dp、`subtitle` 文本样式）。已在 2026-11-14 删除，
所有调用点改用本组件。
