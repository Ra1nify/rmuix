# 容器与布局

页面骨架、内容分组与分隔线。搭页面时从这里开始。

## 选型指引

| 场景 | 推荐组件 |
| :--- | :--- |
| 页面骨架 / 承载浮层 | Scaffold |
| 相关内容分组 | Card / BasicComponentGroup |
| 单条信息行 | BasicComponent |
| 背景容器 | Surface |
| 区块分隔 | Divider |

## 组件

| 组件 | 说明 | 常用场景 |
| :--- | :--- | :--- |
| [Scaffold](./containers/scaffold) | 应用的基础布局 | 页面结构、内容展示 |
| [Surface](./containers/surface) | 基础容器组件 | 内容展示、背景容器 |
| [Card](./containers/card) | 包含相关信息的容器 | 信息展示、内容分组 |
| [BasicComponent](./containers/basiccomponent) | 通用基础组件 | 自定义组件开发 |
| [BasicComponentGroup](./containers/basiccomponentgroup) | 只在组内行之间画分隔线的卡片组 | 设置类列表 |
| [Divider](./containers/divider) | 内容分隔线 | 区块分隔、层次划分 |

## 组合示例

```kotlin
Scaffold {
    Card {
        BasicComponentGroup {
            BasicComponent(title = "WLAN") { Text("已连接") }
            BasicComponent(title = "蓝牙") { Text("关闭") }
        }
    }
}
```

## 相关类型

[文本与图标](./text-and-icons) · [按钮与工具条](./buttons) · [输入与表单](./input) · [选择与调节](./selection) · [导航](./navigation) · [反馈与状态](./feedback) · [浮层与菜单](./overlays) · [偏好设置](./preferences) · [图像与特效](./media)

包内索引：[基础组件](./basic/) · [扩展组件](./extended/) · [Extend 组件](./extend/) · [Glass 组件](./glass/)
