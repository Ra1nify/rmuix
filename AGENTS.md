# Rmuix

Compose Multiplatform UI component library. Targets Android, iOS, Desktop (JVM), macOS, Web (Wasm/JS).

## Quick Start

- For significant features or refactors, sketch a plan first and keep it updated as you work.
- Run the component-specific checks below before handing work off; do not skip failing steps.
- Use Context7 to pull library/API docs when you touch unfamiliar Compose/Android/JVM/Js/WasmJs/Swift APIs or deps.

## Key Commands

| Action              | Command                                                 |
| :------------------ | :------------------------------------------------------ |
| Build (full)        | `./gradlew assemble`                                    |
| Build (quick check) | `./gradlew compileKotlinDesktop`                        |
| Test                | `./gradlew check`                                       |
| Check formatting    | `./gradlew spotlessCheck`                               |
| **Fix formatting**  | `./gradlew spotlessApply`                               |
| Run Android demo    | `./gradlew :example:android:installDebug`               |
| Run Desktop demo    | `./gradlew :example:desktop:hotRunDesktop --auto`       |
| Run WasmJs demo     | `./gradlew :example:web:wasmJsBrowserRun`               |
| Run macOS demo      | `./gradlew :example:macos:runDebugExecutableMacosArm64` |
| Run iOS demo        | Open `example/ios/iosApp.xcodeproj` in Xcode and run    |

## Repository Structure

| Directory           | Purpose                                                                    |
| :------------------ | :------------------------------------------------------------------------- |
| `rmuix-core/`       | Utilities + RmuixIcons base (depended on by `ui` and `icons`)              |
| `rmuix-ui/`         | Main UI library                                                            |
| `rmuix-preference/` | Preference / menu / popup components                                       |
| `rmuix-shader/`     | Runtime shader / render effect abstraction (used by `blur` and `squircle`) |
| `rmuix-blur/`       | Blur effects (Android minSdk=33; depends on `rmuix-shader`)                |
| `rmuix-squircle/`   | Squircle corner shapes (depends on `rmuix-shader`)                         |
| `rmuix-icons/`      | Extended icon resources                                                    |
| `rmuix-nav/`        | Navigation runtime (depends on `rmuix-squircle`)                           |
| `example/`          | Demo app                                                                   |
| `baselineprofile/`  | Android baseline profile generation                                        |
| `docs/`             | VitePress documentation site                                               |
| `build-logic/`      | Custom Gradle plugins                                                      |
| `gradle/`           | Version catalog + wrapper                                                  |

### Component Source Layout

`rmuix-ui/src/commonMain/kotlin/io/github/ra1nify/rmuix/kmp/`:

| Subdir        | Contents                                                        |
| :------------ | :-------------------------------------------------------------- |
| `basic/`      | Fundamental components (Button, Switch, TextField, Surface, …)  |
| `overlay/`    | Scaffold-hosted overlays (OverlayDialog, OverlayBottomSheet, …) |
| `window/`     | Platform window components (WindowDialog, WindowBottomSheet, …) |
| `layout/`     | Shared layout primitives                                        |
| `theme/`      | RmuixTheme, Colors, TextStyles, ThemeController, DynamicColors  |
| `color/`      | Color utilities, Material Color                                 |
| `anim/`       | Animation utilities                                             |
| `utils/`      | General utilities                                               |
| `icon/`       | Built-in basic icons (ArrowRight, Check, Search, …)             |
| `interfaces/` | Shared interfaces                                               |

`rmuix-preference/src/commonMain/kotlin/io/github/ra1nify/rmuix/kmp/`:

| Subdir        | Contents                                                        |
| :------------ | :-------------------------------------------------------------- |
| `preference/` | Preference components (SwitchPreference, CheckboxPreference, …) |
| `menu/`       | Dropdown menus (Overlay/Window variants, cascading)             |
| `popup/`      | Dropdown popup primitives shared by menus                       |

### Platform Source Sets

```
commonMain
├── androidMain
└── skikoMain (Skiko rendering — all non-Android targets)
    ├── darwinMain (iOS + macOS)
    │   ├── iosMain
    │   └── macosMain
    ├── desktopMain (JVM)
    └── webMain
        ├── wasmJsMain
        └── jsMain
```

99% of UI logic lives in `commonMain`; platform source sets are only for genuinely platform-specific code.

## Code Style

- **Formatter**: Spotless + ktlint with Compose rules (`io.nlopez.compose.rules:ktlint`); exact versions in `build-logic/src/main/kotlin/module.spotless.gradle.kts`
- **License header** (required on all `.kt` and `.kts` files; Spotless auto-fills `$YEAR` with the current year — do not manually change years in existing headers):

  ```
  // Copyright $YEAR, compose-rmuix-ui contributors
  // SPDX-License-Identifier: Apache-2.0
  ```

  Spotless auto-fills `$YEAR` with the current year. Do not manually change years in existing file headers.

- **Spotless exclusions**: Icon files (`**/icon/**/*.kt`) are excluded from formatting.
- Line endings: platform-native
- Composable function names may use PascalCase (ktlint rule disabled for `@Composable`)

