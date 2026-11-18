# Containers & Layout

Page structure, grouped content and separators. Start here when composing a screen.

## Selection guide

| Scenario | Recommended |
| :--- | :--- |
| Page skeleton / hosting overlays | Scaffold |
| Group related content | Card / BasicComponentGroup |
| Single information row | BasicComponent |
| Background container | Surface |
| Separate blocks | Divider |

## Components

| Component | Description | Common usage |
| :--- | :--- | :--- |
| [Scaffold](./containers/scaffold) | Basic layout for applications | Page structure, content display |
| [Surface](./containers/surface) | Basic container component | Content display, background container |
| [Card](./containers/card) | Container with related information | Information display, content grouping |
| [BasicComponent](./containers/basiccomponent) | Universal base component | Custom component development |
| [BasicComponentGroup](./containers/basiccomponentgroup) | Card group that draws dividers between rows only | Preference-like lists |
| [Divider](./containers/divider) | Content separator | Block separation, hierarchy division |

## Composition

```kotlin
Scaffold {
    Card {
        BasicComponentGroup {
            BasicComponent(title = "Wi-Fi") { Text("Connected") }
            BasicComponent(title = "Bluetooth") { Text("Off") }
        }
    }
}
```

## Related categories

[Text & Icons](./text-and-icons) · [Buttons & Toolbars](./buttons) · [Input & Forms](./input) · [Selection & Adjustment](./selection) · [Navigation](./navigation) · [Feedback & Status](./feedback) · [Overlays & Menus](./overlays) · [Preferences](./preferences) · [Media & Effects](./media)

Package indexes: [Basic components](./basic/) · [Extended components](./extended/) · [Extend components](./extend/) · [Glass components](./glass/)
