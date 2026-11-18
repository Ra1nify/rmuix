# TextField

`TextField` 是 Rmuix 中的基础输入组件，用于接收用户的文本输入。组件提供了丰富的自定义选项，支持标签动画、前置和后置图标等功能。

<div style="position: relative; height: 340px; border-radius: 10px; overflow: hidden; border: 1px solid #777;">
    <iframe id="demoIframe" style="position: absolute; top: 0; left: 0; width: 100%; height: 100%; border: none;" src="../../../compose/index.html?id=textField" title="Demo" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" referrerpolicy="strict-origin-when-cross-origin"></iframe>
</div>

## Stock ColorOS 静态复核（2026-11-12）

本轮只覆盖 Settings APK `Widget.COUI.EditText` 族的 rectangle 背景模式（`COUIEditText` / `COUICutoutDrawable`）及其尺寸、颜色与样式资源，不是完整 1:1 验收。

- 圆角 16dp → 10dp（`coui_textinput_corner_radius`）；最小高度 62dp（`coui_edit_text_height_min`）。
- 常态描边：`coui_textinput_stroke_width` 细线（0.33dp），颜色 `couiColorDivider`（`#1F000000` / `#33FFFFFF`）；禁用使用 `coui_textinput_stroke_color_disabled`（`#19000000` / `#19FFFFFF`）。 1x 密度（桌面 / 网页）下细线会被钳到至少 1 个物理像素（`hairlinePx`），不会消失。
- 聚焦描边：`Widget.COUI.EditText` 同时绑定 `couiFocusStrokeWidth` → `coui_textinput_focus_stroke_width` = **1dp**，并且 `COUIEditText` 会读取它（`mStrokeWidthFocused`），所以聚焦时既换成 `couiColorPrimary`、**也**加粗到 1dp。早先有一轮写成「矩形框聚焦仍是 0.33dp」，是错的；原先 0→2dp 的动画仍然不存在，官方是 0.33 → 1dp 的跳变。
- 默认文本 16sp（`couiEditTextTextAppearance` = headline6 + regular 字体族）；浮动标签使用 `coui_textinput_hint_text_size`（10sp），未浮动标签 16sp，均按 sp 应用系统字体缩放。
- 标签颜色默认为 `couiColorLabelSecondary`，与 `couiColorSecondNeutral` 相同（`#8A000000` / `#8AFFFFFF`）。
- 标签上浮到顶部边框上时，会向两侧各擦除 4dp 边框（`coui_textinput_label_cutout_padding`，`TextFieldDefaults.LabelCutoutPadding`），对应官方的 cutout，而不是让描边从文字中穿过。
- `TextFieldColors` 新增可选 `restingBorderColor` 与 `disabledBorderColor`；`Color.Unspecified` 回退到官方中性色，既有三参数调用保持可编译。

**BREAKING：** `CornerRadius` 16→10dp，常态描边为 0.33dp 细线，聚焦描边改为官方 `couiFocusStrokeWidth` 的 1dp（不再是 0→2dp 的动画），默认标签色与文本字号改变，控件最小高度为 62dp。

line 模式（`lineMode`，`couiBackgroundMode=line`：0.33dp 底部细线，聚焦时 250ms 从左向右展开）与错误态（`errorState`：`coui_edit_text_shake_amplitude` 5.33dp 抖动 + 217ms 颜色渐变）已实现。方框模式已对齐官方 `coui_edittext_default_background` 九宫格：**透明填充**（selector 只画 0.33dp `couiColorDivider` 细线，聚焦态 1dp `couiColorPrimary`），并在浮动标签处做 stroke 挖空。因此 `backgroundColor` 默认改为 `Color.Transparent`；只有需要实心底色的页面才传入颜色。快速删除图标已作为可选参数 `showClearButton`（默认 `false`，现有调用无变化）；`COUIEditText` 本身也是用属性开关控制删除图标，所以这里同样做成可选。 图标 `ic_edit_text_delete_search_view.xml` 的圆盘用 `?attr/couiColorHintNeutral`、叉固定 `#ffffff`，且没有 `drawable-night` 变体，因此深色下仍是白叉。

