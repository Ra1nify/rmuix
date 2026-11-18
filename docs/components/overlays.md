# Overlays & Menus

Dialogs, bottom sheets, popups and action menus, including the glass material variants.

## Selection guide

| Scenario | Recommended |
| :--- | :--- |
| Needs Scaffold (shared scene) | Overlay* family |
| Independent window | Window* family |
| Frosted material | Glass* family |
| Action / selection menu | Overlay/Window DropdownMenu |
| Two-level menu | *IconCascadingDropdownMenu |

## Components

| Component | Description | Common usage |
| :--- | :--- | :--- |
| [OverlayListPopup](./overlays/overlaylistpopup) | List popup component based on BasicComponent (uses RmuixPopupUtils; requires `Scaffold`) | Option selection, feature list |
| [OverlayCascadingListPopup](./overlays/overlaycascadinglistpopup) | Two-level cascading list popup (uses RmuixPopupUtils; requires `Scaffold`) | Submenus, grouped action panels |
| [OverlayBottomSheet](./overlays/overlaybottomsheet) | Bottom sheet based on BasicComponent (uses RmuixPopupUtils; requires `Scaffold`) | Bottom drawer, additional options |
| [OverlayDialog](./overlays/overlaydialog) | Dialog window based on BasicComponent (uses RmuixPopupUtils; requires `Scaffold`) | Prompts, action confirmation |
| [WindowListPopup](./overlays/windowlistpopup) | Window-level list popup component | Option selection, feature list |
| [WindowCascadingListPopup](./overlays/windowcascadinglistpopup) | Window-level two-level cascading list popup | Submenus, grouped action panels |
| [WindowBottomSheet](./overlays/windowbottomsheet) | Window-level bottom sheet component | Bottom drawer, additional options |
| [WindowDialog](./overlays/windowdialog) | Window-level dialog component | Prompts, action confirmation |
| [OverlayDropdownMenu](./overlays/overlaydropdownmenu) | Action menu based on BasicComponent (uses RmuixPopupUtils; requires `Scaffold`) | Action menus, multi-select choices |
| [OverlayIconDropdownMenu](./overlays/overlayicondropdownmenu) | Icon-button action menu (uses RmuixPopupUtils; requires `Scaffold`) | Toolbar actions, overflow menu |
| [OverlayIconCascadingDropdownMenu](./overlays/overlayiconcascadingdropdownmenu) | Icon-button cascading two-level menu (uses RmuixPopupUtils; requires `Scaffold`) | Toolbar actions with submenus |
| [WindowDropdownMenu](./overlays/windowdropdownmenu) | Window-level action menu component | Action menus, multi-select choices |
| [WindowIconDropdownMenu](./overlays/windowicondropdownmenu) | Window-level icon-button action menu component | Toolbar actions, overflow menu |
| [WindowIconCascadingDropdownMenu](./overlays/windowiconcascadingdropdownmenu) | Window-level icon-button cascading two-level menu | Toolbar actions with submenus |
| [GlassOverlayDialog](./overlays/glassdialog) | OverlayDialog with a sampled backdrop blur | - |
| [GlassWindowDialog](./overlays/glassdialog) | WindowDialog with the platform window blur | - |
| [GlassOverlayBottomSheet](./overlays/glassbottomsheet) | OverlayBottomSheet with a sampled backdrop blur | - |
| [GlassWindowBottomSheet](./overlays/glassbottomsheet) | WindowBottomSheet with the platform window blur | - |
| [GlassFloatingNavigationBar](./overlays/glassfloatingnavigationbar) | Floating capsule bar with the ColorOS bottom-bar material | - |

## Composition

```kotlin
Scaffold {
    Button(onClick = { showDialog = true }) { Text("Open") }
    OverlayDialog(show = showDialog, onDismiss = { showDialog = false }) {
        Text("Dialog content")
    }
}
```

## Related categories

[Containers & Layout](./containers) · [Text & Icons](./text-and-icons) · [Buttons & Toolbars](./buttons) · [Input & Forms](./input) · [Selection & Adjustment](./selection) · [Navigation](./navigation) · [Feedback & Status](./feedback) · [Preferences](./preferences) · [Media & Effects](./media)

Package indexes: [Basic components](./basic/) · [Extended components](./extended/) · [Extend components](./extend/) · [Glass components](./glass/)
