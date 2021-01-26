# Chip

Chip represents entities using icons, labels and images.

[See this widget in the JavaScript API Docs.](../jsdocs/classes/src_primefaces.primefaces.widget.chip.html)

## Info

| Name | Value |
| --- | --- |
| Tag | chip
| Component Class | org.primefaces.component.chip.Chip
| Component Type | org.primefaces.component.Chip
| Component Family | org.primefaces.component
| Renderer Type | org.primefaces.component.ChipRenderer
| Renderer Class | org.primefaces.component.chip.ChipRenderer

## Attributes

| Name | Default | Type | Description | 
| --- | --- | --- | --- |
| id | null | String | Unique identifier of the component
| rendered | true | Boolean | Boolean value to specify the rendering of the component, when set to false component will not be rendered.
| binding | null | Object | An el expression that maps to a server side UIComponent instance in a backing bean
| label	| null | String | Defines the text to display.
| icon | null | String | Defines the icon to display.
| image | null | String | Defines the image to display.
| removable | false | Boolean | Whether to display a remove icon.
| removeIconClass | pi pi-times-circle | String | Icon of the remove element.
| widgetVar | null | String | Name of the client side widget.
| style | null | String | Style of the Chip.
| styleClass | null | String | StyleClass of the Chip.

## Getting Started
Chip can display labels, icons and images.

```xhtml
<p:chip value="2"></p:chip>
<p:chip value="2">
    <i class="pi pi-bell"/>
</p:chip>
```

## Removable
Setting ```removable``` property is "true", displays an icon to close the chip, the optional ```remove``` event is 
available to get notified when a chip is hidden.

```xhtml
<p:chip label="text" removable="true"></p:chip>
```

## Templating
Content can easily be customized with the default slot instead of using the built-in modes.

```xhtml
<p:chip>
    Content
</p:chip>
```

## Ajax Behavior Events
Rating provides _rate_ and _cancel_ as ajax behavior events. A defined listener for rate event will be
executed by passing an _org.primefaces.event.RateEvent_ as a parameter and cancel event will be
invoked with no parameter.

```xhtml
<p:chip label="text" removable="true">
    <p:ajax event="click" listener="#{chipBean.onclick}" update="msgs"/>
    <p:ajax event="close" listener="#{chipBean.onclose}" update="msgs"/>
</p:chip>
<p:messages id="msgs" />
```
```java
public class ChipBean {
    
    public void onclick() {
        //Add facesmessage
    }

    public void onclose() {
        //Add facesmessage
    }
}
```

## Skinning of Chip
Chip resides in a main container element which _style_ and _styleClass_ options apply. As skinning
style classes are global, see the main theming section for more information. Following is the list of
structural style classes;

| Name | Element |
| --- | --- |
|.ui-chip | Container element.
|.ui-chip-image | Container element in image mode.
|.ui-chip-text | Text of the chip.
|.pi-chip-remove-icon | Remove icon.
