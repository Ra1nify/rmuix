# Slider

`Slider` 是 Rmuix 中的基础交互组件，用于在连续的数值范围内进行选择。用户可以通过拖动滑块来调整值，适用于诸如音量调节、亮度控制、进度显示等场景。

Rmuix 还提供了 `VerticalSlider` 用于垂直方向的滑块、`RangeSlider` 用于选择数值范围，以及官方竖向滑杆
`VerticalSeekBar`（`COUIVerticalSeekBar`）。

<div style="position: relative; height: 250px; border-radius: 10px; overflow: hidden; border: 1px solid #777;">
    <iframe id="demoIframe" style="position: absolute; top: 0; left: 0; width: 100%; height: 100%; border: none;" src="../../../compose/index.html?id=slider" title="Demo" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" referrerpolicy="strict-origin-when-cross-origin"></iframe>
</div>

## Stock ColorOS 静态复核（2026-11-11）

本轮只复核连续的水平 Slider，依据 Settings APK 的 COUISeekBarCore、HorizontalOrientationHandler、StandardAnimationHandler 和颜色 selector。不是完整 1:1 验收。

- 背景静止 20dp，按压扩展到 28dp（1.4 倍）；前景保持 20dp，不跟随背景变粗。
- 滑块中心的端点固定内缩 14dp；背景横向边界从静止的 4dp 内缩扩展到按压的 0dp。绘制和触摸使用相同的固定行程，按压不会移动端点。
- 背景弹簧 response=0.5、bounce=0；短按先扩展到一半，再回缩。滑块半径为 6dp → 8dp，按下/抬起 bounce 分别为 0.3/0，response 均为 0.5。
- 中性色跟随 Rmuix 当前主题，而非系统主题。背景在启用/禁用时均为亮色 #1F000000、暗色 #33FFFFFF；禁用滑块为 #8AFFFFFF / #29FFFFFF。默认禁用前景映射 primary 的 0x4D alpha；宿主的独立 couiColorDisable 角色可通过现有颜色参数传入。
- 禁用的水平 Slider 不再暴露可写的无障碍进度动作；启用动作遵循当前步长/磁吸配置，并调用完成回调。

**BREAKING：** 禁用颜色默认值、水平轨道布局、按压反馈及无障碍进度行为已按官方实现修正；公开参数名称、顺序和可调用性不变。

**仍待复核：** 原生像素/大整数精度、fling/触感、多指与逐帧中断等价性、原生纹理/阴影/聚光/细闪。水平showSpotLight已接共享轨道光照，由显式材质标记共同门控，仍独立于标准滑块阴影；VerticalSlider/RangeSlider仍用旧近似。showGlitterEffect暂无渲染实现。`steps` + `showKeyPoints` 现在已复刻 `COUISectionSeekBar` 的刻度（见“分段刻度滑杆对齐”一节），仅剩 200ms 程序化过渡与厂商线性马达强度两项差异。垂直实现尚未按独立 COUIVerticalSeekBar 验收；RangeSlider 是扩展，本轮没有找到可支撑其 COUIRangeSeekBar 名称的官方实现。不得把这些能力标成 stock 已完成。 **Intent 变体：** `COUIIntentSeekBar`（Kotlin 源码类，不是资源驱动的样式）有三档 —— small 轨道 2dp / 最大背景 10dp / 滑块 3dp，default 4 / 6 / 4，large 6 / 10 / 5；配色固定为半透明白（背景 `#4DFFFFFF`、进度 `#8AFFFFFF`、次级 `#29FFFFFF`），滑块跟随主题（亮色 `#FF000000` / 暗色 `#FFFFFFFF`）。本库没有 intent 变体。

### 点击、外部赋值与拖拽（续核）

- 外部 value 变化直接更新，不自动套用点击弹簧。点击则先以 response=0.3 的弹簧预览，到达终点后才调用 onValueChange 和 onValueChangeFinished；预览期间无障碍进度跟随实际绘制值。
- 拖拽只检查水平位移是否严格超过 touch slop，使用完整指针坐标，不扣除门槛，也不因纯纵向移动而自行进入拖拽。连续跟手使用 response=0.15；普通跟手路径的非 fling 松手完成回调与最后一段跟手动画分离，不切换为点击弹簧。
- 官方无拖拽 ACTION_CANCEL 分支也会启动点击过渡。这里将父级消费事件映射到此分支；普通抬手则要求落点仍在控件内部。组合卸载/配置失效不是一次新的点击，不发出延迟提交。
- 受控 API 适配：调用方接受的值回写不取消跟手；拒绝或改写请求时回到调用方值，但不把正在按住的拖拽当作抬手。外部覆盖、禁用和配置重建取消过期提交。这是 Compose 受控模型的适配，不宣称与原生可变 View 的所有重入路径等价。

**BREAKING：** 水平 Slider 的点击 onValueChange 从抬手立即调用改为弹簧结束后提交；完成回调不再在预览刚开始时发出。取消旧点击不提交其目标；开始另一点击/拖拽时仍会结束旧点击的 tracking 生命周期。VerticalSlider / RangeSlider 尚未采用这套生命周期。

### 首拖过渡与整数进度映射（2026-11-12）

