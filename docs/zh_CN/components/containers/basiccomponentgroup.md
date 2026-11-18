# BasicComponentGroup

ColorOS 的卡片分组容器：`content` 里的行共享一张圆角卡片，分隔线**只画在行与行之间**——首行之上、末行之下都不会有线。

这是 `COUICardListHelper` 的模型：每个偏好行都有 `positionInGroup`（HEAD / MIDDLE / TAIL / FULL），
`COUIPreference.drawDivider()` 只对 HEAD 与 MIDDLE 返回 true。分隔线属于**上面那一行**，所以两组之间的接缝处没有线；
`PreferenceCategory` 不是卡片成员，因此分组标题会切断卡片组。

## 引入

```kotlin
import io.github.ra1nify.rmuix.kmp.basic.BasicComponentGroup
```

## 基本用法

```kotlin
BasicComponentGroup {
    BasicComponent(title = "WLAN", summary = "已连接", onClick = {})
    BasicComponent(title = "蓝牙", summary = "已开启", onClick = {})
}
BasicComponentGroup {
    BasicComponent(title = "显示", onClick = {})
}
```

## 参数

| 参数名        | 类型                        | 说明                     | 默认值 |
| :------------ | :-------------------------- | :----------------------- | :----- |
| modifier      | Modifier                    | 应用到整个分组           | Modifier |
| cornerRadius  | Dp                          | 卡片圆角                 | BasicComponentGroupDefaults.CornerRadius（20dp） |
| colors        | BasicComponentGroupColors   | 容器与分隔线颜色         | BasicComponentGroupDefaults.colors() |
| showDividers  | Boolean                     | 是否在行间画分隔线       | true |
| dividerStart | Dp | 无法上报标题 x 的行使用的回退起点 | BasicComponentGroupDefaults.DividerStart (32dp) |
| dividerEnd    | Dp                          | 分隔线的行尾内距         | BasicComponentGroupDefaults.DividerEnd（32dp） |
| horizontalMargin | Dp | 卡片相对组边缘的水平内缩 | BasicComponentGroupDefaults.HorizontalMargin (16dp) |
| title         | String?                     | 卡片上方的可选分组标题（`COUIPreferenceCategory`） | null |
| footer        | String?                     | 卡片下方的可选页脚（`SettingsCategoryBottomTipPreference`） | null |
| headerContent | (@Composable () -> Unit)?   | 标题与卡片之间的插槽 | null |
| footerContent | (@Composable () -> Unit)?   | 卡片与页脚之间的插槽 | null |
| content       | @Composable () -> Unit      | 分组内的行               | - |

## BasicComponentGroupDefaults

| 名称            | 类型  | 说明                                              | 值 |
| :-------------- | :---- | :------------------------------------------------ | :---- |
| CornerRadius    | Dp    | `?attr/couiRoundCornerXL` = `coui_round_corner_xl` | 20dp |
| CornerExtension | Float | `coui_round_corner_xl_weight`                      | 1.1 |
| GroupPadding    | Dp    | `coui_list_card_head_or_tail_padding`（上下各一份） | 2dp |
| HorizontalMargin| Dp | `coui_preference_card_margin_horizontal`（`_tiny` 12dp） | 16dp |
| DividerThickness| Dp    | `coui_list_divider_height`                         | 0.33dp |
| DividerStart    | Dp    | `support_preference_title_padding_start`              | 32dp |
| DividerEnd      | Dp    | `coui_preference_divider_default_horizontal_padding`                | 32dp |
| FooterTextSize  | TextUnit | `couiTextAppearanceArticleBody` | 13sp |
| FooterPadding   | PaddingValues | `support_preference_title_padding_start/end` = 32dp，`category_bottom_tip_preference_padding_top/bottom` = 8dp | 32dp / 8dp |
| footerColor()   | BasicComponentColors | `coui_preference_secondary_text_color` → `couiColorSecondNeutral`（`#8A000000` / `#8AFFFFFF`） | summary colours |

**卡片底色：** `COUICardListSelectedItemLayout.init()` 读取 `?attr/couiColorCardBackground` =
`@color/coui_color_card_background`（浅色 **#FFFFFF** / 深色 **#1AFFFFFF**），不是页面的
`couiColorBackgroundWithCard`；`BasicComponentGroupDefaults.colors()` 已改用这一对。卡片矩形本身左右各内缩
`coui_preference_card_margin_horizontal` = **16dp**。

**divider 起点：** 官方 decoration 会逐行测量标题视图（`COUIBaseListItemViewHolder.getDividerStartAlignView()`）：
无前置图标行 32dp，36dp 图标行 84dp（32 + 36 + 16）。现在每个 [BasicComponent] 会向所在组上报自己的标题 x，
组按"上一行"的起点绘制每条分隔线；`dividerStart` 只作为非 [BasicComponent] 行的回退值。