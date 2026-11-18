# 输入与表单

文本、数值、日期时间与安全输入。按表单收集的数据类型选择。

## 选型指引

| 场景 | 推荐组件 |
| :--- | :--- |
| 自由文本 | TextField |
| 搜索 | SearchBar |
| 验证码 | CodeInput |
| 卡片式单行输入 | SingleInputCard |
| 数字 / 数量 | NumberPicker / Stepper |
| 日期 | DatePicker / CalendarPicker |
| 时间 | TimePicker / TimeLimitPicker |
| PIN / 图案 | SimpleLock / NumericKeyboard / LockPatternView |

## 组件

| 组件 | 说明 | 常用场景 |
| :--- | :--- | :--- |
| [TextField](./input/textfield) | 接收用户文本输入 | 表单填写、搜索框 |
| [SearchBar](./input/searchbar) | 执行搜索的输入框 | 内容搜索、快速查找 |
| [CodeInput](./input/codeinput) | 分格验证码输入，透明输入框叠在格子上 | 验证码、口令 |
| [SingleInputCard](./input/singleinputcard) | 卡片式单行输入，含计数/清除/错误 | 邮箱绑定、单行输入 |
| [NumberPicker](./input/numberpicker) | 垂直滚动数字选择器 | 时间选择、数量选择 |
| [DatePicker](./input/datepicker) | 滚轮日期选择器（年 / 月 / 日） | 日期设置、日程 |
| [TimePicker](./input/timepicker) | 滚轮时间选择器，12 / 24 小时制 | 闹钟、定时 |
| [TimeLimitPicker](./input/timelimitpicker) | 带 `小时` / `分钟` 单位文案的滚轮时间选择器 | 定时类设置页 |
| [CalendarPicker](./input/calendarpicker) | 月历选择器，含今天/选中态 | 日期选择、拍摄时间 |
| [Stepper](./input/stepper) | 带加减按钮的数值步进器 | 数量、档位、步长调整 |
| [SimpleLock](./input/simplelock) | PIN 圆点：4/6 位，按输入填充 | PIN 输入、安全流程 |
| [NumericKeyboard](./input/numerickeyboard) | 3 × 4 PIN 数字键盘，含删除/完成侧键 | PIN/密码输入 |
| [LockPatternView](./input/lockpatternview) | 3 × 3 图案锁，含成功/错误态 | 图案解锁、隐私 |

## 组合示例

```kotlin
TextField(value = text, onValueChange = { text = it })
NumericKeyboard(onNumber = { digit -> }, onDelete = {})
```

## 相关类型

[容器与布局](./containers) · [文本与图标](./text-and-icons) · [按钮与工具条](./buttons) · [选择与调节](./selection) · [导航](./navigation) · [反馈与状态](./feedback) · [浮层与菜单](./overlays) · [偏好设置](./preferences) · [图像与特效](./media)

包内索引：[基础组件](./basic/) · [扩展组件](./extended/) · [Extend 组件](./extend/) · [Glass 组件](./glass/)
