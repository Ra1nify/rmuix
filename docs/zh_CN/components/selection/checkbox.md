# Checkbox

## 共享原厂聚光内核

聚光入口现统一复用[共享 Spotlight](../media/spotlight)：使用官方光照 shader、预设和弹簧，移除旧幂次光斑/径向回退，并在内容之后混合。此项是公共引擎替换，不表示本控件的系统材质门控和预设选择已全部验收。Button/Checkbox 的可选聚光仍默认关闭。

BREAKING：聚光默认 tint 改为白色，移除组件内部随意指定的 40/140/380dp 半径；自定义颜色与半径仍可通过公共入口显式覆盖。


`Checkbox` 是 Rmuix 中的基础选择组件，支持三种状态：选中、未选中和半选中。它提供了具有动画效果的交互式选择控件，适用于多选场景和配置项的启用与禁用。

<div style="position: relative; height: 180px; border-radius: 10px; overflow: hidden; border: 1px solid #777;">
    <iframe id="demoIframe" style="position: absolute; top: 0; left: 0; width: 100%; height: 100%; border: none;" src="../../../compose/index.html?id=checkbox" title="Demo" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" referrerpolicy="strict-origin-when-cross-origin"></iframe>
</div>

## 引入

```kotlin
import io.github.ra1nify.rmuix.kmp.basic.Checkbox
import androidx.compose.ui.state.ToggleableState
```

## 基本用法

Checkbox 组件可以用于创建可选择的选项：

```kotlin
var state by remember { mutableStateOf(ToggleableState.Off) }

Checkbox(
    state = state,
    onClick = { state = if (state == ToggleableState.On) ToggleableState.Off else ToggleableState.On }
)
```

## 组件状态

### Stock ColorOS 图形

24 dp 视口内，选中方块为 18 dp、4 dp 普通圆角。未选中态使用独立的 16.4 dp 描边中心线和 1.3 dp 线宽，不是把选中方块直接放大描边。
初始/静态条目的勾线使用官方填充路径；其半选方块向下偏移 0.666 dp，内部短横是独立图形，不再由勾线压平近似。转换后保留动画矢量的末帧，不会自动回跳到初始/静态图形。
动画保留填充、描边、勾线裁剪和短横路径裁剪的独立通道。通常为 300 ms；半选转全选因一层 X 缩放延迟回落，完整时长为 351 ms。禁用的全选与半选之间也按对应弱化色播放转换。

BREAKING: `BoxSize` 从 20 dp 改为 18 dp，`CornerRadius` 从 7 dp 改为 4 dp。`enableSpotlight` 默认改为 `false`：官方 Checkbox 没有自定义 shader 聚光与 0.92 倍按压压缩；显式传 `true` 仍保留为非 stock 扩展。`checkedForegroundColor` 默认白色；禁用填充改为主色乘 30% alpha，禁用勾线改为 `#8AFFFFFF / #29FFFFFF`。

中断后按官方不可逆 selector 从上一个目标状态、时间零点重启，而非反向续播当前进度。启用状态变化时切换到静态条目；禁用态只有明确定义的全选/半选转换可以播放。
### 交互反馈

可交互的 Checkbox 恢复官方半径 20 dp 的圆形状态层，可超出 24 dp 图形边界；不会缩放控件。悬停与焦点独立叠加，分别采用 response=0.3、bounce=0 的弹簧和 Android `ArgbEvaluator` 线性 ARGB 插值。焦点色跟随库主题 primary，亮/暗 alpha 分别为 `#1F` / `#33`。
Android 按压直接委托系统 `RippleDrawable`，传递触点及其移动，不用普通 Compose indication 或自定义聚光近似。禁用和只读不响应状态层；`enableSpotlight` 只控制额外的非 stock shader 扩展，不再承担原生反馈的开关含义。网页与示例 App 增加了可请求键盘焦点的对照。
非 Android 目前采用明确的软件 ripple 回退，其时序参考本机 Android SDK 的 solid ripple，不视为 ColorOS 材质/噪声 shader 的 1:1。旧 Android 资源覆盖差异、非 Android 材质及实机逐帧验收仍待继续核对。Android 原生路径也依赖宿主系统 framework，跨厂商材质差异仍须与 ColorOS 实机对照。

### 禁用状态

```kotlin
Checkbox(
    state = ToggleableState.Off,
    onClick = null,
    enabled = false
)
```

### 半选中状态

半选中状态通常用于父复选框中，表示子复选框仅部分被选中。处于半选中时，显示一条横线而非对勾。

```kotlin
var parentState by remember { mutableStateOf(ToggleableState.Indeterminate) }

// 循环切换：Off → Indeterminate → On → Off
Checkbox(
    state = parentState,
    onClick = {
        parentState = when (parentState) {
            ToggleableState.Off -> ToggleableState.Indeterminate
            ToggleableState.Indeterminate -> ToggleableState.On
            ToggleableState.On -> ToggleableState.Off
        }
    }
)
```

