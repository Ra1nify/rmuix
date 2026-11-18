# CompleteState

ColorOS 的完成页：`COUICompleteStateView`（`coui_component_complete_state.xml`）。

[EmptyState](./emptystate) 的姊妹组件：同样的居中列，但插图 100dp、标题 20sp（`?attr/couiTextHeadlineM`）、
副标题上方间距 8dp。

## 引入

```kotlin
import io.github.ra1nify.rmuix.kmp.basic.CompleteState
```

## 基本用法

```kotlin
CompleteState(
    title = "全部完成",
    subtitle = "所有内容都已备份。",
    illustration = { Image(painter = painterResource(Res.drawable.done), contentDescription = null) },
)
```

## 参数

| 参数名        | 类型                     | 说明                                | 默认值 |
| :------------ | :----------------------- | :---------------------------------- | :----- |
| title         | String                   | 标题（`?attr/couiTextHeadlineM`，20sp 中粗） | - |
| modifier      | Modifier                 | 应用到整页                          | Modifier |
| subtitle      | String?                  | 副标题（`?attr/couiTextBodyXS`，12sp） | null |
| illustration  | (@Composable () -> Unit)? | 插图；`null` 时保留 100dp 空位        | null |
| colors        | EmptyStateColors         | 整页颜色                            | EmptyStateDefaults.emptyStateColors() |

## CompleteStateDefaults

| 名称               | 类型     | 来源                                            | 值 |
| :----------------- | :------- | :---------------------------------------------- | :---- |
| IllustrationSize   | Dp       | 布局里 100dp 的 `EffectiveAnimationView`          | 100dp |
| HorizontalPadding  | Dp       | `complete_page_text_padding_hor`                 | 8dp |
| TitleFontSize      | TextUnit | `?attr/couiTextHeadlineM`                        | 20sp |
| TitleTopMargin     | Dp       | `complete_page_text_title_margin_top`            | 8dp |
| SubtitleFontSize   | TextUnit | `?attr/couiTextBodyXS`                           | 12sp |
| SubtitleTopMargin  | Dp       | `complete_page_text_subtitle_margin_top`         | 8dp |

官方插图是 `EffectiveAnimationView`，资源在库里而不在 APK，因此这里做成插槽；`null` 时仍保留 100dp 空位，
与布局一致。
