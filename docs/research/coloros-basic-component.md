# ColorOS BasicComponent 逆向调研

`BasicComponent` 是 rmuix 所有 preference 的基底（21 个文件依赖），对应 ColorOS 的
`coui_preference.xml` + `COUICustomListSelectedLinearLayout` + `COUICustomLinearLayoutForPreference`。

**证据优先级**（按用户要求：代码本体为主，dump 为辅）：

1. **源码/资源**（主）：`OplusCamera-7.006.120.apk` 解包的 `com.coui.appcompat.preference.*`
   与 `res/values/dimens.xml`
2. **真机 dump**（辅）：WLAN 设置页，验证结论

---

## 1. 结构

### `coui_preference.xml`

```xml
<COUICustomListSelectedLinearLayout
    android:gravity="center_vertical"
    android:minHeight="@dimen/support_preference_min_height"     <!-- 48dp -->
    android:paddingStart="@dimen/support_preference_title_padding_start"  <!-- 32dp -->
    android:paddingEnd="@dimen/support_preference_title_padding_end">     <!-- 32dp -->

    <FrameLayout android:id="@id/img_layout" style="@style/PreferenceIcon">   <!-- 图标区 -->
        <COUIRoundImageView android:id="@android:id/icon" coui:couiType="round" />
        <COUIHintRedDot android:id="@id/img_red_dot" />                       <!-- 图标红点 -->
    </FrameLayout>

    <RelativeLayout android:layout_weight="1.0" style="@style/PreferenceContent">
        <TextView android:id="@android:id/title" />
        <TextView android:id="@android:id/summary" />
        <TextView android:id="@id/assignment" />                              <!-- 第三行 -->
    </RelativeLayout>

    <COUIHintRedDot android:id="@id/jump_icon_red_dot" />                     <!-- 尾部红点 -->
    <LinearLayout android:id="@android:id/widget_frame" />                    <!-- 控件槽 -->
</COUICustomListSelectedLinearLayout>
```

### 与 rmuix 的对应

| ColorOS | rmuix | 差异 |
| :--- | :--- | :--- |
| `img_layout` | `startAction` | — |
| title / summary / assignment | `content` slot | **rmuix 只有两层** |
| `jump_icon_red_dot` | — | rmuix 由调用方塞 |
| `img_red_dot` | — | rmuix 由调用方塞 |
| `widget_frame` | `endActions` | — |
| — | `bottomAction` | **rmuix 独有** |

---

## 2. 图标垂直边距：**按尺寸分档**（最重要的发现）

`COUICustomListSelectedLinearLayout.onMeasure()` 不是用固定 padding，而是
**根据图标尺寸和文字行数动态选择 dimens**：

```java
int iL = appCompatImageView.getDrawable().getIntrinsicHeight();  // 图标实际高度
int i13 = lineCount + lineCount2 + lineCount3;                   // 总文字行数

if (i13 > 2) {
    // 3 行以上：图标顶部对齐，topMargin 由行位置计算
    layoutParams.gravity = 48;
    ...
    iMax = max(计算值, coui_preference_50icon_margin_vertical_default);
    layoutParams.topMargin = iMax;
} else {
    // 1~2 行：垂直居中，按图标尺寸分档
    layoutParams.gravity = 16;
    int margin = coui_preference_icon_margin_top;      // 默认 14dp
    if (iL == 24) margin = (i13 <= 1) ? 24icon_margin_vertical_default
                                      : 24icon_margin_top_multiline;
    else if (iL == 32) margin = (i13 <= 1) ? 32icon_...
    else if (iL == 36) margin = (i13 <= 1) ? 36icon_...
    else if (iL == 50) margin = 50icon_margin_vertical_default;
    layoutParams.topMargin = margin;
    layoutParams.bottomMargin = margin;
}
```

### 实测分档表

| 图标尺寸 | 单行 | 多行（2 行） |
| ---: | ---: | ---: |
| 24dp | **12.0dp** | **20.0dp** |
| 32dp | **10.0dp** | **16.0dp** |
| 36dp | **12.0dp** | **14.0dp** |
| 50dp | **8.0dp** | 8.0dp（不分档） |
| 其他尺寸 | 14.0dp（`coui_preference_icon_margin_top`） | 同上 |

**图标越大，边距越小** —— 因为行高固定，大图标需要更少边距才能垂直居中。

---

## 3. 文字区

| 资源 | 值 |
| :--- | ---: |
| `support_preference_text_content_padding_top` | **10.0dp** |
| `support_preference_text_content_padding_bottom` | **10.0dp** |
| `coui_preference_text_margin_left` | 0.0dp（图标间距由 `icon_margin_right` 提供） |
| `coui_preference_icon_margin_right` | **16.0dp** |

