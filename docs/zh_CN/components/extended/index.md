# 扩展组件

`rmuix-preference` 在 `rmuix-ui` 之上提供设置项与下拉菜单。

## 组件

| 组件 | 描述 | 常见用途 |
| --- | --- | --- |
| [AppInfoPreference](../preferences/appinfopreference)       | 带图标、应用名与版本号的应用头部；长按版本号可复制 | 关于页 |
| [JumpPreference](../preferences/jumppreference)               | 基于 BasicComponent 的带箭头组件                                                   | 指示可点击、导航提示   |
| [SwitchPreference](../preferences/switchpreference)             | 基于 BasicComponent 的开关组件                                                     | 设置项开关、功能启用   |
| [CheckboxPreference](../preferences/checkboxpreference)         | 基于 BasicComponent 的复选框组件                                                   | 多项选择、条款同意     |
| [RadioButtonPreference](../preferences/radiobuttonpreference)   | 基于 BasicComponent 的单选组件                                                     | 单项选择、选项切换     |
| [SliderPreference](../preferences/sliderpreference)             | 基于 BasicComponent 的滑块组件                                                     | 数值调节、音量/亮度    |
| [RangeSliderPreference](../preferences/rangesliderpreference)   | 基于 BasicComponent 的范围滑块组件                                                 | 范围选择、价格筛选     |
| [OverlayDropdownPreference](../preferences/overlaydropdownpreference) | 基于 BasicComponent 的下拉选择器组件（使用 RmuixPopupUtils，需在 `Scaffold` 中使用） | 选项选择、功能列表     |
| [OverlaySpinnerPreference](../preferences/overlayspinnerpreference) | 基于 BasicComponent 的高级选择器组件（使用 RmuixPopupUtils，需在 `Scaffold` 中使用） | 进阶选项选择、功能列表 |
| [OverlayDropdownMenu](../overlays/overlaydropdownmenu)       | 基于 BasicComponent 的动作菜单组件（使用 RmuixPopupUtils，需在 `Scaffold` 中使用） | 动作菜单、多选场景     |
| [OverlayIconDropdownMenu](../overlays/overlayicondropdownmenu) | 基于 IconButton 的动作菜单组件（使用 RmuixPopupUtils，需在 `Scaffold` 中使用）     | 工具栏动作、更多菜单   |
| [OverlayIconCascadingDropdownMenu](../overlays/overlayiconcascadingdropdownmenu) | 基于 IconButton 的二级级联动作菜单组件（使用 RmuixPopupUtils，需在 `Scaffold` 中使用） | 含子菜单的工具栏动作   |
| [WindowDropdownPreference](../preferences/windowdropdownpreference) | 窗口级下拉选择器组件                                                               | 选项选择、功能列表     |
| [WindowSpinnerPreference](../preferences/windowspinnerpreference) | 窗口级高级选择器组件                                                               | 进阶选项选择、功能列表 |
| [WindowDropdownMenu](../overlays/windowdropdownmenu) | 窗口级动作菜单组件                                                                 | 动作菜单、多选场景     |
| [WindowIconDropdownMenu](../overlays/windowicondropdownmenu) | 基于 IconButton 的窗口级动作菜单组件                                               | 工具栏动作、更多菜单   |
| [WindowIconCascadingDropdownMenu](../overlays/windowiconcascadingdropdownmenu) | 基于 IconButton 的窗口级二级级联动作菜单组件                                       | 含子菜单的工具栏动作   |

## 配置

在项目中添加 `rmuix-preference` 依赖：

```kotlin
kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation("io.github.ra1nify.rmuix.kmp:rmuix-preference:<version>")
        }
    }
}
```

Android 项目：

```kotlin
dependencies {
    implementation("io.github.ra1nify.rmuix.kmp:rmuix-preference-android:<version>")
}
```

`rmuix-preference` 会传递引入 `rmuix-ui`。

完整项目配置与平台依赖见[快速开始](../../guide/getting-started)。

## 组件选择与宿主

- `io.github.ra1nify.rmuix.kmp.preference` 中的组件用于设置行。`DropdownPreference` 适合单选值，`SpinnerPreference` 提供更复杂的选项结构。
- `io.github.ra1nify.rmuix.kmp.menu` 中的组件用于动作菜单；菜单项可以分别管理选中状态，不要求共享同一个单选索引。
- `Overlay*` 变体必须位于 Rmuix [Scaffold](../containers/scaffold) 中，由其提供 `RmuixPopupHost`；`Window*` 变体在窗口层显示，不依赖该宿主。

设置行使用 [BasicComponent](../containers/basiccomponent) 作为基础。

## 相关文档

- [主题系统](../../guide/theme)：颜色、文字与主题配置。
