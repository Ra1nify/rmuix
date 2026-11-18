# Button

## 共享原厂聚光内核

聚光入口现统一复用[共享 Spotlight](../media/spotlight)：使用官方光照 shader、预设和弹簧，移除旧幂次光斑/径向回退，并在内容之后混合。此项是公共引擎替换，不表示本控件的系统材质门控和预设选择已全部验收。Button/Checkbox 的可选聚光仍默认关闭。

BREAKING：聚光默认 tint 改为白色，移除组件内部随意指定的 40/140/380dp 半径；自定义颜色与半径仍可通过公共入口显式覆盖。


`Button` 是 Rmuix 中的基础交互组件，用于触发操作或事件。提供了多种风格选择，包括主要按钮、次要按钮和文本按钮。

<div style="position: relative; height: 592px; border-radius: 10px; overflow: hidden; border: 1px solid #777;">
    <iframe id="demoIframe" style="position: absolute; top: 0; left: 0; width: 100%; height: 100%; border: none;" src="../../../compose/index.html?id=button" title="Demo" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" referrerpolicy="strict-origin-when-cross-origin"></iframe>
</div>

## 引入

```kotlin
import io.github.ra1nify.rmuix.kmp.basic.Button
import io.github.ra1nify.rmuix.kmp.basic.ButtonDeformAnchor
import io.github.ra1nify.rmuix.kmp.basic.ButtonTouchMotionConfig
import io.github.ra1nify.rmuix.kmp.basic.LocalButtonTouchMotionConfig
import androidx.compose.runtime.CompositionLocalProvider
```

## 基本用法

Button 组件可以用于触发操作或事件：

```kotlin
Button(
    onClick = { /* 处理点击事件 */ }
) {
    Text("按钮")
}
```

## 按钮类型

Rmuix 提供了多种类型的按钮，适用于不同的场景和重要程度：

### 主要按钮（Primary Button）

```kotlin
Button(
    onClick = { /* 处理点击事件 */ },
    colors = ButtonDefaults.buttonColorsPrimary()
) {
    Text("主要按钮")
}
```

### 次要按钮（Secondary Button）

```kotlin
Button(
    onClick = { /* 处理点击事件 */ },
    colors = ButtonDefaults.buttonColors()
) {
    Text("次要按钮")
}
```

### 文本按钮（Text Button）

```kotlin
TextButton(
    text = "文本按钮",
    onClick = { /* 处理点击事件 */ }
)
```

## 组件状态

### 禁用状态

```kotlin
Button(
    onClick = { /* 处理点击事件 */ },
    enabled = false
) {
    Text("禁用按钮")
}
```

## Stock ColorOS 基线

Large 档最小尺寸为 132×44 dp，内边距 12×8 dp；默认圆角参数恢复官方 -1 哨兵，随当前高度取一半，不再固定 21.5 dp。TextButton 默认单行尾部省略。正值 cornerRadius 与 textStyle 仍可显式覆盖。
普通次要按钮不是永远叠透明层：启用态按 Android ColorUtils 的整数规则将 container8 合成到 elevated 背景，默认亮/暗为 #FFEBEBEB / #FF343434；禁用态保留 #14000000 / #1AFFFFFF。文字和主按钮禁用 alpha 分别来自各自 selector，而不是通用 disabledPrimary。
默认 Sink 使用官方面积与长宽比映射，不再对所有尺寸固定 0.97。按下和抬手保留 Candy/Jelly 的不同弹簧；短按遮罩先达到 70% 进度再退出。hover/press 在内容之前绘制，键盘焦点为外侧描边，不用通用 MIUI indication 代替。键盘/合成 PressInteraction 不冒充 MotionEvent 压缩。
ButtonColors 与 TextButtonColors 末尾新增可选 hoverColor、pressColor、focusOutlineColor；Color.Unspecified 使用基线角色，Color.Transparent 可显式关闭对应颜色层。五个颜色工厂也支持这些参数，TextButton 会完整转发。
BREAKING: MinWidth 58→132dp，水平内边距 16→12dp，CornerRadius 21.5dp→-1（自适应）；enableSpotlight true→false，indication 的默认值改为 null；次要合成色、禁用色/文字默认值、尺寸映射按压及 TextButton 单行行为改变。原有参数名与顺序不变。
原生材质/模糊/阴影边缘与 Oplus 精确轮廓仍未完成；非均匀形变与锚点的数学/合成基线见下，不据此替代实机帧级验收。现有 squircle 路径仍是轮廓近似，显式 spotlight/destructive 配色仍是应用扩展。

