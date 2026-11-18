// Copyright 2026, compose-rmuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package io.github.ra1nify.rmuix.kmp.preference

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.ra1nify.rmuix.kmp.basic.Text
import io.github.ra1nify.rmuix.kmp.basic.TooltipAnchorPosition
import io.github.ra1nify.rmuix.kmp.basic.TooltipBox
import io.github.ra1nify.rmuix.kmp.basic.TooltipDefaults
import io.github.ra1nify.rmuix.kmp.basic.rememberTooltipState
import io.github.ra1nify.rmuix.kmp.theme.RmuixTheme
import kotlinx.coroutines.launch

/**
 * Colors for [AppInfoPreference].
 *
 * Every value comes from the ColorOS resources named on each property; none of them has a matching
 * `RmuixTheme.colorScheme` token, so they are resolved from the dump rather than the theme.
 */
@Immutable
data class AppInfoPreferenceColors(
    val nameColor: Color,
    val versionColor: Color,
    val copyLabelColor: Color,
    val copyBubbleColor: Color,
    val copyLabelPressedColor: Color,
)

/**
 * Defaults for [AppInfoPreference], from `coui_component_preference_application_info.xml`,
 * `coui_component_popup_window_layout.xml` and `COUIAppInfoPreference`.
 */
object AppInfoPreferenceDefaults {
    /** `setting_appinfo_icon_width_height` (and the 60dp in the canonical layout). */
    val IconSize = 60.dp

    /** `app:couiBorderRadius` of the icon, same as `base_album_item_cover_image_corner_radius`. */
    val IconCornerRadius = 16.dp

    /** `setting_appinfo_icon_margin_top`: the gap between the app bar and the icon. */
    val IconTopMargin = 42.dp

    /** `setting_appinfo_app_name_margin_top`. */
    val NameTopMargin = 8.dp

    /** `setting_appinfo_app_version_margin_top`. */
    val VersionTopMargin = 4.dp

    /** `android:layout_marginBottom` of the canonical layout / `base_memory_details_header_text_view_padding_bottom`. */
    val BottomMargin = 8.dp

    /** `?attr/couiTextAppearanceHeadline4`. */
    val NameFontSize = 20.sp

    /** `?attr/couiTextAppearanceDescription`. */
    val VersionFontSize = 12.sp

    /** `coui_component_copy_window_width`. */
    val CopyLabelMinWidth = 76.dp

    /** `coui_component_copy_window_height`. */
    val CopyLabelMinHeight = 48.dp

    /** `coui_component_copy_text_padding_hor`. */
    val CopyLabelHorizontalPadding = 24.dp

    /** `coui_round_corner_m`, the bubble and ripple-mask radius. */
    val CopyLabelCornerRadius = 12.dp

    /** `?style/couiTextAppearanceBody`. */
    val CopyLabelFontSize = 14.sp

    /** `coui_component_copy_window_margin_bottom`: the gap between the version text and the bubble. */
    val CopyBubbleSpacing = 16.dp

    /** `padding(top = setting_appinfo_icon_margin_top, bottom = layout_marginBottom)`. */
    val InsideMargin = PaddingValues(top = IconTopMargin, bottom = BottomMargin)

    /**
     * The stock colours.
     *
     * `nameColor` is `?attr/couiColorPrimaryNeutral` (`#E6000000` / `#E6FFFFFF`), `versionColor` is
     * `?attr/couiColorSecondNeutral` (`#8A000000` / `#8AFFFFFF`), `copyLabelColor` is the theme's
     * `android:textColorPrimary` (`coui_primary_text_color`: `#FF000000` / `#FFFFFFFF`),
     * `copyBubbleColor` is `?attr/couiColorBackgroundTop` (`#FFFFFF` / `#333333`) and
     * `copyLabelPressedColor` is `?attr/couiColorPressBackground` (`#14000000` / `#26FFFFFF`).
     */
    @Composable
    fun appInfoColors(
        nameColor: Color = stockNeutral(light = 0xE6000000, dark = 0xE6FFFFFF),
        versionColor: Color = stockNeutral(light = 0x8A000000, dark = 0x8AFFFFFF),
        copyLabelColor: Color = stockNeutral(light = 0xFF000000, dark = 0xFFFFFFFF),
        copyBubbleColor: Color = stockNeutral(light = 0xFFFFFFFF, dark = 0xFF333333),
        copyLabelPressedColor: Color = stockNeutral(light = 0x14000000, dark = 0x26FFFFFF),
    ): AppInfoPreferenceColors =
        remember(nameColor, versionColor, copyLabelColor, copyBubbleColor, copyLabelPressedColor) {
            AppInfoPreferenceColors(
                nameColor = nameColor,
                versionColor = versionColor,
                copyLabelColor = copyLabelColor,
                copyBubbleColor = copyBubbleColor,
                copyLabelPressedColor = copyLabelPressedColor,
            )
        }

    /** Picks the light or the dark literal from the ambient [RmuixTheme] colours. */
    @Composable
    private fun stockNeutral(
        light: Long,
        dark: Long,
    ): Color {
        val isDark =
            RmuixTheme.colorScheme.background.luminance() <
                RmuixTheme.colorScheme.onBackground.luminance()
        return remember(isDark) { Color(if (isDark) dark else light) }
    }
}

