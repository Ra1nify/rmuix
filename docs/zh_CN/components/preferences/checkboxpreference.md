# CheckboxPreference

`CheckboxPreference` 是 Rmuix 中的复选框组件，提供了标题、摘要和复选框控件，支持点击交互，常用于多选项设置和选择列表中。

<div style="position: relative; height: 293px; border-radius: 10px; overflow: hidden; border: 1px solid #777;">
    <iframe id="demoIframe" style="position: absolute; top: 0; left: 0; width: 100%; height: 100%; border: none;" src="../../../compose/index.html?id=checkboxPreference" title="Demo" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" referrerpolicy="strict-origin-when-cross-origin"></iframe>
</div>

## Stock ColorOS 静态复核（2026-11-12）

本轮读 Settings APK 的 `res/layout/coui_preference_widget_checkbox.xml`、`preference_widget_radiobutton.xml` / `settingslib_preference_widget_radiobutton.xml` 与 `coui_preference_widget_select.xml`。

- 官方复选框控件布局（`coui_preference_widget_checkbox.xml`）带 `android:paddingRight="1dp"`、`android:layout_marginLeft` = 0dp、`android:layout_marginRight` = **-10dp**（`coui_preference_checkbox_margin_left` / `_right`）。两者都已应用：尾部复选框加 `CheckboxPreferenceDefaults.EndPadding`（1dp），并用自定义 layout 实现负的 `WidgetMarginEnd` 右边距（上报宽度缩小 10dp），因此复选框相对行的 32dp 尾内边距外溢 9dp（含其自身 1dp padding 的可见右缘 = 行右端 - 22dp）。
- 单选控件布局（`preference_widget_radiobutton.xml`、`settingslib_preference_widget_radiobutton.xml`）**没有任何边距或内边距**，与组件尾部单选（无内边距）一致。
- 前导复选框/单选仍保留 5dp 尾部内边距：官方布局只描述尾部控件，前导放置是库内扩展，因此未动。
- `coui_preference_widget_select.xml` 显示官方选择行带**尾部状态文字**（`coui_preference_status_text_max_width` 162dp、`support_preference_widget_focus_jump_margin_start` 24dp）与选择图标（`support_preference_image_padding_start` 16dp）；该状态文字列已由 `BasicComponent` 的 `statusText` 建模（162dp 最大宽度、24dp start margin、右对齐单行）——见 BasicComponent。

**BREAKING：** `checkboxLocation` 默认值由 `Start` 改为 **`End`**：`coui_preference.xml` 把 `@android:id/widget_frame` 放在行尾，官方控件是尾随的；`Start` 仍作为库内扩展保留。尾随复选框另加 1dp 尾部内边距。

**尾部控件边距：**`coui_preference_widget_checkbox.xml` 给尾部控件 `layout_marginLeft` 0dp、
`layout_marginRight` **-10dp**，即向屏幕边缘凸出行内 32dp 内边距。
`CheckboxPreferenceDefaults.WidgetMarginEnd` 复现这个负边距（缩小回报宽度并平移绘制与触区），
`EndPadding` 保留控件自身的 `paddingRight` 1dp。

**更正：** 本 APK 中确实包含多个 COUI 控件布局——`coui_preference_widget_checkbox.xml`、`_switch.xml`（以及 `_switch_compat` / `_switchload`）、`_menu.xml`（跳转箭头）、`_select.xml`（状态文字 + 箭头）、`_stepper_view.xml`、`_mark.xml`、`_button.xml`、`_status_switching.xml`。状态文字列已由 `BasicComponent` 的 `statusText` 建模（`coui_preference_widget_select.xml`：`support_preference_widget_focus_jump_margin_start` 24dp、`coui_preference_status_text_max_width` 162dp）；select 布局的上下 12dp `support_preference_widget_focus_jump_padding_*` 与跳转箭头边距（`support_preference_image_margin_start` 4dp、`support_preference_image_padding_start` 16dp）未由通用 `endActions` 槽应用，其 8dp `EndActionsEndPadding` 是库内取值（没有描述到行边缘间距的 dimen）。


## 基本用法

CheckboxPreference 组件提供了基本的复选框功能：

```kotlin
var isChecked by remember { mutableStateOf(false) }

CheckboxPreference(
    title = "复选框选项",
    checked = isChecked,
    onCheckedChange = { isChecked = it }
)
```

## 带摘要的复选框

```kotlin
var notificationsEnabled by remember { mutableStateOf(false) }

CheckboxPreference(
    title = "通知",
    summary = "接收来自应用的推送通知",
    checked = notificationsEnabled,
    onCheckedChange = { notificationsEnabled = it }
)
```

## 组件状态

### 禁用状态

```kotlin
CheckboxPreference(
    title = "禁用复选框",
    summary = "此复选框当前不可用",
    checked = true,
    onCheckedChange = {},
    enabled = false
)
```

## 复选框位置

CheckboxPreference 支持将复选框放置在起始端或结束端：

### 起始端复选框（默认）

```kotlin
var startChecked by remember { mutableStateOf(false) }

CheckboxPreference(
    title = "起始端复选框",
    summary = "复选框位于起始端（默认）",
    checked = startChecked,
    onCheckedChange = { startChecked = it },
    checkboxLocation = CheckboxLocation.End // 默认值
)
```

### 结束端复选框

```kotlin
var endChecked by remember { mutableStateOf(false) }

CheckboxPreference(
    title = "结束端复选框",
    summary = "复选框位于结束端",
    checked = endChecked,
    onCheckedChange = { endChecked = it },
    checkboxLocation = CheckboxLocation.End
)
```