### Rubber 拖拽分量

默认 Sink 现在还包含官方 rubber 位移：使用局部事件坐标的 5px 门槛，不按 density 放大，也不减去门槛。X/Y 独立非线性映射，最大拉伸由 28dp 经原生 ties-to-even 规则换算。越过门槛后回到门槛内时不强行清零，保留源码的目标更新方式。
跟手弹簧为 response=0.15/bounce=0.15；松手响应随面积从 0.42 过渡到 0.45，并按原生速度单位、当前位置及上限计算初速度和 bounce。重新抓取回弹中的按钮时，用逆函数重建起点；多指加入切换到新指针，活动指针抬起时切到剩余第一指针，连续保留位移。
rubber 只更新绘制变换，不主动调用 onClick，也不消费指针事件；是否点击仍由原有 clickable 判定。父滚动消费移动时按取消处理，以零手势速度回弹。PressFeedbackType.None 保持无物理缩放/平移，颜色遮罩与焦点仍独立。禁用或卸载会取消动画。
rubber 是独立平移分量；非均匀形变与锚点组合已按下一节接入，原生材质和轮廓仍需后续验收。拖拽/取消示例继续保留点击计数和 No motion 对照。

### 非均匀形变与锚点

形变目标按官方面积/长宽比衰减计算：原始像素面积阈值 38416，不改成 dp²；最大形变从 1.2 衰减并保留 1.03 下界。方向拉伸配合倒数压缩，对角线在对数空间按位移权重混合，目标保持面积；这不表示动画过程每一帧都严格守恒。
最终缩放按 collector 规则相加偏差：combined = uniform + deform - 1，而非相乘。锚点补偿基于该合成缩放与真实像素尺寸，将 (size/2 - pivot) × (combined - 1) 加到 rubber 平移；不再同时修改 transformOrigin。
跟手使用 .15/.15 弹簧，形变可见阈值为 .0005×.75。松手以 abs(scale-1)<.002 判定直接归位；其余轴按官方 hand-up 逻辑回弹，restMagnitude 使用缩放差而不是像素位移，也不误用 150dp 作为速度分母。再抓取按占优形变轴逆推，不自行恢复丢失的旧方向符号。
默认仍是中心、autoDeformAnchor=false。可通过 LocalButtonTouchMotionConfig 提供明确锚点预设；自动锚点在局部主轴方向更新，绝对值相等时保留已有选择，选择后跨抬手保留到重置。配置作用域变化会重建该分量，不宣称无缝模拟所有 native 实时 config/pivot 哨兵细节。

| 配置 | 类型 | 默认值 |
| --- | --- | --- |
| deformAnchor | ButtonDeformAnchor | Center |
| autoDeformAnchor | Boolean | false |

以下为显式开启的锚点配置，默认调用不变：

```kotlin
CompositionLocalProvider(
    LocalButtonTouchMotionConfig provides ButtonTouchMotionConfig(
        deformAnchor = ButtonDeformAnchor.Center,
        autoDeformAnchor = true
    )
) {
    Button(onClick = {}) { Text("Auto anchor") }
}
```


### 按压：换底色 vs 遮罩（2026-11-12，已定论）

