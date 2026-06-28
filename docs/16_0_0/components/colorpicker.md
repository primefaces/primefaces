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