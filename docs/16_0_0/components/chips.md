# Chips

Chips is used to enter multiple values on an inputfield.

[See this widget in the JavaScript API Docs.](../jsdocs/classes/src_PrimeFaces.PrimeFaces.widget.Chips.html)

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

## Client Side API
Widget: _PrimeFaces.widget.Chips_

| Method | Params | Return Type | Description |
| --- | --- | --- | --- |
| toggleEditor() | none | void | Converts the current list into a separator delimited list for mass editing while keeping original order of the items or closes the editor turning the values back into chips.|
| addItem(value) | value: to add | void | Adds a new item (chip) to the list of currently displayed items. |
| removeItem(value) | value: to remove | void | Removes an item (chip) from the list of currently displayed items. |
| disable() | - | void | Disables the input field |
| enable() | - | void | Enables the input field |


## Skinning
Following is the list of structural style classes;

| Class | Applies | 
| --- | --- | 
| .ui-chips | Container element.
| .ui-chips-container | List element.
| .ui-chips-token | A list item.
| .ui-chips-input-token | List item containing the input.
| .ui-chips-token-icon | Close icon of a token.
