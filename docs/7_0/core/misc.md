# Misc

## Javascript API

PrimeFaces renders unobstrusive javascript which cleanly separates behavior from the html. Client side engine is powered by jQuery.

### PrimeFaces Namespace

_PrimeFaces_ is the main javascript object providing utilities and namespace.

| Method | Description |
| --- | --- |
escapeClientId(id) | Escaped JSF ids with semi colon to work with jQuery.
addSubmitParam(el, name, param) | Adds request parameters dynamically to the element.
getCookie(name) | Returns cookie with given name.
setCookie(name, value, cfg) | Sets a cookie with given name, value and options. e.g. PrimeFaces.setCookie('name', 'test'); PrimeFaces.setCookie('name','test',{expires:7, path:'/'}) Second example creates cookie for entire site that expires in 7 days.
deleteCookie(name, cfg) | Deletes a cookie with given and and options.
skinInput(input) | Progressively enhances an input element with theming.
info(msg), debug(msg), warn(msg), error(msg) | Client side log API.
changeTheme(theme) | Changes theme on the fly with no page refresh.
cleanWatermarks() | Watermark component extension, cleans all watermarks on page before submitting the form.
showWatermarks() | Shows watermarks on form.
getWidgetById(clientid) | Returns the widget instance from the client id

To be compatible with other javascript entities on a page, PrimeFaces defines two javascript
namespaces;

**PrimeFaces.widget.**

Contains custom PrimeFaces widgets like;

- PrimeFaces.widget.DataTable
- PrimeFaces.widget.Tree
- PrimeFaces.widget.Poll
- and more...

Most of the components have a corresponding client side widget with same name.


## PrimeFaces.current()

PrimeFaces.current() is a simple utility that provides useful goodies such as adding parameters to
ajax callback functions. It's available in both ajax and non-ajax requests. Scope
of the instance is thread local.

Instance can be obtained similarly to the FacesContext or CDI.

```java
PrimeFaces instance = PrimeFaces.current();
```
**API**

| Method | Description |
| --- | --- |
executeScript(String script) | Executes script after ajax request completes or on page load.
isAjaxRequest() | Returns a boolean value if current request is a PrimeFaces ajax request.
scrollTo(String clientId) | Scrolls to the component with given clientId after ajax request completes.
focus(String expression) | Focus the input(s) targeted by the given search expression.
resetInputs(Collection<String>/String... expressions) | Resets all UIInput targeted by the search expression(s).
clearTableStates() | Removes the multiViewState for all DataTables within the current session.
clearTableState(String key) | Removes the multiViewState for DataTable with the defined key.
clearDataListStates() | Removes the multiViewState for all DataList within the current session.
clearDataListState(String key) | Removes the multiViewState for DataList with the defined key.
ajax().addCallBackParam(String name, Object value) | Adds parameters to ajax callbacks like oncomplete.
ajax().update(Collection<String>/String... expressions); | Specifies component(s) to update at runtime.


**Callback Parameters**

There may be cases where you need values from backing beans in ajax callbacks. Callback
parameters are serialized to JSON and provided as an argument in ajax callbacks for this.

```xhtml
<p:commandButton action="#{bean.validate}" oncomplete="handleComplete(xhr, status, args)" />
```
```java
public void validate() {
    //isValid = calculate isValid
    PrimeFaces.current().ajax().addCallbackParam("isValid", true or false);
}
```
_isValid_ parameter will be available in handleComplete callback as;

```js
<script type="text/javascript">
    function handleComplete(xhr, status, args) {
        var isValid = args.isValid;
    }
</script>
```

You can add as many callback parameters as you want with addCallbackParam API. Each parameter
is serialized as JSON and accessible through args parameter so pojos are also supported just like
primitive values. Following example sends a pojo called _User_ that has properties like firstname and
lastname to the client in addition to _isValid_ boolean value.

```java
public void validate() {
    //isValid = calculate isValid
    PrimeFaces.current().ajax().addCallbackParam("isValid", true or false);
    PrimeFaces.current().ajax().addCallbackParam("user", user);
}
```
```js
<script type="text/javascript">
    function handleComplete(xhr, status, args) {
        var firstname = args.user.firstname;
        var lastname = args.user.lastname;
    }
</script>
```
By default _validationFailed_ callback parameter is added implicitly if validation fails.



