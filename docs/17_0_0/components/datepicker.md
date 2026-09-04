# DatePicker

DatePicker is an input component used to select a date featuring display modes, paging, localization,
ajax selection and more.

> DatePicker is designed to replace the old p:calendar component.

## Getting Started with DatePicker
Value of the DatePicker should be a java.time.LocalDate in single selection mode which is the default.

```xhtml
<p:datePicker value="#{dateBean.date}"/>
```
```java
public class DateBean {
    private LocalDate date;
    //Getter and Setter
}
```
## Display Modes
DatePicker has two main display modes, _popup_ (default) and _inline_.

#### Inline

```xhtml
<p:datePicker value="#{dateBean.date}" inline="true" />
```

#### Popup

```xhtml
<p:datePicker value="#{dateBean.date}"  />
```

## Paging
DatePicker can also be rendered in multiple pages where each page corresponds to one month. This
feature is tuned with the _numberOfMonths_ attribute.

```xhtml
<p:datePicker value="#{dateController.date}" numberOfMonths="3"/>
```

## Localization
By default locale information is retrieved from the view’s locale and can be overridden by the locale
attribute. Locale attribute can take a locale key as a `String` or a `java.util.Locale` instance. Default
language of labels are English, for other (bundled) languages and localization options have a look at the
[localization documentation](/core/localization.md?id=client-localization).

To override calculated pattern from locale, use the pattern option;

```xhtml
<p:datePicker value="#{dateController.date1}" pattern="dd.MM.yyyy"/>
<p:datePicker value="#{dateController.date2}" pattern="yy, M, d"/>
<p:datePicker value="#{dateController.date3}" pattern="EEE, dd MMM, yyyy"/>
```

## Multiple and Range Selection
Multiple dates or a range of dates can be selected by setting the _selectionMode_ property. In both cases, the bound value should be a List reference.

```xhtml
<p:datePicker value="#{dateBean.dates}" selectionMode="multiple" />
```

!> The `mask` property cannot be used together with multiple or range selection modes.

## Date Restriction
Using mindate and maxdate options, selectable values can be restricted. Values for these attributes
can either be a String, a java.time.LocalDate, a java.time.LocalDateTime, a java.time.LocalTime or a java.util.Date (deprecated).

In case you'd like to restrict certain dates or weekdays use _disabledDates_ and _disableDays_ options. Here is an example that demonstrates all restriction options.

```xhtml
<p:datePicker value="#{dateBean.date}" mode="inline" disabledDays="#{calendarView.invalidDays}" disabledDates="#{calendarView.invalidDates}" mindate="#{calendarView.minDate}" maxDate="#{calendarView.minDate}" />
```

```java
private List<LocalDate> invalidDates;
private List<Integer> invalidDays;
private LocalDate minDate;
private LocalDate maxDate;

@PostConstruct
public void init() {
    invalidDates = new ArrayList<>();
    invalidDates.add(LocalDate.now());
    for (int i = 0; i < 5; i++) {
        invalidDates.add(invalidDates.get(i).plusDays(1));
    }

    invalidDays = new ArrayList<>();
    invalidDays.add(0); /* the first day of week is disabled */
    invalidDays.add(3);

    minDate = LocalDate.now().minusYears(1);
    maxDate = LocalDate.now().plusYears(1);
}
```

## Date metadata model

You can use the `model` attribute to set metadata per date in the calendar. Metadata currently contains `disabled` and `styleClass`.

Setting disabled dates is already possible using the corresponding attribute, I hear you think. But here comes the interesting part:

### Lazy date metadata model

This can be used to set the metadata when the calendar view changes. For example:

```xhtml
<p:datePicker value="#{dateBean.date}" model="#{dateBean.lazyModel}" />
```

```java
private LocalDate date;
private DateMetadataModel lazyModel;

@PostConstruct
public void init() {
    DefaultDateMetadata metadataDisabled = DefaultDateMetadata.builder().disabled(true).build();
    DefaultDateMetadata metadataDeadline = DefaultDateMetadata.builder().styleClass("deadline").build();
    lazyModel = new LazyDateMetadataModel() {
        @Override
        public void loadDateMetadata(LocalDate start, LocalDate end) {
            add(someDate, metadataDisabled);
            add(someOtherDate, metadataDeadline);
        }
    };
}
```

## Navigator
Navigator is an easy way to jump between months/years quickly.

```xhtml
<p:datePicker value="#{dateBean.date}" monthNavigator="true" yearNavigator="true" />
```

## TimePicker
TimePicker functionality is enabled by _showTime_ property.
_showTime_ defaults to _true_, when value is bound to _java.time.LocalDateTime_.

```xhtml
<p:datePicker value="#{dateBean.dateTime}" showTime="true" />
```

To show the TimePicker only, use the _timeOnly_ attribute.
_timeOnly_ defaults to _true_, when value is bound to _java.time.LocalTime_.

```xhtml
<p:datePicker value="#{dateBean.time}" timeOnly="true" />
```

The TimePicker pattern can be modified with the properties _hourFormat_ and _showSeconds_.

```xhtml
<p:datePicker value="#{dateBean.time}" timeOnly="true" hourFormat="12" showSeconds="true" />
```

## Client Side API
Widget: _PrimeFaces.widget.DatePicker_

| Method | Params | Return Type | Description |
| --- | --- | --- | --- |
| getDate() | - | Date | Return selected date.
| setDate(date) | date: Date to display | void | Sets display date.
| setDisabledDates(dates) | dates: Array of dates to disable | void | Sets disabled dates and update panel. Accepts array of date objects and strings formatted as M/d/yyyy.
| setDisabledDays(days) | days: Array of days to disable | void | Sets disabled days and update panel. Accepts array of numbers, Sunday = 0.
| setViewDate(date, silent) | date: Date to display, silent: Boolean ignore AJAX request | void | Sets view date to display in the panel.
| updatePanel() | - | void | Update panel.
| show() | - | void | Show the overlay panel.
| hide() | - | void | Hide the overlay panel.
| disable() | - | void | Disables DatePicker.
| enable() | - | void | Enables DatePicker.

## Skinning
DatePicker resides in a container element which _style_ and _styleClass_ options apply.

Following is the list of structural style classes;

| Class | Applies |
| --- | --- |
| .ui-datepicker | Main container
| .ui-datepicker-header | Header container
| .ui-datepicker-prev | Previous month navigator
| .ui-datepicker-next | Next month navigator
| .ui-datepicker-title | Title
| .ui-datepicker-month | Month display
| .ui-datepicker-year | Year Display
| .ui-datepicker-table | Date table
| .ui-datepicker-week-end | Label of weekends
| .ui-datepicker-today | Today on the calendar
| .ui-datepicker-other-month | Dates belonging to other months
| .ui-datepicker td | Each cell date
| .ui-datepicker-buttonbar | Button panel
| .ui-today-button | Today button
| .ui-clear-button | Close button
| .ui-datepicker-trigger | Trigger button

As skinning style classes are global, see the main theming section for more information.