## API Conventions

### Composable Function Signature

Follow this parameter ordering:

```kotlin
@Composable
fun ComponentName(
    // 1. Required parameters (functional callbacks first)
    onClick: () -> Unit,
    // 2. Modifier
    modifier: Modifier = Modifier,
    // 3. Boolean flags
    enabled: Boolean = true,
    // 4. Visual parameters with defaults from ComponentDefaults
    cornerRadius: Dp = ComponentDefaults.CornerRadius,
    minWidth: Dp = ComponentDefaults.MinWidth,
    minHeight: Dp = ComponentDefaults.MinHeight,
    colors: ComponentColors = ComponentDefaults.componentColors(),
    insideMargin: PaddingValues = ComponentDefaults.InsideMargin,
    // 5. Content lambda (always last)
    content: @Composable () -> Unit,
)
```

`@NonRestartableComposable` is not part of the default template — see Key Patterns for when to add it.

### Defaults Object

Each component provides a `ComponentDefaults` object:

```kotlin
object ButtonDefaults {
    val MinWidth = 58.dp                    // Constant dimensions as val
    val MinHeight = 40.dp
    val CornerRadius = 16.dp
    val InsideMargin = PaddingValues(horizontal = 16.dp, vertical = 13.dp)

    @Composable
    fun buttonColors(                       // Color factories must be @Composable
        color: Color = RmuixTheme.colorScheme.secondaryVariant,
        disabledColor: Color = RmuixTheme.colorScheme.disabledSecondaryVariant,
        contentColor: Color = RmuixTheme.colorScheme.onSecondaryVariant,
        disabledContentColor: Color = RmuixTheme.colorScheme.disabledOnSecondaryVariant,
    ): ButtonColors = remember(color, disabledColor, contentColor, disabledContentColor) {
        ButtonColors(
            color = color,
            disabledColor = disabledColor,
            contentColor = contentColor,
            disabledContentColor = disabledContentColor,
        )
    }
}
```

### Colors Data Class

```kotlin
@Immutable
data class ButtonColors(
    val color: Color,
    val disabledColor: Color,
    val contentColor: Color,
    val disabledContentColor: Color,
)
```

### Key Patterns

- **`rememberUpdatedState`** for values whose latest reading must be visible to a long-lived closure without re-running an effect or rebuilding a Modifier: inside `LaunchedEffect`/`DisposableEffect` (the body sees the newest value without being keyed on it) and inside `remember { }`-cached lambdas (e.g., a wrapped `Modifier.clickable` onClick). It only prevents _stale captures_ — it does not stabilize the outer lambda's identity (`Modifier.clickable { current() }` is still a fresh object each composition). Do NOT use it when forwarding a callback directly to a child composable (`Button(onClick = onClick)`) — Compose's skip mechanism handles lambda stability there
- **`remember` with keys** for derived values: `val alpha = remember(enabled) { if (enabled) 1f else 0.38f }`
- **`@NonRestartableComposable`** on thin wrapper composables that fully delegate to other composables and read no state themselves; avoid on composables with multiple internal state reads (they benefit from smart recomposition)
- **`@Immutable`** on color/style data classes
- **Shapes**: `RoundedCornerShape(cornerRadius)` for rounded corners, `CircleShape` for capsules — both from `androidx.compose.foundation.shape`
- **Theme colors**: always `RmuixTheme.colorScheme.*`, never hardcoded colors
- **Text styles**: always `RmuixTheme.textStyles.*` (e.g., `RmuixTheme.textStyles.button`)

## Staging: ColorOS Migration Compatibility Rules

This repo is a fork of compose-miuix-ui being migrated component-by-component to ColorOS 17 visuals.
Full detail in [`docs/STAGING.md`](docs/STAGING.md).

### Priority order (highest rule)

A pixel-accurate ColorOS reproduction is the goal; miuix compatibility is a constraint on the way there.
When they conflict, decide in this order:

1. **Match ColorOS visually and behaviourally** — the goal, not negotiable.
2. **Reuse miuix's interface wherever possible** — yields to 1.
3. **Minimise breaking changes** — yields to 1 and 2.

So: reproduces ColorOS first, then stays as miuix-compatible as that allows. Not "keep miuix's look to
avoid a breaking change".

| Situation | Action |
| :--- | :--- |
| A miuix parameter can express the ColorOS result | Reuse it, add nothing |
| A miuix parameter's semantics conflict with ColorOS | **Change its default to match ColorOS**, keep the name, declare breaking |
| ColorOS has something miuix lacks | Add an optional parameter defaulting to the ColorOS behaviour |
| miuix has something ColorOS lacks | Keep the parameter, but default it to the ColorOS behaviour |

The only case where miuix's current behaviour may survive is a difference that does not affect the ColorOS
reproduction (a pure API convenience, say).

### Interface compatibility

- Existing public signatures must stay callable. Do not reorder, rename or remove existing parameters.
- Adding a parameter is fine; removing one is not. If a signature genuinely must change, keep the old
  shape as a `@Deprecated` overload that delegates to the new one.
- Default values must keep their existing meaning unless the change is explicitly declared as breaking.

