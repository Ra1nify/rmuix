# SwitchPreference

`SwitchPreference` 是 Rmuix 中的开关组件，提供了标题、摘要和右侧开关控件，支持点击交互，常用于设置项和偏好切换中。

<div style="position: relative; height: 231px; border-radius: 10px; overflow: hidden; border: 1px solid #777;">
    <iframe id="demoIframe" style="position: absolute; top: 0; left: 0; width: 100%; height: 100%; border: none;" src="../../../compose/index.html?id=switchPreference" title="Demo" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" referrerpolicy="strict-origin-when-cross-origin"></iframe>
</div>

## Stock ColorOS 静态复核（2026-11-12）

本轮读 Settings APK 的 `res/layout/coui_preference_widget_switch.xml` 与 `android_preference_switch_margin_left`。

- 官方偏好行用该布局装配尾部控件：switch 上带 `android:layout_marginStart="@dimen/android_preference_switch_margin_left"` = **16dp**，也就是文字/尾部内容与开关之间的间距。
- `SwitchPreference` 原本在尾部 Row 上写死 `padding(end = 8.dp)`，只有官方间距的一半；现改为 `SwitchPreferenceDefaults.SwitchMarginStart`（16dp）。
- 行级度量（48dp 最小高、32dp 水平内缩、16sp 标题、13sp 摘要）来自基础偏好布局，此前已对齐。

**BREAKING：** 尾部内容与开关之间的间距 8dp → 16dp（新增 `SwitchPreferenceDefaults`）。

**更正（2026-11-17）：** 复选框控件边距已对齐——`CheckboxPreference` 用自定义 layout 应用 `coui_preference_checkbox_margin_right`（-10dp），并加自身 1dp `paddingRight`，已由渲染测试验证（见 `checkboxpreference.md`）。同级行在调用方传入 `endActions` 时仍会在尾部控件前使用 `PreferenceRowDefaults.EndActionsEndPadding`（8dp，库内取值：没有描述到行边缘间距的 dimen）；radio / slider / dropdown / spinner 在本 APK 中没有专属 COUI 控件布局（radio 用 `preference_widget_radiobutton.xml`，slider / dropdown 用 SettingsLib 或 M3 布局）。
## 引入

```kotlin
import io.github.ra1nify.rmuix.kmp.preference.SwitchPreference
```

## 基本用法

SwitchPreference 组件提供了基本的开关功能：

```kotlin
var isChecked by remember { mutableStateOf(false) }

SwitchPreference(
    title = "开关选项",
    checked = isChecked,
    onCheckedChange = { isChecked = it }
)
```

## 带摘要的开关

```kotlin
var wifiEnabled by remember { mutableStateOf(false) }

SwitchPreference(
    title = "WiFi",
    summary = "打开以连接到无线网络",
    checked = wifiEnabled,
    onCheckedChange = { wifiEnabled = it }
)
```

## 组件状态

### 禁用状态

```kotlin
SwitchPreference(
    title = "禁用开关",
    summary = "此开关当前不可用",
    checked = true,
    onCheckedChange = {},
    enabled = false
)
```

## 属性

### SwitchPreference 属性

