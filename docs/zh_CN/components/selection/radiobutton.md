# RadioButton

`RadioButton` 支持选中和未选中两种状态。选中时显示带白色中心的主色圆形，未选中时显示空心圆环，适用于互斥单选。

<div style="position: relative; height: 384px; border-radius: 10px; overflow: hidden; border: 1px solid #777;">
    <iframe id="demoIframe" style="position: absolute; top: 0; left: 0; width: 100%; height: 100%; border: none;" src="../../../compose/index.html?id=radioButton" title="Demo" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" referrerpolicy="strict-origin-when-cross-origin"></iframe>
</div>

## 引入

```kotlin
import io.github.ra1nify.rmuix.kmp.basic.RadioButton
import io.github.ra1nify.rmuix.kmp.basic.RadioButtonDefaults
import io.github.ra1nify.rmuix.kmp.theme.RmuixTheme
```

## 基本用法

RadioButton 组件通常在互斥选择组中使用：

```kotlin
var selectedIndex by remember { mutableIntStateOf(0) }

Column {
    RadioButtonPreference(
        title = "选项 A",
        selected = selectedIndex == 0,
        onClick = { selectedIndex = 0 }
    )
    RadioButtonPreference(
        title = "选项 B",
        selected = selectedIndex == 1,
        onClick = { selectedIndex = 1 }
    )
}
```

## 组件状态

### Stock ColorOS 绘制

未选中态是中间透明的描边圆环，而不是实心圆。24 dp 视口内，选中外圆半径为 10 dp，白色中心半径为 6 dp。
动画按官方矢量关键帧执行：150 ms 达到 1.08 倍，300 ms 回到原尺寸；取消选中时填充在 200 ms 内消失，圆环在 267 ms 内恢复。不额外叠加按压缩放，只读控件也没有按压处理器。
官方不可逆 selector 会保留动画末帧：初始/静态内点为 `#FFFFFF`，完成一次启用态选中动画后，默认内点保持 `#FAFAFA`，直到重新选中静态条目；非白色自定义颜色不受影响。中断后从上一次目标状态重启，而不是反向续播半途进度。启用状态变化时直接选择静态条目，不播放转换。

BREAKING: `SelectedInnerRadius` 从 4.5 dp 改为 6 dp；`disabledSelectedColor` 从 `disabledPrimary` 改为主色乘 30% alpha；暗色禁用白点从 `#26FFFFFF` 改为 `#29FFFFFF`。中性色跟随当前库主题。

### 独立控件与装饰标记

可交互的独立 RadioButton 恢复框架控制背景，不套用 Checkbox 的 COUI hover/focus 弹簧遮罩。Android 从框架 RadioButton 样式读取背景，并透传 checked、pressed、hovered、focused、window-focused 和触点状态；半径保留平台资源值，不能读取原生 RippleDrawable 时才使用 20 dp 回退。
背景采用独立的状态色：启用且选中时为框架 `colorControlActivated` 的 26% alpha，不能把它与图形 primary 混为一谈；未选中为官方 `#7FD0D0D0 / #7F707070`。Android 默认解析宿主主题的 activated 角色；非 Android 使用 APK 裸资源 `coui_theme_primary_color=#FFFFFFFF` 作为明确回退，不能据此推断 ColorOS 运行时 overlay 的实际颜色。禁用或 `onClick = null` 不创建反馈；Preference/单选列表的装饰标记交由整行处理反馈。
非 Android 保留软件 press 回退，背景参考本机 SDK 的框架规则：焦点优先于悬停，活动窗口焦点目标 opacity=0.6，悬停/非活动窗口焦点为 0.2，80 ms 线性进入；不是两个圆层相加，也不使用 Checkbox 的 ARGB gamma 弹簧。按下期间不重启已有焦点/悬停背景动画。
网页与示例 App 增加独立交互/请求焦点对照。软件回退、宿主 framework 材质及跨厂商资源覆盖仍需 ColorOS 实机核验，不标记完整 1:1。

### 背景颜色覆盖

