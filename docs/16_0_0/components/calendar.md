# Calendar

Calendar is an input component used to select a date featuring display modes, paging, localization,
ajax selection and more.

[See this widget in the JavaScript API Docs.](../jsdocs/classes/src_PrimeFaces.PrimeFaces.widget.Calendar-1.html)

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
<h:outputScript name="path_to_your_translations.js" />
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
<p:calendar value="#{dateBean.date}" pattern="MM/dd/yyyy HH:mm" />
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