### New parameters are always optional, and default to existing behaviour

Every capability added for ColorOS fidelity must be an optional parameter whose default reproduces the
current miuix behaviour:

```kotlin
// ✅ new capability, off by default
fun BasicComponent(
    assignment: String? = null,      // null = not shown, as today
    iconBadge: Boolean = false,      // false = not shown, as today
)

// ❌ never
fun BasicComponent(assignment: String, iconBadge: Boolean)
```

Rule of thumb: **a new functional parameter must default to "off"**. Its default rendering has to match
the pre-migration structure — same content, same line count, nothing reserving space.

The one allowed default change is the *visual* metric the migration exists to fix (e.g. row height
56dp → 48dp, padding 16dp → 32dp). Those are the point of the work, not compatibility breaks.

### Declare what cannot be reconciled

Anything that cannot stay compatible goes in the commit body and the component doc:

```
BREAKING: <parameter> default changed from <A> to <B> because <reason>.
```

Already declared: `Switch`'s `switchColors().uncheckedTrackColor` default moved from
`colorScheme.secondary` to the measured `#1F000000` / `#33FFFFFF` pair.

### Migration workflow

1. **Research from source first, dump second.** Read `com.coui.appcompat.*` for the real implementation;
   use dumps only to confirm or disambiguate. Record findings in `docs/research/coloros-<component>.md`.
   Never put a number in code without a source or dump behind it.
2. **Design the API delta** — list new parameters, confirm each defaults to current behaviour, confirm
   nothing existing is removed or renamed.
3. **Implement in one pass.** No "old visual / new visual" switch, no half-migrated state. Keep the hard
   constraints in Critical Constraints (e.g. BasicComponent's custom `Layout`).
4. **Sync everything** — `docs/components/` + `docs/zh_CN/components/`, `docs/demo/`, and
   `example/shared/.../component/`.
5. **Verify** with `./gradlew compileKotlinDesktop spotlessCheck`.

Do not migrate several components in one go. One component, verify, then the next.

## Critical Constraints

### Do NOT Replace Custom Layout in Component.kt

`BasicComponent` (`basic/Component.kt`) uses a custom `Layout` with intrinsic measurement and a 2:5:3 weighted distribution. **Do NOT replace it with `Row + weight(1f)`** — this was tried and broke layout catastrophically (text rendered as single vertical characters) when start/end content overflows.

### Performance

- `LaunchedEffect` keys: only include values actually read in the effect body
- `minIntrinsicWidth`/`maxIntrinsicWidth` triggers full subtree traversal — defer to overflow branch when possible
- `@Immutable` only on truly immutable data classes: all `val`, never mutated, **and** no lambda/callback fields (lambda equality is reference-based and breaks `@Immutable`'s "equals stays equal forever" contract). `@Stable` for data classes holding lambdas/callbacks, for classes whose mutable properties notify Compose via `MutableState`, and on internal helper functions inside `@Immutable` classes (e.g., `@Stable internal fun color(enabled: Boolean): Color`)
- Standard collections (`List`, `Set`, `Map`) are unstable to Compose — prefer `kotlinx.collections.immutable` (`ImmutableList`/`PersistentList`), which the compiler recognizes as stable. Wrapping a raw `List` in an `@Immutable` data class only works if the caller keeps the wrapper's identity stable (e.g., builds it inside `remember`); a wrapper recreated each composition makes `@Immutable` a false promise to Compose

## Workflows

### Adding a New Component

1. Create the `@Composable` function in `rmuix-ui/src/commonMain/kotlin/io/github/ra1nify/rmuix/kmp/basic/` (or `preference/` in `rmuix-preference` for preference components)
2. Follow the API conventions above (parameter ordering, Defaults object, Colors data class)
3. Add a demo section in `example/shared/src/commonMain/kotlin/component/` and register it in the example app
4. Verify on at least Android and Desktop

### Modifying a Component

When changing a component's API, defaults, or behavior, update all related artifacts:

1. **Documentation** (`docs/components/` and `docs/zh_CN/components/`): properties tables, Defaults object sections, and code examples in both English and Chinese docs
2. **Docs demo code** (`docs/demo/`): the interactive demo if it uses changed APIs
3. **Example app** (`example/shared/src/commonMain/kotlin/component/`): demo code reflecting the changes

### Fixing Bugs

1. Reproduce in the `example` app
2. Fix in `rmuix-ui/` or `rmuix-preference/`
3. If platform-specific, verify the fix across affected platforms

## Git Commit Style

Format: `<scope>: <summary>`

- Scopes: `library`, `docs`, `example`, `build`, `fix`, `fix(deps)`, `chore(deps)`
- Keep subject line ≤ 72 characters, sentence case, no trailing period
- Reference PRs as `(#1234)` at subject end; issues as `* Fix #1234` in the body
- Keep the body terse — omit it when the subject says everything; never restate the diff file by file
- Check recent `git log --oneline` to stay consistent with current conventions
- **Run `./gradlew spotlessCheck` before every commit; only run `./gradlew spotlessApply` if it reports violations — CI rejects formatting violations**
