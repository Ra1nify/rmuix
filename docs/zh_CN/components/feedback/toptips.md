# TopTips

ColorOS 的顶部提示条：`COUIDefaultTopTipsView`（`coui_default_toptips.xml`），从内容上方浮出的提示卡片。

可选前置图（无标题 24dp，有标题 36dp）、可选两行标题、正文、可选内联操作、可选关闭按钮。有标题时固定是 20dp 圆角 +
16dp 内距的卡片；没有标题时由**实测正文行数**决定——单行是胶囊（`couiRoundCornerFULL` = 72dp、12dp 内距），换行后是卡片。
圆角在两种形态间用 300ms COUIEase 动画过渡（`COUIDefaultTopTips.createCornerAnimator(300, COUIEaseInterpolator)`），
由 `updateRadius(hasTitle(), isSingleLineText())` 驱动。

## 引入

```kotlin
import io.github.ra1nify.rmuix.kmp.basic.TopTips
```

## 基本用法

```kotlin
TopTips(
    title = "省电模式已开启",
    content = "后台活动受到限制。",
    actionText = "设置",
    onActionClick = { openBatterySettings() },
    onClose = { hideTips() },
    icon = { Icon(RmuixIcons.Basic.Battery, contentDescription = null) },
)

// 单行、无标题
TopTips(content = "已复制到剪贴板", onClose = { hideTips() })
```

## 参数

| 参数名                 | 类型                     | 说明                                              | 默认值 |
| :--------------------- | :----------------------- | :------------------------------------------------ | :----- |
| content                | String                   | 正文（`?style/couiTextBodyS`，13sp）                | - |
| modifier               | Modifier                 | 应用到卡片                                        | Modifier |
| title                  | String?                  | 标题（`?attr/couiTextHeadlineXS`，16sp 中粗，最多 2 行） | null |
| icon                   | (@Composable () -> Unit)? | 前置图：无标题 24dp（`icon_btn_size`），有标题 36dp（`title_icon_size`） | null |
| actionText             | String?                  | 内联操作文字（`?attr/couiColorPrimaryText`，13sp）  | null |
| onActionClick          | (() -> Unit)?            | 点操作时回调                                      | null |
| onClose                | (() -> Unit)?            | 非空时显示关闭按钮                                | null |
| closeContentDescription| String                   | 关闭按钮的无障碍描述                              | "Close" |
| colors                 | TopTipsColors            | 卡片、标题、正文、操作颜色                        | TopTipsDefaults.topTipsColors() |
| marquee                | Boolean                  | 单行滚动正文（`COUIMarqueeTopTips`），见“跑马灯” | false |

## TopTipsDefaults

| 名称               | 类型     | 来源                                                          | 值 |
| :----------------- | :------- | :------------------------------------------------------------ | :---- |
| SingleLineRadius   | Dp       | `?attr/couiRoundCornerFULL` = `coui_round_corner_full`          | 72dp |
| MultiLineRadius    | Dp       | `coui_toptips_view_radius_os16`                                | 20dp |
| SingleLinePadding  | Dp       | `coui_toptips_view_padding`                                    | 12dp |
| MultiLinePadding   | Dp       | `coui_toptips_view_padding_multi_line`                         | 16dp |
| RadiusAnimationMillis | Int  | `createCornerAnimator(300, COUIEaseInterpolator)`             | 300 |
| IconSize           | Dp       | `coui_toptips_view_icon_btn_size`                              | 24dp |
| IconStartMargin    | Dp       | `coui_toptips_view_icon_start_margin`                          | 16dp |
| ImageSize          | Dp       | `coui_toptips_view_icon_btn_size`（无标题）                    | 24dp |
| TitleIconSize      | Dp       | `coui_toptips_view_title_icon_size`（有标题）                  | 36dp |
| TitleIconTextGap   | Dp       | `coui_toptips_view_title_icon_text_gap`（有标题）              | 12dp |
| TitleFontSize      | TextUnit | `coui_toptips_view_default_title_size`                         | 16sp |
| TitleMinHeight     | Dp       | `coui_toptips_view_title_min_height`                           | 22dp |
| TitleMaxLines      | Int      | 标题的 `android:maxLines`                                      | 2 |
| TitleContentGap    | Dp       | `coui_toptips_view_title_content_gap`                          | 0dp |
| ContentFontSize    | TextUnit | `?style/couiTextBodyS`                                         | 13sp |
| ContentMinHeight   | Dp       | `coui_toptips_view_content_min_height`                         | 20dp |
| ContentStartMargin | Dp       | `coui_toptips_view_content_start_margin`                       | 8dp |
| ContentTopMargin   | Dp       | `coui_toptips_view_content_top_margin`                         | 12dp |
| ActionFontSize     | TextUnit | `coui_toptips_view_default_text_size`                          | 13sp |
| ActionCloseGap     | Dp       | `coui_toptips_view_action_close_gap`                           | 12dp |
| ContentEndMargin   | Dp       | `coui_toptips_view_content_end_margin` / `coui_toptips_view_btn_margin` | 16dp |
| MarqueeVelocity | Dp | `coui_top_tips_scroll_speed` / 刷新率 | 25dp/s |
| MarqueeSpacing | Dp | `coui_top_tips_scroll_text_interval` | 80dp |
| MarqueeFadeEdgeLength | Dp | `coui_top_tips_fading_edge_size` | 26dp |
| MarqueeInitialDelayMillis | Int | `DEFAULT_SCROLL_DELAY_DURATION` | 1000 |

