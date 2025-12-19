# Color Picker

ColorPicker is an input component with a color palette based on [Coloris](https://github.com/mdbassit/Coloris). It features:
* Inline or Popup mode
* Themes and dark mode
* Multiple color formats (Hex, RGB, HSL)
* Opacity support (Alpha channel)
* Fully accessible (keyboard and ARIA)
* Touch support
* Right-To-Left support
* Float Label support
* Client Side Validation (CSV) support
* Color swatches


[See this widget in the JavaScript API Docs.](../jsdocs/classes/src_PrimeFaces.PrimeFaces.widget.ColorPicker-1.html)

## Info

| Name | Value |
| --- | --- |
| Tag | colorPicker
| Component Class | org.primefaces.component.colorpicker.ColorPicker
| Component Type | org.primefaces.component.ColorPicker
| Component Family | org.primefaces.component |
| Renderer Type | org.primefaces.component.ColorPickerRenderer
| Renderer Class | org.primefaces.component.colorpicker.ColorPickerRenderer

## Attributes

| Name | Default | Type | Description |
| --- | --- | --- | --- |
| id | null | String | Unique identifier of the component
| rendered | true | Boolean | Boolean value to specify the rendering of the component, when set to false component will not be rendered.
| binding | null | Object | An el expression that maps to a server side UIComponent instance in a backing bean
| accesskey | null | String | Access key that when pressed transfers focus to the input element.
| alt | null | String | Alternate textual description of the input field.
| ariaDescribedBy | null | String | The aria-describedby attribute is used to define a component id that describes the current element for accessibility.
| autocomplete | null | String | Controls browser autocomplete behavior.
| converter | null | Converter/String | An el expression or a literal text that defines a converter for the component. When it’s an EL expression, it’s resolved to a converter instance. In case it’s a static text, it must refer to a converter id
| converterMessage | null | String | Message to be displayed when conversion fails.
| dir | null | String | Direction indication for text that does not inherit directionality. Valid values are LTR and RTL.
| disabled | false | Boolean | Disables input field
| immediate | false | Boolean | When set true, process validations logic is executed at apply request values phase for this component.
| inputmode | null | String | Hint at the type of data this control has for touch devices to display appropriate virtual keyboard.
| label | null | String | A localized user presentable name.
| lang | null | String | Code describing the language used in the generated markup for this component.
| maxlength | null | Integer | Maximum number of characters that may be entered in this field.
| mode | popup | String | Display mode, valid values are`popup` and `inline`.
| onblur | null | String | Client side callback to execute when input element loses focus.
| onchange | null | String | Client side callback to execute when input element loses focus and its value has been modified since gaining focus.
| onclick | null | String | Client side callback to execute when input element is clicked.
| oncontextmenu | null | String | Client side callback to execute when a context menu is triggered.
| oncopy | null | String | Client side callback to execute when the user cuts the content of an element.
| oncut | null | String | Client side callback to execute when the user copies the content of an element.
| ondblclick | null | String | Client side callback to execute when input element is double clicked.
| ondrag | null | String | Client side callback to execute when an element is dragged.
| ondragend | null | String | Client side callback to execute at the end of a drag operation.
| ondragenter | null | String | Client side callback to execute when an element has been dragged to a valid drop target.
| ondragleave | null | String | Client side callback to execute when an element leaves a valid drop target.
| ondragover | null | String | Client side callback to execute when an element is being dragged over a valid drop target.
| ondragstart | null | String | Client side callback to execute at the start of a drag operation.
| ondrop | null | String | Client side callback to execute when dragged element is being dropped.
| onfocus | null | String | Client side callback to execute on input element focus.
| oninput | null | String | Client side callback to execute when an element gets user input.
| oninvalid | null | String | Client side callback to execute when an element is invalid.
| onkeydown | null | String | Client side callback to execute when a key is pressed down over input element.
| onkeypress | null | String | Client side callback to execute when a key is pressed and released over input element.
| onkeyup | null | String | Client side callback to execute when a key is released over input element.
| onmousedown | null | String | Client side callback to execute when a pointer button is pressed down over input element
| onmousemove | null | String | Client side callback to execute when a pointer button is moved within input element.
| onmouseout | null | String | Client side callback to execute when a pointer button is moved away from input element.
| onmouseover | null | String | Client side callback to execute when a pointer button is moved onto input element.
| onmouseup | null | String | Client side callback to execute when a pointer button is released over input element.
| onpaste | null | String | Client side callback to execute when the user pastes some content in an element.
| onreset | null | String | Client side callback to execute when the Reset button in a form is clicked.
| onscroll | null | String | Client side callback to execute when an element's scrollbar is being scrolled.
| onsearch | null | String | Client side callback to execute when the user writes something in a search field.
| onselect | null | String | Client side callback to execute when text within input element is selected by user.
| onwheel | null | String | Client side callback to execute when the mouse wheel rolls up or down over an element.
| placeholder | null | String | Specifies a short hint.
| readonly | false | Boolean | Flag indicating that this component will prevent changes by the user.
| required | false | Boolean | Marks component as required
| requiredMessage | null | String | Message to be displayed after failed validation.
| size | null | Integer | Number of characters used to determine the width of the input element.
| style | null | String | Inline style of the input element.
| styleClass | null | String | Style class of the input element.
| tabindex | null | Integer | Position of the input element in the tabbing order.
| title | null | String | Advisory tooltip information.
| type | text | String | Input field type.
| validator | null | MethodExpr | A method binding expression that refers to a method validations the input
| validatorMessage | null | String | Message to be displayed when validation fields.
| value | null | Object | Value of the component than can be either an EL expression of a literal text
| valueChangeListener | null | MethodExpr | A method binding expression that refers to a method for handling a valuchangeevent
| widgetVar | null | String | Name of the client side widget.
| alpha | true | boolean | Enable or disable alpha support. When disabled, it will strip the alpha value from the existing color value in all formats. Default true.
| clearButton | false | boolean | Show an optional clear button. Default false.
| clearLabel | Clear | String | Set the label of the clear button. 
| closeButton | false | boolean | Show an optional close button. Default false.
| closeLabel | Close | String | Set the label of the close button.
| focusInput | true | boolean | Focus the color value input when the color picker dialog is opened. Default true.
| forceAlpha | false | boolean | Set to true to always include the alpha value in the color value even if the opacity is 100%. Default false.
| format | hex | String | Set the preferred color string format: hex, rgb, hsl, auto, mixed.
| formatToggle | false | boolean | Set to true to enable format toggle buttons in the color picker dialog. This will also force the "format" to auto.
| selectInput | false | boolean | Select and focus the color value input when the color picker dialog is opened. Default false.
| swatches | null | String | A comma separated list of the desired color swatches to display. If omitted or the array is empty, the color swatches will be disabled.
| swatchesOnly | false | boolean | Set to true to hide all the color picker widgets (spectrum, hue, ...) except the swatches. Default false.
| theme | default | String | Available themes: default, large, polaroid, pill (horizontal).
| themeMode | auto | String | Set the theme to light, dark, or auto mode. Default to auto to detect your OS setting.
       

## Getting started with ColorPicker
ColorPicker’s value should be a hex string.

```java
public class Bean {
    private String color;

    public String getColor() {
        return this.color;
    }
    public void setColor(String color) {
        this.color = color;
    }
}
```
```xhtml
<p:colorPicker value="#{bean.color}" />
```
## Display Mode
ColorPicker has two modes, default mode is _popup_ and other available option is _inline_.

```xhtml
<p:colorPicker value="#{bean.color}" mode="inline"/>
```

!> Inline mode only allows 1 instance of the inline ColorPicker on the page and you cannot mix Inline and Popup on the same page.  It is a limitation of Coloris.

## Legacy Hex Color Handling
Prior to 13.0.0 the ColorPicker only returned the 6 digit hex code without the `#` like `ffffff`.  Now by default it includes the hex code `#ffffff`.
If `alpha=true` for alpha blends it will now be an 8 digit hex code like `#c71c1cff`.  If you prefer to have the ColorPicker act like it has before
13.0.0 then make the following changes.

```xhtml
<p:colorPicker value="#{colorView.value}" alpha="false" converter="#{hexColorConverter}">
```

Using the following HexColorConverter to strip out the `#`.

```java
@Named
@FacesConverter(value = "hexColorConverter", managed = true)
public class HexColorConverter implements Converter<String> {

    @Override
    public String getAsObject(FacesContext context, UIComponent component, String value) {
        if (LangUtils.isBlank(value)) {
            return null;
        }

        return value.startsWith("#") ? value.substring(1) : value;
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, String value) {
        if (LangUtils.isBlank(value)) {
            return null;
        }

        return value.startsWith("#") ? value : "#" + value;
    }
}
```


## Localization and Accessibility
ColorPicker is fully accessible both by keyboard and for visually impaired with proper ARIA screen reader support.
To customize for your language you simply need to update your `local-XX.js` file.  The subset of values respected
are below.  For full localization see the [localization documentation](/core/localization.md?id=client-localization).

```js
{
     isRTL: false,
     closeText: 'Close',
     clear: 'Clear',
     aria: {
         'colorpicker.OPEN': 'Open color picker',
         'colorpicker.CLOSE': 'Close color picker',
         'colorpicker.CLEAR': 'Clear the selected color',
         'colorpicker.MARKER': 'Saturation: {s}. Brightness: {v}.',
         'colorpicker.HUESLIDER': 'Hue slider',
         'colorpicker.ALPHASLIDER': 'Opacity slider',
         'colorpicker.INPUT': 'Color value field',
         'colorpicker.FORMAT': 'Color format',
         'colorpicker.SWATCH': 'Color swatch',
         'colorpicker.INSTRUCTION': 'Saturation and brightness selector. Use up, down, left and right arrow keys to select.'
     }
}
```

!> Localization is **global** so once a locale is set on a page it is set for all ColorPicker instances.


## Ajax Behavior Events

| Event | Listener Parameter | Fired |
| --- | --- | --- |
| change | jakarta.faces.event.AjaxBehaviorEvent | On change.
| open | jakarta.faces.event.AjaxBehaviorEvent | On open of the popup in popup mode.
| close | jakarta.faces.event.AjaxBehaviorEvent | On close of the popup in popup mode.


## Client Side API
Widget: _PrimeFaces.widget.ColorPicker_


| Method | Params | Return Type | Description |
| --- | --- | --- | --- |
| disable() | - | void | Disables the input field |
| enable() | - | void | Enables the input field |
| show() | - | void | Shows the popup panel only in popup mode |
| hide(revert) | boolean | void | Hides the popup panel only in popup mode. If revert is true revert back to original color |
| getColor() | void | String | Gets the current color value |
| setColor(color) | String | void | Sets the current color value |

## Skinning
ColorPicker resides in a container element which _style_ and _styleClass_ options apply. Following is
the list of structural style classes;

| Class | Applies |
| --- | --- |
| .ui-colorpicker | Container element.
| .clr-picker | Color picker panel.
| .clr-gradient | Background of gradient.
| .clr-color | Current color display.
| .clr-hue | Hue slider element.
| .clr-alpha | Alpha slider element.
| .clr-format | Format selector.
| .clr-swatches | Swatches area.
| .clr-preview | Color preview area.