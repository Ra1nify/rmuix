# Getting Started

Supported platforms: **Android** / **Desktop (JVM)** / **iOS** / **WasmJs** / **Js** / **macOS (Native)**

::: warning
This library is experimental, and APIs may change in future versions without notice.
:::

## Adding Dependencies

To use Rmuix in your project, follow these steps to add dependencies:

### Gradle (Kotlin DSL)

1. Add the following to the root `settings.gradle.kts` file (usually already included):

```kotlin
repositories {
    mavenCentral()
}
```

2. Check the latest version on Maven Central:
   [![Maven Central](https://img.shields.io/maven-central/v/io.github.ra1nify.rmuix.kmp/rmuix-ui)](https://search.maven.org/search?q=g:io.github.ra1nify.rmuix.kmp)

3. Add dependencies to your project's `build.gradle.kts`:

Rmuix is composed of several modules that can be used independently:

| Module             | Description                                          |
| ------------------ | ---------------------------------------------------- |
| `rmuix-ui`         | Core UI component library                            |
| `rmuix-preference` | Preference components library, depends on `rmuix-ui` |
| `rmuix-icons`      | Extended icon library, can be used independently     |
| `rmuix-blur`       | Blur effect library, can be used independently       |
| `rmuix-squircle`   | Squircle shapes library, can be used independently   |
| `rmuix-nav`        | Navigation library, can be used independently        |
| `rmuix-shader`     | Low-level runtime shader / render effect abstraction |
| `rmuix-window`     | Desktop window & caption bar styling library (Desktop JVM) |

- For Compose Multiplatform projects:

```kotlin
kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation("io.github.ra1nify.rmuix.kmp:rmuix-ui:<version>")
            // Optional: Add rmuix-preference for preference components
            implementation("io.github.ra1nify.rmuix.kmp:rmuix-preference:<version>")
            // Optional: Add rmuix-icons for more icons
            implementation("io.github.ra1nify.rmuix.kmp:rmuix-icons:<version>")
            // Optional: Add rmuix-blur for blur effects
            implementation("io.github.ra1nify.rmuix.kmp:rmuix-blur:<version>")
            // Optional: Add rmuix-squircle for squircle (smooth rounded corner) shapes
            implementation("io.github.ra1nify.rmuix.kmp:rmuix-squircle:<version>")
            // Optional: Add rmuix-nav for navigation
            implementation("io.github.ra1nify.rmuix.kmp:rmuix-nav:<version>")
        }
        // Optional: Add rmuix-window for desktop window and caption bar styling (Desktop JVM)
        desktopMain.dependencies {
            implementation("io.github.ra1nify.rmuix.kmp:rmuix-window:<version>")
        }
    }
}
```

- For Android Compose projects:

```kotlin
dependencies {
    implementation("io.github.ra1nify.rmuix.kmp:rmuix-ui-android:<version>")
    // Optional: Add rmuix-preference for preference components
    implementation("io.github.ra1nify.rmuix.kmp:rmuix-preference-android:<version>")
    // Optional: Add rmuix-icons for more icons
    implementation("io.github.ra1nify.rmuix.kmp:rmuix-icons-android:<version>")
    // Optional: Add rmuix-blur for blur effects (requires minSdk 33)
    implementation("io.github.ra1nify.rmuix.kmp:rmuix-blur-android:<version>")
    // Optional: Add rmuix-squircle for squircle (smooth rounded corner) shapes
    implementation("io.github.ra1nify.rmuix.kmp:rmuix-squircle-android:<version>")
    // Optional: Add rmuix-nav for navigation
    implementation("io.github.ra1nify.rmuix.kmp:rmuix-nav-android:<version>")
}
```

- For other projects, add platform-specific dependencies as needed:

```kotlin
implementation("io.github.ra1nify.rmuix.kmp:rmuix-ui-iosarm64:<version>")
implementation("io.github.ra1nify.rmuix.kmp:rmuix-ui-iossimulatorarm64:<version>")
implementation("io.github.ra1nify.rmuix.kmp:rmuix-ui-macosarm64:<version>")
implementation("io.github.ra1nify.rmuix.kmp:rmuix-ui-desktop:<version>")
implementation("io.github.ra1nify.rmuix.kmp:rmuix-ui-wasmjs:<version>")
implementation("io.github.ra1nify.rmuix.kmp:rmuix-ui-js:<version>")
// Optional: Add rmuix-preference
implementation("io.github.ra1nify.rmuix.kmp:rmuix-preference-iosarm64:<version>")
implementation("io.github.ra1nify.rmuix.kmp:rmuix-preference-iossimulatorarm64:<version>")
implementation("io.github.ra1nify.rmuix.kmp:rmuix-preference-macosarm64:<version>")
implementation("io.github.ra1nify.rmuix.kmp:rmuix-preference-desktop:<version>")
implementation("io.github.ra1nify.rmuix.kmp:rmuix-preference-wasmjs:<version>")
implementation("io.github.ra1nify.rmuix.kmp:rmuix-preference-js:<version>")
// Optional: Add rmuix-blur
implementation("io.github.ra1nify.rmuix.kmp:rmuix-blur-iosarm64:<version>")
implementation("io.github.ra1nify.rmuix.kmp:rmuix-blur-iossimulatorarm64:<version>")
implementation("io.github.ra1nify.rmuix.kmp:rmuix-blur-macosarm64:<version>")
implementation("io.github.ra1nify.rmuix.kmp:rmuix-blur-desktop:<version>")
implementation("io.github.ra1nify.rmuix.kmp:rmuix-blur-wasmjs:<version>")
implementation("io.github.ra1nify.rmuix.kmp:rmuix-blur-js:<version>")
// Optional: Add rmuix-nav
implementation("io.github.ra1nify.rmuix.kmp:rmuix-nav-iosarm64:<version>")
implementation("io.github.ra1nify.rmuix.kmp:rmuix-nav-iossimulatorarm64:<version>")
implementation("io.github.ra1nify.rmuix.kmp:rmuix-nav-macosarm64:<version>")
implementation("io.github.ra1nify.rmuix.kmp:rmuix-nav-desktop:<version>")
implementation("io.github.ra1nify.rmuix.kmp:rmuix-nav-wasmjs:<version>")
implementation("io.github.ra1nify.rmuix.kmp:rmuix-nav-js:<version>")
// Optional: Add rmuix-icons
implementation("io.github.ra1nify.rmuix.kmp:rmuix-icons-iosarm64:<version>")
implementation("io.github.ra1nify.rmuix.kmp:rmuix-icons-iossimulatorarm64:<version>")
implementation("io.github.ra1nify.rmuix.kmp:rmuix-icons-macosarm64:<version>")
implementation("io.github.ra1nify.rmuix.kmp:rmuix-icons-desktop:<version>")
implementation("io.github.ra1nify.rmuix.kmp:rmuix-icons-wasmjs:<version>")
implementation("io.github.ra1nify.rmuix.kmp:rmuix-icons-js:<version>")
// Optional: Add rmuix-squircle
implementation("io.github.ra1nify.rmuix.kmp:rmuix-squircle-iosarm64:<version>")
implementation("io.github.ra1nify.rmuix.kmp:rmuix-squircle-iossimulatorarm64:<version>")
implementation("io.github.ra1nify.rmuix.kmp:rmuix-squircle-macosarm64:<version>")
implementation("io.github.ra1nify.rmuix.kmp:rmuix-squircle-desktop:<version>")
implementation("io.github.ra1nify.rmuix.kmp:rmuix-squircle-wasmjs:<version>")
implementation("io.github.ra1nify.rmuix.kmp:rmuix-squircle-js:<version>")
```

## Basic Usage

### Applying the Rmuix Theme

```kotlin
@Composable
fun AppTheme(
    content: @Composable () -> Unit
) {
    // Available modes: System, Light, Dark, MonetSystem, MonetLight, MonetDark
    val controller = remember { ThemeController(ColorSchemeMode.System) }
    return RmuixTheme(
        controller = controller,
        content = content
    )
}
```

### Using the Rmuix Scaffold

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

::: warning
The Scaffold component provides a suitable container for cross-platform popup windows.
Components such as `OverlayDialog`, `OverlayDropdownPreference`, `OverlaySpinnerPreference`, and `OverlayListPopup` are
all implemented based on this and therefore need to be wrapped by this component.
:::

## API Documentation

- View the [API Documentation](/rmuix/dokka/index.html){target="_blank"},
  generated using Dokka, which contains detailed information about all APIs.
