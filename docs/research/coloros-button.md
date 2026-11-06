# ColorOS Button 逆向调研

对应 rmuix `rmuix-ui/.../basic/Button.kt`。ColorOS 实现是 `com.coui.appcompat.button.COUIButton`
（1313 行，混淆），样式经 `buttonType` 属性驱动。

## 1. ButtonType 体系（源码枚举）

```java
public enum ButtonType {
    PRIMARY(1),
    SECONDARY(2),
    TRANSPARENT(3),
    AI_PAYMENT(4),
    AI_PAYMENT_SECONDARY(5),
    UNKNOWN(-1);
}
```

与 rmuix 的对应（rmuix 只有颜色工厂，无类型枚举）：

| ColorOS | rmuix 等价 |
| :--- | :--- |
| PRIMARY | `buttonColorsPrimary()` |
| SECONDARY | `buttonColors()`（默认） |
| TRANSPARENT | `textButtonColors()` 近似 |
| AI_PAYMENT* | 无对应（相机专属支付样式，跳过） |

## 2. 三档尺寸

| 档位 | 高 | 最小宽 | 文字 | 圆角 |
| :--- | ---: | ---: | ---: | ---: |
| large | **44dp** | **132dp** | **16sp** | — |
| small | **28dp** | **52dp** | **14sp** | — |
| desc（带描述） | **44dp** | — | **16sp** / 子行 10sp | — |

**圆角**（`coui_btn_drawable_radius_*`）：

| 档位 | 半径 |
| :--- | ---: |
| large | **21.5dp** |
| small | **13.0dp** |

注意 21.5 ≈ 44/2，即 large 档默认**胶囊**。`couiRoundType` 属性可覆盖（0=圆角矩形，见
`getRoundType()`）。

## 3. 内边距与文字

| 资源 | 值 |
| :--- | ---: |
| `coui_btn_padding_horizontal` | **12.0dp** |
| `coui_btn_padding_vertical` | **8.0dp** |
| `coui_btn_text_size` | **16.0sp** |
| `coui_btn_small_text_size` | 14.0sp |
| `coui_btn_desc_padding_horizontal` | 12.0dp |
| `coui_btn_desc_padding_vertical` | 4.0dp |
| `coui_btn_desc_sub_text_size` | 10.0sp |
| `coui_checkbox_margin_between_text_drawable` | 8.0dp（图标与文字） |

## 4. 颜色（`c()` 方法分支）

### PRIMARY

| 状态 | 颜色 |
| :--- | :--- |
| 启用 | `couiColorContainerTheme`（主题强调色） |
| 禁用 | `couiColorDisable`（主题禁用色） |
| 按下（blur 开） | `TYPE_CONTENT_PRIMARY_BUTTON` 材质 |

### SECONDARY

| 状态 | 颜色 |
| :--- | :--- |
| 启用（非 blur） | `l(ctx)` = `couiColorContainer8` 叠加 `couiColorBackgroundElevated` |
| 按下（blur 开，暗色） | `Color.argb(51, 白)` = **20% 白** |
| 按下（blur 开，亮色） | `Color.argb(166, 白)` = **65% 白** |
| 禁用 | `couiColorDisable` |

`coui_color_container8` 实测：**亮 `#14000000`（8% 黑）/ 暗 `#1AFFFFFF`（10% 白）**。
`coui_color_background_elevated`：**亮 `#FFFFFFFF` / 暗 `#FF1E1E1E`**。

即 SECONDARY 启用态 ≈ 亮色 8% 黑、暗色 10% 白浮在抬升背景上。

### TRANSPARENT

| 状态 | 颜色 |
| :--- | :--- |
| 启用（blur 开，暗色） | `argb(25, 白)` = **10% 白** |
| 启用（blur 开，亮色） | `argb(51, 白)` = **20% 白** |
| 其他 | `coui_button_translate_color` |

## 5. 高级效果（源码发现，rmuix 未实现）

- **SpotLightEffect**：PRIMARY 用 `TYPE_OPAQUE_MEDIUM_1`，SECONDARY 用 `TYPE_OPAQUE_MEDIUM_2`，
  TRANSPARENT 用 `TYPE_TRANSPARENT_MEDIUM_1`（高光类型随类型变化）
- **Caustic 焦散**：PRIMARY/`AI_PAYMENT` 用 `couiColorContainerTheme` 派生色 +
  `coui_caustic_layout_*_level3` 边距
