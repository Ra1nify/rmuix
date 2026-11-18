# Glass Dialog

ColorOS 17 draws dialog backdrops with `COUIBackgroundBlurBuilder`: a 200px blur of whatever is behind the
dialog plus two tint layers. The builder is built on the private `com.oplus.graphics` API
(`OplusBlurParam.setMaterialParams`), so this module replays the same tokens through `rmuix-blur`'s
`drawBackdrop` pipeline. Treat it as a faithful-token, approximate-rendering implementation.

## Stock tokens

| Token | Value | Role |
| :--- | ---: | :--- |
| `coui_dialog_blur_radius` | 200 (px) | Backdrop blur radius passed to `OplusBlurParam.setBlurRadius` |
| `coui_dialog_list_mix_blur_light` | `#dc909090` | Light-mode mix colour |
| `coui_dialog_list_mix_blur_dark` | `#8c777777` | Dark-mode mix colour |
| `coui_dialog_list_blend_blur_light` | `#509f9f9f` | Light-mode blend colour |
| `coui_dialog_list_blend_blur_dark` | `#b2292929` | Dark-mode blend colour |
| `?attr/couiColorSurface` | `#ffffff` / `#1e1e1e` | Solid fallback when no blur is available |

They are exposed as [GlassDefaults]: `DIALOG_BLUR_RADIUS_PX`, `DialogMixLight/Dark`, `DialogBlendLight/Dark`,
`DialogFallbackLight/Dark`, plus `dialogBlurColors(isDark)`.

## GlassOverlayDialog — the exact path

An overlay dialog draws inside the host scene, so a `LayerBackdrop` captured around the content behind it
can be sampled directly. Both sides must use the same backdrop:

```kotlin
val backdrop = rememberGlassBackdrop()

Box(Modifier.fillMaxSize().layerBackdrop(backdrop)) {
    AppContent()

    GlassOverlayDialog(
        backdrop = backdrop,
        show = showDialog,
        title = "Delete this item?",
        summary = "This cannot be undone.",
        confirmButtonText = "Delete",
        confirmButtonDestructive = true,
        onConfirm = { showDialog = false },
        dismissButtonText = "Cancel",
        onDismiss = { showDialog = false },
    ) {}
}
```

`rememberGlassBackdrop()` must wrap the content that is *behind* the dialog, and `Modifier.layerBackdrop`
must be applied to it; the dialog itself only samples. Layout, typography, button bar and dismissal behave
exactly like [OverlayDialog](./overlaydialog) — only the surface changes.

## GlassWindowDialog — the platform-blur path

A window dialog lives in its own platform window and cannot sample the activity's scene. This variant asks
the platform for a background blur instead:

| Platform | Behaviour |
| :--- | :--- |
| Android 12+ | `Window.setBackgroundBlurRadius(200)` + `FLAG_BLUR_BEHIND`; the stock translucent tint is painted on top |
| Desktop / Web / iOS | No platform window blur: the tint is painted alone, so the content behind the window is **not** blurred |

The layout, colours and buttons are identical to [WindowDialog](./windowdialog); only the material
differs, and on non-Android targets it degrades to a translucent panel.

```kotlin
GlassWindowDialog(
    show = showDialog,
    title = "Delete this item?",
    summary = "This cannot be undone.",
    confirmButtonText = "Delete",
    confirmButtonDestructive = true,
    onConfirm = { showDialog = false },
) {}
```

## Customising the material

`blurRadiusPx` and `colors` are pass-through parameters:

```kotlin
GlassOverlayDialog(
    backdrop = backdrop,
    show = showDialog,
    title = "Custom material",
    blurRadiusPx = 120f,
    colors = GlassDefaults.dialogBlurColors(isDark = true),
) {}
```

Pass `colors = null` (the default) to resolve the light/dark tokens from the current `RmuixTheme` palette.

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

- The material mode (`OplusBlurParam.setMaterialParams(mode, blendColor, mixColor)`) is replayed as two
  `SrcOver` blend layers; the stock shader's material function is not reproduced.
- Window blur needs the window to be dimmed/blur-capable; on Android it also depends on the OEM build.
- The stock blur radius is a raw pixel value, independent of the density bucket - the parameter is
  documented in pixels for that reason.