`COUIButton.changeButtonAlphaColor(pressed, buttonType)` 确实会按类型替换按钮自身的底色（`mDrawableColor`）——`PRIMARY` 保持 `couiColorContainerTheme`、`SECONDARY` 换成白色 0xA6（浅）/ 0x33（深）、`TRANSPARENT` 换成白色 0x33 / 0x19。**但它只能从 `applyBlurEffect()` 进入**，也就是只在材质/模糊（glass）路径里生效：`COUIMaterialEffect.isMaterialVersion() && mStyle != 0 && mIsDrawMaterialEffect`。

同一方法里还会调用 `mMaskDrawable.enablePressedState(!isBlurEffectEnabled())`：模糊效果开启时**按压遮罩被关掉**、改由换底色表现；普通按钮则保留遮罩、底色不变。

判定的闸口是 `COUIEffectVersionUtil.isMaterialVersion()` = `checkOPlusViewSubSDK(40, 2)`，即 OS 版本号 40.2 及以上为真；而且每个按钮还要通过样式属性 `useMaterialEffect` 主动开启。APK 里的枚举是 `FORCE_ENABLE(1)` / `FORCE_DISABLE(0)` / `FOLLOW_SYSTEM(-1)`，只有 AI 支付相关的样式显式设为 `followSystem`；`Widget.COUI.Button.Large.ButtonNew.Secondary.NoMargin` 设为 `forceDisable`，其余按钮样式都回落到 `FOLLOW_SYSTEM`，即运行时跟随 `COUIMaterialStateManager` 的系统开关。所以标准 `Button` 的按压就是 `COUIBackgroundDrawable` 的遮罩模型，而"换白色底"属于**本库没有出货的材质/模糊按钮变体**（库内有 glass dialog / bottom sheet / popup / tab row / top app bar / icon button / navigation bar，但没有 glass button）。标准 Button 不存在这条差异，也未为此改动代码。
## 属性

### Button 属性

| 属性名            | 类型                            | 说明                     | 默认值                        | 是否必须 |
| ----------------- | ------------------------------- | ------------------------ | ----------------------------- | -------- |
| onClick           | () -> Unit                      | 点击按钮时触发的回调     | -                             | 是       |
| modifier          | Modifier                        | 应用于按钮的修饰符       | Modifier                      | 否       |
| enabled           | Boolean                         | 按钮是否可点击           | true                          | 否       |
| enableSpotlight | Boolean | 额外的非 stock shader 扩展 | false | 否 |
| spotlightColor | Color | 可选共享光照的颜色，非按压遮罩颜色 | Color.White | 否 |
| pressFeedbackType | PressFeedbackType | 物理缩放类型；Sink 使用按尺寸映射的压缩 | PressFeedbackType.Sink | 否 |
| cornerRadius      | Dp                              | 按钮圆角半径             | ButtonDefaults.CornerRadius   | 否       |
| minWidth          | Dp                              | 按钮最小宽度             | ButtonDefaults.MinWidth       | 否       |
| minHeight         | Dp                              | 按钮最小高度             | ButtonDefaults.MinHeight      | 否       |
| colors            | ButtonColors                    | 按钮颜色配置             | ButtonDefaults.buttonColors() | 否       |
| insideMargin      | PaddingValues                   | 按钮内部边距             | ButtonDefaults.InsideMargin   | 否       |
| interactionSource | MutableInteractionSource?       | 按钮的交互源             | null                          | 否       |
| indication        | Indication?                     | 点击交互的反馈效果       | null       | 否       |
| content           | @Composable RowScope.() -> Unit | 按钮内容区域的可组合函数 | -                             | 是       |

### TextButton 属性