- **Material blur**：`COUIMaterialThemeBlurEffect.ThemeBlurEffectType.TYPE_CONTENT_PRIMARY_BUTTON` 等
- **按压缩放权重**：`scaleEnable` + `f6829c`，胶囊（radius=h/2）时 1.0，圆角矩形时 3.0
- **震动**：`needVibrate = true` 默认开

这些属于增强层，第一轮不实现（与 Switch 的 `useMaterialEffect` 处理一致）。

## 6. rmuix 现状对比

| 项 | rmuix | ColorOS（large 档） | 差 |
| :--- | ---: | ---: | ---: |
| 最小高度 | **40dp** | **44dp** | +4 |
| 最小宽度 | 58dp | **132dp** | +74（大得多） |
| 圆角 | **16dp** | **21.5dp**（≈胶囊） | +5.5 |
| 水平内边距 | 16dp | **12dp** | −4 |
| 垂直内边距 | 13dp | **8dp** | −5 |
| 文字字号 | 17sp（继承） | **16sp** | −1 |
| PRIMARY 色 | `primary` | `couiColorContainerTheme`（同义） | ✓ |
| SECONDARY 色 | `secondaryVariant` | **8% 黑 / 10% 白** | **不同** |

### 关键差异

1. **最小宽度**：rmuix 58dp，ColorOS large 档 132dp——但 small 档只有 52dp。
   rmuix 的 58dp 更接近 ColorOS 的 small 档。**建议 rmuix 保持 58dp（介于两档之间），
   只同步高度与圆角**。
2. **SECONDARY 底色**：rmuix 用 `secondaryVariant`（实心），ColorOS 用**半透明中性色叠在抬升背景上**。
   这是视觉差异较大的一点。
3. **圆角**：16 → 21.5（或胶囊）。

## 7. 实施建议

### 数值对齐（保守，推荐）

```kotlin
object ButtonDefaults {
    val MinWidth = 58.dp            // 保留（介于 ColorOS large/small 之间）
    val MinHeight = 44.dp           // 40 → 44
    val CornerRadius = 21.5.dp      // 16 → 21.5（44dp 高时即胶囊）
    val InsideMargin = PaddingValues(horizontal = 12.dp, vertical = 8.dp)   // 16/13 → 12/8
}
```

文字字号：`Button.kt` 主体里用 `RmuixTheme.textStyles` 的哪个 token 需确认——
若当前是 17sp（headline1），改为 16sp（headline2）。

### SECONDARY 底色（可选，改动较大）

`buttonColors()` 的默认 `color` 从 `secondaryVariant` 改为半透明中性：

```kotlin
color: Color = if (isSystemInDarkTheme()) Color(0x1AFFFFFF) else Color(0x14000000),
```

**风险**：`secondaryVariant` 的旧语义是“实心次级色”，很多页面可能依赖它在深浅主题下的对比度。
建议先只改 PRIMARY 系（本来就是 `primary`），SECONDARY 底色单独一轮确认。

### 文字色与字号

COUIButton 没有显式 `setTextColor`——文字色继承自主题。rmuix 的 `contentColor`（`LocalContentColor`）
机制等价，保留。

**字号同理**：rmuix 的 `Button` 不渲染文字，文字由调用方通过 `content` 传入，字号不受本组件控制。
ColorOS 的 `coui_btn_text_size = 16sp` 属于调用方约定，不是组件参数。对比表里的“文字字号”一行仅
在 example demo 的调用处生效。

## 8. 待确认

- [ ] **SECONDARY 底色是否本轮改**：半透明中性 vs 实心 `secondaryVariant`
- [ ] **文字字号 token**：当前 Button 用的哪个 style（headline1 17sp → headline2 16sp？）
- [ ] **small 档是否需要**（28dp 高/52dp 宽/14sp）——rmuix 的 `minHeight` 可参数化覆盖，
      可暂不加枚举
- [ ] **SpotLight / Caustic / MaterialBlur**：确认跳过（与 Switch 的 material effect 同级处理）

## 9. 影响面

```
Button 调用点：example/demo 若干 + preference 无直接使用
TextButton：独立类，本轮不动
```

Button 比 BasicComponent/Checkbox/RadioButton 影响面小（preference 体系不用它），
但它是 example 里展示最多的控件，视觉验收成本高。
