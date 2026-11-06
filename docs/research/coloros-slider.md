# ColorOS 17 COUISeekBar 控件 1:1 视觉与动效/光效调研报告

针对 ColorOS 17 `COUISeekBar` / `COUIIntentSeekBar` 原生控件的几何尺寸、颜色色值、光效（Glitter/SpotLight/ConicLight）与动效（物理弹簧/按压放大/边缘拉伸）进行 1:1 反编译源码深度剖析。

---

## 1. 原生源码与资源锚定

- **反编译源码根路径**：`C:\dump\settings_decompiled\sources\com\coui\appcompat\seekbar\`
  - 控件核心类：`COUISeekBar.java` (259 行) / `core/COUISeekBarCore.java` (2189 行) / `core/COUISeekBarAdvanced.java` (1486 行) / `core/SeekBarState.java` (852 行)
  - 细闪光效渲染器：`effect/GlitterEffectRenderer.java` (228 行) & `effect/GlitterEffectParams.java`
  - 聚光灯高光渲染器：`effect/SpotLightEffectRenderer.java` (177 行) & `renderer/TexturedThumbRenderer.java` (371 行)
  - 圆锥光束渲染器：`effect/ConicLightEffectRenderer.java` (680 行) & `shader/ConicLightShaderParams.java`
  - 物理动效句柄：`animation/FlexibleFollowHandHandler.java` & `animation/PhysicsAnimationHandler.java`
- **反编译资源路径**：`C:\dump\settings_decompiled\resources\res\values\dimens.xml`
  - 查找项：`coui_seekbar_*`

---

## 2. 几何度量规范 (Dimensions & Metrics)

| 度量项 | 原生 Resource Key | 经典值 (Dp/Sp) | 对应说明 |
| :--- | :--- | ---: | :--- |
| **视图最小响应高度** | `coui_seekbar_view_min_height` | **36dp** | 点击/触摸响应外框高度 |
| **标准胶囊 Track 高度** | `coui_seekbar_background_height` | **20dp** | 大型胶囊形态 Slider 轨道高度 |
| **细轨 Intent Track 高度** | `coui_seekbar_intent_height_default` | **4dp** | 传统/精细形态 Slider 轨道高度 |
| **Thumb 静态半径** | `coui_seekbar_thumb_radius` | **6.0dp** | 静态下 Thumb 圆形半径（直径 12dp） |
| **Thumb 按压/拖拽最大半径** | `coui_seekbar_thumb_max_radius` | **8.0dp** | 按压/拖拽/Hover 时的 Thumb 最大半径（放大比 1.333x） |
| **Thumb 核心微调半径** | `coui_seekbar_thumb_in_scale_radius` | **3.67dp** | 内部核心微调亮点半径 |
| **Thumb 阴影尺寸** | `coui_seekbar_thumb_shadow_size` | **2.0dp** | 高斯阴影扩散半径 |
| **Thumb 阴影 Y 偏移** | `coui_seekbar_shadow_offset_y` | **2.0dp** | 阴影下移 |
| **静态水平内边距** | `coui_seekbar_progress_padding_horizontal` | **14dp** | 轨道两端留白距 |
| **按压水平内边距** | `coui_seekbar_progress_pressed_padding_horizontal` | **4dp** | 按压拖拽时轨道两端形变拉伸边缘距离 |

---

## 3. 核心光效渲染体系 (Light & Material Effects)

ColorOS 17 Slider 的灵性核心在于三层光效混合绘制：

1. **细闪光效 (Glitter Effect)**
   - 源码：`GlitterEffectRenderer.java`
   - 表现：在已激活进度条（Foreground Track）表面绘制高频微光闪烁粒子，随着 progress 拖拽动态流动。
   - 最小/最大光泽色值：`coui_seekbar_glitter_effect_min_color` (`#1AFFFFFF`) / `coui_seekbar_glitter_effect_max_color` (`#4DFFFFFF`)。
2. **聚光灯/高光环 (SpotLight & Textured Thumb)**
   - 源码：`SpotLightEffectRenderer.java` + `TexturedThumbRenderer.java`
   - 表现：Thumb 边缘包裹一层聚焦的柔和高光环与轻微微光（SpotLight/Spotlight Ring），增强 3D 实体触摸感。
3. **圆锥扫光/渐变光 (Conic Light Beam)**
   - 源码：`ConicLightEffectRenderer.java`
   - 表现：在进度条头部（Thumb 前方/交界处）绘制极短的半透明渐变扫光（Head Glint）。

---

## 4. 核心物理动效体系 (Motion Effects & Physics)

