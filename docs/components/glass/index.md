# Glass Components

`rmuix-glass` provides the ColorOS 17 **glass material** surfaces. It is the only module that depends on
`rmuix-blur`, so the core `rmuix-ui` keeps no blur dependency; import this module only when you actually
want a frosted surface.

Every metric in this module comes from the ColorOS resources (`coui_dialog_blur_radius` 200,
`coui_dialog_list_mix_blur_*`, `coui_dialog_list_blend_blur_*`, `?attr/couiColorSurface`), but the material
itself is an **approximation**: the stock builder drives the private `com.oplus.graphics.OplusBlurParam`
API, which no third-party app can call.

| Component | Description | Notes |
| :--- | :--- | :--- |
| [GlassOverlayDialog](../overlays/glassdialog) | `OverlayDialog` with a sampled backdrop blur | Exact material path: dialog and content share one scene |
| [GlassWindowDialog](../overlays/glassdialog) | `WindowDialog` with the platform's window blur | Android 12+ only; other targets keep the tint without blur |
| [GlassOverlayBottomSheet](../overlays/glassbottomsheet) | `OverlayBottomSheet` with a sampled backdrop blur | Exact material path (400px sheet blur) |
| [GlassWindowBottomSheet](../overlays/glassbottomsheet) | `WindowBottomSheet` with the platform's window blur | Android 12+ only; tint elsewhere |
| [GlassFloatingNavigationBar](../overlays/glassfloatingnavigationbar) | Floating capsule bar with the ColorOS bottom-bar material | Samples `LocalGlassBackdrop`; solid fallback |
