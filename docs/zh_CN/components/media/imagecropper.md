# ImageCropper

平移/缩放的图片裁剪器：固定裁剪视口叠在图上，视口外区域用遮罩压暗。

::: warning 没有 ColorOS 对应物
ColorOS 没有 Compose 图片裁剪器——`com.coui.appcompat` 里没有这个类，官方裁剪流程是一个 Activity intent——
因此本组件**没有官方度量**，放在 `rmuix-extend`，尺寸来自本库令牌。
:::

## 引入

```kotlin
import io.github.ra1nify.rmuix.kmp.extend.image.ImageCropper
import io.github.ra1nify.rmuix.kmp.extend.image.rememberImageCropperState
```

## 基本用法

```kotlin
val image: ImageBitmap = rememberDecodedBitmap()   // 自己解码用户选的图
val state = rememberImageCropperState(image)

ImageCropper(
    state = state,
    modifier = Modifier.padding(16.dp),
    cropRatio = 1.6f,          // 卡片封面
)

Button(onClick = { cropped = state.crop() }) { Text("裁剪") }
```

`state.crop()` 返回当前视口按源图 1:1 分辨率渲染出的 `ImageBitmap`；首次布局前返回 `null`。

## 参数

| 参数名     | 类型               | 说明                          | 默认值 |
| :--------- | :----------------- | :---------------------------- | :----- |
| state      | ImageCropperState  | 图片、缩放、平移与视口        | - |
| modifier   | Modifier           | 应用到裁剪器                  | Modifier |
| cropRatio  | Float              | 视口宽高比                    | 1f |
| shape      | Shape              | 视口轮廓                      | RoundedCornerShape(16.dp) |
| maskColor  | Color              | 视口外遮罩色                  | #66000000 |
| enabled    | Boolean            | 是否接受手势                  | true |

## ImageCropperState

| 名称           | 类型      | 说明                                     |
| :------------- | :-------- | :--------------------------------------- |
| image          | ImageBitmap | 被裁剪的图片                            |
| scale          | Float     | 用户缩放，`1f` = 覆盖比例                |
| offset         | Offset    | 平移量（像素），相对视口左上角            |
| viewport       | Size      | 裁剪视口（像素），布局时写入              |
| canCrop        | Boolean   | 视口是否已布局                            |
| effectiveScale | Float     | 覆盖比例 × `scale`                        |
| setImage()     | -         | 换图并重置变换                            |
| reset()        | -         | 重置缩放与平移                            |
| applyTransform() | -       | 以焦点为中心缩放并平移                    |
| crop()         | ImageBitmap? | 把视口渲染成新位图                     |

## 几何说明

- `scale` 是相对**覆盖比例**（`max(视口宽/源宽, 视口高/源高)`）的，所以 `1f` 永远表示"图片刚好铺满视口"，
  平移不可能露出空边。
- 缩放被夹在 `[1f, ImageCropperDefaults.MAX_SCALE_FACTOR]`（4 倍）。
- 手势：单指平移、双指捏合缩放；不支持旋转。
- 组件接收 `ImageBitmap` 而不是平台 Uri，以保持多平台——解码用户选择的图片（以及把结果写回文件）由调用方负责。

## ImageCropperDefaults

| 名称            | 类型  | 值 |
| :-------------- | :---- | :---- |
| MASK_COLOR_ARGB | Long  | 0x66000000 |
| MAX_SCALE_FACTOR| Float | 4 |
| SAFE_INSET_DP   | Float | 16 |
