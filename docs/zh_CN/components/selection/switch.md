# Switch

`Switch` 是 Rmuix 中的基础切换组件，用于在两种状态之间进行切换。它提供了具有动画效果的交互式开关控件，适用于设置项的启用与禁用场景。

<div style="position: relative; height: 160px; border-radius: 10px; overflow: hidden; border: 1px solid #777;">
    <iframe id="demoIframe" style="position: absolute; top: 0; left: 0; width: 100%; height: 100%; border: none;" src="../../../compose/index.html?id=switch" title="Demo" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" referrerpolicy="strict-origin-when-cross-origin"></iframe>
</div>

## Stock ColorOS 静态复核（2026-11-12）

本轮读 Settings APK 的 `com.coui.appcompat.couiswitch.COUISwitch`、`COUISwitchStyle` 样式、`coui_switch_padding` 与 `switch_custom_track_*` drawable。**没有需要改动的数值**，下面记录为什么现有几何是对的。

- 几何来自 `COUISwitchStyle`：`bar_width` 44dp、`bar_height` 24dp、`outer_circle_width` 18dp、`circle_padding` 3dp。`coui_switch_padding` 为 0dp，而 `onMeasure` 会加上两倍该 padding，因此控件实测正好 44 × 24dp，与 `SwitchDefaults` 一致。
- 轨道颜色是代码里设置的，不是 drawable 里写的：`mBarCheckedColor` = `?attr/couiColorPrimary`，`mBarUnCheckedColor` = `?attr/couiColorControls`（**#29000000** / **#40FFFFFF**）。drawable 自带的 `couiColorDivider` 会被 `setTint` 覆盖，所以库内未选中轨道用 #29000000 / #40FFFFFF 才是对的。
- 禁用态轨道：`mBarCheckedDisabledColor` = `couiColorPrimary & 0x4DFFFFFF`（30% alpha），`mBarUncheckedDisabledColor` = `?attr/couiColorPressBackground`（**#14000000** / **#26FFFFFF**），与库内一致。
- 主题变体：`setSwitchMinWidth(theme_style_bar_width)`（38dp）只在 `coui_switch_theme_enable` 为 true 时生效；本 APK 里该值为 **false**，所以实际出货的是 44dp 几何。
- 死属性：`inner_circle_width`（7.9dp）与 `outer_circle_stroke_width` 只被读进字段、从未绘制（`mInnerCirclePaint` 创建后从未使用），因此单层 18dp 实心滑块是对的。
- 滑块阴影一致：`setShadowLayer(8, 0, 4, argb(25, 0, 0, 0))`。

**加载态：** `loading = true` 对应 `COUISwitch.setLoading()` —— 滑块 433ms 收缩，官方 `coui_icon_loading` 图形（270° 开口圆、1.4 stroke）在 550ms 内淡入并放大到 18dp 的滑块圆（`LoadingSize`；`drawLoading()` 把 drawable 边界设成 `mOuterCircleRectF`，所以 40×40px @xxhdpi 的 `switch_loading.png` 会从内禀 13.33dp 被拉伸），之后每 800ms 转一圈；运行期间开关不再接受输入（`enabled && !loading`）。**仍待复核：** 按压/悬停遮罩的合成时序未与 `COUIStateEffectDrawable` 逐一核对。

## 引入

```kotlin
import io.github.ra1nify.rmuix.kmp.basic.Switch
```

## 基本用法

Switch 组件可以用于在两种状态间切换：

```kotlin
var checked by remember { mutableStateOf(false) }

Switch(
    checked = checked,
    onCheckedChange = { checked = it }
)
```

## 组件状态

### Stock ColorOS 行为

轨道为 44 × 24 dp，18 dp 滑块在切换时变形成胶囊，而不是将圆形拉伸成椭圆。
轨道颜色按滑块位移进行官方 ARGB 插值，不再独立运行颜色动画。
整条轨道都可拖拽，并支持 RTL 镜像。`onCheckedChange = null` 时为只读，不响应拖拽或触感反馈。
默认中性色跟随当前 `RmuixTheme`，不受系统与应用主题不一致的影响。

BREAKING: `switchColors().uncheckedTrackColor` 默认从 `#1F000000 / #33FFFFFF` 改为官方亮暗色 `#29000000 / #40FFFFFF`；`checkedThumbColor` 从 `onPrimary` 改为白色；`disabledCheckedTrackColor` 从 `disabledPrimary` 改为 `primary & 0x4DFFFFFF`。

拖拽只预览状态：松手后使用 response=0.4、bounce=0.2 的吸附弹簧，等吸附结束且状态确实改变时才调用 `onCheckedChange`；初速度限制为 ±1000 原始 px/s。点击与吸附动画期间不能启动新拖拽；吸附不套用点击拉伸，超过 touch slop 后仍使用按下起的完整位移。
按压/悬停遮罩分别采用 response=0.3、bounce=0 的状态弹簧。材质/加载变体、原生焦点反馈和实机逐帧验收仍未完成，不代表已经完整 1:1。

点击弹簧按官方 SpringData(bounce 0.3, response 0.4) 解析，并把原始 bounce 交给 `COUISpringForce.setBounce`，阻尼 = `1 - bounce` = 0.7（拖拽 1.0、吸附 0.8）；吸附不是 0.9，因为 `SpringData.getDampingRatio()` 的折半公式没有任何 stock 调用方。

### 禁用状态

