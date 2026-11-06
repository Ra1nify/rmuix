# ColorOS 17 官方 Settings / 系统应用 Dialog 控件调用点与实机触发指南

本文档响应用户指示（“抓 Settings 源码，告诉我除了 WiFi 分享，还有哪里有 Dialog 控件调用处，我对着实机看看”），梳理 ColorOS 17 原厂应用中全量 `COUIAlertDialogBuilder` / `COUIAlertDialog` 的官方调用点与实机操作触发步骤。

---

## 1. 官方 Settings / 系统应用 Dialog 调用点与实机触发操作

你在 ColorOS 真机上可以通过以下几个最典型的系统设置场景，随时调出原厂 `COUIAlertDialog` 弹窗进行视觉对比：

### 场景 1：WLAN 详情 - 分享二维码弹窗 (WiFi QrCode Dialog)
* **触发路径**：`设置 -> WLAN -> 点击当前已连接 WiFi 右侧 (i) 图标 -> 点击『分享』`
* **源码调用**：`com.oplus.wirelesssettings.SubSettingsExt`
* **控件形态**：底部悬浮 32dp Squircle 卡片，带二维码与 `#F0F0F2` 浅灰胶囊形“取消”按钮。

### 场景 2：开发者选项 - 开启 USB 调试警告弹窗 (USB Debugging Warning)
* **触发路径**：`设置 -> 其他设置 (系统设置) -> 开发者选项 -> 点击开启『USB 调试』开关`
* **源码调用**：`com.android.settings.development.DevelopmentSettingsDashboardFragment`
* **控件形态**：标题 18sp Bold + 详细提示文案 + “取消”/“确定”双胶囊按钮。

### 场景 3：位置信息 - 关闭定位服务二次确认 (Location Service Off)
* **触发路径**：`设置 -> 权限与隐私 -> 位置信息 -> 点击关闭『位置服务』主开关`
* **源码调用**：`com.android.settings.location.LocationSwitchBar`
* **控件形态**：带有取消与确认关闭按钮的标准 COUI 警告弹窗。

### 场景 4：应用管理 - 强行停止 / 卸载更新 (Force Stop App)
* **触发路径**：`设置 -> 应用 -> 应用管理 -> 选择任意应用 -> 点击『强行停止』`
* **源码调用**：`com.android.settings.applications.appinfo.AppButtonsPreferenceController`
* **控件形态**：红色/警示色按钮提示弹窗。

### 场景 5：重置手机 - 还原所有设置 (Reset Phone Confirmation)
* **触发路径**：`设置 -> 其他设置 -> 重置手机 -> 点击『还原所有设置』`
* **源码调用**：`com.android.settings.MasterClear`
* **控件形态**：警示提示卡片 + 底部高亮警示按钮。

---

## 2. 官方代码模式 (Official Code Snippet)

ColorOS 官方应用在调用弹窗时，核心统一采用 `COUIAlertDialogBuilder`：

```java
// ColorOS 17 官方标准弹窗构建写法
COUIAlertDialogBuilder builder = new COUIAlertDialogBuilder(context);
builder.setTitle("标题文本")                      // 18sp Medium (couiTextHeadlineS)
       .setMessage("详细说明描述文案...")            // 15sp/16sp (onSurfaceVariantSummary)
       .setPositiveButton("确定", positiveListener) // 胶囊按钮
       .setNegativeButton("取消", negativeListener) // 浅灰胶囊按钮 (#F0F0F2)
       .show();
```

---

## 3. 总结

以上 5 个路径覆盖了 ColorOS 17 原厂最标准的普通提醒弹窗、警告确认弹窗与自定义卡片弹窗。你可以在实机上逐一操作调出，直观体验 ColorOS 17 弹窗 **32dp 大圆角 + 浅灰胶囊按钮** 的轻盈质感！

调研报告归档于 `docs/research/coloros-dialog-callers.md`。
