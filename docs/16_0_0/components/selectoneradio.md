# SelectOneRadio

SelectOneRadio is an extended version of the standard SelectOneRadio with theme integration.

[See this widget in the JavaScript API Docs.](../jsdocs/classes/src_PrimeFaces.PrimeFaces.widget.SelectOneRadio.html)

## Getting started with SelectOneRadio
SelectOneRadio usage is same as the standard one.

## Custom Layout
Standard selectOneRadio component only supports horizontal and vertical rendering of the radio
buttons with a strict table markup. PrimeFaces SelectOneRadio on the other hand provides a
flexible layout option so that radio buttons can be located anywhere on the page.

There are two ways of using a custom layout. Referenced and via a facet named `custom`.
Note that the facet variant offers better accessibility.

### Referenced
This is implemented by setting layout option to custom and with standalone radioButton components. Note
that in custom mode, selectOneRadio itself does not render any output.

```xhtml
<p:selectOneRadio id="customRadio" value="#{formBean.option}" layout="custom">
    <f:selectItem itemLabel="Option 1" itemValue="1" />
    <f:selectItem itemLabel="Option 2" itemValue="2" />
</p:selectOneRadio>
```
```xhtml
<h:panelGrid columns="3">
    <p:radioButton id="opt1" for="customRadio" itemIndex="0"/>
    <h:outputLabel for="opt1" value="Option 1" />
    <p:spinner />
    <p:radioButton id="opt2" for="customRadio" itemIndex="1"/>
    <h:outputLabel for="opt2" value="Option 2" />
    <p:inputText />
</h:panelGrid>
```

RadioButton’s for attribute should refer to a selectOneRadio component and itemIndex points to the
index of the selectItem. When using custom layout option, selectOneRadio component should be
placed above any radioButton that points to the selectOneRadio.

### Facet

This is implemented by setting adding custom components to a facet named `custom`.

```xhtml
<p:selectOneRadio id="customRadio" value="#{radioView.color}" label="Color">
    <f:selectItem itemLabel="Red" itemValue="Red"/>
    <f:selectItem itemLabel="Green" itemValue="Green"/>

    <f:facet name="custom">
        <div class="p-field-radiobutton" role="radio">
            <p:radioButton id="opt1" for="customRadio" itemIndex="0"/>
            <p:outputLabel for="opt1">
                <span class="legend" style="background:red"/> Red
            </p:outputLabel>
        </div>
        <div class="p-field-radiobutton" role="radio">
            <p:radioButton id="opt2" for="customRadio" itemIndex="1"/>
            <p:outputLabel for="opt2">
                <span class="legend" style="background:green"/> Green
            </p:outputLabel>
        </div>
    </f:facet>
</p:selectOneRadio>
```

RadioButton’s for attribute should refer to a selectOneRadio component and itemIndex points to the
index of the selectItem.

For the better accessibility support, you might want to wrap each custom radio button in an element
with a `role="radio"` attribute.

## Client Side API
Widget: _PrimeFaces.widget.SelectOneRadio_

| Method | Params | Return Type | Description | 
| --- | --- | --- | --- | 
enable() | - | void | Enables component.
disable() | - | void | Disables component.

## Skinning
SelectOneRadio resides in a main container which _style_ and _styleClass_ attributes apply. As skinning
style classes are global, see the main theming section for more information. Following is the list of
structural style classes;

| Class | Applies | 
| --- | --- | 
.ui-selectoneradio | Main container element.
.ui-radiobutton | Container of a radio button.
.ui-radiobutton-box | Container of radio button icon.
.ui-radiobutton-icon | Radio button icon.
