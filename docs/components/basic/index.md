# Basic Components

`rmuix-ui` provides application layout, navigation, basic controls and Overlay/Window surfaces.

## Components

| Component | Description | Common Usage |
| --- | --- | --- |
| [Spotlight](../media/spotlight) | Shared stock lighting kernel and presets | Touch lighting, material previews |
| [Scaffold](../containers/scaffold) | Basic layout for applications | Page structure, content display |
| [Surface](../containers/surface)                           | Basic container component                  | Content display, background container |
| [TopAppBar](../navigation/topappbar)                       | Application top navigation bar             | Page title, primary actions           |
| [NavigationBar](../navigation/navigationbar)               | Bottom navigation component                | Main page switching                   |
| [SideNavigationDrawer](../navigation/sidenavigationdrawer) | Slide-in side drawer with scrim and divider | Album/section navigation              |
| [NavigationRail](../navigation/navigationrail)             | Side navigation component                  | Main page switching (Large screen)    |
| [TabRow](../navigation/tabrow)                             | Horizontal tab bar                         | Content category browsing             |
| [ChipTabRow](../navigation/chiptabrow)                    | Chip-style tab bar                          | Filter tab switching                 |
| [Card](../containers/card)                                 | Container with related information         | Information display, content grouping |
| [EmptyState](../feedback/emptystate)                     | Empty page: illustration, title, subtitle, action | Empty lists, no-result pages      |
| [CompleteState](../feedback/completestate)               | Completion page with a 100dp illustration           | Finished flows, backup done       |
| [BasicComponent](../containers/basiccomponent)             | Universal base component                   | Custom component development          |
| [BasicComponentGroup](../containers/basiccomponentgroup)  | Card group that draws dividers between rows only| Preference-like lists                |
| [Button](../buttons/button)                             | Interactive element for triggering actions | Form submission, action confirmation  |
| [LoadingButton](../buttons/loadingbutton)              | Button that swaps its label for a three-dot loader| Submit with pending state            |
| [SegmentButton](../buttons/segmentbutton)              | Capsule track with equal-width segments     | Compact mode switching               |
| [IconButton](../buttons/iconbutton)                     | Icon button component                      | Auxiliary actions, toolbars           |
| [Text](../text-and-icons/text)                                 | Display text content with various styles   | Titles, body text, descriptive text   |
| [CategoryTitle](../text-and-icons/categorytitle)                     | Small title component                      | Auxiliary titles, category labels     |
| [TextField](../input/textfield)                       | Receives user text input                   | Form filling, search boxes            |
| [CodeInput](../input/codeinput)                       | Boxed one-time-code input with a transparent field | Verification codes, PINs             |
| [SimpleLock](../input/simplelock)                     | PIN dots: 4/6 circles filled as the code is typed  | PIN entry, security flows            |
| [NumericKeyboard](../input/numerickeyboard)           | 3 × 4 PIN keypad with delete/finish side keys      | PIN/password entry                   |
| [LockPatternView](../input/lockpatternview)           | 3 × 3 pattern lock with success/error states       | Pattern unlock, privacy              |
| [SingleInputCard](../input/singleinputcard)           | Card single-line input with count/delete/error     | Email binding, single inputs          |
| [CalendarPicker](../input/calendarpicker)             | Month-grid calendar with today/selection states    | Date selection, capture time          |
| [AlphabetIndexBar](../feedback/alphabetindexbar)         | A-Z fast-scroll strip with pressed-letter bubble   | Long lists, app drawer                |
| [RoundImage](../media/roundimage)                     | Circular / rounded / shadow image wrapper          | Avatars, preference icons              |
| [Switch](../selection/switch)                             | Binary state toggle control                | Setting switches, feature enabling    |
| [Checkbox](../selection/checkbox)                         | Multiple selection control                 | Multiple choices, terms agreement     |
| [RadioButton](../selection/radiobutton)                   | Single selection control                   | Exclusive choices, option selection   |
| [Slider](../selection/slider)                             | Sliding control for value adjustment       | Volume control, range selection       |
| [NumberPicker](../input/numberpicker)                 | Vertical scroll picker for number selection | Time picker, quantity selection       |
| [DatePicker](../input/datepicker)                     | Wheel date picker (year / month / day)      | Date settings, scheduling             |
| [TimePicker](../input/timepicker)                     | Wheel time picker, 12- or 24-hour           | Alarms, schedules                     |
| [TimeLimitPicker](../input/timelimitpicker)           | Wheel time picker with `小时` / `分钟` unit labels | Schedules, 定时 screens               |
| [ProgressIndicator](../feedback/progressindicator)       | Displays operation progress status         | Loading, progress display             |
| [PageIndicator](../feedback/pageindicator)              | Row of flat page dots                       | Carousels, pager position            |
| [Snackbar](../feedback/snackbar)                         | Temporary message bar for brief feedback   | Operation result, status notification |
| [Tooltip](../feedback/tooltip)                           | Brief label shown on hover or long press   | Icon button labels, element hints     |
| [TopTips](../feedback/toptips)                          | Floating hint card with title, message and action| Battery, network and mode hints      |
| [Chip](../selection/chip)                                 | Capsule filter / selection label            | Filters, tags, choices                |
| [ChipGroup](../selection/chipgroup)                       | Wrapping, collapsible chip group            | Tag lists, filter rows                |
| [Icon](../text-and-icons/icon)                                 | Icon display component                     | Icon buttons, status indicators       |
| [FloatingActionButton](../buttons/floatingactionbutton) | Floating action button                     | Primary actions, quick functions      |
| [FloatingToolbar](../buttons/floatingtoolbar)           | Floating toolbar                           | Quick actions, information display    |
| [Divider](../containers/divider)                           | Content separator                          | Block separation, hierarchy division  |
| [Modifier.pressable](../media/pressable)              | Press feedback modifier with optional click | Cards, colour swatches, custom tap targets |
| [Stepper](../input/stepper)                           | Numeric stepper with +/- buttons           | Quantity, level, step adjust          |
| [RedDot](../feedback/reddot)                            | Hint dot, optionally with a count           | Unread badges, hints                 |
| [ScrollBar](../feedback/scrollbar)                       | Draggable scroll indicator                 | Long lists, scroll position feedback  |
| [SearchBar](../input/searchbar)                       | Search input field                         | Content search, quick find            |
| [OverlayListPopup](../overlays/overlaylistpopup)             | List popup component based on BasicComponent (uses RmuixPopupUtils; requires `Scaffold`) | Option selection, feature list         |
| [OverlayCascadingListPopup](../overlays/overlaycascadinglistpopup) | Two-level cascading list popup (uses RmuixPopupUtils; requires `Scaffold`)               | Submenus, grouped action panels        |
| [OverlayBottomSheet](../overlays/overlaybottomsheet)         | Bottom sheet based on BasicComponent (uses RmuixPopupUtils; requires `Scaffold`)         | Bottom drawer, additional options      |
| [OverlayDialog](../overlays/overlaydialog)                   | Dialog window based on BasicComponent (uses RmuixPopupUtils; requires `Scaffold`)        | Prompts, action confirmation           |
| [WindowListPopup](../overlays/windowlistpopup)     | Window-level list popup component                                                        | Option selection, feature list         |
| [WindowCascadingListPopup](../overlays/windowcascadinglistpopup) | Window-level two-level cascading list popup                                              | Submenus, grouped action panels        |
| [WindowBottomSheet](../overlays/windowbottomsheet) | Window-level bottom sheet component                                                      | Bottom drawer, additional options      |
| [WindowDialog](../overlays/windowdialog)           | Window-level dialog component                                                            | Prompts, action confirmation           |

## Setup

Add the `rmuix-ui` dependency to your project:

```kotlin
kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation("io.github.ra1nify.rmuix.kmp:rmuix-ui:<version>")
        }
    }
}
```

For Android-only projects:

```kotlin
dependencies {
    implementation("io.github.ra1nify.rmuix.kmp:rmuix-ui-android:<version>")
}
```

See [Getting Started](../../guide/getting-started) for complete project configuration and platform-specific dependencies.

## Organization and Hosts

- `io.github.ra1nify.rmuix.kmp.basic`: Scaffold, navigation, layout and interactive controls.
- `io.github.ra1nify.rmuix.kmp.overlay`: surfaces hosted inside the Compose page; they require Rmuix [Scaffold](../containers/scaffold) to provide `RmuixPopupHost`.
- `io.github.ra1nify.rmuix.kmp.window`: window-level surfaces, independent of the Scaffold popup host.

Choose Overlay or Window based on hosting and layering requirements; both belong to `rmuix-ui`. Each component page documents its parameters, defaults and platform constraints.

## Related Documentation

- [Theme System](../../guide/theme): colors, typography and theme configuration.