| 属性名            | 类型                      | 说明                 | 默认值                            | 是否必须 |
| ----------------- | ------------------------- | -------------------- | --------------------------------- | -------- |
| text              | String                    | 按钮显示的文本       | -                                 | 是       |
| onClick           | () -> Unit                | 点击按钮时触发的回调 | -                                 | 是       |
| modifier          | Modifier                  | 应用于按钮的修饰符   | Modifier                          | 否       |
| enabled           | Boolean                   | 按钮是否可点击       | true                              | 否       |
| enableSpotlight | Boolean | 额外的非 stock shader 扩展 | false | 否 |
| spotlightColor | Color | 可选共享光照的颜色，非按压遮罩颜色 | Color.White | 否 |
| pressFeedbackType | PressFeedbackType | 物理缩放类型；Sink 使用按尺寸映射的压缩 | PressFeedbackType.Sink | 否 |
| cornerRadius      | Dp                        | 按钮圆角半径         | ButtonDefaults.CornerRadius       | 否       |
| minWidth          | Dp                        | 按钮最小宽度         | ButtonDefaults.MinWidth           | 否       |
| minHeight         | Dp                        | 按钮最小高度         | ButtonDefaults.MinHeight          | 否       |
| colors            | TextButtonColors          | 文本按钮颜色配置     | ButtonDefaults.textButtonColors() | 否       |
| insideMargin      | PaddingValues             | 按钮内部边距         | ButtonDefaults.InsideMargin       | 否       |
| textStyle         | TextStyle?                | 按钮文本的文本样式   | null（RmuixTheme.textStyles.button）| 否     |
| interactionSource | MutableInteractionSource? | 按钮的交互源         | null                              | 否       |
| indication        | Indication?               | 点击交互的反馈效果   | null           | 否       |

### ButtonDefaults 对象

ButtonDefaults 对象提供了按钮组件的默认值和颜色配置。

#### 常量

| 常量名       | 类型          | 说明           | 默认值               |
| ------------ | ------------- | -------------- | -------------------- |
| MinWidth     | Dp            | 按钮的最小宽度 | 132.dp                |
| MinHeight    | Dp            | 按钮的最小高度 | 44.dp                |
| CornerRadius | Dp            | 按钮的圆角半径 | (-1).dp（按当前高度自适应）                |
| InsideMargin | PaddingValues | 按钮的内部边距 | PaddingValues(horizontal = 12.dp, vertical = 8.dp) |

#### 方法

| 方法名                    | 类型             | 说明                       |
| ------------------------- | ---------------- | -------------------------- |
| buttonColors() | ButtonColors | 次要按钮：启用态按原生整数规则合成到 elevated 背景，禁用态保留 container8 |
| buttonColorsPrimary()     | ButtonColors     | 创建主要按钮的颜色配置     |
| textButtonColors()        | TextButtonColors | 创建次要文本按钮的颜色配置 |
| textButtonColorsPrimary() | TextButtonColors | 创建主要文本按钮的颜色配置 |

## 进阶用法

### 带图标按钮

```kotlin
Button(
    onClick = { /* 处理点击事件 */ }
) {
    Icon(
        imageVector = RmuixIcons.Favorites,
        contentDescription = "Favorites"
    )
    Spacer(modifier = Modifier.width(8.dp))
    Text("带图标按钮")
    }
```

### 自定义样式按钮

```kotlin
Button(
    onClick = { /* 处理点击事件 */ },
    colors = ButtonDefaults.buttonColors(
        color = Color.Red.copy(alpha = 0.7f)
    ),
    cornerRadius = 8.dp
) {
    Text("自定义按钮")
}
```

### 加载状态按钮

```kotlin
var isLoading by remember { mutableStateOf(false) }
val scope = rememberCoroutineScope()

Button(
    onClick = {
        isLoading = true
        // 模拟操作
        scope.launch {
            delay(2000)
            isLoading = false
        }
    },
    enabled = !isLoading
) {
     AnimatedVisibility(
        visible = isLoading
    ) {
        CircularProgressIndicator(
            modifier = Modifier.padding(end = 8.dp),
            size = 20.dp,
            strokeWidth = 4.dp
        )
    }
    Text("提交")
}
```
