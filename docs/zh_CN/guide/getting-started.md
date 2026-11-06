# 快速开始

当前支持的平台: **Android** / **Desktop(JVM)** / **iOS** / **WasmJs** / **Js** / **macOS(Native)**

::: warning 注意
此库处于实验阶段，API 可能会在未来版本中变更而不另行通知
:::

## 添加依赖

要在您的项目中使用 Rmuix，请按照以下步骤添加依赖：

### Gradle (Kotlin DSL)

1. 在根目录的 settings.gradle.kts 添加（正常情况应已包含）：

```kotlin
repositories {
    mavenCentral()
}
```

2. 检查 Maven Central 当前最新版本：
   [![Maven Central](https://img.shields.io/maven-central/v/io.github.ra1nify.rmuix.kmp/rmuix-ui)](https://search.maven.org/search?q=g:io.github.ra1nify.rmuix.kmp)

3. 在项目的 build.gradle.kts 中添加依赖：

Rmuix 由多个可独立使用的模块组成：

| 模块 | 说明 |
|---|---|
| `rmuix-ui` | 核心 UI 组件库（自动包含 `rmuix-core`） |
| `rmuix-preference` | Preference 组件（SwitchPreference、CheckboxPreference 等），依赖 `rmuix-ui` |
| `rmuix-icons` | 扩展图标库，可独立使用，也可与 `rmuix-ui` 同时使用（自动包含 `rmuix-core`） |
| `rmuix-blur` | 模糊效果库，可独立使用 |
| `rmuix-squircle` | 平滑圆角形状，可独立使用（已由 `rmuix-ui` 传递包含） |
| `rmuix-shader` | 底层运行时着色器 / 渲染效果抽象，已由 `rmuix-blur` / `rmuix-squircle` 传递包含 |
| `rmuix-nav` | 自包含导航库，可独立使用 |
| `rmuix-window` | 桌面端窗口与自绘标题栏样式库（仅支持 Desktop JVM） |

- 在 Compose Multiplatform 项目目录的 build.gradle.kts 中：

```kotlin
kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation("io.github.ra1nify.rmuix.kmp:rmuix-ui:<version>")
            // 可选：添加 rmuix-preference 以获取 Preference 组件
            implementation("io.github.ra1nify.rmuix.kmp:rmuix-preference:<version>")
            // 可选：添加 rmuix-icons 以获取更多图标
            implementation("io.github.ra1nify.rmuix.kmp:rmuix-icons:<version>")
            // 可选：添加 rmuix-blur 以获取模糊效果
            implementation("io.github.ra1nify.rmuix.kmp:rmuix-blur:<version>")
            // 可选：添加 rmuix-squircle 以获取平滑圆角形状
            implementation("io.github.ra1nify.rmuix.kmp:rmuix-squircle:<version>")
            // 可选：添加 rmuix-nav 以获取导航支持
            implementation("io.github.ra1nify.rmuix.kmp:rmuix-nav:<version>")
        }
        // 可选：添加 rmuix-window 以获取桌面端窗口与标题栏支持（仅 Desktop JVM）
        desktopMain.dependencies {
            implementation("io.github.ra1nify.rmuix.kmp:rmuix-window:<version>")
        }
    }
}
```

- 在 Android Compose 项目目录的 build.gradle.kts 中：

```kotlin
dependencies {
    implementation("io.github.ra1nify.rmuix.kmp:rmuix-ui-android:<version>")
    // 可选：添加 rmuix-preference 以获取 Preference 组件
    implementation("io.github.ra1nify.rmuix.kmp:rmuix-preference-android:<version>")
    // 可选：添加 rmuix-icons 以获取更多图标
    implementation("io.github.ra1nify.rmuix.kmp:rmuix-icons-android:<version>")
    // 可选：添加 rmuix-blur 以获取模糊效果（需要 minSdk 33）
    implementation("io.github.ra1nify.rmuix.kmp:rmuix-blur-android:<version>")
    // 可选：添加 rmuix-squircle 以获取平滑圆角形状
    implementation("io.github.ra1nify.rmuix.kmp:rmuix-squircle-android:<version>")
    // 可选：添加 rmuix-nav 以获取导航支持
    implementation("io.github.ra1nify.rmuix.kmp:rmuix-nav-android:<version>")
}
```

- 在其他常规项目中使用，则只需要根据需要添加对应平台后缀的依赖即可：

```kotlin
implementation("io.github.ra1nify.rmuix.kmp:rmuix-ui-iosarm64:<version>")
implementation("io.github.ra1nify.rmuix.kmp:rmuix-ui-iossimulatorarm64:<version>")
implementation("io.github.ra1nify.rmuix.kmp:rmuix-ui-macosarm64:<version>")
implementation("io.github.ra1nify.rmuix.kmp:rmuix-ui-desktop:<version>")
implementation("io.github.ra1nify.rmuix.kmp:rmuix-ui-wasmjs:<version>")
implementation("io.github.ra1nify.rmuix.kmp:rmuix-ui-js:<version>")
// 可选：添加 rmuix-preference
implementation("io.github.ra1nify.rmuix.kmp:rmuix-preference-iosarm64:<version>")
implementation("io.github.ra1nify.rmuix.kmp:rmuix-preference-iossimulatorarm64:<version>")
implementation("io.github.ra1nify.rmuix.kmp:rmuix-preference-macosarm64:<version>")
implementation("io.github.ra1nify.rmuix.kmp:rmuix-preference-desktop:<version>")
implementation("io.github.ra1nify.rmuix.kmp:rmuix-preference-wasmjs:<version>")
implementation("io.github.ra1nify.rmuix.kmp:rmuix-preference-js:<version>")
// 可选：添加 rmuix-blur
implementation("io.github.ra1nify.rmuix.kmp:rmuix-blur-iosarm64:<version>")
implementation("io.github.ra1nify.rmuix.kmp:rmuix-blur-iossimulatorarm64:<version>")
implementation("io.github.ra1nify.rmuix.kmp:rmuix-blur-macosarm64:<version>")
implementation("io.github.ra1nify.rmuix.kmp:rmuix-blur-desktop:<version>")
implementation("io.github.ra1nify.rmuix.kmp:rmuix-blur-wasmjs:<version>")
implementation("io.github.ra1nify.rmuix.kmp:rmuix-blur-js:<version>")
// 可选：添加 rmuix-nav
implementation("io.github.ra1nify.rmuix.kmp:rmuix-nav-iosarm64:<version>")
implementation("io.github.ra1nify.rmuix.kmp:rmuix-nav-iossimulatorarm64:<version>")
implementation("io.github.ra1nify.rmuix.kmp:rmuix-nav-macosarm64:<version>")
implementation("io.github.ra1nify.rmuix.kmp:rmuix-nav-desktop:<version>")
implementation("io.github.ra1nify.rmuix.kmp:rmuix-nav-wasmjs:<version>")
implementation("io.github.ra1nify.rmuix.kmp:rmuix-nav-js:<version>")
// 可选：添加 rmuix-icons
implementation("io.github.ra1nify.rmuix.kmp:rmuix-icons-iosarm64:<version>")
implementation("io.github.ra1nify.rmuix.kmp:rmuix-icons-iossimulatorarm64:<version>")
implementation("io.github.ra1nify.rmuix.kmp:rmuix-icons-macosarm64:<version>")
implementation("io.github.ra1nify.rmuix.kmp:rmuix-icons-desktop:<version>")
implementation("io.github.ra1nify.rmuix.kmp:rmuix-icons-wasmjs:<version>")
implementation("io.github.ra1nify.rmuix.kmp:rmuix-icons-js:<version>")
// 可选：添加 rmuix-squircle
implementation("io.github.ra1nify.rmuix.kmp:rmuix-squircle-iosarm64:<version>")
implementation("io.github.ra1nify.rmuix.kmp:rmuix-squircle-iossimulatorarm64:<version>")
implementation("io.github.ra1nify.rmuix.kmp:rmuix-squircle-macosarm64:<version>")
implementation("io.github.ra1nify.rmuix.kmp:rmuix-squircle-desktop:<version>")
implementation("io.github.ra1nify.rmuix.kmp:rmuix-squircle-wasmjs:<version>")
implementation("io.github.ra1nify.rmuix.kmp:rmuix-squircle-js:<version>")
```

## 基本用法

### 应用 Rmuix 主题

```kotlin
@Composable
fun AppTheme(
    content: @Composable () -> Unit
) {
    // 可用模式: System, Light, Dark, MonetSystem, MonetLight, MonetDark
    val controller = remember { ThemeController(ColorSchemeMode.System) }
    return RmuixTheme(
        controller = controller,
        content = content
    )
}
```

### 使用 Rmuix 脚手架

```kotlin
Scaffold(
    topBar = {
        // TopBar
    },
    bottomBar = {
        // BottomBar
    },
    floatingActionButton = {
        // FloatingActionButton
    },
    floatingToolbar = {
        // FloatingToolbar
    }
) {
    // Content...
}
```

::: warning 注意
Scaffold 组件为跨平台提供了一个合适的弹出窗口的容器。`OverlayDialog`、`OverlayDropdownPreference`、`OverlaySpinnerPreference`、
`OverlayListPopup` 等组件都基于此实现弹出窗口，因此都需要被该组件包裹。
:::

## API 文档

- 查看 [API 文档](/rmuix/dokka/index.html){target="_blank"}，此文档使用 Dokka 生成，包含了所有 API
  的详细信息。
