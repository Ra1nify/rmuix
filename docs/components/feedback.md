# Feedback & Status

Loading, empty / complete states, transient messages, hints and badges.

## Selection guide

| Scenario | Recommended |
| :--- | :--- |
| Loading / progress | ProgressIndicator |
| Carousel position | PageIndicator |
| Transient message | Snackbar |
| Long-press hint | Tooltip |
| Floating hint card | TopTips |
| No data / finished flow | EmptyState / CompleteState |
| Unread badge | RedDot |
| Scroll indicator | ScrollBar |
| A-Z list index | AlphabetIndexBar |

## Components

| Component | Description | Common usage |
| :--- | :--- | :--- |
| [ProgressIndicator](./feedback/progressindicator) | Displays operation progress status | Loading, progress display |
| [PageIndicator](./feedback/pageindicator) | Row of flat page dots | Carousels, pager position |
| [Snackbar](./feedback/snackbar) | Temporary message bar for brief feedback | Operation result, status notification |
| [Tooltip](./feedback/tooltip) | Brief label shown on hover or long press | Icon button labels, element hints |
| [TopTips](./feedback/toptips) | Floating hint card with title, message and action | Battery, network and mode hints |
| [EmptyState](./feedback/emptystate) | Empty page: illustration, title, subtitle, action | Empty lists, no-result pages |
| [CompleteState](./feedback/completestate) | Completion page with a 100dp illustration | Finished flows, backup done |
| [RedDot](./feedback/reddot) | Hint dot, optionally with a count | Unread badges, hints |
| [ScrollBar](./feedback/scrollbar) | Draggable scroll indicator | Long lists, scroll position feedback |
| [AlphabetIndexBar](./feedback/alphabetindexbar) | A-Z fast-scroll strip with pressed-letter bubble | Long lists, app drawer |

## Composition

```kotlin
if (loading) {
    CircularProgressIndicator()
}
EmptyState(title = "No results", subtitle = "Try another keyword")
```

## Related categories

[Containers & Layout](./containers) · [Text & Icons](./text-and-icons) · [Buttons & Toolbars](./buttons) · [Input & Forms](./input) · [Selection & Adjustment](./selection) · [Navigation](./navigation) · [Overlays & Menus](./overlays) · [Preferences](./preferences) · [Media & Effects](./media)

Package indexes: [Basic components](./basic/) · [Extended components](./extended/) · [Extend components](./extend/) · [Glass components](./glass/)
