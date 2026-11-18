# Shared Spotlight

The shared effect lives in rmuix-shader. All rmuixSpotlight call sites use one source-backed COUI lighting kernel; each instance has its own uniforms, pointer tracking and animations. Only immutable compiled effects are cached by the existing runtime backend.

## Usage

~~~kotlin
import io.github.ra1nify.rmuix.kmp.shader.SpotlightStyle
import io.github.ra1nify.rmuix.kmp.shader.rmuixSpotlight

Modifier.rmuixSpotlight(
    shape = CircleShape,
    style = SpotlightStyle.OpaqueMedium,
    darkTheme = false,
)
~~~

| Parameter | Meaning | Default |
| --- | --- | --- |
| glowColor | Physical light RGB; alpha scales intensity as a library extension | Color.White |
| lightRadius (Float) | Raw-pixel end-radius override; NaN selects the preset | Float.NaN |
| lightRadius (Dp overload) | Explicit density-converted end-radius override | Required |
| shape | Outline used to clip the light; null clips to component bounds | null |
| enabled | Enables the effect and its non-consuming pointer observer | true |
| style | Official parameter family, not a different shader | OpaqueMedium |
| darkTheme | Explicit theme mapping; null uses the host system theme | null |

Existing Float/Dp overloads and their first four parameter names/order stay callable. Explicit radius overrides do not scale the preset's height or other fields: a radius smaller than light height may produce no visible light, as in the native model. Black is not a translucent dark press mask in this model.

## Source Presets

| Style | Light / dark peak intensity | Light / dark blend |
| --- | --- | --- |
| Capsule | 1 / 1 | Overlay / Overlay |
| Circle | .2 / .2 | Plus / Plus |
| RoundedRectangle | 1.3 / .4 | Plus / Plus |
| Switch | .8 / .8 | Overlay / Overlay |
| ChipTab | .2 / .3 | Plus / SrcOver |
| OpaqueSmall | .6 / .5 | Overlay / Overlay |
| OpaqueMedium | .6 / .8 | Overlay / Overlay |
| OpaqueSecondary | 1 / .15 | Overlay / Plus |
| TransparentMedium | .5 / .3 | Overlay / Overlay |

These values come from the Settings APK's COUISpotLightEffect presets. The OpaqueMedium library default does not certify that every component should use that preset. Widget-specific selection and system material gating remain separate review work.

## Rendering And Motion

- Uses the official packed-uniform 3D light model: ambient/diffuse lighting, distance attenuation, luminance alpha, soft-light noise and dithering. No power-law white disc or radial-gradient replacement remains.
- Uses the preset blend mode after content, so opaque component backgrounds no longer cover the effect.
- Glow uses response=.15, fade-out=.4; normalized X/Y following uses response=.35 and bounce=.15. Fade-out also expands the radius. The source's animation scale thresholds are preserved.
- The newest added finger controls the light; removing it selects the native alternate pointer. This differs deliberately from Slider value tracking at pointer index 0. The observer never consumes pointer changes.
- Android requires runtime-shader support and a hardware canvas. Unsupported or failed shader creation omits the effect; it does not draw a fake fallback. Skia uses the same kernel through the existing runtime abstraction.

**BREAKING:** Default tint changes from a low-alpha black mask to white; automatic radius now comes from the selected raw-pixel preset rather than 380px. Internal 40/140/380dp overrides are removed. Timing, blend mode and draw order change to the source-backed model. Tint/radius overrides remain extensions, not stock certification.

The implementation is statically reviewed only. Native offscreen skip-to-end optimization, exact frame clocks, complex parent cancellation, cross-node hit filtering, transformed layers and runtime system feature/power gates are not fully verified. No build, executed tests or device acceptance is implied.
