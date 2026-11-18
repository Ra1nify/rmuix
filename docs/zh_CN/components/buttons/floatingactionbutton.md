# FloatingActionButton

`FloatingActionButton` 是 Rmuix 中的悬浮按钮组件，通常用于展示页面中最重要或最常用的操作。它通常悬浮在界面上方，具有突出的视觉效果，便于用户快速访问。

此组件通常与 `Scaffold` 组件结合使用，以便在应用程序的不同页面中保持一致的布局和行为。

<div style="position: relative; height: 300px; border-radius: 10px; overflow: hidden; border: 1px solid #777;">
    <iframe id="demoIframe" style="position: absolute; top: 0; left: 0; width: 100%; height: 100%; border: none;" src="../../../compose/index.html?id=floatingActionButton" title="Demo" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" referrerpolicy="strict-origin-when-cross-origin"></iframe>
</div>

## Stock ColorOS 静态复核（2026-11-12）

本轮读 Settings APK 的 `COUIFABPressFeedbackUtil` / `COUIFABShadowUtils` 与 `coui_floating_button_fab_*` / `coui_shadow_*_caustic_fab` 尺寸。

- 常规按钮为 `coui_floating_button_fab_normal_size` = **56dp**（库内原 60dp），迷你尺寸为 `coui_floating_button_fab_mini_size` = **54dp**，不是 Material 的 40dp。
- 按钮距屏幕边缘 `coui_floating_button_fab_side_margin` = **12dp**，以 `FloatingActionButtonDefaults.SideMargin` 暴露。
- 阴影高度为 `coui_shadow_elevation_caustic_fab` = **8dp**（库内原 4dp）；caustic 光效阴影的其余 token（`coui_shadow_light_*_caustic_fab`）属于 OPPO 光效管线，未移植。
- 其余外观（圆形、主色容器）本来就符合 Material 契约。

**BREAKING：** 默认尺寸 60dp → 56dp，默认阴影高度 4dp → 8dp；`FloatingActionButtonDefaults` 新增 `MiniSize` 与 `SideMargin`。

**仍待复核 / 更正：** caustic/光效阴影管线（`COUIFABShadowUtils`、`coui_shadow_light_*`）未建模。此前
“按压反馈（`COUIFABPressFeedbackUtil`）交给共享 `Surface` 的 indication”把两个视图混在一起：
`COUIFABPressFeedbackUtil` 只被 `COUIFloatingButtonLabel`（扩展 FAB 的子按钮）使用——1.0 → 0.9 缩放 200ms、
回弹 340ms，插值器均为 `COUIMoveEaseInterpolator` = cubic-bezier(0.3, 0, 0.1, 1)；普通 `COUIFloatingButton`
仍保留按压常量（`DEFAULT_PRESS_FEEDBACK_SCALE_MIN_VALUE` 0.9、200ms），但 `getScaleValue()` /
`getBrightnessValue()` 没有调用点，其实际反馈是共享的 `COUIMaskEffectDrawable` 状态遮罩
（`COUIStateEffectDrawable`，response 0.3 / bounce 0），库内由主题 `RmuixIndication` 复现。未建模的只是扩展
FAB 标签的缩放动画。
## 引入

```kotlin
import io.github.ra1nify.rmuix.kmp.basic.FloatingActionButton
```

## 基本用法

FloatingActionButton 组件可以用于触发重要的操作：

```kotlin
FloatingActionButton(
    onClick = { /* 处理点击事件 */ }
) {
    Icon(
        imageVector = RmuixIcons.Add,
        contentDescription = "添加"
    )
}
```

## 组件状态

由于本组件通常用于最常用的操作，因此组件本身没有内置状态变化。

## 属性

### FloatingActionButton 属性

| 属性名                     | 类型                   | 说明                     | 默认值                            | 是否必须 |
| -------------------------- | ---------------------- | ------------------------ |--------------------------------| -------- |
| onClick                    | () -> Unit             | 点击按钮时触发的回调     | -                              | 是       |
| modifier                   | Modifier               | 应用于按钮的修饰符       | Modifier                       | 否       |
| shape                      | Shape                  | 按钮的形状               | CircleShape           | 否       |
| containerColor             | Color                  | 按钮的背景颜色           | RmuixTheme.colorScheme.primary | 否       |
| shadowElevation            | Dp                     | 按钮的阴影高度           | FloatingActionButtonDefaults.ShadowElevation | 否       |
| minWidth                   | Dp                     | 按钮的最小宽度           | FloatingActionButtonDefaults.MinWidth        | 否       |
| minHeight                  | Dp                     | 按钮的最小高度           | FloatingActionButtonDefaults.MinHeight       | 否       |
| content                    | @Composable () -> Unit | 按钮内容区域的可组合函数 | -                              | 是       |

### FloatingActionButtonDefaults 对象

FloatingActionButtonDefaults 对象提供了 FloatingActionButton 组件的默认值。

#### 常量

| 常量名          | 类型 | 说明         | 默认值 |
| --------------- | ---- | ------------ | ------ |
| MinWidth        | Dp   | 按钮最小宽度（`coui_floating_button_fab_normal_size`） | 56.dp |
| MinHeight       | Dp   | 按钮最小高度（`coui_floating_button_fab_normal_size`） | 56.dp |
| ShadowElevation | Dp   | 按钮阴影高度（`coui_shadow_elevation_caustic_fab`） | 8.dp |
| MiniSize        | Dp   | 迷你 FAB 尺寸（`coui_floating_button_fab_mini_size`） | 54.dp |
| SideMargin      | Dp   | 距屏幕边缘的最小距离（`coui_floating_button_fab_side_margin`） | 12.dp |

## 进阶用法

### 自定义颜色

```kotlin
FloatingActionButton(
    onClick = { /* 处理点击事件 */ },
    containerColor = Color.Red
) {
    Icon(
        imageVector = RmuixIcons.Favorites,
        contentDescription = "Favorites",
        tint = Color.White
    )
}
```

### 扩展的悬浮按钮

```kotlin
FloatingActionButton(
    onClick = { /* 处理点击事件 */ },
    shape = RoundedCornerShape(16.dp),
    minWidth = 120.dp
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier.padding(horizontal = 16.dp)
    ) {
        Icon(
            imageVector = RmuixIcons.Add,
            contentDescription = "添加",
            tint = Color.White
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text("添加", color = Color.White)
    }
}
```

### 绑定到脚手架的悬浮按钮

```kotlin
Scaffold(
    floatingActionButton = {
        FloatingActionButton(
            onClick = { /* 处理点击事件 */ }
        ) {
            Icon(
                imageVector = RmuixIcons.Add,
                contentDescription = "添加"
            )
        }
    },
    floatingActionButtonPosition = FabPosition.End
) { paddingValues ->
    // 内容区域需要考虑 padding
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
        ) {
            // ...
    }
}
```

### 带动画效果的悬浮按钮

```kotlin
var expanded by remember { mutableStateOf(false) }
val animatedSize by animateDpAsState(
    targetValue = if (expanded) 65.dp else 60.dp,
    label = "FAB 尺寸动画",
)

FloatingActionButton(
    onClick = { expanded = !expanded },
    minWidth = animatedSize,
    minHeight = animatedSize
) {
    Icon(
        imageVector = if (expanded) RmuixIcons.Remove else RmuixIcons.Add,
        contentDescription = if (expanded) "移除" else "添加",
        tint = Color.White
    )
}
```
