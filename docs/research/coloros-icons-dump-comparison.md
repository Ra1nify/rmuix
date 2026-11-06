# ColorOS 17 与 rmuix 图标库全量 Dump 对比与补齐清单

本文档响应用户指示，Dump 并全量比对 ColorOS 17 系统矢量图标集（`Settings.apk` / COUI 控件库）与 `rmuix`（原 `miuix`）现有内置图标库，找出缺失的图标并制定补齐清单。

> **最高指示**：“只多不少：少了自己补，多了直接放上面。”先进行全面调研对比，暂不修改代码。

---

## 1. rmuix 现有内置图标库盘点 (rmuix Icon Inventory)

`rmuix` 现有的图标分为两层：
1. **基础图标 (`rmuix-ui` / `Basic`)**：共 7 个（`ArrowRight`, `ArrowUpDown`, `Check`, `Close`, `Search`, `SearchCleanup`, `Sidebar`）。
2. **扩展图标 (`rmuix-icons` / `Extended`)**：共 150+ 个，包含 31 个日历数字图标 (`Th1` ~ `Th31`)。

### 现有扩展图标全表 (150+ 个)：
- **通用动作**：`Add`, `AddCircle`, `AddFolder`, `Answer`, `Back`, `Backup`, `Clear`, `Close`, `Copy`, `Create`, `Cut`, `Delete`, `Download`, `Edit`, `Filter`, `Forward`, `Help`, `Hide`, `Import`, `Info`, `Lock`, `Merge`, `Mic`, `MicSlash`, `MoveFile`, `Ok`, `Paste`, `Pause`, `Play`, `Redo`, `Refresh`, `Remove`, `RemoveContact`, `Rename`, `Replace`, `Reply`, `ReplyAll`, `Report`, `Reset`, `RotateLeft`, `Scan`, `SearchDevice`, `SelectAll`, `Send`, `Share`, `Show`, `Sort`, `Trim`, `Tune`, `Undo`, `Unlock`, `Unpin`, `Update`, `UploadCloud`, `ZoomOut`
- **媒体与文件**：`Album`, `Background`, `BankCards`, `ConvertFile`, `File`, `FileDownloads`, `Folder`, `FolderFill`, `GridView`, `Image`, `Layers`, `ListView`, `MapAlbum`, `Music`, `Notes`, `NotesFill`, `Photos`, `Playlist`, `Recording`, `RecordingTape`, `TopDownloads`
- **通讯与分类**：`Blocklist`, `CallRecording`, `Carrier`, `ChevronBackward`, `ChevronForward`, `CloudFill`, `Community`, `Contacts`, `ContactsBook`, `ContactsCircle`, `Email`, `ExpandLess`, `ExpandMore`, `Favorites`, `FavoritesFill`, `Home`, `HorizontalSplit`, `Location`, `Messages`, `MindMap`, `Months`, `More`, `MoreCircle`, `Phone`, `Promotions`, `Recent`, `ScreenCapture`, `ScreenMirroring`, `Settings`, `Stopwatch`, `Store`, `Tasks`, `Theme`, `Timer`, `Translate`, `VerticalSplit`, `VolumeOff`, `VolumeUp`, `Weeks`, `WorldClock`, `Years`
- **日期数字图标**：`Th1` ~ `Th31`

---

## 2. ColorOS 17 原厂 Dump 比对与缺失图标补齐清单 (Gap Analysis)

通过 Dump `Settings.apk` 与 COUI 矢量资源，对比发现 `rmuix` 在 **系统设置、通信连接、生物识别与硬件控制** 领域的图标存在较明显的缺失。根据“只多不少”的原则，需补齐以下 **24 个 ColorOS 17 核心系统图标**：

