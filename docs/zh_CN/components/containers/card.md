# Card

## 共享原厂聚光内核

聚光入口现统一复用[共享 Spotlight](../media/spotlight)：使用官方光照 shader、预设和弹簧，移除旧幂次光斑/径向回退，并在内容之后混合。此项是公共引擎替换，不表示本控件的系统材质门控和预设选择已全部验收。Button/Checkbox 的可选聚光仍默认关闭。

BREAKING：聚光默认 tint 改为白色，移除组件内部随意指定的 40/140/380dp 半径；自定义颜色与半径仍可通过公共入口显式覆盖。

**BREAKING（stock 底色，2026-11-17）：** 官方卡片 drawable（`card_preference_normal_background.xml`、
`about_device_*_card_*` 系列、`coui_component_card_*`）统一填充 `?attr/couiColorCardBackground` =
`@color/coui_color_card_background`（浅色 **#FFFFFF** / 深色 **#1AFFFFFF**），卡片内文字取
`?attr/couiColorLabelPrimary`。`CardDefaults.defaultColors()` 已改用这一对，不再用主题的
`surfaceContainer` / `onSurfaceContainer`。


`Card` 是 Rmuix 中的基础容器组件，用于承载相关内容和操作。它提供了具有 Rmuix 风格的卡片容器，适用于信息展示、内容分组等场景。支持静态显示和交互式两种模式。

<div style="position: relative; height: 300px; border-radius: 10px; overflow: hidden; border: 1px solid #777;">
    <iframe id="demoIframe" style="position: absolute; top: 0; left: 0; width: 100%; height: 100%; border: none;" src="../../../compose/index.html?id=card" title="Demo" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" referrerpolicy="strict-origin-when-cross-origin"></iframe>
</div>

## 引入

```kotlin
import io.github.ra1nify.rmuix.kmp.basic.Card
import io.github.ra1nify.rmuix.kmp.utils.PressFeedbackType // 如果使用交互式卡片
```

## 基本用法

Card 组件可以用于包装和组织内容（静态卡片）：

```kotlin
Card {
    Text("这是卡片内容")
}
```

## 属性

### Card 属性


| 属性名            | 类型                               | 说明                     | 默认值                       | 是否必须 | 适用范围 |
| ----------------- | ---------------------------------- | ------------------------ | ---------------------------- | -------- | -------- |
| modifier          | Modifier                           | 应用于卡片的修饰符       | Modifier                     | 否       | 所有     |
| cornerRadius      | Dp                                 | 卡片圆角半径             | CardDefaults.CornerRadius    | 否       | 所有     |
| cornerTier        | CardCornerTier?           | 官方八档圆角；设置后覆盖 `cornerRadius`（见下表） | null |
| cornerScale       | CardCornerScale           | 档位取哪一族半径：`LEGACY` / `NORMAL_16_1` / `LARGE_16_1` | `LARGE_16_1` |
| cornerExtension   | Float                     | 平滑圆角权重，传给 `squircleSurface` | `CardDefaults.CornerExtension`（1.1） |
| insideMargin      | PaddingValues                      | 卡片内部边距             | CardDefaults.InsideMargin    | 否       | 所有     |
| colors            | CardColors                         | 卡片颜色配置             | CardDefaults.defaultColors() | 否       | 所有     |
| pressFeedbackType | PressFeedbackType                  | 按压反馈类型             | PressFeedbackType.None       | 否       | 交互式   |
| showIndication    | Boolean                            | 显示点击指示效果         | false                        | 否       | 交互式   |
| holdDownState     | Boolean                            | 卡片是否处于按下状态     | false                        | 否       | 交互式   |
| onClick           | (() -> Unit)?                      | 点击事件回调             | null                         | 否       | 交互式   |
| onLongPress       | (() -> Unit)?                      | 长按事件回调             | null                         | 否       | 交互式   |
| content           | @Composable ColumnScope.() -> Unit | 卡片内容区域的可组合函数 | -                            | 是       | 所有     |

::: warning 注意
部分属性仅在创建可交互的卡片时可用！
:::

### CardDefaults 对象

CardDefaults 对象提供了卡片组件的默认值和颜色配置。

#### 常量

| 常量名       | 类型          | 说明           | 默认值              |
| ------------ | ------------- | -------------- | ------------------- |
| CornerRadius | Dp            | 卡片圆角半径（`?attr/couiRoundCornerXL` = `coui_round_corner_xl`） | 20.dp |
| InsideMargin | PaddingValues | 卡片的内部边距 | PaddingValues(0.dp) |

#### 方法

| 方法名          | 类型       | 说明           |
| --------------- | ---------- | -------------- |
| defaultColors() | CardColors | 卡片的默认颜色 |

### CardColors 类
| 属性名       | 类型  | 说明           |
| ------------ | ----- | -------------- |
| color        | Color | 卡片的背景颜色 |
| contentColor | Color | 卡片的内容颜色 |

## 进阶用法

### 自定义样式卡片

```kotlin
Card(
    cornerRadius = 8.dp,
    insideMargin = PaddingValues(16.dp),
    colors = CardDefaults.defaultColors(
        color = RmuixTheme.colorScheme.primaryVariant
    ),
) {
    Text("自定义样式卡片")
}
```

### 内容丰富的卡片

```kotlin
Card(
    modifier = Modifier.padding(16.dp),
    insideMargin = PaddingValues(16.dp)
) {
    Text(
        text = "卡片标题",
        style = RmuixTheme.textStyles.title2
    )
    Spacer(modifier = Modifier.height(8.dp))
    Text(
        text = "这是卡片的详细内容描述，可以包含多行文本信息。"
    )
    Spacer(modifier = Modifier.height(16.dp))
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.End
    ) {
        TextButton(
            text = "取消",
            onClick = { /* 处理取消事件 */ }
        )
        Spacer(modifier = Modifier.width(8.dp))
        TextButton(
            text = "确定",
            colors = ButtonDefaults.textButtonColorsPrimary(), // 使用主题颜色
            onClick = { /* 处理确认事件 */ }
        )
    }
}
```

