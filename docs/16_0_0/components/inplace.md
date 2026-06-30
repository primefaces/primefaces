# Inplace

Inplace provides easy inplace editing and inline content display. Inplace consists of two members,
display element is the initial clickable label and inline element is the hidden content that is
displayed when display element is toggled.

## Getting Started with Inplace
The inline component needs to be a child of inplace.

```xhtml
<p:inplace>
    <h:inputText value="Edit me" />
</p:inplace>
```
## Custom Labels
By default inplace displays its first childs value as the label, you can customize it via the label
attribute.

```xhtml
<h:outputText value="Select One:" />
<p:inplace label="Cities">
    <h:selectOneMenu>
        <f:selectItem itemLabel="Istanbul" itemValue="Istanbul" />
        <f:selectItem itemLabel="Ankara" itemValue="Ankara" />
    </h:selectOneMenu>
</p:inplace>
```
## Facets
For advanced customization, _output_ and _input_ facets are provided.

```xhtml
<p:inplace id="checkboxInplace">
    <f:facet name="output">
        Yes or No
    </f:facet>
    <f:facet name="input">
        <h:selectBooleanCheckbox />
    </f:facet>
</p:inplace>
```

## Effects
Default effect is _fade_ and other possible effect is _slide_ , also effect speed can be tuned with values
_slow_ , _normal_ and _fast_.

```xhtml
<p:inplace label="Show Image" effect="slide" effectSpeed="fast">
    <p:graphicImage value="/images/nature1.jpg" />
</p:inplace>
```
## Editor
Inplace editing is enabled via the _editor_ option.

```java
public class InplaceBean {
    private String text;
    //getter-setter
}
```
```xhtml
<p:inplace editor="true">
    <h:inputText value="#{inplaceBean.text}" />
</p:inplace>
```
_save_ and _cancel_ are two provided ajax behaviors events you can use to hook-in the editing process.

```java
public class InplaceBean {
    private String text;
    public void handleSave() {
        //add faces message with update text value
    }
    //getter-setter
}
```
```xhtml
<p:inplace editor="true">
    <p:ajax event="save" listener="#{inplaceBean.handleSave}" update="msgs" />
    <h:inputText value="#{inplaceBean.text}" />
</p:inplace>
<p:growl id="msgs" />
```

## Client Side API
Widget: _PrimeFaces.widget.Inplace_

| Method | Params | Return Type | Description |
| --- | --- | --- | --- |
show() | - | void | Shows content and hides display element.
hide() | - | void | Shows display element and hides content.
toggle() | - | void | Toggles visibility of between content and display element.
save() | - | void | Triggers an ajax request to process inplace input.
cancel() | - | void | Triggers an ajax request to revert inplace input.

## Skinning
Inplace resides in a main container element which _style_ and _styleClass_ options apply. Following is
the list of structural style classes;

| Class | Applies |
| --- | --- |
.ui-inplace | Main container element.
.ui-inplace-disabled | Main container element when disabled.
.ui-inplace-display | Display element.
.ui-inplace-content | Inline content.
.ui-inplace-editor | Editor controls container.
.ui-inplace-save | Save button.
.ui-inplace-cancel | Cancel button.

As skinning style classes are global, see the main theming section for more information.
