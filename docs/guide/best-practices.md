# Best Practices

- Prefer the components' `XxxDefaults` objects over hard-coded values; every constant is measured against
  the stock COUI resources or documented as a library choice (see `docs/dev/`).
- Give every `Scaffold` page one `RmuixPopupHost`; overlay surfaces (`OverlayDialog`, `OverlayBottomSheet`,
  `OverlayListPopup`, …) require it, window-level surfaces (`WindowDialog`, …) do not.
- Wrap the app once in `RmuixTheme`; read colors through `RmuixTheme.colorScheme` and text through
  `RmuixTheme.textStyles`, never hard-code either.
- Run the demo app (`example/`) for the component you are changing; the library's checks are static and
  cannot certify rendering, gestures or timing.
