# Buttons & Toolbars

Actions the user can trigger, from filled buttons to floating toolbars.

## Selection guide

| Scenario | Recommended |
| :--- | :--- |
| Primary / secondary action | Button |
| Icon-only action | IconButton |
| Pending submit | LoadingButton |
| Equal-width mode switch | SegmentButton |
| Primary floating action | FloatingActionButton |
| Bottom tool strip | FloatingToolbar / EditToolbar |

## Components

| Component | Description | Common usage |
| :--- | :--- | :--- |
| [Button](./buttons/button) | Interactive element for triggering actions | Form submission, action confirmation |
| [IconButton](./buttons/iconbutton) | Icon button component | Auxiliary actions, toolbars |
| [LoadingButton](./buttons/loadingbutton) | Button that swaps its label for a three-dot loader | Submit with pending state |
| [SegmentButton](./buttons/segmentbutton) | Capsule track with equal-width segments | Compact mode switching |
| [FloatingActionButton](./buttons/floatingactionbutton) | Floating action button | Primary actions, quick functions |
| [FloatingToolbar](./buttons/floatingtoolbar) | Floating toolbar | Quick actions, information display |

## Composition

```kotlin
Button(onClick = {}) { Text("Save") }
IconButton(onClick = {}) { Icon(RmuixIcons.Settings, contentDescription = null) }
LoadingButton(loading = true, onClick = {}) { Text("Saving") }
```

## Related categories

[Containers & Layout](./containers) · [Text & Icons](./text-and-icons) · [Input & Forms](./input) · [Selection & Adjustment](./selection) · [Navigation](./navigation) · [Feedback & Status](./feedback) · [Overlays & Menus](./overlays) · [Preferences](./preferences) · [Media & Effects](./media)

Package indexes: [Basic components](./basic/) · [Extended components](./extended/) · [Extend components](./extend/) · [Glass components](./glass/)
