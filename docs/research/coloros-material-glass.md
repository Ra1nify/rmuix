# ColorOS 17 液态玻璃 (Liquid Glass / Blur Material) 深度调研与对比报告

本文档对 ColorOS 17 的基类材质——**液态玻璃 (Liquid Glass Material / COUIMaterialBlurEffect)** 进行源码级解析，并与 MIUI 材质体系及 `rmuix-blur` / `rmuix-shader` 进行循环比对，提炼出两者在渲染管道、混合模式、参数阶梯及降级策略上的异同与桥接方案。

> **注意**：本调研文档遵循“只调研、不修改代码”的原则，为后续全库控件 1:1 复刻 ColorOS 液态玻璃效果提供理论与参数依据。

---

## 1. ColorOS 液态玻璃 (COUIMaterialBlurEffect) 源码机制

从 COUI 反编译源码 `com.coui.appcompat.COUIMaterialBlurEffect.java` 可以看出，ColorOS 17 的液态玻璃并非单层高斯模糊，而是**基于 Android API 33+ `RenderEffect` 的“双层色彩混合+镜像拓界”链式管道**：

### 1.1 渲染管道 (Render Pipeline)
```java
// 1. 基础高斯模糊 (TileMode.MIRROR 镜像拓界，防止边缘黑边)
RenderEffect blur = RenderEffect.createBlurEffect(radius, radius, Shader.TileMode.MIRROR);

// 2. 第一层色彩混合 (Color Dodge 减淡高光层)
RenderEffect layer1 = RenderEffect.createColorFilterEffect(
    new BlendModeColorFilter(colorLayer1, blendModeLayer1), blur
);

// 3. 第二层色彩混合 (Luminosity 亮度调整层，链式叠加)
RenderEffect layer2 = RenderEffect.createColorFilterEffect(
    new BlendModeColorFilter(colorLayer2, blendModeLayer2), layer1
);

// 4. 应用到 View 背景
OplusViewBackgroundRenderEffect.setBackgroundRenderEffect(layer2, view);
```

### 1.2 核心预设参数表 (Presets Matrix)

ColorOS 在 `COUIMaterialBlurEffect` 中预置了不同控件档位的液态玻璃参数：

| 档位类型 (`BlurEffectType`) | 亮色模式 Radius | 亮色 Layer 1 (BlendMode) | 亮色 Layer 2 (BlendMode) | 暗色模式 Radius | 暗色 Layer 1 (BlendMode) | 暗色 Layer 2 (BlendMode) |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| **`TYPE_FRAMEWORK_TOP_BAR_BLUR`** (顶栏) | 20.0f | `#66333333` (`ColorDodge`) | `#99F5F5F5` (`Luminosity`) | 20.0f | `#CC262626` (`SrcOver`) | `#B37F7F7F` (`Overlay`) |
| **`TYPE_FRAMEWORK_BOTTOM_BAR`** (底栏) | 25.0f | `#B3666666` (`ColorDodge`) | `#99F5F5F5` (`Luminosity`) | 25.0f | `#80666666` (`Overlay`) | `#B3262626` (`Luminosity`) |
| **`TYPE_CONTENT_SECONDARY_BUTTON`** (次要按钮) | 25.0f | `#CCFFFFFF` (`SrcOver`) | `#00FFFFFF` (`SrcOver`) | 25.0f | `#33FFFFFF` (`SrcOver`) | `#00FFFFFF` (`SrcOver`) |
| **`TYPE_CONTENT_CHIP_TAB`** (标签/段落) | 40.0f | `#144E4E4E` (`ColorDodge`) | `#14606060` (`Luminosity`) | 40.0f | `#33B2B2B2` (`Luminosity`) | `#33303030` (`Overlay`) |
| **`TYPE_CONTENT_CHIP_TAB_SELECTED`** (选中标签) | 40.0f | `#66A3A1A1` (`ColorDodge`) | `#99F5F5F5` (`Luminosity`) | 40.0f | `#A6505050` (`Luminosity`) | `#4CA7A7A7` (`Overlay`) |

---

## 2. ColorOS 液态玻璃 vs MIUI / `rmuix-blur` 异同比对

