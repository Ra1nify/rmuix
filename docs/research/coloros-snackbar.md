# ColorOS Snackbar 逆向调研

对应 rmuix `rmuix-ui/.../basic/Snackbar.kt`（533 行）。

ColorOS 实现：`com.coui.appcompat.snackbar.COUISnackBar` +
`res/layout/coui_snack_bar_item.xml` + `coui_snack_bar_*` 尺寸。

## 1. 布局结构

`coui_snack_bar_item.xml`（**RelativeLayout**）：

```xml
<RelativeLayout id="snack_bar"
    background="?couiColorSurfaceTop"
    layout_gravity="center|bottom"
    clipChildren="false" clipToPadding="false">

    <!-- 左侧图标 -->
    <ImageView id="iv_snack_bar_icon"
        width="coui_snack_bar_icon_width"      <!-- 30dp -->
        height="coui_snack_bar_icon_height"    <!-- 30dp -->
        marginTop/Bottom="coui_snack_bar_icon_margin_top_horizontal"  <!-- 16dp -->
        marginStart="coui_snack_bar_context_margin_start_with_icon"   <!-- 16dp -->
        alignParentStart="true"
        alignParentTop="true" />

    <!-- 正文 -->
    <TextView id="tv_snack_bar_content"
        textAppearance="couiTextAppearanceButton"    <!-- 14sp medium -->
        textColor="?couiColorPrimaryNeutral"
        marginTop/Bottom="coui_snack_bar_child_margin_vertical"       <!-- 10dp -->
        marginStart="coui_snack_bar_child_margin_horizontal_start"    <!-- 16dp -->
        marginEnd="coui_snack_bar_child_margin_horizontal_end"        <!-- 16dp -->
        toEndOf="iv_snack_bar_icon" />

    <!-- 右侧动作 -->
    <TextView id="tv_snack_bar_action"
        textAppearance="couiTextAppearanceButton"    <!-- 14sp medium -->
        textColor="@color/coui_snack_bar_action_text_color"
        marginTop/Bottom="coui_snack_bar_action_margin_vertical"      <!-- 10dp -->
        marginEnd="coui_snack_bar_action_margin_horizontal_end"       <!-- 4dp -->
        lineSpacingMultiplier="1.15"
        alignParentEnd="true" />
</RelativeLayout>
```

**关键**：action 是**纯 TextView**，不是按钮胶囊！

## 2. 几何（核心）

| 资源 | 值 | 说明 |
| :--- | ---: | :--- |
| `coui_round_corner_l_radius` | **13.0dp** | **实际圆角**（代码 outline provider） |
| `coui_round_corner_l_weight` | 0.99 | squircle 曲率 |
| `coui_snack_bar_radius` | 16.0dp | 声明值，**源码未引用** |
| `coui_snack_bar_radius_single_line` | 24.0dp | 声明值，**源码未引用** |
| `coui_snack_bar_max_width` | **328.0dp** | 最大宽度 |
| `coui_snack_bar_width` | 368.0dp | 标称宽 |
| `coui_snack_bar_margin_bottom` | **32.0dp** | 距底 |
| `coui_snack_bar_padding_ver` | 16.0dp | — |
| `coui_snack_bar_shadow_size` | **26.0dp** | 阴影 |
| `coui_snack_bar_background_radius` | 12.67dp | 背景 drawable 的圆角 |
| `coui_snack_bar_icon_width/height` | **30.0dp** | 图标 |
| `coui_snack_bar_icon_margin_top_horizontal` | **16.0dp** | 图标上下 |
| `coui_snack_bar_context_margin_start_with_icon` | **16.0dp** | 图标左 |
| `coui_snack_bar_child_margin_horizontal_start` | 16.0dp | 文字左 |
| `coui_snack_bar_child_margin_horizontal_end` | 16.0dp | 文字右 |
| `coui_snack_bar_child_margin_vertical` | **10.0dp** | 文字上下 |
| `coui_snack_bar_child_margin_vertical_multi_lines` | 12.0dp | 多行 |
| `coui_snack_bar_child_margin_vertical_multi_lines_bottom` | 8.0dp | 多行底部 |
| `coui_snack_bar_action_margin_vertical` | **10.0dp** | action 上下 |
| `coui_snack_bar_action_margin_horizontal_end` | **4.0dp** | action 右 |
| `coui_snack_bar_action_max_width` | **110.0dp** | action 最大宽 |
| `coui_snack_bar_action_multi_line_interval` | 8.0dp | 多行 action 间距 |
| `coui_snack_bar_action_margin_top_multi_lines` | 8.0dp | 多行 action 上 |
| `coui_snack_bar_action_margin_bottom_multi_lines` | 9.0dp | 多行 action 下 |

### 圆角（已溯源到代码）

`coui_snack_bar_radius = 16dp` / `_radius_single_line = 24dp` **在源码里没有任何引用**——
真正的圆角来自 `COUISnackBar` 构造时设置的 outline provider：

