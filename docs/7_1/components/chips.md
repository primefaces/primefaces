# Chips

Chips is used to enter multiple values on an inputfield.

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
| max | null | Integer | Maximum number of entries allowed.
| addOnBlur | false | Boolean | Whether to add an item when the input loses focus.

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