- 第一帧进入拖拽时，只判定一次“指针目标与当前绘制进度是否超过一个原生进度单位”。超过时进入 response=0.15、bounce=0 的首拖过渡；之后即使弹簧到达目标，也继续走该路径，直到抬手/取消。
- 首拖过渡按动画帧提交截断后的整数进度，而不是立刻提交手指终点；普通跟手分支仍在指针移动时提交最近进度。抬手时先吸附到最终指针坐标，再以最近整数提交最终值和完成回调。
- 新增尾部可选参数 progressRange: IntRange? = null。null 保留未量化的 Float 扩展，对应连续极限的非零差值门槛；显式 0..100 则将 valueRange 映射到原生100个进度间隔，复现严格大于一个单位、逐帧截断/松手取整的区别。不能从 Float 的值域猜测100格。
- progressRange 的起点必须非负、终点必须更大；外部值、点击与无障碍写入按最近整数映射，绘制保持连续。它不新增刻度，也不等于 steps；分段刻度滑杆的对齐见 `steps` + `showKeyPoints` 一节。不要叠加自定义步长/磁吸后宣称仍是原生连续控件。

**BREAKING：** 水平 Slider 的首拖回调改为沿过渡帧报告，不再立即报告远处指针终点；首拖过渡仍激活时，松手不再保留旧的绘制跟手尾巴。已有参数名称、顺序保持不变；未传 progressRange 不引入整数网格。

示例：将归一化值映射到官方整数进度，仍保留默认无刻度外观。

```kotlin
var nativeValue by remember { mutableFloatStateOf(0.5f) }
Slider(
    value = nativeValue,
    onValueChange = { nativeValue = it },
    progressRange = 0..100,
)
```

### 越界目标与 RTL 坐标（2026-11-12）

- 首拖过渡启动时，目标仍限制在轨道内；同一过渡的后续指针更新在内部保留 -1..2 的比例。绘制和回调值仍在合法范围，额外状态影响的是返回运动，不是画出超长轨道。
- 抬手/取消时，激活的首拖过渡先吸附到最终指针的合法轨道位置，再提交最终值。不新增越界形变样式，也不冒充实现独立的 OEM touch-motion 渲染器或 fling 引擎。
- RTL 触摸以原生右侧锚点（宽度减固定内缩）计算，不把取整后的绘制行程直接镜像。分数密度下，两者可能相差分数像素；保留源码区别，不强行做成看似对称的错误公式。
- 自定义步长/关键点在轨道内继续按扩展规则磁吸，越界目标保留连续比例；整数进度映射仍将对外值限制到合法范围。

**BREAKING：** 首拖过渡激活后的越界移动不再把弹簧目标提前压平在端点；返回运动和分数像素 RTL 输入按官方公式修正。公开参数不变。本轮静态修正不代表设备级 1:1 验收。

### 连续滑块 fling（2026-11-12）

- 水平连续 Slider 在释放时，物理 X 速度达到100原始px/s即继续惯性运动，速度上限8000px/s。速度只取 DOWN/MOVE 样本，不额外加入最终 UP 坐标；RTL 反转进度映射，不反转物理速度本身。
- 一维实现按官方 Oplus mode-4 FlingBehavior 移植：速度先乘1/(1+15*dt)，再积分位置；边界由独立2.8Hz、阻尼比1的辅助弹簧处理，保留四次速度约束迭代。不是通用指数衰减，也不是把内部物体直接钳死而丢掉动量。
- 绘制与回调仍在合法范围；速度和约束距离均低于源码的原始0.1像素阈值后，才调用 onValueChangeFinished。重新按住会停止旧fling并结束其tracking；受控值覆盖/配置失效仍取消过期工作，不提交旧目标。
- Android使用系统VelocityTracker并记录视图所在显示器的标称周期；原始MotionEvent现已在当前事件作用域内复制采样，保留历史批次与原生指针ID，仅缺少原始元数据的构造事件使用合成样本。非Android使用Compose估速和引擎1/120秒回退，不能据此声称宿主平台时序一致；各平台帧/手势链路仍需另行核验。
- 仅steps=0且未配置关键点时使用；带步长/关键点的滑块仍按扩展管理，不接这条连续fling路径。本轮不修改VerticalSlider和RangeSlider。

**BREAKING：** 水平连续滑块抬手后可能继续回调数值，完成回调由抬手改为fling结束。公开参数不变。本轮只是按stock公式和生命周期静态移植，没有构建、执行测试或实机验收。

### 原始事件历史与多指（2026-11-12）

- Android复制原始DOWN/MOVE，只平移副本到组件局部坐标后，将完整批次交给系统估速；不修改或持有Compose借用的MotionEvent，副本使用后立即回收。UP与增减手指事件不额外进入速度采样。
- 原生pointer ID映射到Compose ID，跟随原生索引0，而非永久锁住首次按下的Compose ID。新增手指和部分抬起不更新进度、不结束tracking；拖动原点仍是初次按下坐标，按官方实现保留，不发明多指rebase。
- Android缺少原始元数据时及非Android回退，会先输入已有历史点，再输入当前点；选中指针变化时重置回退速度窗口，避免把换指当作同一手指瞬移。此回退不宣称等同原生多指估速策略。

**BREAKING：** 初始手指抬起、但仍有其他手指按住时，不再提前结束手势或启动fling，继续等待最终释放/取消路径。原始批次估速也可能改变旧版单点重建得到的fling结果。公开参数不变。

本轮仅恢复基础源码路径，并非实机验收；跨子节点命中范围的指针过滤、变换图层、构造/原生事件混合及平台取消时序仍需继续核验。

### 标准阴影与显式纹理滑块（2026-11-12）

