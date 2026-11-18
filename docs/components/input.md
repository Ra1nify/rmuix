# Input & Forms

Text, numeric, date/time and secure input. Pick by the data the form collects.

## Selection guide

| Scenario | Recommended |
| :--- | :--- |
| Free text | TextField |
| Search | SearchBar |
| Verification code | CodeInput |
| Card-style single field | SingleInputCard |
| Number / quantity | NumberPicker / Stepper |
| Date | DatePicker / CalendarPicker |
| Time | TimePicker / TimeLimitPicker |
| PIN / pattern | SimpleLock / NumericKeyboard / LockPatternView |

## Components

| Component | Description | Common usage |
| :--- | :--- | :--- |
| [TextField](./input/textfield) | Receives user text input | Form filling, search boxes |
| [SearchBar](./input/searchbar) | Search input field | Content search, quick find |
| [CodeInput](./input/codeinput) | Boxed one-time-code input with a transparent field | Verification codes, PINs |
| [SingleInputCard](./input/singleinputcard) | Card single-line input with count/delete/error | Email binding, single inputs |
| [NumberPicker](./input/numberpicker) | Vertical scroll picker for number selection | Time picker, quantity selection |
| [DatePicker](./input/datepicker) | Wheel date picker (year / month / day) | Date settings, scheduling |
| [TimePicker](./input/timepicker) | Wheel time picker, 12- or 24-hour | Alarms, schedules |
| [TimeLimitPicker](./input/timelimitpicker) | Wheel time picker with `小时` / `分钟` unit labels | Schedules, 定时 screens |
| [CalendarPicker](./input/calendarpicker) | Month-grid calendar with today/selection states | Date selection, capture time |
| [Stepper](./input/stepper) | Numeric stepper with +/- buttons | Quantity, level, step adjust |
| [SimpleLock](./input/simplelock) | PIN dots: 4/6 circles filled as the code is typed | PIN entry, security flows |
| [NumericKeyboard](./input/numerickeyboard) | 3 × 4 PIN keypad with delete/finish side keys | PIN/password entry |
| [LockPatternView](./input/lockpatternview) | 3 × 3 pattern lock with success/error states | Pattern unlock, privacy |

## Composition

```kotlin
TextField(value = text, onValueChange = { text = it })
NumericKeyboard(onNumber = { digit -> }, onDelete = {})
```

## Related categories

[Containers & Layout](./containers) · [Text & Icons](./text-and-icons) · [Buttons & Toolbars](./buttons) · [Selection & Adjustment](./selection) · [Navigation](./navigation) · [Feedback & Status](./feedback) · [Overlays & Menus](./overlays) · [Preferences](./preferences) · [Media & Effects](./media)

Package indexes: [Basic components](./basic/) · [Extended components](./extended/) · [Extend components](./extend/) · [Glass components](./glass/)
