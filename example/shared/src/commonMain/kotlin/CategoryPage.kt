// Copyright 2026, compose-rmuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

@file:OptIn(ExperimentalScrollBarApi::class)

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import component.BackNavigationIcon
import component.ExampleCatalog
import component.alphabetIndexBarSection
import component.appInfoPreferenceSection
import component.basicComponentGroupSection
import component.basicComponentSection
import component.blurSection
import component.bottomSheetSection
import component.buttonSection
import component.calendarPickerSection
import component.cardSection
import component.checkboxSection
import component.chipSection
import component.codeInputSection
import component.colorPickerSection
import component.datePickerSection
import component.dialogSection
import component.dropdownSection
import component.editToolbarSection
import component.emptyStateSection
import component.glassDialogSection
import component.imageCropperSection
import component.jumpSection
import component.loadingButtonSection
import component.lockPatternViewSection
import component.navigationBarSection
import component.numberPickerSection
import component.numericKeyboardSection
import component.otherPageSection
import component.pageIndicatorSection
import component.progressIndicatorSection
import component.radioButtonSection
import component.redDotSection
import component.roundImageSection
import component.scrollBarSection
import component.segmentButtonSection
import component.sideNavigationDrawerSection
import component.simpleLockSection
import component.singleInputCardSection
import component.sliderSection
import component.snackbarSection
import component.spinnerSection
import component.spotlightSection
import component.switchSection
import component.tabRowSection
import component.textAndIconSection
import component.textFieldSection
import component.timeLimitPickerSection
import component.timePickerSection
import component.tooltipSection
import component.topTipsSection
import component.verticalSeekBarSection
import io.github.ra1nify.rmuix.kmp.basic.RmuixScrollBehavior
import io.github.ra1nify.rmuix.kmp.basic.Scaffold
import io.github.ra1nify.rmuix.kmp.basic.SmallTopAppBar
import io.github.ra1nify.rmuix.kmp.basic.SnackbarHost
import io.github.ra1nify.rmuix.kmp.basic.SnackbarHostState
import io.github.ra1nify.rmuix.kmp.basic.VerticalScrollBar
import io.github.ra1nify.rmuix.kmp.basic.rememberScrollBarAdapter
import io.github.ra1nify.rmuix.kmp.interfaces.ExperimentalScrollBarApi
import io.github.ra1nify.rmuix.kmp.theme.RmuixTheme
import utils.pageContentPadding
import utils.pageScrollModifiers

/**
 * Second-level component archive page: renders the demo sections of one [ExampleCatalog] category.
 * Pushed from the home page as `Route.Category(key)`; mirrors the docs site's ten category pages.
 */
@Composable
fun CategoryPage(
    categoryKey: String,
    padding: PaddingValues,
) {
    val navigator = LocalNavigator.current
    val appState = LocalAppState.current
    val isWideScreen = LocalIsWideScreen.current
    val scrollBehavior = RmuixScrollBehavior()
    val lazyListState = rememberLazyListState()
    val snackbarHostState = remember { SnackbarHostState() }
    val category = ExampleCatalog.category(categoryKey)

    Scaffold(
        topBar = {
            SmallTopAppBar(
                title = category?.title ?: "Components",
                subtitle = category?.description ?: "",
                color = RmuixTheme.colorScheme.surface,
                scrollBehavior = scrollBehavior,
                defaultWindowInsetsPadding = false,
                navigationIcon = {
                    BackNavigationIcon(onClick = { navigator.pop() })
                },
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { innerPadding ->
        val contentPadding = pageContentPadding(
            innerPadding,
            PaddingValues(bottom = padding.calculateBottomPadding()),
            isWideScreen,
        )
        Box {
            LazyColumn(
                state = lazyListState,
                modifier = Modifier
                    .pageScrollModifiers(
                        appState.enableScrollEndHaptic,
                        appState.showTopAppBar,
                        scrollBehavior,
                    )
                    .fillMaxWidth(),
                contentPadding = contentPadding,
            ) {
                when (categoryKey) {
                    "containers" -> {
                        basicComponentSection()
                        basicComponentGroupSection()
                        cardSection()
                        emptyStateSection()
                    }

                    "text-and-icons" -> textAndIconSection()

                    "buttons" -> {
                        buttonSection()
                        loadingButtonSection()
                        segmentButtonSection()
                        editToolbarSection()
                    }

                    "input" -> {
                        textFieldSection()
                        codeInputSection()
                        singleInputCardSection()
                        numberPickerSection()
                        datePickerSection()
                        timePickerSection()
                        timeLimitPickerSection()
                        calendarPickerSection()
                        simpleLockSection()
                        numericKeyboardSection()
                        lockPatternViewSection()
                    }

                    "selection" -> {
                        switchSection()
                        checkboxSection()
                        radioButtonSection()
                        sliderSection()
                        verticalSeekBarSection()
                        chipSection()
                    }

                    "navigation" -> {
                        navigationBarSection()
                        sideNavigationDrawerSection()
                        tabRowSection()
                        otherPageSection()
                    }

                    "feedback" -> {
                        progressIndicatorSection()
                        pageIndicatorSection()
                        snackbarSection(snackbarHostState)
                        tooltipSection()
                        topTipsSection()
                        redDotSection()
                        scrollBarSection()
                        alphabetIndexBarSection()
                    }

                    "overlays" -> {
                        dialogSection()
                        bottomSheetSection()
                        glassDialogSection()
                        dropdownSection()
                        spinnerSection()
                    }

                    "preferences" -> {
                        appInfoPreferenceSection()
                        jumpSection()
                    }

                    "media" -> {
                        roundImageSection()
                        colorPickerSection()
                        imageCropperSection()
                        spotlightSection()
                        blurSection()
                    }

                    else -> {
                        item(key = "unknownCategory") {
                            Spacer(modifier = Modifier.height(12.dp))
                        }
                    }
                }
                item { Spacer(modifier = Modifier.height(12.dp)) }
            }
            VerticalScrollBar(
                adapter = rememberScrollBarAdapter(lazyListState),
                modifier = Modifier.align(Alignment.CenterEnd).fillMaxHeight(),
                trackPadding = contentPadding,
            )
        }
    }
}
