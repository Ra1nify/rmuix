# Stepper

`Stepper` 是 Rmuix 的数值步进器：中间是数值读数，两侧是圆形减号与加号按钮。它会把数值限制在 `minValue` / `maxValue` 之间、在到达端点时自动禁用对应按钮，并在每次步进时触发一次轻微触感反馈。

<div style="position: relative; height: 200px; border-radius: 10px; overflow: hidden; border: 1px solid #777;">
    <iframe id="demoIframe" style="position: absolute; top: 0; left: 0; width: 100%; height: 100%; border: none;" src="../../compose/index.html?id=stepper" title="Demo" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" referrerpolicy="strict-origin-when-cross-origin"></iframe>
</div>

## Stock ColorOS 静态复核（2026-11-12）

- 符号是矢量而不是字形：`ic_minus_sign` 是 **13.6dp 圆头线**（x = 9.199 → 22.799）；`ic_plus_sign` 的两条臂各 **15.2dp、两端圆头**（x = 8.398 → 23.598、y = 8.4 → 23.6），32dp 视口内笔画都是 **1.6dp**。

- 两个按钮都是 **32dp** 触控区（`stepper_button_size`），填充 `?attr/couiColorContainer8`（`#14000000` / `#1AFFFFFF`），且**静止态没有边框**：源码把 `ShapeDrawable`、`COUIMaskEffectDrawable`、`COUIStrokeDrawable` 组合起来，而描边只由 `FocusChangedMetricHelper.HAS_FOCUS` 驱动。
- 符号是矢量而不是字形：`ic_minus_sign` 是 **13.6dp 圆头线**（x = 9.199 → 22.799），`ic_plus_sign` 是两条 **15.2dp 方头**条（8.398 → 23.598 与 8.4 → 23.6），在 32dp 视口里都是 **1.6dp** 粗。组件此前用加粗的 `Text("-")` / `Text("+")` 字形。
- 启用态符号与读数取 `?attr/couiColorPrimaryNeutral`（`#E6000000` / `#E6FFFFFF`）；禁用态矢量（`ic_minus_sign_disable` / `ic_plus_sign_disable`）与禁用读数（`coui_stepper_text_color_select`）改为 `?attr/couiColorDivider`（`#1F000000` / `#33FFFFFF`），不是淡出。
- 读数保留 `android:minWidth="44dp"`、左右各 12dp 外边距，并使用 `?attr/couiTextAppearanceHeadline5`（**18sp**、`sans-serif-medium`）。
- 键盘聚焦时画与 `Button` 相同的聚焦环：6dp 描边（`default_focus_stroke_radius` × 2），裁掉内侧只留 3dp，颜色 `?attr/couiColorFocusOutline` → `coui_color_focus_outline_blue`（`#660080FF` / `#665C9DFF`）。

**BREAKING：** 按钮去掉静止态边框，符号改为矢量，禁用态改为换色而非淡出；`StepperColors` 新增 `focusOutlineColor` 与 `disabledIconTint`，移除 `borderColor`。

**按压反馈：** `COUIStepperView.configEffectDrawableForButton` 给每个圆形按钮叠三层 drawable —— `couiColorContainer8` 椭圆底、`?attr/couiColorPress` 的 `COUIMaskEffectDrawable` 按压遮罩、以及聚焦描边 —— 并调用 `COUIStateEffectDrawable.enableScaleEffect(view, 2)`。组件现在在缩放之外也补上了按压遮罩（`#1F000000` / `#33FFFFFF`，0.3s 弹簧）。缩放已改为官方
`enableScaleEffect` 结果：`COUIPressAnimatorUnit.initTargetScale()` → `PressCalc.calculateUniformScaleBreakdown()`，
采用 `PressParamsExtKt` 默认值（面积映射 0.85..0.97、20dp..150dp，`BezierData(0,0,0,1)`，长宽比通道 2..15），
32dp 按钮得到 **0.8766**（`StepperDefaults.PressTargetScale`）；按下弹簧为 `jellyPressSpring`
（response 0.25 / bounce 0），回弹为 `jellyHandupSpring`（response 0.5 / bounce 0.6）。

## 引入

```kotlin
import io.github.ra1nify.rmuix.kmp.basic.Stepper
import io.github.ra1nify.rmuix.kmp.basic.StepperDefaults
```

## 基本用法

