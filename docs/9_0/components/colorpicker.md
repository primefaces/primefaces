# Color Picker

ColorPicker is an input component with a color palette.

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
| value | null | Object | Value of the component.
| converter | null | Converter/String | An el expression or a literal text that defines a converter for the component. When it’s an EL expression, it’s resolved to a converter instance. In case it’s a static text, it must refer to a converter id
| immediate | false | Boolean | When set true, process validations logic is executed at apply request values phase for this component.
| required | false | Boolean | Marks component as required.
| validator | null | MethodExpr | A method expression that refers to a method for validation the input.
| valueChangeListener | null | ValueChangeListener | A method binding expression that refers to a method for handling a valuchangeevent.
| requiredMessage | null | String | Message to be displayed when required field validation fails.
| converterMessage | null | String | Message to be displayed when conversion fails.
| validatorMessage | null | String | Message to be displayed when validation fields.
| widgetVar | null | String | Name of the client side widget.
| mode | popup | String | Display mode, valid values are “popup” and “inline”.
| style | null | String | Inline style of the component.
| styleClass | null | String | Style class of the component.
| onchange | null | String | Client side callback to execute on value change.

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

## Ajax Behavior Events

| Event | Listener Parameter | Fired |
| --- | --- | --- |
| change | javax.faces.event.AjaxBehaviorEvent | On change.


## Skinning
ColorPicker resides in a container element which _style_ and _styleClass_ options apply. Following is
the list of structural style classes;

| Class | Applies |
| --- | --- |
| .ui-colorpicker | Container element.
| .ui-colorpicker_color | Background of gradient.
| .ui-colorpicker_hue | Hue element.
| .ui-colorpicker_new_color | New color display.
| .ui-colorpicker_current_color | Current color display.
| .ui-colorpicker-rgb-r | Red input.
| .ui-colorpicker-rgb-g | Greed input.
| .ui-colorpicker-rgb-b | Blue input.
| .ui-colorpicker-rgb-h | Hue input.
| .ui-colorpicker-rgb-s | Saturation input.
| .ui-colorpicker-rgb-b | Brightness input.
| .ui-colorpicker-rgb-hex | Hex input.