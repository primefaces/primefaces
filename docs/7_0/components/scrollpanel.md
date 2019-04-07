# ScrollPanel

ScrollPanel is used to display scrollable content with theme aware scrollbars instead of native
browser scrollbars.

## Info

| Name | Value |
| --- | --- |
| Tag | scrollPanel
| Component Class | org.primefaces.component.scrollpanel.ScrollPanel
| Component Type | org.primefaces.component.ScrollPanel
| Component Family | org.primefaces.component |
| Renderer Type | org.primefaces.component.ScrollPanelRenderer
| Renderer Class | org.primefaces.component.scrollpanel.ScrollPanelRenderer

## Attributes

| Name | Default | Type | Description | 
| --- | --- | --- | --- |
id | null | String | Unique identifier of the component
rendered | true | Boolean | Boolean value to specify the rendering of the component, when set to false component will not be rendered.
binding | null | Object | An el expression that maps to a server side UIComponent instance in a backing bean
style | null | String | Inline style of the container element.
styleClass | null | String | Style class of the container element.
mode | default | String | Scrollbar display mode, valid values are default and native.

## Getting started with ScrollPanel
In order to get scrollable content, width and/or height should be defined.

```xhtml
<p:scrollPanel style="width:250px;height:200px">
    //any content
</p:scrollPanel>
```
## Native ScrollBars
By default, scrollPanel displays theme aware scrollbars, setting mode option to native displays
browser scrollbars.

```xhtml
<p:scrollPanel style="width:250px;height:200px" mode="native">
    //any content
</p:scrollPanel>
```
## Skinning
ScrollPanel resides in a main container which _style_ and _styleClass_ attributes apply. As skinning style
classes are global, see the main theming section for more information. Following is the list of
structural style classes;

| Class | Applies | 
| --- | --- | 
.ui-scrollpanel | Main container element.
.ui-scrollpanel-container | Overflow container.
.ui-scrollpanel-content | Content element.
.ui-scrollpanel-hbar | Horizontal scrollbar container.
.ui-scrollpanel-vbar | Vertical scrollbar container.
.ui-scrollpanel-track | Track element.
.ui-scrollbar-drag | Drag element.
