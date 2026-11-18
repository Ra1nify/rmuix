# PageIndicator

ColorOS 的页码圆点（`COUIPageIndicator`）：每页一个圆点，间距取自 `coui_page_indicator_dot_spacing`（3dp）。

## 引入

```kotlin
import io.github.ra1nify.rmuix.kmp.basic.PageIndicator
```

## 基本用法

```kotlin
PageIndicator(count = 5, selectedIndex = 2)
```

## 参数

| 参数名        | 类型                 | 说明                 | 默认值 |
| :------------ | :------------------- | :------------------- | :----- |
| count         | Int                  | 页数                 | - |
| selectedIndex | Int                  | 当前页（超出会被夹取）| - |
| modifier      | Modifier             | 应用到指示器         | Modifier |
| dotSize       | Dp                   | 圆点直径             | PageIndicatorDefaults.DotSize（6dp） |
| spacing       | Dp                   | 圆点间距             | PageIndicatorDefaults.Spacing（3dp） |
| colors        | PageIndicatorColors  | 选中 / 未选中圆点颜色 | PageIndicatorDefaults.colors() |

## PageIndicatorDefaults

| 名称          | 类型 | 说明                                    | 值 |
| :------------ | :--- | :-------------------------------------- | :---- |
| DotSize       | Dp   | `coui_page_indicator_dot_size`          | 6dp |
| DotSizeMedium | Dp   | `coui_page_indicator_dot_size_medium`   | 4.5dp |
| DotSizeSmall  | Dp   | `coui_page_indicator_dot_size_small`    | 2.5dp |
| Spacing       | Dp   | `coui_page_indicator_dot_spacing`       | 3dp |
| StrokeWidth   | Dp   | `coui_page_indicator_dot_stroke_width`  | 1dp |

颜色：`Widget.COUI.COUIPageIndicator` 的 `traceDotColor` = `@color/coui_color_label_primary`
（选中，`#E6000000` / `#E6FFFFFF`），`dotColor` = `?attr/couiColorHintNeutral`（未选中
`coui_page_indicator_dot_color` 为 `#4D000000` / `#4DFFFFFF`（浅色 / 深色主题）。

## 未移植

页数超过 6 时官方会缩小圆点（`MAX_VISIBLE_DOT_NUMBER` 6、`DEFAULT_MAXIMUM_LARGE_DOT_NUMBER` 4、
`DEFAULT_MAXIMUM_MEDIUM_DOT_NUMBER` 2，级别尺寸 `9 / 3 / 5 / 5 / 7`）。目前只实现等大圆点。
