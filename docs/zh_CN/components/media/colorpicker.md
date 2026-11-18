# ColorPicker

饱和度/明度面板 + 色相与透明度轨道 + 可选的十六进制读数 + 预设色块行，另提供一个偏好行和一层浮层对话框。

::: warning 没有 ColorOS 对应物
三棵 ColorOS 反编译树（`settings`、`gallery3d`、`systemui`）里都**没有**取色器类，因此本组件**没有官方度量**。
尺寸由本库的 ColorOS 令牌（`ColorPickerDefaults`）推导，可能变化，这也是它被放在 `rmuix-extend` 的原因。
:::

## 引入

```kotlin
import io.github.ra1nify.rmuix.kmp.extend.colorpicker.ColorPicker
import io.github.ra1nify.rmuix.kmp.extend.colorpicker.OverlayColorPickerDialog
import io.github.ra1nify.rmuix.kmp.extend.preference.ColorPickerPreference
```

## 基本用法

```kotlin
// 一行色块，点开对话框取色；只有确认才回调
var accent by remember { mutableStateOf(Color(0xFF0080FF)) }

ColorPickerPreference(
    color = accent,
    onColorChange = { accent = it },
    title = "强调色",
    summary = "用于高亮",
)
```

不套对话框时直接用：

```kotlin
ColorPicker(
    color = accent,
    onColorChange = { accent = it },
    showAlphaSlider = false,
)
```

## 参数

### ColorPicker

| 参数名          | 类型                    | 说明                                    | 默认值 |
| :-------------- | :---------------------- | :-------------------------------------- | :----- |
| color           | Color                   | 当前颜色                                | - |
| onColorChange   | (Color) -> Unit         | 拖动时回调新颜色                        | - |
| modifier        | Modifier                | 应用到取色器                            | Modifier |
| showAlphaSlider | Boolean                 | 是否显示透明度轨道                      | true |
| showHexText     | Boolean                 | 是否显示 `#RRGGBB` / `#AARRGGBB` 读数   | true |
| presetColors    | List<Color>             | 预设色块；传空列表即隐藏                | ColorPickerDefaults.presetColors() |
| colors          | ColorPickerColors       | 滑块、轨道、读数颜色                    | ColorPickerDefaults.colorPickerColors() |

### OverlayColorPickerDialog

| 参数名          | 类型            | 说明                            | 默认值 |
| :-------------- | :-------------- | :------------------------------ | :----- |
| show            | Boolean         | 是否显示                        | - |
| color           | Color           | 打开时的颜色                    | - |
| onColorChange   | (Color) -> Unit | 实时草稿颜色                    | - |
| onConfirm       | (Color) -> Unit | 点确认时的草稿颜色              | - |
| onDismissRequest| () -> Unit      | 点遮罩、返回或取消              | - |
| modifier        | Modifier        | 应用到对话框                    | Modifier |
| title           | String?         | 标题                            | null |
| showAlphaSlider | Boolean         | 是否显示透明度轨道              | true |
| confirmLabel    | String          | 确认按钮文字                    | "OK" |
| cancelLabel     | String          | 取消按钮文字                    | "Cancel" |
| colors          | ColorPickerColors | 取色器颜色                    | ColorPickerDefaults.colorPickerColors() |

### ColorPickerPreference

| 参数名          | 类型                    | 说明                        | 默认值 |
| :-------------- | :---------------------- | :-------------------------- | :----- |
| color           | Color                   | 当前颜色                    | - |
| onColorChange   | (Color) -> Unit         | 确认后回调所选颜色          | - |
| title           | String                  | 行标题                      | - |
| modifier        | Modifier                | 应用到行                    | Modifier |
| summary         | String?                 | 行摘要                      | null |
| enabled         | Boolean                 | 是否可交互                  | true |
| showAlphaSlider | Boolean                 | 对话框是否显示透明度轨道    | true |
| dialogTitle     | String                  | 对话框标题                  | title |
| previewShape    | Shape                   | 色块形状                    | CircleShape |
| previewSize     | Dp                      | 色块尺寸                    | 28dp |
| confirmLabel    | String                  | 确认按钮文字                | "OK" |
| cancelLabel     | String                  | 取消按钮文字                | "Cancel" |

## ColorPickerDefaults

| 名称              | 类型     | 值 |
| :---------------- | :------- | :---- |
| AREA_ASPECT_RATIO | Float    | 1.6 |
| AreaCornerRadius  | Dp       | 12dp（`?attr/couiRoundCornerM`） |
| TrackHeight       | Dp       | 12dp |
| ThumbSize         | Dp       | 20dp |
| PresetSize        | Dp       | 28dp |
| RowSpacing        | Dp       | 12dp |
| HexTextSize       | TextUnit | 14sp（`couiTextAppearanceArticleBody` 是 13sp，读数取 14sp） |

## 行为说明

- 对话框打开期间，行内保留**草稿**颜色：对话框的 `onColorChange` 实时触发，但偏好的 `onColorChange` 只在确认时触发，
  因此取消不会改动已保存的颜色。
- 色相保存在组件状态里：把饱和度拖到 0 不会让色相跳回红色。
