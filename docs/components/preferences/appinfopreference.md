# AppInfoPreference

The ColorOS "about this app" header: `COUIAppInfoPreference` (`com.coui.appcompat.aboutpage`).

It is **not** the Settings app's app-details screen. In the three dumps the class only appears in the
Gallery's about page (`setting_album_about.xml` + `SettingsAboutActivity`), where it is the top block of
the 关于 page: app icon, app name, version line.

Long-pressing the version shows a bubble with a single label; tapping it copies the version to the
clipboard. That is the whole interaction - `COUIAppInfoPreference` sets an `OnLongClickListener` on the
version `TextView` and opens a `COUIPopupWindow`.

## Import

```kotlin
import io.github.ra1nify.rmuix.kmp.preference.AppInfoPreference
```

## Basic Usage

```kotlin
Card {
    AppInfoPreference(
        appName = "Rmuix",
        appVersion = "Version 1.0.0",
        copyText = "Copy",
        icon = { Image(painter = painterResource(Res.drawable.app_icon), contentDescription = null) },
    )
}
```

## Properties

| Name          | Type                              | Description                                            | Default |
| :------------ | :-------------------------------- | :----------------------------------------------------- | :------ |
| appName       | String                            | Application name (`app:appName`)                        | - |
| modifier      | Modifier                          | Modifier applied to the header                          | Modifier |
| appVersion    | String?                           | Version line (`app:appVersion`); `null` hides it        | null |
| icon          | (@Composable () -> Unit)?         | Application icon, clipped to a rounded square           | null |
| copyText      | String?                           | Label inside the copy bubble (`app:copyText`); `null` disables the long press | null |
| onVersionCopied | ((String) -> Unit)?             | Called with the version after it reached the clipboard  | null |
| colors        | AppInfoPreferenceColors           | Header and bubble colours                               | AppInfoPreferenceDefaults.appInfoColors() |
| iconSize      | Dp                                | Size of `icon`                                          | 60dp |
| iconCornerRadius | Dp                             | Corner radius applied to `icon`                         | 16dp |
| insideMargin  | PaddingValues                     | Padding around the whole block                          | top 42dp / bottom 8dp |

Leaving `copyText` at `null` matches stock: `COUIAppInfoPreference` returns early from the long-press
handler when `copyText` is unset, so nothing happens at all. The version is always copied by the
component itself; `onVersionCopied` exists only so a caller can show its own "copied" feedback, since the
library has no cross-platform toast.

## AppInfoPreferenceDefaults

| Name                      | Type         | Source                                                          | Value |
| :------------------------ | :----------- | :-------------------------------------------------------------- | :---- |
| IconSize                  | Dp           | `setting_appinfo_icon_width_height`                              | 60dp |
| IconCornerRadius          | Dp           | `app:couiBorderRadius` / `base_album_item_cover_image_corner_radius` | 16dp |
| IconTopMargin             | Dp           | `setting_appinfo_icon_margin_top`                                | 42dp |
| NameTopMargin             | Dp           | `setting_appinfo_app_name_margin_top`                            | 8dp |
| VersionTopMargin          | Dp           | `setting_appinfo_app_version_margin_top`                         | 4dp |
| BottomMargin              | Dp           | `base_memory_details_header_text_view_padding_bottom`            | 8dp |
| NameFontSize              | TextUnit     | `?attr/couiTextAppearanceHeadline4`                              | 20sp |
| VersionFontSize           | TextUnit     | `?attr/couiTextAppearanceDescription`                            | 12sp |
| CopyLabelMinWidth         | Dp           | `coui_component_copy_window_width`                               | 76dp |
| CopyLabelMinHeight        | Dp           | `coui_component_copy_window_height`                              | 48dp |
| CopyLabelHorizontalPadding| Dp           | `coui_component_copy_text_padding_hor`                           | 24dp |
| CopyLabelCornerRadius     | Dp           | `coui_round_corner_m`                                            | 12dp |
| CopyLabelFontSize         | TextUnit     | `?style/couiTextAppearanceBody`                                  | 14sp |
| CopyBubbleSpacing         | Dp           | `coui_component_copy_window_margin_bottom`                       | 16dp |
| InsideMargin              | PaddingValues| top `IconTopMargin` + bottom `BottomMargin`                      | 42dp / 8dp |

## Colours

| Slot                  | Attr / colour                        | Light     | Dark      |
| :-------------------- | :----------------------------------- | :-------- | :-------- |
| nameColor             | `?attr/couiColorPrimaryNeutral`       | `#E6000000` | `#E6FFFFFF` |
| versionColor          | `?attr/couiColorSecondNeutral`        | `#8A000000` | `#8AFFFFFF` |
| copyLabelColor        | `android:textColorPrimary`            | `#FF000000` | `#FFFFFFFF` |
| copyBubbleColor       | `?attr/couiColorBackgroundTop`        | `#FFFFFF`   | `#333333`   |
| copyLabelPressedColor | `?attr/couiColorPressBackground`      | `#14000000` | `#26FFFFFF` |

None of these has a matching `RmuixTheme.colorScheme` token, so they are resolved from the dump; the light
and dark literals are picked from the ambient scheme's luminance.

## Known gaps

The bubble background in stock is a 9-patch (`coui_popup_window_bg.9.png`) whose shadow padding is not
expressible as a dimension in the dump, so this implementation draws the bubble shape without a shadow.
Its radius, minimum size, padding, text size and press colour are all taken from the dump.
