# 反馈与状态

加载、空/完成态、临时消息、提示与徽标。

## 选型指引

| 场景 | 推荐组件 |
| :--- | :--- |
| 加载 / 进度 | ProgressIndicator |
| 轮播位置 | PageIndicator |
| 临时消息 | Snackbar |
| 长按提示 | Tooltip |
| 悬浮提示卡 | TopTips |
| 空数据 / 完成流程 | EmptyState / CompleteState |
| 未读徽标 | RedDot |
| 滚动指示 | ScrollBar |
| A–Z 列表索引 | AlphabetIndexBar |

## 组件

| 组件 | 说明 | 常用场景 |
| :--- | :--- | :--- |
| [ProgressIndicator](./feedback/progressindicator) | 展示操作进度状态 | 加载中、进度展示 |
| [PageIndicator](./feedback/pageindicator) | 一排扁平页码圆点 | 轮播、分页位置 |
| [Snackbar](./feedback/snackbar) | 底部临时消息条组件 | 状态提示、操作结果 |
| [Tooltip](./feedback/tooltip) | 悬停或长按显示的简短标签 | 图标按钮标签、元素提示 |
| [TopTips](./feedback/toptips) | 浮层提示卡：标题、正文、操作 | 省电、网络、模式提示 |
| [EmptyState](./feedback/emptystate) | 空状态页：插图、标题、副标题、操作文字 | 空列表、无结果页 |
| [CompleteState](./feedback/completestate) | 完成页，带 100dp 插图 | 流程结束、备份完成 |
| [RedDot](./feedback/reddot) | 红点提示，可带数字 | 未读、提示 |
| [ScrollBar](./feedback/scrollbar) | 可拖拽滚动指示条 | 长列表、滚动位置反馈 |
| [AlphabetIndexBar](./feedback/alphabetindexbar) | A–Z 快速滚动条，含按压字母气泡 | 长列表、应用列表 |

## 组合示例

```kotlin
if (loading) {
    CircularProgressIndicator()
}
EmptyState(title = "无结果", subtitle = "换个关键词试试")
```

## 相关类型

[容器与布局](./containers) · [文本与图标](./text-and-icons) · [按钮与工具条](./buttons) · [输入与表单](./input) · [选择与调节](./selection) · [导航](./navigation) · [浮层与菜单](./overlays) · [偏好设置](./preferences) · [图像与特效](./media)

包内索引：[基础组件](./basic/) · [扩展组件](./extended/) · [Extend 组件](./extend/) · [Glass 组件](./glass/)