**Execute Javascript**

PrimeFaces provides a way to execute javascript when the ajax request completes, this
approach is easier compared to passing callback params and execute conditional javascript.
Example below hides the dialog when ajax request completes;

```java
public void save() {
    PrimeFaces.current().executeScript(“dialog.hide()”);
}
```

## EL Functions

PrimeFaces provides built-in EL extensions that are helpers to common use cases.

**Common Functions**

| Function | Description |
| --- | --- |
resolveClientId(expression, context) | Returns clientId of the component from a given expression.
resolveWidgetVar(expression, context) | Returns widget variable name of the component from a given expression.

**resolveClientId**

```xhtml
//Example to search from root:
var clientId = #{p:resolveClientId('form:tabView:myDataTable', view)};
//Example to search from current component:
<p:commandButton onclick="#{p:resolveClientId('@parent', component)}">
```
**resolveWidgetVar**

```xhtml
<cc:implementation>
    <p:dialog id="dlg">
        //contents
    </p:dialog>
    <p:commandButton type="button" value="Show" onclick="#{p:resolveWidgetVar(‘dlg’), cc}.show()" />
</cc:implementation>
```
**Page Authorization**

Authorization function use HttpServletRequest API for the backend information.

| Function | Description |
| --- | --- |
ifGranted(String role) | Returns true if user has the given role, else false.
ifAllGranted(String roles) | Returns true if user has all of the given roles, else false.
ifAnyGranted(String roles) | Returns true if user has any of the given roles, else false.
ifNotGranted(String roles) | Returns true if user has none of the given roles, else false.
remoteUser() | Returns the name of the logged in user.
userPrincipal() | Returns the principal instance of the logged in user.

```xhtml
<p:commandButton rendered="#{p:ifGranted('ROLE_ADMIN')}" />
<h:inputText disabled="#{p:ifGranted('ROLE_GUEST')}" />
<p:inputMask rendered="#{p:ifAllGranted('ROLE_EDITOR, ROLE_READER')}" />
```

## PrimeFaces Locales

Components may require translations and other settings based on different locales. This is handled
with a client side api called PrimeFaces Locales. A client side locale is basically a javascript object
with various settings, en_US is the default locale provided out of the box. In case you need to
support another locale, settings should be extended with the new information.

A wiki page is available for user contributed settings, the list is community driven and a good
starting point although it might be incomplete.

https://github.com/primefaces/primefaces/wiki/Locales

**Default Locale**
Here is the list of all key-value pairs for en_US locale that is provided by PrimeFaces. DateTime
related properties are utilized by components such as calendar and schedule. If you are using Client
Side Validation, messages property is used as the bundle for the locale.

