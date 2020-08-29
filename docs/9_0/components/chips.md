# Chips

Chips is used to enter multiple values on an inputfield.

[See this widget in the JavaScript API Docs.](../jsdocs/classes/primefaces.widget.chips.html)

## Info

| Name | Value |
| --- | --- |
| Tag | chips
| Component Class | org.primefaces.component.chips.Chips
| Component Type | org.primefaces.component.Chips
| Component Family | org.primefaces.component |
| Renderer Type | org.primefaces.component.ChipsRenderer
| Renderer Class | org.primefaces.component.chips.ChipsRenderer

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
| max | null | Integer | Maximum number of entries allowed.
| addOnBlur | false | Boolean | Whether to add an item when the input loses focus.
| title | null | String | Advisory tooltip information.

## Getting started with Chips
Value of the component should be a List where type of the elements it contains can be a string or a
custom one if a converter is defined.

```java
public class ChipsView {
    private List<String> cities;

    public List<String> getCities() {
        return this.cities;
    }
    public void setCities(List<String> cities) {
        this.cities = cities;
    }
}
```
```xhtml
<p:chips value="#{chipsView.cities}" />
```
## Ajax Behavior Events

The following AJAX behavior events are available for this component. If no event is specified the default event is called.  
  
**Default Event:** `valueChange`  
**Available Events:** `blur, change, click, contextmenu, copy, cut, dblclick, drag, dragend, dragenter, dragleave, dragover, dragstart, drop, focus, input, invalid, itemSelect, itemUnselect, keydown, keypress, keyup, mousedown, mousemove, mouseout, mouseover, mouseup, paste, reset, scroll, search, select, valueChange, wheel`  

```xhtml
<p:ajax event="itemSelect" listener="#{bean.handlevalueChange}" update="msgs" />
```

| Event | Listener Parameter | Fired |
| --- | --- | --- |
| itemSelect | org.primefaces.event.SelectEvent | When an item is added.
| itemUnselect | org.primefaces.event.UnselectEvent | When an item is removed.

## Skinning
Following is the list of structural style classes;

| Class | Applies | 
| --- | --- | 
| .ui-chips | Container element.
| .ui-chips-container | List element.
| .ui-chips-token | A list item.
| .ui-chips-input-token | List item containing the input.
| .ui-chips-token-icon | Close icon of a token.