- 水平普通滑块恢复官方样式的4dp阴影半径（转为整数像素）、2dp向下偏移；启用时绘制，禁用时关闭。它不受showSpotLight控制；原先半径增大1.5dp的实心圆并不是该阴影。
- 新增尾部可选thumbTextureEnabled=false，显式映射已解析的材质状态或用于预览。官方门控还依赖材质特性提供器、模糊设置、版本/主题与功耗，库没有猜测这些运行时值，不能将该参数等同系统自动解析。
- 开启后，按下时纹理层以response=.3出现，白色盖层从1以response=.5退出；抬手时分别回到0/1，response均为.3，均无bounce。中途只切换材质标记不会制造新按下事件。
- 控件enabled=false与材质标记关闭不是一回事：控件禁用不合成纹理抬手；关闭材质标记才重置两路alpha。禁用后重新启用且尚未抬手的完整事件恢复仍需继续核对。
- 三层为底到顶的230/230/140白色alpha渐变、#77FFFFFF底与白色内阴影位图、独立白色盖层。内阴影5px模糊/.4px偏移/10px描边/12px缓存留白均为原始像素，不乘density；盖层及分组alpha按整数截断。
- 按官方实现，内阴影位图只捕获本次renderer首次非零纹理帧，之后随目标矩形缩放，不按每个半径重新生成；销毁时释放缓存。纹理层内部采用官方白色，不使用自定义thumbColor替代；回到普通模式时才使用原有颜色selector。

**BREAKING：** 水平showSpotLight=false不再移除滑块阴影；若要自定义无阴影外观，可通过现有sliderColors(thumbShadowColor=Color.Transparent)覆盖。公开旧参数顺序不变，新增纹理参数默认关闭。VerticalSlider/RangeSlider未迁移到此renderer。

Android使用原生Paint/Canvas及软件位图；Skia使用对应分层和模糊滤镜。API移植不代表光栅结果逐像素一致；原生轨道聚光/细闪、系统门控接入、禁用/配置切换逐帧和各平台光栅差异仍待继续核验。

### 复用共享聚光（2026-11-12）

轨道聚光现调用同一个[公共 Spotlight](../media/spotlight)，使用官方 OpaqueMedium 预设；没有 Slider 私有 shader/动画副本。showSpotLight 与显式 thumbTextureEnabled 材质标记同时开启时生效，不自行猜测系统门控。裁剪跟随绘制后的背景矩形，shader 在滑块之后混合。中间背景高度按源码取整，静止/最大端点保留原值；裁剪圆角保留当前未取整半径。


### `coui_seekbar_*` token 对照（2026-11-12）

`COUISeekBarCore` 的几何与配色来自 **`coui_seekbar_*`**（前缀是 `seekbar` 而不是 `slider`，早先有一轮因为搜错了前缀，错误地得出"轨道没有官方依据"的结论）：

| 库内值 | COUI token | 值 |
| --- | --- | --- |
| `SliderDefaults.MinHeight` | `coui_seekbar_view_min_height` | 36dp |
| `CapsuleTrackHeight` | `coui_seekbar_background_height` | 20dp |
| `IntentTrackHeight` | `coui_seekbar_intent_height_default` | 4dp |
| `ThumbRestingRadius` | `coui_seekbar_thumb_radius` | 6dp |
| `ThumbExpandedRadius` | `coui_seekbar_thumb_max_radius` | 8dp |
| `ThumbInScaleRadius` | `coui_seekbar_thumb_in_scale_radius` | 3.67dp |
| `ThumbShadowSize` | `coui_seekbar_thumb_shadow_size` | 2dp |
| 背景色 | `coui_seekbar_background_selector` → `couiColorContainer12` | #1F000000 / #33FFFFFF |
| 进度色 | `coui_seekbar_progress_selector` → `couiColorContainerTheme` | 主题 primary（#0080FF / #1A8CFF） |
| 滑块色 | `coui_seekbar_thumb_selector` | 白色；禁用 `coui_seekbar_thumb_disable_color` #8AFFFFFF / #29FFFFFF |

`SliderDefaultsTest` 固定上述七个几何值。进度色按本仓约定取主题 `primary`，而不是裸的 `#0080FF` / `#1A8CFF`。
## 引入

```kotlin
import io.github.ra1nify.rmuix.kmp.basic.Slider
import io.github.ra1nify.rmuix.kmp.basic.VerticalSlider
import io.github.ra1nify.rmuix.kmp.basic.RangeSlider
```

## 基本用法

### `COUISectionSeekBar`（分段刻度滑杆）对齐（2026-11-17）

`COUISectionSeekBar` 继承 `COUISeekBar`，样式只覆盖 `couiSeekBarPhysicsEnable=false`；分段观感来自
`SectionProgressRenderer` + `SectionAnimationHandler`。库内用 `Slider(steps = max - 1, showKeyPoints = true)` 还原：