```kotlin
var checked by remember { mutableStateOf(false) }

Switch(
    checked = checked,
    onCheckedChange = { checked = it },
    enabled = false
)
```


## 属性

### Switch 属性

| 属性名          | 类型                 | 说明                     | 默认值                        | 是否必须 |
| --------------- | -------------------- | ------------------------ | ----------------------------- | -------- |
| checked         | Boolean              | 开关是否处于选中状态     | -                             | 是       |
| onCheckedChange | ((Boolean) -> Unit)? | 开关状态变化时的回调函数 | -                             | 否       |
| modifier        | Modifier             | 应用于开关的修饰符       | Modifier                      | 否       |
| colors          | SwitchColors         | 开关的颜色配置           | SwitchDefaults.switchColors() | 否       |
| enabled         | Boolean              | 开关是否可交互           | true                          | 否       |
| loading         | Boolean              | 在滑块位置显示官方加载圈（加载期间不响应点击） | false           | 否       |

`loading` 对应 `COUISwitch.setLoading()`：`circleScale` 用 433ms 收起滑块，加载圈在 550ms 内淡入并由 0.5 放大到 1，之后每 800ms 转 360 度。图形取自官方 `coui_icon_loading` 矢量（24 视口、1.4 描边的 270 度开口圆），按 `LoadingSize` = 18dp（`drawLoading()` 的 `mOuterCircleRectF` 边界）绘制；加载期间点击无效。

### SwitchDefaults 对象

SwitchDefaults 提供 Switch 的几何常量与默认颜色配置。

#### 属性

| 属性名                | 类型   | 描述                                     | 值     |
| --------------------- | ------ | ---------------------------------------- | ------ |
| Width                 | Dp     | 轨道宽度                                 | 44.dp  |
| Height                | Dp     | 轨道高度                                 | 24.dp  |
| ThumbSize             | Dp     | 滑块直径                                 | 18.dp  |
| ThumbPadding          | Dp     | 滑块与轨道边缘的间距                     | 3.dp   |
| Travel                | Dp     | 滑块行程，`Width - ThumbPadding * 2 - ThumbSize` | 20.dp |
| ThumbOffsetUnchecked  | Dp     | 关闭态滑块 x 偏移                        | 3.dp   |
| ThumbOffsetChecked    | Dp     | 开启态滑块 x 偏移                        | 23.dp  |
| thumbShadow           | Shadow | 滑块阴影，仅在可用状态下绘制             | -      |

#### 方法

| 方法名         | 类型         | 描述                        |
| -------------- | ------------ | --------------------------- |
| switchColors() | SwitchColors | 创建 Switch 的默认颜色配置  |

### SwitchColors 类

| 属性名                      | 类型  | 说明                         | 默认值 | 是否必须 |
| --------------------------- | ----- | ---------------------------- | ------ | -------- |
| checkedThumbColor           | Color | 选中状态时滑块的颜色         | -      | 是       |
| uncheckedThumbColor         | Color | 未选中状态时滑块的颜色       | -      | 是       |
| disabledCheckedThumbColor   | Color | 禁用且选中状态时滑块的颜色   | -      | 是       |
| disabledUncheckedThumbColor | Color | 禁用且未选中状态时滑块的颜色 | -      | 是       |
| checkedTrackColor           | Color | 选中状态时轨道的颜色         | -      | 是       |
| uncheckedTrackColor         | Color | 未选中状态时轨道的颜色       | -      | 是       |
| disabledCheckedTrackColor   | Color | 禁用且选中状态时轨道的颜色   | -      | 是       |
| disabledUncheckedTrackColor | Color | 禁用且未选中状态时轨道的颜色 | -      | 是       |

## 进阶用法

### 自定义颜色

```kotlin
var checked by remember { mutableStateOf(false) }

Switch(
    checked = checked,
    onCheckedChange = { checked = it },
    colors = SwitchDefaults.switchColors(
        checkedTrackColor = Color.Red,
        checkedThumbColor = Color.White
    )
)
```

### 结合文本使用

```kotlin
var checked by remember { mutableStateOf(false) }

Row(
    verticalAlignment = Alignment.CenterVertically,
    modifier = Modifier.padding(16.dp)
) {
    Switch(
        checked = checked,
        onCheckedChange = { checked = it }
    )
    Spacer(modifier = Modifier.width(8.dp))
    Text(text = if (checked) "已开启" else "已关闭")
}
```

### 在列表中使用

```kotlin
val options = listOf("飞行模式", "蓝牙", "位置服务")
val checkedStates = remember { mutableStateListOf(false, true, false) }

LazyColumn {
    items(options.size) { index ->
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = options[index])
            Switch(
                checked = checkedStates[index],
                onCheckedChange = { checkedStates[index] = it }
            )
        }
    }
}
```

### 整行列表可点击

```kotlin
data class Option(val text: String, var isSelected: Boolean)
val options = remember {
    mutableStateListOf(
        Option("飞行模式", false),
        Option("蓝牙", true),
        Option("位置服务", false)
    )
}

LazyColumn {
    items(options.size) { index ->
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    options[index] = options[index].copy(
                        isSelected = !options[index].isSelected
                    )
                }
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = options[index].text)
            Switch(
                checked = options[index].isSelected,
                onCheckedChange = { 
                    options[index] = options[index].copy(
                        isSelected = !options[index].isSelected
                    )
                }
            )
        }
    }
}
````