## 引入

```kotlin
import io.github.ra1nify.rmuix.kmp.basic.TextField
```

## 基本用法

TextField 组件可以用于获取用户输入：

```kotlin
var text by remember { mutableStateOf("") }

TextField(
    value = text,
    onValueChange = { text = it },
    label = "用户名"
)
```

::: info 信息
此 TextFiled 组件现在也支持最新基于状态的版本，具体请参考 [State-based](https://developer.android.com/develop/ui/compose/text/user-input?textfield=state-based&hl=zh-cn) 文档。
:::

## 输入框类型

### 带标签输入框

标签会在输入框获得焦点或有内容时自动移动到顶部：

```kotlin
var text by remember { mutableStateOf("") }

TextField(
    value = text,
    onValueChange = { text = it },
    label = "邮箱地址"
)
```

### 使用标签作为占位符

```kotlin
var text by remember { mutableStateOf("") }

TextField(
    value = text,
    onValueChange = { text = it },
    label = "请输入内容",
    useLabelAsPlaceholder = true
)
```

## 组件状态

### 禁用状态

```kotlin
var text by remember { mutableStateOf("") }
TextField(
    value = text,
    onValueChange = { text = it },
    label = "禁用输入框",
    enabled = false
)
```

### 只读状态

```kotlin
var text by remember { mutableStateOf("这是只读内容") }

TextField(
    value = text,
    onValueChange = { text = it },
    label = "只读输入框",
    readOnly = true
)
```

## 属性

### TextField 属性

| 属性名                | 类型                                         | 说明                   | 默认值                                               | 是否必须 |
| --------------------- | -------------------------------------------- | ---------------------- | ---------------------------------------------------- | -------- |
| value                 | String 或 TextFieldValue                     | 输入框的文本值         | -                                                    | 是       |
| onValueChange         | (String) -> Unit 或 (TextFieldValue) -> Unit | 文本变化时的回调函数   | -                                                    | 是       |
| modifier              | Modifier                                     | 应用于输入框的修饰符   | Modifier                                             | 否       |
| insideMargin          | DpSize                                       | 输入框内部边距         | TextFieldDefaults.InsideMargin                        | 否       |
| colors                | TextFieldColors                              | 输入框使用的颜色       | TextFieldDefaults.textFieldColors()                  | 否       |
| cornerRadius          | Dp                                           | 输入框圆角半径         | TextFieldDefaults.CornerRadius                        | 否       |
| label                 | String                                       | 输入框标签文本         | ""                                                   | 否       |
| useLabelAsPlaceholder | Boolean                                      | 是否使用标签作为占位符 | false                                                | 否       |
| enabled               | Boolean                                      | 输入框是否可用         | true                                                 | 否       |
| errorState        | Boolean                   | 错误态：描边在 217ms 内渐变到 `couiColorErrorTextBg`（`#4DE82A18`/`#EA3939`），内容横向抖动 `coui_edit_text_shake_amplitude` 5.33dp、450ms | false |
| lineMode          | Boolean                   | 仅底部描边（`couiBackgroundMode = 1`）：静止 0.33dp 通栏细线，聚焦时 1dp 细线 250ms 从左向右生长 | false |

> line 模式的三支内边距：`coui_textinput_line_padding_top` 10dp 与 `coui_textinput_line_padding_middle` 5dp 已作为 `TextFieldDefaults.LinePaddingTop` / `LinePaddingMiddle` 暴露（官方 `getModePaddingTop()` = top + 提示高 + middle）；第三支 `coui_textinput_line_padding` 11dp **被赋值给 `mLinePadding` 后从未读取**，属死资源，仅记录不使用。
| readOnly              | Boolean                                      | 输入框是否只读         | false                                                | 否       |
| textStyle             | TextStyle                                    | 输入文本样式           | TextFieldDefaults.textStyle()                          | 否       |
| keyboardOptions       | KeyboardOptions                              | 键盘选项配置           | KeyboardOptions.Default                              | 否       |
| keyboardActions       | KeyboardActions                              | 键盘操作配置           | KeyboardActions.Default                              | 否       |
| leadingIcon           | @Composable (() -> Unit)?                    | 前置图标               | null                                                 | 否       |
| trailingIcon          | @Composable (() -> Unit)?                    | 后置图标               | null                                                 | 否       |
| singleLine            | Boolean                                      | 是否为单行输入         | false                                                | 否       |
| maxLines              | Int                                          | 最大行数               | 如果 singleLine 为 true 则为 1，否则为 Int.MAX_VALUE | 否       |
| minLines              | Int                                          | 最小行数               | 1                                                    | 否       |
| visualTransformation  | VisualTransformation                         | 视觉转换器             | VisualTransformation.None                            | 否       |
| onTextLayout          | (TextLayoutResult) -> Unit                   | 文本布局变化回调       | {}                                                   | 否       |
| interactionSource     | MutableInteractionSource?                    | 交互源                 | null                                                 | 否       |
| cursorBrush           | Brush                                        | 光标画刷               | SolidColor(colors.borderColor)                       | 否       |
| showClearButton       | Boolean                                      | 有内容时显示原厂 `ic_edit_text_delete_search_view` 清除按钮，点击清空输入框 | false |

### TextField（state-based）属性

| 属性名                | 类型                                                | 说明                           | 默认值                                        | 是否必须 |
| --------------------- | --------------------------------------------------- | ------------------------------ | --------------------------------------------- | -------- |
| state                 | TextFieldState                                      | 保存文本与选择的状态对象       | -                                             | 是       |
| modifier              | Modifier                                            | 应用于输入框的修饰符           | Modifier                                      | 否       |
| insideMargin          | DpSize                                              | 输入框内部边距                 | TextFieldDefaults.InsideMargin                | 否       |
| colors                | TextFieldColors                                     | 输入框使用的颜色               | TextFieldDefaults.textFieldColors()           | 否       |
| cornerRadius          | Dp                                                  | 输入框圆角半径                 | TextFieldDefaults.CornerRadius                | 否       |
| label                 | String                                              | 输入框标签文本                 | ""                                            | 否       |
| useLabelAsPlaceholder | Boolean                                             | 是否使用标签作为占位符         | false                                         | 否       |
| enabled               | Boolean                                             | 输入框是否可用                 | true                                          | 否       |
| readOnly              | Boolean                                             | 输入框是否只读                 | false                                         | 否       |
| inputTransformation   | InputTransformation?                                | 输入变换器                     | null                                          | 否       |
| textStyle             | TextStyle                                           | 输入文本样式                   | TextFieldDefaults.textStyle()                  | 否       |
| keyboardOptions       | KeyboardOptions                                     | 键盘选项配置                   | KeyboardOptions.Default                       | 否       |
| onKeyboardAction      | KeyboardActionHandler?                              | 键盘动作处理器                 | null                                          | 否       |
| lineLimits            | TextFieldLineLimits                                 | 行数限制                       | TextFieldLineLimits.Default                   | 否       |
| leadingIcon           | @Composable (() -> Unit)?                           | 前置图标                       | null                                          | 否       |
| trailingIcon          | @Composable (() -> Unit)?                           | 后置图标                       | null                                          | 否       |
| onTextLayout          | Density.(getResult: () -> TextLayoutResult?) -> Unit | 文本布局回调（带 Density 接收） | null                                          | 否       |
| interactionSource     | MutableInteractionSource?                           | 交互源                         | null                                          | 否       |
| cursorBrush           | Brush                                               | 光标画刷                       | SolidColor(colors.borderColor)                | 否       |
| showClearButton       | Boolean                                      | 有内容时显示原厂 `ic_edit_text_delete_search_view` 清除按钮，点击清空输入框 | false |
| outputTransformation  | OutputTransformation?                               | 输出变换器                     | null                                          | 否       |
| scrollState           | ScrollState                                         | 滚动状态                       | rememberScrollState()                         | 否       |

### TextFieldDefaults 对象

TextFieldDefaults 对象提供了 TextField 组件的默认值。

#### 常量

| 常量名       | 类型   | 说明           | 默认值                |
| ------------ | ------ | -------------- | --------------------- |
| CornerRadius | Dp     | 输入框圆角半径 | 10.dp                 |
| InsideMargin | DpSize | 输入框内部边距 | DpSize(16.dp, 16.dp) |
| MinHeight    | Dp     | 输入框最小高度 | 62.dp                 |
| StrokeWidth  | Dp     | 常态描边宽度（`coui_textinput_stroke_width`） | 0.33.dp |
| FocusStrokeWidth | Dp | 聚焦描边宽度（`coui_textinput_focus_stroke_width`） | 1.dp |
| LabelCutoutPadding | Dp | 上浮标签周围擦除的边框宽度（`coui_textinput_label_cutout_padding`） | 4.dp |

#### `textFieldColors()` 工厂

构造 [TextFieldColors] 实例。按需覆盖任意子集，未指定的参数回退到官方 COUI 中性色。

| 参数             | 类型  | 默认值                                            |
| ---------------- | ----- | ------------------------------------------------- |
| backgroundColor  | Color | Color.Transparent（官方只有描边）                 |
| labelColor       | Color | 官方 `couiColorLabelSecondary` 中性色             |
| borderColor      | Color | RmuixTheme.colorScheme.primary                    |
| restingBorderColor | Color | 官方 `couiColorDivider` 细描边                  |
| disabledBorderColor | Color | 官方禁用描边                                  |

## 进阶用法

### 带图标输入框

```kotlin
var text by remember { mutableStateOf("") }

TextField(
    value = text,
    onValueChange = { text = it },
    label = "搜索",
    leadingIcon = {
        Icon(
            imageVector = RmuixIcons.Search,
            contentDescription = "搜索图标",
            modifier = Modifier.padding(horizontal = 12.dp)
        )
    }
)
```

### 密码输入框

```kotlin
var password by remember { mutableStateOf("") }
var passwordVisible by remember { mutableStateOf(false) }

TextField(
    value = password,
    onValueChange = { password = it },
    label = "密码",
    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
    trailingIcon = {
        IconButton(
            onClick = { passwordVisible = !passwordVisible },
            modifier = Modifier.padding(end = 12.dp)
        ) {
            Icon(
                imageVector = RmuixIcons.Rename,
                tint = if (passwordVisible) RmuixTheme.colorScheme.primary else RmuixTheme.colorScheme.onSecondaryContainer,
                contentDescription = if (passwordVisible) "隐藏密码" else "显示密码"
            )
        }
    }
)
```

### 带验证的输入框

```kotlin
var email by remember { mutableStateOf("") }
var isError by remember { mutableStateOf(false) }
val errorColor = Color.Red.copy(0.3f)
val emailPattern = remember { Regex("[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+") }

Column {
    TextField(
        value = email,
        onValueChange = {
            email = it
            isError = email.isNotEmpty() && !emailPattern.matches(email)
        },
        label = "电子邮箱",
        colors = TextFieldDefaults.textFieldColors(
            labelColor = if (isError) errorColor else RmuixTheme.colorScheme.onSurfaceVariantSummary,
        ),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
    )
    if (isError) {
        Text(
            text = "请输入有效的邮箱地址",
            color = errorColor,
            style = RmuixTheme.textStyles.body2,
            modifier = Modifier.padding(start = 16.dp, top = 4.dp)
        )
    }
}
```

### 自定义样式

```kotlin
var text by remember { mutableStateOf("") }

TextField(
    value = text,
    onValueChange = { text = it },
    label = "自定义输入框",
    cornerRadius = 8.dp,
    colors = TextFieldDefaults.textFieldColors(
        backgroundColor = RmuixTheme.colorScheme.primary.copy(alpha = 0.1f),
    ),
    textStyle = TextStyle(
        fontWeight = FontWeight.Medium,
        fontSize = 16.sp,
        color = RmuixTheme.colorScheme.primary
    )
)
```

### 使用 TextFieldValue

当需要更细致地控制文本选择和光标位置时：

```kotlin
var textFieldValue by remember { mutableStateOf(TextFieldValue("")) }

TextField(
    value = textFieldValue,
    onValueChange = { textFieldValue = it },
    label = "高级输入控制",
    // TextFieldValue 提供了对文本、选择范围和光标位置的控制
)
```
