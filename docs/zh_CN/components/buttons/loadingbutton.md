# LoadingButton

带 ColorOS 三点加载指示器的按钮。

`COUILoadingButton` 有两条绘制路径：加载文本放得下时，它画文本再加一个字面量 `"..."`，按三个点的位置切成三条带、
每条带单独设 alpha；否则画三个圆。本组件只复刻**圆**这条路径——它不依赖字体度量，各平台表现一致。
点的颜色取按钮的内容色，与官方使用按钮 `TextPaint` 的做法一致。

加载中按钮**仍可点击**，与 `COUIButton` 一致：官方的 loading 态只改变绘制内容。

## 引入

```kotlin
import io.github.ra1nify.rmuix.kmp.basic.LoadingButton
```

## 基本用法

```kotlin
LoadingButton(
    onClick = { submit() },
    loading = submitting,
) {
    Text("提交")
}
```

用 `loadingContent` 可换成别的内容（例如转圈）：

```kotlin
LoadingButton(onClick = {}, loading = true, loadingContent = { CircularProgressIndicator() }) {
    Text("提交")
}
```

## 参数

| 参数名           | 类型                     | 说明                       | 默认值 |
| :--------------- | :----------------------- | :------------------------- | :----- |
| onClick          | () -> Unit               | 点击回调                   | - |
| modifier         | Modifier                 | 应用到按钮                 | Modifier |
| loading          | Boolean                  | 是否用加载指示器替换内容   | false |
| enabled          | Boolean                  | 是否可点击                 | true |
| enableSpotlight  | Boolean                  | 是否启用聚光效果           | false |
| spotlightColor   | Color                    | 聚光颜色                   | Color.White |
| cornerRadius     | Dp                       | 圆角                       | ButtonDefaults.CornerRadius |
| minWidth         | Dp                       | 最小宽度                   | ButtonDefaults.MinWidth |
| minHeight        | Dp                       | 最小高度                   | ButtonDefaults.MinHeight |
| colors           | ButtonColors             | 按钮颜色                   | ButtonDefaults.buttonColors() |
| insideMargin     | PaddingValues            | 内部内距                   | ButtonDefaults.InsideMargin |
| pressFeedbackType| PressFeedbackType        | 按压反馈类型               | PressFeedbackType.Sink |
| interactionSource| MutableInteractionSource? | 提升的交互源               | null |
| indication       | Indication?              | 指示器                     | null |
| loadingContent   | (@Composable () -> Unit)? | 加载态替换三点             | null |
| content          | @Composable RowScope.() -> Unit | 按钮内容             | - |

## LoadingButtonDefaults

| 名称            | 类型 | 说明                                | 值 |
| :-------------- | :--- | :---------------------------------- | :---- |
| DotRadius       | Dp   | `coui_loading_btn_circle_radius`    | 1dp |
| DotSpacing      | Dp   | `coui_loading_btn_circle_spacing`   | 2dp |
| DOT_START_ALPHA | Float| `DOT_START_ALPHA`（20%）            | 51 |
| DOT_MID_ALPHA   | Float| `DOT_MID_ALPHA`（50%）              | 127.5 |
| DOT_END_ALPHA   | Float| `DOT_END_ALPHA`（100%）             | 255 |
| DOT_STAGGER_MILLIS | Int | 两点错开时间                     | 333 |
| CYCLE_MILLIS    | Int  | 一轮时长                         | 1332 |

## 点的动画

每个点四段 `ValueAnimator`，线性插值：

| 段 | 时长（ms） | 点1 起始 | 点2 起始 | 点3 起始 |
| :--- | --: | --: | --: | --: |
| START -> MID | 133 | 0 | 333 | 666 |
| MID -> END | 67 | 133 | 466 | 799 |
| END -> MID | 67 | 467 | 800 | 1133 |
| MID -> START | 133 | 533 | 866 | 1199 |

200–467ms 之间 alpha 保持在最亮，这一段停顿正是「跑动高光」的观感来源。单点跨度 0–666ms，
三点错开 333ms，整套 1332ms 后循环。
