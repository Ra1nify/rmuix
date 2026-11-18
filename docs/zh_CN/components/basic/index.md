# 基础组件

`rmuix-ui` 提供应用布局、导航、基础控件与 Overlay/Window 浮层。

## 组件

| 组件 | 描述 | 常见用途 |
| --- | --- | --- |
| [Spotlight](../media/spotlight) | 共享原厂光照内核与预设 | 触摸聚光、材质预览 |
| [Scaffold](../containers/scaffold) | 应用的基础布局 | 页面结构、内容展示 |
| [Surface](../containers/surface)                           | 基础容器组件           | 内容展示、背景容器   |
| [TopAppBar](../navigation/topappbar)                       | 应用顶部的导航栏       | 页面标题、主要操作   |
| [NavigationBar](../navigation/navigationbar)               | 底部导航组件           | 主要页面切换         |
| [SideNavigationDrawer](../navigation/sidenavigationdrawer) | 带遮罩与分隔线的侧滑抽屉 | 相册/分类导航                          |
| [NavigationRail](../navigation/navigationrail)             | 侧边导航组件           | 主要页面切换（大屏）  |
| [TabRow](../navigation/tabrow)                             | 水平标签页切换栏       | 内容分类浏览         |
| [ChipTabRow](../navigation/chiptabrow)                    | Chip 风格标签栏                                  | 筛选类切换                                |
| [Card](../containers/card)                                 | 包含相关信息的容器     | 信息展示、内容分组   |
| [EmptyState](../feedback/emptystate)                     | 空状态页：插图、标题、副标题、操作文字 | 空列表、无结果页                      |
| [CompleteState](../feedback/completestate)               | 完成页，带 100dp 插图                 | 流程结束、备份完成                    |
| [BasicComponent](../containers/basiccomponent)             | 通用基础组件           | 自定义组件开发       |
| [BasicComponentGroup](../containers/basiccomponentgroup)  | 只在组内行之间画分隔线的卡片组                             | 设置类列表                                |
| [Button](../buttons/button)                             | 触发操作的交互元素     | 表单提交、操作确认   |
| [LoadingButton](../buttons/loadingbutton)              | 用一个三点加载替换文字的按钮                              | 提交中状态                                |
| [SegmentButton](../buttons/segmentbutton)              | 胶囊轨道 + 等宽分段                                 | 紧凑的模式切换                              |
| [IconButton](../buttons/iconbutton)                     | 图标按钮组件           | 辅助操作、工具栏     |
| [Text](../text-and-icons/text)                                 | 展示各种样式的文字内容 | 标题、正文、描述文本 |
| [CategoryTitle](../text-and-icons/categorytitle)                     | 小型标题组件           | 辅助标题、分类标识   |
| [TextField](../input/textfield)                       | 接收用户文本输入       | 表单填写、搜索框     |
| [CodeInput](../input/codeinput)                       | 分格验证码输入，透明输入框叠在格子上 | 验证码、口令                        |
| [SimpleLock](../input/simplelock)                     | PIN 圆点：4/6 位，按输入填充 | PIN 输入、安全流程                  |
| [NumericKeyboard](../input/numerickeyboard)           | 3 × 4 PIN 数字键盘，含删除/完成侧键 | PIN/密码输入                       |
| [LockPatternView](../input/lockpatternview)           | 3 × 3 图案锁，含成功/错误态 | 图案解锁、隐私                     |
| [SingleInputCard](../input/singleinputcard)           | 卡片式单行输入，含计数/清除/错误 | 邮箱绑定、单行输入                 |
| [CalendarPicker](../input/calendarpicker)             | 月历选择器，含今天/选中态 | 日期选择、拍摄时间                 |
| [AlphabetIndexBar](../feedback/alphabetindexbar)         | A–Z 快速滚动条，含按压字母气泡 | 长列表、应用列表                   |
| [RoundImage](../media/roundimage)                     | 圆形/圆角/带阴影的图片容器 | 头像、设置图标                     |
| [Switch](../selection/switch)                             | 双态切换控件           | 设置项开关、功能启用 |
| [Checkbox](../selection/checkbox)                         | 多选控件               | 多项选择、条款同意   |
| [RadioButton](../selection/radiobutton)                   | 单选控件               | 单项选择、选项切换   |
| [Slider](../selection/slider)                             | 调节值的滑动控件       | 音量调节、范围选择   |
| [NumberPicker](../input/numberpicker)                 | 垂直滚动数字选择器     | 时间选择、数量选择   |
| [DatePicker](../input/datepicker)                     | 滚轮日期选择器（年 / 月 / 日）              | 日期设置、日程                        |
| [TimePicker](../input/timepicker)                     | 滚轮时间选择器，12 / 24 小时制              | 闹钟、定时                            |
| [TimeLimitPicker](../input/timelimitpicker)           | 带 `小时` / `分钟` 单位文案的滚轮时间选择器 | 定时类设置页                          |
| [ProgressIndicator](../feedback/progressindicator)       | 展示操作进度状态       | 加载中、进度展示     |
| [PageIndicator](../feedback/pageindicator)              | 一排扁平页码圆点                                    | 轮播、分页位置                              |
| [Snackbar](../feedback/snackbar)                         | 底部临时消息条组件     | 状态提示、操作结果   |
| [Tooltip](../feedback/tooltip)                           | 悬停或长按显示的简短标签 | 图标按钮标签、元素提示 |
| [TopTips](../feedback/toptips)                          | 浮层提示卡：标题、正文、操作                              | 省电、网络、模式提示                           |
| [Chip](../selection/chip)                                 | 胶囊形筛选 / 选择标签    | 筛选、标签、选项       |
| [ChipGroup](../selection/chipgroup)                       | 可折叠的芯片组           | 标签列表、筛选行       |
| [Icon](../text-and-icons/icon)                                 | 图标展示组件           | 图标按钮、状态指示   |
| [FloatingActionButton](../buttons/floatingactionbutton) | 悬浮操作按钮           | 主要操作、快捷功能   |
| [FloatingToolbar](../buttons/floatingtoolbar)           | 悬浮工具栏             | 快捷操作、信息展示   |
| [Divider](../containers/divider)                           | 内容分隔线             | 区块分隔、层次划分   |
| [Modifier.pressable](../media/pressable)              | 按压反馈 Modifier（可附带点击） | 卡片、色块、自定义点击区域 |
| [Stepper](../input/stepper)                           | 带加减按钮的数值步进器 | 数量、档位、步长调整 |
| [RedDot](../feedback/reddot)                            | 红点提示，可带数字                                   | 未读、提示                                |
| [ScrollBar](../feedback/scrollbar)                       | 可拖拽滚动指示条       | 长列表、滚动位置反馈 |
| [SearchBar](../input/searchbar)                       | 执行搜索的输入框       | 内容搜索、快速查找   |
| [OverlayListPopup](../overlays/overlaylistpopup)             | 基于 BasicComponent 的列表弹窗组件（使用 RmuixPopupUtils，需在 `Scaffold` 中使用） | 选项选择、功能列表     |
| [OverlayCascadingListPopup](../overlays/overlaycascadinglistpopup) | 二级级联列表弹窗组件（使用 RmuixPopupUtils，需在 `Scaffold` 中使用）               | 子菜单、分组动作面板   |
| [OverlayBottomSheet](../overlays/overlaybottomsheet)         | 基于 BasicComponent 的底部抽屉组件（使用 RmuixPopupUtils，需在 `Scaffold` 中使用） | 底部抽屉、附加选项     |
| [OverlayDialog](../overlays/overlaydialog)                   | 基于 BasicComponent 的对话弹窗组件（使用 RmuixPopupUtils，需在 `Scaffold` 中使用） | 提示、确认操作         |
| [WindowListPopup](../overlays/windowlistpopup)     | 窗口级列表弹窗组件                                                                 | 选项选择、功能列表     |
| [WindowCascadingListPopup](../overlays/windowcascadinglistpopup) | 窗口级二级级联列表弹窗组件                                                         | 子菜单、分组动作面板   |
| [WindowBottomSheet](../overlays/windowbottomsheet) | 窗口级底部抽屉组件                                                                 | 底部抽屉、附加选项     |
| [WindowDialog](../overlays/windowdialog)           | 窗口级对话框组件                                                                   | 提示、确认操作         |

## 配置

在项目中添加 `rmuix-ui` 依赖：

```kotlin
kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation("io.github.ra1nify.rmuix.kmp:rmuix-ui:<version>")
        }
    }
}
```

Android 项目：

```kotlin
dependencies {
    implementation("io.github.ra1nify.rmuix.kmp:rmuix-ui-android:<version>")
}
```

完整项目配置与平台依赖见[快速开始](../../guide/getting-started)。

## 组件组织与宿主

- `io.github.ra1nify.rmuix.kmp.basic`：脚手架、导航、布局与交互控件。
- `io.github.ra1nify.rmuix.kmp.overlay`：在当前 Compose 页面内托管的浮层，必须位于 Rmuix [Scaffold](../containers/scaffold) 中，由其提供 `RmuixPopupHost`。
- `io.github.ra1nify.rmuix.kmp.window`：窗口级浮层，不依赖 `Scaffold` 弹窗宿主。

选择 Overlay 或 Window 时，应依据宿主与层级需求，而不是把两者当作不同模块。各组件页列出了具体参数、默认值和平台约束。

## 相关文档

- [主题系统](../../guide/theme)：颜色、文字与主题配置。
