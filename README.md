## Rmuix

A UI library for Compose Multiplatform.

> This library is experimental. APIs may change without notice.

[![Kotlin](https://img.shields.io/badge/kotlin-2.4.21-7F52FF)](https://kotlinlang.org/)
[![Compose Multiplatform](https://img.shields.io/badge/compose-1.12.2-4285F4)](https://kotlinlang.org/compose-multiplatform/)
[![Maven Central](https://img.shields.io/maven-central/v/io.github.ra1nify.rmuix.kmp/rmuix-ui)](https://search.maven.org/search?q=g:io.github.ra1nify.rmuix.kmp)
[![License](https://img.shields.io/github/license/compose-rmuix-ui/rmuix)](LICENSE)

### Supported Platforms

![Android](https://img.shields.io/badge/Android-3DDC84?logo=android&logoColor=white)
![iOS](https://img.shields.io/badge/iOS-000000?logo=apple&logoColor=white)
![macOS](https://img.shields.io/badge/macOS-000000?logo=apple&logoColor=white)
![Desktop](https://img.shields.io/badge/Desktop-007396?logo=openjdk&logoColor=white)
![Web](https://img.shields.io/badge/Web-4285F4?logo=googlechrome&logoColor=white)

### Demos

[![Web](https://img.shields.io/badge/Demo-Web-4285F4?logo=googlechrome&logoColor=white)](https://compose-rmuix-ui.github.io/rmuix-wasmJs/)
[![Other](https://img.shields.io/badge/Demo-Other-2088FF?logo=githubactions&logoColor=white)](https://github.com/compose-rmuix-ui/rmuix/actions/workflows/example.yml)

### Modules

| Module             | Description                                                |
| ------------------ | ---------------------------------------------------------- |
| `rmuix-ui`         | Core UI component library                                  |
| `rmuix-preference` | Preference components library, depends on `rmuix-ui`       |
| `rmuix-icons`      | Extended icon library, can be used independently           |
| `rmuix-blur`       | Blur effect library, can be used independently             |
| `rmuix-squircle`   | Squircle shapes library, can be used independently         |
| `rmuix-nav`        | Navigation library, can be used independently              |
| `rmuix-window`     | Desktop window & caption bar styling library (Desktop JVM) |
| `rmuix-shader`     | Low-level runtime shader / render effect abstraction       |

### Getting Started

```diff
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
            // Other dependencies...
        }
        // Optional: Add rmuix-window for desktop window and caption bar styling (Desktop JVM)
        desktopMain.dependencies {
            implementation("io.github.ra1nify.rmuix.kmp:rmuix-window:<version>")
        }
        // Other sourceSets...
    }
    // Other configurations...
}
```

### Usage

- Provide a color scheme via `RmuixTheme(colors = ...)`, e.g., `lightColorScheme()` or `darkColorScheme()`.

```kotlin
@Composable
fun AppTheme(
    content: @Composable () -> Unit
) {
    val colors = if (isSystemInDarkTheme()) darkColorScheme() else lightColorScheme()
    return RmuixTheme(
        colors = colors,
        content = content
    )
}
```

- Use `ThemeController` to manage modes and enable Monet dynamic colors. Pass `keyColor` to set a custom seed color.

```kotlin
@Composable
fun AppTheme(
    content: @Composable () -> Unit
) {
    val controller = remember {
        ThemeController(
            ColorSchemeMode.MonetSystem,
            keyColor = Color(0xFF3482FF)
        )
    }
    return RmuixTheme(
        controller = controller,
        content = content
    )
}
```

### Screenshots

<table>
  <tr>
    <td><a href="https://github.com/compose-rmuix-ui/rmuix/blob/main/assets/001.webp?raw=true"><img src="https://github.com/compose-rmuix-ui/rmuix/blob/main/assets/001.webp?raw=true" width="300" alt="Screenshot 001"/></a></td>
    <td><a href="https://github.com/compose-rmuix-ui/rmuix/blob/main/assets/002.webp?raw=true"><img src="https://github.com/compose-rmuix-ui/rmuix/blob/main/assets/002.webp?raw=true" width="300" alt="Screenshot 002"/></a></td>
    <td><a href="https://github.com/compose-rmuix-ui/rmuix/blob/main/assets/003.webp?raw=true"><img src="https://github.com/compose-rmuix-ui/rmuix/blob/main/assets/003.webp?raw=true" width="300" alt="Screenshot 003"/></a></td>
  </tr>
  <tr>
    <td><a href="https://github.com/compose-rmuix-ui/rmuix/blob/main/assets/004.webp?raw=true"><img src="https://github.com/compose-rmuix-ui/rmuix/blob/main/assets/004.webp?raw=true" width="300" alt="Screenshot 004"/></a></td>
    <td><a href="https://github.com/compose-rmuix-ui/rmuix/blob/main/assets/005.webp?raw=true"><img src="https://github.com/compose-rmuix-ui/rmuix/blob/main/assets/005.webp?raw=true" width="300" alt="Screenshot 005"/></a></td>
    <td><a href="https://github.com/compose-rmuix-ui/rmuix/blob/main/assets/006.webp?raw=true"><img src="https://github.com/compose-rmuix-ui/rmuix/blob/main/assets/006.webp?raw=true" width="300" alt="Screenshot 006"/></a></td>
  </tr>
</table>
