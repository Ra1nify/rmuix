# 偏好设置

基于 BasicComponent 的设置页行：开关、滑块与下拉选择。

## 选型指引

| 场景 | 推荐组件 |
| :--- | :--- |
| 只读信息行 | AppInfoPreference / JumpPreference |
| 开关设置 | SwitchPreference |
| 勾选 / 单选行 | CheckboxPreference / RadioButtonPreference |
| 数值 / 范围 | SliderPreference / RangeSliderPreference |
| 下拉选择 | Overlay/Window DropdownPreference |
| 高级选择器 | Overlay/Window SpinnerPreference |

## 组件

| 组件 | 说明 | 常用场景 |
| :--- | :--- | :--- |
| [AppInfoPreference](./preferences/appinfopreference) | 带图标、应用名与版本号的应用头部；长按版本号可复制 | 关于页 |
| [JumpPreference](./preferences/jumppreference) | 基于 BasicComponent 的带箭头组件 | 指示可点击、导航提示 |
| [SwitchPreference](./preferences/switchpreference) | 基于 BasicComponent 的开关组件 | 设置项开关、功能启用 |
| [CheckboxPreference](./preferences/checkboxpreference) | 基于 BasicComponent 的复选框组件 | 多项选择、条款同意 |
| [RadioButtonPreference](./preferences/radiobuttonpreference) | 基于 BasicComponent 的单选组件 | 单项选择、选项切换 |
| [SliderPreference](./preferences/sliderpreference) | 基于 BasicComponent 的滑块组件 | 数值调节、音量/亮度 |
| [RangeSliderPreference](./preferences/rangesliderpreference) | 基于 BasicComponent 的范围滑块组件 | 范围选择、价格筛选 |
| [OverlayDropdownPreference](./preferences/overlaydropdownpreference) | 基于 BasicComponent 的下拉选择器组件（使用 RmuixPopupUtils，需在 `Scaffold` 中使用） | 选项选择、功能列表 |
| [OverlaySpinnerPreference](./preferences/overlayspinnerpreference) | 基于 BasicComponent 的高级选择器组件（使用 RmuixPopupUtils，需在 `Scaffold` 中使用） | 进阶选项选择、功能列表 |
| [WindowDropdownPreference](./preferences/windowdropdownpreference) | 窗口级下拉选择器组件 | 选项选择、功能列表 |
| [WindowSpinnerPreference](./preferences/windowspinnerpreference) | 窗口级高级选择器组件 | 进阶选项选择、功能列表 |

## 组合示例

```kotlin
SwitchPreference(title = "WLAN", checked = on, onCheckedChange = { on = it })
JumpPreference(title = "关于") { /* 导航 */ }
```

## 相关类型

[容器与布局](./containers) · [文本与图标](./text-and-icons) · [按钮与工具条](./buttons) · [输入与表单](./input) · [选择与调节](./selection) · [导航](./navigation) · [反馈与状态](./feedback) · [浮层与菜单](./overlays) · [图像与特效](./media)

包内索引：[基础组件](./basic/) · [扩展组件](./extended/) · [Extend 组件](./extend/) · [Glass 组件](./glass/)
