# DatePicker

DatePicker is an input component used to select a date featuring display modes, paging, localization,
ajax selection and more.

> DatePicker is designed to replace the old p:calendar component.

[See this widget in the JavaScript API Docs.](../jsdocs/classes/primefaces.widget.datepicker.html)

## Info

| Name | Value |
| --- | --- |
| Tag | datePicker
| Component Class | org.primefaces.component.datepicker.DatePicker
| Component Type | org.primefaces.component.DatePicker
| Component Family | org.primefaces.component |
| Renderer Type | org.primefaces.component.DatePickerRenderer
| Renderer Class | org.primefaces.component.DatePicker.DatePickerRenderer

## Attributes

| Name | Default | Type | Description |
| --- | --- | --- | --- |
| id | null | String | Unique identifier of the component
| rendered | true | Boolean | Boolean value to specify the rendering of the component.
| binding | null | Object | An el expression that maps to a server side UIComponent instance in a backing bean
| value | null | java.time.LocalDate, java.time.LocalDateTime, java.time.LocalTime, java.time.YearMonth, java.util.Date (deprecated) | Value of the component
| accesskey | null | String | Access key that when pressed transfers focus to the input element.
| alt | null | String | Alternate textual description of the input field.
| appendTo | @(body) | String | Appends the dialog to the element defined by the given search expression.
| autocomplete | null | String | Controls browser autocomplete behavior.
| beforeShow | null | String | Callback to execute before displaying DatePicker, element and DatePicker instance are passed as parameters
| buttonTabindex | null | String | Tabindex of the datepicker button
| converter | null | Converter/String | An el expression or a literal text that defines a converter for the component. When it’s an EL expression, it’s resolved to a converter instance. In case it’s a static text, it must refer to a converter id
| converterMessage | null | String | Message to be displayed when conversion fails.
| dateTemplate | null | Function | Javascript function that takes a date object and returns the content for the date cell.
| dir | null | String | Direction indication for text that does not inherit directionality. Valid values are LTR and RTL.
| disabled | false | Boolean | Disables input field
| disabledDates | null | List<java.time.LocalDate>, List<java.util.Date> (deprecated) | List of dates that should be disabled.
| disabledDays | null | List<Integer> | List of week day indexes that should be disabled.
| focusOnSelect | false | Boolean | When enabled, input receives focus after a value is picked.
| hideOnDateTimeSelect | false | Boolean | Defines if the popup should be hidden when a time is selected.
| hourFormat | '24' | String | Defines the hour format, valid values are '12' and '24'
| immediate | false | Boolean | When set true, process validations logic is executed at apply request values phase for this component.
| inline |  false | Boolean | Whether to show the datepicker inline or as a popup
| inputmode | null | String | Hint at the type of data this control has for touch devices to display appropriate virtual keyboard.
| inputStyle | null | String | Inline style of the input element. Used when mode is popup.
| inputStyleClass | null | String | Style class of the input element. Used when mode is popup.
| keepInvalid | false | Boolean | Whether to keep the invalid inputs in the field or not.
| label | null | String | A localized user presentable name.
| lang | null | String | Code describing the language used in the generated markup for this component.
| locale | null | Object | Locale to be used for labels and conversion.
| mask | null | String | Defines if a mask should be applied to the input field. Default value is "false" and valid values to enable are "true" that uses the pattern as the mask or a custom template. Refer to inputMask component for more information about custom templates..
| maskAutoClear | true | Boolean | Clears the field on blur when incomplete input is entered
| maskSlotChar | '_' | String | Placeholder in mask template.  Default to `_`.
| maxDateCount | null | Integer | Defines the maximum number of selectable dates in multiple selection mode.
| maxdate | null | java.time.LocalDate, java.time.LocalDateTime, java.time.LocalTime, java.util.Date (deprecated) or String | Sets DatePicker's maximum selectable value; Also used for validation on the server-side.
| maxlength | null | Integer | Maximum number of characters that may be entered in this field.
| mindate | null | java.time.LocalDate, java.time.LocalDateTime, java.time.LocalTime, java.util.Date (deprecated) or String | Sets DatePicker's minimum selectable value; Also used for validation on the server-side.
| monthNavigator | false | Boolean | Whether to show the month navigator
| model | null | org.primefaces.model.datepicker.DateMetadataModel | Model with meta data for certain dates, like `disabled` and `styleClass`
| numberOfMonths | 1 | Integer | Number of months to display concurrently.
| onMonthChange | null | Function | Javascript function to invoke when month changes.
| onYearChange | null | Function | Javascript function to invoke when year changes.
| onblur | null | String | Client side callback to execute when input element loses focus.
| onchange | null | String | Client side callback to execute when input element loses focus and its value has been modified since gaining focus.
| oninput | null | String | Client side callback to execute when an element gets user input.
| onclick | null | String | Client side callback to execute when input element is clicked.
| ondblclick | null | String | Client side callback to execute when input element is double clicked.
| onfocus | null | String | Client side callback to execute on input element focus.
| onkeydown | null | String | Client side callback to execute when a key is pressed down over input element.
| onkeypress | null | String | Client side callback to execute when a key is pressed and released over input element.
| onkeyup | null | String | Client side callback to execute when a key is released over input element.
| onmousedown | null | String | Client side callback to execute when a pointer button is pressed down over input element
| onmousemove | null | String | Client side callback to execute when a pointer button is moved within input element.
| onmouseout | null | String | Client side callback to execute when a pointer button is moved away from input element.
| onmouseover | null | String | Client side callback to execute when a pointer button is moved onto input element.
| onmouseup | null | String | Client side callback to execute when a pointer button is released over input element.
| onwheel | null | String | Client side callback to execute when the mouse wheel rolls up or down over an element.
| onselect | null | String | Client side callback to execute when text within input element is selected by user.
| oncut | null | String | Client side callback to execute when the user copies the content of an element.
| oncopy | null | String | Client side callback to execute when the user cuts the content of an element.
| onpaste | null | String | Client side callback to execute when the user pastes some content in an element.
| oncontextmenu | null | String | Client side callback to execute when a context menu is triggered.
| oninvalid | null | String | Client side callback to execute when an element is invalid.
| onreset | null | String | Client side callback to execute when the Reset button in a form is clicked.
| onsearch | null | String | Client side callback to execute when the user writes something in a search field.
| ondrag | null | String | Client side callback to execute when an element is dragged.
| ondragend | null | String | Client side callback to execute at the end of a drag operation.
| ondragenter | null | String | Client side callback to execute when an element has been dragged to a valid drop target.
| ondragleave | null | String | Client side callback to execute when an element leaves a valid drop target.
| ondragover | null | String | Client side callback to execute when an element is being dragged over a valid drop target.
| ondragstart | null | String | Client side callback to execute at the start of a drag operation.
| ondrop | null | String | Client side callback to execute when dragged element is being dropped.
| onscroll | null | String | Client side callback to execute when an element's scrollbar is being scrolled.e input value.
| panelStyle | null | String | Inline style of the container element.
| panelStyleClass | null | String | Style class of the container element.
| pattern | MM/dd/yy | String | DateFormat pattern for localization (for the date part only)
| placeholder | null | String | Specifies a short hint.
| placeholder | null | String | Specifies a short hint.
| rangeSeparator | - | String | Separator for joining start and end dates on range selection mode.
| readonly | false | Boolean | Flag indicating that this component will prevent changes by the user.
| readonlyInput | false | Boolean | Makes input text of a popup DatePicker readonly.
| required | false | Boolean | Marks component as required
| requiredMessage | null | String | Message to be displayed when required field validation fails.
| resolverStyle | smart | String | Relevant when parsing to a Java 8 Date/Time object. lenient, smart or strict. See [ResolverStyle](https://docs.oracle.com/javase/8/docs/api/java/time/format/ResolverStyle.html).
| selectOtherMonths | false | Boolean | Enables selection of days belonging to other months.
| selectionMode | single | String | Defines the selection mode, valid values are "single", "multiple" and "range"
| shortYearCutoff | +10 | String | The cutoff year for determining the century for a date. Any dates entered with a year value less than or equal to the cutoff year are considered to be in the current century, while those greater than it are deemed to be in the previous century.
| showButtonBar | false | Boolean | Whether to display buttons at the footer.
| showIcon | false | String | Whether to show an icon to display the picker in an overlay
| showOnFocus | true | Boolean | Whether to show the popup when input receives focus.
| showOtherMonths | false | Boolean | Displays days belonging to other months.
| showSeconds | false | Boolean | Whether to show the seconds in time picker. Default is false.
| showTime | false * | Boolean | Specifies if the timepicker should be displayed.  (* Defaults to true, when value is bound to java.time.LocalDateTime)
| showWeek | false | Boolean | Displays the week number next to each week.
| size | null | Integer | Number of characters used to determine the width of the input element.
| stepHour | 1 | Integer | Hour steps.
| stepMinute | 1 | Integer | Minute steps.
| stepSecond | 1 | Integer | Second steps.
| style | null | String | Inline style of the input element.
| styleClass | null | String | Style class of the input element.
| tabindex | null | Integer | Position of the input element in the tabbing order.
| timeInput | false | Boolean | Allows direct input in time field.
| timeOnly | false * | Boolean | Shows only timepicker without date. (* Defaults to true, when value is bound to java.time.LocalTime)
| timeZone | null | Time Zone | String a java.time.ZoneId instance or a java.util.TimeZone instance to specify the timezone used for date conversion, defaults to ZoneId.systemDefault(). (This attribute is only relevant for java.util.Date in combination with the built-in converter.)
| title | null | String | Advisory tooltip information.
| touchUI | false | Boolean | Activates touch friendly mode
| touchable | true | Boolean | Enable touch support if browser detection supports it.
| triggerButtonIcon | null | String | Icon of the datepicker element that toggles the visibility in popup mode.
| type | text | String | Type of the input field
| validator | null | Method Expr | A method expression that refers to a method validationg the input
| validatorMessage | null | String | Message to be displayed when validation fails.
| valueChangeListener | null | Method Expr | A method expression that refers to a method for handling a valuchangeevent
| view | date | String | Defines the view mode, valid values are "date" for datepicker and "month" for month picker.
| weekCalculator | false | Boolean | A javascript function that is used to calculate the week number. Uses internal implementation on default when start of week is monday, sunday or saturday.
| widgetVar | null | String | Name of the client side widget.
| yearNavigator | false | Boolean | Whether to show the year navigator
| yearRange | null | String | The range of years displayed in the year drop-down in (nnnn:nnnn) format such as (2000:2020). Default value is "displayed_date - 10 : displayed_date + 10".

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
attribute. Locale attribute can take a locale key as a | String | or a java.util.Locale instance. Default
language of labels are English and you need to add the necessary translations to your page manually
as PrimeFaces does not include language translations. PrimeFaces Wiki Page for
PrimeFacesLocales is a community driven page where you may find the translations you need.
Please contribute to this wiki with your own translations.

```html
https://github.com/primefaces/primefaces/wiki/Locales
```
Translation is a simple javascript object, we suggest adding the code to a javascript file and include
in your application. Following is a Turkish DatePicker.

```xhtml
<h:outputScript name="path_to_your_translations.js" />
<p:datePicker value="#{dateController.date}" locale="tr" />
```

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

## Ajax Behavior Events
The following AJAX behavior events are available for this component. If no event is specified the default event is called.

**Default Event:** `valueChange`
**Available Events:** `blur, change, click, close, contextmenu, copy, cut, dateSelect, dblclick, drag, dragend, dragenter, dragleave, dragover, dragstart, drop, focus, input, invalid, keydown, keypress, keyup, mousedown, mousemove, mouseout, mouseover, mouseup, paste, reset, scroll, search, select, valueChange, viewChange, wheel`

**Custom Events:**

| Event | Listener Parameter | Fired |
| --- | --- | --- |
| close | - | When the popup is closed.
| dateSelect | org.primefaces.event.SelectEvent | When a date is selected.
| viewChange | org.primefaces.event.DateViewChangeEvent | When the date picker changed to a different month or year page.

DatePicker provides a _dateSelect_ ajax behavior event to execute an instant ajax selection whenever a
date is selected. If you define a method as a listener, it will be invoked by passing an
_org.primefaces.event.SelectEvent_ instance.

```xhtml
<p:datePicker value="#{DatePickerBean.date}">
    <p:ajax event="dateSelect" listener="#{bean.handleDateSelect}" update="msg" />
</p:datePicker>
<p:messages id="msg" />
```
```java
public void handleDateSelect(SelectEvent<LocalDate> event) {
    LocalDate date = event.getObject();
    //Add facesmessage
}
```
In popup mode, DatePicker also supports regular ajax behavior events like blur, keyup and more.

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
Widget: _PrimeFaces.widget.Calendar_

| Method | Params | Return Type | Description |
| --- | --- | --- | --- |
| getDate() | - | Date | Return selected date.
| setDate(date) | date: Date to display | void | Sets display date.
| setDisabledDates(dates) | dates: Array of dates to disable | void | Sets disabled dates and update panel. Accepts array of date objects and strings formatted as M/d/yyyy.
| setDisabledDays(days) | days: Array of days to disable | void | Sets disabled days and update panel. Accepts array of numbers, Sunday = 0.
| updatePanel() | - | void | Update panel.

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
| .ui-datepicker-table | Date table
| .ui-datepicker-week-end | Label of weekends
| .ui-datepicker-other-month | Dates belonging to other months
| .ui-datepicker td | Each cell date
| .ui-datepicker-buttonpane | Button panel
| .ui-datepicker-current | Today button
| .ui-datepicker-close | Close button

As skinning style classes are global, see the main theming section for more information.