| 属性名          | 类型                            | 说明                          | 默认值                                | 是否必须 |
| --------------- | ------------------------------- | ----------------------------- | ------------------------------------- | -------- |
| checked         | Boolean                         | 开关的选中状态                | -                                     | 是       |
| onCheckedChange | Yes                             | 开关状态变化时的回调          | -                                     | 是       |
| title           | String                          | 开关项的标题                  | -                                     | 是       |
| modifier        | Modifier                        | 应用于组件的修饰符            | Modifier                              | 否       |
| titleColor      | BasicComponentColors            | 标题文本的颜色配置            | BasicComponentDefaults.titleColor()   | 否       |
| summary         | String?                         | 开关项的摘要说明              | null                                  | 否       |
| shortcutHint    | String?                         | 开关开启时替代 `summary` 显示的快捷提示（roui 快捷开关行） | null | No |
| summaryColor    | BasicComponentColors            | 摘要文本的颜色配置            | BasicComponentDefaults.summaryColor() | 否       |
| startAction     | @Composable (() -> Unit)?       | 左侧显示的自定义内容          | null                                  | 否       |
| endActions      | @Composable RowScope.() -> Unit | 右侧显示的自定义内容（开关前）| {}                                    | 否       |
| bottomAction    | @Composable (() -> Unit)?       | 底部显示的自定义内容          | null                                  | 否       |
| switchColors    | SwitchColors                    | 开关控件的颜色配置            | SwitchDefaults.switchColors()         | 否       |
| insideMargin    | PaddingValues                   | 组件内部内容的边距            | BasicComponentDefaults.InsideMargin   | 否       |
| holdDownState   | Boolean                         | 组件是否处于按下状态          | false                                 | 否       |
| enabled         | Boolean                         | 组件是否可交互                | true                                  | 否       |

## 进阶用法

### 带左侧图标

```kotlin
var enabled by remember { mutableStateOf(false) }

SwitchPreference(
    title = "测试",
    summary = "启用以允许测试",
    checked = enabled,
    onCheckedChange = { enabled = it },
    startAction = {
        Icon(
            imageVector = RmuixIcons.Sort,
            contentDescription = "命令图标",
            tint = RmuixTheme.colorScheme.onBackground,
            modifier = Modifier.padding(end = 12.dp)
        )
    }
)
```

### 带右侧操作

```kotlin
var locationEnabled by remember { mutableStateOf(false) }

SwitchPreference(
    title = "位置服务",
    summary = "允许应用获取位置信息",
    checked = locationEnabled,
    onCheckedChange = { locationEnabled = it },
    endActions = {
        Text(
            text = if (locationEnabled) "已开启" else "已关闭",
            color = RmuixTheme.colorScheme.onSurfaceVariantActions,
            modifier = Modifier.padding(end = 6.dp)
        )
    }
)
```

### 带动画显示/隐藏的内容

```kotlin
var parentEnabled by remember { mutableStateOf(false) }
var childEnabled by remember { mutableStateOf(false) }
Column {
    SwitchPreference(
        title = "主设置",
        summary = "打开以显示更多选项",
        checked = parentEnabled,
        onCheckedChange = { parentEnabled = it }
    )
    AnimatedVisibility(
        visible = parentEnabled
    ) {
        SwitchPreference(
            title = "子设置",
            summary = "仅当主设置开启时可用",
            checked = childEnabled,
            onCheckedChange = { childEnabled = it }
        )
    }
}
```

### 自定义颜色

```kotlin
var customEnabled by remember { mutableStateOf(false) }

SwitchPreference(
    title = "自定义颜色",
    titleColor = BasicComponentDefaults.titleColor(
        color = RmuixTheme.colorScheme.primary
    ),
    summary = "使用自定义颜色的开关",
    summaryColor = BasicComponentDefaults.summaryColor(
        color = RmuixTheme.colorScheme.secondary
    ),
    checked = customEnabled,
    onCheckedChange = { customEnabled = it },
    switchColors = SwitchDefaults.switchColors(
        checkedThumbColor = RmuixTheme.colorScheme.primary,
        checkedTrackColor = RmuixTheme.colorScheme.primary.copy(alpha = 0.2f)
    )
)
```

### 结合对话框使用

```kotlin
var showDialog by remember { mutableStateOf(false) }
var option by remember { mutableStateOf(false) }

Scaffold {
    JumpPreference(
        title = "高级设置",
        onClick = { showDialog = true },
        holdDownState = showDialog
    )
    OverlayDialog(
        title = "高级设置",
        show = showDialog,
        onDismissRequest = { showDialog = false }
    ) {
        Card {
            SwitchPreference(
                title = "实验性功能",
                summary = "启用尚在开发中的功能",
                checked = option,
                onCheckedChange = { option = it }
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
