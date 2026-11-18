# 最佳实践

- 优先使用组件自带的 `XxxDefaults` 常量，不要写死数值；每个常量都能在 `docs/dev/` 里找到
  官方 token 依据，或明确标注为库内选择。
- 每个 `Scaffold` 页面只挂一个 `RmuixPopupHost`；`OverlayDialog`、`OverlayBottomSheet`、
  `OverlayListPopup` 等浮层依赖它，而 `WindowDialog` 一类窗口级浮层不需要。
- 应用根部包一次 `RmuixTheme`；颜色统一走 `RmuixTheme.colorScheme`，文字样式统一走
  `RmuixTheme.textStyles`。
- 改动组件后跑一遍 `example/` 里对应的演示页：库内检查都是静态的，无法验证渲染、手势与时序。
