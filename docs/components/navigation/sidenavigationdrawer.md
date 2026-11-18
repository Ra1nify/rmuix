# SideNavigationDrawer

The ColorOS side drawer: `COUISideNavigationBar` (`com.coui.appcompat.sidenavigation`), a `DrawerLayout`
subclass.

A panel that slides in from the start edge over a dimming scrim, framed by a hairline divider. Dragging
the panel closes it; dragging from a 20dp strip at the start edge opens it; releasing settles to the
nearer side.

## Import

```kotlin
import io.github.ra1nify.rmuix.kmp.basic.SideNavigationDrawer
import io.github.ra1nify.rmuix.kmp.basic.rememberSideNavigationDrawerState
```

## Basic Usage

```kotlin
val state = rememberSideNavigationDrawerState()
val scope = rememberCoroutineScope()

SideNavigationDrawer(
    state = state,
    drawerContent = {
        Column(Modifier.padding(16.dp)) {
            Text("Albums")
            Text("Photos")
        }
    },
) {
    // the screen behind the drawer
}

// open it from a button / back handling
scope.launch { state.open() }
```

## Properties

| Name          | Type                       | Description                                  | Default |
| :------------ | :------------------------- | :------------------------------------------- | :------ |
| modifier      | Modifier                   | Modifier applied to the drawer               | Modifier |
| state         | SideNavigationDrawerState  | Open/closed slide position                   | rememberSideNavigationDrawerState() |
| width         | Dp                         | Panel width                                  | 300dp |
| colors        | SideNavigationDrawerColors | Scrim, divider and panel colours             | SideNavigationDrawerDefaults.drawerColors() |
| drawerContent | @Composable () -> Unit      | The sliding panel's content                  | - |
| content       | @Composable () -> Unit      | The content underneath                       | - |

`SideNavigationDrawerState` exposes `isOpen`, `isClosed`, and the suspending `open()` / `close()`.

## SideNavigationDrawerDefaults

| Name             | Type  | Source                                                     | Value |
| :--------------- | :---- | :--------------------------------------------------------- | :---- |
| Width            | Dp    | `coui_side_navigation_bar_min_width` (temporary-mode width)  | 300dp |
| MaxWidth         | Dp    | `coui_side_navigation_bar_max_width`                        | 280dp |
| WIDTH_PROPORTION | Float | `COUISideNavigationBar.DRAWER_WIDTH_PROPORTION`              | 0.382 |
| DividerWidth     | Dp    | `coui_side_navigation_drawer_divider_width`                 | 0.33dp |
| EdgeWidth        | Dp    | framework `ViewDragHelper.EDGE_SIZE` (not a COUI dimen)      | 20dp |
| OPEN_MILLIS      | Int   | settle bounds; the dump's scroller is opaque                 | 250 |
| CLOSE_MILLIS     | Int   | settle bounds                                                | 200 |

## Colours

| Slot         | Source                   | Light       | Dark        |
| :----------- | :----------------------- | :---------- | :---------- |
| scrimColor   | `coui_color_mask`         | `#33000000` | `#99000000` |
| dividerColor | `coui_color_divider`      | `#1F000000` | `#33FFFFFF` |
| drawerColor  | theme surface             | - | - |

## Width behaviour

`coui_side_navigation_bar_min_width` is **not** a lower bound: `COUISideNavigationBar` uses it as the
fixed width of its temporary (overlay) mode, which is what a phone-size screen gets. On medium/large
screens stock instead uses `min(coui_side_navigation_bar_max_width, screenWidth * 0.382)`. Only the
temporary branch is implemented here; the values of the other branch are exposed in
`SideNavigationDrawerDefaults` for callers that need them.

## Known gaps

- The medium/large permanent-width branch is not applied automatically.
- `isParentChildHierarchy` (a two-level drawer hierarchy) is not modelled.
- The scrim and settle curves come from `DrawerLayout`'s framework scroller, which the dump does not
  expose, so plain tweens are used.