### 列表中的卡片

```kotlin
LazyColumn {
    items(5) { index ->
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            insideMargin = PaddingValues(16.dp)
        ) {
            Text("列表项 ${index + 1}")
        }
    }
}
```

### 可交互的卡片

```kotlin
Card(
    modifier = Modifier.padding(16.dp),
    pressFeedbackType = PressFeedbackType.Sink, // 设置按压反馈为下沉动画效果
    showIndication = true, // 显示点击时的视觉反馈效果
    onClick = { /* 处理点击事件 */ },
    onLongPress = { /* 处理长按事件 */ }
) {
    Text("可交互的卡片")
}
```


`PressFeedbackType.Sink` 复现 `COUIPressFeedbackJumpPreference` 的 `COUIPressFeedbackHelper(view, 0)`：按实测卡片
尺寸经 `PressCalc.calculateUniformScaleBreakdown()` 映射到 **0.85..0.97**（面积超过 150dp 映射上限后固定在 0.97，
常见列表卡片基本都超过），弹簧为 `PressParamsExtKt` 的 jelly 默认值（按下 response 0.25 / bounce 0，回弹
response 0.5 / bounce 0.6）。`Tilt` 仍是库内自定义倾斜效果。
### 官方八档圆角（`CardCornerTier`）

| 档位 | 显示半径 | legacy 绘制 | 16.1 normal | 16.1 large | 权重 |
| :--- | ---: | ---: | ---: | ---: | ---: |
| XS | 4dp | 4dp | 5dp | 5dp | 1.5 |
| S | 8dp | 8dp | 10dp | 11dp | 1.5 |
| M | 12dp | 9dp | 15dp | 17dp | 0.99 |
| L | 16dp | 13dp | 19dp | 23dp | 0.99 |
| XL | 20dp | 16dp | 24dp | 28dp | 1.1 |
| XXL | 24dp | 19dp | 29dp | 34dp | 1.1 |
| XXXL | 28dp | 22dp | 34dp | 39dp | 1.1 |
| XXXXL | 32dp | 25dp | 38dp | 45dp | 1.1 |

> `RoundCornerUtil.getRoundCornerForOS17()` 只读 `*_radius_large_16_1`（按 `couiRoundCornerXXL` 这类属性查表），`*_radius_normal_16_1` 只在设备关闭平滑样式时使用。
>
> **平滑权重：** dump 里有两套「weight」——每档的 `coui_round_corner_<tier>_weight`（1.5 / 0.99 / 1.1，本库以 `CardCornerTier.weight` 暴露），以及 `RoundCornerUtil` 里的**全局**平滑权重（`DEFAULT_WIGHT = 170` / `DEFAULT_WIGHT_DIVISOR = 100`，即默认 **1.7**；`WEIGHT_16_1_NORMAL` = **2.5**、`WEIGHT_16_1_LARGE` = **3.0**、`NON_WEIGHT` = 2.0）。两者到 `SquircleShape.extension` 的映射仍未证实，因此 `cornerExtension` 保持实测默认值，而不是从这些数推导。
## 平滑扩展因子与一处圆角 bug

`Card` 把圆角传给 `squircleSurface`，它的角块大小是一个 `extension` 因子（1.0 = 圆弧，1.1 = 连续角）。`CardDefaults.CornerExtension` 暴露它。官方各档带一个"平滑圆角权重"，16_1 系列为 **3**，但那个权重属于官方 shader 的参数化，与 `extension` **没有已知换算**，所以默认保持 `SquircleDefaults.Extension`（1.1），而不是猜一个映射。

> 同一轮修掉的 bug：`BasicCard` 之前把**原始 `cornerRadius` 参数**传给 `squircleSurface`，而不是 `resolvedCornerRadius`，于是设了 `cornerTier` 只会改变聚光形状，**不会改变实际绘制的卡片圆角**。

## 两层共用同一个轮廓

`Card` 之前给 `Modifier.rmuixSpotlight(shape = …)` 传 `RoundedCornerShape`，底下却画 squircle 背景 —— 聚光的轮廓是圆弧，卡片是连续曲率，两者不重合。`SquircleShape`（在 `rmuix-squircle`）补齐了这一点：它用与 `squircleSurface` / `squircleClip` 相同的 `Path.addSquircleRect` 构造轮廓，于是 `Card` 两层共用一个形状。

```kotlin
val shape = SquircleShape(resolvedCornerRadius, cornerExtension)
Box(Modifier.rmuixSpotlight(shape = shape).squircleSurface(colors.color, resolvedCornerRadius, cornerExtension))
```

`SquircleShape` 返回 `Outline.Generic` 路径（与分辨率无关，但不是解析式的 `Outline.Rounded`）；传 `squircleEnabled = false` 时退化为普通圆角矩形。

## 退化契约

`squircleSurface` 在 shader 不可用时会退化为 `.clip(RoundedCornerShape(…))`，所以聚光轮廓必须**跟着一起退化**。`Card` 因此把 `isSquircleEnabled()` 传进 `SquircleShape(cornerRadius, extension, squircleEnabled)`；关掉 composition-local 后，两层都退化为同一个圆角矩形。

`Modifier.rmuixSpotlight` 通过 `clipOutline` 处理 `SquircleShape` 返回的 `Outline.Generic`，把它映射为 `clipPath(outline.path)` —— 所以聚光确实被裁到 squircle 轮廓，而不只是半径相同。