| 官方实现 | 依据 | 库内对应 |
| :--- | :--- | :--- |
| 每段一个刻度、两端都画 | `SectionProgressRenderer.markPositionAt()` 循环 `index = 0..max` | `stepsToTickFractions()` 返回 `steps + 2` 个分数（0 … 1） |
| 刻度半径 3dp | `coui_section_seekbar_tick_mark_radius`（兜底 `DEFAULT_SECTION_MARK_RADIUS = 3f`） | `SliderDefaults.TickMarkRadius = 3.dp` |
| 已选/未选颜色 | `coui_seekbar_active_mark_selector` = `?attr/couiColorLabelOnColor`（#FFFFFF）；`coui_seekbar_inactive_mark_selector` = `?attr/couiColorLabelTertiary`（#42000000 / #4Dffffff）；禁用 #8AFFFFFF / #29FFFFFF 与 #42000000 / #29FFFFFF（`values-night`） | `SliderColors.keyPointForegroundColor` / `keyPointColor` 及 `*Disabled` 四值，`SliderSectionMarkTest` 断言 |
| thumb 覆盖的刻度不画 | `drawMarkIfVisible()`：仅 `mark + r <= thumbLeft || mark - r >= thumbRight` 时绘制 | 新增 `sliderSectionMarkIsVisible()`，水平/垂直/RangeSlider 三处绘制均跳过 |
| 关闭物理惯性 | 样式 `couiSeekBarPhysicsEnable=false` | `SliderMotion` 的 `physicsEnabled = steps == 0 && keyPoints.isNullOrEmpty()`，离散滑杆不走 fling |
| 值吸附到最近段 | `SectionAnimationHandler.snapToNearestSection()`，`SNAP_POSITION_THRESHOLD = 0.5f` | `resolveValueFromFraction()` 在 `steps > 0` 时取最近 step |
| 吸附动画 100ms 拖动 / 150ms 释放，`PathInterpolator(0,0,0.25,1)` | `DRAG_SNAP_DURATION` / `RELEASE_SNAP_DURATION` | 新增 `sliderSectionSnapSpec(dragging)`（100ms / 150ms），离散滑杆替代连续弹簧 |
| 分段触感 | `VibrationFeedbackHandler.setUseSectionStyleFeedback(true)` | `SliderHapticEffect.Step`：每跨一个 key point 触发一次 |

已知差异：官方 `setProgress(progress, animate = true, …)` 走 200ms `PROGRESS_TRANSITION_DURATION`，
而 Compose 的 value 由调用方持有，没有等价的"程序化动画入口"；`handleFastMoveSnap()` /
`onDirectionReversed()` 只是把目标重新锚定到同一个"最近段"，与库内统一取整等价。
`coui_section_seekbar_progress_padding_horizontal`（12dp）是**死 token**：只在 `R.java` 出现，
没有任何代码/布局读取。

### VerticalSeekBar（`COUIVerticalSeekBar`）

官方的竖向滑杆（`com.coui.appcompat.seekbar.COUIVerticalSeekBar`）是**独立控件**，不是旋转过的
`VerticalSlider`：圆角轨道从底部 padding 里长出，进度自底向上填充，可选方形 thumb，拖动时还有一条把
轨道/进度/padding 一起加宽的按压“光带”。systemui 的快捷开关亮度/音量滑条用的就是它
（`qs_toggle_slider_container_layout.xml`、`vertical_volume_row.xml`，共 22 处布局引用）。

```kotlin
var value by remember { mutableFloatStateOf(0.4f) }
VerticalSeekBar(
    value = value,
    onValueChange = { value = it },
    modifier = Modifier.size(36.dp, 180.dp),
)
```

| 官方属性 | 参数 / 默认值 | 依据 |
| :--- | :--- | :--- |
| `couiVerticalSeekBarMinWidth` | `VerticalSeekBarStyle.minWidth` = 36dp | `coui_vertical_seekbar_view_min_width` |
| `couiVerticalSeekBarMaxHeight` | `VerticalSeekBarStyle.maxHeight` = 未指定（官方 `0` 表示不限制） | `onMeasure()` |
| `couiVerticalSeekBarBackgroundWidth` | `backgroundWidth` = 4dp = 半径 × 2 | `ensureSize()` / 属性默认 |
| `couiVerticalSeekBarBackgroundRadius` | `backgroundRadius` = 2dp | `coui_seekbar_background_radius` |
| `couiVerticalSeekBarProgressWidth` | `progressWidth` = 4dp = 半径 × 2 | 属性默认 |
| `couiVerticalSeekBarProgressRadius` | `progressRadius` = 2dp | `coui_seekbar_progress_radius` |
| `couiVerticalSeekBarProgressPaddingVertical` | `paddingVertical` = 10dp | `coui_vertical_seekbar_progress_padding_vertical` |
| 按压 padding | `pressedPaddingVertical` = 4dp；目标 = 按压 padding + 半径 × 半径放大系数 | `coui_vertical_seekbar_progress_pressed_padding_vertical` |
| `couiVerticalSeekBarBackGroundEnlargeScale` | `backgroundEnlargeScale` = 6.0 | `ensureSize()` |
| `couiVerticalSeekBarProgressEnlargeScale` | `progressEnlargeScale` = 4.0 | `ensureSize()` |
| `couiVerticalSeekBarBackGroundRadiusEnlargeScale` | `backgroundRadiusEnlargeScale` = 6.0 | 默认跟随背景系数 |
| `couiVerticalSeekBarProgressRadiusEnlargeScale` | `progressRadiusEnlargeScale` = 4.0 | 默认跟随进度系数 |
| `couiVerticalSeekBarShowThumb` | `showThumb` = true；thumb 为 `progressWidth × progressEnlargeScale` 的圆角方块 | `ensureSize()` / `drawThumb()` |
| `couiVerticalSeekBarShowProgress` | `showProgress` = true | `drawActiveTrack()` |
| `couiVerticalSeekBarProgressFull` | `progressFull` = false；`true` 时进度继承轨道宽度/半径/放大系数 | `resetProgressSize()` |
| `TOUCH_ANIMATION_ENLARGE_DURATION` / `RELEASE_ANIM_DURATION` | `touchEnlargeDurationMillis` = 183 | 常量 |

