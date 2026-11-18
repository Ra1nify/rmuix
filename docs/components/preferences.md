# Preferences

Settings rows built on BasicComponent: switches, sliders and dropdown selectors.

## Selection guide

| Scenario | Recommended |
| :--- | :--- |
| Read-only info row | AppInfoPreference / JumpPreference |
| Toggle setting | SwitchPreference |
| Check / radio row | CheckboxPreference / RadioButtonPreference |
| Value / range | SliderPreference / RangeSliderPreference |
| Dropdown selector | Overlay/Window DropdownPreference |
| Advanced selector | Overlay/Window SpinnerPreference |

## Components

| Component | Description | Common usage |
| :--- | :--- | :--- |
| [AppInfoPreference](./preferences/appinfopreference) | App header with icon, name and version; long-press the version to copy it | About pages |
| [JumpPreference](./preferences/jumppreference) | Arrow component based on BasicComponent | Clickable indication, navigation hints |
| [SwitchPreference](./preferences/switchpreference) | Switch component based on BasicComponent | Setting switches, feature enabling |
| [CheckboxPreference](./preferences/checkboxpreference) | Checkbox component based on BasicComponent | Multiple selection, terms agreement |
| [RadioButtonPreference](./preferences/radiobuttonpreference) | Radio button component based on BasicComponent | Exclusive choices, option selection |
| [SliderPreference](./preferences/sliderpreference) | Slider component based on BasicComponent | Value adjustment, volume/brightness |
| [RangeSliderPreference](./preferences/rangesliderpreference) | Range slider component based on BasicComponent | Range selection, price filter |
| [OverlayDropdownPreference](./preferences/overlaydropdownpreference) | Dropdown selector based on BasicComponent (uses RmuixPopupUtils; requires `Scaffold`) | Option selection, feature list |
| [OverlaySpinnerPreference](./preferences/overlayspinnerpreference) | Advanced selector based on BasicComponent (uses RmuixPopupUtils; requires `Scaffold`) | Advanced options, feature list |
| [WindowDropdownPreference](./preferences/windowdropdownpreference) | Window-level dropdown selector component | Option selection, feature list |
| [WindowSpinnerPreference](./preferences/windowspinnerpreference) | Window-level advanced selector component | Advanced options, feature list |

## Composition

```kotlin
SwitchPreference(title = "Wi-Fi", checked = on, onCheckedChange = { on = it })
JumpPreference(title = "About") { /* navigate */ }
```

## Related categories

[Containers & Layout](./containers) · [Text & Icons](./text-and-icons) · [Buttons & Toolbars](./buttons) · [Input & Forms](./input) · [Selection & Adjustment](./selection) · [Navigation](./navigation) · [Feedback & Status](./feedback) · [Overlays & Menus](./overlays) · [Media & Effects](./media)

Package indexes: [Basic components](./basic/) · [Extended components](./extended/) · [Extend components](./extend/) · [Glass components](./glass/)
