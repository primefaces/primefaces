# OverlayPanel

OverlayPanel is a generic panel component that can be displayed on top of other content.

## Info

| Name | Value |
| --- | --- |
| Tag | overlayPanel
| Component Class | org.primefaces.component.overlaypanel.OverlayPanelRenderer
| Component Type | org.primefaces.component.OverlayPanel
| Component Family | org.primefaces.component |
| Renderer Type | org.primefaces.component.OverlayPanelRenderer
| Renderer Class | org.primefaces.component.overlaypanel.OverlayPanelRenderer

## Attributes

| Name | Default | Type | Description |
| --- | --- | --- | --- |
id | null | String | Unique identifier of the component
rendered | true | Boolean | Boolean value to specify the rendering of the component, when set to false component will not be rendered.
binding | null | Object | An el expression that maps to a server side UIComponent instance in a backing bean
widgetVar | null | String | Name of the client side widget.
style | null | String | Inline style of the panel.
styleClass | null | String | Style class of the panel.
for | null | String | Target component to display panel next to.
showEvent | click | String | Event on target to show the panel.
hideEvent | click | String | Event on target to hide the panel.
showEffect | null | String | Animation to display when showing the panel.
hideEffect | null | String | Animation to display when hiding the panel.
onShow | null | String | Client side callback to execute when panel is shown.
onHide | null | String | Client side callback to execute when panel is hidden.
my | left top | String | Position of the panel relative to the target.
at | left bottom | String | Position of the target relative to the panel.
collision | flip | String | When the positioned element overflows the window in some direction, move it to an alternative position. Similar to my and at, this accepts a single value or a pair for horizontal/vertical, e.g., "flip", "fit", "fit flip", "fit none".
dynamic | false | Boolean | Defines content loading mode.
dismissable | true | Boolean | When set true, clicking outside of the panel hides the overlay.
showCloseIcon | false | Boolean | Displays a close icon to hide the overlay, default is false.
modal | false | Boolean | Boolean value that specifies whether the document should be shielded with a partially transparent mask to require the user to close the Panel before being able to activate any elements in the document.
appendTo | null | String | Appends the overlayPanel to the given search expression.

## Getting started with OverlayPanel
OverlayPanel needs a component as a target in addition to the content to display. Example below
demonstrates an overlayPanel attached to a button to show a chart in a popup.

```xhtml
<p:commandButton id="chartBtn" value="Basic" type="button" />
    <p:overlayPanel for="chartBtn">
    <p:pieChart value="#{chartBean.pieModel}" legendPosition="w" title="Sample Pie Chart" style="width:400px;height:300px" />
</p:overlayPanel>
```
## Events
Default event on target to show and hide the panel is mousedown. These are customized using
_showEvent_ and _hideEvent_ options.

```xhtml
<p:commandButton id="chartBtn" value="Basic" type="button" />
<p:overlayPanel showEvent="mouseover" hideEvent="mousedown">
    //content
</p:overlayPanel>
```

## Effects
blind, bounce, clip, drop, explode, fold, highlight, puff, pulsate, scale, shake, size, slide are
available values for _showEffect_ and _hideEffect_ options if you’d like display animations.

## Positioning
By default, left top corner of panel is aligned to left bottom corner of the target if there is enough
space in window viewport, if not the position is flipped on the fly to find the best location to
display. In order to customize the position use _my_ and _at_ options that takes combinations of left,
right, bottom and top e.g. “right bottom”.

## Dynamic Mode
Dynamic mode enables lazy loading of the content, in this mode content of the panel is not rendered
on page load and loaded just before panel is shown. Also content is cached so consecutive displays
do not load the content again. This feature is useful to reduce the page size and reduce page load
time.

## Standalone
OverlayPanel is positioned relative to its target based on a one-to-one relationship, this causes
limitations when used inside a data iteration because every row needs an overlaypanel which is far
from ideal, requires client side memory and slows down page performance. Instead a single
overlayPanel can be used by calling show passing the client id of the component to be relatively
positioned. See overlayPanel demo in showcase for an example.

## Client Side API
Widget: _PrimeFaces.widget.OverlayPanel_

| Method | Params | Return Type | Description |
| --- | --- | --- | --- |
| show(target) | - | void | Displays the OverlayPanel. The target parameter is optional and can be used to dynamically overwrite the _for_ attribute.
| hide() | - | void | Closes the OverlayPanel.
| isVisible() | - | void | Returns visibility as a boolean.

## Skinning
Panel resides in a main container which _style_ and _styleClass_ attributes apply. Following is the list of
structural style classes;

| Class | Applies |
| --- | --- |
.ui-overlaypanel | Main container element of panel

As skinning style classes are global, see the main theming section for more information.

**Tips**

- Use appendTo with a value like "@(body) when overlayPanel is in other panel components like layout, dialog ...
- If there is a component with a popup like calendar, autocomplete placed inside the overlay panel,
    popup part might exceed the boundaries of panel and clicking the outside hides the panel. This is
    undesirable so in cases like this use overlayPanel with _dismissable_ false and optional
    _showCloseIcon_ settings.