```java
this.f8455h.setOutlineProvider(new b(
    v2.a.c(R$attr.couiRoundCornerL, context),       // coui_round_corner_l       = 16dp
    v2.a.c(R$attr.couiRoundCornerLRadius, context), // coui_round_corner_l_radius = 13dp
    v2.a.e(R$attr.couiRoundCornerLWeight, context), // coui_round_corner_l_weight = 0.99
    v2.a.c(R$attr.couiRoundCornerXXL, context)      // coui_round_corner_xxl     = 24dp
));
this.f8455h.setClipToOutline(true);
```

即 **`l` 档：名义 16dp / 实际半径 13dp / 曲率 0.99**，收起态用 `xxl` 档（24dp）。

这与 Dialog、Dropdown 是同一个模式：**看 `_radius`，名义值是给轮廓计算用的中间量。**

### 阴影

```java
j4.g.f(view, 2,
    coui_snack_bar_shadow_size,          // 26dp
    support_shadow_size_level_one,
    coui_snack_bar_background_shadow_color);  // #EE000000
```

阴影色 **`#EE000000`**（93% 黑），比 rmuix 的 `alpha=0.1` 重得多。

## 3. 颜色

| 元素 | 资源 | 值 |
| :--- | :--- | :--- |
| 背景 | `couiColorSurfaceTop` | **`#FFFFFFFF`** / **`#FF333333`** |
| 阴影 | `coui_snack_bar_background_shadow_color` | **`#EE000000`**，size 26dp |
| 正文 | `couiColorPrimaryNeutral` | **`#E6000000`** / **`#E6FFFFFF`** |
| Action（启用） | `couiColorPrimaryTextOnPopup` | 待查 |
| Action（禁用） | `coui_snack_bar_action_text_disable` | **`#26FFFFFF`** |

**背景是亮色的！**（`#FFFFFFFF` 亮 / `#FF333333` 暗）——不是 rmuix 用 `onSecondaryVariant`（深色）那种反色底。

这是最大的视觉差异：ColorOS 的 snackbar 是「**浅色浮起卡片**」，不是「深色反色条」。

## 4. 文字

| 元素 | 样式 | 字号 | 字重 |
| :--- | :--- | ---: | :--- |
| 正文 | `couiTextAppearanceButton` | **14.0sp** | `sans-serif-medium` |
| Action | `couiTextAppearanceButton` | **14.0sp** | `sans-serif-medium` |

两者**字号字重完全相同**，只是颜色不同。

## 5. rmuix 现状对比

| 项 | rmuix | ColorOS | 差 |
| :--- | ---: | ---: | ---: |
| **背景色** | `onSecondaryVariant`（**深色反色**） | **`#FFFFFFFF`/`#FF333333`** | **根本不同** |
| 圆角 | `16dp` 固定 | **单行 24dp / 多行 16dp** | 自适应 |
| 最大宽 | — | **328dp** | 新增 |
| 距底 | — | **32dp** | — |
| 内边距 | `InsideMargin = 12dp all` | 文字上下 **10dp**，左右 **16dp** | 不同 |
| 最小高 | **48dp** | 图标 30 + 上下 16×2 = 62dp | +14 |
| 正文字号 | `body2` | **14sp** medium | — |
| **Action 形态** | **`TextButton` 胶囊**（`ActionCornerRadius = 50dp`，`actionContainerColor = primary`） | **纯 TextView**，无背景 | **形态不同** |
| Action 字号 | `15.sp` 硬编码 | **14sp** medium | −1 |
| Action 色 | `onPrimary`（白） | `couiColorPrimaryTextOnPopup` | — |
| 图标尺寸 | 关闭 20dp | 状态图标 **30dp** | 不同概念 |
| 阴影 | `radius=10dp, alpha=0.1` | `shadow_size = 26dp` | — |

### 最关键的三个差异

1. **背景色反了**：rmuix 深色底浅字，ColorOS 浅色底深字
2. **Action 是纯文字，没有胶囊背景**：rmuix 用 `TextButton` 渲染成 primary 底白字的胶囊
3. **圆角自适应**：单行 24dp / 多行 16dp

## 6. 关于「button 已做了一部分」

用户提到的这点已确认：

```kotlin
TextButton(
    text = visuals.actionLabel,
    cornerRadius = SnackbarDefaults.ActionCornerRadius,     // 50dp 胶囊
    minWidth = 26.dp,
    minHeight = 26.dp,
    colors = ButtonDefaults.textButtonColorsPrimary(
        color = colors.actionContainerColor,                 // primary
        textColor = colors.actionContentColor,               // onPrimary 白
    ),
    insideMargin = SnackbarDefaults.ActionInsideMargin,      // 12dp 横 0 纵
    textStyle = TextStyle(fontSize = 15.sp),                 // 硬编码 15sp
)
```

**确实已经做了一部分**——但方向和 ColorOS 相反：