/**
 * The ColorOS "about this app" header (`COUIAppInfoPreference`).
 *
 * It is **not** the app-details screen of the Settings app - the class only ships in
 * `com.coui.appcompat.aboutpage`, and the only consumer in the three dumps is the Gallery's
 * 关于页 (`setting_album_about.xml` + `SettingsAboutActivity`). It renders the app icon, the app
 * name and the version string, and long-pressing the version pops up a bubble that copies the
 * version to the clipboard.
 *
 * Stock behaviour kept as-is: `copyText` is the bubble's only label (`app:copyText`, in the Gallery
 * it is `common_copy_to` = "复制"), and leaving it `null` - the attribute default - makes the long
 * press do nothing at all. The version is copied straight to the clipboard; [onVersionCopied] is
 * only an extra hook so a caller can show its own "已复制" feedback, because the library has no
 * cross-platform toast.
 *
 * @param appName The application name (`app:appName`).
 * @param modifier The modifier to be applied to the header.
 * @param appVersion The version line (`app:appVersion`); `null` hides it.
 * @param icon The application icon, sized and clipped to [iconSize] / [iconCornerRadius] by the
 *   component.
 * @param copyText The label inside the copy bubble (`app:copyText`); `null` disables the long press.
 * @param onVersionCopied Invoked with [appVersion] after it has been copied to the clipboard.
 * @param colors The colours of the header and of the copy bubble.
 * @param iconSize The size of [icon].
 * @param iconCornerRadius The corner radius applied to [icon].
 * @param insideMargin The padding around the whole header block.
 */
@Composable
fun AppInfoPreference(
    appName: String,
    modifier: Modifier = Modifier,
    appVersion: String? = null,
    icon: (@Composable () -> Unit)? = null,
    copyText: String? = null,
    onVersionCopied: ((String) -> Unit)? = null,
    colors: AppInfoPreferenceColors = AppInfoPreferenceDefaults.appInfoColors(),
    iconSize: Dp = AppInfoPreferenceDefaults.IconSize,
    iconCornerRadius: Dp = AppInfoPreferenceDefaults.IconCornerRadius,
    insideMargin: PaddingValues = AppInfoPreferenceDefaults.InsideMargin,
) {
    Column(
        modifier = modifier.fillMaxWidth().padding(insideMargin),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        if (icon != null) {
            Box(
                modifier =
                    Modifier
                        .size(iconSize)
                        .clip(RoundedCornerShape(iconCornerRadius)),
            ) {
                icon()
            }
        }
        AppInfoPreferenceTitle(
            text = appName,
            color = colors.nameColor,
            topPadding = AppInfoPreferenceDefaults.NameTopMargin,
        )
        if (appVersion != null) {
            AppInfoPreferenceVersion(
                appVersion = appVersion,
                copyText = copyText,
                onVersionCopied = onVersionCopied,
                colors = colors,
            )
        }
    }
}

@Composable
private fun AppInfoPreferenceTitle(
    text: String,
    color: Color,
    topPadding: Dp,
) {
    Text(
        modifier = Modifier.padding(top = topPadding),
        text = text,
        fontSize = AppInfoPreferenceDefaults.NameFontSize,
        fontWeight = FontWeight.Medium,
        color = color,
        textAlign = TextAlign.Center,
    )
}

@Composable
private fun AppInfoPreferenceVersion(
    appVersion: String,
    copyText: String?,
    onVersionCopied: ((String) -> Unit)?,
    colors: AppInfoPreferenceColors,
) {
    // `LocalClipboard` replaces this but needs a platform-specific `ClipEntry`, so the deprecated
    // composition local stays until Compose ships a common plain-text entry point.
    @Suppress("DEPRECATION")
    val clipboard = LocalClipboardManager.current
    val tooltipState = rememberTooltipState(isPersistent = true)
    val scope = rememberCoroutineScope()

    TooltipBox(
        positionProvider =
            TooltipDefaults.rememberTooltipPositionProvider(
                positioning = TooltipAnchorPosition.Above,
                spacingBetweenTooltipAndAnchor = AppInfoPreferenceDefaults.CopyBubbleSpacing,
            ),
        tooltip = {
            if (copyText != null) {
                CopyLabel(
                    text = copyText,
                    colors = colors,
                    onClick = {
                        clipboard.setText(AnnotatedString(appVersion))
                        tooltipState.dismiss()
                        onVersionCopied?.invoke(appVersion)
                    },
                )
            }
        },
        state = tooltipState,
        content = {
            Text(
                modifier =
                    Modifier.padding(top = AppInfoPreferenceDefaults.VersionTopMargin).run {
                        if (copyText == null) {
                            this
                        } else {
                            pointerInput(appVersion, copyText) {
                                detectTapGestures(
                                    onLongPress = { scope.launch { tooltipState.show() } },
                                )
                            }
                        }
                    },
                text = appVersion,
                fontSize = AppInfoPreferenceDefaults.VersionFontSize,
                color = colors.versionColor,
                textAlign = TextAlign.Center,
            )
        },
    )
}

@Composable
private fun CopyLabel(
    text: String,
    colors: AppInfoPreferenceColors,
    onClick: () -> Unit,
) {
    val shape = remember { RoundedCornerShape(AppInfoPreferenceDefaults.CopyLabelCornerRadius) }
    Box(
        modifier =
            Modifier
                .clip(shape)
                .background(colors.copyBubbleColor),
    ) {
        Box(
            modifier =
                Modifier
                    .clip(shape)
                    .clickable(onClick = onClick)
                    .defaultMinSize(
                        minWidth = AppInfoPreferenceDefaults.CopyLabelMinWidth,
                        minHeight = AppInfoPreferenceDefaults.CopyLabelMinHeight,
                    ).padding(horizontal = AppInfoPreferenceDefaults.CopyLabelHorizontalPadding),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = text,
                fontSize = AppInfoPreferenceDefaults.CopyLabelFontSize,
                color = colors.copyLabelColor,
                maxLines = 1,
            )
        }
    }
}
