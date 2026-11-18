# Components

Rmuix provides a rich set of UI components that follow the ColorOS 17 design language. Each component is measured against the stock COUI resources (`com.coui.appcompat.*`) so its visuals and interaction match the native implementation; components with no stock counterpart say so in their page.

## Browse by category

| Category | Components | Description |
| :--- | ---: | :--- |
| [Containers & Layout](./containers) | 6 | Page structure, grouped content and separators. Start here when composing a screen. |
| [Text & Icons](./text-and-icons) | 3 | Typography and icon display. Every other component is built on these primitives. |
| [Buttons & Toolbars](./buttons) | 6 | Actions the user can trigger, from filled buttons to floating toolbars. |
| [Input & Forms](./input) | 13 | Text, numeric, date/time and secure input. Pick by the data the form collects. |
| [Selection & Adjustment](./selection) | 6 | Binary toggles, exclusive choices, range adjustment and filter tags. |
| [Navigation](./navigation) | 6 | Page-level navigation: top bars, bottom bars, rails, drawers and tabs. |
| [Feedback & Status](./feedback) | 10 | Loading, empty / complete states, transient messages, hints and badges. |
| [Overlays & Menus](./overlays) | 19 | Dialogs, bottom sheets, popups and action menus, including the glass material variants. |
| [Preferences](./preferences) | 11 | Settings rows built on BasicComponent: switches, sliders and dropdown selectors. |
| [Media & Effects](./media) | 5 | Image wrappers, pickers, croppers and touch-light effects. |


## Scaffold Components

| Component                          | Description                   | Common Usage                    |
| ---------------------------------- | ----------------------------- | ------------------------------- |
| [Scaffold](./containers/scaffold) | Basic layout for applications | Page structure, content display |

::: warning
The Scaffold component provides a suitable container for cross-platform popup windows. Components such as `OverlayDialog`, `OverlayDropdownPreference`, `OverlaySpinnerPreference`, and `OverlayListPopup` are all implemented based on this and therefore need to be wrapped by this component.
:::

## Basic Components

| Component                                                  | Description                                | Common Usage                          |
| ---------------------------------------------------------- | ------------------------------------------ | ------------------------------------- |
| [Surface](./containers/surface)                           | Basic container component                  | Content display, background container |
| [TopAppBar](./navigation/topappbar)                       | Application top navigation bar             | Page title, primary actions           |
| [NavigationBar](./navigation/navigationbar)               | Bottom navigation component                | Main page switching                   |
| [NavigationRail](./navigation/navigationrail)             | Side navigation component                  | Main page switching (Large screen)    |
| [TabRow](./navigation/tabrow)                             | Horizontal tab bar                         | Content category browsing             |
| [Card](./containers/card)                                 | Container with related information         | Information display, content grouping |
| [BasicComponent](./containers/basiccomponent)             | Universal base component                   | Custom component development          |
| [Button](./buttons/button)                             | Interactive element for triggering actions | Form submission, action confirmation  |
| [IconButton](./buttons/iconbutton)                     | Icon button component                      | Auxiliary actions, toolbars           |
| [Text](./text-and-icons/text)                                 | Display text content with various styles   | Titles, body text, descriptive text   |
| [CategoryTitle](./text-and-icons/categorytitle)                     | Small title component                      | Auxiliary titles, category labels     |
| [TextField](./input/textfield)                       | Receives user text input                   | Form filling, search boxes            |
| [Switch](./selection/switch)                             | Binary state toggle control                | Setting switches, feature enabling    |
| [Checkbox](./selection/checkbox)                         | Multiple selection control                 | Multiple choices, terms agreement     |
| [RadioButton](./selection/radiobutton)                   | Single selection control                   | Exclusive choices, option selection   |
| [Slider](./selection/slider)                             | Sliding control for value adjustment       | Volume control, range selection       |
| [NumberPicker](./input/numberpicker)                 | Vertical scroll picker for number selection | Time picker, quantity selection       |
| [ProgressIndicator](./feedback/progressindicator)       | Displays operation progress status         | Loading, progress display             |
| [Snackbar](./feedback/snackbar)                         | Temporary message bar for brief feedback   | Operation result, status notification |
| [Tooltip](./feedback/tooltip)                           | Brief label shown on hover or long press   | Icon button labels, element hints     |
| [Icon](./text-and-icons/icon)                                 | Icon display component                     | Icon buttons, status indicators       |
| [FloatingActionButton](./buttons/floatingactionbutton) | Floating action button                     | Primary actions, quick functions      |
| [FloatingToolbar](./buttons/floatingtoolbar)           | Floating toolbar                           | Quick actions, information display    |
| [Divider](./containers/divider)                           | Content separator                          | Block separation, hierarchy division  |
| [SearchBar](./input/searchbar)                       | Search input field                         | Content search, quick find            |

## Extended Components

