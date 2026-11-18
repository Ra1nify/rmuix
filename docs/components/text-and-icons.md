# Text & Icons

Typography and icon display. Every other component is built on these primitives.

## Selection guide

| Scenario | Recommended |
| :--- | :--- |
| Body / title text | Text |
| Small group title | CategoryTitle |
| Any vector icon | Icon + RmuixIcons |

## Components

| Component | Description | Common usage |
| :--- | :--- | :--- |
| [Text](./text-and-icons/text) | Display text content with various styles | Titles, body text, descriptive text |
| [CategoryTitle](./text-and-icons/categorytitle) | Small title component | Auxiliary titles, category labels |
| [Icon](./text-and-icons/icon) | Icon display component | Icon buttons, status indicators |

## Composition

```kotlin
CategoryTitle(text = "Display")
Text(text = "Brightness")
Icon(imageVector = RmuixIcons.Settings, contentDescription = null)
```

## Related categories

[Containers & Layout](./containers) · [Buttons & Toolbars](./buttons) · [Input & Forms](./input) · [Selection & Adjustment](./selection) · [Navigation](./navigation) · [Feedback & Status](./feedback) · [Overlays & Menus](./overlays) · [Preferences](./preferences) · [Media & Effects](./media)

Package indexes: [Basic components](./basic/) · [Extended components](./extended/) · [Extend components](./extend/) · [Glass components](./glass/)