颜色按官方 selector：轨道 `?attr/couiColorContainer12`（`#1F000000` / `#33FFFFFF`），进度
`?attr/couiColorContainerTheme`（库内取 `RmuixTheme.colorScheme.primary`），thumb `coui_seekbar_thumb_color`
（`#FFFFFF`），禁用 thumb `#8AFFFFFF` / `#29FFFFFF`。

已知差异：形变引擎（`couiVerticalSeekBarDeformation`）、物理惯性（`couiVerticalSeekBarPhysicsEnable`）、
数值文字（`couiVerticalSeekBarShowText`）、振动（`EnableVibrator` / `AdaptiveVibrator`）、点击过渡动画与
平滑圆角权重（`OplusPath`/`OplusCanvas`）未建模；`couiVerticalSeekBarBackgroundHighlightColor`、
`couiVerticalSeekBarProgressScaleRadius`、`couiVerticalSeekBarThumbOutRadius` 是**死属性**——类里从不读取
（thumb 半径始终由 `progressRadius × progressRadiusEnlargeScale` 推出）。

### Slider

```kotlin
var sliderValue by remember { mutableFloatStateOf(0.5f) }

Slider(
    value = sliderValue,
    onValueChange = { sliderValue = it }
)
```

### VerticalSlider

```kotlin
var sliderValue by remember { mutableFloatStateOf(0.5f) }

VerticalSlider(
    value = sliderValue,
    onValueChange = { sliderValue = it },
    modifier = Modifier.height(200.dp)
)
```

### RangeSlider

```kotlin
var rangeValue by remember { mutableStateOf(0.2f..0.8f) }

RangeSlider(
    value = rangeValue,
    onValueChange = { rangeValue = it }
)
```

## 组件状态

### 禁用状态

```kotlin
var value by remember { mutableFloatStateOf(0.5f) }

Slider(
    value = value,
    onValueChange = { value = it },
    enabled = false
)
```

## 触觉反馈

**BREAKING**：`SliderDefaults.DefaultHapticEffect` 默认值由 `SliderHapticEffect.Edge` 改为
`SliderHapticEffect.Continuous`——ColorOS 滑杆在拖动中**每次数值变化**都震一次
（`VibrationFeedbackHandler` → `performHapticFeedback(305)`，线性马达 waveform 152），到达两端时用更重的反馈
（306 / waveform 154）。`Edge` 与 `Step` 仍然可用；`Step` 也由「关键点邻域内每帧触发」改为「**穿过**关键点时一次」。

**行为修复（RangeSlider）**：在选中区间**之外**按下时，现在抓取**最近的那只拇指**，不再整段平移；只有按在两只拇指
之间才整段拖动。这与 realOS 已在真机验证过的 roui `RouiRangeSlider` 一致。

