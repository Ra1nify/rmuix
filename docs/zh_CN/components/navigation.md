# 导航

页面级导航：顶栏、底栏、侧栏、抽屉与标签。

## 选型指引

| 场景 | 推荐组件 |
| :--- | :--- |
| 页面标题 + 操作 | TopAppBar |
| 底部主标签 | NavigationBar / FloatingNavigationBar |
| 大屏侧导航 | NavigationRail |
| 滑出菜单 | SideNavigationDrawer |
| 内容标签 | TabRow / ChipTabRow |

## 组件

| 组件 | 说明 | 常用场景 |
| :--- | :--- | :--- |
| [TopAppBar](./navigation/topappbar) | 应用顶部的导航栏 | 页面标题、主要操作 |
| [NavigationBar](./navigation/navigationbar) | 底部导航组件 | 主要页面切换 |
| [SideNavigationDrawer](./navigation/sidenavigationdrawer) | 带遮罩与分隔线的侧滑抽屉 | 相册/分类导航 |
| [NavigationRail](./navigation/navigationrail) | 侧边导航组件 | 主要页面切换（大屏） |
| [TabRow](./navigation/tabrow) | 水平标签页切换栏 | 内容分类浏览 |
| [ChipTabRow](./navigation/chiptabrow) | Chip 风格标签栏 | 筛选类切换 |

## 组合示例

```kotlin
Scaffold(
    topBar = { TopAppBar(title = { Text("设置") }) },
    bottomBar = { NavigationBar { /* items */ } },
) { /* 内容 */ }
```

## 相关类型

[容器与布局](./containers) · [文本与图标](./text-and-icons) · [按钮与工具条](./buttons) · [输入与表单](./input) · [选择与调节](./selection) · [反馈与状态](./feedback) · [浮层与菜单](./overlays) · [偏好设置](./preferences) · [图像与特效](./media)

包内索引：[基础组件](./basic/) · [扩展组件](./extended/) · [Extend 组件](./extend/) · [Glass 组件](./glass/)
