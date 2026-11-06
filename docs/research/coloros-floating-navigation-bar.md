# ColorOS 17 悬浮导航栏 (COUIBottomFloatingAccessoryNavigationBar) 1:1 复刻设计与对照规范

> 本文档用于对齐 ColorOS 17 官方相册 (`com.coloros.gallery3d`) 与 COUI 官方组件 (`com.coui.appcompat.bottomfloatingaccessorynavigationbar.*`) 的完整交互、度量、材质与动效逻辑。

---

## 一、 核心交互模型与无极滑动 (Continuous Touch Selection)

### 1. 触控无极滑动 (Continuous Drag & Slide Selection)
- **按住拖拽阶段 (`ACTION_MOVE`)**：
  - 手指按住底栏并在 X 轴滑动时，悬浮胶囊内部的高亮滑块（Active Indicator Pill）**实时无缝跟随手指落点 X 坐标进行平滑移动**。
  - 手指下方的 Tab Label 和 Icon 实时更新高亮/字重状态。
  - **关键约束**：此阶段**不触发主页面（Pager/Content）的实质载入或切换**，仅更新底栏内部触控预选状态，避免频繁重构页面。
- **抬手释放确认阶段 (`ACTION_UP`)**：
  - 手指离开屏幕的瞬间，锁定手指最终落点所在的 Tab 索引。
  - 触发 `onTabSelected` / `onItemClick`，主页面动画切入该最终页面。
  - 高亮滑块触发 `folmeSpring` 物理弹簧（Response 0.45f, Damping 0.78f）弹性精确定位收尾。

---

## 二、 4 种三岛变体模式 (Three-Island Variants)

| 模式名称 | 结构组合 | 包含组件 | 典型应用场景 |
| :--- | :--- | :--- | :--- |
| **`ColorOS--Single`** | `[ 中间主胶囊 ]` | 仅 `CenterMenuView` | 纯导航栏（首页 / 社区 / 设置） |
| **`ColorOS--Left`** | `[ 左附属圆钮 ] [ 中间主胶囊 ]` | `StartAccessory` + `CenterMenuView` | 带有左侧快捷主操作（如 `[全选]`） |
| **`ColorOS--Right`** | `[ 中间主胶囊 ] [ 右附属圆钮 ]` | `CenterMenuView` + `EndAccessory` | 带有右侧筛选/更多（如 `[筛选]`） |
| **`ColorOS--Both`** | `[ 左附属圆钮 ] [ 中间主胶囊 ] [ 右附属圆钮 ]` | 完整 3 岛组合 | 官方相册主页 (`[精选] [照片|图集|创作] [筛选]`) |

---

## 三、 官方度量与尺寸规范 (Stock Geometry Metrics)

取自 500dpi (Density 3.125) 真实设备测量与 COUI 官方 `dimens.xml`：

| 几hado/度量项 | 官方 Raw px | 换算 dp 规范 | 代码 Token 命名 |
| :--- | ---: | ---: | :--- |
| **中间主胶囊高度** | 170 px | **56dp** | `FloatingNavigationBarDefaults.BarHeight` |
| **内部单项高度** | 150 px | **48dp** | `FloatingNavigationBarDefaults.ItemHeight` |
| **内部单项最大宽度** | 179 px | **70dp** | `FloatingNavigationBarDefaults.ItemMaxWidth` |
| **内部项间距** | 28 px | **8dp** | `FloatingNavigationBarDefaults.ItemSpacing` |
| **内部两端 padding** | 50 px | **16dp** | `FloatingNavigationBarDefaults.HorizontalPadding` |
| **附属圆钮尺寸** | 198 × 198 px | **56dp × 56dp** | `FloatingAccessoryButtonDefaults.Size` |
| **主胶囊与附属圆钮间距** | 28 px | **8dp** | `FloatingNavigationBarDefaults.SideSpacing` |
| **屏两侧外边距** | 57 px | **16dp / 36dp** | `FloatingNavigationBarDefaults.HorizontalOutSidePadding` |
| **离屏底悬浮间距** | 25 px | **8dp + Inset** | `FloatingNavigationBarDefaults.NaviDistance` |

---

## 四、 材质与配色规范 (Material & Colors)

### 1. 主胶囊与附属圆钮背景 (Surface)
- **浅色模式 (Light Mode)**：`#F2F3F5` / `surfaceContainer` 实色或 85% 悬浮毛玻璃，带 1dp 超轻投影。
- **深色模式 (Dark Mode)**：`#1C1C1E` 纯黑/深灰色，100% OLED 纯黑适配。

### 2. 滑动高光指示块 (Active Indicator Pill)
- **浅色模式 (Light Mode)**：纯白 **`#FFFFFF`**，配合 `3.dp` 柔和 DropShadow (`#14000000`)。
- **深色模式 (Dark Mode)**：**`#2C2C2E`** 或 15% 纯白透明度。

### 3. 图标与文字前景 (Foreground Tint)
- **选中项 (Selected)**：高对比 **`onSurface`** (浅色 `#000000` / 深色 `#FFFFFF`)，字重 `Bold` / `Medium`。
- **未选中项 (Unselected)**：40% 中性透明度 (`onSurfaceContainer.copy(alpha = 0.4f)`).

---

## 五、 动效与手势折叠规划 (Motion & Gestures)

1. **触控拖拽实时跟手 (Real-time Drag Following)**：
   - 监听指针手势 X 轴位移，指示块零延迟随手指滑动。
2. **抬手确认与 Spring 弹簧收尾**：
   - 抬手瞬间触发 `folmeSpring(damping = 0.78f, response = 0.45f)` 精确收尾，并唤醒页面切换。
3. **列表滚动时的折叠与展开 (Scroll Hide / Show Dynamics)**：
   - 上滑列表：左右附属圆钮隐藏/收缩 (`ADDED_HIDDEN`)。
   - 下滑/停止：左右附属圆钮平滑展开 (`ADDED_VISIBLE`)。
4. **按压缩放弹簧 (Touch Spring Scale)**：
   - 按压时 Item 及圆钮收缩至 `0.96x`，抬手弹性恢复。

---

## 六、 触控光效通用层 (Touch Light Effect Shader)

- 针对 ColorOS 17 `COUISpotLightEffect` / `mTouchEffectShader`（跟手 RadialGradient / AGSL 立体光斑）：
- 已在 `rmuix-shader` 模块中实现通用型 Modifier：`Modifier.rmuixSpotlight(glowColor, lightRadius)`！
  - **API 33+ (Android 13+ / Skiko Desktop)**：直接挂载 AGSL `RuntimeShader` (`SPOTLIGHT_AGSL`)，计算 2.5 次方三次方衰减曲线 (`pow(1.0 - norm, 2.5)`)。
  - **旧设备/降级环境**：自动平滑降级为 Compose `Brush.radialGradient` Canvas 渲染。
  - **渲染顺序**：在 content 绘制之前先绘制光斑 (`onDrawWithContent { drawRect(activeBrush); drawContent() }`)，避免污染前景图标与文字。