## 属性

### CheckboxPreference 属性

| 属性名              | 类型                                 | 说明          | 默认值                                   | 是否必须 |
|------------------|------------------------------------|-------------|---------------------------------------|------|
| title            | String                             | 复选框项的标题     | -                                     | 是    |
| checked          | Boolean                            | 复选框的选中状态    | -                                     | 是    |
| onCheckedChange  | ((Boolean) -> Unit)?               | 复选框状态变化时的回调 | -                                     | 否    |
| modifier         | Modifier                           | 应用于组件的修饰符   | Modifier                              | 否    |
| titleColor       | BasicComponentColors               | 标题文本的颜色配置   | BasicComponentDefaults.titleColor()   | 否    |
| summary          | String?                            | 复选框项的摘要说明   | null                                  | 否    |
| summaryColor     | BasicComponentColors               | 摘要文本的颜色配置   | BasicComponentDefaults.summaryColor() | 否    |
| checkboxColors   | CheckboxColors                     | 复选框控件的颜色配置  | CheckboxDefaults.checkboxColors()     | 否    |
| startAction      | @Composable (() -> Unit)?          | 复选框后的自定义内容  | null                                  | 否    |
| endActions       | @Composable (RowScope.() -> Unit)? | 复选框前的自定义内容  | null                                  | 否    |
| checkboxLocation | CheckboxLocation                   | 复选框的位置      | CheckboxLocation.End                  | 否    |
| bottomAction     | @Composable (() -> Unit)?          | 底部自定义内容     | null                                  | 否    |
| insideMargin     | PaddingValues                      | 组件内部内容的边距   | BasicComponentDefaults.InsideMargin   | 否    |
| holdDownState    | Boolean                            | 组件是否处于按下状态  | false                                 | 否    |
| enabled          | Boolean                            | 组件是否可交互     | true                                  | 否    |

## 进阶用法

### 带右侧额外内容

```kotlin
var backupEnabled by remember { mutableStateOf(false) }

CheckboxPreference(
    title = "自动备份",
    summary = "定期备份您的数据",
    checked = backupEnabled,
    onCheckedChange = { backupEnabled = it },
    endActions = {
        Text(
            text = if (backupEnabled) "已启用" else "未启用",
            color = RmuixTheme.colorScheme.onSurfaceVariantActions,
            modifier = Modifier.padding(end = 6.dp)
        )
    }
)
```

### 嵌套复选框

```kotlin
var allSelected by remember { mutableStateOf(false) }
var option1 by remember { mutableStateOf(false) }
var option2 by remember { mutableStateOf(false) }
var option3 by remember { mutableStateOf(false) }

Column {
    CheckboxPreference(
        title = "选择全部",
        checked = allSelected,
        onCheckedChange = { newState ->
            allSelected = newState
            option1 = newState
            option2 = newState
            option3 = newState
        }
    )
    CheckboxPreference(
        title = "选项 1",
        checked = option1,
        onCheckedChange = {
            option1 = it
            allSelected = option1 && option2 && option3
        },
        modifier = Modifier.padding(start = 24.dp)
    )
    CheckboxPreference(
        title = "选项 2",
        checked = option2,
        onCheckedChange = {
            option2 = it
            allSelected = option1 && option2 && option3
        },
        modifier = Modifier.padding(start = 24.dp)
    )
    CheckboxPreference(
        title = "选项 3",
        checked = option3,
        onCheckedChange = {
            option3 = it
            allSelected = option1 && option2 && option3
        },
        modifier = Modifier.padding(start = 24.dp)
    )
}
```

### 自定义颜色

```kotlin
var customChecked by remember { mutableStateOf(false) }

CheckboxPreference(
    title = "自定义颜色",
    titleColor = BasicComponentDefaults.titleColor(
        color = RmuixTheme.colorScheme.primary
    ),
    summary = "使用自定义颜色的复选框",
    summaryColor = BasicComponentDefaults.summaryColor(
        color = RmuixTheme.colorScheme.secondary
    ),
    checked = customChecked,
    onCheckedChange = { customChecked = it },
    checkboxColors = CheckboxDefaults.checkboxColors(
        checkedForegroundColor = Color.Red,
        checkedBackgroundColor = RmuixTheme.colorScheme.secondary
    )
)
```

### 结合对话框使用

```kotlin
var showDialog by remember { mutableStateOf(false) }
var privacyOption by remember { mutableStateOf(false) }
var analyticsOption by remember { mutableStateOf(false) }

Scaffold {
    JumpPreference(
        title = "隐私设置",
        onClick = { showDialog = true },
        holdDownState = showDialog
    )

    OverlayDialog(
        title = "隐私设置",
        show = showDialog,
        onDismissRequest = { showDialog = false } // 关闭对话框
    ) {
        Card {
            CheckboxPreference(
                title = "隐私政策",
                summary = "同意隐私政策条款",
                checked = privacyOption,
                onCheckedChange = { privacyOption = it }
            )

            CheckboxPreference(
                title = "分析数据",
                summary = "允许收集匿名使用数据以改进服务",
                checked = analyticsOption,
                onCheckedChange = { analyticsOption = it }
            )
        }

        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.padding(top = 12.dp)
        ) {
            TextButton(
                text = "取消",
                onClick = { showDialog = false }, // 关闭对话框
                modifier = Modifier.weight(1f)
            )
            Spacer(Modifier.width(16.dp))
            TextButton(
                text = "确认",
                onClick = { showDialog = false }, // 关闭对话框
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.textButtonColorsPrimary() // 使用主题颜色
            )
        }
    }
}
```