## 颜色

| 槽位           | 来源                            | 浅色        | 深色        |
| :------------- | :------------------------------ | :---------- | :---------- |
| containerColor | `?attr/couiColorFillThin`        | `#0A000000` | `#14FFFFFF` |
| titleColor     | `?attr/couiColorLabelPrimary`    | `#E6000000` | `#E6FFFFFF` |
| contentColor   | `?attr/couiColorLabelSecondary`  | `#8A000000` | `#8AFFFFFF` |
| actionColor    | `?attr/couiColorPrimaryText`     | `#0080FF` | `#5C9DFF` |

## 已知差异

## 跑马灯（`COUIMarqueeTopTips`）

`marquee = true` 时正文按 `COUIMarqueeTopTips` 里的 `COUIMarqueeTextView` 渲染：

- 正文强制单行；
- **1000ms** 后开始滚动（`DEFAULT_SCROLL_DELAY_DURATION`；该 View 在构造函数里 `postDelayed` 了
  `mStartScrollRunnable`），速度 **25dp/s**（`coui_top_tips_scroll_speed` / 屏幕刷新率）；
- 重复副本之间留 **80dp**（`coui_top_tips_scroll_text_interval`，`generateTextDistance()`）；
- 两侧 **26dp** 渐隐（`coui_top_tips_fading_edge_size`；滚动时 `getLeftFadingEdgeStrength()` /
  `getRightFadingEdgeStrength()` 返回 `1.0`），且只在文字真正溢出时出现；
- 不溢出的短文本不滚动。

| 常量 | token | 值 |
| :--- | :--- | :--- |
| MarqueeVelocity | `coui_top_tips_scroll_speed` | 25dp/s |
| MarqueeSpacing | `coui_top_tips_scroll_text_interval` | 80dp |
| MarqueeFadeEdgeLength | `coui_top_tips_fading_edge_size` | 26dp |
| MarqueeInitialDelayMillis | `DEFAULT_SCROLL_DELAY_DURATION` | 1000ms |
| MarqueeStartLocation | `coui_top_tips_scroll_text_start_location` | 10dp |

官方只有在调用 `COUIMarqueeTopTips.startRoll()` -> `COUIDefaultTopTipsView.startRoll()` ->
`COUIMarqueeTextView.continueRoll()` 时才滚动；构造函数里的 `postDelayed(StartScrollRunnable, 1000L)` 受
`isMarqueeEnable` 保护，而普通 `COUIDefaultTopTips` 该值为 **false**，所以本库用显式 `marquee = true`
与官方默认的多行/截断渲染一致。没有 `stopRoll()` API——把 `marquee` 置回 `false` 即停止。初始
`coui_top_tips_scroll_text_start_location`（10dp）内缩已实现（起始侧 10dp padding，RTL 布局镜像）；
官方按字符判断的 RTL 翻转（`checkAllCharactersDirections()` -> `canvas.translate(-x)`）未复刻，
滚动方向改为按布局方向处理。

- `coui_ai_payment_toptips`（AI 支付双按钮变体）不在范围内。
