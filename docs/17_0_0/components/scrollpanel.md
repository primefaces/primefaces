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

The content scrolls natively (mouse wheel, touch, keyboard), only the browser scrollbars are hidden and replaced by
themed bars. The bars can be dragged, a click on a track scrolls by a page. They follow changes of the content and of
the size automatically, also in a container that becomes visible later (e.g. a tab).

## Native ScrollBars
By default, scrollPanel displays theme aware scrollbars, setting mode option to native displays
browser scrollbars.

```xhtml
<p:scrollPanel style="width:250px;height:200px" mode="native">
    //any content
</p:scrollPanel>
```

## Client Side API
Widget: _PrimeFaces.widget.ScrollPanel_

| Method | Params | Return Type | Description |
| --- | --- | --- | --- |
scrollTo(x, y) | x: horizontal, y: vertical position in pixels | void | Scrolls to the given position.
scrollX(x) | x: horizontal position in pixels | void | Scrolls to the given horizontal position.
scrollY(y) | y: vertical position in pixels | void | Scrolls to the given vertical position.
redraw() | - | void | Updates the bars, only needed for changes the scroll panel does not notice.

## Keyboard Support
While the content overflows, it is focusable and scrolls with the keys of the browser (arrow keys, Page Up /
Page Down, Home / End, space).

## Skinning
ScrollPanel resides in a main container which _style_ and _styleClass_ attributes apply. As skinning style
classes are global, see the main theming section for more information. Following is the list of
structural style classes;

| Class | Applies |
| --- | --- |
.p-scrollpanel | Main container element.
.p-scrollpanel-native | Main container element in native mode.
.p-scrollpanel-content | Scrolling content element.
.p-scrollpanel-track | Track of a bar.
.p-scrollpanel-track-x | Track of the horizontal bar.
.p-scrollpanel-track-y | Track of the vertical bar.
.p-scrollpanel-bar | Bar, it can be dragged.
.p-scrollpanel-bar-active | Bar while it is dragged.
.p-scrollpanel-scrollable-x | Main container element, when the content overflows horizontally.
.p-scrollpanel-scrollable-y | Main container element, when the content overflows vertically.

The bars use the `--p-scrollpanel-*` tokens of the theme (`--p-scrollpanel-bar-size`, `--p-scrollpanel-bar-background`,
`--p-scrollpanel-bar-hover-background`, `--p-scrollpanel-track-background`, ...).
