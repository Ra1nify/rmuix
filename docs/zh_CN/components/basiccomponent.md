# BasicComponent

`BasicComponent` 是 Rmuix 中的基础标准组件。提供了标题、摘要以及左右两侧的可自定义内容区域，常用于构建列表项、设置项等界面元素。

本项目以此为基础提供了一些扩展组件，方便开发者快速构建符合设计规范的 UI 组件，详见[扩展组件](../components/#扩展组件)的使用。

<div style="position: relative; height: 350px; border-radius: 10px; overflow: hidden; border: 1px solid #777;">
    <iframe id="demoIframe" style="position: absolute; top: 0; left: 0; width: 100%; height: 100%; border: none;" src="../../compose/index.html?id=basicComponent" title="Demo" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" referrerpolicy="strict-origin-when-cross-origin"></iframe>
</div>

## 引入

```kotlin
import io.github.ra1nify.rmuix.kmp.basic.BasicComponent
```

## 基本用法

BasicComponent 组件可以用于展示标题和摘要信息：

```kotlin
BasicComponent(
    title = "设置项标题",
    summary = "这里是设置项的描述文本"
)
```

## 组件变体

### 可点击组件

```kotlin
BasicComponent(
    title = "Wi-Fi",
    summary = "已连接到 MIUI-WiFi",
    onClick = { /* 处理点击事件 */ }
)
```

### 带左侧图标的组件

```kotlin
BasicComponent(
    title = "昵称",
    summary = "一段简介",
    startAction = {
        Icon(
            modifier = Modifier.padding(end = 16.dp),
            imageVector = RmuixIcons.Contacts,
            contentDescription = "头像图标",
            tint = RmuixTheme.colorScheme.onBackground
        )
    },
    onClick = { /* 处理点击事件 */ }
)
```

### 带右侧操作的组件

```kotlin
var isFlightMode by remember { mutableStateOf(false) }

BasicComponent(
    title = "飞行模式",
    endActions = {
        Switch(
            checked = isFlightMode,
            onCheckedChange = { isFlightMode = it }
        )
    }
)
```

### 自定义内容变体

除了带标题和摘要的形式外，BasicComponent 还提供了一个接收自定义内容的重载。当你希望完全掌控组件内部布局，但仍复用容器与交互效果时，可以使用该形式：

```kotlin
BasicComponent(
    startAction = {
        Icon(
            modifier = Modifier.padding(end = 16.dp),
            imageVector = RmuixIcons.Contacts,
            contentDescription = "头像图标",
            tint = RmuixTheme.colorScheme.onBackground
        )
    },
    endActions = {
        Text("详情")
    }
) {
    Text(
        text = "自定义标题",
        style = RmuixTheme.textStyles.headline1
    )
    Text(
        text = "自定义内容说明",
        style = RmuixTheme.textStyles.body2
    )
}
```

## 组件状态

### 禁用状态

```kotlin
BasicComponent(
    title = "移动网络",
    summary = "SIM卡未插入",
    enabled = false
)
```

## 属性

### BasicComponent 属性（标题和摘要变体）

| 属性名            | 类型                               | 说明                 | 默认值                                | 是否必须 |
| ----------------- | ---------------------------------- | -------------------- | ------------------------------------- | -------- |
| modifier          | Modifier                           | 应用于组件的修饰符   | Modifier                              | 否       |
| title             | String?                            | 组件标题             | null                                  | 否       |
| titleColor        | BasicComponentColors               | 标题颜色配置         | BasicComponentDefaults.titleColor()   | 否       |
| summary           | String?                            | 组件摘要             | null                                  | 否       |
| summaryColor      | BasicComponentColors               | 摘要颜色配置         | BasicComponentDefaults.summaryColor() | 否       |
| assignment        | String?                            | 可选第三行文字，显示在 summary 下方                   | null                                  | 否       |
| assignmentColor   | BasicComponentColors               | 第三行文字的颜色配置                                  | BasicComponentDefaults.assignmentColor() | 否    |
| startAction       | @Composable (() -> Unit)?          | 组件左侧的可组合内容 | null                                  | 否       |
| endActions        | @Composable (RowScope.() -> Unit)? | 组件右侧的可组合内容 | null                                  | 否       |
| bottomAction      | @Composable (() -> Unit)?          | 组件底部的可组合内容 | null                                  | 否       |
| insideMargin      | PaddingValues                      | 组件内部边距         | BasicComponentDefaults.InsideMargin   | 否       |
| onClick           | (() -> Unit)?                      | 点击组件时触发的回调 | null                                  | 否       |
| onClickLabel      | String?                            | 无障碍点击操作描述   | null                                  | 否       |
| role              | Role?                              | 无障碍语义角色       | null                                  | 否       |
| holdDownState     | Boolean                            | 组件是否处于按下状态 | false                                 | 否       |
| enabled           | Boolean                            | 组件是否可用         | true                                  | 否       |
| iconBadge         | Boolean                            | 在 startAction 右上角显示提示红点                     | false                                 | 否       |
| iconBadgeCount    | Int                                | 红点内的数字；0 显示纯圆点                            | 0                                     | 否       |
| endBadge          | Boolean                            | 在 endActions 前显示提示红点                          | false                                 | 否       |
| iconMarginTiered  | Boolean                            | 按 ColorOS 分档选择图标的垂直边距                     | true                                  | 否       |
| interactionSource | MutableInteractionSource?          | 组件的交互源         | null                                  | 否       |

### BasicComponent 属性（自定义内容变体）

| 属性名            | 类型                               | 说明                 | 默认值                              | 是否必须 |
| ----------------- | ---------------------------------- | -------------------- | ----------------------------------- | -------- |
| modifier          | Modifier                           | 应用于组件的修饰符   | Modifier                            | 否       |
| startAction       | @Composable (() -> Unit)?          | 组件左侧的可组合内容 | null                                | 否       |
| endActions        | @Composable (RowScope.() -> Unit)? | 组件右侧的可组合内容 | null                                | 否       |
| bottomAction      | @Composable (() -> Unit)?          | 组件底部的可组合内容 | null                                | 否       |
| insideMargin      | PaddingValues                      | 组件内部边距         | BasicComponentDefaults.InsideMargin | 否       |
| onClick           | (() -> Unit)?                      | 点击组件时触发的回调 | null                                | 否       |
| onClickLabel      | String?                            | 无障碍点击操作描述   | null                                | 否       |
| role              | Role?                              | 无障碍语义角色       | null                                | 否       |
| holdDownState     | Boolean                            | 组件是否处于按下状态 | false                               | 否       |
| enabled           | Boolean                            | 组件是否可用         | true                                | 否       |
| interactionSource | MutableInteractionSource?          | 组件的交互源         | null                                | 否       |
| content           | @Composable ColumnScope.() -> Unit | 组件内部的可组合内容 | -                                   | 是       |

### BasicComponentDefaults 对象

BasicComponentDefaults 对象提供了 BasicComponent 组件的默认值和颜色配置。

#### BasicComponentDefaults 常量

| 常量名              | 类型          | 描述                                     | 默认值                                |
| ------------------ | ------------- | ---------------------------------------- | ------------------------------------- |
| MinHeight          | Dp            | 行最小高度                                | 48.dp                                 |
| InsideMargin       | PaddingValues | 组件内部边距                              | PaddingValues(horizontal = 32.dp, vertical = 10.dp) |
| IconTextGap        | Dp            | 前置图标与文字列的间距                     | 16.dp                                 |
| IconMarginDefault  | Dp            | 未命中分档时的图标垂直边距                 | 14.dp                                 |
| IconMargin24       | Dp            | 24dp 图标，单行                          | 12.dp                                 |
| IconMargin24Multiline | Dp         | 24dp 图标，多行                          | 20.dp                                 |
| IconMargin32       | Dp            | 32dp 图标，单行                          | 10.dp                                 |
| IconMargin32Multiline | Dp         | 32dp 图标，多行                          | 16.dp                                 |
| IconMargin36       | Dp            | 36dp 图标，单行                          | 12.dp                                 |
| IconMargin36Multiline | Dp         | 36dp 图标，多行                          | 14.dp                                 |
| IconMargin50       | Dp            | 50dp 图标，单/多行                       | 8.dp                                  |
| BadgeSize          | Dp            | 纯圆点红点直径                            | 6.dp                                  |
| BadgeCornerRadius  | Dp            | 数字红点圆角                              | 2.67.dp                               |
| BadgeTextPadding   | Dp            | 数字红点内水平留白                        | 2.dp                                  |
| BadgeGap           | Dp            | 尾部红点与内容的间距                      | 4.dp                                  |

#### BasicComponentDefaults 方法

| 方法名         | 类型                 | 说明             |
| -------------- | -------------------- | ---------------- |
| titleColor()   | BasicComponentColors | 创建标题颜色配置 |
| summaryColor() | BasicComponentColors | 创建摘要颜色配置 |

### BasicComponentColors 类

用于配置组件的颜色状态。

| 属性名        | 类型  | 说明           |
| ------------- | ----- | -------------- |
| color         | Color | 正常状态的颜色 |
| disabledColor | Color | 禁用状态的颜色 |

## 进阶用法

### 复杂布局组件

```kotlin
BasicComponent(
    title = "音量",
    summary = "媒体音量：70%",
        startAction = {
        Icon(
        modifier = Modifier.padding(end = 16.dp),
            imageVector = RmuixIcons.Play,
            contentDescription = "音量图标",
            tint = RmuixTheme.colorScheme.onBackground
        )
    },
    endActions = {
        IconButton(onClick = { /* 减小音量 */ }) {
            Icon(
                imageVector = RmuixIcons.Remove,
                contentDescription = "减小音量",
                tint = RmuixTheme.colorScheme.onBackground
            )
        }
        Text("70%")
        IconButton(onClick = { /* 增大音量 */ }) {
            Icon(
                imageVector = RmuixIcons.Add,
                contentDescription = "增大音量",
                tint = RmuixTheme.colorScheme.onBackground
            )
        }
    }
)
```

### 自定义样式组件

```kotlin
BasicComponent(
    title = "自定义组件",
    summary = "使用自定义颜色",
    titleColor = BasicComponentColors(
        color = Color.Blue,
        disabledColor = Color.Gray
    ),
    summaryColor = BasicComponentColors(
        color = Color.DarkGray,
        disabledColor = Color.LightGray
    ),
    insideMargin = PaddingValues(horizontal = 24.dp, vertical = 12.dp),
    onClick = { /* 处理选项点击 */ }
)
```

### 列表中使用

```kotlin
val options = listOf("选项1", "选项2", "选项3", "选项4")

Column {
    options.forEach { option ->
        BasicComponent(
            title = option,
            onClick = { /* 处理选项点击 */ }
        )
    }
}
```
