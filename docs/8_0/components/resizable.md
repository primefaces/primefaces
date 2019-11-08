# Resizable

Resizable component is used to make another JSF component resizable.

## Info

| Name | Value |
| --- | --- |
| Tag | resizable
| Component Class | org.primefaces.component.resizable.Resizable
| Component Type | org.primefaces.component.Resizable
| Component Family | org.primefaces.component |
| Renderer Type | org.primefaces.component.ResizableRenderer
| Renderer Class | org.primefaces.component.resizable.ResizableRenderer

## Attributes

| Name | Default | Type | Description | 
| --- | --- | --- | --- |
id | null | String | Unique identifier of the component
rendered | true | Boolean | Boolean value to specify the rendering of the component, when set to false component will not be rendered.
binding | null | Object | An el expression that maps to a server side UIComponent instance in a backing bean
widgetVar | null | String | Name of the client side widget.
for | null | String | Identifier of the target component to make resizable.
aspectRatio | false | Boolean | Defines if aspectRatio should be kept or not.
proxy | false | Boolean | Displays proxy element instead of actual element.
handles | null | String | Specifies the resize handles.
ghost | false | Boolean | In ghost mode, resize helper is displayed as the original element with less opacity.
animate | false | Boolean | Enables animation.
effect | swing | String | Effect to use in animation.
effectDuration | normal | String | Effect duration of animation.
maxWidth | null | Integer | Maximum width boundary in pixels.
maxHeight | null | Integer | Maximum height boundary in pixels.
minWidth | 10 | Integer | Minimum width boundary in pixels.
minHeight | 10 | Integer | Maximum height boundary in pixels.
containment | false | Boolean | Sets resizable boundaries as the parents size.
grid | 1 | Integer | Snaps resizing to grid structure.
onStart | null | String | Client side callback to execute when resizing begins.
onResize | null | String | Client side callback to execute during resizing.
onStop | null | String | Client side callback to execute after resizing end.

## Getting started with Resizable
Resizable is used by setting _for_ option as the identifier of the target.

```xhtml
<p:graphicImage id="img" value="campnou.jpg" />
<p:resizable for="img" />
```
Another example is the input fields, if users need more space for a textarea, make it resizable by;

```xhtml
<h:inputTextarea id="area" value="Resize me if you need more space" />
<p:resizable for="area" />
```
## Boundaries
To prevent overlapping with other elements on page, boundaries need to be specified. There’re 4
attributes for this _minWidth_ , _maxWidth_ , _minHeight_ and _maxHeight_. The valid values for these
attributes are numbers in terms of pixels.

```xhtml
<h:inputTextarea id="area" value="Resize me if you need more space" />
<p:resizable for="area" minWidth="20" minHeight="40" maxWidth="50" maxHeight="100"/>
```
## Handles
Resize handles to display are customize using _handles_ attribute with a combination of _n_ , _e_ , _s_ , _w_ , _ne_ ,
_se_ , _sw_ and _nw_ as the value. Default value is " _e_ , _s_ , _se_ ".

```xhtml
<h:inputTextarea id="area" value="Resize me if you need more space" />
<p:resizable for="area" handles="e,w,n,se,sw,ne,nw"/>
```
## Visual Feedback
Resize helper is the element used to provide visual feedback during resizing. By default actual
element itself is the helper and two options are available to customize the way feedback is provided.
Enabling _ghost_ option displays the element itself with a lower opacity, in addition enabling _proxy_
option adds a css class called _.ui-resizable-proxy_ which you can override to customize.


```xhtml
<h:inputTextarea id="area" value="Resize me if you need more space" />
<p:resizable for="area" proxy="true" />
```
```css
.ui-resizable-proxy {
   border: 2px dotted #00F;
}
```
## Effects
Resizing can be animated using _animate_ option and setting an _effect_ name. Animation speed is
customized using _effectDuration_ option _"slow"_ , _"normal"_ and _"fast"_ as valid values.

```xhtml
<h:inputTextarea id="area" value="Resize me if you need more space" />
<p:resizable for="area" animate="true" effect="swing" effectDuration="normal" />
```
Following is the list of available effect names;

- swing
- easeInQuad
- easeOutQuad
- easeInOutQuad
- easeInCubic
- easeOutCubic
- easeInOutCubic
    - easeInQuart
    - easeOutQuart
    - easeInOutQuart
    - easeInQuint
    - easeOutQuint
    - easeInOutQuint
    - easeInSine
       - easeOutSine
       - easeInExpo
       - easeOutExpo
       - easeInOutExpo
       - easeInCirc
       - easeOutCirc
       - easeInOutCirc
          - easeInElastic
          - easeOutElastic
          - easeInOutElastic
          - easeInBack
          - easeOutBack
          - easeInOutBack
             - easeInBounce
             - easeOutBounce
             - easeInOutBounce

## Ajax Behavior Events
Resizable provides default and only _resize_ event that is called on resize end. In case you have a
listener defined, it will be called by passing an _org.primefaces.event.ResizeEvent_ instance as a
parameter_._

```xhtml
<h:inputTextarea id="area" value="Resize me if you need more space" />
<p:resizable for="area">
   <p:ajax listener="#{resizeBean.handleResize}">
</p:resizable>
```
```java
public class ResizeBean {
   public void handleResize(ResizeEvent event) {
      int width = event.getWidth();
      int height = event.getHeight();
   }
}
```

## Client Side Callbacks
Resizable has three client side callbacks you can use to hook-in your javascript; _onStart_ , _onResize_
and _onStop_. All of these callbacks receive two parameters that provide various information about
resize event.

```xhtml
<h:inputTextarea id="area" value="Resize me if you need more space" />
<p:resizable for="area" onStop="handleStop(event, ui)" />
```
```js
function handleStop(event, ui) {
   //ui.helper = helper element as a jQuery object
   //ui.originalPosition = top, left position before resizing
   //ui.originalSize = width, height before resizing
   //ui.positon = top, left after resizing
   //ui.size = width height of current size
}
```
## Skinning

| Class | Applies | 
| --- | --- | 
.ui-resizable | Element that is resizable
.ui-resizable-handle | Handle element
.ui-resizable-handle-{handlekey} | Particular handle element identified by handlekey like e, s, ne
.ui-resizable-proxy | Proxy helper element for visual feedback