Slider 支持触觉反馈，可以通过 `hapticEffect` 参数自定义反馈效果，详见 [SliderHapticEffect](./slider#sliderhapticeffect)。

```kotlin
var value by remember { mutableFloatStateOf(0.5f) }

Slider(
    value = value,
    onValueChange = { value = it },
    hapticEffect = SliderHapticEffect.Step
)
```

## 属性

### Slider 属性

| 属性名                | 类型                              | 说明                                                                                                             | 默认值                             | 是否必须 |
| --------------------- | --------------------------------- | ---------------------------------------------------------------------------------------------------------------- | ---------------------------------- | -------- |
| value                 | Float                             | 当前滑块的值。如果超出 valueRange，值将被强制限制在该范围内                                                      | -                                  | 是       |
| onValueChange | (Float) -> Unit | 点击在预览结束后提交；连续拖拽按有效值变化回调 | - | 是 |
| modifier              | Modifier                          | 应用于滑块的修饰符                                                                                               | Modifier                           | 否       |
| enabled               | Boolean                           | 是否启用滑块                                                                                                     | true                               | 否       |
| valueRange            | ClosedFloatingPointRange\<Float\> | 滑块可以采用的值范围。传递的值将被强制限制在此范围内                                                             | 0f..1f                             | 否       |
| steps                 | Int                               | 离散值的数量。如果为 0，滑块将连续运行。必须非负                                                                 | 0                                  | 否       |
| onValueChangeFinished | (() -> Unit)? | 点击或tracking结束；连续滑块若启动fling则等到惯性结束，重新按住会结束旧fling | null | 否 |
| reverseDirection      | Boolean                           | 控制滑块的方向。默认 false，方向跟随 LayoutDirection (LTR 时从左到右，RTL 时从右到左)。为 true 时反转方向。                                      | false                              | 否       |
| height                | Dp                                | 滑块的高度                                                                                                       | SliderDefaults.MinHeight           | 否       |
| colors                | SliderColors                      | 滑块的颜色配置                                                                                                   | SliderDefaults.sliderColors()      | 否       |
| hapticEffect          | SliderDefaults.SliderHapticEffect | 滑块的触感反馈类型                                                                                               | SliderDefaults.DefaultHapticEffect | 否       |
| showKeyPoints         | Boolean                           | 是否显示分段/关键点刻度。传了 `keyPoints` 用自定义点，否则用 `steps` 的位置；两者都没有时不显示                                                            | false                              | 否       |
| keyPoints             | List\<Float\>?                    | 要在滑块上显示的自定义关键点值。如果为 null,则使用 steps 参数的步长位置。值应在 valueRange 范围内                | null                               | 否       |
| magnetThreshold       | Float                             | 磁吸对齐阈值,以分数表示 (0.0 到 1.0)。当滑块值与关键点的距离在此阈值内时,将对齐到该点。仅在设置 keyPoints 时生效 | 0.02f                              | 否       |
| trackHeight | Dp | 轨道基础厚度；水平 Slider 的前景不随背景扩展 | SliderDefaults.CapsuleTrackHeight | 否 |
| showGlitterEffect | Boolean | 速度驱动的扫光：拖动速度决定强度，方向随 RTL 反转（官方 `GlitterEffectRenderer`） | false | 否 |
| showSpotLight | Boolean | 共享轨道聚光，受显式材质标记门控；不控制滑块阴影 | true | 否 |
| progressRange | IntRange? | 将 Float 值域映射到显式原生整数进度；null 不量化、不新增刻度 | null | 否 |
| thumbTextureEnabled | Boolean | 显式的滑块纹理材质标记；不自动读取OEM系统门控 | false | 否 |
| intentSize        | IntentSize?               | 细轨意图变体：`Small`(2/10/3/8dp)、`Default`(4/6/4/5dp)、`Large`(6/10/5/8dp)（轨道高/拉伸后背景高/滑块半径/滑块最大半径，对应 `coui_seekbar_intent_*`）；`null` 保持胶囊样式 | null |

> 轨道两端留白有**两条官方路径**：胶囊样式走 `COUISeekBarCore` 的 `(backgroundHeight × enlargeScale) / 2`（随背景拉伸变化），intent 样式走 `COUIIntentSeekBar` 读的绝对 `coui_seekbar_progress_padding_horizontal`(14dp) 与按压 4dp。`Slider` 按 `intentSize` 是否为 null 选择，两条都已实现。

### VerticalSlider 属性

| 属性名                | 类型                              | 说明                                                                                                             | 默认值                             | 是否必须 |
| --------------------- | --------------------------------- | ---------------------------------------------------------------------------------------------------------------- | ---------------------------------- | -------- |
| value                 | Float                             | 当前滑块的值                                                                                                     | -                                  | 是       |
| onValueChange         | (Float) -> Unit                   | 值变化时的回调函数                                                                                               | -                                  | 是       |
| modifier              | Modifier                          | 应用于滑块的修饰符                                                                                               | Modifier                           | 否       |
| enabled               | Boolean                           | 是否启用滑块                                                                                                     | true                               | 否       |
| valueRange            | ClosedFloatingPointRange\<Float\> | 滑块可以采用的值范围                                                                                             | 0f..1f                             | 否       |
| steps                 | Int                               | 离散值的数量                                                                                                     | 0                                  | 否       |
| onValueChangeFinished | (() -> Unit)?                     | 值变化结束时调用                                                                                                 | null                               | 否       |
| reverseDirection      | Boolean                           | 控制滑块的方向。false 时从下到上增加,true 时从上到下增加                                                         | false                              | 否       |
| width                 | Dp                                | 垂直滑块的宽度                                                                                                   | SliderDefaults.MinHeight           | 否       |
| colors                | SliderColors                      | 滑块的颜色配置                                                                                                   | SliderDefaults.sliderColors()      | 否       |
| effect                | Boolean                           | 是否显示特殊效果                                                                                                 | false                              | 否       |
| hapticEffect          | SliderDefaults.SliderHapticEffect | 滑块的触感反馈类型                                                                                               | SliderDefaults.DefaultHapticEffect | 否       |
| showKeyPoints         | Boolean                           | 是否显示分段/关键点刻度。传了 `keyPoints` 用自定义点，否则用 `steps` 的位置；两者都没有时不显示                                                            | false                              | 否       |
| keyPoints             | List\<Float\>?                    | 要在滑块上显示的自定义关键点值。如果为 null,则使用 steps 参数的步长位置。值应在 valueRange 范围内                | null                               | 否       |
| magnetThreshold       | Float                             | 磁吸对齐阈值,以分数表示 (0.0 到 1.0)。当滑块值与关键点的距离在此阈值内时,将对齐到该点。仅在设置 keyPoints 时生效 | 0.02f                              | 否       |
| trackWidth | Dp | 轨道基础厚度；水平 Slider 的前景不随背景扩展 | SliderDefaults.CapsuleTrackHeight | 否 |
| showSpotLight | Boolean | 近似阴影开关，非原生聚光 | true | 否 |

### RangeSlider 属性

| 属性名                | 类型                                        | 说明                                                                                                             | 默认值                             | 是否必须 |
| --------------------- | ------------------------------------------- | ---------------------------------------------------------------------------------------------------------------- | ---------------------------------- | -------- |
| value                 | ClosedFloatingPointRange\<Float\>           | RangeSlider 的当前值。如果任一值超出 valueRange，将被强制限制在范围内                                            | -                                  | 是       |
| onValueChange         | (ClosedFloatingPointRange\<Float\>) -> Unit | 值变化时的回调函数                                                                                               | -                                  | 是       |
| modifier              | Modifier                                    | 应用于滑块的修饰符                                                                                               | Modifier                           | 否       |
| enabled               | Boolean                                     | 是否启用滑块                                                                                                     | true                               | 否       |
| valueRange            | ClosedFloatingPointRange\<Float\>           | Range Slider 值可以采用的范围                                                                                    | 0f..1f                             | 否       |
| steps                 | Int                                         | 离散值的数量                                                                                                     | 0                                  | 否       |
| onValueChangeFinished | (() -> Unit)?                               | 值变化结束时调用                                                                                                 | null                               | 否       |
| height                | Dp                                          | 滑块的高度                                                                                                       | SliderDefaults.MinHeight           | 否       |
| colors                | SliderColors                                | 滑块的颜色配置                                                                                                   | SliderDefaults.sliderColors()      | 否       |
| hapticEffect          | SliderDefaults.SliderHapticEffect           | 滑块的触感反馈类型                                                                                               | SliderDefaults.DefaultHapticEffect | 否       |
| showKeyPoints         | Boolean                                     | 是否显示分段/关键点刻度。传了 `keyPoints` 用自定义点，否则用 `steps` 的位置；两者都没有时不显示                                                            | false                              | 否       |
| keyPoints             | List\<Float\>?                              | 要在滑块上显示的自定义关键点值。如果为 null,则使用 steps 参数的步长位置。值应在 valueRange 范围内                | null                               | 否       |
| magnetThreshold       | Float                                       | 磁吸对齐阈值,以分数表示 (0.0 到 1.0)。当滑块值与关键点的距离在此阈值内时,将对齐到该点。仅在设置 keyPoints 时生效 | 0.02f                              | 否       |
| trackHeight | Dp | 轨道基础厚度；水平 Slider 的前景不随背景扩展 | SliderDefaults.CapsuleTrackHeight | 否 |
| showSpotLight | Boolean | 近似阴影开关，非原生聚光 | true | 否 |
| minGap | Float | 区间两端的最小数值间距 | 0f | 否 |

### SliderDefaults 对象

SliderDefaults 对象提供了 Slider 组件的默认配置。

#### 常量

| 常量名              | 类型               | 默认值                  | 说明               |
| ------------------- | ------------------ | ----------------------- | ------------------ |
| MinHeight | Dp | 36.dp | 默认触摸容器厚度 |
| TickMarkRadius | Dp | 3.dp | 扩展关键点的半径 |
| CapsuleTrackHeight | Dp | 20.dp | 基础轨道厚度 |
| ThumbRestingRadius | Dp | 6.dp | 静止滑块半径 |
| ThumbExpandedRadius | Dp | 8.dp | 交互滑块半径 |
| IntentTrackHeight | Dp | 4.dp | Intent 样式度量，非默认轨道 |
| ThumbInScaleRadius | Dp | 3.67.dp | 保留的 Intent 样式度量 |
| ThumbShadowSize | Dp | 2.dp | 资源基础度量；不能等同于默认样式的 4dp 阴影 |
| DefaultHapticEffect | SliderHapticEffect | SliderHapticEffect.Continuous | 默认震动反馈类型（ColorOS 模型：每次数值变化一次 tick，两端更重） |
### SliderHapticEffect

| 值   | 说明                   |
| ---- | ---------------------- |
| None       | 无震动反馈 |
| Edge       | 只在拇指到达 `valueRange` 两端时震动 |
| Step       | 每次穿过关键点 / 步长时一次 tick，外加两端反馈 |
| Continuous | 拖动中每次数值变化一次 tick，两端用更重的反馈——ColorOS 滑杆模型（`VibrationFeedbackHandler`：拖动 305、两端 306、线性马达 152 / 154） |

#### 方法

| 方法名         | 返回类型     | 说明                   |
| -------------- | ------------ | ---------------------- |
| sliderColors() | SliderColors | 创建滑块的默认颜色配置 |

### SliderColors 类

| 属性名                  | 类型  | 说明                       |
| ----------------------- | ----- | -------------------------- |
| foregroundColor         | Color | 滑块的前景颜色             |
| disabledForegroundColor | Color | 禁用状态时滑块的前景颜色   |
| backgroundColor         | Color | 滑块的背景颜色             |
| disabledBackgroundColor | Color | 禁用状态时滑块的背景颜色   |
| thumbColor              | Color | 滑块拇指的颜色             |
| disabledThumbColor      | Color | 禁用状态时拇指的颜色       |
| keyPointColor           | Color | 滑块右侧刻度的颜色（`?attr/couiColorLabelTertiary`：#42000000 / #4DFFFFFF） |
| keyPointForegroundColor | Color | 滑块左侧刻度的颜色（`?attr/couiColorLabelOnColor`：#FFFFFF） |
| keyPointDisabledColor   | Color | 禁用时右侧刻度（`coui_seekbar_inactive_mark_disable_color`：#42000000 / #29FFFFFF） |
| keyPointForegroundDisabledColor | Color | 禁用时左侧刻度（`coui_seekbar_active_mark_disable_color`：#8AFFFFFF / #29FFFFFF） |
| thumbShadowColor | Color | 滑块阴影颜色，亮/暗为 #1A000000 / #40000000 |
| glitterMinColor | Color | 预留细闪颜色；目前未绘制 |
| glitterMaxColor | Color | 预留细闪颜色；目前未绘制 |

## 进阶用法

### 自定义数值范围

```kotlin
var temperature by remember { mutableFloatStateOf(25f) }

Column {
    Text("温度: ${temperature.roundToInt()}°C")
    Slider(
        value = temperature,
        onValueChange = { temperature = it },
        valueRange = 16f..32f
    )
}
```

### 离散步长

```kotlin
var rating by remember { mutableFloatStateOf(3f) }

Column {
    Text("评分: ${rating.roundToInt()}/5")
    Slider(
        value = rating,
        onValueChange = { rating = it },
        valueRange = 1f..5f,
        steps = 3  // 创建 5 个离散值: 1, 2, 3, 4, 5
    )
}
```

### 自定义颜色

```kotlin
var volume by remember { mutableFloatStateOf(0.7f) }

Slider(
    value = volume,
    onValueChange = { volume = it },
    colors = SliderDefaults.sliderColors(
        foregroundColor = Color.Red,
        backgroundColor = Color.LightGray
    )
)
```

### 自定义高度

```kotlin
var brightness by remember { mutableFloatStateOf(0.8f) }

Slider(
    value = brightness,
    onValueChange = { brightness = it },
    height = 40.dp
)
```

### 带触感反馈的滑块

```kotlin
var value by remember { mutableFloatStateOf(0.5f) }

Slider(
    value = value,
    onValueChange = { value = it },
    hapticEffect = SliderDefaults.SliderHapticEffect.Step
)
```

### 反向方向的 VerticalSlider

```kotlin
var volume by remember { mutableFloatStateOf(0.5f) }

VerticalSlider(
    value = volume,
    onValueChange = { volume = it },
    modifier = Modifier.height(200.dp),
    reverseDirection = true  // 从上到下
)
```

### 用于价格筛选的 RangeSlider

```kotlin
var priceRange by remember { mutableStateOf(100f..500f) }

Column {
    Text("价格: ¥${priceRange.start.roundToInt()} - ¥${priceRange.endInclusive.roundToInt()}")
    RangeSlider(
        value = priceRange,
        onValueChange = { priceRange = it },
        valueRange = 0f..1000f,
        steps = 99  // 从 0 到 1000 的 101 个离散值
    )
}
```

### 完整示例（含值变化回调）

```kotlin
var value by remember { mutableFloatStateOf(0.5f) }
var finalValue by remember { mutableFloatStateOf(0.5f) }

Column {
    Text("当前值: $value")
    Text("最终值: $finalValue")
    Slider(
        value = value,
        onValueChange = { value = it },
        onValueChangeFinished = { finalValue = value },
        valueRange = 0f..100f,
        steps = 99
    )
}
```

### 自定义关键点的滑块

```kotlin
var value by remember { mutableFloatStateOf(50f) }

Column {
    Text("值: ${value.roundToInt()}")
    Slider(
        value = value,
        onValueChange = { value = it },
        valueRange = 0f..100f,
        showKeyPoints = true,
        keyPoints = listOf(0f, 25f, 50f, 75f, 100f),
        magnetThreshold = 0.05f,  // 5% 磁吸阈值
        hapticEffect = SliderDefaults.SliderHapticEffect.Step
    )
}
```

### 带关键点的 RangeSlider

```kotlin
var range by remember { mutableStateOf(20f..80f) }

Column {
    Text("范围: ${range.start.roundToInt()} - ${range.endInclusive.roundToInt()}")
    RangeSlider(
        value = range,
        onValueChange = { range = it },
        valueRange = 0f..100f,
        showKeyPoints = true,
        keyPoints = listOf(0f, 20f, 40f, 60f, 80f, 100f),
        magnetThreshold = 0.03f
    )
}
```

> 官方所谓"气泡"其实不是气泡：`seekbar.TextDrawable` 画的是**透明**方框（`getPaint().setColor(0)`），只绘制右对齐文本。宽度硬编码 **144px**（不是 dp —— `getIntrinsicWidth()` 返回的是裸像素）、字号 12sp、方框 16dp + 底部 6dp、尾部 4dp；`COUISeekBarCore.setValueForLabel()` 把它锚在轨道右端的上方。已作为 `SliderDefaults.ValueLabelWidthPx` / `ValueLabelTextSize` / `ValueLabelHeight` / `ValueLabelMarginBottom` / `ValueLabelPaddingEnd` 暴露，覆盖层本身尚未绘制。
## 扫光效果

`showGlitterEffect = true` 复刻 `GlitterEffectRenderer`。它**由速度驱动**，不是常驻渐变：每次拖动都会重新起一个弹簧，起点是当前驱动值、目标是 `0`、初速度是 `|拖动速度|`（`response = 0.6`、`bounce = 0`）。可见 alpha 为 `1 - exp(-(ln 85 / 360) * 驱动值)`，也就是手指快速移动时闪一下、弹簧归零后消失。

叠加层是活动轨道上的水平渐变，`#00FFFFFF` → `#A0FFFFFF`（`coui_seekbar_glitter_effect_{min,max}_color`），RTL 时反向。只有水平 `Slider` 绘制它；`VerticalSlider` 与 `RangeSlider` 已不再保留该参数。

## 分段刻度滑块（`COUISectionSeekBar`）

ColorOS 把带刻度的滑块做成独立类 `COUISectionSeekBar`（`COUISeekBar` 的子类），用在字体大小、显示大小、
色温、运行内存扩展这类档位型设置上。这里不需要单独的组件：`showKeyPoints` + `keyPoints` + `magnetThreshold`
已经画出同样的刻度并吸附到刻度点。

它提供的官方数值：

| 项 | 值 | 来源 |
| :--- | :--- | :--- |
| 刻度半径 | **3dp** | `coui_section_seekbar_tick_mark_radius` |
| 已走过区域的刻度 | **#FFFFFF** | `coui_seekbar_active_mark_selector` → `?attr/couiColorLabelOnColor` |
| 未走到的刻度 | **#42000000 / #4DFFFFFF** | `coui_seekbar_inactive_mark_selector` → `?attr/couiColorLabelTertiary` |
| 禁用时已走过的刻度 | **#8AFFFFFF / #29FFFFFF** | `coui_seekbar_active_mark_disable_color` |
| 禁用时未走到的刻度 | **#42000000 / #29FFFFFF** | `coui_seekbar_inactive_mark_disable_color` |

官方按区间内每个整数步长画一个刻度，并隐藏被滑块盖住的那几个；这里是按 `keyPoints`（`keyPoints` 为 null 时
按步长位置）绘制，被滑块挡住的部分由滑块自身覆盖。
