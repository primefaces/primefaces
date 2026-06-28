# Slider

Slider is used to provide input with various customization options like orientation, display modes
and skinning.

[See this widget in the JavaScript API Docs.](../jsdocs/classes/src_PrimeFaces.PrimeFaces.widget.Slider-1.html)

## Getting started with Slider
Slider requires an input component to work with, _for_ attribute is used to set the id of the input
component whose input will be provided by the slider.

```java
public class SliderBean {
    private int number;

    public int getNumber() {
        return number;
    }
    public void setNumber(int number) {
        this.number = number;
    }
}
```
```xhtml
<h:inputText id="number" value="#{sliderBean.number}" />
<p:slider for="number" />
```
## Display Value
Using _display_ feature, you can present a readonly display value and still use slider to provide input,
in this case _for_ should refer to a hidden input to bind the value.

```xhtml
<h:inputHidden id="number" value="#{sliderBean.number}" />
<h:outputText value="Set ratio to %" />
<h:outputText id="output" value="#{sliderBean.number}" />
<p:slider for="number" display="output" />
```

## Vertical Slider
By default slider’s orientation is horizontal, vertical sliding is also supported and can be set using
the _type_ attribute.

```xhtml
<h:inputText id="number" value="#{sliderController.number}" />
<p:slider for="number" type="vertical" minValue="0" maxValue="200"/>
```
## Step
Step factor defines the interval between each point during sliding. Default value is one and it is
customized using _step_ option.

```xhtml
<h:inputText id="number" value="#{sliderBean.number}" />
<p:slider for="number" step="10" />
```
## Animation
Sliding is animated by default, if you want to turn it of animate attribute set the _animate_ option to
false.

## Boundaries
Maximum and minimum boundaries for the sliding is defined using minValue and maxValue
attributes. Following slider can slide between -100 and +100.

```xhtml
<h:inputText id="number" value="#{sliderBean.number}" />
<p:slider for="number" minValue="-100" maxValue="100"/>
```
## Range Slider
Selecting a range with min-max values are supported by slider. To enable this feature, set _range_
attribute to true and provide a comma separate pair of input fields to attach min-max values.
Following sample demonstrates a range slider along with the display template feature for feedback;


```xhtml
<h:outputText id="displayRange" value="Between #{sliderBean.number6} and #{sliderBean.number7}"/>
<p:slider for="txt6,txt7" display="displayRange" style="width:400px" range="true" displayTemplate="Between {min} and {max}"/>
<h:inputHidden id="min" value="#{sliderBean.min}" />
<h:inputHidden id="max" value="#{sliderBean.max}" />
```
## Client Side Callbacks
Slider provides three callbacks to hook-in your custom javascript, onSlideStart, onSlide and
onSlideEnd. All of these callbacks receive two parameters; slide event and the ui object containing
information about the event.

```xhtml
<h:inputText id="number" value="#{sliderBean.number}" />
<p:slider for="number" onSlideEnd="handleSlideEnd(event, ui)"/>
```
```js
function handleSlideEnd(event, ui) {
    //ui.helper = Handle element of slider
    //ui.value = Current value of slider
}
```

## Client Side API
Widget: _PrimeFaces.widget.Slider_

| Method | Params | Return Type | Description | 
| --- | --- | --- | --- | 
getValue() | - | Number | Returns the current value
setValue(value) | value: Value to set | void | Updates slider value with provided one.
disable() | index: Index of tab to disable | void | Disables slider.
enable() | index: Index of tab to enable | void | Enables slider.

## Skinning
Slider resides in a main container which _style_ and _styleClass_ attributes apply. These attributes are
handy to specify the dimensions of the slider. Following is the list of structural style classes;

| Class | Applies | 
| --- | --- | 
.ui-slider | Main container element
.ui-slider-horizontal | Main container element of horizontal slider
.ui-slider-vertical | Main container element of vertical slider
.ui-slider-handle | Slider handle

As skinning style classes are global, see the main theming section for more information.

