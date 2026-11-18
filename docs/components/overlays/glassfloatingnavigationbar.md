# Glass Floating Navigation Bar

`GlassFloatingNavigationBar` is [FloatingNavigationBar](../navigation/navigationbar) with the ColorOS glass material on
its capsule. The material is `rmuix-blur`'s `colorOSLiquidGlass(ColorOSGlassType.BottomBar)` preset, i.e. the two
`COUIMaterialBlurEffect` blend layers of the ColorOS bottom bar - no colour is invented for this component.

Everything else stays stock: the capsule radius (`FloatingNavigationBarDefaults.CornerRadius`, half the 56dp bar
height), the drop shadow, the 48dp indicator and the item metrics.

## Usage

Provide the backdrop once at the root of the scene (`LocalGlassBackdrop`), then drop the glass bar in:

```kotlin
Box(Modifier.fillMaxSize().layerBackdrop(backdrop)) {
    Scaffold(
        bottomBar = {
            GlassFloatingNavigationBar {
                FloatingNavigationBarItem(selected = true, onClick = {}, icon = {}, label = "Home")
                FloatingNavigationBarItem(selected = false, onClick = {}, icon = {}, label = "Settings")
            }
        },
    ) { /* content */ }
}
```

The bar samples the backdrop behind it. Without a backdrop (`LocalGlassBackdrop.current == null` and no explicit
`backdrop` argument) it falls back to its solid `color`, which is the same behaviour as the other glass
components.

## Platform notes

The material is drawn in the same scene as the bar, so it works on every Compose Multiplatform target that
supports runtime shaders. Where `isRuntimeShaderSupported()` is false the backdrop effect is skipped by
`rmuix-blur` itself and the bar keeps drawing its shadow and content.

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

- The stock floating bar can enable its material blur through `couiFloatingToolbarBackgroundBlur`; this module
  always uses the bottom-bar preset when a backdrop is available and never invents a radius/colour pair.
- The melted/liquid bridge between the capsule and its accessory pills is the existing `meltEffectMode`
  approximation, unchanged by this wrapper.