| 项 | rmuix 现状 | ColorOS |
| :--- | :--- | :--- |
| 组件 | `TextButton` | `TextView` |
| 背景 | primary 填充胶囊 | **无背景** |
| 文字色 | 白 | `couiColorPrimaryTextOnPopup` |
| 字号 | 15sp 硬编码 | 14sp medium |
| 圆角 | 50dp | 无（无背景） |

**ColorOS 的 action 根本不是按钮，是一段可点击文字。**

## 7. 实施建议

### 必须改（视觉根本差异）

```kotlin
object SnackbarDefaults {
    val CornerRadiusSingleLine = 24.dp   // 新增
    val CornerRadius = 16.dp             // 多行，保留
    val MaxWidth = 328.dp                // 新增
    val MarginBottom = 32.dp             // 新增
    val ShadowSize = 26.dp               // 阴影
    val ContentVerticalPadding = 10.dp   // 新增
    val ContentHorizontalPadding = 16.dp // 新增
    val ActionMaxWidth = 110.dp          // 新增
}
```

**背景色**：
```kotlin
containerColor = if (isSystemInDarkTheme()) Color(0xFF333333) else Color(0xFFFFFFFF)
contentColor = if (isSystemInDarkTheme()) Color(0xE6FFFFFF) else Color(0xE6000000)
```

**Action 去掉胶囊**：
```kotlin
// 改前：TextButton with primary 胶囊
// 改后：纯 Text，14sp medium，无背景
Text(
    text = visuals.actionLabel,
    fontSize = 14.sp,
    fontWeight = FontWeight.Medium,
    color = colors.actionContentColor,   // couiColorPrimaryTextOnPopup
    modifier = Modifier
        .clickable { scope.launch { onAction() } }
        .padding(vertical = 10.dp)
        .widthIn(max = 110.dp),
)
```

**圆角自适应**：需要测量行数。可以用 `onTextLayout` 的 `lineCount`，或用现有 BasicComponent 那种高度估算。

### 待决策

1. **背景色改成浅色**：这是最大改动，且 `SnackbarColors` 的语义会反转
2. **Action 从 TextButton 改成纯 Text**：`actionContainerColor` 参数将失效
3. **圆角自适应**：需要额外测量逻辑

## 8. 待确认

- [ ] `couiColorPrimaryTextOnPopup` 实际色值
- [x] ~~圆角来源~~ → **`coui_round_corner_l_radius = 13dp`**，通过 outline provider 设置
- [ ] 图标 30dp 是什么（状态图标，不是关闭按钮）
- [ ] `ActionContainerColor` 参数废弃还是保留

## 8.6 实施记录

### 已实现

| 改动 | 内容 |
| :--- | :--- |
| 背景色 | `onSecondaryVariant` → **`#FFFFFFFF` / `#FF333333`**（浅色卡片） |
| 正文色 | `secondaryVariant` → **`#E6000000` / `#E6FFFFFF`** |
| 圆角 | 16dp → **13dp**（`l_radius`） |
| 最大宽 | 新增 **328dp** |
| 内边距 | `InsideMargin`（12dp all）→ **水平 16dp / 垂直 10dp** |
| 正文字号 | `body2` → **14sp medium** |
| Action | `TextButton` 胶囊 → **纯 Text**，14sp medium，最大宽 110dp |
| Action 间距 | 左 12dp / 右 **4dp** / 垂直 **10dp** |
| 阴影 | `10dp / 0.1` → **26dp / 0.93** |

### 新增常量

```kotlin
val CornerRadius = 13.dp
val CornerRadiusSingleLine = 24.dp
val MaxWidth = 328.dp
val MarginBottom = 32.dp
val ContentHorizontalPadding = 16.dp
val ContentVerticalPadding = 10.dp
val ContentVerticalPaddingMultiline = 12.dp
val ContentVerticalPaddingMultilineBottom = 8.dp
val IconSize = 30.dp
val IconVerticalMargin = 16.dp
val ActionMaxWidth = 110.dp
val ActionMarginEnd = 4.dp
val ActionVerticalMargin = 10.dp
val ShadowRadius = 26.dp
```

### 兼容处理

| 项 | 处理 |
| :--- | :--- |
| `ActionCornerRadius` | `@Deprecated`，保留（调用方自定义 action 仍可用） |
| `ActionInsideMargin` | `@Deprecated`，指向 `ActionVerticalMargin` |
| `InsideMargin` 参数 | 保留签名，KDoc 注明已不生效 |
| `actionContainerColor` | 保留，默认改 `Color.Transparent`（默认布局不再使用） |

### 未实现

- **单行 24dp 圆角过渡**：`CornerRadiusSingleLine` 常量已定义但未接入，需要测量行数
- **前置 30dp 图标**：rmuix 的 `SnackbarVisuals` 无图标字段，未加

## 9. 影响面

```
Snackbar 调用点：SnackbarHost 内部
example/demo：SnackbarSection、SnackbarDemo
```

影响面小，但 `SnackbarColors` 的 5 个字段里有 2 个会失去意义。
