# EmptyState

ColorOS 的空状态页：`COUIEmptyStateView`（`coui_component_empty_state.xml`）。

居中的插图 + 280dp 宽的标题/副标题块 + 可选操作文字。官方把文本块放进限高 92dp 的
`COUIMaxHeightScrollView`，这样长文案不会把操作挤出去；标题最多两行、副标题最多三行。

## 引入

```kotlin
import io.github.ra1nify.rmuix.kmp.basic.EmptyState
```

## 基本用法

```kotlin
EmptyState(
    title = "还没有照片",
    subtitle = "你拍的照片会显示在这里。",
    actionText = "去拍照",
    onActionClick = { takePhoto() },
    illustration = { Image(painter = painterResource(Res.drawable.empty_photos), contentDescription = null) },
)
```

## 参数

| 参数名        | 类型                     | 说明                                    | 默认值 |
| :------------ | :----------------------- | :-------------------------------------- | :----- |
| title         | String                   | 标题（`?attr/couiTextHeadlineXS`，16sp 中粗） | - |
| modifier      | Modifier                 | 应用到整页                              | Modifier |
| subtitle      | String?                  | 副标题（`?attr/couiTextBodyXS`，12sp）  | null |
| actionText    | String?                  | 操作文字（`?attr/couiTextButtonM`，14sp 中粗） | null |
| onActionClick | (() -> Unit)?            | 点击操作时回调                          | null |
| illustration  | (@Composable () -> Unit)? | 文本上方的插图                          | null |
| colors        | EmptyStateColors         | 整页颜色                                | EmptyStateDefaults.emptyStateColors() |

## EmptyStateDefaults

| 名称               | 类型   | 来源                                                       | 值 |
| :----------------- | :----- | :--------------------------------------------------------- | :---- |
| GroupWidth         | Dp     | `coui_component_empty_group_width`                          | 280dp |
| TextGroupMaxHeight | Dp     | `coui_component_empty_text_scroll_max_height`               | 92dp |
| TitleFontSize      | TextUnit | `?attr/couiTextHeadlineXS`                                | 16sp |
| TitleMaxLines      | Int    | 标题的 `android:maxLines`                                   | 2 |
| SubtitleFontSize   | TextUnit | `?attr/couiTextBodyXS`                                    | 12sp |
| SubtitleTopMargin  | Dp     | 副标题的 `android:layout_marginTop`                         | 2dp |
| SubtitleMaxLines   | Int    | 副标题的 `android:maxLines`                                 | 3 |
| ActionFontSize     | TextUnit | `?attr/couiTextButtonM`                                   | 14sp |
| ActionTopMargin    | Dp     | 操作文字的 `android:layout_marginTop`                       | 8dp |

## 颜色

| 槽位          | 来源                            | 浅色        | 深色        |
| :------------ | :------------------------------ | :---------- | :---------- |
| titleColor    | `?attr/couiColorLabelPrimary`    | `#E6000000` | `#E6FFFFFF` |
| subtitleColor | `?attr/couiColorLabelSecondary`  | `#8A000000` | `#8AFFFFFF` |
| actionColor   | `?attr/couiColorPrimaryText`     | `#0080FF`   | `#5C9DFF`   |

## 已知差异

- 官方的 `EmptyStateAnimView` 是一个 `EffectiveAnimationView`，动画资源在 `coui-support-emptyview` 库里、
  不在 APK 资源中，所以这里把插图做成插槽而不是内置动画。官方在 `setAnimSize` 拿到 0 尺寸时会隐藏它。
- 官方的 `COUIMaxHeightScrollView` 允许长文案在 92dp 内滚动；本组件只做高度上限。