`RadioButtonDefaults.feedbackColors(activatedColor)` 显式配置背景 activated 角色；其返回值有 `checked` 和 `unchecked` 两个独立颜色。若应用希望将该角色绑定到库主色，可以显式指定；这是应用映射，不是对 APK 占位白色的蓝色猜测。末尾新增的可选 `feedbackColors` 不影响原有调用。

```kotlin
RadioButton(
    selected = selected,
    onClick = { selected = true },
    feedbackColors = RadioButtonDefaults.feedbackColors(
        activatedColor = RmuixTheme.colorScheme.primary
    )
)
```

### 禁用状态

```kotlin
RadioButton(
    selected = true,
    onClick = null,
    enabled = false
)
```

## 属性

### RadioButton 属性

| 属性名    | 类型              | 说明                                              | 默认值                                    | 是否必须 |
| --------- | ----------------- | ------------------------------------------------- | ----------------------------------------- | -------- |
| selected  | Boolean           | 单选按钮是否被选中                                | -                                         | 是       |
| onClick   | (() -> Unit)?     | 点击时的回调；为 `null` 时组件不可交互            | -                                         | 是       |
| modifier  | Modifier          | 应用于单选按钮的修饰符                            | Modifier                                  | 否       |
| colors    | RadioButtonColors | 单选按钮的颜色配置                                | RadioButtonDefaults.radioButtonColors()   | 否       |
| enabled   | Boolean           | 单选按钮是否可交互                                | true                                      | 否       |
| feedbackColors | RadioButtonFeedbackColors | 独立的框架背景状态色 | RadioButtonDefaults.feedbackColors() | 否 |

### RadioButtonDefaults 对象

RadioButtonDefaults 对象提供了 RadioButton 组件的几何常量与默认颜色配置。

#### 属性

| 属性名              | 类型 | 描述                                   | 值     |
| ------------------- | ---- | -------------------------------------- | ------ |
| Size                | Dp   | 控件整体尺寸                           | 24.dp  |
| SelectedOuterRadius | Dp   | 选中时外圆半径                         | 10.dp  |
| SelectedInnerRadius | Dp   | 选中时内部白点半径                     | 6.dp   |
| UncheckedRingRadius | Dp   | 未选中时圆环半径                       | 9.2.dp |
| StrokeWidth         | Dp   | 圆环描边宽度（与复选框一致）           | 1.3.dp |
| StateLayerRadius | Dp | 软件/原生背景解析失败时的反馈半径 | 20.dp |
| MarkMarginStart     | Dp   | 设置行内的起始边距                     | 16.dp  |

#### 方法

| 方法名              | 类型              | 说明                       |
| ------------------- | ----------------- | -------------------------- |
| radioButtonColors() | RadioButtonColors | 创建单选按钮的默认颜色配置 |

### RadioButtonColors 类

| 属性名                | 类型  | 说明                     |
| --------------------- | ----- | ------------------------ |
| selectedColor         | Color | 选中状态时的外圆颜色     |
| disabledSelectedColor | Color | 禁用且选中时的外圆颜色   |
| uncheckedStrokeColor          | Color | 未选中时的圆环颜色                           |
| disabledUncheckedStrokeColor | Color | 未选中且禁用时的圆环颜色                     |
| selectedInnerColor           | Color | 选中时的内部白点颜色                         |
| disabledSelectedInnerColor   | Color | 选中且禁用时的内部白点颜色                   |

## 进阶用法

### 自定义颜色

```kotlin
var selected by remember { mutableStateOf(false) }

RadioButton(
    selected = selected,
    onClick = { selected = !selected },
    colors = RadioButtonDefaults.radioButtonColors(
        selectedColor = Color.Red
    )
)
```

### 在列表中使用

```kotlin
val options = listOf("选项 A", "选项 B", "选项 C")
var selectedIndex by remember { mutableIntStateOf(0) }

options.forEachIndexed { index, option ->
    Card(
        modifier = Modifier.padding(bottom = 12.dp)
    ) {
        RadioButtonPreference(
            title = option,
            selected = selectedIndex == index,
            onClick = { selectedIndex = index }
        )
    }
}
```
