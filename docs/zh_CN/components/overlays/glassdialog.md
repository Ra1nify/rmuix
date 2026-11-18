# Glass Dialog 玻璃对话框

ColorOS 17 的对话框背板由 `COUIBackgroundBlurBuilder` 绘制：对背后内容做 200px 模糊，再叠两层
着色。它建在私有 `com.oplus.graphics` API（`OplusBlurParam.setMaterialParams`）上，
因此本模块用 `rmuix-blur` 的 `drawBackdrop` 管线重新演绎同一组 token——**数值忠实、渲染近似**。

## 官方 token

| token | 值 | 作用 |
| :--- | ---: | :--- |
| `coui_dialog_blur_radius` | 200（px） | `OplusBlurParam.setBlurRadius` 的背板模糊半径 |
| `coui_dialog_list_mix_blur_light` | `#dc909090` | 浅色混合色 |
| `coui_dialog_list_mix_blur_dark` | `#8c777777` | 深色混合色 |
| `coui_dialog_list_blend_blur_light` | `#509f9f9f` | 浅色混合层 |
| `coui_dialog_list_blend_blur_dark` | `#b2292929` | 深色混合层 |
| `?attr/couiColorSurface` | `#ffffff` / `#1e1e1e` | 无模糊时的实色兜底 |

它们在 [GlassDefaults] 里对应 `DIALOG_BLUR_RADIUS_PX`、`DialogMixLight/Dark`、`DialogBlendLight/Dark`、
`DialogFallbackLight/Dark` 与 `dialogBlurColors(isDark)`。

## GlassOverlayDialog（精确路径）

overlay 对话框画在主场景内，因此可以直接采样背后内容的 `LayerBackdrop`。两边必须用同一个 backdrop：

```kotlin
val backdrop = rememberGlassBackdrop()

Box(Modifier.fillMaxSize().layerBackdrop(backdrop)) {
    AppContent()

    GlassOverlayDialog(
        backdrop = backdrop,
        show = showDialog,
        title = "删除这个条目？",
        summary = "此操作无法撤销。",
        confirmButtonText = "删除",
        confirmButtonDestructive = true,
        onConfirm = { showDialog = false },
        dismissButtonText = "取消",
        onDismiss = { showDialog = false },
    ) {}
}
```

`rememberGlassBackdrop()` 必须包住**对话框背后**的内容，并用 `Modifier.layerBackdrop`
应用到它上；对话框本身只做采样。布局、字体、按钮栏、关闭行为与
[OverlayDialog](./overlaydialog) 完全一致，只换表面。

## GlassWindowDialog（平台模糊路径）

window 对话框在独立窗口中，无法采样 Activity 场景，因此改为向平台要求背景模糊：

| 平台 | 行为 |
| :--- | :--- |
| Android 12+ | `Window.setBackgroundBlurRadius(200)` + `FLAG_BLUR_BEHIND`，再叠官方半透明着色 |
| Desktop / Web / iOS | 没有平台窗口模糊：只画着色，背后内容**不会**被模糊 |

布局、颜色、按钮与 [WindowDialog](./windowdialog) 相同；只有材质不同，
非 Android 平台退化为半透明面板。

## 自定义材质

`blurRadiusPx` 与 `colors` 直接透传：

```kotlin
GlassOverlayDialog(
    backdrop = backdrop,
    show = showDialog,
    title = "自定义材质",
    blurRadiusPx = 120f,
    colors = GlassDefaults.dialogBlurColors(isDark = true),
) {}
```

`colors = null`（默认）时从当前 `RmuixTheme` 调色板推导浅/深色 token。

## 平台能力分档

`rmuix-blur` 按两级降级，而不是要求高 minSdk；这对**所有**模糊入口生效
（`drawBackdrop`、`textureBlur`、`textureEffect`、`colorOSLiquidGlass` 等），不仅限于 glass 组件：

| 平台 | 渲染器 | 行为 |
| :--- | :--- | :--- |
| Android 13+（`isRuntimeShaderSupported()`）、skiko | AGSL 管线 | 完整材质：多次采样高斯 + mix/blend 两层 |
| Android 12 / 12L（`isRenderEffectSupported()`） | `RenderEffect` | 对录制的 backdrop 做普通高斯模糊 + `SrcOver` 着色层 |
| Android 11 及以下 | 仅着色 | 不模糊：把捕获到的 `SrcOver` 层画在透明表面上，面板仍然带官方半透明色 |
| 无 backdrop | 实色 | 组件画自己的不透明/半透明兜底色 |

`isBackdropEffectSupported()` 告知是否存在任何模糊分档；`Modifier.legacyBackdropBlur(...)`
是供需要显式使用 RenderEffect 路径的调用方使用的助手。

库声明 `minSdk 24`（`BuildConfig.MIN_SDK`），老设备仍能编译与使用，丢的只是模糊，
布局、颜色与按钮态都不变。

降级分档的限制：没有 shader 时只能重现 `SrcOver` 着色层（自定义混合模式与颜色调节会被跳过），
平台模糊的衰减曲线也不同于 shader 管线的多次采样高斯，属于近似。

## 已知差异

- 官方的 material 模式（`OplusBlurParam.setMaterialParams(mode, blendColor, mixColor)`）在本库里用两层
  `SrcOver` 混合近似，并未复现其 shader 函数；
- 窗口模糊依赖系统实现，部分厂商 ROM 不支持；
- 官方模糊半径是像素值，不随屏幕密度变化，因此参数明确以 px 为单位。
