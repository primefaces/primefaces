# Fieldset

Fieldset is a grouping component as an extension to html fieldset.

## Info

| Name | Value |
| --- | --- |
| Tag | fieldset
| Component Class | org.primefaces.component.fieldset.Fieldset
| Component Type | org.primefaces.component.Fieldset
| Component Family | org.primefaces.component |
| Renderer Type | org.primefaces.component.FieldsetRenderer
| Renderer Class | org.primefaces.component.fieldset.FieldsetRenderer

## Attributes

| Name | Default | Type | Description | 
| --- | --- | --- | --- |
| id | null | String | Unique identifier of the component
| rendered | true | Boolean | Boolean value to specify the rendering of the component, when set to false component will not be rendered.
| binding | null | Object | An el expression that maps to a server side UIComponent instance in a backing bean
| widgetVar | null | String | Name of the client side widget.
| legend | null | String | Title text.
| style | null | String | Inline style of the fieldset.
| styleClass | null | String | Style class of the fieldset.
| toggleable | false | Boolean | Makes content toggleable with animation.
| toggleSpeed | 500 | Integer | Toggle duration in milliseconds.
| collapsed | false | Boolean | Defines initial visibility state of content.
| tabindex | 0 | String | Position of the element in the tabbing order.
| escape | true | Boolean | Whether value would be escaped or not.
| title | null | String | Advisory tooltip information.

## Getting started with Fieldset
Fieldset is used as a container component for its children.

```xhtml
<p:fieldset legend="Simple Fieldset">
    <h:panelGrid column="2">
        <p:graphicImage value="/images/godfather/1.jpg" />
        <h:outputText value="The story begins as Don Vito Corleone ..." />
    </h:panelGrid>
</p:fieldset>
```
## Legend
Legend can be defined in two ways, with legend attribute as in example above or using legend
facet. Use facet way if you need to place custom html other than simple text.

```xhtml
<p:fieldset>
    <f:facet name="legend">
    </f:facet>
    //content
</p:fieldset>
```
When both legend attribute and legend facet are present, facet is chosen.

## Toggleable Content
Clicking on fieldset legend can toggle contents, this is handy to use space efficiently in a layout. Set
toggleable to true to enable this feature.

```xhtml
<p:fieldset legend="Toggleable Fieldset" toggleable="true">
    <h:panelGrid column="2">
        <p:graphicImage value="/images/godfather/2.jpg" />
        <h:outputText value="Francis Ford Coppolas’ legendary ..." />
    </h:panelGrid>
</p:fieldset>
```

## Ajax Behavior Events
_toggle_ is the default and only ajax behavior event provided by fieldset that is processed when the
content is toggled. In case you have a listener defined, it will be invoked by passing an instance of
_org.primefaces.event.ToggleEvent._

Here is an example that adds a facesmessage and updates growl component when fieldset is
toggled.

```xhtml
<p:growld id="messages" />
<p:fieldset legend="Toggleable Fieldset" toggleable="true"
    <p:ajax listener="#{bean.onToggle}" update="messages">
    //content
</p:fieldset>
```
```java
public void onToggle(ToggleEvent event) {
    Visibility visibility = event.getVisibility();
    FacesMessage msg = new FacesMessage();
    msg.setSummary("Fieldset " + event.getId() + " toggled");
    msg.setDetail("Visibility: " + visibility);
    FacesContext.getCurrentInstance().addMessage(null, msg);
}
```
## Client Side API
Widget: _PrimeFaces.widget.Fieldset_

| Method | Params | Return Type | Description | 
| --- | --- | --- | --- | 
| toggle() | - | void | Toggles fieldset content.

## Skinning
_style_ and _styleClass_ options apply to the fieldset. Following is the list of structural style classes;


| Class | Applies | 
| --- | --- | 
| .ui-fieldset | Main container
| .ui-fieldset-toggleable | Main container when fieldset is toggleable
| .ui-fieldset .ui-fieldset-legend | Legend of fieldset
| .ui-fieldset-toggleable .ui-fieldset-legend | Legend of fieldset when fieldset is toggleable
| .ui-fieldset .ui-fieldset-toggler | Toggle icon on fieldset

As skinning style classes are global, see the main theming section for more information.

**Tips**:

- A collapsed fieldset will remain collapsed after a postback since fieldset keeps its toggle state
    internally, you don’t need to manage this using toggleListener and collapsed option.
