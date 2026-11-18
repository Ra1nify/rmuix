# 文本与图标

排版与图标展示，其余组件都建立在这些原语之上。

## 选型指引

| 场景 | 推荐组件 |
| :--- | :--- |
| 正文 / 标题 | Text |
| 小标题 | CategoryTitle |
| 任意矢量图标 | Icon + RmuixIcons |

## 组件

| 组件 | 说明 | 常用场景 |
| :--- | :--- | :--- |
| [Text](./text-and-icons/text) | 展示各种样式的文字内容 | 标题、正文、描述文本 |
| [CategoryTitle](./text-and-icons/categorytitle) | 小型标题组件 | 辅助标题、分类标识 |
| [Icon](./text-and-icons/icon) | 图标展示组件 | 图标按钮、状态指示 |

## 组合示例

```kotlin
CategoryTitle(text = "显示")
Text(text = "亮度")
Icon(imageVector = RmuixIcons.Settings, contentDescription = null)
```

## 相关类型

[容器与布局](./containers) · [按钮与工具条](./buttons) · [输入与表单](./input) · [选择与调节](./selection) · [导航](./navigation) · [反馈与状态](./feedback) · [浮层与菜单](./overlays) · [偏好设置](./preferences) · [图像与特效](./media)

包内索引：[基础组件](./basic/) · [扩展组件](./extended/) · [Extend 组件](./extend/) · [Glass 组件](./glass/)
