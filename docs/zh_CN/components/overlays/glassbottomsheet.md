# Glass Bottom Sheet 玻璃底部弹层

底部弹层有自己的材质 token：模糊半径是 **400px**（对话框 200px 的两倍），
因为弹层要透过更高的表面取模糊；mix/blend 两组颜色也不同：

| token | 值 | 作用 |
| :--- | ---: | :--- |
| `coui_bottomdialog_blur_radius` | 400（px） | 背板模糊半径（`OplusBlurParam.setBlurRadius`） |
| `coui_bottomdialog_list_mix_blur_light` | `#dcc8c8c8` | 浅色混合色 |
| `coui_bottomdialog_list_mix_blur_dark` | `#a4515151` | 深色混合色 |
| `coui_bottomdialog_list_blend_blur_light` | `#376e6e6e` | 浅色混合层 |
| `coui_bottomdialog_list_blend_blur_dark` | `#b30f0f0f` | 深色混合层 |

对应 `GlassDefaults` 中的 `BOTTOM_SHEET_BLUR_RADIUS_PX`、`BottomSheetMixLight/Dark`、
`BottomSheetBlendLight/Dark` 与 `bottomSheetBlurColors(isDark)`。

## GlassOverlayBottomSheet

契约与 [GlassOverlayDialog](./glassdialog) 相同：采样通过 `LocalGlassBackdrop` 安装的（或显式传入的）backdrop，
且只圆上两个角。

```kotlin
val backdrop = rememberGlassBackdrop()

Box(Modifier.fillMaxSize().layerBackdrop(backdrop)) {
    AppContent()

    GlassOverlayBottomSheet(
        backdrop = backdrop,
        show = showSheet,
        title = "分享",
        onDismissRequest = { showSheet = false },
    ) {
        Text("弹层内容")
    }
}
```

## GlassWindowBottomSheet

窗口变体无法采样场景，因此向平台要求窗口模糊（Android 12+）并在上面叠着色。
Desktop / Web / iOS 与 [GlassWindowDialog](./glassdialog) 一样，只有着色。

```kotlin
GlassWindowBottomSheet(
    show = showSheet,
    title = "分享",
    onDismissRequest = { showSheet = false },
) {
    Text("弹层内容")
}
```

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

- 官方 material 模式同样用两层 `SrcOver` 近似；
- 底部弹层的圆角不同于对话框：`BottomSheetDefaults.cornerRadius`（32dp）只作用在上两角，
  玻璃剪載同样遵循这个几何。
