# Localization

Components may require translations and other settings based on different locales. This is handled
with a client side api called PrimeFaces Locales. A client side locale is basically a javascript object
with various settings, en_US is the default locale provided out of the box. In case you need to
support another locale, settings should be extended with the new information.

A wiki page is available for user contributed settings, the list is community driven and a good
starting point although it might be incomplete.

https://github.com/primefaces/primefaces/wiki/Locales

## Default Locale
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

## Usage

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

