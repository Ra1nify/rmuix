# 按钮与工具条

用户可触发的动作：从实心按钮到悬浮工具条。

## 选型指引

| 场景 | 推荐组件 |
| :--- | :--- |
| 主 / 次操作 | Button |
| 纯图标操作 | IconButton |
| 提交中状态 | LoadingButton |
| 等宽模式切换 | SegmentButton |
| 悬浮主操作 | FloatingActionButton |
| 底部工具条 | FloatingToolbar / EditToolbar |

## 组件

| 组件 | 说明 | 常用场景 |
| :--- | :--- | :--- |
| [Button](./buttons/button) | 触发操作的交互元素 | 表单提交、操作确认 |
| [IconButton](./buttons/iconbutton) | 图标按钮组件 | 辅助操作、工具栏 |
| [LoadingButton](./buttons/loadingbutton) | 用一个三点加载替换文字的按钮 | 提交中状态 |
| [SegmentButton](./buttons/segmentbutton) | 胶囊轨道 + 等宽分段 | 紧凑的模式切换 |
| [FloatingActionButton](./buttons/floatingactionbutton) | 悬浮操作按钮 | 主要操作、快捷功能 |
| [FloatingToolbar](./buttons/floatingtoolbar) | 悬浮工具栏 | 快捷操作、信息展示 |

## 组合示例

```kotlin
Button(onClick = {}) { Text("保存") }
IconButton(onClick = {}) { Icon(RmuixIcons.Settings, contentDescription = null) }
LoadingButton(loading = true, onClick = {}) { Text("保存中") }
```

## 相关类型

[容器与布局](./containers) · [文本与图标](./text-and-icons) · [输入与表单](./input) · [选择与调节](./selection) · [导航](./navigation) · [反馈与状态](./feedback) · [浮层与菜单](./overlays) · [偏好设置](./preferences) · [图像与特效](./media)

包内索引：[基础组件](./basic/) · [扩展组件](./extended/) · [Extend 组件](./extend/) · [Glass 组件](./glass/)
