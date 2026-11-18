# Selection & Adjustment

Binary toggles, exclusive choices, range adjustment and filter tags.

## Selection guide

| Scenario | Recommended |
| :--- | :--- |
| On / off setting | Switch |
| Multiple choices | Checkbox |
| One of N choices | RadioButton |
| Continuous value / range | Slider (RangeSlider, VerticalSlider) |
| Filter / tag | Chip / ChipGroup |

## Components

| Component | Description | Common usage |
| :--- | :--- | :--- |
| [Switch](./selection/switch) | Binary state toggle control | Setting switches, feature enabling |
| [Checkbox](./selection/checkbox) | Multiple selection control | Multiple choices, terms agreement |
| [RadioButton](./selection/radiobutton) | Single selection control | Exclusive choices, option selection |
| [Slider](./selection/slider) | Sliding control for value adjustment | Volume control, range selection |
| [Chip](./selection/chip) | Capsule filter / selection label | Filters, tags, choices |
| [ChipGroup](./selection/chipgroup) | Wrapping, collapsible chip group | Tag lists, filter rows |

## Composition

```kotlin
Switch(checked = on, onCheckedChange = { on = it })
Slider(value = volume, onValueChange = { volume = it })
Chip(text = "All", selected = true, onClick = {})
```

## Related categories

[Containers & Layout](./containers) · [Text & Icons](./text-and-icons) · [Buttons & Toolbars](./buttons) · [Input & Forms](./input) · [Navigation](./navigation) · [Feedback & Status](./feedback) · [Overlays & Menus](./overlays) · [Preferences](./preferences) · [Media & Effects](./media)

Package indexes: [Basic components](./basic/) · [Extended components](./extended/) · [Extend components](./extend/) · [Glass components](./glass/)
