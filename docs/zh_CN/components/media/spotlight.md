# 共享 Spotlight

公共效果位于rmuix-shader。所有rmuixSpotlight调用复用同一个有官方信源的COUI光照内核；每个实例独立持有uniform、指针与动画，现有运行时后端只缓存不可变的编译结果，不共享可变uniform。

## 使用

~~~kotlin
import io.github.ra1nify.rmuix.kmp.shader.SpotlightStyle
import io.github.ra1nify.rmuix.kmp.shader.rmuixSpotlight

Modifier.rmuixSpotlight(
    shape = CircleShape,
    style = SpotlightStyle.OpaqueMedium,
    darkTheme = false,
)
~~~

| 参数 | 含义 | 默认值 |
| --- | --- | --- |
| glowColor | 物理光源RGB；alpha作为库扩展缩放强度 | Color.White |
| lightRadius（Float） | 原始像素的终点半径覆盖，NaN选用预设 | Float.NaN |
| lightRadius（Dp重载） | 显式按density换算的终点半径覆盖 | 必填 |
| shape | 聚光裁剪轮廓，null裁剪到组件矩形边界 | null |
| enabled | 启用效果与不消费事件的指针观察器 | true |
| style | 官方参数族，而不是不同shader | OpaqueMedium |
| darkTheme | 显式主题映射，null跟随宿主系统主题 | null |

Float/Dp两个旧重载的前四个参数名、顺序与可调用性保留。显式半径只改终点半径，不缩放预设光源高度等参数；半径小于光源高度时，按原生模型可能完全不发光。这里的黑色不是低透明黑色按压遮罩。

## 官方预设

| Style | 亮/暗峰值强度 | 亮/暗混合 |
| --- | --- | --- |
| Capsule | 1 / 1 | Overlay / Overlay |
| Circle | .2 / .2 | Plus / Plus |
| RoundedRectangle | 1.3 / .4 | Plus / Plus |
| Switch | .8 / .8 | Overlay / Overlay |
| ChipTab | .2 / .3 | Plus / SrcOver |
| OpaqueSmall | .6 / .5 | Overlay / Overlay |
| OpaqueMedium | .6 / .8 | Overlay / Overlay |
| OpaqueSecondary | 1 / .15 | Overlay / Plus |
| TransparentMedium | .5 / .3 | Overlay / Overlay |

数据来自Settings APK的COUISpotLightEffect。公共入口默认OpaqueMedium，不等于每个控件都该选它；各控件精确预设与系统材质门控仍需分别核对，不能全局套上shader就宣布控件1:1。

## 绘制与动效

- 使用官方打包uniform的3D光照模型：环境/漫反射、距离衰减、亮度alpha、soft-light噪声与抖动；不再保留幂次白光斑或径向渐变替代品。
- 在内容之后按预设混合，修正旧实现被不透明背景盖住的问题。
- 点亮response=.15，熄灭=.4，归一化XY跟手response=.35、bounce=.15；熄灭额外扩散半径，保留源码动画尺度阈值。
- 新增手指接管光源，移除当前手指后选原生备用指针；这刻意不同于Slider数值跟踪的索引0规则。观察器不消费事件。
- Android要求RuntimeShader与硬件Canvas；不支持或shader创建失败时跳过，不画假回退。Skia通过现有运行时抽象使用同一内核。

**BREAKING：** 默认tint由低透明黑遮罩改为白光；自动半径由380px改为所选预设的原始像素值；组件内部40/140/380dp覆盖已移除。时序、混合与绘制顺序按官方模型改变。自定义tint/半径仍是扩展，不代表stock验收。

本轮只静态核对。原生越界skip-to-end优化、精确帧时钟、复杂父级取消/跨节点指针过滤、变换图层与系统特性/功耗门控仍未完整核验。不表示构建、执行测试或实机验收通过。
