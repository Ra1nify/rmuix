# AppInfoPreference

ColorOS 的「关于本应用」头部：`COUIAppInfoPreference`（`com.coui.appcompat.aboutpage`）。

它**不是**设置里「应用详情」页的控件。三棵树里它只出现在相册的关于页（`setting_album_about.xml` +
`SettingsAboutActivity`），是「关于」页顶部那一块：应用图标、应用名、版本号。

长按版本号会弹出一个小气泡，里面只有一个标签，点它把版本号复制到剪贴板——这就是全部交互。
`COUIAppInfoPreference` 给版本 `TextView` 设的是 `OnLongClickListener`，点开一个 `COUIPopupWindow`。

## 引入

```kotlin
import io.github.ra1nify.rmuix.kmp.preference.AppInfoPreference
```

## 基本用法

```kotlin
Card {
    AppInfoPreference(
        appName = "Rmuix",
        appVersion = "版本 1.0.0",
        copyText = "复制",
        icon = { Image(painter = painterResource(Res.drawable.app_icon), contentDescription = null) },
    )
}
```

## 参数

| 参数名        | 类型                              | 说明                                              | 默认值 |
| :------------ | :-------------------------------- | :------------------------------------------------ | :----- |
| appName       | String                            | 应用名（`app:appName`）                            | - |
| modifier      | Modifier                          | 应用到头部的 Modifier                              | Modifier |
| appVersion    | String?                           | 版本行（`app:appVersion`），`null` 不显示          | null |
| icon          | (@Composable () -> Unit)?         | 应用图标，会被裁成圆角方形                         | null |
| copyText      | String?                           | 复制气泡里的标签（`app:copyText`），`null` 关闭长按 | null |
| onVersionCopied | ((String) -> Unit)?             | 版本号写入剪贴板后回调                             | null |
| colors        | AppInfoPreferenceColors           | 头部与气泡颜色                                     | AppInfoPreferenceDefaults.appInfoColors() |
| iconSize      | Dp                                | `icon` 的尺寸                                      | 60dp |
| iconCornerRadius | Dp                             | `icon` 的圆角半径                                  | 16dp |
| insideMargin  | PaddingValues                     | 整块外围内距                                       | 上 42dp / 下 8dp |

`copyText` 保持 `null` 与官方一致：`COUIAppInfoPreference` 在 `copyText` 未设置时会直接从长按处理里
返回，什么都不做。版本号由组件自己复制；`onVersionCopied` 只是为了方便调用方显示自己的「已复制」提示，
因为库里没有跨平台 toast。

## AppInfoPreferenceDefaults

| 名称                      | 类型         | 来源                                                            | 值 |
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
| InsideMargin              | PaddingValues| 上 `IconTopMargin` + 下 `BottomMargin`                            | 42dp / 8dp |

## 颜色

| 槽位                  | Attr / 颜色                          | 浅色      | 深色      |
| :-------------------- | :----------------------------------- | :-------- | :-------- |
| nameColor             | `?attr/couiColorPrimaryNeutral`       | `#E6000000` | `#E6FFFFFF` |
| versionColor          | `?attr/couiColorSecondNeutral`        | `#8A000000` | `#8AFFFFFF` |
| copyLabelColor        | `android:textColorPrimary`            | `#FF000000` | `#FFFFFFFF` |
| copyBubbleColor       | `?attr/couiColorBackgroundTop`        | `#FFFFFF`   | `#333333`   |
| copyLabelPressedColor | `?attr/couiColorPressBackground`      | `#14000000` | `#26FFFFFF` |

这些颜色在 `RmuixTheme.colorScheme` 里都没有对应 token，所以按 dump 解析；浅深两套字面量按环境配色的
亮度选取。

## 已知差异

官方的气泡背景是 9-patch（`coui_popup_window_bg.9.png`），它的阴影 padding 在 dump 里不是可读的尺寸，
因此本实现只画气泡形状、不画阴影。圆角、最小尺寸、内距、字号、按压色都有 dump 依据。
