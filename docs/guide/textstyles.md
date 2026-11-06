# Text Styles

This page documents all text styles provided by Rmuix, based on the actual implementation.

## Using Text Styles

- Use `RmuixTheme.textStyles.<name>` in composables.
- The color of all styles is set from `RmuixTheme.colorScheme.onBackground` at runtime.

## Styles

| Style Name     | Size | Weight | Line Height |
|----------------|------|--------|-------------|
| `main`         | 17sp | Normal | -           |
| `paragraph`    | 17sp | Normal | 1.2em       |
| `body1`        | 16sp | Normal | -           |
| `body2`        | 14sp | Normal | -           |
| `button`       | 16sp | Medium | -           |
| `footnote1`    | 13sp | Normal | -           |
| `footnote2`    | 12sp | Normal | -           |
| `headline1`    | 17sp | Normal | -           |
| `headline2`    | 16sp | Normal | -           |
| `headlineLarge`| 28sp | Medium | -           |
| `headlineSmall`| 18sp | Medium | -           |
| `subtitle`     | 14sp | Bold   | -           |
| `title1`       | 32sp | Normal | -           |
| `title2`       | 24sp | Normal | -           |
| `title3`       | 20sp | Normal | -           |
| `title4`       | 18sp | Normal | -           |

## Usage

```kotlin
Text(
    text = "Title",
    style = RmuixTheme.textStyles.title2
)

Text(
    text = "Body",
    style = RmuixTheme.textStyles.body1
)
```

## Customization

- Override styles via `defaultTextStyles(...)` and pass to `RmuixTheme(textStyles = ...)`.

```kotlin
val customTextStyles = defaultTextStyles(
    title1 = TextStyle(
        fontSize = 36.sp,
        fontWeight = FontWeight.Bold
    )
)

val controller = remember { ThemeController(ColorSchemeMode.System) }
RmuixTheme(
    controller = controller,
    textStyles = customTextStyles
) { /* Content */ }
```