```js
{
    closeText : 'Close',
    prevText : 'Previous',
    nextText : 'Next',
    monthNames : ['January', 'February', 'March', 'April', 'May', 'June', 'July', 'August', 'September', 'October', 'November', 'December' ],
    monthNamesShort : ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun', 'Jul', 'Aug', 'Sep', 'Oct', 'Nov', 'Dec' ],
    dayNames : ['Sunday', 'Monday', 'Tuesday', 'Wednesday', 'Thursday', 'Friday', 'Saturday'],
    dayNamesShort : ['Sun', 'Mon', 'Tue', 'Wed', 'Tue', 'Fri', 'Sat'],
    dayNamesMin : ['S', 'M', 'T', 'W ', 'T', 'F ', 'S'],
    weekHeader : 'Week',
    firstDay : 0,
    isRTL : false,
    showMonthAfterYear : false,
    yearSuffix :'',
    timeOnlyTitle : 'Only Time',
    timeText : 'Time',
    hourText : 'Time',
    minuteText : 'Minute',
    secondText : 'Second',
    currentText : 'Current Date',
    ampm : false,
    month : 'Month',
    week : 'week',
    day : 'Day',
    allDayText : 'All Day',
    messages : {
        'javax.faces.component.UIInput.REQUIRED' : '{0}: Validation Error: Value is required.',
        'javax.faces.converter.IntegerConverter.INTEGER' : '{2}: \'{0}\' must be a number consisting of one or more digits.',
        'javax.faces.converter.IntegerConverter.INTEGER_detail' : '{2}: \'{0}\' must be a number between -2147483648 and 2147483647 Example: {1}',
        'javax.faces.converter.DoubleConverter.DOUBLE' : '{2}: \'{0}\' must be a number consisting of one or more digits.',
        'javax.faces.converter.DoubleConverter.DOUBLE_detail' : '{2}: \'{0}\' must be a number between 4.9E-324 and 1.7976931348623157E308 Example: {1}',
        'javax.faces.converter.BigDecimalConverter.DECIMAL' : '{2}: \'{0}\' must be a signed decimal number.',
        'javax.faces.converter.BigDecimalConverter.DECIMAL_detail' : '{2}: \'{0}\' must be a signed decimal number consisting of zero or more digits, that may be followed by a decimal point and fraction. Example: {1}',
        'javax.faces.converter.BigIntegerConverter.BIGINTEGER' : '{2}: \'{0}\' must be a number consisting of one or more digits.',
        'javax.faces.converter.BigIntegerConverter.BIGINTEGER_detail' : '{2}: \'{0}\' must be a number consisting of one or more digits. Example: {1}',
        'javax.faces.converter.ByteConverter.BYTE' : '{2}: \'{0}\' must be a number between 0 and 255.',
        'javax.faces.converter.ByteConverter.BYTE_detail' : '{2}: \'{0}\' must be a number between 0 and 255. Example: {1}',
        'javax.faces.converter.CharacterConverter.CHARACTER' : '{1}: \'{0}\' must be a valid character.',
        'javax.faces.converter.CharacterConverter.CHARACTER_detail' : '{1}: \'{0}\' must be a valid ASCII character.',
        'javax.faces.converter.ShortConverter.SHORT' : '{2}: \'{0}\' must be a number consisting of one or more digits.',
        'javax.faces.converter.ShortConverter.SHORT_detail' : '{2}: \'{0}\' must be a number between -32768 and 32767 Example: {1}',
        'javax.faces.converter.BooleanConverter.BOOLEAN' : '{1}: \'{0}\' must be \'true\' or \'false\'',
        'javax.faces.converter.BooleanConverter.BOOLEAN_detail' : '{1}: \'{0}\' must be \'true\' or \'false\'. Any value other than \'true\' will evaluate \'false\'.',
        'javax.faces.validator.LongRangeValidator.MAXIMUM' : '{1}: Validation Error: Value is greater than allowable maximum of \'{0}\'',
        'javax.faces.validator.LongRangeValidator.MINIMUM' : '{1}: Validation Error: Value is less than allowable minimum of \'{0}\'',
        'javax.faces.validator.LongRangeValidator.NOT_IN_RANGE' : '{2}: Validation Error: Specified attribute is not between the expected values of {0} and {1}.',
        'javax.faces.validator.LongRangeValidator.TYPE={0}' : 'Validation Error: Value is not of the correct type.',
        'javax.faces.validator.DoubleRangeValidator.MAXIMUM' : '{1}: Validation Error: Value is greater than allowable maximum of \'{0}\'',
        'javax.faces.validator.DoubleRangeValidator.MINIMUM' : '{1}: Validation Error: Value is less than allowable minimum of \'{0}\'',
        'javax.faces.validator.DoubleRangeValidator.NOT_IN_RANGE' : '{2}: Validation Error: Specified attribute is not between the expected values of {0} and {1}',
        'javax.faces.validator.DoubleRangeValidator.TYPE={0}' : 'Validation Error: Value is not of the correct type',
        'javax.faces.converter.FloatConverter.FLOAT' : '{2}: \'{0}\' must be a number consisting of one or more digits.',
        'javax.faces.converter.FloatConverter.FLOAT_detail' : '{2}: \'{0}\' must be a number between 1.4E-45 and 3.4028235E38 Example: {1}',
        'javax.faces.converter.DateTimeConverter.DATE' : '{2}: \'{0}\' could not be understood as a date.',
        'javax.faces.converter.DateTimeConverter.DATE_detail' : '{2}: \'{0}\' could not be understood as a date. Example: {1}',
        'javax.faces.converter.DateTimeConverter.TIME' : '{2}: \'{0}\' could not be understood as a time.',
        'javax.faces.converter.DateTimeConverter.TIME_detail' : '{2}: \'{0}\' could not be understood as a time. Example: {1}',
        'javax.faces.converter.DateTimeConverter.DATETIME' : '{2}: \'{0}\' could not be understood as a date and time.',
        'javax.faces.converter.DateTimeConverter.DATETIME_detail' : '{2}: \'{0}\' could not be understood as a date and time. Example: {1}',
        'javax.faces.converter.DateTimeConverter.PATTERN_TYPE' : '{1}: A \'pattern\' or \'type\' attribute must be specified to convert the value \'{0}\'',
        'javax.faces.converter.NumberConverter.CURRENCY' : '{2}: \'{0}\' could not be understood as a currency value.',
        'javax.faces.converter.NumberConverter.CURRENCY_detail' : '{2}: \'{0}\' could not be understood as a currency value. Example: {1}',
        'javax.faces.converter.NumberConverter.PERCENT' : '{2}: \'{0}\' could not be understood as a percentage.',
        'javax.faces.converter.NumberConverter.PERCENT_detail' : '{2}: \'{0}\' could not be understood as a percentage. Example: {1}',
        'javax.faces.converter.NumberConverter.NUMBER' : '{2}: \'{0}\' could not be understood as a date.',
        'javax.faces.converter.NumberConverter.NUMBER_detail' : '{2}: \'{0}\' is not a number. Example: {1}',
        'javax.faces.converter.NumberConverter.PATTERN' : '{2}: \'{0}\' is not a number pattern.',
        'javax.faces.converter.NumberConverter.PATTERN_detail' : '{2}: \'{0}\' is not a number pattern. Example: {1}',
        'javax.faces.validator.LengthValidator.MINIMUM' : '{1}: Validation Error: Length is less than allowable minimum of \'{0}\'',
        'javax.faces.validator.LengthValidator.MAXIMUM' : '{1}: Validation Error: Length is greater than allowable maximum of \'{0}\'',
        'javax.faces.validator.RegexValidator.PATTERN_NOT_SET' : 'Regex pattern must be set.',
        'javax.faces.validator.RegexValidator.PATTERN_NOT_SET_detail' : 'Regex pattern must be set to non-empty value.',
        'javax.faces.validator.RegexValidator.NOT_MATCHED' : 'Regex Pattern not matched',
        'javax.faces.validator.RegexValidator.NOT_MATCHED_detail' : 'Regex pattern of \'{0}\' not matched',
        'javax.faces.validator.RegexValidator.MATCH_EXCEPTION' : 'Error in regular expression.',
        'javax.faces.validator.RegexValidator.MATCH_EXCEPTION_detail' : 'Error in regular expression, \'{0}\''
    }
}
```

