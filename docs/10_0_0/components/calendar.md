# Calendar

Calendar is an input component used to select a date featuring display modes, paging, localization,
ajax selection and more.

[See this widget in the JavaScript API Docs.](../jsdocs/classes/src_primefaces.primefaces.widget.calendar-1.html)

## Info

| Name | Value |
| --- | --- |
| Tag | calendar
| Component Class | org.primefaces.component.calendar.Calendar
| Component Type | org.primefaces.component.Calendar
| Component Family | org.primefaces.component |
| Renderer Type | org.primefaces.component.CalendarRenderer
| Renderer Class | org.primefaces.component.calendar.CalendarRenderer

## Attributes

| Name | Default | Type | Description |
| --- | --- | --- | --- |
| id | null | String | Unique identifier of the component
| rendered | true | Boolean | Boolean value to specify the rendering of the component.
| binding | null | Object | An el expression that maps to a server side UIComponent instance in a backing bean
| value | null | java.time.LocalDate, java.time.LocalDateTime, java.time.LocalTime, java.util.Date (deprecated) | Value of the component
| accesskey | null | String | Access key that when pressed transfers focus to the input element.
| alt | null | String | Alternate textual description of the input field.
| autocomplete | null | String | Controls browser autocomplete behavior.
| beforeShow | null | String | Callback to execute before displaying calendar, element and calendar instance are passed as parameters
| beforeShowDay | null | String | Client side callback to execute before displaying a date, used to customize date display.
| buttonTabindex | null | String | Position of the button in the tabbing order.
| converter | null | Converter/String | An el expression or a literal text that defines a converter for the component. When it’s an EL expression, it’s resolved to a converter instance. In case it’s a static text, it must refer to a converter id
| converterMessage | null | String | Message to be displayed when conversion fails.
| defaultHour | 0 | Integer | Default for hour selection, if no date is given. Default is 0.
| defaultMillisec | 0 | Integer | Default for millisecond selection, if no date is given. Default is 0.
| defaultMinute | 0 | Integer | Default for minute selection, if no date is given. Default is 0.
| defaultSecond | 0 | Integer | Default for second selection, if no date is given. Default is 0.
| dir | null | String | Direction indication for text that does not inherit directionality. Valid values are LTR and RTL.
| disabled | false | Boolean | Disables the calendar when set to true.
| disabledWeekends | false | Boolean | Disables weekend columns.
| effect | null | String | Effect to use when displaying and showing the popup calendar.
| effectDuration | normal | String | Duration of the effect.
| focusOnSelect | false | Boolean | If enabled, the input is focused again after selecting a date. Default is false.
| immediate | false | Boolean | When set true, process validations logic is executed at apply request values phase for this component.
| inputmode | null | String | Hint at the type of data this control has for touch devices to display appropriate virtual keyboard.
| inputStyle | null | String | Inline style of the input element. Used when mode is popup.
| inputStyleClass | null | String | Style class of the input element. Used when mode is popup.
| label | null | String | A localized user presentable name.
| lang | null | String | Code describing the language used in the generated markup for this component.
| locale | null | Object | Locale to be used for labels and conversion.
| mask | null | String | Defines if a mask should be applied to the input field. Default value is "false" and valid values to enable are "true" that uses the pattern as the mask or a custom template. Refer to inputMask component for more information about custom templates..
| maskAutoClear | true | Boolean | Clears the field on blur when incomplete input is entered
| maskSlotChar | '_' | String | Placeholder in mask template.  Default to `_`.
| maxdate | null | java.time.LocalDate, java.util.Date (deprecated) or String | Sets calendar's maximum visible date; Also used for validation on the server-side.
| maxHour | 23 | Integer | Maximum boundary for hour selection.
| maxlength | null | Integer | Maximum number of characters that may be entered in this field.
| maxMinute | 59 | Integer | Maximum boundary for hour selection.
| maxSecond | 59 | Integer | Maximum boundary for second selection.
| mindate | null | java.time.LocalDate, java.util.Date (deprecated) or String | Sets calendar's minimum visible date; Also used for validation on the server-side.
| minHour | 0 | Integer | Minimum boundary for hour selection.
| minMinute | 0 | Integer | Minimum boundary for minute selection.
| minSecond | 0 | Integer | Minimum boundary for second selection.
| mode | popup | String | Defines how the calendar will be displayed.
| navigator | false | Boolean | Enables month/year navigator
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
| pagedate | null | Object | Initial date to display if value is null.
| pages | 1 | Integer | Enables multiple page rendering.
| pattern | MM/dd/yyyy | String | DateFormat pattern for localization
| placeholder | null | String | Specifies a short hint.
| readonly | false | Boolean | Flag indicating that this component will prevent changes by the user.
| readonly | false | Boolean | Makes the entire component readonly not allowing calendar popup or text entry.
| readonlyInput | false | Boolean | Makes input text of a popup calendar readonly.
| required | false | Boolean | Marks component as required
| requiredMessage | null | String | Message to be displayed when required field validation fails.
| resolverStyle | smart | String | Relevant when parsing to a Java 8 Date/Time object. lenient, smart or strict. See [ResolverStyle](https://docs.oracle.com/javase/8/docs/api/java/time/format/ResolverStyle.html).
| selectOtherMonths | false | Boolean | Enables selection of days belonging to other months.
| showButtonPanel | false | Boolean | Visibility of button panel containing today and done buttons.
| showHour | null | String | Whether to show the hour control.
| showMillisec | null | String | Whether to show the millisec control
| showMinute | null | String | Whether to show the minute control.
| showOn | both | String | Client side event that displays the popup calendar.
| showOtherMonths | false | Boolean | Displays days belonging to other months.
| showSecond | null | String | Whether to show the second control.
| showTodayButton | true | Boolean | Whether to show the "Current Date" button if showButtonPanel is rendered.
| showWeek | false | Boolean | Displays the week number next to each week.
| size | null | Integer | Number of characters used to determine the width of the input element.
| stepHour | 1 | Integer | Hour steps.
| stepMinute | 1 | Integer | Minute steps.
| stepSecond | 1 | Integer | Second steps.
| style | null | String | Inline style of the component.
| styleClass | null | String | Style class of the component.
| tabindex | null | Integer | Position of the input element in the tabbing order.
| timeControlObject | null | String | Client side object to use in custom timeControlType.
| timeControlType | slider | String | Defines the type of element to use for time picker, valid values are "slider" and "select".
| timeInput | false | Boolean | Allows direct input in time field.
| timeOnly | false | Boolean | Shows only timepicker without date.
| timeZone | null | Time Zone | String or a java.time.ZoneId instance or a java.util.TimeZone instance to specify the timezone used for date conversion, defaults to ZoneId.systemDefault(). (This attribute is only relevant for java.util.Date in combination with the built-in converter.)
| title | null | String | Advisory tooltip information.
| touchable | true | Boolean | Enable touch support if browser detection supports it.
| type | text | String | Input field type. Default is text.
| validator | null | Method Expr | A method expression that refers to a method validationg the input
| validatorMessage | null | String | Message to be displayed when validation fails.
| valueChangeListener | null | Method Expr | A method expression that refers to a method for handling a valuchangeevent
| widgetVar | null | String | Name of the client side widget.
| yearRange | null | String | Year range for the navigator, default "c-10:c+10"

## Getting Started with Calendar
Value of the calendar should be a java.time.LocalDate.

```xhtml
<p:calendar value="#{dateBean.date}"/>
```
```java
public class DateBean {
    private LocalDate date;
    //Getter and Setter
}
```
## Display Modes
Calendar has two main display modes, _popup_ (default) and _inline_.

#### Inline

```xhtml
<p:calendar value="#{dateBean.date}" mode="inline" />
```

#### Popup

```xhtml
<p:calendar value="#{dateBean.date}" mode="popup" />
```
_showOn_ option defines the client side event to display the calendar. Valid values are;

- **focus**: When input field receives focus
- **button**: When popup button is clicked
- **both**: Both _focus_ and _button_ cases

#### Popup Button

```xhtml
<p:calendar value="#{dateBean.date}" mode="popup" showOn="button" />
```
#### Popup Icon Only

```xhtml
<p:calendar value="#{dateBean.date}" mode="popup"
    showOn="button" popupIconOnly="true" />
```

## Paging
Calendar can also be rendered in multiple pages where each page corresponds to one month. This
feature is tuned with the _pages_ attribute.

```xhtml
<p:calendar value="#{dateController.date}" pages="3"/>
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
in your application. Following is a Turkish calendar.

```xhtml
<h:outputScript name=”path_to_your_translations.js” />
<p:calendar value="#{dateController.date}" locale="tr" navigator="true" showButtonPanel="true"/>
```

To override calculated pattern from locale, use the pattern option;

```xhtml
<p:calendar value="#{dateController.date1}" pattern="dd.MM.yyyy"/>
<p:calendar value="#{dateController.date2}" pattern="yy, M, d"/>
<p:calendar value="#{dateController.date3}" pattern="EEE, dd MMM, yyyy"/>
```
## Effects
Various effects can be used when showing and hiding the popup calendar, options are; show,
slideDown, fadeIn, blind, bounce, clip, drop, fold and slide.

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

Calendar provides a _dateSelect_ ajax behavior event to execute an instant ajax selection whenever a
date is selected. If you define a method as a listener, it will be invoked by passing an
_org.primefaces.event.SelectEvent_ instance.

```xhtml
<p:calendar value="#{calendarBean.date}">
    <p:ajax event=”dateSelect” listener=”#{bean.handleDateSelect}” update=”msg” />
</p:calendar>
<p:messages id="msg" />
```
```java
public void handleDateSelect(SelectEvent<LocalDate> event) {
    LocalDate date = event.getObject();
    //Add facesmessage
}
```
In popup mode, calendar also supports regular ajax behavior events like blur, keyup and more.

Another handy event is the _viewChange_ that is fired when month and year changes. An instance of
_org.primefaces.event.DateViewChangeEvent_ is passed to the event listener providing the current
month and year information.

In case, you need to know about when a calendar gets hidden, use _close_ event.

## Date Ranges
Using mindate and maxdate options, selectable dates can be restricted. Values for these attributes
can either be a string or a java.time.LocalDate.

```xhtml
<p:calendar value="#{dateBean.date}" mode="inline" mindate="07/10/2010" maxdate="07/15/2010"/>
```

## Navigator
Navigator is an easy way to jump between months/years quickly.

```xhtml
<p:calendar value="#{dateBean.date}" mode="inline" navigator="true" />
```
## TimePicker
TimePicker functionality is enabled by adding time format to your pattern.

```xhtml
<p:calendar value="#{dateBean.date}" pattern=”MM/dd/yyyy HH:mm” />
```
## Advanced Customization
Use beforeShowDay javascript callback to customize the look of each date. The function returns an
array with two values, first one is flag to indicate if date would be displayed as enabled and second
parameter is the optional style class to add to date cell. Following example disabled tuesdays and
fridays.

```xhtml
<p:calendar value="#{dateBean.date}" beforeShowDay="tuesdaysAndFridaysOnly" />
```

```js
function tuesdaysAndFridaysDisabled(date) {
    var day = date.getDay();
    return [(day != 2 && day != 5), '']
}
```
## Mask
Calendar has a built-in mask feature similar to the InputMask component. Set _mask_ option to true to
enable mask support.

## Client Side API
Widget: _PrimeFaces.widget.Calendar_

| Method | Params | Return Type | Description |
| --- | --- | --- | --- |
| getDate() | - | Date | Return selected date
| setDate(date) | date: Date to display | void | Sets display date
| disable() | - | void | Disables calendar
| enable() | - | void | Enables calendar

## Skinning
Calendar resides in a container element which _style_ and _styleClass_ options apply.

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