---

## 4. 尾部 margin 动态调整

`COUICustomLinearLayoutForPreference.onMeasure()` 根据**子视图可见性**调整标题区右边距：

```java
if (widget_frame 可见) {
    if (jump_icon 不可见)      title_marginEnd = f7891g;  // 0dp
    else                       title_marginEnd = f7892h;  // 4dp
}
if (messageLayout 可见 && 其他都不可见) {
    title_marginEnd = f7895k;   // 自定义，默认 0
} else {
    title_marginEnd = f7893i;   // 16dp
}
```

| 资源 | 值 | 场景 |
| :--- | ---: | :--- |
| `support_preference_reddot_margin_end_in_right_noassignment` | **0.0dp** | 有控件、无箭头、无 assignment |
| `support_preference_reddot_margin_end_in_right_hasassignment` | **4.0dp** | 有控件、有箭头 |
| `support_preference_title_margin_end_in_right` | **16.0dp** | 默认 |

---

## 5. 文字样式与颜色

| 元素 | 样式 | 字号 | 字重 | 颜色（亮/暗） |
| :--- | :--- | ---: | :--- | :--- |
| title | `couiTextAppearanceHeadline6` | **16.0sp** | `sans-serif-medium` | `couiColorPrimaryText` `#FF000000` / `#FFFFFFFF` |
| summary | `couiTextAppearanceArticleBody` | **13.0sp** | 默认 | `couiColorSecondary` `#8A000000` / `#8AFFFFFF` |
| assignment | `TextAppearance.COUI.List.Assignment` | **13.0sp**（继承 summary） | 默认 | `couiColorPrimaryText` `#FF000000` / `#FFFFFFFF` |

### assignment 颜色解析链（来自 Settings.apk 资源表）

`res/color/coui_preference_assignment_text_color.xml` 是个 selector：

```xml
<selector>
    <item android:state_enabled="false" android:color="?couiColorLabelTertiary" />
    <item                              android:color="?couiColorPrimaryText" />
</selector>
```

attr 再指向具体色值：

| attr | color 资源 | 亮色 | 暗色 |
| :--- | :--- | :--- | :--- |
| `couiColorPrimaryText` | `coui_primary_text_color` | `#FF000000` | `#FFFFFFFF` |
| `couiColorLabelTertiary` | `coui_color_hint_neutral` | `#42000000` | `#4DFFFFFF` |

**注意**：assignment 用的是 **主文字色**，不是次要色 —— 它和 title 同色，只是字号更小。
rmuix 对应 `RmuixTheme.colorScheme.onBackground`。

### 三档中性色对照（Settings.apk 实测）

| coui 资源 | 亮色 | 暗色 | rmuix 对应 |
| :--- | :--- | :--- | :--- |
| `coui_color_primary_text_color` | `#FF000000` | `#FFFFFFFF` | `onBackground` |
| `coui_color_secondary_neutral` | `#8A000000` | `#8AFFFFFF` | `secondaryText` |
| `coui_color_hint_neutral` | `#42000000` | `#4DFFFFFF` | `tertiaryText` |

三档与 rmuix 现有 `RmuiColors` 完全一致，无需调整。

---

## 6. 真机验证（WLAN 页面，辅助证据）

WLAN 页同时有图标行和无图标行，可验证 `paddingStart` 语义：

| 行类型 | 元素 | px | dp |
| :--- | :--- | ---: | ---: |
| 无图标 | title | 100 | **32.0** |
| 有图标 | signal | 100 | 32.0 |
| | signal size | 63 | 20.2 |
| | title | 213 | **68.2** |
| 两者 | 尾部箭头/控件距右 | 100 | **32.0** |

验证：`32 + 20.2 + 16 = 68.2` ✓

**结论**：`paddingStart = 32dp` 是**容器内边距**，图标画在其中，
`icon_margin_right = 16dp` 提供图标到文字的间距。左右对称都是 32dp。

### 行高验证

| 元素 | px | dp |
| :--- | ---: | ---: |
| 普通行间距 | 149 | **47.7** |
| toolbar | 163 | 52.2 |
| 返回按钮 | 125 | 40.0 |

与 `support_preference_min_height = 48dp` 吻合。

### Switch 尺寸（独立验证）

WLAN `switch_widget` = 138×75px = **44.2×24.0dp**，与 `COUISwitch` 源码的 44×24dp 一致。

---