| Component                                            | Description                                                                              | Common Usage                           |
| ---------------------------------------------------- | ---------------------------------------------------------------------------------------- | -------------------------------------- |
| [JumpPreference](./preferences/jumppreference)               | Arrow component based on BasicComponent                                                  | Clickable indication, navigation hints |
| [SwitchPreference](./preferences/switchpreference)             | Switch component based on BasicComponent                                                 | Setting switches, feature enabling     |
| [CheckboxPreference](./preferences/checkboxpreference)         | Checkbox component based on BasicComponent                                               | Multiple selection, terms agreement    |
| [RadioButtonPreference](./preferences/radiobuttonpreference)   | Radio button component based on BasicComponent                                           | Exclusive choices, option selection    |
| [SliderPreference](./preferences/sliderpreference)             | Slider component based on BasicComponent                                                 | Value adjustment, volume/brightness    |
| [RangeSliderPreference](./preferences/rangesliderpreference)   | Range slider component based on BasicComponent                                           | Range selection, price filter          |
| [OverlayListPopup](./overlays/overlaylistpopup)             | List popup component based on BasicComponent (uses RmuixPopupUtils; requires `Scaffold`) | Option selection, feature list         |
| [OverlayCascadingListPopup](./overlays/overlaycascadinglistpopup) | Two-level cascading list popup (uses RmuixPopupUtils; requires `Scaffold`)               | Submenus, grouped action panels        |
| [OverlayDropdownPreference](./preferences/overlaydropdownpreference) | Dropdown selector based on BasicComponent (uses RmuixPopupUtils; requires `Scaffold`)    | Option selection, feature list         |
| [OverlaySpinnerPreference](./preferences/overlayspinnerpreference) | Advanced selector based on BasicComponent (uses RmuixPopupUtils; requires `Scaffold`)    | Advanced options, feature list         |
| [OverlayDropdownMenu](./overlays/overlaydropdownmenu)       | Action menu based on BasicComponent (uses RmuixPopupUtils; requires `Scaffold`)          | Action menus, multi-select choices     |
| [OverlayIconDropdownMenu](./overlays/overlayicondropdownmenu) | Icon-button action menu (uses RmuixPopupUtils; requires `Scaffold`)                      | Toolbar actions, overflow menu         |
| [OverlayIconCascadingDropdownMenu](./overlays/overlayiconcascadingdropdownmenu) | Icon-button cascading two-level menu (uses RmuixPopupUtils; requires `Scaffold`)         | Toolbar actions with submenus          |
| [OverlayBottomSheet](./overlays/overlaybottomsheet)         | Bottom sheet based on BasicComponent (uses RmuixPopupUtils; requires `Scaffold`)         | Bottom drawer, additional options      |
| [OverlayDialog](./overlays/overlaydialog)                   | Dialog window based on BasicComponent (uses RmuixPopupUtils; requires `Scaffold`)        | Prompts, action confirmation           |
| [WindowListPopup](./overlays/windowlistpopup)     | Window-level list popup component                                                        | Option selection, feature list         |
| [WindowCascadingListPopup](./overlays/windowcascadinglistpopup) | Window-level two-level cascading list popup                                              | Submenus, grouped action panels        |
| [WindowDropdownPreference](./preferences/windowdropdownpreference) | Window-level dropdown selector component                                                 | Option selection, feature list         |
| [WindowSpinnerPreference](./preferences/windowspinnerpreference)   | Window-level advanced selector component                                                 | Advanced options, feature list         |
| [WindowDropdownMenu](./overlays/windowdropdownmenu) | Window-level action menu component                                                       | Action menus, multi-select choices     |
| [WindowIconDropdownMenu](./overlays/windowicondropdownmenu) | Window-level icon-button action menu component                                           | Toolbar actions, overflow menu         |
| [WindowIconCascadingDropdownMenu](./overlays/windowiconcascadingdropdownmenu) | Window-level icon-button cascading two-level menu                                        | Toolbar actions with submenus          |
| [WindowBottomSheet](./overlays/windowbottomsheet) | Window-level bottom sheet component                                                      | Bottom drawer, additional options      |
| [WindowDialog](./overlays/windowdialog)           | Window-level dialog component                                                            | Prompts, action confirmation           |
| [GlassOverlayDialog](./overlays/glassdialog) | Overlay dialog with a sampled backdrop blur (`rmuix-glass`) | Frosted dialogs, material surfaces |
| [GlassWindowDialog](./overlays/glassdialog) | Window dialog with the platform's window blur (`rmuix-glass`) | Frosted dialogs on Android 12+ |
| [GlassOverlayBottomSheet](./overlays/glassbottomsheet) | Overlay bottom sheet with a sampled backdrop blur (`rmuix-glass`) | Frosted sheets |
| [GlassWindowBottomSheet](./overlays/glassbottomsheet) | Window bottom sheet with the platform's window blur (`rmuix-glass`) | Frosted sheets on Android 12+ |
| [GlassFloatingNavigationBar](./overlays/glassfloatingnavigationbar) | Floating navigation capsule with the ColorOS bottom-bar material (`rmuix-glass`) | Frosted bottom navigation |
