# 选择与调节

开关、互斥选择、范围调节与筛选标签。

## 选型指引

| 场景 | 推荐组件 |
| :--- | :--- |
| 开关设置 | Switch |
| 多选 | Checkbox |
| N 选一 | RadioButton |
| 连续值 / 范围 | Slider（RangeSlider、VerticalSlider） |
| 筛选 / 标签 | Chip / ChipGroup |

## 组件

| 组件 | 说明 | 常用场景 |
| :--- | :--- | :--- |
| [Switch](./selection/switch) | 双态切换控件 | 设置项开关、功能启用 |
| [Checkbox](./selection/checkbox) | 多选控件 | 多项选择、条款同意 |
| [RadioButton](./selection/radiobutton) | 单选控件 | 单项选择、选项切换 |
| [Slider](./selection/slider) | 调节值的滑动控件 | 音量调节、范围选择 |
| [Chip](./selection/chip) | 胶囊形筛选 / 选择标签 | 筛选、标签、选项 |
| [ChipGroup](./selection/chipgroup) | 可折叠的芯片组 | 标签列表、筛选行 |

## 组合示例

```kotlin
Switch(checked = on, onCheckedChange = { on = it })
Slider(value = volume, onValueChange = { volume = it })
Chip(text = "全部", selected = true, onClick = {})
```

## 相关类型

[容器与布局](./containers) · [文本与图标](./text-and-icons) · [按钮与工具条](./buttons) · [输入与表单](./input) · [导航](./navigation) · [反馈与状态](./feedback) · [浮层与菜单](./overlays) · [偏好设置](./preferences) · [图像与特效](./media)

包内索引：[基础组件](./basic/) · [扩展组件](./extended/) · [Extend 组件](./extend/) · [Glass 组件](./glass/)