| 分类 | 缺失的 ColorOS 17 系统图标 | 原厂资源文件名 | 推荐映射命名 |
| :--- | :--- | :--- | :--- |
| **网络与连接** | WLAN 无线网络 | `ic_status_manager_wifi` | `RmuixIcons.Outline.Wifi` |
| | WLAN 断开/无信号 | `ic_status_manager_wifi_off` | `RmuixIcons.Outline.WifiOff` |
| | 蓝牙 | `ic_status_manager_bluetooth` | `RmuixIcons.Outline.Bluetooth` |
| | 蜂窝数据 / 移动网络 | `ic_status_manager_mobiledata` | `RmuixIcons.Outline.Cellular` |
| | 个人热点 | `ic_status_manager_hotspot` | `RmuixIcons.Outline.Hotspot` |
| | 飞行模式 | `ic_status_manager_airplane` | `RmuixIcons.Outline.AirplaneMode` |
| | NFC | `ic_status_manager_nfc` | `RmuixIcons.Outline.Nfc` |
| **显示与屏幕** | 屏幕亮度 | `ic_settings_brightness` | `RmuixIcons.Outline.Brightness` |
| | 自动亮度 | `ic_settings_auto_brightness` | `RmuixIcons.Outline.AutoBrightness` |
| | 暗色模式 | `ic_settings_dark_mode` | `RmuixIcons.Outline.DarkMode` |
| | 护眼模式 / 护眼屏 | `ic_settings_eye_protect` | `RmuixIcons.Outline.EyeProtection` |
| | 屏幕自动旋转 | `ic_settings_auto_rotate` | `RmuixIcons.Outline.AutoRotate` |
| **声音与电量** | 电池 | `ic_status_manager_battery` | `RmuixIcons.Outline.Battery` |
| | 充电中 | `ic_status_manager_battery_charging` | `RmuixIcons.Outline.BatteryCharging` |
| | 省电模式 | `ic_status_manager_battery_saver` | `RmuixIcons.Outline.BatterySaver` |
| | 响铃 / 振动 | `ic_status_manager_vibrate` | `RmuixIcons.Outline.Vibrate` |
| | 静音 / 禁音 | `ic_status_manager_silent` | `RmuixIcons.Outline.Silent` |
| | 免打扰 | `ic_status_manager_zen` | `RmuixIcons.Outline.DoNotDisturb` |
| | 耳机 | `ic_status_manager_headset` | `RmuixIcons.Outline.Headphones` |
| **安全与隐私** | 指纹解锁 | `ic_settings_fingerprint` | `RmuixIcons.Outline.Fingerprint` |
| | 面部识别 | `ic_settings_face_unlock` | `RmuixIcons.Outline.FaceUnlock` |
| | 隐私保护 | `ic_settings_privacy` | `RmuixIcons.Outline.Privacy` |
| | 安全守护 / 盾牌 | `ic_settings_security_shield` | `RmuixIcons.Outline.ShieldCheck` |
| **系统与存储** | 系统存储与内存 | `ic_settings_storage` | `RmuixIcons.Outline.Storage` |

---

## 3. 图标补齐与 5 档字重 + Outline/Filled 整合计划

对于补齐的 24 个图标及既有的 150+ 个图标，重构时将统一下发至新的 **`Outline`** 与 **`Filled`** 命名空间，并为每个图标构建 **5 档字重（Light, Normal, Regular, Medium, Demibold）** 变体：

```
RmuixIcons
├── Outline (空心线型图标, 默认 Regular)
│   ├── Light / Normal / Regular / Medium / Demibold
│   ├── Wifi, Bluetooth, Cellular, Hotspot, AirplaneMode, Nfc
│   ├── Brightness, DarkMode, EyeProtection, AutoRotate
│   ├── Battery, BatteryCharging, Vibrate, Silent, Headphones
│   ├── Fingerprint, FaceUnlock, Privacy, ShieldCheck, Storage
│   └── (包含既有 150+ 线性图标)
└── Filled (实心面型图标, 默认 Regular)
    ├── Light / Normal / Regular / Medium / Demibold
    ├── Wifi, Bluetooth, Cellular, Battery, Home, Folder...
    └── (包含既有 FolderFill, NotesFill, FavoritesFill 等转化的实心图标)
```

---

## 4. 总结与后续指导

1. 盘点了 `rmuix` 既有 150+ 图标，Dump 对比了 ColorOS 17 `Settings.apk` / COUI 系统图标库。
2. 筛选补齐了 **24 个缺失的 ColorOS 核心系统图标**（网络、电池、声音、显示、指纹隐私等），确保图标库“只多不少”。
3. 确定了新架构下 24 个补齐图标与既有图标的 `Outline` / `Filled` 及 5 档字重映射命名。
