# InputNumber

InputNumber formats input fields with numeric Strings. It supports currency symbols, minimum
and maximum value, negative numbers, and a lot of round methods.

[See this widget in the JavaScript API Docs.](../jsdocs/classes/src_PrimeFaces.PrimeFaces.widget.InputNumber-1.html)

!> WARNING: If you use `minValue>0` or `maxValue<0` it will behave in unexpectedly. AutoNumeric will force the users to always have a valid value in the input, hence preventing them to clear the field. PrimeFaces has automatically disabled this with `overrideMinMaxLimits-invalid` as per this AutoNumeric issue: https://github.com/autoNumeric/autoNumeric/issues/543

## Getting Started with InputNumber
Without any configuration, input number will parse the value and format it as a number using
decimal and thousand separator.

```xhtml
<p:inputNumber value="#{bean.propertyName}" />
```

## Converter usage

`p:inputNumber` does NOT support common converters like `<f:convertNumber type="percent" minFractionDigits="2"/>`
as `p:inputNumber` only works with number types on server side (and not strings like `100 %`).

#### Custom converter example to handle percent multiplication / division

```
@FacesConverter("percentConverter")
public class PercentConverter implements Converter<Number> {

    @Override
    public Number getAsObject(FacesContext context, UIComponent component, String value) {
        if (context == null || value == null || value.trim().isBlank()) {
            return null;
        }

        try {
            return Double.valueOf(value) / 100;
        }
        catch (Exception e) {
            throw new ConverterException(String.format("Cannot convert %s to Number", value), e);
        }
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Number value) {
        if (context == null || value == null) {
            return null;
        }

        return String.valueOf(value * 100);
    }

}

```

## Examples
Here are some examples demonstrating various cases;

#### Suffix currency symbol and comma for decimal separator

```xhtml
<p:inputNumber id="input2" value="0.0" symbol=" CHF" symbolPosition="s" decimalSeparator="," thousandSeparator="." />
```
#### Maximum and minimum values (-1000.999 to 1000.000)

```xhtml
<p:inputNumber id="input3" value="0.0" minValue="-1000.999" maxValue="1000" />
```
#### Custom decimal places

```xhtml
<p:inputNumber id="input4" value="0.0" decimalPlaces="6" />
```
#### Empty value (empty) and required

```xhtml
<p:inputNumber id="input5" value="251.31" symbol="%" symbolPosition="s" required="true" emptyValue="empty" />
```
#### Empty value (zero)

```xhtml
<p:inputNumber id="input6" value="60.0" symbol="%" symbolPosition="s" emptyValue="zero" />
```
#### Empty value (always show symbol)

```xhtml
<p:inputNumber id="Input7" value="" symbol="%" symbolPosition="s" emptyValue="always" />
```
#### 15 Decimals using BigDecimal

```xhtml
<p:inputNumber id="Input8" value="1234.000000001" decimalPlaces="15" />
```

## Ajax Behavior Events

The following AJAX behavior events are available for this component. If no event is specified the default event is called.  
  
**Default Event:** `valueChange`  
**Available Events:** `blur, change, click, contextmenu, copy, cut, dblclick, drag, dragend, dragenter, dragleave, dragover, dragstart, drop, focus, input, invalid, keydown, keypress, keyup, mousedown, mousemove, mouseout, mouseover, mouseup, paste, reset, scroll, search, select, valueChange, wheel`  

```xhtml
<p:ajax event="valueChange" listener="#{bean.handlevalueChange}" update="msgs" />
```

## Client Side API
Widget: _PrimeFaces.widget.InputNumber_

| Method | Params | Return Type | Description |
| --- | --- | --- | --- |
| enable() | none | void | Enables the component |
| disabled() | none | void | Disables the component |
| setValue(value) | value: the numeric value to set | void | Sets the value of the InputNumber|
| getValue() | none | String | Gets the current value of the InputNumber as a String. Formatted if formatted=true attribute.|