### 2.1 相同点 (Similarities)
1. **背景捕获与实时模糊**：两者均基于底层 Canvas / Backdrop 实时抓取像素，采用高斯模糊（Gaussian Kernel）生成通透底色。
2. **多层色彩叠加**：两者都不是单纯变透明，都需要叠加一层或多层半透明色彩以保证上层文字的可读性与对比度。
3. **分档模糊半径**：两者都对不同层级（顶栏 20dp、底栏 25dp、浮动卡片 40dp）使用不同的模糊半径。

### 2.2 不同点 (Differences & Gaps)

| 维度 | ColorOS 液态玻璃 (`COUI`) | MIUI / `rmuix-blur` | 差异与启示 |
| :--- | :--- | :--- | :--- |
| **混合模式 (Blend Mode)** | 极度依赖 **`COLOR_DODGE` (颜色减淡)** 与 **`Luminosity` (亮度)** | 多采用 `SrcOver` 或自定义 AGSL 线性调整 (`LinearLight`) | ColorOS 依靠 `COLOR_DODGE` 产生背景加亮的“液态光感”，比普通的半透明灰色更具通透感 |
| **边缘拓界 (Tile Mode)** | 显式使用 `Shader.TileMode.MIRROR` | 自定义 Sampling Padding 扩充 | `MIRROR` 模式能有效避免边缘出现黑边/采样缩窄问题 |
| **噪点与纹理 (Noise)** | 无微粒噪点/暗纹 | 含有 `NoiseCoefficient` (0.0045f) | ColorOS 追求极其平滑的纯净液态感，不使用噪点 Shader |
| **高光与阴影边缘** | 结合 `COUIMaterialStrokeEffect` 绘制 `0.2~0.6` Alpha 的边缘高光描边 | 普通 Border | ColorOS 液态玻璃卡片边缘有一层精致的线性减淡高光边 |
| **降级策略 (Fallback)** | 无法开启 Blur 时，降级为 `coui_color_container8` (`#14000000` / `#1AFFFFFF`) | 降级为纯色/半透明灰色 | 降级色彩保持一致的中性透明度 |

---

## 3. 在 `rmuix` 中的桥接与实装方案 (Implementation Strategy)

为使 `rmuix` 在全平台（Android + Desktop/Skiko）上能 1:1 复刻 ColorOS 液态玻璃，同时完美兼顾性能与降级，可采用如下桥接架构：

```kotlin
// 利用 rmuix-blur 已有的 BlurColors 与 BlendColorEntry 拟合 ColorOS 17 液态玻璃
fun BackdropEffectScope.colorOSLiquidGlass(
    type: ColorOSGlassType,
    isDark: Boolean,
) {
    val params = ColorOSGlassDefaults.getParams(type, isDark)
    
    // 1. 设置高斯模糊半径 + 镜像拓界
    blur(radiusX = params.radius, radiusY = params.radius)
    
    // 2. 链式叠加 ColorOS 双层混合模式 (Layer 1: ColorDodge / Layer 2: Luminosity)
    color(
        BlurDefaults.blurColors(
            blendColors = listOf(
                BlendColorEntry(params.colorLayer1, params.blendModeLayer1),
                BlendColorEntry(params.colorLayer2, params.blendModeLayer2),
            )
        )
    )
}
```

### 预设类型 (`ColorOSGlassType`)：
- `TopBar` (半径 20dp)
- `BottomBar` (半径 25dp)
- `ButtonSecondary` (半径 25dp)
- `ChipTab` (半径 40dp)
- `DialogPanel` (半径 28dp)

---

## 4. 总结与后续指导

1. **ColorOS 的核心精髓在于 `ColorDodge`（减淡高光）与 `Luminosity`（亮度）的结合**，使得背景在被模糊的同时呈现出充满光泽感的液态卡片质感。
2. 在接下来的组件深度重构中（如 `Button` SECONDARY、`TabRow` 胶囊、`TopAppBar` 顶栏等），当需要启用背景模糊时，将统一采用本调研得出的 ColorOS 双层 BlendMode 参数。
