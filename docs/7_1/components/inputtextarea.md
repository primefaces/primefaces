# InputTextarea

InputTextarea is an extension to standard inputTextarea with autoComplete, autoResize, remaining
characters counter and theming features.

## Info

| Name | Value |
| --- | --- |
| Tag | inputTextarea
| Component Class | org.primefaces.component.inputtextarea.InputTextarea
| Component Type | org.primefaces.component.InputTextarea
| Component Family | org.primefaces.component |
| Renderer Type | org.primefaces.component.InputTextareaRenderer
| Renderer Class | org.primefaces.component.inputtextarea.InputTextareaRender

## Attributes

| Name | Default | Type | Description | 
| --- | --- | --- | --- |
id | null | String | Unique identifier of the component
rendered | true | Boolean | Boolean value to specify the rendering of the component, when set to false component will not be rendered.
binding | null | Object | An el expression that maps to a server side UIComponent instance in a backing bean
value | null | Object | Value of the component than can be either an EL expression of a literal text
converter | null | Converter/String | An el expression or a literal text that defines a converter for the component. When it’s an EL expression, it’s resolved to a converter instance. In case it’s a static text, it must refer to a converter id
immediate | false | Boolean | When set true, process validations logic is executed at apply request values phase for this component.
required | false | Boolean | Marks component as required
validator | null | MethodExpr | A method binding expression that refers to a method validationg the input
valueChangeListener | null | MethodExpr | A method binding expression that refers to a method for handling a valuchangeevent
requiredMessage | null | String | Message to be displayed when required field validation fails.
converterMessage | null | String | Message to be displayed when conversion fails.
validatorMessage | null | String | Message to be displayed when validation fields.
widgetVar | null | String | Name of the client side widget.
accesskey | null | String | Access key that when pressed transfers focus to the input element.
alt | null | String | Alternate textual description of the input field.
autocomplete | null | String | Controls browser autocomplete behavior.
dir | null | String | Direction indication for text that does not inherit directionality. Valid values are LTR and RTL.
disabled | false | Boolean | Disables input field
label | null | String | A localized user presentable name.
lang | null | String | Code describing the language used in the generated markup for this component.
onblur | null | String | Client side callback to execute when input element loses focus.
onchange | null | String | Client side callback to execute when input element loses focus and its value has been modified since gaining focus.
onclick | null | String | Client side callback to execute when input element is clicked.
ondblclick | null | String | Client side callback to execute when input element is double clicked.
onfocus | null | String | Client side callback to execute when input element receives focus.
onkeydown | null | String | Client side callback to execute when a key is pressed down over input element.
onkeypress | null | String | Client side callback to execute when a key is pressed and released over input element.
onkeyup | null | String | Client side callback to execute when a key is released over input element.
onmousedown | null | String | Client side callback to execute when a pointer button is pressed down over input element
onmousemove | null | String | Client side callback to execute when a pointer button is moved within input element.
onmouseout | null | String | Client side callback to execute when a pointer button is moved away from input element.
onmouseover | null | String | Client side callback to execute when a pointer button is moved onto input element.
onmouseup | null | String | Client side callback to execute when a pointer button is released over input element.
onselect | null | String | Client side callback to execute when text within input element is selected by user.
readonly | false | Boolean | Flag indicating that this component will prevent changes by the user.
size | null | Integer | Number of characters used to determine the width of the input element.
style | null | String | Inline style of the input element.
styleClass | null | String | Style class of the input element.
tabindex | null | Integer | Position of the input element in the tabbing order.
title | null | String | Advisory tooltip informaton.
autoResize | true | Boolean | Specifies auto growing when being typed.
maxlength | null | Integer | Maximum number of characters that may be entered in this field.
counter | null | String | Id of the output component to display remaining chars.
counterTemplate | {0} | String | Template text to display in counter.
completeMethod | null | MethodExpr | Method to provide suggestions.
minQueryLength |3 | Integer | Number of characters to be typed to run a query.
queryDelay | 700 | Integer | Delay in ms before sending each query.
scrollHeight | null | Integer | Height of the viewport for autocomplete suggestions.

## Getting Started with InputTextarea
InputTextarea usage is same as standard inputTextarea;

```xhtml
<p:inputTextarea value="#{bean.propertyName}" />
```
## AutoResize
While textarea is being typed, if content height exceeds the allocated space, textarea can grow
automatically. Use autoResize option to turn on/off this feature.

```xhtml
<p:inputTextarea value="#{bean.propertyName}" autoResize="true|false"/>
```

## Remaining Characters
InputTextarea can limit the maximum allowed characters with maxLength option and display the
remaining characters count as well.

```xhtml
<p:inputTextarea value="#{bean.propertyName}" counter="display" maxlength="20" counterTemplate="{0} characters remaining" />
<h:outputText id="display" />
```

## AutoComplete
InputTextarea supports ajax autocomplete functionality as well. You need to provide a
completeMethod to provide the suggestions to use this feature. In sample below, completeArea
method will be invoked with the query being the four characters before the caret.

```java
public class AutoCompleteBean {
    public List<String> completeArea(String query) {
        List<String> results = new ArrayList<String>();
        if(query.equals("PrimeFaces")) {
            results.add("PrimeFaces Rocks!!!");
            results.add("PrimeFaces has 100+ components.");
            results.add("PrimeFaces is lightweight.");
            results.add("PrimeFaces is easy to use.");
            results.add("PrimeFaces is developed with passion!");
        }
        else {
            for(int i = 0; i < 10; i++) {
                results.add(query + i);
            }
        }
        return results;
    }
}
```

```xhtml
<p:inputTextarea rows="10" cols="50" minQueryLength="4" completeMethod="#{autoCompleteBean.completeArea}" />
```

## Skinning
InputTextarea renders a textarea element which _style_ and _styleClass_ options apply. Following is the
list of structural style classes;

| Class | Applies | 
| --- | --- | 
.ui-inputtextarea | Textarea element.
.ui-inputfield | Textarea element.
.ui-autocomplete-panel | Overlay for suggestions.
.ui-autocomplete-items | Suggestions container.
.ui-autocomplete-item | Each suggestion.

As skinning style classes are global, see the main theming section for more information.

## Ajax Behavior Events

The following AJAX behavior events are available for this component. If no event is specific the default event is called.  
  
**Default Event:** valueChange  
**Available Events:** blur, change, click, dblclick, focus, itemSelect, keydown, keypress, keyup, mousedown, mousemove, mouseout, mouseover, mouseup, select, valueChange  

```xhtml
<p:ajax event="valueChange" listener="#{bean.handlevalueChange}" update="msgs" />
```