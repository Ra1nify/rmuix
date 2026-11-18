# Glass Bottom Sheet

The bottom sheet uses its own material tokens - the blur radius is **400px** (twice the dialog's 200px)
because the sheet is blurred through a taller surface, and the mix/blend pairs differ:

| Token | Value | Role |
| :--- | ---: | :--- |
| `coui_bottomdialog_blur_radius` | 400 (px) | Backdrop blur radius (`OplusBlurParam.setBlurRadius`) |
| `coui_bottomdialog_list_mix_blur_light` | `#dcc8c8c8` | Light-mode mix colour |
| `coui_bottomdialog_list_mix_blur_dark` | `#a4515151` | Dark-mode mix colour |
| `coui_bottomdialog_list_blend_blur_light` | `#376e6e6e` | Light-mode blend colour |
| `coui_bottomdialog_list_blend_blur_dark` | `#b30f0f0f` | Dark-mode blend colour |

They are exposed as [GlassDefaults]: `BOTTOM_SHEET_BLUR_RADIUS_PX`, `BottomSheetMixLight/Dark`,
`BottomSheetBlendLight/Dark`, plus `bottomSheetBlurColors(isDark)`.

## GlassOverlayBottomSheet

Same contract as [GlassOverlayDialog](./glassdialog): the sheet samples the backdrop installed through
`LocalGlassBackdrop` or passed explicitly, and only the top corners are rounded.

```kotlin
val backdrop = rememberGlassBackdrop()

Box(Modifier.fillMaxSize().layerBackdrop(backdrop)) {
    AppContent()

    GlassOverlayBottomSheet(
        backdrop = backdrop,
        show = showSheet,
        title = "Share",
        onDismissRequest = { showSheet = false },
    ) {
        Text("Sheet content")
    }
}
```

## GlassWindowBottomSheet

The window variant cannot sample the scene; it asks the platform for a window blur (Android 12+) and paints
the tint over it. Desktop / Web / iOS show the tint alone, exactly like [GlassWindowDialog](./glassdialog).

```kotlin
GlassWindowBottomSheet(
    show = showSheet,
    title = "Share",
    onDismissRequest = { showSheet = false },
) {
    Text("Sheet content")
}
```

## Platform capability tiers

`rmuix-blur` degrades in two steps instead of requiring a high `minSdk`, and this applies to **every** blur
entry point (`drawBackdrop`, `textureBlur`, `textureEffect`, `colorOSLiquidGlass`, …), not only the glass
components:

| Platform | Renderer | Behaviour |
| :--- | :--- | :--- |
| Android 13+ (`isRuntimeShaderSupported()`), skiko | AGSL pipeline | Full material: multi-tap Gaussian + mix/blend layers |
| Android 12 / 12L (`isRenderEffectSupported()`) | `RenderEffect` | Plain Gaussian blur of the recorded backdrop + `SrcOver` tint layers |
| Android 11 and below | tint only | No blur: the captured `SrcOver` layers are drawn over the transparent surface, so the panel keeps the stock translucent colour |
| No backdrop at all | solid | The component paints its opaque/translucent fallback colour |

`isBackdropEffectSupported()` reports whether any blur tier is available. `Modifier.legacyBackdropBlur(...)` is
the explicit RenderEffect-only helper for callers that want that path without the shader pipeline.

Because the library declares `minSdk 24` (`BuildConfig.MIN_SDK`), older devices keep building and lose only the
blur — never the layout, colours or buttons.

Notes on the fallback tiers: without a shader only `SrcOver` tint layers are reproducible (custom blend modes and
colour controls are skipped), and the platform blur's falloff differs from the shader pipeline's multi-tap
Gaussian, so those tiers are approximations.

## Known differences

- The stock material mode is replayed as two `SrcOver` blend layers, the same approximation as the dialog.
- The bottom sheet radius is **not** the dialog's: `BottomSheetDefaults.cornerRadius` (32dp) applies to the top
  corners only, and the glass clip follows that geometry.
