# Glass 悬浮导航栏

`GlassFloatingNavigationBar` 是带 ColorOS 玻璃材质的
[FloatingNavigationBar](../navigation/navigationbar)。材质来自 `rmuix-blur` 的
`colorOSLiquidGlass(ColorOSGlassType.BottomBar)` 预设（`COUIMaterialBlurEffect` 的两层混合），
不为该组件发明任何颜色。

其余全部保持官方尺寸：胶囊半径（`FloatingNavigationBarDefaults.CornerRadius`，即 56dp 高度的一半）、
阴影、48dp 指示器与各项目度量。

## 用法

在场景根部安装一次 backdrop（`LocalGlassBackdrop`），然后直接使用玻璃导航栏：

```kotlin
Box(Modifier.fillMaxSize().layerBackdrop(backdrop)) {
    Scaffold(
        bottomBar = {
            GlassFloatingNavigationBar {
                FloatingNavigationBarItem(selected = true, onClick = {}, icon = {}, label = "首页")
                FloatingNavigationBarItem(selected = false, onClick = {}, icon = {}, label = "设置")
            }
        },
    ) { /* 内容 */ }
}
```

导航栏采样背后的 backdrop；若没有（`LocalGlassBackdrop.current == null` 且未显式传入
`backdrop`），则回退为实色 `color`，与其他玻璃组件一致。

## 平台说明

材质与导航栏在同一场景内绘制，因此在支持 runtime shader 的所有 Compose Multiplatform
平台上都可用；`isRuntimeShaderSupported()` 为 false 时 `rmuix-blur` 自身会跳过 backdrop 效果，
导航栏仍然正常绘制阴影与内容。

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

- 官方悬浮导航栏可通过 `couiFloatingToolbarBackgroundBlur` 开启材质模糊；本模块在有
  backdrop 时固定使用 bottom-bar 预设，不自行发明半径与颜色组合；
- 胶囊与侧边按钮之间的「融球」桥接仍是现有 `meltEffectMode` 近似，本包装未修改。
