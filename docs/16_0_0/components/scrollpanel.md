# ScrollPanel

ScrollPanel is used to display scrollable content with theme aware scrollbars instead of native
browser scrollbars.

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