## 7. 与 rmuix 现状对比

| 项 | rmuix | ColorOS | 差 |
| :--- | ---: | ---: | ---: |
| 最小高度 | 56dp | **48dp** | **−8** |
| 左右内边距 | 16dp | **32dp** | **+16** |
| 文字上下内边距 | 16dp | **10dp** | **−6** |
| 图标→文字 | 8dp | **16dp** | **+8** |
| 图标垂直边距 | 无（居中） | **按尺寸分档 8–20dp** | 新增 |
| 标题字号 | 17sp | **16sp** | −1 |
| 副标题字号 | 14sp | **13sp** | −1 |
| 尾部动态 margin | 无 | **0 / 4 / 16dp 三档** | 新增 |

---

## 8. 实施建议

### 只改数值（最小改动）

```kotlin
// BasicComponentDefaults
val InsideMargin = PaddingValues(horizontal = 32.dp, vertical = 10.dp)
val MinHeight = 48.dp

// Component.kt
.heightIn(min = 48.dp)          // 原 56.dp
val spacerPx = 16.dp.roundToPx()  // 原 8.dp

// 文字
title   → RmuixTheme.textStyles.headline2  (16sp)
summary → RmuixTheme.textStyles.footnote1  (13sp)
```

### 若要完整还原（更大改动）

需要在 `Layout` 里实现：

1. 图标垂直边距按 `IntrinsicHeight` 分档（24/32/36/50 → 12/10/12/8dp）
2. 文字行数 > 2 时切换为顶部对齐
3. 尾部 margin 按子视图可见性在 0/4/16dp 间切换

---

## 9. 必须保留

AGENTS.md 硬约束：

> **Do NOT Replace Custom Layout in Component.kt**
> 不要改成 `Row + weight(1f)` —— start/end 溢出时文字会变竖直单字。

当前 `Layout`（`Component.kt` 174–251 行）是正确实现，改数值时必须保留结构。

---

## 10. 红点尺寸体系（来自 `COUIHintRedDot`）

| 档位 | 红点直径 | 文字字号 | 圆角图标 margin | 方形图标 margin |
| :--- | ---: | ---: | ---: | ---: |
| small | **6.0dp** | 9.0dp | 1.0dp | 1.0dp |
| medium | **8.0dp** | — | 2.0dp | 2.0dp |
| large | **12.0dp** | 10.0dp | 2.0dp | 4.0dp |

其他常量：

| 资源 | 值 |
| :--- | ---: |
| `coui_hint_red_dot_rect_radius` | 2.67dp（矩形红点圆角） |
| `coui_hint_red_dot_ellipsis_spacing` | 2.0dp |
| `coui_hint_red_dot_navi_small_width` | 12.0dp |
| `support_preference_icon_red_dot_margin_top` | 2.0dp |
| `coui_hint_red_dot_mode_stroke_extra_diameter` | 2.0dp |

图标红点尺寸随图标大小选择：图标 ≤ small → small 红点，以此类推。

---

## 11. 已确认的实现决策

用户已拍板：

| 项 | 决策 |
| :--- | :--- |
| 图标边距分档 | **完整实现**（4 档 + 多行切换） |
| `assignment` 第三行 | **要加**（`assignment: String? = null`） |
| 两个红点 | **内置**（`iconBadge` / `endBadge`） |
| Switch 阴影 | **不改**（保持 9.8%） |

---

## 12. 待确认

- [x] ~~`assignment` 颜色~~ → `couiColorPrimaryText` `#FF000000`/`#FFFFFFFF`
- [x] ~~红点尺寸~~ → small 6dp / medium 8dp / large 12dp
- [ ] **尾部 margin 动态切换**：是否实现（0/4/16dp 三档）？
- [ ] **红点档位选择规则**：按图标尺寸自动选，还是调用方指定？

---

## 13. 影响面

```
BasicComponent 直接调用：21 个文件
├── rmuix-preference/  9 个 preference 组件
├── example/           5 个 demo
└── docs/demo/         4 个交互 demo
```

`InsideMargin` 16 → 32dp 会让**所有行左右各窄 16dp**，视觉变化明显，需整体验收。

新增参数（全部可选，默认不启用）：

```kotlin
assignment: String? = null,        // null = 不显示第三行
iconBadge: Boolean = false,        // false = 不画图标红点
endBadge: Boolean = false,         // false = 不画尾部红点
iconBadgeCount: Int = 0,           // 0 = 圆点，>0 = 数字
```

默认值下渲染结构与 miuix 原版一致（符合 STAGING.md 兼容规范）。
