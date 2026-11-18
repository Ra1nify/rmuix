# 组件库

Rmuix 提供了一套遵循 ColorOS 17 设计语言的 UI 组件。每个组件都对照 COUI 原厂资源（`com.coui.appcompat.*`）逐项度量，以保证视觉与交互和原生实现一致；原厂没有对应控件的组件会在各自页面注明。

## 按类型浏览

| 类型 | 组件数 | 说明 |
| :--- | ---: | :--- |
| [容器与布局](./containers) | 6 | 页面骨架、内容分组与分隔线。搭页面时从这里开始。 |
| [文本与图标](./text-and-icons) | 3 | 排版与图标展示，其余组件都建立在这些原语之上。 |
| [按钮与工具条](./buttons) | 6 | 用户可触发的动作：从实心按钮到悬浮工具条。 |
| [输入与表单](./input) | 13 | 文本、数值、日期时间与安全输入。按表单收集的数据类型选择。 |
| [选择与调节](./selection) | 6 | 开关、互斥选择、范围调节与筛选标签。 |
| [导航](./navigation) | 6 | 页面级导航：顶栏、底栏、侧栏、抽屉与标签。 |
| [反馈与状态](./feedback) | 10 | 加载、空/完成态、临时消息、提示与徽标。 |
| [浮层与菜单](./overlays) | 19 | 对话框、底部面板、弹窗与动作菜单，含玻璃材质变体。 |
| [偏好设置](./preferences) | 11 | 基于 BasicComponent 的设置页行：开关、滑块与下拉选择。 |
| [图像与特效](./media) | 5 | 图片包装、选择器、裁剪器与触控光效。 |


## 脚手架

| 组件                               | 描述           | 常见用途           |
| ---------------------------------- | -------------- | ------------------ |
| [Scaffold](./containers/scaffold) | 应用的基础布局 | 页面结构、内容展示 |

::: warning 注意
Scaffold 组件为跨平台提供了一个合适的弹出窗口的容器。`OverlayDialog`、`OverlayDropdownPreference`、`OverlaySpinnerPreference`、`OverlayListPopup` 等组件都基于此实现弹出窗口，因此都需要被该组件包裹。
:::

## 基础组件

| 组件                                                       | 描述                   | 常见用途             |
| ---------------------------------------------------------- | ---------------------- | -------------------- |
| [Surface](./containers/surface)                           | 基础容器组件           | 内容展示、背景容器   |
| [TopAppBar](./navigation/topappbar)                       | 应用顶部的导航栏       | 页面标题、主要操作   |
| [NavigationBar](./navigation/navigationbar)               | 底部导航组件           | 主要页面切换         |
| [NavigationRail](./navigation/navigationrail)             | 侧边导航组件           | 主要页面切换（大屏）  |
| [TabRow](./navigation/tabrow)                             | 水平标签页切换栏       | 内容分类浏览         |
| [Card](./containers/card)                                 | 包含相关信息的容器     | 信息展示、内容分组   |
| [BasicComponent](./containers/basiccomponent)             | 通用基础组件           | 自定义组件开发       |
| [Button](./buttons/button)                             | 触发操作的交互元素     | 表单提交、操作确认   |
| [IconButton](./buttons/iconbutton)                     | 图标按钮组件           | 辅助操作、工具栏     |
| [Text](./text-and-icons/text)                                 | 展示各种样式的文字内容 | 标题、正文、描述文本 |
| [CategoryTitle](./text-and-icons/categorytitle)                     | 小型标题组件           | 辅助标题、分类标识   |
| [TextField](./input/textfield)                       | 接收用户文本输入       | 表单填写、搜索框     |
| [Switch](./selection/switch)                             | 双态切换控件           | 设置项开关、功能启用 |
| [Checkbox](./selection/checkbox)                         | 多选控件               | 多项选择、条款同意   |
| [RadioButton](./selection/radiobutton)                   | 单选控件               | 单项选择、选项切换   |
| [Slider](./selection/slider)                             | 调节值的滑动控件       | 音量调节、范围选择   |
| [NumberPicker](./input/numberpicker)                 | 垂直滚动数字选择器     | 时间选择、数量选择   |
| [ProgressIndicator](./feedback/progressindicator)       | 展示操作进度状态       | 加载中、进度展示     |
| [Snackbar](./feedback/snackbar)                         | 底部临时消息条组件     | 状态提示、操作结果   |
| [Tooltip](./feedback/tooltip)                           | 悬停或长按显示的简短标签 | 图标按钮标签、元素提示 |
| [Icon](./text-and-icons/icon)                                 | 图标展示组件           | 图标按钮、状态指示   |
| [FloatingActionButton](./buttons/floatingactionbutton) | 悬浮操作按钮           | 主要操作、快捷功能   |
| [FloatingToolbar](./buttons/floatingtoolbar)           | 悬浮工具栏             | 快捷操作、信息展示   |
| [Divider](./containers/divider)                           | 内容分隔线             | 区块分隔、层次划分   |
| [SearchBar](./input/searchbar)                       | 执行搜索的输入框       | 内容搜索、快速查找   |

## 扩展组件