## 属性

### Checkbox 属性

| 属性名    | 类型              | 说明                                              | 默认值                            | 是否必须 |
| --------- | ----------------- | ------------------------------------------------- | --------------------------------- | -------- |
| state     | ToggleableState   | 复选框当前状态（On、Off 或 Indeterminate）        | -                                 | 是       |
| onClick   | (() -> Unit)?     | 点击时的回调；为 `null` 时组件不可交互            | -                                 | 是       |
| modifier  | Modifier          | 应用于复选框的修饰符                              | Modifier                          | 否       |
| colors    | CheckboxColors    | 复选框的颜色配置                                  | CheckboxDefaults.checkboxColors() | 否       |
| enabled   | Boolean           | 复选框是否可交互                                  | true                              | 否       |

### CheckboxDefaults 对象

两种重载都保留可选的 `enableSpotlight: Boolean = false` 与 `spotlightColor: Color`。聚光是非 stock 扩展，只在显式启用且具有点击回调时绘制。

CheckboxDefaults 对象提供了 Checkbox 组件的几何常量与默认颜色配置。

#### 属性

| 属性名        | 类型 | 描述                             | 值     |
| ------------- | ---- | -------------------------------- | ------ |
| Size          | Dp   | 含透明留白的整体尺寸             | 24.dp  |
| BoxSize       | Dp   | 去除留白后的绘制尺寸             | 18.dp  |
| CornerRadius  | Dp   | 绘制方块的圆角                   | 4.dp   |
| StrokeWidth   | Dp   | 未选中状态的描边宽度             | 1.3.dp |
| StateLayerRadius | Dp | 悬停/焦点/按压的圆形反馈半径 | 20.dp |

#### 方法

| 方法名           | 类型           | 说明                     |
| ---------------- | -------------- | ------------------------ |
| checkboxColors() | CheckboxColors | 创建复选框的默认颜色配置 |

### CheckboxColors 类

| 属性名                           | 类型  | 说明                         |
| -------------------------------- | ----- | ---------------------------- |
| uncheckedStrokeColor             | Color | 未选中状态的描边色            |
| disabledUncheckedStrokeColor     | Color | 未选中且禁用时的描边色        |
| checkedForegroundColor           | Color | 选中状态时前景色（对勾颜色） |
| uncheckedForegroundColor         | Color | 未选中状态时前景色           |
| disabledCheckedForegroundColor   | Color | 禁用且选中状态时前景色       |
| disabledUncheckedForegroundColor | Color | 禁用且未选中状态时前景色     |
| checkedBackgroundColor           | Color | 选中状态时背景色             |
| uncheckedBackgroundColor         | Color | 未选中状态时背景色           |
| disabledCheckedBackgroundColor   | Color | 禁用且选中状态时背景色       |
| disabledUncheckedBackgroundColor | Color | 禁用且未选中状态时背景色     |

> 半选中状态与选中状态共用同一套颜色。

## 进阶用法

### 自定义颜色

```kotlin
var state by remember { mutableStateOf(ToggleableState.Off) }

Checkbox(
    state = state,
    onClick = { state = if (state == ToggleableState.On) ToggleableState.Off else ToggleableState.On },
    colors = CheckboxDefaults.checkboxColors(
        checkedBackgroundColor = Color.Red,
        checkedForegroundColor = Color.White
    )
)
```

### 父子复选框（半选中模式）

```kotlin
val childStates = remember { mutableStateListOf(false, true, false) }
val parentState by remember {
    derivedStateOf {
        when {
            childStates.all { it } -> ToggleableState.On
            childStates.none { it } -> ToggleableState.Off
            else -> ToggleableState.Indeterminate
        }
    }
}

Column {
    Checkbox(
        state = parentState,
        onClick = {
            val newValue = parentState != ToggleableState.On
            childStates.indices.forEach { childStates[it] = newValue }
        }
    )
    childStates.forEachIndexed { index, checked ->
        Checkbox(
            state = ToggleableState(checked),
            onClick = { childStates[index] = !childStates[index] }
        )
    }
}
```

### 结合文本使用

```kotlin
var state by remember { mutableStateOf(ToggleableState.Off) }

Row(
    verticalAlignment = Alignment.CenterVertically,
    modifier = Modifier.padding(16.dp)
) {
    Checkbox(
        state = state,
        onClick = { state = if (state == ToggleableState.On) ToggleableState.Off else ToggleableState.On }
    )
    Spacer(modifier = Modifier.width(8.dp))
    Text(text = "接受用户协议与隐私政策")
}
```

### 在列表中使用

```kotlin
val options = listOf("选项 A", "选项 B", "选项 C")
val checkedStates = remember { mutableStateListOf(false, true, false) }

LazyColumn {
    items(options.size) { index ->
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                state = ToggleableState(checkedStates[index]),
                onClick = { checkedStates[index] = !checkedStates[index] }
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = options[index])
        }
    }
}
```
