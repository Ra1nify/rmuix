# Navigation

Page-level navigation: top bars, bottom bars, rails, drawers and tabs.

## Selection guide

| Scenario | Recommended |
| :--- | :--- |
| Page title + actions | TopAppBar |
| Bottom main tabs | NavigationBar / FloatingNavigationBar |
| Large-screen side nav | NavigationRail |
| Slide-in menu | SideNavigationDrawer |
| Content tabs | TabRow / ChipTabRow |

## Components

| Component | Description | Common usage |
| :--- | :--- | :--- |
| [TopAppBar](./navigation/topappbar) | Application top navigation bar | Page title, primary actions |
| [NavigationBar](./navigation/navigationbar) | Bottom navigation component | Main page switching |
| [SideNavigationDrawer](./navigation/sidenavigationdrawer) | Slide-in side drawer with scrim and divider | Album/section navigation |
| [NavigationRail](./navigation/navigationrail) | Side navigation component | Main page switching (Large screen) |
| [TabRow](./navigation/tabrow) | Horizontal tab bar | Content category browsing |
| [ChipTabRow](./navigation/chiptabrow) | Chip-style tab bar | Filter tab switching |

## Composition

```kotlin
Scaffold(
    topBar = { TopAppBar(title = { Text("Settings") }) },
    bottomBar = { NavigationBar { /* items */ } },
) { /* content */ }
```

## Related categories

[Containers & Layout](./containers) · [Text & Icons](./text-and-icons) · [Buttons & Toolbars](./buttons) · [Input & Forms](./input) · [Selection & Adjustment](./selection) · [Feedback & Status](./feedback) · [Overlays & Menus](./overlays) · [Preferences](./preferences) · [Media & Effects](./media)

Package indexes: [Basic components](./basic/) · [Extended components](./extended/) · [Extend components](./extend/) · [Glass components](./glass/)