**Usage**

To add another locale to the API, first create the locale object first with settings and assign it as a
property of PrimeFaces.locales javascript object such as;

_PrimeFaces.locales['de'] = {//settings}_

It is suggested to put this code in a javascript file and include the file into your pages.


## Right to Left

Right-To-Left language support in short RTL is provided out of the box by a subset of PrimeFaces
components. Any component equipped with _dir_ attribute has the official support and there is also a
global setting to switch to RTL mode globally.

Here is an example of an RTL AccordionPanel enabled via _dir_ setting.

```xhtml
<p:accordionPanel dir="rtl">
    //tabs
</p:accordionPanel>
```
**Global Configuration**
Using _primefaces.DIR_ global setting to rtl instructs PrimeFaces RTL aware components such as
datatable, accordion, tabview, dialog, tree to render in RTL mode.

```xml
<context-param>
    <param-name>primefaces.DIR</param-name>
    <param-value>rtl</param-value>
</context-param>
```
Parameter value can also be an EL expression for dynamic values.

In upcoming PrimeFaces releases, more components will receive built-in RTL support. Until then if
the component you use doesn’t provide it, overriding css and javascript in your application would be
the solution.


## Responsive Design

There are three ingridients to make a responsive page with PrimeFaces.

**Page Layout**
Page layout typically consists of the menus, header, footer and the content section. A responsive
page layout should optimize these sections according to the screen size. You may create your own
layout with CSS, pick one from a responsive css framework or choose PrimeFaces Premium
Layouts such as Sentinel, Spark, Modena, Rio and more.

**Grid Framework**
Grid framework is used to define container where you place the content and the components. A
typical grid framework usually consists of columns with varying widths and since they are also
responsive, containers adjust themselves according to the screen size. There are 3rd party grid
frameworks you can use whereas PrimeFaces also provides Grid CSS as a solution.

**Components**
Components also must be flexible enough to use within a responsive layout, if a component has
fixed width, it will not work well with a responsive page layout and grid framework as it does not
adjust its dimensions based on its container. There are two important points in PrimeFaces
components related to responsive design.

First is the fluid mode support for components where component gets 100% width meaning when
used within a grid, it will take the width of the grid. Fluid usually effects the form components. To
enable fluid mode, add _ui-fluid_ to a container element.

```xhtml
<div class="ui-fluid">
    <p:panelGrid columns="2" layout="grid">
        <p:outputLabel for="input" value="Input"/>
        <p:inputText id="input"/>
    </p:panelGrid>
</div>
```
Second is the built-in responsive modes for complex components such Dialog, Charts, Carousel and
PickList. These types of components get a responsive attribute, when enabled they hook-in to
screen size change to optimize their content.

```xhtml
<p:dialog responsive="true"...
```
For a detailed example of a responsive page that uses all of the parts above, visit;

http://www.primefaces.org/showcase/ui/misc/responsive.xhtml

Source code is available at GitHub.


## WAI-ARIA

WAI-ARIA (Web Accessibility Initiative – Accessible Rich Internet Applications) is a technical
specification published by the World Wide Web Consortium (W3C) that specifies how to increase
the accessibility of web pages, in particular, dynamic content and user interface components
developed with Ajax, HTML, JavaScript and related technologies. – Wikipedia

ARIA compatibility is an important goal of PrimeFaces as a result keyboard support as well as
screen reader support are available to many components. Many of these features are built-in and
does not require any configuration to use them. However for screen readers, localized texts might
be necessary so that component can read the aria labels and messages from a bundle. PrimeFaces
provides English translations by default and you may use the following keys in your JSF message
bundle to provide your own values.

- primefaces.datatable.aria.FILTER_BY = Filter by {0}
- primefaces.paginator.aria.HEADER = Pagination
- primefaces.paginator.aria.FIRST_PAGE = First Page
- primefaces.paginator.aria.PREVIOUS_PAGE = Previous Page
- primefaces.paginator.aria.NEXT_PAGE = Next Page
- primefaces.paginator.aria.LAST_PAGE = Last Page
- primefaces.paginator.aria.ROWS_PER_PAGE = Rows Per Page
- primefaces.datatable.aria.HEADER_CHECKBOX_ALL = Select All
- primefaces.dialog.aria.CLOSE = Close
- primefaces.rowtoggler.aria.ROW_TOGGLER = Toggle Row
- primefaces.datatable.SORT_LABEL = Sort
- primefaces.datatable.SORT_ASC = Ascending
- primefaces.datatable.SORT_DESC = Descending

## Portlets

PrimeFaces supports portlet environments based on JSF 2 and Portlet 2 APIs. A portlet bridge is
necessary to run a JSF application as a portlet and we suggest LiferayFaces bridge as the
implementation. Both teams work together time to time to make sure PrimeFaces runs well on
liferay. A kickstart example with necessary configuration is available at LiferayFaces Demos;

http://www.liferay.com/community/liferay-projects/liferay-faces/demos

Demo contains a single "Job Application" portlet within the WAR that demonstrates several of the
key features of JSF 2 and PrimeFaces;

- Uses the PrimeFaces <p:calendar/> tag for a popup date selector
- Uses the JSF 2 <f:ajax /> tag on the postal (zip) code field in order to provide the ability to auto-
fill fields via Ajax
- Uses the JSF 2 <f:ajax /> tag on the show/hide comments links in order to show/hide the
comments field via Ajax
- Model managed-bean is marked with the JSF 2 @ViewScoped annotation in order to support a
rich UI with the <f:ajax /> tag
- Uses the JSF 2 <f:ajax /> tag to show navigation-rules executing without full page refreshes
- File upload capabilities via <h:form enctype="multipart/form-data">
- Managed-beans defined by marking POJOs with the JSF 2 @ManagedBean annotation
- Dependency injection of managed-beans done via the JSF 2 @ManagedProperty annotation
- Uses the PrimeFaces p:fileUpload tag for multi-file Ajax-based file upload
- Uses the PrimeFaces p:dataTable tag to list the uploaded files
- Uses the PrimeFaces p:confirmDialog tag to popup a yes/no dialog to verify file deletion
