# BasicComponent

## 共享原厂聚光内核

聚光入口现统一复用[共享 Spotlight](../media/spotlight)：使用官方光照 shader、预设和弹簧，移除旧幂次光斑/径向回退，并在内容之后混合。此项是公共引擎替换，不表示本控件的系统材质门控和预设选择已全部验收。Button/Checkbox 的可选聚光仍默认关闭。

BREAKING：聚光默认 tint 改为白色，移除组件内部随意指定的 40/140/380dp 半径；自定义颜色与半径仍可通过公共入口显式覆盖。


`BasicComponent` 是 Rmuix 中的基础标准组件。提供了标题、摘要以及左右两侧的可自定义内容区域，常用于构建列表项、设置项等界面元素。

本项目以此为基础提供了一些扩展组件，方便开发者快速构建符合设计规范的 UI 组件，详见[扩展组件](../#扩展组件)的使用。

<div style="position: relative; height: 350px; border-radius: 10px; overflow: hidden; border: 1px solid #777;">
    <iframe id="demoIframe" style="position: absolute; top: 0; left: 0; width: 100%; height: 100%; border: none;" src="../../../compose/index.html?id=basicComponent" title="Demo" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" referrerpolicy="strict-origin-when-cross-origin"></iframe>
</div>

## Stock ColorOS 静态复核（2026-11-12）

本轮重读 Settings APK 的 `coui_preference.xml` 布局与 `res/color/coui_preference_*` selector。行几何此前已对齐，文字颜色角色没对齐。

- 标题按 `coui_preference_title_color` → `couiColorLabelPrimary`（`#E6000000` / `#E6FFFFFF`），不再用主题里完全不透明的 `onBackground`。
- 摘要按 `coui_preference_secondary_text_color` → `couiColorSecondNeutral`（`#8A000000` / `#8AFFFFFF`），不再用默认 60%/50% 的 `onSurfaceVariantSummary`。
- 所有禁用文字（含第三行）统一用 `couiColorLabelTertiary`（`#42000000` / `#4DFFFFFF`），替换不透明的 `disabledOnSecondaryVariant` 灰。
- 第三行**不是**标题色：`coui_preference_assignment_text_color` 解析到 `?attr/couiColorPrimaryText`，`Theme.COUI.Main`（父主题 `Theme.COUI.Blue`）映射到 `coui_color_primary_text_blue` = **#0080FF** 浅 / **#5C9DFF** 深；库内固定该对颜色，不再跟随 `RmuixTheme.colorScheme.primary`（后者是 #3482FF 系）。此前“跟随主题强调色”的结论不准确。
- 红点/徽标按 `COUIHintRedDotHelper`：6dp 圆点；数字徽标高 16dp、按 1/2/3 位数字宽 16/20/26dp、`?attr/couiRoundCornerFULL`（胶囊）、10sp `sans-serif-medium` 白字、底色 `coui_color_additional_red`（`#EB3B2F` / `#EB493D`）；≥1000 画三个 2dp 省略号圆点。图标红点距图标右上各 2dp（`support_preference_icon_red_dot_margin_top`），尾部红点与控件之间无间距（`coui_preference.xml`）。
- 行按压底色已按 `coui_preference_bg_selector` 接入：`couiColorPressBackground`（`#14000000` / `#26FFFFFF`），进入 100ms、退出 360ms，并关闭 `clickable` 的 ripple；selector 的禁用分支是 `#00000000`，因此禁用行不画底色。
- 共享聚光原先是所有可点击行无条件启用，现改为可选 `enableSpotlight = false`——官方 selector 没有任何光效。

**BREAKING：** 标题、摘要、第三行与禁用文字默认色改变；可点击行不再有 ripple 与默认聚光，改为官方按压底色；参数名与顺序不变（`enableSpotlight` 追加在尾部）。
**图标对齐（2026-11-17）：** `COUICustomListSelectedLinearLayout.operateMultilineIconPosition()` 在标题 + 摘要 + assignment 总行数 ≤2 时保持前置图标 frame 居中（成对的 tier margin 只把行撑高），超过 2 行则改成 top gravity：`topMargin = max(前两行文字中心 - iconHeight / 2, coui_preference_50icon_margin_vertical_default = 8dp)`，样式里的 14dp bottom margin 继续参与行高。行高为 `max(support_preference_min_height = 48dp, 文本块高 + 2 * support_preference_text_content_padding_top(10dp), iconHeight + 2 * tierMargin)`（top 情况为 `topMargin + iconHeight + 14dp`），因此高图标会像 stock 一样把行撑高，图标也不会再被行自身的 10dp 垂直 padding 往下推。标题/摘要/assignment 文字会上报真实的 `TextLayoutResult` 行度量，因此 `getLineCount() > 2` 判断与前两行中心（`lineTop(0)` / `lineBottom(1)`，加上 `support_preference_margin_between_line` 2dp）都是精确值；只有自定义 `content`（没有包装层文字）才退回 20dp 单行估算。

**右侧 assignment（`coui_preference_assignment_in_right.xml`）：**`assignmentInRight = true` 把 assignment
移到标题行内、`endActions` 之前右对齐，使用 `TextAppearance.COUI.List.Assignment.End`
（即 summary 字体：13sp、次级文字色、末尾省略），并留 `support_preference_title_margin_end_in_right`
16dp 间距；`support_preference_reddot_margin_end_in_right_*`（4dp / 0dp）只影响带红点的变体，本组件已由
`iconBadge` / `endBadge` 覆盖。官方 `COUICustomLinearLayoutForPreference` 的优先级挤压
（`assignment_in_right_low_priority_min_width` 80dp）用本行 6/10 的末端宽度上限近似。

**行距（2026-11-17）：** 官方样式通过 `android:lineSpacingMultiplier` 设置行距——
`couiTextAppearanceHeadline6` 用 `coui_spacing_multiplier_headline_xs` = **1.05**（标题），
`couiTextAppearanceArticleBody` 用 `coui_spacing_multiplier_body_s` = **1.1**（摘要 / assignment）。
Compose 没有倍数概念，因此 `rememberPreferenceLineHeight()` 在当前平台先测出该样式的自然行高
（两行相同文字，`getLineBottom(1) - getLineBottom(0)`），再把 `自然行高 * 倍数` 作为标题 / 摘要 / assignment
文字的 `lineHeight`（见 `BasicComponentDefaults.TitleLineSpacingMultiplier` /
`SummaryLineSpacingMultiplier`）。

键盘焦点下的标题颜色（`coui_preference_focus_title_text_color`）经核对是**死 token**（2026-11-14）：它只在 `public.xml` / `R.java` 里声明，除自动生成的 `R` 之外没有任何类读取，因此没有可复刻的对象。

### 行文字字号（续核）

`res/layout/coui_preference.xml` 把标题指向 `COUIPreferenceTitleStyle`（`TextAppearance.COUI.List.Title`）、摘要指向 `COUIPreferenceSummaryStyle`（`TextAppearance.COUI.Preference.Summary`），而 `TextAppearance.COUI.List.Assignment` 继承自摘要外观。布局里声明了 `android:minHeight="@dimen/support_preference_min_height"`（48dp）与 `paddingStart/End="@dimen/support_preference_title_padding_start/end"`（32dp），这两项组件本来就一致。

- 标题：`TextAppearance.COUI.List.Title` → `couiTextAppearanceHeadline6` = **16sp** `sans-serif-medium`（`lineSpacingMultiplier` 1.05），固定为 `BasicComponentDefaults.TitleTextSize`（按 `FontWeight.Medium` 渲染）。
- 摘要**与**赋值行：`TextAppearance.COUI.Preference.Summary` → `couiTextAppearanceArticleBody` = **13sp**（`lineSpacingMultiplier` 1.1），`TextAppearance.COUI.List.Assignment` 继承同一外观。`support_preference_summary_size`（12sp）在 APK 里只有生成的 `R` 引用、没有任何类读取，因此不是行内实际字号。

**更正（2026-11-17）：** 此前的迁移依据那个未被使用的 dimen 把摘要固定成 **12sp**；现在两行都回到官方 **13sp**，此前的 13sp → 12sp BREAKING 结论撤回。

### 尾部状态文字（续核）

`res/layout/coui_preference_widget_select.xml` 展示了官方选择行：右对齐的状态文字 + 尾部控件。它声明了

- `android:maxWidth="@dimen/coui_preference_status_text_max_width"` = **162dp**；
- `android:paddingStart="@dimen/support_preference_widget_focus_jump_margin_start"` = **24dp**；
- `style="@style/PreferenceStatusText.Tiny"`，字号 **14dp**（外观的 body 样式自带次级文字颜色）。

`BasicComponent` 新增可选参数 `statusText`（以及 `statusTextColor`，默认取摘要颜色）。它渲染在尾部行内、`endActions` 之前，右对齐并受 `BasicComponentDefaults.StatusTextMaxWidth` 限制；尾部行不再要求 `endActions` 非空。

**勾选图标：** 状态行之后的勾选图标（`support_preference_image_padding_start` 16dp、drawable `coui_btn_select`）**没有可抄的官方几何**：`coui_btn_select` 只存在于自动生成的 `R.drawable` 字段，本 APK 没有任何类读取它；`support_preference_image_padding_start` 被三个**设置应用自己的**视图使用（`LanguageViewHolder`、`ManageLanguageCurrentAdapter`、`RingtoneMultiCardPreference`），与 `COUIPreference` 无关。需要勾选图标时由调用方通过 `endActions` 提供。
## 引入

```kotlin
import io.github.ra1nify.rmuix.kmp.basic.BasicComponent
```

## 基本用法

BasicComponent 组件可以用于展示标题和摘要信息：

```kotlin
BasicComponent(
    title = "设置项标题",
    summary = "这里是设置项的描述文本"
)
```

## 组件变体

### 可点击组件

```kotlin
BasicComponent(
    title = "Wi-Fi",
    summary = "已连接到 MIUI-WiFi",
    onClick = { /* 处理点击事件 */ }
)
```

### 带左侧图标的组件

```kotlin
BasicComponent(
    title = "昵称",
    summary = "一段简介",
    startAction = {
        Icon(
            modifier = Modifier.padding(end = 16.dp),
            imageVector = RmuixIcons.Contacts,
            contentDescription = "头像图标",
            tint = RmuixTheme.colorScheme.onBackground
        )
    },
    onClick = { /* 处理点击事件 */ }
)
```

### 带右侧操作的组件

```kotlin
var isFlightMode by remember { mutableStateOf(false) }

BasicComponent(
    title = "飞行模式",
    endActions = {
        Switch(
            checked = isFlightMode,
            onCheckedChange = { isFlightMode = it }
        )
    }
)
```

### 自定义内容变体

除了带标题和摘要的形式外，BasicComponent 还提供了一个接收自定义内容的重载。当你希望完全掌控组件内部布局，但仍复用容器与交互效果时，可以使用该形式：

```kotlin
BasicComponent(
    startAction = {
        Icon(
            modifier = Modifier.padding(end = 16.dp),
            imageVector = RmuixIcons.Contacts,
            contentDescription = "头像图标",
            tint = RmuixTheme.colorScheme.onBackground
        )
    },
    endActions = {
        Text("详情")
    }
) {
    Text(
        text = "自定义标题",
        style = RmuixTheme.textStyles.headline1
    )
    Text(
        text = "自定义内容说明",
        style = RmuixTheme.textStyles.body2
    )
}
```

## 组件状态

### 禁用状态

```kotlin
BasicComponent(
    title = "移动网络",
    summary = "SIM卡未插入",
    enabled = false
)
```

## 属性

### BasicComponent 属性（标题和摘要变体）

| 属性名            | 类型                               | 说明                 | 默认值                                | 是否必须 |
| ----------------- | ---------------------------------- | -------------------- | ------------------------------------- | -------- |
| modifier          | Modifier                           | 应用于组件的修饰符   | Modifier                              | 否       |
| title             | String?                            | 组件标题             | null                                  | 否       |
| titleColor        | BasicComponentColors               | 标题颜色配置         | BasicComponentDefaults.titleColor()   | 否       |
| summary           | String?                            | 组件摘要             | null                                  | 否       |
| summaryColor      | BasicComponentColors               | 摘要颜色配置         | BasicComponentDefaults.summaryColor() | 否       |
| assignment        | String?                            | 可选第三行文字，显示在 summary 下方                   | null                                  | 否       |
| assignmentColor   | BasicComponentColors               | 第三行文字的颜色配置                                  | BasicComponentDefaults.assignmentColor() | 否    |
| assignmentInRight | Boolean                            | 把 assignment 改为标题行内右对齐显示（对应官方 `coui_preference_assignment_in_right.xml`） | false | 否 |
| startAction       | @Composable (() -> Unit)?          | 组件左侧的可组合内容 | null                                  | 否       |
| endActions        | @Composable (RowScope.() -> Unit)? | 组件右侧的可组合内容 | null                                  | 否       |
| bottomAction      | @Composable (() -> Unit)?          | 组件底部的可组合内容 | null                                  | 否       |
| insideMargin      | PaddingValues                      | 组件内部边距         | BasicComponentDefaults.InsideMargin   | 否       |
| onClick           | (() -> Unit)?                      | 点击组件时触发的回调 | null                                  | 否       |
| onClickLabel      | String?                            | 无障碍点击操作描述   | null                                  | 否       |
| role              | Role?                              | 无障碍语义角色       | null                                  | 否       |
| holdDownState     | Boolean                            | 组件是否处于按下状态 | false                                 | 否       |
| enabled           | Boolean                            | 组件是否可用         | true                                  | 否       |
| iconBadge         | Boolean                            | 在 startAction 右上角显示提示红点                     | false                                 | 否       |
| iconBadgeCount    | Int                                | 红点内的数字；0 显示纯圆点                            | 0                                     | 否       |
| endBadge          | Boolean                            | 在 endActions 前显示提示红点                          | false                                 | 否       |
| statusText        | String?                            | 显示在 `endActions` 之前的尾部状态文字（`coui_preference_widget_select.xml`） | null | 否 |
| statusTextColor   | BasicComponentColors               | 状态文字的颜色配置                                    | BasicComponentDefaults.summaryColor() | 否       |
| iconMarginTiered  | Boolean                            | 按 ColorOS 分档选择图标的垂直边距                     | true                                  | 否       |
| interactionSource | MutableInteractionSource?          | 组件的交互源         | null                                  | 否       |
| enableSpotlight   | Boolean                            | 触摸时绘制共享光效；官方行只画按压底色，故默认关闭 | false                                 | 否       |

### BasicComponent 属性（自定义内容变体）

| 属性名            | 类型                               | 说明                 | 默认值                              | 是否必须 |
| ----------------- | ---------------------------------- | -------------------- | ----------------------------------- | -------- |
| modifier          | Modifier                           | 应用于组件的修饰符   | Modifier                            | 否       |
| startAction       | @Composable (() -> Unit)?          | 组件左侧的可组合内容 | null                                | 否       |
| endActions        | @Composable (RowScope.() -> Unit)? | 组件右侧的可组合内容 | null                                | 否       |
| bottomAction      | @Composable (() -> Unit)?          | 组件底部的可组合内容 | null                                | 否       |
| insideMargin      | PaddingValues                      | 组件内部边距         | BasicComponentDefaults.InsideMargin | 否       |
| onClick           | (() -> Unit)?                      | 点击组件时触发的回调 | null                                | 否       |
| onClickLabel      | String?                            | 无障碍点击操作描述   | null                                | 否       |
| role              | Role?                              | 无障碍语义角色       | null                                | 否       |
| holdDownState     | Boolean                            | 组件是否处于按下状态 | false                               | 否       |
| enabled           | Boolean                            | 组件是否可用         | true                                | 否       |
| interactionSource | MutableInteractionSource?          | 组件的交互源         | null                                | 否       |
| enableSpotlight   | Boolean                            | 触摸时绘制共享光效，默认关闭       | false                               | 否       |
| content           | @Composable ColumnScope.() -> Unit | 组件内部的可组合内容 | -                                   | 是       |

### BasicComponentDefaults 对象

BasicComponentDefaults 对象提供了 BasicComponent 组件的默认值和颜色配置。

#### BasicComponentDefaults 常量

| 常量名              | 类型          | 描述                                     | 默认值                                |
| ------------------ | ------------- | ---------------------------------------- | ------------------------------------- |
| MinHeight          | Dp            | 行最小高度                                | 48.dp                                 |
| TitleTextSize      | TextUnit      | 标题字号（`support_preference_title_size`） | 16.sp                          |
| SummaryTextSize    | TextUnit      | 摘要/赋值行字号（`couiTextAppearanceArticleBody`） | 13.sp                |
| StatusTextSize     | TextUnit      | 尾部状态文字字号（`PreferenceStatusText.Tiny`） | 14.sp                        |
| StatusTextMaxWidth | Dp            | 状态文字最大宽度（`coui_preference_status_text_max_width`） | 162.dp     |
| StatusTextStartMargin | Dp         | 状态文字起始留白（`support_preference_widget_focus_jump_margin_start`） | 24.dp |
| InsideMargin       | PaddingValues | 组件内部边距                              | PaddingValues(horizontal = 32.dp, vertical = 10.dp) |
| IconTextGap        | Dp            | 前置图标与文字列的间距                     | 16.dp                                 |
| IconMarginDefault  | Dp            | 未命中分档时的图标垂直边距                 | 14.dp                                 |
| IconMargin24       | Dp            | 24dp 图标，单行                          | 12.dp                                 |
| IconMargin24Multiline | Dp         | 24dp 图标，多行                          | 20.dp                                 |
| IconMargin32       | Dp            | 32dp 图标，单行                          | 10.dp                                 |
| IconMargin32Multiline | Dp         | 32dp 图标，多行                          | 16.dp                                 |
| IconMargin36       | Dp            | 36dp 图标，单行                          | 12.dp                                 |
| IconMargin36Multiline | Dp         | 36dp 图标，多行                          | 14.dp                                 |
| IconMargin50       | Dp            | 50dp 图标，单/多行                       | 8.dp                                  |
| BadgeSize          | Dp            | 圆点直径（`coui_dot_diameter`） | 6.dp |
| BadgeHeight        | Dp            | 数字徽标高度（`coui_height`） | 16.dp |
| BadgeCornerRadius  | Dp            | 数字徽标圆角（`couiRoundCornerFULL`，胶囊 = 高度/2） | 8.dp |
| BadgeSmallWidth    | Dp            | 一位数字宽度（`coui_small_width`） | 16.dp |
| BadgeMediumWidth   | Dp            | 两位数字 / `1000+` 省略号宽度（`coui_medium_width`） | 20.dp |
| BadgeLargeWidth    | Dp            | 三位数字宽度（`coui_large_width`） | 26.dp |
| BadgeTextSize      | TextUnit      | 徽标字号（`coui_hint_text_size`，medium） | 10.sp |
| BadgeEllipsisDotSize | Dp          | `1000+` 省略号圆点直径 | 2.dp |
| BadgeEllipsisSpacing | Dp          | 三个省略号圆点间距 | 2.dp |
| BadgeTextPadding   | Dp            | 数字徽标水平内距（按档位固定宽度） | 0.dp |
| BadgeGap           | Dp            | 尾部徽标与后面内容的间距（官方 jump dot 无间距） | 0.dp |

#### BasicComponentDefaults 方法

| 方法名         | 类型                 | 说明             |
| -------------- | -------------------- | ---------------- |
| titleColor()   | BasicComponentColors | 创建标题颜色配置 |
| summaryColor() | BasicComponentColors | 创建摘要颜色配置 |

#### 默认文字颜色

| 工厂 | 启用 | 禁用 |
| --- | --- | --- |
| `titleColor()` | `couiColorLabelPrimary` `#E6000000` / `#E6FFFFFF` | `couiColorLabelTertiary` `#42000000` / `#4DFFFFFF` |
| `summaryColor()` | `couiColorSecondNeutral` `#8A000000` / `#8AFFFFFF` | `couiColorLabelTertiary` |
| `assignmentColor()` | 主题强调色（`couiColorPrimaryText`；APK 默认 `#0080FF` / `#5C9DFF`） | `couiColorLabelTertiary` |

### BasicComponentColors 类

用于配置组件的颜色状态。

| 属性名        | 类型  | 说明           |
| ------------- | ----- | -------------- |
| color         | Color | 正常状态的颜色 |
| disabledColor | Color | 禁用状态的颜色 |

## 进阶用法

### 复杂布局组件

```kotlin
BasicComponent(
    title = "音量",
    summary = "媒体音量：70%",
        startAction = {
        Icon(
        modifier = Modifier.padding(end = 16.dp),
            imageVector = RmuixIcons.Play,
            contentDescription = "音量图标",
            tint = RmuixTheme.colorScheme.onBackground
        )
    },
    endActions = {
        IconButton(onClick = { /* 减小音量 */ }) {
            Icon(
                imageVector = RmuixIcons.Remove,
                contentDescription = "减小音量",
                tint = RmuixTheme.colorScheme.onBackground
            )
        }
        Text("70%")
        IconButton(onClick = { /* 增大音量 */ }) {
            Icon(
                imageVector = RmuixIcons.Add,
                contentDescription = "增大音量",
                tint = RmuixTheme.colorScheme.onBackground
            )
        }
    }
)
```

### 自定义样式组件

```kotlin
BasicComponent(
    title = "自定义组件",
    summary = "使用自定义颜色",
    titleColor = BasicComponentColors(
        color = Color.Blue,
        disabledColor = Color.Gray
    ),
    summaryColor = BasicComponentColors(
        color = Color.DarkGray,
        disabledColor = Color.LightGray
    ),
    insideMargin = PaddingValues(horizontal = 24.dp, vertical = 12.dp),
    onClick = { /* 处理选项点击 */ }
)
```

### 列表中使用

```kotlin
val options = listOf("选项1", "选项2", "选项3", "选项4")

Column {
    options.forEach { option ->
        BasicComponent(
            title = option,
            onClick = { /* 处理选项点击 */ }
        )
    }
}
```