1. **Thumb 按压弹性放缩 (Thumb Spring Scale)**
   - 静态半径 6.0dp ↔ 按压/拖拽半径 8.0dp (`scale: 1.0f -> 1.333f`)。
   - 采用弹性动效（`spring(dampingRatio = 0.75f, stiffness = 450f)`），具有平滑过冲与回弹感。
2. **跟手阻尼与平滑差值 (Flexible Follow-Hand)**
   - `FlexibleFollowHandHandler` 实现拖拽点与真实 Thumb 位置之间的平滑跟随与物理阻尼（Damping）。
3. **触顶/触底边缘形变 (Edge Bumping & Deformation)**
   - 当 Slider 滑拽至 0% 或 100% 极值时，触发 `isBumpingEdges` 边缘微幅拉伸与恢复弹簧。

---

## 5. 颜色色值体系 (Color Scheme & Neutral Alpha)

遵照 ColorOS **“次要/未激活状态一律采用半透明中性色”** 原则：

| 映射项 | 亮色模式 (Light) | 暗色模式 (Dark) | 语义说明 |
| :--- | :--- | :--- | :--- |
| **未激活 Track 背景** | `#1F000000` (12% 黑色) | `#33FFFFFF` (20% 白色) | 半透明中性色，绝不使用实心灰色 |
| **激活 Track 进度色** | 主主题色 (`#2080F0` / Primary) | 主主题色 (`#2080F0` / Primary) | 核心品牌高亮 |
| **Thumb 填充色** | `#FFFFFF` | `#FFFFFF` | 纯白带 2dp 高斯阴影（`#1A000000`） |
| **细闪微光粒子** | `#26FFFFFF` | `#40FFFFFF` | 动态微光 |
| **禁用 Track 色** | `#14000000` (8% 黑色) | `#26FFFFFF` (15% 白色) | 禁用半透明 |
| **禁用 Track 进度色** | `#3D2080F0` (24% 主色) | `#3D2080F0` (24% 主色) | 禁用进度 |

---

## 6. 拟定 API 变更方案 (ColorOS Fidelity + 100% Compatibility)

所有新增的光效与动效功能性参数均提供默认值，保持与现存 `Slider` / `VerticalSlider` / `RangeSlider` 的完全兼容：

```kotlin
// SliderDefaults 补充 ColorOS 几何尺寸与默认值
object SliderDefaults {
    val MinHeight = 36.dp
    val CapsuleTrackHeight = 20.dp
    val IntentTrackHeight = 4.dp
    val ThumbRadius = 6.dp
    val ThumbMaxRadius = 8.dp
    val ThumbInScaleRadius = 3.67.dp
    val ThumbShadowSize = 2.dp

    @Composable
    fun sliderColors(
        backgroundColor: Color = if (isSystemInDarkTheme()) Color(0x33FFFFFF) else Color(0x1F000000),
        foregroundColor: Color = RmuixTheme.colorScheme.primary,
        thumbColor: Color = Color.White,
        thumbShadowColor: Color = Color(0x1A000000),
        glitterMinColor: Color = Color(0x1AFFFFFF),
        glitterMaxColor: Color = Color(0x4DFFFFFF),
        disabledBackgroundColor: Color = if (isSystemInDarkTheme()) Color(0x26FFFFFF) else Color(0x14000000),
        disabledForegroundColor: Color = RmuixTheme.colorScheme.primary.copy(alpha = 0.24f),
        disabledThumbColor: Color = Color.White.copy(alpha = 0.6f),
    ): SliderColors
}

// 控件签名中新增可选参数 (默认开启 ColorOS 光效与动效)
@Composable
fun Slider(
    value: Float,
    onValueChange: (Float) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    valueRange: ClosedFloatingPointRange<Float> = 0f..1f,
    steps: Int = 0,
    onValueChangeFinished: (() -> Unit)? = null,
    reverseDirection: Boolean = false,
    height: Dp = SliderDefaults.MinHeight,
    trackHeight: Dp = SliderDefaults.CapsuleTrackHeight,
    colors: SliderColors = SliderDefaults.sliderColors(),
    hapticEffect: SliderDefaults.SliderHapticEffect = SliderDefaults.DefaultHapticEffect,
    showGlitterEffect: Boolean = true,     // 新增：细闪粒子光效
    showSpotLight: Boolean = true,          // 新增：Thumb 聚光高光
    showKeyPoints: Boolean = false,
    keyPoints: List<Float>? = null,
    magnetThreshold: Float = 0.02f,
)
```

---

## 7. 下一步计划

调研报告已生成。等待用户确认评估方案后，将开始在 `Slider.kt` 中实施具体的物理动效、光效绘制 Shader/Canvas 渲染，并同步更新相关文档与 Demo！
