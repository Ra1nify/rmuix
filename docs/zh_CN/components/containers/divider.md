# Divider

`Divider` 是 Rmuix 中的基础布局组件，用于在列表和布局中分隔内容。提供了水平分割线和垂直分割线两种形式。

<div style="position: relative; height: 160px; border-radius: 10px; overflow: hidden; border: 1px solid #777;">
    <iframe id="demoIframe" style="position: absolute; top: 0; left: 0; width: 100%; height: 100%; border: none;" src="../../../compose/index.html?id=divider" title="Demo" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" referrerpolicy="strict-origin-when-cross-origin"></iframe>
</div>

## Stock ColorOS 静态复核（2026-11-12）

本轮读 Settings APK 的 `content_divider_width` 与 `couiColorDivider` 颜色角色。

- 设置页细线是 `content_divider_width` = **0.33dp**；组件默认是 0.75dp，比官方每条分隔线都粗。
- 官方颜色是 `couiColorDivider` → `coui_color_divider` / `coui_color_divider_dark` = `#1F000000` / `#33FFFFFF`。组件原先用主题的 `dividerLine` 角色（`#0D000000` / `#1AFFFFFF`），强度大约只有一半。
- 两个默认值现在都来自 `DividerStockValues`；所有消费 `DividerDefaults.Thickness` / `DividerColor` 的组件都会继承官方值。

**BREAKING：** 分隔线厚度 0.75dp → 0.33dp，颜色改为 `#1F000000` / `#33FFFFFF`（主题的 `dividerLine` 角色对其他用途仍是 `#0D000000` / `#1AFFFFFF`）。

app bar 自己的分隔线由 `TopAppBar` 建模：水平内缩从 `coui_appbar_divider_expanded_margin_horizontal`（24dp）插值到 `..._collapsed_...`（0dp），alpha 同步 0 → 1，两者的进度都是 `fraction = 累计滚动距离 / 展开态栏高`（钳到 1，`COUIDividerAppBarLayout.onDividerChanged()`，`getDividerScrollRange()` 即 `getMeasuredHeight()`），也就是随内容滚动淡入，而不是跟随折叠进度。**仍待复核：** 部分页面用 `coui_component_card_instruction_divider_width`（24dp）画短横线，本组件无法表达。

**短说明线：** 部分页面用 `coui_component_card_instruction_divider_width` = **24dp** 的短线
（`coui_component_card_instruction_divider.xml`、`CardInstructionDescriptionAdapter`）；
`HorizontalDivider` / `VerticalDivider` 现在支持 `length = 24.dp`（默认 `null` 仍撑满父容器）。
## 引入

```kotlin
import io.github.ra1nify.rmuix.kmp.basic.HorizontalDivider // 水平分割线
import io.github.ra1nify.rmuix.kmp.basic.VerticalDivider  // 垂直分割线
```

## 基本用法

### 水平分割线

水平分割线用于分隔垂直排列的内容：

```kotlin
Column {
    Text("上方内容")
    HorizontalDivider()
    Text("下方内容")
}
```

### 垂直分割线

垂直分割线用于分隔水平排列的内容：

```kotlin
Row {
    Text("左侧内容")
    VerticalDivider()
    Text("右侧内容")
}
```

## 属性

### HorizontalDivider 属性

| 属性名    | 类型     | 说明                 | 默认值                       | 是否必须 |
| --------- | -------- | -------------------- | ---------------------------- | -------- |
| modifier  | Modifier | 应用于分割线的修饰符 | Modifier                     | 否       |
| thickness | Dp       | 分割线的厚度         | DividerDefaults.Thickness    | 否       |
| color     | Color    | 分割线的颜色         | DividerDefaults.DividerColor | 否       |
| startIndent   | Dp       | 行首方向的内缩（0 = 通栏）                     | DividerDefaults.StartIndent  | No       |
| endIndent     | Dp       | 行尾方向的内缩                                          | DividerDefaults.EndIndent    | No       |
| length        | Dp?      | 固定线长；`null` 撑满父宽                       | null                         | No       |

### VerticalDivider 属性

| 属性名    | 类型     | 说明                 | 默认值                       | 是否必须 |
| --------- | -------- | -------------------- | ---------------------------- | -------- |
| modifier  | Modifier | 应用于分割线的修饰符 | Modifier                     | 否       |
| thickness | Dp       | 分割线的厚度         | DividerDefaults.Thickness    | 否       |
| color     | Color    | 分割线的颜色         | DividerDefaults.DividerColor | 否       |
| startIndent | Dp     | 顶部内缩             | DividerDefaults.StartIndent  | 否       |
| endIndent | Dp       | 底部内缩             | DividerDefaults.EndIndent    | 否       |
| length    | Dp?      | 固定线长；`null` 撑满父高 | null                     | 否       |

### DividerDefaults 对象

DividerDefaults 对象提供了分割线组件的默认值。

#### 常量

| 常量名       | 类型  | 说明             | 默认值                             |
| ------------ | ----- | ---------------- | ---------------------------------- |
| Thickness    | Dp    | 分割线的默认厚度（`content_divider_width`） | 0.33.dp |
| DividerColor | Color | 分割线的默认颜色（`couiColorDivider`） | `#1F000000` / `#33FFFFFF` |
| StartIndent   | Dp    | 默认无缩进；单独的分隔线通栏。ColorOS 卡片行改用 `BasicComponentGroupDefaults.DividerStart`（32dp） | 0.dp |
| EndIndent     | Dp    | 默认无缩进，见 `StartIndent` | 0.dp |

## 进阶用法

### 自定义厚度的分割线

```kotlin
Column {
    Text("上方内容")
    HorizontalDivider(
        thickness = 2.dp
    )
    Text("下方内容")
}
```

### 自定义颜色的分割线

```kotlin
Column {
    Text("上方内容")
    HorizontalDivider(
        color = Color.Red
    )
    Text("下方内容")
}
```

### 带内边距的分割线

```kotlin
Column {
    Text("上方内容")
    HorizontalDivider(
        modifier = Modifier.padding(horizontal = 16.dp)
    )
    Text("下方内容")
}
```

### 使用垂直分割线分隔按钮

```kotlin
Row(
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically
) {
    Button(onClick = { /* 处理点击事件 */ }) {
        Text("取消")
    }
    VerticalDivider(
        modifier = Modifier.height(24.dp)
    )
    Button(onClick = { /* 处理点击事件 */ }) {
        Text("确认")
    }
}
```
