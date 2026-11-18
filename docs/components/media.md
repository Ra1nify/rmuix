# Media & Effects

Image wrappers, pickers, croppers and touch-light effects.

## Selection guide

| Scenario | Recommended |
| :--- | :--- |
| Avatar / icon image | RoundImage |
| Pick a colour | ColorPicker |
| Crop an image | ImageCropper |
| Touch lighting | Spotlight |
| Press feedback | Modifier.pressable |

## Components

| Component | Description | Common usage |
| :--- | :--- | :--- |
| [RoundImage](./media/roundimage) | Circular / rounded / shadow image wrapper | Avatars, preference icons |
| [Spotlight](./media/spotlight) | Shared stock lighting kernel and presets | Touch lighting, material previews |
| [Modifier.pressable](./media/pressable) | Press feedback modifier with optional click | Cards, colour swatches, custom tap targets |
| [ColorPicker](./media/colorpicker) | Colour picker + colour preference row | No ColorOS colour-picker class exists |
| [ImageCropper](./media/imagecropper) | Pan/zoom image cropper with a fixed viewport | No ColorOS crop component exists |

## Composition

```kotlin
RoundImage(
    painter = painterResource(Res.drawable.avatar),
    contentDescription = null,
    modifier = Modifier.size(36.dp),
)
```

## Related categories

[Containers & Layout](./containers) · [Text & Icons](./text-and-icons) · [Buttons & Toolbars](./buttons) · [Input & Forms](./input) · [Selection & Adjustment](./selection) · [Navigation](./navigation) · [Feedback & Status](./feedback) · [Overlays & Menus](./overlays) · [Preferences](./preferences)

Package indexes: [Basic components](./basic/) · [Extended components](./extended/) · [Extend components](./extend/) · [Glass components](./glass/)
