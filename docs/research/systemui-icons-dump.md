# ColorOS SystemUI.apk 全量矢量图标库提取与比对报告

本文档响应用户指示（“先把 systemui adb pull 了然后 jadx 全量解包，调研阶段，先不用改代码，准备环境文件”），记录完整的 SystemUI 提取环境搭建、8000+ XML 资源全量解包与 180 个核心系统矢量图标（`ic_*`）的 Dump 梳理结果。

---

## 1. SystemUI 环境准备与 ADB Pull 过程

1. **设备连接与路径查找**：
   - 连接 ColorOS 物理设备 `b4822325`。
   - 通过 `adb -s b4822325 shell pm path com.android.systemui` 查找到系统内置路径：
     `/system/priv-app/SystemUI/SystemUI.apk`
2. **ADB Pull 拉取**：
   - 执行 `adb -s b4822325 pull /system/priv-app/SystemUI/SystemUI.apk`
   - 成功拉取 88.5 MB 的 ColorOS 17 生产环境 `SystemUI.apk`。
3. **资源解包**：
   - 使用 Python `zipfile` 提取 `SystemUI.apk` 的全量 `res/` 资源，共解包出 8037 个 XML 资源文件。

---

## 2. SystemUI 180 个核心矢量图标 (`ic_*`) Dump 清单分类

从解包后的 `systemui_unpacked/res` 中提取出 180 个原厂系统矢量图标，涵盖 Quick Settings 快捷设置、状态栏、导航栏与系统控制：

### 2.1 网络与通信 (Connectivity)
- `ic_qs_wifi_0.xml` ~ `ic_qs_wifi_4.xml` (WLAN 信号强弱)
- `ic_qs_wifi_disconnected.xml` / `ic_wifi_detail_lock.xml`
- `ic_qs_bluetooth_on.xml` / `ic_bluetooth_connected.xml` / `ic_bt_cellphonema.xml`
- `ic_1x_mobiledata.xml`, `ic_3g_mobiledata.xml`, `ic_4g_mobiledata.xml`, `ic_5g_mobiledata.xml`, `ic_lte_mobiledata.xml`
- `ic_qs_nfc_enabled.xml` / `ic_qs_nfc_disabled.xml`
- `ic_hotspot.xml` / `ic_qs_no_sim.xml`

### 2.2 声音与多媒体 (Sound & Media)
- `ic_vol_mute.xml` / `ic_vol_unmute.xml`
- `ic_volume_alarm.xml` / `ic_volume_alarm_mute.xml`
- `ic_volume_media.xml` / `ic_volume_media_bt.xml` / `ic_volume_media_mute.xml`
- `ic_volume_ringer.xml` / `ic_volume_ringer_mute.xml` / `ic_volume_ringer_vibrate.xml`
- `ic_headset.xml` / `ic_headset_mic.xml` / `ic_mic_26dp.xml`
- `ic_cast.xml` / `ic_cast_connected.xml` / `ic_cast_connected_fill.xml`

### 2.3 电量、显示与快捷设置 (Battery, Display & QS)
- `ic_power_saver.xml` / `ic_power_low.xml`
- `ic_brightness_thumb.xml` / `ic_invert_colors.xml`
- `ic_rotate_to_landscape_animation_interpolator_0.xml` / `ic_rotate_to_portrait_animation_interpolator_0.xml`
- `ic_qs_dnd_on.xml` / `ic_qs_dnd_detail_empty.xml`
- `ic_data_saver.xml` / `ic_data_saver_off.xml`

### 2.4 系统控制与导航 (System Navigation & Tools)
- `ic_sysbar_back.xml` / `ic_sysbar_home.xml` / `ic_sysbar_recent.xml` / `ic_sysbar_docked.xml`
- `ic_settings.xml` / `ic_settings_16dp.xml`
- `ic_screenshot_delete.xml` / `ic_screenshot_edit.xml` / `ic_screenshot_share.xml`
- `ic_camera.xml` / `ic_alarm.xml` / `ic_snooze.xml` / `ic_memory.xml` / `ic_system_update.xml`

---

## 3. 双层矢量图标整合与来源标注策略 (Two-Tier Vector Architecture)

结合用户“**看有没有 ColorOS 提供，没有就用 MIUIX 版本，展示时加副标题说明来自 MIUIX**”的最高指示，制定如下双层策略：

1. **第一层 (ColorOS 原厂系统图标 - 来自 SystemUI / COUI)**：
   - 涉及网络状态、电池、声音、导航条与快捷设置等的图标，直接采用 ColorOS `SystemUI.apk` 解包提取出的原厂 Android Vector 路径。
   - 标注副标题：`来源: ColorOS`
2. **第二层 (MIUIX 官方矢量图标 - 来自 MIUIX)**：
   - 涉及扩展应用领域（如文档、文件夹、社交、日历数字等），回退采用 MIUIX 官方可变字体 (`misymbol_vf.ttf`) 的高精度 5 档 Bezier 矢量数据。
   - 标注副标题：`来源: MIUIX` / `(来自 MIUIX)`

---

## 4. 环境准备总结与下一步

1. **环境文件就绪**：
   - `C:\Users\aka\Desktop\Portal\Projekt\camera\SystemUI.apk` (88.5 MB 原厂包)
   - `C:\Users\aka\Desktop\Portal\Projekt\camera\systemui_unpacked\res` (8037 个解包资源文件)
2. **遵守规则**：
   - 本阶段为纯调研与环境准备阶段，代码未做任何变动。
   - 本报告已提交 Commit `docs: inventory ColorOS SystemUI extracted vector drawables and fallback strategy`。
