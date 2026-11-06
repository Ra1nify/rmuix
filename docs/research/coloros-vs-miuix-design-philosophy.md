# ColorOS 17 (COUI) 与 MIUIX 全局设计理念深度对比调研报告

本报告立足于 ColorOS 17 真实系统包（Settings.apk / SystemUI.apk）反编译源码、XML 布局以及真机 dump 数据，系统性总结 ColorOS 17 (COUI) 与 MIUIX 在架构、材质、色彩、动效与控件选型上的四大全局设计理念差异。

---

## 1. 材质与光影：动态 AGSL 着色器 vs 静态高斯模糊

| 维度 | ColorOS 17 (COUI) | MIUIX (MIUI / HyperOS) |
| :--- | :--- | :--- |
| **材质渲染** | **动态 AGSL 着色器 (RuntimeShader)**<br>核心类：`COUIMaterialBlurEffect`、`COUIMaterialStrokeEffect` | **静态/基础 RenderNode 高斯模糊**<br>标准 `RenderEffect.createBlurEffect` |
| **边缘与描边** | **光泽与微光描边 (Material Stroke)**<br>根据窗口焦距与光照角度动态合成边缘光泽 | 静态半透明/实心 1dp 描边 |
| **光效微粒** | **细闪 (Glitter) 与 聚光灯 (SpotLight)**<br>如 Slider 激活轨道的流动细闪粒子 (`GlitterEffectRenderer`)，Thumb 的 3D 聚光高光环 (`SpotLightEffectRenderer`) | 无粒子光效，单色填充或简单渐变 |
| **阴影与水波** | **水波焦散阴影 (Caustic Shadow)**<br>卡片与对话框悬浮时渲染具有光学折射感的自然弥散阴影 | 标准 Android 扁平 Elevation 高斯阴影 |

---

## 2. 色彩策[build-logic](../../build-logic)略：半透明中性色 (Neutral Alpha) vs 实心分界

| 维度 | ColorOS 17 (COUI) | MIUIX (MIUI / HyperOS) |
| :--- | :--- | :--- |
| **未激活状态色** | **严格采用半透明中性色 (Neutral Alpha)**<br>亮色：`12%~20% 黑色` (`#1F000000`)<br>暗色：`15%~30% 白色` (`#33FFFFFF`) | **常用实心二次色 (Opaque Secondary)**<br>使用不透明的灰色/白底色（如 `colorScheme.secondary`） |
| **融合感** | **强调透明度与背景自然融合**<br>未选中轨迹、卡片背景、次要按钮均透出下层背景色 | **强调明确的色彩分界**<br>使用实心色块进行区域切割 |
| **禁用态** | 半透明降低（`8% 黑色` / `15% 白色`） | 降低整体 Alpha 变灰 |

---

## 3. 物理动效与触感：跟手阻尼与边缘拉伸 vs 三次方贝塞尔

| 维度 | ColorOS 17 (COUI) | MIUIX (MIUI / HyperOS) |
| :--- | :--- | :--- |
| **手势跟手** | **物理跟手阻尼 (Flexible Follow-Hand)**<br>核心类：`FlexibleFollowHandHandler` + `PhysicsAnimationHandler`<br>拖拽点与控件存在阻尼平滑追随 | 实时 1:1 指针位置映射 |
| **极值反馈** | **边缘撞击拉伸 (Edge Bumping Deformation)**<br>滑拽到达 0% 或 100% 边界时控件发生弹性拉伸形变（Deformation），松开回弹 | 到了边界直接静止或简单过冲 |
| **触感震动** | **分级细粒度触感 (VibrateUtils)**<br>按压、拖拽、跨越刻度、触顶各有不同的振幅与频率 | 标准 Haptic Feedback |

---

## 4. 控件选型与调用频率：高频真实控件 vs 弃用旧类

在 COUI 控件库（`com.coui.appcompat.*`）中，存在多代演进遗留的类。**在调研与重构时，必须以 Settings.apk 与 SystemUI.apk 实际 Layout XML 中高频调用的类为唯一准则**：

1. **Slider / SeekBar 选型**：
   - 高频调用：`COUISeekBar` (胶囊形态，高频用于设置/控制中心)
   - 细轨形态：`COUIIntentSeekBar`
   - 底层基类/弃用类：`COUISeekBarCore` / `COUISeekBarAdvanced` (仅作为继承基类，不直接实例化)
2. **Dialog / Panel 选型**：
   - 高频调用：`COUIAlertDialogBuilder` (圆角 32dp Squircle, 最大宽 328dp)、`COUIBottomSheetDialog`
3. **Preference 选型**：
   - 高频调用：`COUIPreference` (三行布局，48dp 行高)
