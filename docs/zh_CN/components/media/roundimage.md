# RoundImage

ColorOS 圆角图片：官方 `COUIRoundImageView`（`com.coui.appcompat.imageview`），设置页里的 36dp 图标
（`couiType="round"`）大量使用（dump 中 180 个布局引用）。

## 引入

```kotlin
import io.github.ra1nify.rmuix.kmp.basic.RoundImage
import io.github.ra1nify.rmuix.kmp.basic.RoundImageType
```

## 基本用法

```kotlin
RoundImage(
    painter = painterResource(Res.drawable.account_avatar),
    contentDescription = null,
    modifier = Modifier.size(36.dp),
    type = RoundImageType.Round,
    showBorder = true,
)
```

## 参数

| 参数名 | 类型 | 说明 | 默认值 |
| :--- | :--- | :--- | :--- |
| painter | Painter | 要绘制的图片 | - |
| contentDescription | String? | 无障碍描述 | - |
| modifier | Modifier | 应用到图片；官方尺寸由布局决定 | Modifier |
| type | RoundImageType | `Circle` / `Round` / `Shadow`（`COUIRoundImageView.mType`） | Round |
| radius | Dp | `Round` 圆角半径（`couiBorderRadius`，默认 1dp） | 1dp |
| showBorder | Boolean | 是否绘制官方 `mOutCircle` 外圈（`onDraw()` 唯一绘制的描边） | false |
| contentScale | ContentScale | 图片填充方式 | ContentScale.Crop |
| colors | RoundImageColors | 描边颜色 | RoundImageDefaults.colors() |

## RoundImageDefaults

| 名称 | 类型 | 来源 | 值 |
| :--- | :--- | :--- | :--- |
| Radius | Dp | `couiBorderRadius` 默认圆角半径 | 1dp |
| CircleFallbackSize | Dp | `coui_roundimageview_default_radius` 的 CIRCLE 回退宽度 | 42dp |
| BorderWidth | Dp | `COUIRoundImageView.mBorderWidth`（2px，同时是 `mOutCircle` 的描边宽度） | 1dp |
| ShadowRingWidth | Dp | `BORDER_CIRCLE_WIDTH`（5px） | 2dp |
| ShadowSourceSize | Dp | `coui_roundimageView_src_width` | 60dp |

## 颜色

| 槽位 | 来源 | 浅色 | 深色 |
| :--- | :--- | :--- | :--- |
| borderColor | `coui_border`（`mBorderPaint`）：**死 token**——`initBorderPaint()` 初始化但 `onDraw()` 从不绘制 | `#26000000` | `#26FFFFFF` |
| outCircleColor | `coui_roundimageview_outcircle_color` / `_dark`：`mOutCircle` 描边与 `Shadow` 外圈 | `#1A000000` | `#33FFFFFF` |

**BREAKING：** `showBorder` 现在用 `outCircleColor` 绘制——即官方 `mOutCircle`（`setOutCircleColor`
作用对象，描边宽度 `mBorderWidth`）；此前用的是 `colors.borderColor`，而官方 `onDraw()` 从不绘制它。

## 已知差异

- 官方的 `BitmapShader` 矩阵/裁剪交给 Compose `Image` 的 `contentScale` 处理，对 dump 使用的
  `Crop`/`Fit` 场景等价。
- `Shadow` 类型用 drop shadow 近似官方 `coui_round_image_view_shadow` 外圈。
- 外圈描边宽度固定为 `BorderWidth`（`mOutCircle.setStrokeWidth(mBorderWidth)`）；`COUIRoundImageView`
  只提供 `setOutCircleColor`，**没有** `setOutCircleWidth`，因此没有宽度 token 可开放。
