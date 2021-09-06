# Ribbon 

Ribbon is container component to group different sets of controls in a tabbed layout. Special styling
is applied to inner components for a unified look.

## Info

| Name | Value |
| --- | --- |
| Tag | ribbon
| Component Class | org.primefaces.component.ribbon.Ribbon
| Component Type | org.primefaces.component.Ribbon
| Component Family | org.primefaces.component |
| Renderer Type | org.primefaces.component.RibbonRenderer
| Renderer Class | org.primefaces.component.ribbon.RibbonRenderer

## Attributes

| Name | Default | Type | Description | 
| --- | --- | --- | --- |
id | null | String | Unique identifier of the component
rendered | true | Boolean | Boolean value to specify the rendering of the component, when set to false component will not be rendered.
binding | null | Object | An el expression that maps to a server side UIComponent instance in a backing bean
widgetVar | null | String | Name of the client side widget.
style | null | String | Inline style of the container element.
styleClass | null | String | Style class of the container element.
activeIndex | 0 | Integer | Index of the active tab.

## Getting started with Ribbon
Tab and RibbonGroup components are used when building the Ribbon.

```xhtml
<p:ribbon>
    <p:tab title="File">
        <p:ribbonGroup label="Options">
            <p:commandButton value="New" icon="ui-ribbonicon-new" styleClass="ui-ribbon-bigbutton" type="button"/>
            <p:commandButton value="Save" icon="ui-ribbonicon-save" styleClass="ui-ribbon-bigbutton" type="button"/>
        </p:ribbonGroup>
        <p:ribbonGroup label="Clipboard" style="width:120px">
            <p:selectManyButton>
                <p:commandButton value="Paste" icon="ui-ribbonicon-paste" styleClass="ui-ribbon-bigbutton" type="button"/>
                <p:commandButton value="Cut" icon="ui-ribbonicon-cut" style="width:64px" type="button"/>
                <p:commandButton value="Print" icon="ui-ribbonicon-print" style="width:64px" type="button"/>
            </p:selectManyButton>
        </p:ribbonGroup>
        <p:ribbonGroup label="Fonts" style="width:220px">
            <p:selectOneMenu appendTo="@this">
                <f:selectItem itemLabel="Arial" itemValue="0" />
                <f:selectItem itemLabel="Comis Sans" itemValue="1" />
                <f:selectItem itemLabel="Helvetica" itemValue="2" />
                <f:selectItem itemLabel="Times New Roman" itemValue="3" />
                <f:selectItem itemLabel="Verdana" itemValue="4" />
            </p:selectOneMenu>
            <p:colorPicker />
        </p:ribbonGroup>
    </p:tab>
    <p:tab title="View">
        <p:ribbonGroup label="Zoom">
            <p:commandButton value="In" icon="ui-ribbonicon-zoomin" styleClass="ui-ribbon-bigbutton" type="button" />
            <p:commandButton value="Out" icon="ui-ribbonicon-zoomout" styleClass="ui-ribbon-bigbutton" type="button"/>
        </p:ribbonGroup>
    </p:tab>
</p:ribbon>
```
## Styling
Following components have special styling applied inside ribbon;

- Button
- CommandButton
- SelectOneButton
- SelectManyButton
- SelectOneMenu
- InputText

Default PrimeFaces icons are 16px, in case you need bigger icons add ui-ribbon-bigbutton style
class to the button.


## Skinning
Ribbon resides in a main container which _style_ and _styleClass_ attributes apply. Following is the list
of structural style classes.

| Class | Applies | 
| --- | --- | 
.ui-ribbon | Main container element.
.ui-ribbon-groups | Container of ribbon groups in a tab.
.ui-ribbon-group | Ribbon group element.
.ui-ribbon-group-content | Content of a group.
.ui-ribbon-group-label | Label of a group.

Ribbon shares the same structure with TabView for the tabbing functionality, refer to TabView for
the styles of the Tabs.

