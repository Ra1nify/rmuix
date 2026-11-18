# Modifier.pressable

`Modifier.pressable` 给任意表面加上 ColorOS 17 的按压动效：按下时内容缩放，抬起或取消时弹回。

它是卡片、色块等"自带背景的可点击表面"所使用的按压反馈的 Modifier 形式。这类表面不需要
`Modifier.clickable` 附带的水波纹，因此本 Modifier 只挂**点击，不绘制任何 indication**。

## 引入

```kotlin
import io.github.ra1nify.rmuix.kmp.basic.pressable
```

## 基本用法

```kotlin
Box(
    Modifier
        .size(48.dp)
        .pressable(pressedScale = 0.92f, onClick = { select(index) })
)
```

## 参数

| 参数名              | 类型                | 说明                                                    | 默认值 | 必填 |
| ------------------- | ------------------- | ------------------------------------------------------- | ------ | ---- |
| enabled             | Boolean             | 是否启用按压反馈与点击                                  | true   | 否   |
| pressedScale        | Float               | 按下时的缩放比例；传 `1f` 即只响应点击、不做缩放         | 0.97f  | 否   |
| pressDampingRatio   | Float               | 按下动画的阻尼比                                        | 0.82f  | 否   |
| pressStiffness      | Float               | 按下动画的刚度                                          | 1600f  | 否   |
| releaseDampingRatio | Float               | 回弹动画的阻尼比（越小越"弹"）                          | 0.65f  | 否   |
| releaseStiffness    | Float               | 回弹动画的刚度                                          | 380f   | 否   |
| haptic              | HapticFeedbackType? | 点击时触发的触感；`null` 表示不触发                     | null   | 否   |
| onClick             | (() -> Unit)?       | 点击回调；`null` 时只提供按压反馈                       | null   | 否   |

## 说明

- 弹簧参数对应 `com.coui.appcompat.animation.touchanimation.PressEngineConfig`：按下 `0.82` 阻尼 /
  `1600` 刚度，回弹 `0.65` / `380`。
- 不绘制水波纹等任何 indication。需要水波纹时请自行与 `Modifier.clickable` 组合，或直接用
  `Button` / `IconButton`（它们自带反馈）。
- 点击 lambda 通过 `rememberUpdatedState` 读取，长按期间不会用到过期的闭包。
- 抬起、滑出边界、多指取消都会回弹到 `1f`，不会残留"卡在按下态"。