```kotlin
var value by remember { mutableIntStateOf(1) }

Stepper(
    value = value,
    onValueChange = { value = it },
)
```

## 组件状态

### 边界

减号在 `minValue` 处禁用，加号在 `maxValue` 处禁用；两者都会把符号颜色切到 `couiColorDivider`。

```kotlin
var value by remember { mutableIntStateOf(0) }

Stepper(
    value = value,
    onValueChange = { value = it },
    minValue = 0,
    maxValue = 5,
)
```

### 自定义步长与格式

```kotlin
var percent by remember { mutableIntStateOf(50) }

Stepper(
    value = percent,
    onValueChange = { percent = it },
    minValue = 0,
    maxValue = 100,
    step = 10,
    valueFormatter = { "$it%" },
)
```

### 禁用

```kotlin
Stepper(
    value = 3,
    onValueChange = {},
    enabled = false,
)
```

## 属性

### Stepper 属性

| 属性名         | 类型                  | 说明                                                       | 默认值                        | 是否必需 |
| -------------- | --------------------- | ---------------------------------------------------------- | ----------------------------- | -------- |
| value          | Int                   | 当前值                                                     | -                             | 是       |
| onValueChange  | (Int) -> Unit         | 步进后回调，参数已做边界钳制                               | -                             | 是       |
| modifier       | Modifier              | 应用到整行的修饰符                                         | Modifier                      | 否       |
| minValue       | Int                   | 下界；到达时禁用减号                                       | 0                             | 否       |
| maxValue       | Int                   | 上界；到达时禁用加号                                       | 100                           | 否       |
| step           | Int                   | 每次点击增减的数值                                         | 1                             | 否       |
| enabled        | Boolean               | 整个步进器是否接受输入                                     | true                          | 否       |
| valueFormatter | (Int) -> String       | 把数值转成读数文本                                         | `{ it.toString() }`           | 否       |
| colors         | StepperColors         | 按钮、符号、读数与聚焦颜色                                 | `StepperDefaults.stepperColors()` | 否   |

### StepperDefaults 对象

| 常量名                      | 类型 | 说明                                                       | 默认值 |
| --------------------------- | ---- | ---------------------------------------------------------- | ------ |
| ButtonSize                  | Dp   | 两个按钮的触控尺寸（`stepper_button_size`）                | 32.dp  |
| PressTargetScale            | Float | `PressCalc.calculateUniformScaleBreakdown()` 对 `stepper_button_size` 的结果 | 0.8766 |
| ValueTextSize               | TextUnit | 读数字号（`?attr/couiTextAppearanceHeadline5`）       | 18.sp  |
| ValueFontWeight             | FontWeight | 读数字重（`sans-serif-medium`）                     | FontWeight.Medium |
| ValueMinWidth               | Dp   | 读数最小宽度（`android:minWidth="44dp"`）                  | 44.dp  |
| ValueHorizontalPadding      | Dp   | 读数左右外边距                                             | 12.dp  |

`StepperDefaults.stepperColors()` 会按当前主题解析 `buttonBgColor`（`couiColorContainer8`）、`iconTint` 与 `textColor`（`couiColorPrimaryNeutral`）、`disabledTextColor` / `disabledIconTint`（`couiColorDivider`）以及 `focusOutlineColor`（`couiColorFocusOutline`）。

## 进阶用法

### 自定义颜色

```kotlin
Stepper(
    value = value,
    onValueChange = { value = it },
    colors = StepperDefaults.stepperColors(
        iconTint = Color(0xFF3482FF),
        textColor = Color(0xFF3482FF),
    ),
)
```

### 放在偏好行里

`Stepper` 自身不带行内边距，因此可以放进 `Row` 的尾部位置。

```kotlin
Row(
    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.SpaceBetween,
) {
    Text("亮度档位")
    Stepper(value = level, onValueChange = { level = it }, maxValue = 10)
}
```

## 最佳实践

- 始终传入 `minValue` / `maxValue`：端点禁用依赖这两个边界，按钮不会回绕。
- 读数带单位时把 `step` 与 `valueFormatter` 一起用，保证单位与步长同步。
- 不要给按钮加边框：官方静止态没有边框，键盘焦点由聚焦环表达。
- 数值变大时行高不应变化：`ValueMinWidth`（44dp）已为两位数预留空间，超过之后才会把两侧按钮外推。
