# Chip

Chip represents entities using icons, labels and images.

[See this widget in the JavaScript API Docs.](../jsdocs/classes/src_PrimeFaces.PrimeFaces.widget.Chip.html)

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
Chip provides _select_ and _close_ as ajax behavior events. 

```xhtml
<p:chip label="text" removable="true">
    <p:ajax event="select" listener="#{chipBean.onselect}" update="msgs"/>
    <p:ajax event="close" listener="#{chipBean.onclose}" update="msgs"/>
</p:chip>
<p:messages id="msgs" />
```
```java
public class ChipBean {
    
    public void onselect() {
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
