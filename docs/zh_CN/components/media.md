# 图像与特效

图片包装、选择器、裁剪器与触控光效。

## 选型指引

| 场景 | 推荐组件 |
| :--- | :--- |
| 头像 / 图标图片 | RoundImage |
| 取色 | ColorPicker |
| 裁剪图片 | ImageCropper |
| 触控光效 | Spotlight |
| 按压反馈 | Modifier.pressable |

## 组件

| 组件 | 说明 | 常用场景 |
| :--- | :--- | :--- |
| [RoundImage](./media/roundimage) | 圆形/圆角/带阴影的图片容器 | 头像、设置图标 |
| [Spotlight](./media/spotlight) | 共享原厂光照内核与预设 | 触摸聚光、材质预览 |
| [Modifier.pressable](./media/pressable) | 按压反馈 Modifier（可附带点击） | 卡片、色块、自定义点击区域 |
| [ColorPicker](./media/colorpicker) | 取色器 + 颜色偏好行 | ColorOS 没有取色器类 |
| [ImageCropper](./media/imagecropper) | 平移/缩放的固定视口裁剪器 | ColorOS 没有裁剪组件 |

## 组合示例

```kotlin
RoundImage(
    painter = painterResource(Res.drawable.avatar),
    contentDescription = null,
    modifier = Modifier.size(36.dp),
)
```

## 相关类型

[容器与布局](./containers) · [文本与图标](./text-and-icons) · [按钮与工具条](./buttons) · [输入与表单](./input) · [选择与调节](./selection) · [导航](./navigation) · [反馈与状态](./feedback) · [浮层与菜单](./overlays) · [偏好设置](./preferences)

包内索引：[基础组件](./basic/) · [扩展组件](./extended/) · [Extend 组件](./extend/) · [Glass 组件](./glass/)
