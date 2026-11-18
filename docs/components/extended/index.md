# Extended Components

`rmuix-preference` builds preference rows and dropdown menus on `rmuix-ui`.

## Components

| Component | Description | Common Usage |
| --- | --- | --- |
| [AppInfoPreference](../preferences/appinfopreference)       | App header with icon, name and version; long-press the version to copy it | About pages |
| [JumpPreference](../preferences/jumppreference)               | Arrow component based on BasicComponent                                                  | Clickable indication, navigation hints |
| [SwitchPreference](../preferences/switchpreference)             | Switch component based on BasicComponent                                                 | Setting switches, feature enabling     |
| [CheckboxPreference](../preferences/checkboxpreference)         | Checkbox component based on BasicComponent                                               | Multiple selection, terms agreement    |
| [RadioButtonPreference](../preferences/radiobuttonpreference)   | Radio button component based on BasicComponent                                           | Exclusive choices, option selection    |
| [SliderPreference](../preferences/sliderpreference)             | Slider component based on BasicComponent                                                 | Value adjustment, volume/brightness    |
| [RangeSliderPreference](../preferences/rangesliderpreference)   | Range slider component based on BasicComponent                                           | Range selection, price filter          |
| [OverlayDropdownPreference](../preferences/overlaydropdownpreference) | Dropdown selector based on BasicComponent (uses RmuixPopupUtils; requires `Scaffold`)    | Option selection, feature list         |
| [OverlaySpinnerPreference](../preferences/overlayspinnerpreference) | Advanced selector based on BasicComponent (uses RmuixPopupUtils; requires `Scaffold`)    | Advanced options, feature list         |
| [OverlayDropdownMenu](../overlays/overlaydropdownmenu)       | Action menu based on BasicComponent (uses RmuixPopupUtils; requires `Scaffold`)          | Action menus, multi-select choices     |
| [OverlayIconDropdownMenu](../overlays/overlayicondropdownmenu) | Icon-button action menu (uses RmuixPopupUtils; requires `Scaffold`)                      | Toolbar actions, overflow menu         |
| [OverlayIconCascadingDropdownMenu](../overlays/overlayiconcascadingdropdownmenu) | Icon-button cascading two-level menu (uses RmuixPopupUtils; requires `Scaffold`)         | Toolbar actions with submenus          |
| [WindowDropdownPreference](../preferences/windowdropdownpreference) | Window-level dropdown selector component                                                 | Option selection, feature list         |
| [WindowSpinnerPreference](../preferences/windowspinnerpreference)   | Window-level advanced selector component                                                 | Advanced options, feature list         |
| [WindowDropdownMenu](../overlays/windowdropdownmenu) | Window-level action menu component                                                       | Action menus, multi-select choices     |
| [WindowIconDropdownMenu](../overlays/windowicondropdownmenu) | Window-level icon-button action menu component                                           | Toolbar actions, overflow menu         |
| [WindowIconCascadingDropdownMenu](../overlays/windowiconcascadingdropdownmenu) | Window-level icon-button cascading two-level menu                                        | Toolbar actions with submenus          |

## Setup

Add the `rmuix-preference` dependency to your project:

```kotlin
kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation("io.github.ra1nify.rmuix.kmp:rmuix-preference:<version>")
        }
    }
}
```

For Android-only projects:

```kotlin
dependencies {
    implementation("io.github.ra1nify.rmuix.kmp:rmuix-preference-android:<version>")
}
```

`rmuix-preference` includes `rmuix-ui` transitively.

See [Getting Started](../../guide/getting-started) for complete project configuration and platform-specific dependencies.

## Choosing Components and Hosts

- Components in `io.github.ra1nify.rmuix.kmp.preference` represent setting rows. `DropdownPreference` suits a single selected value; `SpinnerPreference` supports more elaborate option structures.
- Components in `io.github.ra1nify.rmuix.kmp.menu` represent action menus. Items can own their selection independently instead of sharing one single-selection index.
- `Overlay*` variants require Rmuix [Scaffold](../containers/scaffold) and its `RmuixPopupHost`; `Window*` variants render at window level without that host.

Preference rows build on [BasicComponent](../containers/basiccomponent).

## Related Documentation

- [Theme System](../../guide/theme): colors, typography and theme configuration.
