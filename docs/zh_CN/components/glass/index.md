# Glass 组件

`rmuix-glass` 提供 ColorOS 17 的**玻璃材质**表面。它是唯一依赖 `rmuix-blur` 的模块，
因此核心 `rmuix-ui` 保持无 blur 依赖；只有真正需要磨砂表面时才引入它。

本模块的每个数值都来自 ColorOS 资源（`coui_dialog_blur_radius` 200、
`coui_dialog_list_mix_blur_*`、`coui_dialog_list_blend_blur_*`、`?attr/couiColorSurface`），但材质本身是**近似实现**：
官方走的是私有 `com.oplus.graphics.OplusBlurParam`，第三方应用无法调用。

| 组件 | 说明 | 备注 |
| :--- | :--- | :--- |
| [GlassOverlayDialog](../overlays/glassdialog) | 带 backdrop 采样模糊的 `OverlayDialog` | 精确材质路径：对话框与内容同一场景 |
| [GlassWindowDialog](../overlays/glassdialog) | 接平台窗口模糊的 `WindowDialog` | 仅 Android 12+；其他平台保留着色、无模糊 |
| [GlassOverlayBottomSheet](../overlays/glassbottomsheet) | 带 backdrop 采样模糊的 `OverlayBottomSheet` | 精确材质路径（400px） |
| [GlassWindowBottomSheet](../overlays/glassbottomsheet) | 接平台窗口模糊的 `WindowBottomSheet` | 仅 Android 12+；其他平台仅着色 |
| [GlassFloatingNavigationBar](../overlays/glassfloatingnavigationbar) | 带 ColorOS bottom-bar 材质的悬浮胶囊导航栏 | 采样 `LocalGlassBackdrop`；无 backdrop 时实色兜底 |