| 组件                                                 | 描述                                                                               | 常见用途               |
| ---------------------------------------------------- | ---------------------------------------------------------------------------------- | ---------------------- |
| [JumpPreference](./preferences/jumppreference)               | 基于 BasicComponent 的带箭头组件                                                   | 指示可点击、导航提示   |
| [SwitchPreference](./preferences/switchpreference)             | 基于 BasicComponent 的开关组件                                                     | 设置项开关、功能启用   |
| [CheckboxPreference](./preferences/checkboxpreference)         | 基于 BasicComponent 的复选框组件                                                   | 多项选择、条款同意     |
| [RadioButtonPreference](./preferences/radiobuttonpreference)   | 基于 BasicComponent 的单选组件                                                     | 单项选择、选项切换     |
| [SliderPreference](./preferences/sliderpreference)             | 基于 BasicComponent 的滑块组件                                                     | 数值调节、音量/亮度    |
| [RangeSliderPreference](./preferences/rangesliderpreference)   | 基于 BasicComponent 的范围滑块组件                                                 | 范围选择、价格筛选     |
| [OverlayListPopup](./overlays/overlaylistpopup)             | 基于 BasicComponent 的列表弹窗组件（使用 RmuixPopupUtils，需在 `Scaffold` 中使用） | 选项选择、功能列表     |
| [OverlayCascadingListPopup](./overlays/overlaycascadinglistpopup) | 二级级联列表弹窗组件（使用 RmuixPopupUtils，需在 `Scaffold` 中使用）               | 子菜单、分组动作面板   |
| [OverlayDropdownPreference](./preferences/overlaydropdownpreference) | 基于 BasicComponent 的下拉选择器组件（使用 RmuixPopupUtils，需在 `Scaffold` 中使用） | 选项选择、功能列表     |
| [OverlaySpinnerPreference](./preferences/overlayspinnerpreference) | 基于 BasicComponent 的高级选择器组件（使用 RmuixPopupUtils，需在 `Scaffold` 中使用） | 进阶选项选择、功能列表 |
| [OverlayDropdownMenu](./overlays/overlaydropdownmenu)       | 基于 BasicComponent 的动作菜单组件（使用 RmuixPopupUtils，需在 `Scaffold` 中使用） | 动作菜单、多选场景     |
| [OverlayIconDropdownMenu](./overlays/overlayicondropdownmenu) | 基于 IconButton 的动作菜单组件（使用 RmuixPopupUtils，需在 `Scaffold` 中使用）     | 工具栏动作、更多菜单   |
| [OverlayIconCascadingDropdownMenu](./overlays/overlayiconcascadingdropdownmenu) | 基于 IconButton 的二级级联动作菜单组件（使用 RmuixPopupUtils，需在 `Scaffold` 中使用） | 含子菜单的工具栏动作   |
| [OverlayBottomSheet](./overlays/overlaybottomsheet)         | 基于 BasicComponent 的底部抽屉组件（使用 RmuixPopupUtils，需在 `Scaffold` 中使用） | 底部抽屉、附加选项     |
| [OverlayDialog](./overlays/overlaydialog)                   | 基于 BasicComponent 的对话弹窗组件（使用 RmuixPopupUtils，需在 `Scaffold` 中使用） | 提示、确认操作         |
| [WindowListPopup](./overlays/windowlistpopup)     | 窗口级列表弹窗组件                                                                 | 选项选择、功能列表     |
| [WindowCascadingListPopup](./overlays/windowcascadinglistpopup) | 窗口级二级级联列表弹窗组件                                                         | 子菜单、分组动作面板   |
| [WindowDropdownPreference](./preferences/windowdropdownpreference) | 窗口级下拉选择器组件                                                               | 选项选择、功能列表     |
| [WindowSpinnerPreference](./preferences/windowspinnerpreference) | 窗口级高级选择器组件                                                               | 进阶选项选择、功能列表 |
| [WindowDropdownMenu](./overlays/windowdropdownmenu) | 窗口级动作菜单组件                                                                 | 动作菜单、多选场景     |
| [WindowIconDropdownMenu](./overlays/windowicondropdownmenu) | 基于 IconButton 的窗口级动作菜单组件                                               | 工具栏动作、更多菜单   |
| [WindowIconCascadingDropdownMenu](./overlays/windowiconcascadingdropdownmenu) | 基于 IconButton 的窗口级二级级联动作菜单组件                                       | 含子菜单的工具栏动作   |
| [WindowBottomSheet](./overlays/windowbottomsheet) | 窗口级底部抽屉组件                                                                 | 底部抽屉、附加选项     |
| [WindowDialog](./overlays/windowdialog)           | 窗口级对话框组件                                                                   | 提示、确认操作         |
| [GlassOverlayDialog](./overlays/glassdialog) | 带 backdrop 采样模糊的 overlay 对话框（`rmuix-glass`） | 磨砂对话框、材质表面 |
| [GlassWindowDialog](./overlays/glassdialog) | 接平台窗口模糊的 window 对话框（`rmuix-glass`） | Android 12+ 上的磨砂对话框 |
| [GlassOverlayBottomSheet](./overlays/glassbottomsheet) | 带 backdrop 采样模糊的 overlay 底部弹层（`rmuix-glass`） | 磨砂弹层 |
| [GlassWindowBottomSheet](./overlays/glassbottomsheet) | 接平台窗口模糊的 window 底部弹层（`rmuix-glass`） | Android 12+ 上的磨砂弹层 |
| [GlassFloatingNavigationBar](./overlays/glassfloatingnavigationbar) | 带 ColorOS bottom-bar 材质的悬浮导航胶囊（`rmuix-glass`） | 磨砂底部导航 |
