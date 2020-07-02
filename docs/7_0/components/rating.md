# Rating

Rating component features a star based rating system.

## Info

| Name | Value |
| --- | --- |
| Tag | rating
| Component Class | org.primefaces.component.rating.Rating
| Component Type | org.primefaces.component.Rating
| Component Family | org.primefaces.component |
| Renderer Type | org.primefaces.component.RatingRenderer
| Renderer Class | org.primefaces.component.rating.RatingRenderer

## Attributes

| Name | Default | Type | Description | 
| --- | --- | --- | --- |
id | null | String | Unique identifier of the component
rendered | true | Boolean | Boolean value to specify the rendering of the component, when set to false component will not be rendered.
binding | null | Object | An el expression that maps to a server side UIComponent instance in a backing bean
value | null | Object | Value of the component than can be either an EL expression of a literal text
converter | null | Converter/String | An el expression or a literal text that defines a converter for the component. When it’s an EL expression, it’s resolved to a converter instance. In case it’s a static text, it must refer to a converter id
immediate | 0 | Boolean | Boolean value that specifies the lifecycle phase the valueChangeEvents should be processed, when true the events will be fired at "apply request values", if immediate is set to false, valueChange Events are fired in "process validations" phase
required | false | Boolean | Marks component as required
validator | null | MethodExpr | A method binding expression that refers to a method validationg the input
valueChangeListener | null | MethodExpr | A method binding expression that refers to a method for handling a valuchangeevent
requiredMessage | null | String | Message to be displayed when required field validation fails.
converterMessage | null | String | Message to be displayed when conversion fails.
validatorMessage | null | String | Message to be displayed when validation fields.
widgetVar | null | String | Name of the client side widget.
stars | 5 | Integer | Number of stars to display
disabled | false | Boolean | Disables user interaction
readonly | false | Boolean | Disables user interaction without disabled visuals.
onRate | null | String | Client side callback to execute when rate happens.
style | null | String | Inline style of the component.
styleClass | null | String | Style class of the component.
cancel | true | Boolean | When enabled, displays a cancel icon to reset.

## Getting Started with Rating
Rating is an input component that takes an integer variable as its value.

```java
public class RatingBean {
    private Integer rating;
    //getter-setter
}
```
```xhtml
<p:rating value="#{ratingBean.rating}" />
```
## Number of Stars
Default number of stars is 5, if you need less or more stars use the stars attribute. Following rating
consists of 10 stars.

```xhtml
<p:rating value="#{ratingBean.rating}" stars="10"/>
```

## Display Value Only
In cases where you only want to use the rating component to display the rating value and disallow
user interaction, set _readonly_ to true. Using _disabled_ attribute does the same but adds disabled
visual styles.

## Ajax Behavior Events
Rating provides _rate_ and _cancel_ as ajax behavior events. A defined listener for rate event will be
executed by passing an _org.primefaces.event.RateEvent_ as a parameter and cancel event will be
invoked with no parameter.

```xhtml
<p:rating value="#{ratingBean.rating}">
    <p:ajax event="rate" listener="#{ratingBean.handleRate}" update="msgs" />
    <p:ajax event="cancel" listener="#{ratingBean.handleCancel}" update="msgs" />
</p:rating>
<p:messages id="msgs" />
```
```java
public class RatingBean {
    private Integer rating;

    public void handleRate(RateEvent rateEvent) {
        Integer rate = (Integer) rateEvent.getRating();
        //Add facesmessage
    }
    public void handleCancel() {
        //Add facesmessage
    }
    //getter-setter
}
```
## Client Side Callbacks
_onRate_ is called when a star is selected with _value_ as the only parameter.

```xhtml
<p:rating value="#{ratingBean.rating}" onRate="alert('You rated: ' + value)" />
```
## Client Side API
Widget: _PrimeFaces.widget.Rating_

| Method | Params | Return Type | Description | 
| --- | --- | --- | --- | 
getValue() | - | Number | Returns the current value
setValue(value) | value: Value to set | void | Updates rating value with provided one.
disable() | - | void | Disables component.
enable() | - | void | Enables component.
reset() | - | void | Clears the rating.

## Skinning
Rating resides in a main container which _style_ and _styleClass_ attributes apply. Following is the list
of structural style classes;

| Class | Applies | 
| --- | --- | 
.ui-rating | Main container element.
.ui-rating-cancel | Cancel icon
.ui-rating | Default star
.ui-rating-on | Active star
